/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce.enhancedtwoobjective;

import clustering.KMedoids;
import dataset.ProjectiveClusterDataset;
import dataset.ProjectiveClusteringDataset;
import evaluation.cluster.features.SquaredEuclideanFeaturesProjectiveClusterSim;
import evaluation.cluster.objects.SquaredEuclideanObjectsProjectiveClusterSim;
import evaluation.cluster.features.ProjectiveClusterFeaturesSimilarity;
import evaluation.cluster.objects.ProjectiveClusterObjectsSimilarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.ClusterBasedPCEMethod;


public class OneObjectiveClusterBasedPCE_EUCLIDEAN extends ClusterBasedPCEMethod
{
    protected double[][] clusterObjectsDistMatrix;
    protected double[][] clusterFeaturesDistMatrix;

    protected ProjectiveClusterObjectsSimilarity objectClusterSim;
    protected ProjectiveClusterFeaturesSimilarity featureClusterSim;

    protected ProjectiveClustering result;

    public OneObjectiveClusterBasedPCE_EUCLIDEAN (ProjectiveClusteringDataset ensemble)
    {
        this.ensemble = ensemble;
        this.objectClusterSim = new SquaredEuclideanObjectsProjectiveClusterSim();
        this.featureClusterSim = new SquaredEuclideanFeaturesProjectiveClusterSim();

        long start = System.currentTimeMillis();
        this.clusterObjectsDistMatrix = this.ensemble.getClusterObjectsDistMatrix(this.objectClusterSim);
        this.clusterFeaturesDistMatrix = this.ensemble.getClusterFeaturesDistMatrix(this.featureClusterSim);
        long stop = System.currentTimeMillis();

        this.offlineExecutionTime = stop-start;
    }

    public ProjectiveClustering execute (int nClusters)
    {
        System.out.println("####################################################");
        System.out.println("OneObjectiveClusterBasedSubspaceEnsembles_EUCLIDEAN");
        System.out.println("####################################################");

        long start = System.currentTimeMillis();
        double[][] simMatrix = computeCumulativeSimilarityMatrix();

        ProjectiveClusterDataset subspaceClusterDataset = new ProjectiveClusterDataset(ensemble.getClusters(),null);

        //AHCAverageLinkage ahc = new AHCAverageLinkage(subspaceClusterDataset,false);
        //Clustering ahcResult = ahc.execute(simMatrix, nClusters);

        KMedoids km = new KMedoids(subspaceClusterDataset);
        Clustering kmResult = km.execute(simMatrix, nClusters);

        //this.result = assignObjectsAndFeatures(ahcResult);
        this.result = assignObjectsAndFeatures(kmResult);
        long stop = System.currentTimeMillis();

        this.onlineExecutionTime = stop-start;

        return this.result;
    }

    protected double[][] computeCumulativeSimilarityMatrix()
    {
        double[][] m = new double[this.ensemble.getNumberOfAllClusters()][this.ensemble.getNumberOfAllClusters()];

        for (int i=0; i<m.length; i++)
        {
            for (int j=i+1; j<m[i].length; j++)
            {
                double norm1 = this.clusterObjectsDistMatrix[i][j]/this.ensemble.getNumberOfInstances();
                if (Double.isInfinite(norm1) || Double.isNaN(norm1) || norm1<-0.0000001 || norm1>1.0000001)
                {
                    throw new RuntimeException("ERROR: norm1 must be within [0,1]---norm1="+norm1);
                }

                double norm2 = this.clusterFeaturesDistMatrix[i][j]/this.ensemble.getNumberOfFeaturesInEachCluster();
                if (Double.isInfinite(norm2) || Double.isNaN(norm2) || norm2<-0.0000001 || norm2>1.0000001)
                {
                    throw new RuntimeException("ERROR: norm2 must be within [0,1]---norm2="+norm2);
                }

                m[i][j] = (norm1+norm2)/2;
                m[j][i] = m[i][j];
            }
        }

        return m;
    }

    protected ProjectiveClustering assignObjectsAndFeatures(Clustering ahcResult)
    {
        Cluster[] metaClusters = ahcResult.getClusters();

        Double[][] objectAssignments = new Double[metaClusters.length][this.ensemble.getNumberOfInstances()];
        for (int i=0; i<objectAssignments.length; i++)
        {
            for (int j=0; j<objectAssignments[i].length; j++)
            {
                objectAssignments[i][j] = new Double(0.0);
            }
        }

        Double[][] featureAssignments = new Double[metaClusters.length][this.ensemble.getNumberOfFeaturesInEachCluster()];
        for (int i=0; i<featureAssignments.length; i++)
        {
            for (int j=0; j<featureAssignments[i].length; j++)
            {
                featureAssignments[i][j] = new Double(0.0);
            }
        }

        ProjectiveCluster[] subspaceClusters = new ProjectiveCluster[metaClusters.length];


        double X = 0.0;
        for (int i=0; i<metaClusters.length; i++)
        {
            int n = metaClusters[i].getNumberOfInstances();
            if (n > 0)
            {
                X += ((double)1.0)/metaClusters[i].getNumberOfInstances();
            }
        }
        X = ((double)1.0)/X;
        if (Double.isInfinite(X) || Double.isNaN(X))
        {
            throw new RuntimeException("ERROR: X is NAN or INF---X="+X);
        }

        for (int n=0; n<this.ensemble.getNumberOfInstances(); n++)
        {
            double Yn = 0.0;
            for (int i=0; i<metaClusters.length; i++)
            {
                double coeff = metaClusters[i].getNumberOfInstances();
                if (coeff > 0)
                {
                    coeff = ((double)1.0)/coeff;
                }


                double tmp = 0.0;
                for (int j=0; j<metaClusters[i].getNumberOfInstances(); j++)
                {
                    Double[] orep = ((ProjectiveCluster)(metaClusters[i].getInstances()[j])).getFeatureVectorRepresentationDouble();
                    tmp += orep[n];
                }

                Yn += coeff*tmp;
            }

            Yn = Yn-1;

            if (Double.isInfinite(Yn) || Double.isNaN(Yn))
            {
                throw new RuntimeException("ERROR: NAN or INF---Yn="+Yn);
            }

            for (int k=0; k<objectAssignments.length; k++)
            {
                double coeff = metaClusters[k].getNumberOfInstances();
                if (coeff > 0)
                {
                    coeff = ((double)1.0)/coeff;
                }

                objectAssignments[k][n] = -coeff*X*Yn;
            }
            
        }

        for (int i=0; i<metaClusters.length; i++)
        {
            Instance[] clusters = metaClusters[i].getInstances();
            if (clusters != null && clusters.length > 0)
            {
                for (int j=0; j<clusters.length; j++)
                {
                    //object assignment: it comes from the resolution of the optimization problem
                    Double[] orep = ((ProjectiveCluster)clusters[j]).getFeatureVectorRepresentationDouble();
                    for (int k=0; k<orep.length; k++)
                    {
                        objectAssignments[i][k] += orep[k]/metaClusters[i].getNumberOfInstances();
                    }

                    Double[] frep = ((ProjectiveCluster)clusters[j]).getFeatureToClusterAssignments();
                    for (int k=0; k<frep.length; k++)
                    {
                        featureAssignments[i][k] += frep[k]/metaClusters[i].getNumberOfInstances();
                    }
                }
            }
            else
            {
                for (int k=0; k<featureAssignments[i].length; k++)
                {
                    featureAssignments[i][k] += ((double)1.0)/featureAssignments[i].length;
                }
            }
        }


        //testing
        for (int k=0; k<objectAssignments[0].length; k++)
        {
            double sum = 0.0;
            for (int i=0; i<objectAssignments.length; i++)
            {
                if (objectAssignments[i][k] < -0.000001 || objectAssignments[i][k] > 1.000001)
                {
                    throw new RuntimeException("ERROR: the objectAssignment value must be within [0,1]---value="+objectAssignments[i][k]);
                }
                sum += objectAssignments[i][k];
            }

            if (sum <0.9999999 || sum > 1.0000001)
            {
                throw new RuntimeException("ERROR: sum must be equal to 1---sum="+sum);
            }
        }

        for (int i=0; i<featureAssignments.length; i++)
        {
            double sum = 0.0;
            for (int k=0; k<featureAssignments[i].length; k++)
            {
                if (featureAssignments[i][k] < -0.000001 || featureAssignments[i][k] > 1.000001)
                {
                    throw new RuntimeException("ERROR: the featureAssignment value must be within [0,1]---value="+featureAssignments[i][k]);
                }
                sum += featureAssignments[i][k];
            }

            if (sum <0.9999999 || sum > 1.0000001)
            {
                throw new RuntimeException("ERROR: sum must be equal to 1---sum="+sum);
            }
        }

        for (int i=0; i<subspaceClusters.length; i++)
        {
            Double[] objectRep = objectAssignments[i];
            Double[] featureRep = featureAssignments[i];

            subspaceClusters[i] = new ProjectiveCluster(this.ensemble.getInstances(), objectRep, featureRep, metaClusters[i].getID(), true, true);
        }

        return new ProjectiveClustering(subspaceClusters);
    }

    public ProjectiveClusterObjectsSimilarity getObjectClusterSim()
    {
        return this.objectClusterSim;
    }

    public void setObjectClusterSim (ProjectiveClusterObjectsSimilarity objectClusterSim)
    {
        this.objectClusterSim = objectClusterSim;
    }

    public ProjectiveClusterFeaturesSimilarity getFeatureClusterSim()
    {
        return this.featureClusterSim;
    }

    public void setFeatureClusterSim (ProjectiveClusterFeaturesSimilarity featureClusterSim)
    {
        this.featureClusterSim = featureClusterSim;
    }

    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }
}



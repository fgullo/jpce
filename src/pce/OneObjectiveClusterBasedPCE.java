/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce;

import clustering.AHCAverageLinkage;
import clustering.KMedoids;
import dataset.ProjectiveClusterDataset;
import dataset.ProjectiveClusteringDataset;
import evaluation.cluster.features.ProjectiveClusterFeaturesSimilarity;
import evaluation.cluster.objects.ProjectiveClusterObjectsSimilarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;


public class OneObjectiveClusterBasedPCE extends ClusterBasedPCEMethod
{
    protected double[][] clusterObjectsDistMatrix;
    protected double[][] clusterFeaturesDistMatrix;
    
    protected ProjectiveClusterObjectsSimilarity objectClusterSim;
    protected ProjectiveClusterFeaturesSimilarity featureClusterSim;
    
    protected ProjectiveClustering result;
    
    public OneObjectiveClusterBasedPCE (ProjectiveClusteringDataset ensemble, ProjectiveClusterObjectsSimilarity objectClusterSim, ProjectiveClusterFeaturesSimilarity featureClusterSim)
    {
        this.ensemble = ensemble;
        this.objectClusterSim = objectClusterSim;
        this.featureClusterSim = featureClusterSim;
        
        long startTime = System.currentTimeMillis();
        this.clusterObjectsDistMatrix = this.ensemble.getClusterObjectsDistMatrix(this.objectClusterSim);
        this.clusterFeaturesDistMatrix = this.ensemble.getClusterFeaturesDistMatrix(this.featureClusterSim);
        long stopTime = System.currentTimeMillis();

        this.offlineExecutionTime = stopTime-startTime;
    }
    
    public ProjectiveClustering execute (int nClusters)
    {
        long startTime = System.currentTimeMillis();

        double[][] simMatrix = computeCumulativeSimilarityMatrix();
        
        ProjectiveClusterDataset subspaceClusterDataset = new ProjectiveClusterDataset(ensemble.getClusters(),null);
        
        //AHCAverageLinkage ahc = new AHCAverageLinkage(subspaceClusterDataset,false);
        //Clustering ahcResult = ahc.execute(simMatrix, nClusters);
        
        KMedoids km = new KMedoids(subspaceClusterDataset);
        Clustering kmResult = km.execute(simMatrix, nClusters);
        
        //this.result = assignObjectsAndFeatures(ahcResult);
        this.result = assignObjectsAndFeatures(kmResult);

        long stopTime = System.currentTimeMillis();
        this.onlineExecutionTime = stopTime-startTime;

        return this.result;
    }
    
    protected double[][] computeCumulativeSimilarityMatrix()
    {
        double[][] m = new double[this.ensemble.getNumberOfAllClusters()][this.ensemble.getNumberOfAllClusters()];
        
        double max1 = Double.NEGATIVE_INFINITY;
        double max2 = Double.NEGATIVE_INFINITY;
        double min1 = Double.POSITIVE_INFINITY;
        double min2 = Double.POSITIVE_INFINITY;
        
        for (int i=0; i<this.clusterObjectsDistMatrix.length-1; i++)
        {
            for (int j=i+1; j<this.clusterObjectsDistMatrix[i].length; j++)
            {
                if (this.clusterObjectsDistMatrix[i][j] > max1)
                {
                    max1 = this.clusterObjectsDistMatrix[i][j];
                }
                if (this.clusterObjectsDistMatrix[i][j] < min1)
                {
                    min1 = this.clusterObjectsDistMatrix[i][j];
                }
                
                if (this.clusterFeaturesDistMatrix[i][j] > max2)
                {
                    max2 = this.clusterFeaturesDistMatrix[i][j];
                }
                if (this.clusterFeaturesDistMatrix[i][j] < min2)
                {
                    min2 = this.clusterFeaturesDistMatrix[i][j];
                }
            }
        }
        
        for (int i=0; i<m.length-1; i++)
        {
            for (int j=i+1; j<m[i].length; j++)
            {
                //m[i][j] = 1-0.5*((this.clusterObjectsDistMatrix[i][j]/max1)+(this.clusterFeaturesDistMatrix[i][j]/max2));
                double val1 = 0.0;
                if (max1 > min1)
                {
                    val1 = (this.clusterObjectsDistMatrix[i][j]-min1)/(max1-min1);
                }
                double val2 = 0.0;
                if (max2 > min2)
                {
                    val2 = (this.clusterFeaturesDistMatrix[i][j]-min2)/(max2-min2);
                }
                m[i][j] = 1-0.5*(val1+val2);
                m[j][i] = m[i][j];
                
                if (Double.isInfinite(m[i][j]) || Double.isNaN(m[i][j]))
                {
                    throw new RuntimeException("ERROR: m[i][j] is INFINITY or NAN");
                }
                
                if (m[i][j] < -0.000001 | m[i][j] > 1.000001)
                {
                    throw new RuntimeException("ERROR: m[i][j] must be within [0,1]");
                }
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
        
        for (int i=0; i<metaClusters.length; i++)
        {
            Instance[] clusters = metaClusters[i].getInstances();
            for (int j=0; j<clusters.length; j++)
            {
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
        
        
        //renormalization
        for (int k=0; k<objectAssignments[0].length; k++)
        {
            double sum = 0.0;
            for (int i=0; i<objectAssignments.length; i++)
            {
                sum += objectAssignments[i][k];
            }
            
            for (int i=0; i<objectAssignments.length; i++)
            {
                if (sum > 0.0)
                {
                    objectAssignments[i][k] /= sum;
                }
                else
                {
                    objectAssignments[i][k] = ((double)1.0)/objectAssignments.length;
                }
            }
        }
        
        for (int i=0; i<featureAssignments.length; i++)
        {
            double sum = 0.0;
            for (int k=0; k<featureAssignments[i].length; k++)
            {
                sum += featureAssignments[i][k];
            }
            
            for (int k=0; k<featureAssignments[i].length; k++)
            {
                if (sum > 0.0)
                {
                    featureAssignments[i][k] /= sum;
                }
                else
                {
                    featureAssignments[i][k] = ((double)1.0)/featureAssignments[i].length;
                }
                
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


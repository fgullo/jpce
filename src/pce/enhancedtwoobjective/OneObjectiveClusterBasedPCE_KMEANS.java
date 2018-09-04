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
import java.util.ArrayList;
import pce.ClusterBasedPCEMethod;


public class OneObjectiveClusterBasedPCE_KMEANS extends ClusterBasedPCEMethod
{
    protected final int maxIterations = 50;

    protected ProjectiveClusterObjectsSimilarity objectClusterSim;
    protected ProjectiveClusterFeaturesSimilarity featureClusterSim;

    protected ArrayList<Double> negativeCentroidRep = new ArrayList<Double>();

    protected ProjectiveClustering result;

    public OneObjectiveClusterBasedPCE_KMEANS (ProjectiveClusteringDataset ensemble)
    {
        this.ensemble = ensemble;
        this.objectClusterSim = new SquaredEuclideanObjectsProjectiveClusterSim();
        this.featureClusterSim = new SquaredEuclideanFeaturesProjectiveClusterSim();

        this.offlineExecutionTime = 0;
    }

    public ProjectiveClustering execute (int nClusters)
    {
        long startTime = System.currentTimeMillis();

        ProjectiveCluster[] dataset = this.ensemble.getClusters();

        //double SUM = 0.0;
        for (int i=0; i<dataset.length; i++)
        {
            for (int j=0; j<dataset[i].getFeatureVectorRepresentationDouble().length; j++)
            {
                double H = dataset[i].getFeatureVectorRepresentationDouble()[j];
                if (H < -0.000001 || H > 1.000001)
                {
                    boolean d = false;
                }
                //SUM += dataset[i].getFeatureVectorRepresentationDouble()[j];
            }
        }

        //int M = this.ensemble.getDataLength();
        //int K = this.ensemble.getNumberOfClustersInEachClustering();
        //int N = this.ensemble.getNumberOfInstances();

        double[][] centroidObjectRep = new double[nClusters][this.ensemble.getNumberOfInstances()];
        double[][] centroidFeatureRep = new double[nClusters][this.ensemble.getNumberOfFeaturesInEachCluster()];
        randomInitialCentroids(nClusters, dataset, centroidObjectRep, centroidFeatureRep);

        int[] datasetToClusterAssignments = new int[dataset.length];
        for (int i=0; i<datasetToClusterAssignments.length; i++)
        {
            datasetToClusterAssignments[i] = -1;
        }



        int currentIt = 1;
        boolean stop = false;

        while (!stop && currentIt<=this.maxIterations)
        {
            int[] clusterSizes = new int[nClusters];

            //step one: object-to-cluster assignment
            stop = true;
            for (int i=0; i<dataset.length; i++)
            {
                Double[] currObjectRep = dataset[i].getFeatureVectorRepresentationDouble();
                Double[] currFeatureRep = dataset[i].getFeatureToClusterAssignments();

                int assignment = -1;
                double minDist = Double.POSITIVE_INFINITY;

                for (int j=0; j<centroidObjectRep.length; j++)
                {
                    double currDistO = 0.0;
                    double currDistF = 0.0;
                    for (int k=0; k<centroidObjectRep[j].length; k++)
                    {
                        currDistO += (currObjectRep[k]-centroidObjectRep[j][k])*(currObjectRep[k]-centroidObjectRep[j][k]);
                    }
                    currDistO /= currObjectRep.length;
                    if (Double.isInfinite(currDistO) || Double.isNaN(currDistO) || currDistO<-0.0000001 || currDistO>1.0000001)
                    {
                        throw new RuntimeException("ERROR: currDistO must be within [0,1]---currDistO="+currDistO);
                    }


                    for (int k=0; k<centroidFeatureRep[j].length; k++)
                    {
                        currDistF += (currFeatureRep[k]-centroidFeatureRep[j][k])*(currFeatureRep[k]-centroidFeatureRep[j][k]);
                    }
                    currDistF /= currFeatureRep.length;
                    if (Double.isInfinite(currDistF) || Double.isNaN(currDistF) || currDistF<-0.0000001 || currDistF>1.0000001)
                    {
                        throw new RuntimeException("ERROR: currDistF must be within [0,1]---currDistF="+currDistF);
                    }

                    double currDist = currDistO+currDistF;

                    if (currDist < minDist)
                    {
                        minDist = currDist;
                        assignment = j;
                    }
                }

                if (assignment < 0 || assignment > nClusters-1)
                {
                    throw new RuntimeException("ERROR: assignment="+assignment);
                }


                if (datasetToClusterAssignments[i] != assignment)
                {
                    stop = false;
                }
                datasetToClusterAssignments[i] = assignment;
                clusterSizes[assignment]++;
            }


            if (!stop)
            {
                //step two: recomputing centroids
                //objects
                double X = 0.0;
                for (int i=0; i<clusterSizes.length; i++)
                {
                    if (clusterSizes[i] > 0)
                    {
                        X += ((double)1.0)/clusterSizes[i];
                    }
                }
                if (X > 0.0)
                {
                    X = ((double)1.0)/X;
                }
                else
                {
                    throw new RuntimeException("ERROR: X must be greater than zero---X="+X+" (come cazzo è possibile????)");
                }

                double[] Yn = new double[centroidObjectRep[0].length];
                for (int n=0; n<Yn.length; n++)
                {
        //########################################################################################################
//########################################################################################################
//########################################################################################################
                    //ATTENZIONE: ottimizzare il seguente ciclo!!!
                    //non ha senso per ogni centroide scorrere tutti gli oggetti
                    //si può fare tutto in una sola passata sugli oggetti
                    //si passa da una complessità K*N a una complessità N (K: #cluster, N: #oggetti)
//########################################################################################################
//########################################################################################################
//########################################################################################################


                    double sumInt[] = new double[nClusters];
                    for (int i=0; i<dataset.length; i++)
                    {
                        int j = datasetToClusterAssignments[i];
                        sumInt[j] += dataset[i].getFeatureVectorRepresentationDouble()[n]/clusterSizes[j];
                    }
                    double sumExt = 0.0;
                    for (int j=0; j<sumInt.length; j++)
                    {
                        sumExt += sumInt[j];
                    }

                    /*
                    double sumExt = 0.0;
                    for (int j=0; j<centroidObjectRep.length; j++)
                    {
                        double sumInt = 0.0;
                        if (clusterSizes[j] > 0)
                        {
                            for (int i=0; i<dataset.length; i++)
                            {
                                if (datasetToClusterAssignments[i] == j)
                                {
                                    sumInt += dataset[i].getFeatureVectorRepresentationDouble()[n];
                                }
                            }
                            sumInt /= clusterSizes[j];
                        }
                        sumExt += sumInt;
                    }
                    */
                    Yn[n] = sumExt-1;

                    /*
                    if (Yn[n] > 0)
                    {
                        boolean x = false;
                        sumExt = 0.0;
                        for (int j=0; j<centroidObjectRep.length; j++)
                        {
                            double sumInt = 0.0;
                            if (clusterSizes[j] > 0)
                            {
                                for (int i=0; i<dataset.length; i++)
                                {
                                    if (datasetToClusterAssignments[i] == j)
                                    {
                                        sumInt += dataset[i].getFeatureVectorRepresentationDouble()[n];
                                    }
                                }
                                sumInt /= clusterSizes[j];
                            }
                            sumExt += sumInt;
                        }

                    }
                    */
                }

                for (int j=0; j<centroidObjectRep.length; j++)
                {
                    double[] newObjectRep = new double[centroidObjectRep[j].length];

                    if (clusterSizes[j] > 0)
                    {
                        for (int n=0; n<newObjectRep.length; n++)
                        {
                            double sum = 0.0;
                            for (int i=0; i<dataset.length; i++)
                            {
                                if (datasetToClusterAssignments[i] == j)
                                {
                                    sum += dataset[i].getFeatureVectorRepresentationDouble()[n];
                                }
                            }
                            sum /= clusterSizes[j];

                            newObjectRep[n] = sum-X/clusterSizes[j]*Yn[n];
                        }
                    }

                    centroidObjectRep[j] = newObjectRep;
                }
                //test
                for (int n=0; n<centroidObjectRep[0].length; n++)
                {
                    double sumCheck = 0.0;
                    for (int j=0; j<centroidObjectRep.length; j++)
                    {
                        if (Double.isInfinite(centroidObjectRep[j][n]) || Double.isNaN(centroidObjectRep[j][n]))
                        {
                            throw new RuntimeException("ERROR: value must be within [0,1]---centroidObjectRep[j][n]="+centroidObjectRep[j][n]);
                        }

                        if (centroidObjectRep[j][n] < -0.0000001 || centroidObjectRep[j][n] > 1.0000001)
                        {
                            this.negativeCentroidRep.add(centroidObjectRep[j][n]);
                        }

                        sumCheck += centroidObjectRep[j][n];
                    }

                    if (sumCheck < 0.9999999 || sumCheck > 1.0000001)
                    {
                        throw new RuntimeException("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);
                    }
                }

                // features
                for (int j=0; j<centroidFeatureRep.length; j++)
                {
                    double[] newFeatureRep = new double[centroidFeatureRep[j].length];

                    if (clusterSizes[j] > 0)
                    {
                        for (int k=0; k<newFeatureRep.length; k++)
                        {
                            double sum = 0.0;
                            for (int i=0; i<datasetToClusterAssignments.length; i++)
                            {
                                if (datasetToClusterAssignments[i] == j)
                                {
                                    sum += dataset[i].getFeatureToClusterAssignments()[k];
                                }
                            }

                            newFeatureRep[k] = sum/clusterSizes[j];
                        }
                    }
                    else
                    {
                        for (int k=0; k<newFeatureRep.length; k++)
                        {
                            newFeatureRep[k] = ((double)1.0)/newFeatureRep.length;
                        }
                    }

                    //test
                    double sumCheck =0.0;
                    for (int k=0; k<newFeatureRep.length; k++)
                    {
                        if (Double.isInfinite(newFeatureRep[k]) || Double.isNaN(newFeatureRep[k]) || newFeatureRep[k] < -0.0000001 || newFeatureRep[k] > 1.0000001)
                        {
                            throw new RuntimeException("ERROR: value must be within [0,1]---newFeatureRep[k]="+newFeatureRep[k]);
                        }

                        sumCheck += newFeatureRep[k];
                    }
                    if (sumCheck < 0.9999999 || sumCheck > 1.0000001)
                    {
                        throw new RuntimeException("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);
                    }

                    centroidFeatureRep[j] = newFeatureRep;
                }
            }

            currentIt++;
        }

        ProjectiveCluster[] outputClusters = new ProjectiveCluster[nClusters];
        for (int i=0; i<outputClusters.length; i++)
        {
            Double[] objects = new Double[centroidObjectRep[i].length];
            for (int n=0; n<objects.length; n++)
            {
                objects[n] = new Double(centroidObjectRep[i][n]);
            }
            Double[] features = new Double[centroidFeatureRep[i].length];
            for (int d=0; d<features.length; d++)
            {
                features[d] = new Double(centroidFeatureRep[i][d]);
            }
            outputClusters[i] = new ProjectiveCluster(this.ensemble.getInstances(), objects, features, -(i+1), true, true);
        }

        long stopTime = System.currentTimeMillis();

        this.onlineExecutionTime = stopTime-startTime;


        //System.out.println("####################################################");
        System.out.println("OneObjectiveClusterBasedSubspaceEnsembles_KMEANS terminated");
        System.out.println("Number of iterations="+(currentIt-1)+" Time="+this.getOnlineExecutionTime());
        //System.out.println("####################################################");

        return new ProjectiveClustering(outputClusters);
    }

    protected void randomInitialCentroids(int nClusters, ProjectiveCluster[] dataset, double[][] centroidObjectRep, double[][] centroidFeatureRep)
    {
        boolean[] chosen = new boolean[dataset.length];
        for (int i=0; i<chosen.length; i++)
        {
            chosen[i] = false;
        }

        for (int i=0; i<nClusters; i++)
        {
            int x=-1;
            do
            {
                x = (int)Math.rint(Math.random()*(dataset.length-1));
            }
            while(chosen[x]);

            chosen[x] = true;

            for (int n=0; n<centroidObjectRep[i].length; n++)
            {
                centroidObjectRep[i][n] = dataset[x].getFeatureVectorRepresentationDouble()[n].doubleValue();
            }
            for (int d=0; d<centroidFeatureRep[i].length; d++)
            {
                centroidFeatureRep[i][d] = dataset[x].getFeatureToClusterAssignments()[d].doubleValue();
            }
        }

        for (int n=0; n<centroidObjectRep[0].length; n++)
        {
            double sum = 0.0;
            for (int k=0; k<centroidObjectRep.length; k++)
            {
                sum += centroidObjectRep[k][n];
            }

            if (sum > 0)
            {
                for (int k=0; k<centroidObjectRep.length; k++)
                {
                    centroidObjectRep[k][n]/=sum;
                }
            }
            else
            {
                for (int k=0; k<centroidObjectRep.length; k++)
                {
                    centroidObjectRep[k][n] = ((double)1.0)/centroidObjectRep.length;
                }
            }
        }
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

    public ArrayList<Double> getNegativeObjectRep()
    {
        return this.negativeCentroidRep;
    }
}

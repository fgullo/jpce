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


public class OneObjectiveClusterBasedPCE_KMEANS_NORMALIZATION extends ClusterBasedPCEMethod
{
    protected final int maxIterations = 15;

    protected ProjectiveClusterObjectsSimilarity objectClusterSim;
    protected ProjectiveClusterFeaturesSimilarity featureClusterSim;

    protected ArrayList<Double> negativeCentroidRep = new ArrayList<Double>();

    protected ProjectiveClustering result;

    public OneObjectiveClusterBasedPCE_KMEANS_NORMALIZATION (ProjectiveClusteringDataset ensemble)
    {
        this.ensemble = ensemble;
        this.objectClusterSim = new SquaredEuclideanObjectsProjectiveClusterSim();
        this.featureClusterSim = new SquaredEuclideanFeaturesProjectiveClusterSim();

        this.offlineExecutionTime = 0;
    }

    public ProjectiveClustering execute (int nClusters)
    {
        long startTime = System.currentTimeMillis();

        //ProjectiveCluster[] datasetTmp = this.ensemble.getClusters();

        double[][] allNormClustersObjectRep = new double[this.ensemble.getClusters().length][this.ensemble.getNumberOfInstances()];
        double[][] allNormClustersFeatureRep = new double[this.ensemble.getClusters().length][this.ensemble.getNumberOfFeaturesInEachCluster()];

        normalize(allNormClustersObjectRep, allNormClustersFeatureRep);

        //int M = this.ensemble.getDataLength();
        //int K = this.ensemble.getNumberOfClustersInEachClustering();
        //int N = this.ensemble.getNumberOfInstances();

        double[][] centroidObjectRep = new double[nClusters][this.ensemble.getNumberOfInstances()];
        double[][] centroidFeatureRep = new double[nClusters][this.ensemble.getNumberOfFeaturesInEachCluster()];
        randomInitialCentroids(nClusters, allNormClustersObjectRep, allNormClustersFeatureRep, centroidObjectRep, centroidFeatureRep);

        int[] datasetToClusterAssignments = new int[allNormClustersObjectRep.length];
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
            for (int i=0; i<allNormClustersObjectRep.length; i++)
            {
                double[] currObjectRep = allNormClustersObjectRep[i];
                double[] currFeatureRep = allNormClustersFeatureRep[i];

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

                    //System.out.println("DistO="+currDistO+"   DistF="+currDistF);

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
                    double sumInt[] = new double[nClusters];
                    for (int i=0; i<allNormClustersObjectRep.length; i++)
                    {
                        int j = datasetToClusterAssignments[i];
                        sumInt[j] += allNormClustersObjectRep[i][n]/clusterSizes[j];
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
                            for (int i=0; i<allNormClustersObjectRep.length; i++)
                            {
                                if (datasetToClusterAssignments[i] == j)
                                {
                                    sumInt += allNormClustersObjectRep[i][n];
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
                            for (int i=0; i<allNormClustersObjectRep.length; i++)
                            {
                                if (datasetToClusterAssignments[i] == j)
                                {
                                    sum += allNormClustersObjectRep[i][n];
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
                            throw new RuntimeException("ERROR: centroidObjectRep[j][n] must be within [0,1]---centroidObjectRep[j][n]="+centroidObjectRep[j][n]);
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
                                    sum += allNormClustersFeatureRep[i][k];
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

    protected void randomInitialCentroids(int nClusters, double[][] allNormClustersObjectRep, double[][] allNormClustersFeatureRep, double[][] centroidObjectRep, double[][] centroidFeatureRep)
    {
        boolean[] chosen = new boolean[allNormClustersObjectRep.length];
        for (int i=0; i<chosen.length; i++)
        {
            chosen[i] = false;
        }

        for (int i=0; i<nClusters; i++)
        {
            int x=-1;
            do
            {
                x = (int)Math.rint(Math.random()*(allNormClustersObjectRep.length-1));
            }
            while(chosen[x]);

            chosen[x] = true;

            for (int n=0; n<centroidObjectRep[i].length; n++)
            {
                centroidObjectRep[i][n] = allNormClustersObjectRep[x][n];
            }
            for (int d=0; d<centroidFeatureRep[i].length; d++)
            {
                centroidFeatureRep[i][d] = allNormClustersFeatureRep[x][d];
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

    private void normalize(double[][] allNormClustersObjectRep, double[][] allNormClustersFeatureRep)
    {
        ProjectiveCluster[] datasetTmp = this.ensemble.getClusters();
        //int M = 1;
        int M = this.ensemble.getDataLength();

        for (int n=0; n<allNormClustersObjectRep[0].length; n++)
        {
            double check = 0.0;
            for (int j=0; j<allNormClustersObjectRep.length; j++)
            {
                allNormClustersObjectRep[j][n] = datasetTmp[j].getFeatureVectorRepresentationDouble()[n].doubleValue()/M;
                check += allNormClustersObjectRep[j][n];
            }

            
            if (check < 0.9999999 || check > 1.0000001)
            {
                throw new RuntimeException("ERROR: check must be equal to 1");
            }
            
            
        }

        for (int j=0; j<allNormClustersFeatureRep.length; j++)
        {
            for (int d=0; d<allNormClustersFeatureRep[j].length; d++)
            {
                allNormClustersFeatureRep[j][d] = datasetTmp[j].getFeatureToClusterAssignments()[d].doubleValue();
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


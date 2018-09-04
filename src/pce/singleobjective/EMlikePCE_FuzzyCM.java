/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce.singleobjective;

import dataset.ProjectiveClusteringDataset;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.PCEMethod;


public class EMlikePCE_FuzzyCM extends PCEMethod
{
    protected double[][] objectByFeatureMatrix;
    protected int maxIterations = 100;
    protected int m1=2;
    protected int m2=2;
    
    protected ProjectiveClustering result;
    
    //public EMlikeSubspaceEnsembles (ProjectiveClusteringDataset ensemble, SubspaceClusterObjectsSimilarity objectClusterSim, SubspaceClusterFeaturesSimilarity featureClusterSim)
    public EMlikePCE_FuzzyCM (ProjectiveClusteringDataset ensemble)
    {
        this.ensemble = ensemble;        
        this.objectByFeatureMatrix = this.ensemble.getObjectByFeatureMatrix();
    }
    
    public ProjectiveClustering execute (int nClusters)
    {
        if (nClusters != this.ensemble.getNumberOfClustersInEachClustering())
        {
            System.out.println("WARNING: the number of clusters is different from the number of clusters in each clustering of the ensemble");
        }
        
        Double[][] objectSolution = new Double[nClusters][this.ensemble.getNumberOfInstances()];
        Double[][] featureSolution = new Double[nClusters][this.ensemble.getNumberOfFeaturesInEachCluster()];
        double[] kj = new double[nClusters];
        
        //uniformInizialization(objectSolution, featureSolution, kj);
        randomInizialization(objectSolution, featureSolution, kj, nClusters);
        
        int currentIt=1;
        boolean stop = false;
        
        while (currentIt <= this.maxIterations && !stop)
        {
            stop = true;
            
            //feature assignment
            for (int j=0; j<featureSolution.length; j++)
            {
                Double[] wji_tmp = new Double[featureSolution[j].length];
                double sum = 0.0;
                for (int i=0; i<featureSolution[j].length; i++)
                {
                    double xji = 0.0;
                    for (int l=0; l<objectSolution[j].length; l++)
                    {
                        /*
                        if (this.objectByFeatureMatrix[l][i] < -0.000000001 || this.objectByFeatureMatrix[l][i] > 1.000000001)
                        {
                            
                            throw new RuntimeException("ERROR: the value must be within [0,1]----the value is "+this.objectByFeatureMatrix[l][i]);
                        }
                        double value = this.objectByFeatureMatrix[l][i];
                        if (this.objectByFeatureMatrix[l][i] < 0.0)
                        {
                            value = 0.0;
                        }
                        else if (this.objectByFeatureMatrix[l][i] > 1.0)
                        {
                            value = 1.0;
                        }
                        */
                        
                        double value = 1-this.objectByFeatureMatrix[l][i];
                        if (this.objectByFeatureMatrix[l][i] < 0.0)
                        {
                            value = 1.0;
                        }
                        else if (this.objectByFeatureMatrix[l][i] > 1.0)
                        {
                            value = 0.0;
                        }
                        
                        //xji += objectSolution[j][l]*this.objectByFeatureMatrix[l][i];
                        xji += Math.pow(objectSolution[j][l],this.m2)*value;
                    }
                    xji /= kj[j];
                    //wji_tmp[i] = 1-Math.exp(-xji/h1);
                    wji_tmp[i] = Math.pow(xji, ((double)1.0)/(this.m2-1));
                    if (xji != 0.0)
                    {
                        double value = Math.pow(((double)1.0)/xji, ((double)1.0)/(this.m1-1));
                        sum += value;
                    }
                }
                
                for (int i=0; i<wji_tmp.length; i++)
                {
                    double value = wji_tmp[i]*sum;
                    if (value == 0.0)
                    {
                        wji_tmp[i] = 1.0;
                    }
                    else
                    {
                        wji_tmp[i] = ((double)1.0)/value;
                    }
                    
                    //if (wji_tmp[i] != featureSolution[j][i])
                    if (Math.abs(wji_tmp[i]-featureSolution[j][i]) > 0.000000000001)
                    {
                        stop = false;
                    }
                }
                
                featureSolution[j] = wji_tmp;
            }
            
            //object assignment
            for (int l=0; l<objectSolution[0].length; l++)
            {
                double sum = 0.0;
                double[] ujl_tmp = new double[objectSolution.length];
                
                for (int j=0; j<objectSolution.length; j++)
                {
                    double yjl = 0.0;
                    for (int i=0; i<featureSolution[j].length; i++)
                    {
                        /*
                        if (this.objectByFeatureMatrix[l][i] < -0.000000001 || this.objectByFeatureMatrix[l][i] > 1.000000001)
                        {
                            throw new RuntimeException("ERROR: the value must be within [0,1]----the value is "+this.objectByFeatureMatrix[l][i]);
                        }
                        double value = this.objectByFeatureMatrix[l][i];
                        if (this.objectByFeatureMatrix[l][i] < 0.0)
                        {
                            value = 0.0;
                        }
                        else if (this.objectByFeatureMatrix[l][i] > 1.0)
                        {
                            value = 1.0;
                        }
                        */
                        
                        double value = 1-this.objectByFeatureMatrix[l][i];
                        if (this.objectByFeatureMatrix[l][i] < 0.0)
                        {
                            value = 1.0;
                        }
                        else if (this.objectByFeatureMatrix[l][i] > 1.0)
                        {
                            value = 0.0;
                        }
                        
                        //yjl += featureSolution[j][i]*this.objectByFeatureMatrix[l][i];
                        yjl += Math.pow(featureSolution[j][i],this.m1)*value;
                    }
                    yjl /= kj[j];
                    //ujl_tmp[j] = 1-Math.exp(-yjl/h2);
                    ujl_tmp[j] = Math.pow(yjl, ((double)1.0)/(this.m2-1));
                    if (yjl != 0.0)
                    {
                        double value = Math.pow(((double)1.0)/yjl, ((double)1.0)/(this.m2-1));
                        sum += value;
                    }
                }
                
                for (int j=0; j<objectSolution.length; j++)
                {
                    double value = ujl_tmp[j]*sum;
                    if (value == 0.0)
                    {
                        ujl_tmp[j] = 1.0;
                    }
                    else
                    {
                        ujl_tmp[j] = ((double)1.0)/value;
                    }
                    //if (ujl_tmp[j] != objectSolution[j][l])
                    if (Math.abs(ujl_tmp[j]-objectSolution[j][l]) > 0.000000000001)
                    {
                        stop = false;
                    }
                    objectSolution[j][l] = ujl_tmp[j];
                }
            }
            
            currentIt++;
        }
        
        System.out.println("\n##########################################################");
        System.out.println("EMlikeSubspaceEnsemble---NumberOfIterations="+(currentIt-1));
        System.out.println("##########################################################\n");
        
        ProjectiveCluster[] clusters = new ProjectiveCluster[nClusters];
        for (int j=0; j<clusters.length; j++)
        {
            clusters[j] = new ProjectiveCluster(this.ensemble.getInstances(), objectSolution[j], featureSolution[j], -(j+1), true, true);
        }
        
        this.result = new ProjectiveClustering(clusters, -1);
        return this.result;
    }
    
    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }
    
    protected void uniformInizialization(Double[][] objectSolution, Double[][] featureSolution, double[] kj)
    {
        for (int j=0; j<objectSolution.length; j++)
        {
            for (int i=0; i<objectSolution[j].length; i++)
            {
                objectSolution[j][i] = ((double)1.0)/objectSolution.length;
            }
            kj[j] = ((double)1.0)/objectSolution.length*objectSolution[0].length; 
        }
        
        for (int j=0; j<featureSolution.length; j++)
        {
            for (int i=0; i<featureSolution[j].length; i++)
            {
                featureSolution[j][i] = ((double)1.0)/featureSolution[j].length;
            }
        }        
    }
    
    protected void randomInizialization(Double[][] objectSolution, Double[][] featureSolution, double[] kj, int nClusters)
    {
        ProjectiveClustering random = ProjectiveClustering.randomGen(this.ensemble.getInstances(), nClusters, this.ensemble.getNumberOfInstances(), this.ensemble.getNumberOfFeaturesInEachCluster());
        
        Double[][] rndObjectSolution = random.getClusterByObjectsMatrix();
        for (int j=0; j<rndObjectSolution.length; j++)
        {
            kj[j] = 0.0;
            for (int l=0; l<rndObjectSolution[j].length; l++)
            {
                objectSolution[j][l] = new Double(rndObjectSolution[j][l].doubleValue());
                kj[j] += objectSolution[j][l];
            }
        }
        
        Double[][] rndFeatureSolution = random.getClusterByFeaturesMatrix();
        for (int j=0; j<rndFeatureSolution.length; j++)
        {
            double sum = 0.0;
            for (int i=0; i<rndFeatureSolution[j].length; i++)
            {
                sum += rndFeatureSolution[j][i];
            }
            
            for (int i=0; i<rndFeatureSolution[j].length; i++)
            {
                featureSolution[j][i] = new Double(rndFeatureSolution[j][i].doubleValue()/sum);
            }
        }
    }

}


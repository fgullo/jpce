/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce.singleobjective;

import dataset.ProjectiveClusteringDataset;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.PCEMethod;


public class EMlikePCE_ObjectByFeatureMatrix extends PCEMethod
{
    protected Double[][] allClusterObjectRep;
    protected Double[][] allClusterFeatureRep;
    protected int maxIterations = 50;
    protected int m=2;
    
    protected ProjectiveClustering result;
    
    //public EMlikeSubspaceEnsembles (ProjectiveClusteringDataset ensemble, SubspaceClusterObjectsSimilarity objectClusterSim, SubspaceClusterFeaturesSimilarity featureClusterSim)
    public EMlikePCE_ObjectByFeatureMatrix (ProjectiveClusteringDataset ensemble)
    {
        this.ensemble = ensemble;        
        this.allClusterObjectRep = this.ensemble.getAllClusterByObjectRepresentationMatrix();
        this.allClusterFeatureRep = this.ensemble.getAllClusterByfeatureRepresentationMatrix();
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
            
            //object assignment
            for (int l=0; l<objectSolution[0].length; l++)
            {
                double[] zjl = new double[objectSolution.length];
                int exactAssignment = -1; 
                for (int j=0; j<objectSolution.length; j++)
                {
                    for (int c=0; c<this.allClusterObjectRep.length; c++)
                    {
                        for (int i=0; i<this.allClusterFeatureRep[c].length; i++)
                        {
                            double dist = (featureSolution[j][i]-this.allClusterFeatureRep[c][i])*(featureSolution[j][i]-this.allClusterFeatureRep[c][i]);
                            zjl[j] += this.allClusterObjectRep[c][l]*dist;
                            
                            if (zjl[j] == 0.0)
                            {
                                exactAssignment = j;
                            }
                        }
                    }
                }
                
                if (exactAssignment != -1)
                {
                    double newAssignment = 1.0;
                    if (Math.abs(newAssignment-objectSolution[exactAssignment][l]) > 0.00000001)
                    {
                        stop = false;
                    }
                    
                    for (int j=0; j<objectSolution.length; j++)
                    {
                        objectSolution[j][l] = 0.0;
                    }
                    objectSolution[exactAssignment][l] = new Double(1.0);
                }
                else
                {
                    double sum = 0.0;
                    for (int j=0; j<zjl.length; j++)
                    {
                        sum += Math.pow(((double)1.0)/zjl[j],((double)1.0)/(this.m-1));
                    }
                    
                    double sumCheck = 0.0;
                    for (int j=0; j<objectSolution.length; j++)
                    {
                        double value1 = Math.pow(zjl[j], ((double)1.0)/(this.m-1))*sum;
                        double value = ((double)1.0)/value1;
                        
                        if (Double.isInfinite(value) || Double.isNaN(value) || value < 0.0 || value > 1.0)
                        {
                            throw new RuntimeException ("ERROR: value must be within [0,1]---value="+value);
                        }
                        
                        if (Math.abs(value-objectSolution[j][l]) > 0.00000001)
                        {
                            stop = false;
                        }
                        
                        sumCheck += value;
                        
                        objectSolution[j][l] = new Double(value);                        
                    }
                    
                    if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.999999999 || sumCheck > 1.000000001)
                    {
                        throw new RuntimeException ("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);
                    }
                }
            }
            
            
            //feature assignment
            for (int j=0; j<featureSolution.length; j++)
            {
                double xj = 0.0;
                for (int l=0; l<objectSolution[j].length; l++)
                {
                    for (int c=0; c<this.allClusterObjectRep.length; c++)
                    {
                        xj += Math.pow(objectSolution[j][l],this.m)*this.allClusterObjectRep[c][l];
                    }
                }
                
                //N.B. if xj==0, then cluster j doen not have any object; so, the feature should have equel weight
                if (xj == 0.0)
                {
                    for (int i=0; i<featureSolution[j].length; i++)
                    {  
                        double newAssignment = ((double)1.0)/featureSolution[j].length;
                        if (Math.abs(newAssignment-featureSolution[j][i]) > 0.00000001)
                        {
                            stop = false;
                        }
                        featureSolution[j][i] = newAssignment;
                    }                  
                }
                else
                {
                    double sum = 0.0;
                    double[] newAssignment = new double[featureSolution[j].length];
                    for (int i=0; i<featureSolution[j].length; i++)
                    {
                        double yji = 0.0;
                        for (int l=0; l<objectSolution[j].length; l++)
                        {
                            for (int c=0; c<this.allClusterObjectRep.length; c++)
                            {
                                yji += Math.pow(objectSolution[j][l],this.m)*this.allClusterObjectRep[c][l]*this.allClusterFeatureRep[c][i];
                            }
                        }
                        sum += yji;
                        newAssignment[i] = yji;
                    }
                    
                    double sumCheck = 0.0;
                    for (int i=0; i<featureSolution[j].length; i++)
                    {
                        double value1 = newAssignment[i]/xj;
                        double value2 = ((double)1.0)/featureSolution[j].length;
                        double value3 = -(((double)1.0)/(featureSolution[j].length*xj))*sum;
                        
                        double value = value1+value2+value3;
                        
                        if (Double.isInfinite(value) || Double.isNaN(value) || value < 0.0 || value > 1.0)
                        {
                            throw new RuntimeException ("ERROR: value must be within [0,1]---value="+value);
                        }
                        
                        if (Math.abs(value-featureSolution[j][i]) > 0.00000001)
                        {
                            stop = false;
                        }
                        
                        sumCheck += value;
                        
                        featureSolution[j][i] = new Double(value);
                    }
                    
                    if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.999999999 || sumCheck > 1.000000001)
                    {
                        throw new RuntimeException ("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);
                    }
                }
            }        
            
            currentIt++;            
        }
    
        //System.out.println("\n##########################################################");
        System.out.println("EMlikeSubspaceEnsemble_ObjectByFeatureMatrix---NumberOfIterations="+(currentIt-1));
        //System.out.println("##########################################################\n");
        
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



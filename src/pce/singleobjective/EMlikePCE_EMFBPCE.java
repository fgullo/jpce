/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce.singleobjective;

import dataset.ProjectiveClusteringDataset;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.PCEMethod;
import pce.singleobjective.EMlikeMethod;


public class EMlikePCE_EMFBPCE extends PCEMethod implements EMlikeMethod
{
    protected Double[][] allClusterObjectRep;
    protected Double[][] allClusterFeatureRep;
    protected double[] psii;
    protected double[] tetai;
    protected double[][] Cli;
    protected int maxIterations = 50;
    //protected double k=1.01;
    protected int m = 2;
    
    protected double epsilon = 0.00000001;
    
    protected ProjectiveClustering result;
    
    //public EMlikeSubspaceEnsembles (ProjectiveClusteringDataset ensemble, SubspaceClusterObjectsSimilarity objectClusterSim, SubspaceClusterFeaturesSimilarity featureClusterSim)
    public EMlikePCE_EMFBPCE(ProjectiveClusteringDataset ensemble)
    {
        this.offlineExecutionTime = 0;

        long start = System.currentTimeMillis();
        this.ensemble = ensemble;
        this.allClusterObjectRep = this.ensemble.getAllClusterByObjectRepresentationMatrix();
        this.allClusterFeatureRep = this.ensemble.getAllClusterByfeatureRepresentationMatrix();
        initialization();
        long stop = System.currentTimeMillis();
        this.offlineExecutionTime += stop-start;
    }
    
    public ProjectiveClustering execute (int nClusters)
    {
        long startTime = System.currentTimeMillis();

        if (nClusters != this.ensemble.getNumberOfClustersInEachClustering())
        {
            System.out.println("WARNING: the number of clusters is different from the number of clusters in each clustering of the ensemble");
        }
        
        double previousError = Double.POSITIVE_INFINITY;
        
        Double[][] objectSolution = new Double[nClusters][this.ensemble.getNumberOfInstances()];
        Double[][] featureSolution = new Double[nClusters][this.ensemble.getNumberOfFeaturesInEachCluster()];
        double[] kj = new double[nClusters];
        
        //uniformInizialization(objectSolution, featureSolution, kj);
        randomInizialization(objectSolution, featureSolution, kj, nClusters);
        
        int currentIt=1;
        boolean stop = false;
        
        
        System.out.println("EMlikeSubspaceEnsembles_EM-FB-PCE");
        while (currentIt <= this.maxIterations && !stop)
        {
            stop = true;
            double currentError = 0.0;
            
            //feature assignment            
            for (int j=0; j<featureSolution.length; j++)
            {
                double term1 = 0.0;
                for (int l=0; l<objectSolution[j].length; l++)
                {
                    term1 += objectSolution[j][l]*objectSolution[j][l];
                }                
                
                double[] Aji = new double[featureSolution[j].length];
                int exactAssignments = 0;                
                int wrongAssignments = 0;
                boolean allZero = true;
                
                for (int i=0; i<featureSolution[j].length; i++)
                {
                    double term2 = this.tetai[i];
                    double term3 = 0.0;
                    double termNew = 0.0;
                    
                    for (int l=0; l<objectSolution[j].length; l++)
                    {
                        term3 += objectSolution[j][l]*this.Cli[l][i];
                        termNew += Math.pow(objectSolution[j][l]-this.Cli[l][i]/this.ensemble.getDataLength(),2);
                    }

                    
                    //Aji[i] = this.psii[i]*term1+term2-2*term3;
                    Aji[i] = termNew;
                    
                    if (Aji[i] == 0)
                    {
                        if (this.psii[i] == 0.0)
                        {
                            wrongAssignments++;
                        }
                        else
                        {
                            exactAssignments++;
                        }
                    }
                    else
                    {
                        allZero = false;
                    }
                    
                    currentError += Aji[i]*Math.pow(featureSolution[j][i], this.m);
                    //currentError += Aji[i]*Math.pow(featureSolution[j][i], this.k);
                }
                
                
                
                if (exactAssignments > 0)
                {
                    for (int i=0; i<featureSolution[j].length; i++)
                    {
                        if (Aji[i] == 0 && this.psii[i] > 0.0)// if feature 'i' is an 'exact assignment'
                        {
                            double value = ((double)1.0)/exactAssignments;
                            if (Math.abs(value-featureSolution[j][i]) > 0.00000001)
                            {
                                stop = false;
                            } 
                            featureSolution[j][i] = value;
                        }
                        else
                        {
                            if (featureSolution[j][i] > 0.00000001)
                            {
                                stop = false;
                            }
                            featureSolution[j][i] = 0.0;
                        }
                    }      
                }
                else if (allZero) //all wrong assignments: equal assignment to all the features
                {
                     for (int i=0; i<featureSolution[j].length; i++)
                    {
                        double value = ((double)1.0)/featureSolution[j].length;
                        if (Math.abs(value-featureSolution[j][i]) > 0.00000001)
                        {
                            stop = false;
                        }                        
                        featureSolution[j][i] = value;
                    }                    
                }
                else
                {
                    double sum = 0.0;
                    for (int i=0; i<Aji.length; i++)
                    {
                        if (Aji[i] > 0.0)
                        {
                            sum += Math.pow(((double)1.0)/Aji[i],((double)1.0)/(this.m-1));
                            //sum += Math.pow(((double)1.0)/Aji[i],((double)1.0)/(this.k-1));
                        }
                    }
              
                    double sumCheck = 0.0;
                    for (int i=0; i<featureSolution[j].length; i++)
                    {
                        if (Aji[i] == 0.0)//wrong assignment
                        { 
                            if (featureSolution[j][i] > 0.00000001)
                            {
                                stop = false;
                            }
                            featureSolution[j][i] = 0.0;
                        }
                        else
                        {                        
                            double value1 = Math.pow(Aji[i], ((double)1.0)/(this.m-1))*sum;
                            //double value1 = Math.pow(Aji[i], ((double)1.0)/(this.k-1))*sum;
                            double value = ((double)1.0)/value1;
                        
                            if (Double.isInfinite(value) || Double.isNaN(value) || value < -0.000000001 || value > 1.000000001)
                            {
                                throw new RuntimeException ("ERROR: value must be within [0,1]---value="+value);
                            }
                        
                            if (value < 0.0)
                            {
                                value = 0.0;
                            }
                        
                            if (value > 1.0)
                            {
                                value = 1.0;
                            }
                        
                            if (Math.abs(value-featureSolution[j][i]) > 0.00000001)
                            {
                                stop = false;
                            }
                        
                            sumCheck += value;
                        
                            featureSolution[j][i] = new Double(value);                        
                        }
                    }
                    if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.999999999 || sumCheck > 1.000000001)
                    {
                        throw new RuntimeException ("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);
                    }
                }
            }
            
            
            //object assignment
            double[] Bk = new double[objectSolution.length];
            double sumBk = 0.0;
            for (int j=0; j<Bk.length; j++)
            {
                for (int i=0; i<featureSolution[j].length; i++)
                {
                    Bk[j] += Math.pow(featureSolution[j][i], this.m)*this.psii[i];
                    //Bk[j] += Math.pow(featureSolution[j][i], this.k)*this.psii[i];
                }
                
                if (Bk[j] > 0.0)
                {
                    sumBk += ((double)1.0)/Bk[j];
                }
            }
            for (int l=0; l<objectSolution[0].length; l++)
            {
                double[] Cjl = new double[objectSolution.length];
                boolean allZero = true;
                
                double sumCjl = 0.0;
                
                for (int j=0; j<objectSolution.length; j++)
                {
                    for (int i=0; i<featureSolution[j].length; i++)
                    {
                        Cjl[j] += Math.pow(featureSolution[j][i],this.m)*this.Cli[l][i];
                        //Cjl[j] += Math.pow(featureSolution[j][i],this.k)*this.Cli[l][i];
                    }
                    
                    if (Cjl[j] > 0.0)
                    {
                        allZero = false;
                        sumCjl += Cjl[j]/Bk[j];

                        if (Double.isInfinite(Cjl[j]/Bk[j]) || Double.isNaN(Cjl[j]/Bk[j]))
                        {
                            throw new RuntimeException("ERROR: Cjl[j]/Bk[j]="+Cjl[j]/Bk[j]);
                        }
                    }
                }
                
                if (allZero)
                {
                    for (int j=0; j<objectSolution.length; j++)
                    {
                        if (Math.abs(objectSolution[j][l]-1.0) > 0.00000001*((double)nClusters))
                        {
                            stop = false;
                        }
                        objectSolution[j][l] = 1.0;
                    }                       
                }
                else
                {
                    double sumCheck = 0.0;
                    for (int j=0; j<objectSolution.length; j++)
                    {
                        if (Cjl[j] == 0.0)
                        {
                            if (Math.abs(objectSolution[j][l]) > 0.00000001*((double)nClusters))
                            {
                                stop = false;
                            }                            
                            objectSolution[j][l] = 0.0;
                        }
                        else
                        {
                            double tmp = (nClusters-sumCjl)/(Bk[j]*sumBk);
                            double value = Cjl[j]/Bk[j]+tmp;
                            
                            if (Double.isInfinite(value) || Double.isNaN(value) || value < -0.000000001 || value > 1.000000001*((double)nClusters))
                            {
                                throw new RuntimeException ("ERROR: value must be within [0,"+nClusters+"]---value="+value);
                            }

                            if (value < 0.0)
                            {
                                value = 0.0;
                            }

                            if (value > nClusters)
                            {
                                value = nClusters;
                            }
                            
                            if (Math.abs(objectSolution[j][l]-value) > 0.00000001*((double)nClusters))
                            {
                                stop = false;
                            }
                            
                            sumCheck += value;
                            
                            objectSolution[j][l] = value;
                        }
                    }
                    
                    if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.999999999*((double)nClusters) || sumCheck > 1.000000001*((double)nClusters))
                    {
                        throw new RuntimeException ("ERROR: sumCheck must be equal to "+nClusters+"---sumCheck="+sumCheck);
                    }
                }
            }
            

            System.out.println("ERROR="+currentError);

            
            if (previousError-currentError < -0.1)
            {
                throw new RuntimeException("ERROR: previous error lower than current error---previous="+previousError+", current="+currentError);
            }
           
            
            
            if (Math.abs(previousError - currentError) <= this.epsilon)
            {
                stop = true;
            }
            else
            {
                stop = false;
            }
            previousError = currentError;
            
            
            System.out.print("it="+currentIt+"--");
            
            currentIt++;            
        }
    
        //System.out.println("\n##########################################################");
        //System.out.println("EMlikeSubspaceEnsemble_ObjectByFeatureMatrix---NumberOfIterations="+(currentIt-1));
        //System.out.println("##########################################################\n");
        System.out.println();
        
        for (int j=0; j<objectSolution.length; j++)
        {
            for (int l=0; l<objectSolution[j].length; l++)
            {
                objectSolution[j][l] /= nClusters;
            }
        }
        
        ProjectiveCluster[] clusters = new ProjectiveCluster[nClusters];
        for (int j=0; j<clusters.length; j++)
        {
            clusters[j] = new ProjectiveCluster(this.ensemble.getInstances(), objectSolution[j], featureSolution[j], -(j+1), true, true);
        }
        
        this.result = new ProjectiveClustering(clusters, -1);

        long stopTime = System.currentTimeMillis();

        this.onlineExecutionTime = stopTime-startTime;

        System.out.println("EM-FB-PCE: OnlineExecutionTime="+this.getOnlineExecutionTime());

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
                objectSolution[j][l] = new Double(rndObjectSolution[j][l].doubleValue()*((double)nClusters));
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
    
    protected void initialization()
    {
        this.psii = new double[this.ensemble.getNumberOfFeaturesInEachCluster()];
        this.tetai = new double[this.ensemble.getNumberOfFeaturesInEachCluster()];
        this.Cli = new double[this.ensemble.getNumberOfInstances()][this.ensemble.getNumberOfFeaturesInEachCluster()];
        
        
        for (int i=0; i<this.psii.length; i++)
        {
            for (int c=0; c<this.allClusterObjectRep.length; c++)
            {
                this.psii[i] += this.allClusterFeatureRep[c][i];
            }
        }
         
        
        for (int i=0; i<this.tetai.length; i++)
        {
            for (int c=0; c<this.allClusterFeatureRep.length; c++)
            {
                for (int l=0; l<this.allClusterObjectRep[c].length; l++)
                {
                    this.tetai[i] += this.allClusterFeatureRep[c][i]*this.allClusterObjectRep[c][l]*this.allClusterObjectRep[c][l];
                }
            }
        }
        
        for (int l=0; l<this.Cli.length; l++)
        {
            for (int i=0; i<this.Cli[l].length; i++)
            {
                for (int c=0; c<this.allClusterObjectRep.length; c++)
                {
                    this.Cli[l][i] += this.allClusterObjectRep[c][l]*this.allClusterFeatureRep[c][i];
                }
            }
        }
    }
    
    public void setM(int m)
    {
        this.m = m;
    }

}


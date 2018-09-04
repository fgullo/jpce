package pce.enhancedsingleobjective;

import dataset.ProjectiveClusteringDataset;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.AlphaBetaPCEMethod;
import pce.PCEMethod;
import pce.singleobjective.EMlikeMethod;


public class EMlikePCE_EEMPCE_LATEST extends PCEMethod implements EMlikeMethod,AlphaBetaPCEMethod
{
    protected Double[][] allClusterObjectRep;
    protected Double[][] allClusterFeatureRep;
    protected double[][] Cli;
    protected int maxIterations = 50;
    protected int m=2;

    protected double epsilon = 0.00000001;

    protected ProjectiveClustering result;
    
    protected ProjectiveClustering[] allResult;
    
    protected boolean executeOneDivided;

    //public EMlikeSubspaceEnsembles (ProjectiveClusteringDataset ensemble, SubspaceClusterObjectsSimilarity objectClusterSim, SubspaceClusterFeaturesSimilarity featureClusterSim)
    public EMlikePCE_EEMPCE_LATEST (ProjectiveClusteringDataset ensemble, boolean executeOneDivided)
    {
        this.offlineExecutionTime = 0;

        long start = System.currentTimeMillis();
        this.ensemble = ensemble;
        this.executeOneDivided = executeOneDivided;
        this.allClusterObjectRep = this.ensemble.getAllClusterByObjectRepresentationMatrix();
        this.allClusterFeatureRep = this.ensemble.getAllClusterByfeatureRepresentationMatrix();
        initialization();
        this.allResult = (this.executeOneDivided)?new ProjectiveClustering[2]:new ProjectiveClustering[1];
        long stop = System.currentTimeMillis();
        this.offlineExecutionTime += stop-start;
    }

    public ProjectiveClustering execute (int nClusters)
    {
        executeOneMinus(nClusters);
        
        if (this.executeOneDivided)
        {
            executeOneDivided(nClusters);
        }

        return this.result;
    }
    
    protected void executeOneMinus(int nClusters)
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


        //System.out.println("EMlikeSubspaceEnsembles_ObjectByFeatureMatrix_OPTIMIZED");
        while (currentIt <= this.maxIterations && !stop)
        {
            stop = true;
            double currentError = 0.0;

            //object assignment

            double[] Gk = new double[nClusters];
            for (int j=0; j<Gk.length; j++)
            {
                for (int l=0; l<objectSolution[j].length; l++)
                {
                    Gk[j] += objectSolution[j][l];
                    //Gk[j] += Math.pow(objectSolution[j][l],this.m);
                }
            }

            double[][] GkhPrime = new double[nClusters][this.allClusterObjectRep.length];
            for (int j=0; j<GkhPrime.length; j++)
            {
                for (int c=0; c<GkhPrime[j].length; c++)
                {
                    for (int l=0; l<objectSolution[0].length; l++)
                    {
                        GkhPrime[j][c] += objectSolution[j][l]*this.allClusterObjectRep[c][l];
                        //GkhPrime[j][c] += Math.pow(objectSolution[j][l],this.m)*this.allClusterObjectRep[c][l];
                    }
                }
            }

            double[][] oldObjectSolution = new double[objectSolution.length][objectSolution[0].length];
            for (int j=0; j<oldObjectSolution.length; j++)
            {
                for (int l=0; l<oldObjectSolution[j].length; l++)
                {
                    oldObjectSolution[j][l] = objectSolution[j][l].doubleValue();
                }
            }

            for (int l=0; l<objectSolution[0].length; l++)
            {
                double[] zjl = new double[objectSolution.length];
                int exactAssignment = -1;
                for (int j=0; j<objectSolution.length; j++)
                {
                    double term = 0.0;
                    for (int i=0; i<this.allClusterFeatureRep[0].length; i++)
                    {
                        term += Math.pow(featureSolution[j][i]-Cli[l][i]/this.ensemble.getDataLength(),2);
                    }
                    zjl[j] = term;
                    //normalization
                    zjl[j] /= 2;
                    if (zjl[j]<-0.000000001 || zjl[j]-1 > 0.000000001)
                    {
                        throw new RuntimeException("ERROR: zjl values must be within[0,1]---zjl["+j+"]="+zjl[j]);
                    }
                    
                    if (zjl[j] < 0.0)
                    {
                        zjl[j] = 0.0;
                    }
                    
                    
                    

                    //computing Xkn' terms
                    
                    
                    double tmp = 0.0;
                    for (int c=0; c<this.allClusterObjectRep.length; c++)
                    {
                        tmp += this.allClusterObjectRep[c][l]*(GkhPrime[j][c]-oldObjectSolution[j][l]*this.allClusterObjectRep[c][l]);
                    }
                    //double newTerm = Gk[j]-Math.pow(oldObjectSolution[j][l],this.m)-tmp/this.ensemble.getDataLength();
                    double newTerm = (objectSolution[0].length-1)-tmp/this.ensemble.getDataLength();
                    //double newTerm = ((double)1.0)/(1+tmp/this.ensemble.getDataLength());
                    
                    
                    
                    
                    
                    /*
                    double newTerm = 0.0;
                    for (int l1=0; l1<objectSolution[j].length; l1++)
                    {
                        if (l1 != l)
                        {
                            double tmp2 = 0.0;
                            for (int c=0; c<this.allClusterObjectRep.length; c++)
                            {
                                tmp2 += this.allClusterObjectRep[c][l]*this.allClusterObjectRep[c][l1];
                            }
                            newTerm += oldObjectSolution[j][l1]/(1+tmp2);
                        }
                    }
                    */
                    
                    
                    
                    /*
                    double newTerm = 0.0;
                    for (int l1=0; l1<objectSolution[j].length; l1++)
                    {
                        if (l1 != l)
                        {
                            double tmp = 0.0;
                            for (int c=0; c<this.allClusterObjectRep.length; c++)
                            {
                                tmp += this.allClusterObjectRep[c][l]*this.allClusterObjectRep[c][l1];
                            }
                            //newTerm += Math.pow(oldObjectSolution[j][l1],this.m)*tmp;
                            newTerm += 1-(oldObjectSolution[j][l1]*(tmp/this.ensemble.getDataLength()));
                        }
                    }
                    */
                    
                    
                    
                    /*
                    double newTerm = 0.0;
                    for (int l1=0; l1<objectSolution[0].length; l1++)
                    {
                        if (l1 != l)
                        {
                            double term1 = 0.0;
                            for(int c=0; c<this.allClusterObjectRep.length; c++)
                            {
                                term1 += Math.pow(this.allClusterObjectRep[c][l1]-this.allClusterObjectRep[c][l],2);
                            }
                            
                            if (term1 == 0.0)
                            {
                                throw new RuntimeException("INFINITYYYYYYYYYYYY!!!");
                            }
                            newTerm += Math.pow(oldObjectSolution[j][l1], this.m)/term1;
                        }                       
                    }
                     */ 
                        
                    
                    
                    if (newTerm < -0.0000000001)
                    {
                        throw new RuntimeException("ERROR: newTerm must be greater than or equal to zero---newTerm="+newTerm);
                    }
                    else if (newTerm < 0.0)
                    {
                        newTerm = 0.0;
                    }
                    //normalization
                    newTerm /= objectSolution[0].length-1;
                    //newTerm -= ((double)1.0)/objectSolution[0].length;
                    //newTerm /= ((double)1.0)-((double)1.0)/objectSolution[0].length;

                    
                    if (newTerm<-0.000000001 || newTerm-1 > 0.000000001)
                    {
                        throw new RuntimeException("ERROR: newTerm must be within [0,1]---newTerm="+newTerm);
                    }
                    
                    if (newTerm < 0.0)
                    {
                        newTerm = 0.0;
                    }
                    
                    
                    //newTerm = 0.0;
                    zjl[j] *= newTerm;

                    if (zjl[j] == 0.0)
                    {
                        exactAssignment = j;
                    }

                    currentError += zjl[j]*Math.pow(oldObjectSolution[j][l], this.m);
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
                    xj += Math.pow(objectSolution[j][l],this.m)*this.ensemble.getDataLength();
                }


                //N.B. if xj==0, then cluster j doen not have any object; so, the feature should have equal weight
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
                            yji += Math.pow(objectSolution[j][l],this.m)*this.Cli[l][i];
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

                        if (Math.abs(value2 + value3) > 0.000000001)
                        {
                            System.out.println("ACHTUNG-------------------"+Math.abs(value2 + value3));
                        }

                        double value = value1+value2+value3;

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

                    if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.999999999 || sumCheck > 1.000000001)
                    {
                        throw new RuntimeException ("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);
                    }
                }
            }
            
            //System.out.println("ERROR="+currentError);
            
            /*
            if (previousError-currentError < -0.1)
            {
                throw new RuntimeException("ERROR: previous error lower than current error---previous="+previousError+", current="+currentError);
            }
            */
            
            if (Math.abs(previousError - currentError) <= this.epsilon)
            {
                stop = true;
            }
            else
            {
                stop = false;
            }
            previousError = currentError;


            //System.out.print("it="+currentIt+"--");

            currentIt++;
        }

        //System.out.println("\n##########################################################");
        //System.out.println("EMlikeSubspaceEnsemble_ObjectByFeatureMatrix---NumberOfIterations="+(currentIt-1));
        //System.out.println("##########################################################\n");
        //System.out.println();

        ProjectiveCluster[] clusters = new ProjectiveCluster[nClusters];
        for (int j=0; j<clusters.length; j++)
        {
            clusters[j] = new ProjectiveCluster(this.ensemble.getInstances(), objectSolution[j], featureSolution[j], -(j+1), true, true);
        }

        this.result = new ProjectiveClustering(clusters, -1);

        long stopTime = System.currentTimeMillis();

        this.onlineExecutionTime = stopTime-startTime;

        //System.out.println("EMlike Subspace Clustering Ensembles: OnlineExecutionTime="+this.getOnlineExecutionTime());
        
        this.allResult[0] = this.result;
    }
    
    
    
    protected void executeOneDivided(int nClusters)
    {
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


        //System.out.println("EMlikeSubspaceEnsembles_ObjectByFeatureMatrix_OPTIMIZED");
        while (currentIt <= this.maxIterations && !stop)
        {
            stop = true;
            double currentError = 0.0;

            //object assignment

            double[] Gk = new double[nClusters];
            for (int j=0; j<Gk.length; j++)
            {
                for (int l=0; l<objectSolution[j].length; l++)
                {
                    Gk[j] += objectSolution[j][l];
                    //Gk[j] += Math.pow(objectSolution[j][l],this.m);
                }
            }

            double[][] GkhPrime = new double[nClusters][this.allClusterObjectRep.length];
            for (int j=0; j<GkhPrime.length; j++)
            {
                for (int c=0; c<GkhPrime[j].length; c++)
                {
                    for (int l=0; l<objectSolution[0].length; l++)
                    {
                        GkhPrime[j][c] += objectSolution[j][l]*this.allClusterObjectRep[c][l];
                        //GkhPrime[j][c] += Math.pow(objectSolution[j][l],this.m)*this.allClusterObjectRep[c][l];
                    }
                }
            }

            double[][] oldObjectSolution = new double[objectSolution.length][objectSolution[0].length];
            for (int j=0; j<oldObjectSolution.length; j++)
            {
                for (int l=0; l<oldObjectSolution[j].length; l++)
                {
                    oldObjectSolution[j][l] = objectSolution[j][l].doubleValue();
                }
            }

            for (int l=0; l<objectSolution[0].length; l++)
            {
                double[] zjl = new double[objectSolution.length];
                int exactAssignment = -1;
                for (int j=0; j<objectSolution.length; j++)
                {
                    double term = 0.0;
                    for (int i=0; i<this.allClusterFeatureRep[0].length; i++)
                    {
                        term += Math.pow(featureSolution[j][i]-Cli[l][i]/this.ensemble.getDataLength(),2);
                    }
                    zjl[j] = term;
                    //normalization
                    zjl[j] /= 2;
                    if (zjl[j]<-0.000000001 || zjl[j]-1 > 0.000000001)
                    {
                        throw new RuntimeException("ERROR: zjl values must be within[0,1]---zjl["+j+"]="+zjl[j]);
                    }
                    
                    if (zjl[j] < 0.0)
                    {
                        zjl[j] = 0.0;
                    }
                    
                    
                    

                    //computing Xkn' terms
                    
                    
                    double tmp = 0.0;
                    for (int c=0; c<this.allClusterObjectRep.length; c++)
                    {
                        tmp += this.allClusterObjectRep[c][l]*(GkhPrime[j][c]-oldObjectSolution[j][l]*this.allClusterObjectRep[c][l]);
                    }
                    //double newTerm = Gk[j]-Math.pow(oldObjectSolution[j][l],this.m)-tmp/this.ensemble.getDataLength();
                    //double newTerm = (objectSolution[0].length-1)-tmp/this.ensemble.getDataLength();
                    double newTerm = ((double)1.0)/(1+tmp/this.ensemble.getDataLength());
                    
                    
                    
                    
                    
                    /*
                    double newTerm = 0.0;
                    for (int l1=0; l1<objectSolution[j].length; l1++)
                    {
                        if (l1 != l)
                        {
                            double tmp2 = 0.0;
                            for (int c=0; c<this.allClusterObjectRep.length; c++)
                            {
                                tmp2 += this.allClusterObjectRep[c][l]*this.allClusterObjectRep[c][l1];
                            }
                            newTerm += oldObjectSolution[j][l1]/(1+tmp2);
                        }
                    }
                    */
                    
                    
                    
                    /*
                    double newTerm = 0.0;
                    for (int l1=0; l1<objectSolution[j].length; l1++)
                    {
                        if (l1 != l)
                        {
                            double tmp = 0.0;
                            for (int c=0; c<this.allClusterObjectRep.length; c++)
                            {
                                tmp += this.allClusterObjectRep[c][l]*this.allClusterObjectRep[c][l1];
                            }
                            //newTerm += Math.pow(oldObjectSolution[j][l1],this.m)*tmp;
                            newTerm += 1-(oldObjectSolution[j][l1]*(tmp/this.ensemble.getDataLength()));
                        }
                    }
                    */
                    
                    
                    
                    /*
                    double newTerm = 0.0;
                    for (int l1=0; l1<objectSolution[0].length; l1++)
                    {
                        if (l1 != l)
                        {
                            double term1 = 0.0;
                            for(int c=0; c<this.allClusterObjectRep.length; c++)
                            {
                                term1 += Math.pow(this.allClusterObjectRep[c][l1]-this.allClusterObjectRep[c][l],2);
                            }
                            
                            if (term1 == 0.0)
                            {
                                throw new RuntimeException("INFINITYYYYYYYYYYYY!!!");
                            }
                            newTerm += Math.pow(oldObjectSolution[j][l1], this.m)/term1;
                        }                       
                    }
                     */ 
                        
                    
                    
                    if (newTerm < -0.0000000001)
                    {
                        throw new RuntimeException("ERROR: newTerm must be greater than or equal to zero---newTerm="+newTerm);
                    }
                    else if (newTerm < 0.0)
                    {
                        newTerm = 0.0;
                    }
                    //normalization
                    //newTerm /= objectSolution[0].length-1;
                    newTerm -= ((double)1.0)/objectSolution[0].length;
                    newTerm /= ((double)1.0)-((double)1.0)/objectSolution[0].length;

                    
                    if (newTerm<-0.000000001 || newTerm-1 > 0.000000001)
                    {
                        throw new RuntimeException("ERROR: newTerm must be within [0,1]---newTerm="+newTerm);
                    }
                    
                    if (newTerm < 0.0)
                    {
                        newTerm = 0.0;
                    }
                    
                    
                    //newTerm = 0.0;
                    zjl[j] *= newTerm;

                    if (zjl[j] == 0.0)
                    {
                        exactAssignment = j;
                    }

                    currentError += zjl[j]*Math.pow(oldObjectSolution[j][l], this.m);
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
                    xj += Math.pow(objectSolution[j][l],this.m)*this.ensemble.getDataLength();
                }


                //N.B. if xj==0, then cluster j doen not have any object; so, the feature should have equal weight
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
                            yji += Math.pow(objectSolution[j][l],this.m)*this.Cli[l][i];
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

                        if (Math.abs(value2 + value3) > 0.000000001)
                        {
                            System.out.println("ACHTUNG-------------------"+Math.abs(value2 + value3));
                        }

                        double value = value1+value2+value3;

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

                    if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.999999999 || sumCheck > 1.000000001)
                    {
                        throw new RuntimeException ("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);
                    }
                }
            }
            
            //System.out.println("ERROR="+currentError);
            
            /*
            if (previousError-currentError < -0.1)
            {
                throw new RuntimeException("ERROR: previous error lower than current error---previous="+previousError+", current="+currentError);
            }
            */
            
            if (Math.abs(previousError - currentError) <= this.epsilon)
            {
                stop = true;
            }
            else
            {
                stop = false;
            }
            previousError = currentError;

            currentIt++;
        }


        ProjectiveCluster[] clusters = new ProjectiveCluster[nClusters];
        for (int j=0; j<clusters.length; j++)
        {
            clusters[j] = new ProjectiveCluster(this.ensemble.getInstances(), objectSolution[j], featureSolution[j], -(j+1), true, true);
        }

        ProjectiveClustering res = new ProjectiveClustering(clusters, -1);

        this.allResult[1] = res;
    }

    public ProjectiveClustering[] getAllResults()
    {
        return this.allResult;
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

    protected void initialization()
    {
        this.Cli = new double[this.ensemble.getNumberOfInstances()][this.ensemble.getNumberOfFeaturesInEachCluster()];

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

    public void setAlpha(int alpha) 
    {
        if (alpha <= 1)
        {
            throw new RuntimeException("ERROR: beta must be greater than 1");
        }
        
        this.m = alpha;
    }

    public void setBeta(int beta) 
    {

    }

}



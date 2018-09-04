package pce.enhancedsingleobjective;

import dataset.ProjectiveClusteringDataset;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.PCEMethod;
import pce.singleobjective.EMlikeMethod;


public class EMlikePCE_EEMPCE extends PCEMethod implements EMlikeMethod
{
    protected Double[][] allClusterObjectRep;
    protected Double[][] allClusterFeatureRep;
    protected double[] Bl;
    protected double[][] Cli;
    protected int maxIterations = 50;
    protected int m=2;

    protected double epsilon = 0.00000001;

    protected ProjectiveClustering result;

    //public EMlikeSubspaceEnsembles (ProjectiveClusteringDataset ensemble, SubspaceClusterObjectsSimilarity objectClusterSim, SubspaceClusterFeaturesSimilarity featureClusterSim)
    public EMlikePCE_EEMPCE (ProjectiveClusteringDataset ensemble)
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
            
            double[] term1 = new double[objectSolution.length];
            for (int j=0; j<term1.length; j++)
            {
                double t = 0.0;
                for (int i=0; i<featureSolution[j].length; i++)
                {
                    t += featureSolution[j][i]*featureSolution[j][i];
                }
                t *= this.ensemble.getDataLength();
                term1[j] = t;
            } 

            for (int l=0; l<objectSolution[0].length; l++)
            {
                double[] zjl = new double[objectSolution.length];
                int exactAssignment = -1;
                for (int j=0; j<objectSolution.length; j++)
                {
                    double term2 = this.Bl[l];
                    double term3 = 0.0;

                    for (int i=0; i<this.allClusterFeatureRep[0].length; i++)
                    {
                        term3 += featureSolution[j][i]*this.Cli[l][i];   
                    }
                    

                    zjl[j] = term1[j]+term2-2*term3;
                    //normalization
                    zjl[j] /= 2*this.ensemble.getDataLength();
                    if (zjl[j]-1 > 0.000000001)
                    {
                        throw new RuntimeException("ERROR: zjl values must be lower than or equal to 1---zjl["+j+"]="+zjl[j]);
                    }

                    //computing Xkn' terms
                    double tmp = 0.0;
                    for (int c=0; c<this.allClusterObjectRep.length; c++)
                    {
                        tmp += this.allClusterObjectRep[c][l]*(GkhPrime[j][c]-oldObjectSolution[j][l]*this.allClusterObjectRep[c][l]);
                    }
                    double newTerm = Gk[j]-oldObjectSolution[j][l]-tmp/this.ensemble.getDataLength();
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
                    if (newTerm-1 > 0.000000001)
                    {
                        throw new RuntimeException("ERROR: newTerm must be lower than or equal to 1---newTerm="+newTerm);
                    }
                    
                    zjl[j] += newTerm;

                    if (zjl[j] == 0.0)
                    {
                        exactAssignment = j;
                    }

                    currentError += zjl[j]*Math.pow(objectSolution[j][l], this.m);
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
            
            if (previousError-currentError < -0.1)
            {
                throw new RuntimeException("ERROR: previous error lower than current error---previous="+previousError+", current="+currentError);
            }

            if (previousError - currentError <= this.epsilon)
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

        ProjectiveCluster[] clusters = new ProjectiveCluster[nClusters];
        for (int j=0; j<clusters.length; j++)
        {
            clusters[j] = new ProjectiveCluster(this.ensemble.getInstances(), objectSolution[j], featureSolution[j], -(j+1), true, true);
        }

        this.result = new ProjectiveClustering(clusters, -1);

        long stopTime = System.currentTimeMillis();

        this.onlineExecutionTime = stopTime-startTime;

        System.out.println("EMlike Subspace Clustering Ensembles: OnlineExecutionTime="+this.getOnlineExecutionTime());

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

    protected void initialization()
    {
        this.Bl = new double[this.ensemble.getNumberOfInstances()];
        this.Cli = new double[this.ensemble.getNumberOfInstances()][this.ensemble.getNumberOfFeaturesInEachCluster()];

        for (int l=0; l<this.Bl.length; l++)
        {
            for (int c=0; c<this.allClusterObjectRep.length; c++)
            {
                for (int i=0; i<this.allClusterFeatureRep[c].length; i++)
                {
                    this.Bl[l] += this.allClusterObjectRep[c][l]*this.allClusterFeatureRep[c][i]*this.allClusterFeatureRep[c][i];
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


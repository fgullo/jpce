/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce.singleobjective;

import dataset.ProjectiveClusteringDataset;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.PCEMethod;


public class EMlikePCE_HYBRID_HARD extends PCEMethod implements EMlikeMethod
{
    protected Double[][] allClusterObjectRep;
    protected Double[][] allClusterFeatureRep;
    protected double[][] Cli;
    protected int maxIterations = 50;
    protected int m=2;

    protected double epsilon = 0.00000001;

    protected ProjectiveClustering result;

    //public EMlikeSubspaceEnsembles (ProjectiveClusteringDataset ensemble, SubspaceClusterObjectsSimilarity objectClusterSim, SubspaceClusterFeaturesSimilarity featureClusterSim)
    public EMlikePCE_HYBRID_HARD (ProjectiveClusteringDataset ensemble)
    {
        this.offlineExecutionTime = 0;

        long start = System.currentTimeMillis();
        this.ensemble = ensemble;
        this.allClusterObjectRep = this.ensemble.getAllClusterByObjectRepresentationMatrix();
        this.allClusterFeatureRep = this.ensemble.getAllClusterByfeatureRepresentationMatrix();
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
        Double[][] clusterSolution = new Double[nClusters][this.ensemble.getNumberOfAllClusters()];
        double[] kj = new double[nClusters];

        //uniformInizialization(objectSolution, featureSolution, kj);
        randomInizialization(objectSolution, featureSolution, clusterSolution, kj, nClusters);

        int currentIt=1;
        boolean stop = false;


        System.out.println("EMlikeSubspaceEnsembles_HYBRID");
        while (currentIt <= this.maxIterations && !stop)
        {
            stop = true;
            double currentError = 0.0;



            //cluster assignment
            for (int c=0; c<clusterSolution[0].length; c++)
            {
                double[] Ajc = new double[clusterSolution.length];
                int exactAssignment = -1;
                for (int j=0; j<clusterSolution.length; j++)
                {

                    double term = 0.0;
                    for (int l=0; l<objectSolution[j].length; l++)
                    {
                        term += Math.pow(objectSolution[j][l],this.m)*(1-this.allClusterObjectRep[c][l]);
                    }
                    for (int i=0; i<featureSolution[j].length; i++)
                    {
                        term += Math.pow(featureSolution[j][i],this.m)*(1-this.allClusterFeatureRep[c][i]);
                    }
                    Ajc[j] = term;

                    if (Ajc[j] == 0.0)
                    {
                        exactAssignment = j;
                    }

                    currentError += Ajc[j]*clusterSolution[j][c];
                }

                if (exactAssignment != -1)
                {
                    double newAssignment = 1.0;
                    if (Math.abs(newAssignment-clusterSolution[exactAssignment][c]) > 0.00000001)
                    {
                        stop = false;
                    }

                    for (int j=0; j<clusterSolution.length; j++)
                    {
                        clusterSolution[j][c] = 0.0;
                    }
                    clusterSolution[exactAssignment][c] = new Double(1.0);
                }
                else
                {
                    double min = Double.POSITIVE_INFINITY;
                    int iMin = -1;
                    for (int j=0; j<Ajc.length; j++)
                    {
                        if (Ajc[j]<min)
                        {
                            min = Ajc[j];
                            iMin = j;
                        }
                    }

                    for (int j=0; j<clusterSolution.length; j++)
                    {
                        double value = 0.0;
                        if (j == iMin)
                        {
                            value = 1.0;
                        }

                        if (clusterSolution[j][c] != value)
                        {
                            stop = false;
                        }

                        clusterSolution[j][c] = value;
                    }
                }
            }


            //object assignment
            for (int l=0; l<objectSolution[0].length; l++)
            {
                double[] zjl = new double[objectSolution.length];
                int exactAssignment = -1;
                for (int j=0; j<objectSolution.length; j++)
                {

                    double term = 0.0;
                    for (int c=0; c<clusterSolution[j].length; c++)
                    {
                        term += clusterSolution[j][c]*(1-this.allClusterObjectRep[c][l]);
                    }
                    zjl[j] = term;

                    if (zjl[j] == 0.0)
                    {
                        exactAssignment = j;
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

                double[] Aji = new double[featureSolution[j].length];
                int exactAssignments = 0;

                for (int i=0; i<featureSolution[j].length; i++)
                {
                    double termNew = 0.0;

                    for (int c=0; c<this.allClusterFeatureRep[j].length; c++)
                    {
                        termNew += clusterSolution[j][c]*(1-this.allClusterFeatureRep[c][i]);
                    }
                    Aji[i] = termNew;

                    if (Aji[i] == 0)
                    {
                        exactAssignments++;
                    }
                }



                if (exactAssignments > 0)
                {
                    for (int i=0; i<featureSolution[j].length; i++)
                    {
                        if (Aji[i] == 0)
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
                else
                {
                    double sum = 0.0;
                    for (int i=0; i<Aji.length; i++)
                    {
                        if (Aji[i] > 0.0)
                        {
                            sum += Math.pow(((double)1.0)/Aji[i],((double)1.0)/(this.m-1));
                        }
                    }

                    double sumCheck = 0.0;
                    for (int i=0; i<featureSolution[j].length; i++)
                    {
                        double value1 = Math.pow(Aji[i], ((double)1.0)/(this.m-1))*sum;
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
                    if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.999999999 || sumCheck > 1.000000001)
                    {
                        throw new RuntimeException ("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);
                    }
                }
            }

            /*
            if (previousError-currentError < -0.1)
            {
                throw new RuntimeException("ERROR: previous error lower than current error---previous="+previousError+", current="+currentError);
            }
            */


            System.out.println("ERROR="+currentError);

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

    protected void randomInizialization(Double[][] objectSolution, Double[][] featureSolution, Double[][] clusterSolution, double[] kj, int nClusters)
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


        for (int j=0; j<clusterSolution[0].length; j++)
        {
            double max = Double.NEGATIVE_INFINITY;
            int iMax = -1;
            for (int z=0; z<clusterSolution.length; z++)
            {
                clusterSolution[z][j] = Math.random();
                if (clusterSolution[z][j] > max)
                {
                    max = clusterSolution[z][j];
                    iMax = z;
                }
            }

            for (int z=0; z<clusterSolution.length; z++)
            {
                if (z == iMax)
                {
                    clusterSolution[z][j] = 1.0;
                }
                else
                {
                    clusterSolution[z][j] = 0.0;
                }
            }
        }
    }


    public void setM(int m)
    {
        this.m = m;
    }

}


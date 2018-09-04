package pce.enhancedtwoobjective;

import clustering.AHCAverageLinkage;
import clustering.FuzzyKMedoids;
import clustering.KMedoids;
import dataset.ProjectiveClusterDataset;
import dataset.ProjectiveClusteringDataset;
import evaluation.cluster.features.ProjectiveClusterFeaturesSimilarity;
import evaluation.cluster.objectsfeatures.ProjectiveClusterObjectsFeaturesSimilarity;
import evaluation.cluster.objects.ProjectiveClusterObjectsSimilarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.AlphaBetaPCEMethod;
import pce.ClusterBasedPCEMethod;


public class OneObjectiveClusterBasedPCE_CBPCE extends ClusterBasedPCEMethod implements AlphaBetaPCEMethod
{
    public static final int STANDARD = 1;
    public static final int STANDARD_OBJ_CENTROID_FEAT = 2;
    public static final int STANDARD_OBJ_PROB_FEAT = 3;
    public static final int PROB_OBJ_PROB_FEAT = 4;
    public static final int PROB_OBJ_CENTROID_FEAT = 5;
    
    protected double[][] clusterObjectsFeaturesDistMatrix;
    
    protected ProjectiveClusterObjectsFeaturesSimilarity objectFeatureClusterSim;
    
    protected ProjectiveClustering result;
    
    protected int alpha;
    protected int beta;
    
    
    protected int version;
    
    
    public OneObjectiveClusterBasedPCE_CBPCE()
    {
        
    }
    
    public OneObjectiveClusterBasedPCE_CBPCE (ProjectiveClusteringDataset ensemble, ProjectiveClusterObjectsFeaturesSimilarity objectFeatureClusterSim, int alpha, int beta, boolean featureCentroid)
    {
        this.ensemble = ensemble;
        this.objectFeatureClusterSim = objectFeatureClusterSim;
        
        if (alpha <= 1)
        {
            throw new RuntimeException("ERROR: alpha must be greater than 1");
        }
        this.alpha = alpha;

        if (beta <= 1)
        {
            throw new RuntimeException("ERROR: beta must be greater than 1");
        }
        this.beta = beta;

        if (featureCentroid)
        {
            this.version = OneObjectiveClusterBasedPCE_CBPCE.STANDARD_OBJ_CENTROID_FEAT;
        }
        else
        {
            this.version = OneObjectiveClusterBasedPCE_CBPCE.STANDARD;
        }
        
        //System.out.println("CB-PCE---Starting computing pair-wise distances matrix");
        //long startTime = System.currentTimeMillis();
        this.clusterObjectsFeaturesDistMatrix = this.ensemble.getClusterObjectsFeaturesDistMatrix(this.objectFeatureClusterSim);
        //this.clusterObjectsFeaturesDistMatrix = this.ensemble.getClusterObjectsFeaturesSimMatrix(this.objectFeatureClusterSim);
        //long stopTime = System.currentTimeMillis();
        //System.out.println("CB-PCE---Computing pair-wise distances matrix done");

        //this.offlineExecutionTime = stopTime-startTime;
        this.offlineExecutionTime = this.ensemble.getClusterObjectsFeaturesDistMatrixTime();
    }
    
    
    public OneObjectiveClusterBasedPCE_CBPCE (ProjectiveClusterObjectsFeaturesSimilarity objectFeatureClusterSim, int alpha, int beta)
    {
        
    }
    
    public ProjectiveClustering execute (int nClusters)
    {
        long startTime = System.currentTimeMillis();
        
        Clustering kmResult = buildMetaClusters(nClusters);
        //this.result = assignObjectsAndFeatures(ahcResult);
        this.result = assignObjectsAndFeatures(kmResult);

        long stopTime = System.currentTimeMillis();
        this.onlineExecutionTime = stopTime-startTime;

        return this.result;
    }
    
    protected Clustering buildMetaClusters(int nClusters) 
    {
        ProjectiveClusterDataset projectiveClusterDataset = new ProjectiveClusterDataset(ensemble.getClusters(),null);
        KMedoids km = new KMedoids(projectiveClusterDataset);
        Clustering kmResult = km.execute(clusterObjectsFeaturesDistMatrix, nClusters);
        
        //AHCAverageLinkage ahc = new AHCAverageLinkage(projectiveClusterDataset,false);
        //Clustering ahcResult = ahc.execute(clusterObjectsFeaturesDistMatrix, nClusters);
        
        //FuzzyKMedoids km = new FuzzyKMedoids(projectiveClusterDataset);
        //Clustering kmResult = km.execute(clusterObjectsFeaturesDistMatrix, nClusters).hardPartition();
        
        return kmResult;
    }

    
    
    protected ProjectiveClustering assignObjectsAndFeatures(Clustering clustering)
    {
        Cluster[] metaClusters = clustering.getClusters();
        
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
        
        
        //object assignments
        if (this.version == OneObjectiveClusterBasedPCE_CBPCE.PROB_OBJ_PROB_FEAT || this.version == OneObjectiveClusterBasedPCE_CBPCE.PROB_OBJ_CENTROID_FEAT)
        {
            objectAssignmentsProbability(objectAssignments,metaClusters,ensemble.size());
        }
        else
        {
            for (int k=0; k<objectAssignments.length; k++)
            {
                for (int n=0; n<objectAssignments[k].length; n++)
                {
                    double Wkn = 0.0;
                    Instance[] clusters = metaClusters[k].getInstances();
                    for (int h=0; h<clusters.length; h++)
                    {
                        Double[] orep = ((ProjectiveCluster)clusters[h]).getFeatureVectorRepresentationDouble();
                        Wkn += 1-orep[n];
                    }

                    if (clusters.length > 0)
                    {
                        Wkn /= clusters.length;
                    }

                    objectAssignments[k][n] = Wkn;

                }
            }

            for (int n=0; n<objectAssignments[0].length; n++)
            {
                int zeros = 0;
                int countZeros = 0;
                for (int k=0; k<objectAssignments.length; k++)
                {
                    if (objectAssignments[k][n] == 0.0 && metaClusters[k].getInstances().length != 0)
                    {
                        zeros += metaClusters[k].getInstances().length;
                        countZeros++;
                    }        
                }

                if (zeros > 0)
                {
                    //System.out.println("\n\nZEROS = "+countZeros+"\n\n");
                    for (int k=0; k<objectAssignments.length; k++)
                    {
                        if (objectAssignments[k][n] == 0.0 && metaClusters[k].getInstances().length != 0)
                        {
                            objectAssignments[k][n] = ((double)metaClusters[k].getInstances().length)/zeros;
                        }
                        else
                        {
                            objectAssignments[k][n] = 0.0;
                        }
                    }                
                }
                else
                {
                    double sum = 0.0;
                    for (int k=0; k<objectAssignments.length; k++)
                    {
                        if (objectAssignments[k][n] != 0)
                        {
                            sum += Math.pow(((double)1.0)/objectAssignments[k][n],((double)1.0)/(this.alpha-1));
                        }        
                    }

                    if (sum > 0.0)
                    {
                        sum = ((double)1.0)/sum;
                    }
                    else
                    {
                        throw new RuntimeException("ERROR: sum must be greater than zero");
                    }

                    for (int k=0; k<objectAssignments.length; k++)
                    {
                        if (objectAssignments[k][n] != 0)
                        {
                            double value = Math.pow(((double)1.0)/objectAssignments[k][n],((double)1.0)/(this.alpha-1))*sum;
                            objectAssignments[k][n] = value; 
                        }        
                    }              
                }
            }
        }
            
            
        //feature assignments
        if (this.version == OneObjectiveClusterBasedPCE_CBPCE.STANDARD_OBJ_CENTROID_FEAT || this.version == OneObjectiveClusterBasedPCE_CBPCE.PROB_OBJ_CENTROID_FEAT)
        {
            featureAssignmentsCentroid(featureAssignments, metaClusters);
        }
        else if (this.version == OneObjectiveClusterBasedPCE_CBPCE.STANDARD_OBJ_PROB_FEAT)
        {
            featureAssignmentsProbability(featureAssignments, metaClusters);
        }
        else if (this.version == OneObjectiveClusterBasedPCE_CBPCE.STANDARD)
        {
            for (int k=0; k<featureAssignments.length; k++)
            {
                if (metaClusters[k].getInstances().length == 0)
                {
                    for (int d=0; d<featureAssignments[k].length; d++)
                    {
                        featureAssignments[k][d] = ((double)1.0)/featureAssignments[k].length;
                    }
                }
                else
                {
                    for (int d=0; d<featureAssignments[k].length; d++)
                    {
                        double WkdPrime = 0.0;
                        Instance[] clusters = metaClusters[k].getInstances();
                        for (int h=0; h<clusters.length; h++)
                        {
                            Double[] frep = ((ProjectiveCluster)clusters[h]).getFeatureToClusterAssignments();
                            WkdPrime += 1-frep[d];
                        }

                        if (clusters.length > 0)
                        {
                            WkdPrime /= clusters.length;
                        }

                        featureAssignments[k][d] = WkdPrime;
                    }
                }
            }

            for (int k=0; k<featureAssignments.length; k++)
            {
                if (metaClusters[k].getInstances().length != 0)
                {
                    int zeros = 0;
                    for (int d=0; d<featureAssignments[k].length; d++)
                    {
                        if (featureAssignments[k][d] == 0.0)
                        {
                            if (zeros > 0.0)
                            {
                                throw new RuntimeException("ERROR: no more than one feature may have feature assignment equal to one");
                            }
                            else
                            {
                                zeros++;
                            }
                        }
                    }

                    if (zeros > 0)
                    {
                        for (int d=0; d<featureAssignments[k].length; d++)
                        {
                            if (featureAssignments[k][d] == 0.0)
                            {
                                featureAssignments[k][d] = 1.0;
                            }
                            else
                            {
                                featureAssignments[k][d] = 0.0;
                            }
                        }                  
                    }
                    else
                    {
                        double sum = 0.0;
                        for (int d=0; d<featureAssignments[k].length; d++)
                        {
                            if (featureAssignments[k][d] == 0.0)
                            {
                                throw new RuntimeException("ERROR: feature assignment cannot be zero");
                            }

                            sum += Math.pow(((double)1.0)/featureAssignments[k][d],((double)1.0)/(this.beta-1));
                        }

                        if (sum > 0.0)
                        {
                            sum = ((double)1.0)/sum;
                        }
                        else
                        {
                            throw new RuntimeException("ERROR: sum must be greater than zero");
                        }

                        for (int d=0; d<featureAssignments[k].length; d++)
                        {
                            double value = Math.pow(((double)1.0)/featureAssignments[k][d],((double)1.0)/(this.beta-1))*sum;
                            featureAssignments[k][d] = value;
                        }                    
                    }

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
    
    protected void featureAssignmentsCentroid(Double[][] featureAssignments, Cluster[] metaClusters)
    {
        for (int k=0; k<featureAssignments.length; k++)
        {
            if (metaClusters[k].getInstances().length == 0)
            {
                for (int d=0; d<featureAssignments[k].length; d++)
                {
                    featureAssignments[k][d] = ((double)1.0)/featureAssignments[k].length;
                }
            }
            else
            { 
                for (int d=0; d<featureAssignments[k].length; d++)
                {
                    Instance[] clusters = metaClusters[k].getInstances();
                    for (int h=0; h<clusters.length; h++)
                    {
                        Double[] frep = ((ProjectiveCluster)clusters[h]).getFeatureToClusterAssignments();
                        featureAssignments[k][d] += frep[d];
                    }
                    
                    featureAssignments[k][d] /= clusters.length;
                }
            }
        }       
    }
    
    protected void featureAssignmentsProbability(Double[][] featureAssignments, Cluster[] metaClusters)
    {
        int H = 0;
        for (int i=0; i<metaClusters.length; i++)
        {
            H += metaClusters[i].getInstances().length;
        }
        
        for (int k=0; k<featureAssignments.length; k++)
        {
            if (metaClusters[k].getInstances().length == 0)
            {
                for (int d=0; d<featureAssignments[k].length; d++)
                {
                    featureAssignments[k][d] = ((double)1.0)/featureAssignments[k].length;
                }
            }
            else
            {
                for (int d=0; d<featureAssignments[k].length; d++)
                {
                    double WkdPrime = 0.0;
                    Instance[] clusters = metaClusters[k].getInstances();
                    for (int h=0; h<clusters.length; h++)
                    {
                        Double[] frep = ((ProjectiveCluster)clusters[h]).getFeatureToClusterAssignments();
                        WkdPrime += frep[d]/H;
                    }

                    WkdPrime = 1-WkdPrime;

                    featureAssignments[k][d] = WkdPrime;
                }
            }
        }

        for (int k=0; k<featureAssignments.length; k++)
        {
            if (metaClusters[k].getInstances().length != 0)
            {
                int zeros = 0;
                for (int d=0; d<featureAssignments[k].length; d++)
                {
                    if (featureAssignments[k][d] == 0.0)
                    {
                        if (zeros > 0.0)
                        {
                            throw new RuntimeException("ERROR: no more than one feature may have feature assignemnt equal to one");
                        }
                        else
                        {
                            zeros++;
                        }
                    }
                }

                if (zeros > 0)
                {
                    for (int d=0; d<featureAssignments[k].length; d++)
                    {
                        if (featureAssignments[k][d] == 0.0)
                        {
                            featureAssignments[k][d] = 1.0;
                        }
                        else
                        {
                            featureAssignments[k][d] = 0.0;
                        }
                    }                  
                }
                else
                {
                    double sum = 0.0;
                    for (int d=0; d<featureAssignments[k].length; d++)
                    {
                        if (featureAssignments[k][d] == 0.0)
                        {
                            throw new RuntimeException("ERROR: feature assignment cannot be zero");
                        }

                        sum += Math.pow(((double)1.0)/featureAssignments[k][d],((double)1.0)/(this.alpha-1));
                    }

                    if (sum > 0.0)
                    {
                        sum = ((double)1.0)/sum;
                    }
                    else
                    {
                        throw new RuntimeException("ERROR: sum must be greater than zero");
                    }

                    for (int d=0; d<featureAssignments[k].length; d++)
                    {
                        double value = Math.pow(((double)1.0)/featureAssignments[k][d],((double)1.0)/(this.alpha-1))*sum;
                        featureAssignments[k][d] = value;
                    }                    
                }

            }
        }
    }
    
    protected void objectAssignmentsProbability(Double[][] objectAssignments, Cluster[] metaClusters, int M)
    {
        for (int k=0; k<objectAssignments.length; k++)
        {
            for (int n=0; n<objectAssignments[k].length; n++)
            {
                double Wkn = 0.0;
                Instance[] clusters = metaClusters[k].getInstances();
                for (int h=0; h<clusters.length; h++)
                {
                    Double[] orep = ((ProjectiveCluster)clusters[h]).getFeatureVectorRepresentationDouble();
                    Wkn += orep[n]/M;
                }

                Wkn = 1-Wkn;
                if (clusters.length > 0)
                {
                    Wkn /= clusters.length;
                }

                objectAssignments[k][n] = Wkn;

            }
        }

        for (int n=0; n<objectAssignments[0].length; n++)
        {
            int zeros = 0;
            int countZeros = 0;
            for (int k=0; k<objectAssignments.length; k++)
            {
                if (objectAssignments[k][n] == 0.0 && metaClusters[k].getInstances().length != 0)
                {
                    zeros += metaClusters[k].getInstances().length;
                    countZeros++;
                }        
            }

            if (zeros > 0)
            {
                //System.out.println("\n\nZEROS = "+countZeros+"\n\n");
                for (int k=0; k<objectAssignments.length; k++)
                {
                    if (objectAssignments[k][n] == 0.0 && metaClusters[k].getInstances().length != 0)
                    {
                        objectAssignments[k][n] = ((double)metaClusters[k].getInstances().length)/zeros;
                    }
                    else
                    {
                        objectAssignments[k][n] = 0.0;
                    }
                }                
            }
            else
            {
                double sum = 0.0;
                for (int k=0; k<objectAssignments.length; k++)
                {
                    if (objectAssignments[k][n] != 0)
                    {
                        sum += Math.pow(((double)1.0)/objectAssignments[k][n],((double)1.0)/(this.alpha-1));
                    }        
                }

                if (sum > 0.0)
                {
                    sum = ((double)1.0)/sum;
                }
                else
                {
                    throw new RuntimeException("ERROR: sum must be greater than zero");
                }

                for (int k=0; k<objectAssignments.length; k++)
                {
                    if (objectAssignments[k][n] != 0)
                    {
                        double value = Math.pow(((double)1.0)/objectAssignments[k][n],((double)1.0)/(this.alpha-1))*sum;
                        objectAssignments[k][n] = value; 
                    }        
                }              
            }
        }
    }
    
    
    public ProjectiveClusterObjectsFeaturesSimilarity getObjectClusterSim()
    {
        return this.objectFeatureClusterSim;
    }
    
    public void setObjectFeatureClusterSim (ProjectiveClusterObjectsFeaturesSimilarity objectFeatureClusterSim)
    {
        this.objectFeatureClusterSim = objectFeatureClusterSim;
    }
    
    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }
    
    public int getAlpha()
    {
        return this.alpha;
    }
    
    public void setAlpha(int alpha)
    {
        if (alpha <= 1)
        {
            throw new RuntimeException("ERROR: alpha must be greater than 1");
        }
        
        this.alpha = alpha;
    }

    public int getBeta()
    {
        return this.beta;
    }

    public void setBeta(int beta)
    {
        if (beta <= 1)
        {
            throw new RuntimeException("ERROR: beta must be greater than 1");
        }

        this.beta = beta;
    }


}



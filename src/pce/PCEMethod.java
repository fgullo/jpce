package pce;

import dataset.ProjectiveClusteringDataset;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;


public abstract class PCEMethod {

    protected ProjectiveClusteringDataset ensemble;
    protected long onlineExecutionTime;
    protected long offlineExecutionTime;

    public ProjectiveClusteringDataset getEnsemble ()
    {
        return ensemble;
    }

    public ProjectiveClustering execute ()
    {
        ProjectiveClustering[] partitions = (ProjectiveClustering[])ensemble.getData();
        double mean = 0.0;
        for (int i=0; i<partitions.length; i++)
        {
            mean+=partitions[i].getNumberOfClusters();
        }
        mean/=partitions.length;
        
        int n = (int)Math.rint(mean);
        
        return execute(n);
    }
    
    protected ProjectiveClustering[] randomSelectionFromEnsemble(int size)
    {
        if (size > this.ensemble.getDataLength())
        {
            throw new RuntimeException("ERROR: size must be less than or equal to the size of the ensemble!");
        }
        
        ProjectiveClustering[] random = new ProjectiveClustering[this.ensemble.getDataLength()];
        for (int i=0; i<random.length; i++)
        {
            random[i] = cloneSolution((ProjectiveClustering)this.ensemble.getData()[i]);
        }
        if (size == this.ensemble.getDataLength())
        {
            return random;
        }
        
        ProjectiveClustering[] random2 = new ProjectiveClustering[size];
        boolean[] chosen = new boolean[random.length];
        for (int i=0; i<random2.length; i++)
        {
            int rnd = (int)Math.rint(Math.random()*(random.length-1));
            if (!chosen[rnd])
            {
                chosen[rnd] = true;
                random2[i] = random[rnd];
            }
            else
            {
                int j=rnd+1;
                while (j<random.length && chosen[j])
                {
                    j++;
                }
                
                if (j<random.length)
                {
                    chosen[j] = true;
                    random2[i] = random[j];
                }
                else
                {
                    j=0;
                    while (j<random.length && chosen[j])
                    {
                        j++;
                    }
                    
                    chosen[j] = true;
                    random2[i] = random[j];
                }
            }
        }
        
        
        return random2;
    }
    
    protected ProjectiveClustering cloneSolution(ProjectiveClustering sc)
    {
        ProjectiveCluster[] newClusters = new ProjectiveCluster[sc.getNumberOfClusters()];
        for (int i=0; i<newClusters.length; i++)
        {
            Object[] scObjects = sc.getClusters()[i].getFeatureVectorRepresentation();
            Double[] newObjects = new Double[scObjects.length];
            for (int j=0; j<newObjects.length; j++)
            {
                newObjects[j] = new Double(((Double)scObjects[j]).doubleValue());
            }
            
            Double[] scFeatures = sc.getClusters()[i].getFeatureToClusterAssignments();
            Double[] newFeatures = new Double[scFeatures.length];
            for (int j=0; j<newFeatures.length; j++)
            {
                newFeatures[j] = new Double(scFeatures[j].doubleValue());
            }
            
            newClusters[i] = new ProjectiveCluster(sc.getInstances(), newObjects, newFeatures, sc.getClusters()[i].getID(), sc.getClusters()[i].getFuzzyObjectsAssignment(), sc.getClusters()[i].getFuzzyFeaturesAssignment());
        }
        
        return new ProjectiveClustering(newClusters, sc.getID());
    }
    
    /*
    public Clustering weightedExecute (WeightingScheme ws) 
    {
        Clustering[] partitions = (Clustering[])ensemble.getData();
        double mean = 0.0;
        for (int i=0; i<partitions.length; i++)
        {
            mean+=partitions[i].getNumberOfClusters();
        }
        mean/=partitions.length;
        
        int n = (int)Math.rint(mean);
        
        return weightedExecute(n,ws);
    }
    */ 

    public abstract ProjectiveClustering execute (int nClusters);
    
    public abstract ProjectiveClustering[] getAllResults();

    public long getOnlineExecutionTime()
    {
        return this.onlineExecutionTime;
    }

    public long getOfflineExecutionTime()
    {
        return this.offlineExecutionTime;
    }
    
    public void setOfflineExecutionTime(long time)
    {
        this.offlineExecutionTime = time;
    }

    //public abstract Clustering weightedExecute (int nClusters, WeightingScheme ws);    
}

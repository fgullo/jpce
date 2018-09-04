package clusteringensembles;

import weighting.WeightingScheme;
import dataset.ClusteringDataset;
import objects.Clustering;


public abstract class CEMethod {

    protected ClusteringDataset ensemble;

    public ClusteringDataset getEnsemble () 
    {
        return ensemble;
    }

    public Clustering execute () 
    {
        Clustering[] partitions = (Clustering[])ensemble.getData();
        double mean = 0.0;
        for (int i=0; i<partitions.length; i++)
        {
            mean+=partitions[i].getNumberOfClusters();
        }
        mean/=partitions.length;
        
        int n = (int)Math.rint(mean);
        
        return execute(n);
    }
    
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

    public abstract Clustering execute (int nClusters);
    
    public abstract Clustering weightedExecute (int nClusters, WeightingScheme ws);    
}


package clusteringensembles;

import dataset.ClusterDataset;
import dataset.ClusteringDataset;
import dataset.Dataset;
import dataset.NumericalInstanceDataset;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;


public abstract class HybridCEMethod extends CEMethod
{
    
    protected Dataset buildHybridDataset(Instance[] instances, int totInstances, int totClusters)
    {
        Instance[] dataset = new Instance[totInstances+totClusters];
        Dataset d = null;
        for (int i=0; i<instances.length; i++)
        {
            dataset[i] = instances[i];
        }
        
        if (dataset[0] instanceof NumericalInstance)
        {
            for (int i=totInstances; i<dataset.length; i++)
            {
                //sham instances
                dataset[i] = new NumericalInstance(((NumericalInstance)dataset[0]).getDataVector(),i);
            }
            
            d = new NumericalInstanceDataset(dataset,null);
        }
        else if (dataset[0] instanceof Cluster)
        {
            for (int i=totInstances; i<dataset.length; i++)
            {
                //sham instances
                dataset[i] = new Cluster(((Cluster)dataset[0]).getInstances(),i,((Cluster)dataset[0]).getNumberOfFeatures());
            }
            
            d = new ClusterDataset(dataset,null);
        }
        else if (dataset[0] instanceof Clustering)
        {
            for (int i=totInstances; i<dataset.length; i++)
            {
                //sham instances
                dataset[i] = new Clustering(((Clustering)dataset[0]).getClusters(),i);
            }
            
            d = new ClusteringDataset(dataset,null);
        } 
        
        return d;
    }
    
    protected Clustering buildFinalClustering(Clustering hybridGraphPartitioningResult, int totInstances)
    {
        Cluster[] metisClusters = hybridGraphPartitioningResult.getClusters();
        Cluster[] finalClusters = new Cluster[metisClusters.length];
        
        for (int i=0; i<metisClusters.length; i++)
        {
            Instance[] tmp = metisClusters[i].getInstances();
            int trueInstances = 0;
            for (int j=0; j<tmp.length; j++)
            {
                if (tmp[j].getID() < totInstances)//true instance
                {
                    trueInstances++;
                }
            }
            
            Instance[] trueTmp = new Instance[trueInstances];
            int k=0;
            for (int j=0; j<tmp.length; j++)
            {
                if (tmp[j].getID() < totInstances)
                {
                    trueTmp[k] = tmp[j];
                    k++;
                }
            }
            
            finalClusters[i] = new Cluster(trueTmp,i,totInstances);
        }
        
        return new Clustering(finalClusters);
    }

}


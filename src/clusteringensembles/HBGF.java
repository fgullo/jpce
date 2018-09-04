package clusteringensembles;

import clustering.METIS;
import weighting.WeightingScheme;
import dataset.ClusteringDataset;
import dataset.Dataset;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;


public class HBGF extends HybridCEMethod {

    public HBGF (ClusteringDataset ensemble) 
    {
        this.ensemble = ensemble;
    }

    public Clustering execute (int nClusters) 
    {
        Instance[] instances = ensemble.getInstances();
        Cluster[] clusters = ensemble.getClusters();
        
        int totInstances = instances.length;
        int totClusters = clusters.length;
        
        //build similarity matrix representing the edge weights in the hybrid graph  
        double[][] simMatrix = new double[totInstances+totClusters][totInstances+totClusters];   
        for (int i=0; i<totClusters; i++)
        {
            int column = totInstances+i;
            Instance[] curr = clusters[i].getInstances();
            for (int j=0; j<curr.length; j++)
            {
                int row = curr[j].getID();
                simMatrix[row][column] = 1.0;
            }
        }
        
        //build dataset
        Dataset d = buildHybridDataset(instances,totInstances,totClusters);
        
        //run metis
        METIS metis = new METIS(d);
        Clustering metisResult = metis.execute(simMatrix, nClusters);
        
        return buildFinalClustering(metisResult,totInstances);
    }
    
    public Clustering weightedExecute (int nClusters, WeightingScheme ws) 
    {
        double[] weights = ws.weight(ensemble);
        
        Instance[] instances = ensemble.getInstances();
        Cluster[] clusters = ensemble.getClusters();
        
        int[] clusterToClusteringMapping = new int[ensemble.getNumberOfClusters()];
        Clustering[] clusterings = (Clustering[])ensemble.getData();
        for (int i=0; i<clusterings.length; i++)
        {
            Cluster[] clust = clusterings[i].getClusters();
            for (int j=0; j<clust.length; j++)
            {
                clusterToClusteringMapping[clust[j].getID()] = i;
            }
        }        
        
        int totInstances = instances.length;
        int totClusters = clusters.length;
        
        //build similarity matrix representing the edge weights in the hybrid graph  
        double[][] simMatrix = new double[totInstances+totClusters][totInstances+totClusters];   
        for (int i=0; i<totClusters; i++)
        {
            int column = totInstances+i;
            Instance[] curr = clusters[i].getInstances();
            for (int j=0; j<curr.length; j++)
            {
                int row = curr[j].getID();
                simMatrix[row][column] = weights[clusterToClusteringMapping[clusters[i].getID()]];
            }
        }
        
        //build dataset
        Dataset d = buildHybridDataset(instances,totInstances,totClusters);
        
        //run metis
        METIS metis = new METIS(d);
        Clustering metisResult = metis.execute(simMatrix, nClusters);
        
        return buildFinalClustering(metisResult,totInstances);
    }    

}


package clusteringensembles;

import clustering.KMeans;
import weighting.WeightingScheme;
import dataset.ClusterDataset;
import dataset.ClusteringDataset;
import evaluation.cluster.JaccardClusterSim;
import objects.Cluster;
import objects.centroid.ClusterCentroidComputationMajorityVoting;
import objects.Clustering;

public class MetaClusterSearch extends ClusterBasedCEMethod {

    public MetaClusterSearch (ClusteringDataset ensemble) 
    {
        this.ensemble = ensemble;
    }

    public Clustering execute (int nClusters) 
    {
        ClusterDataset clusterDataset = new ClusterDataset(ensemble.getClusters(),null);
        KMeans kmeans = new KMeans(clusterDataset, new ClusterCentroidComputationMajorityVoting());
        
        Clustering metaClusteringResult = kmeans.execute(new JaccardClusterSim(), nClusters);
        
        //build final clustering by majority voting
        return buildFinalClusteringByMajorityVoting(metaClusteringResult);
    }

    public Clustering weightedExecute (int nClusters, WeightingScheme ws) 
    {
        int[] clusterToClusteringMapping = new int[ensemble.getNumberOfClusters()];
        Clustering[] clusterings = (Clustering[])ensemble.getData();
        for (int i=0; i<clusterings.length; i++)
        {
            Cluster[] clusters = clusterings[i].getClusters();
            for (int j=0; j<clusters.length; j++)
            {
                clusterToClusteringMapping[clusters[j].getID()] = i;
            }
        }
        
        ClusterDataset clusterDataset = new ClusterDataset(ensemble.getClusters(),null);
        KMeans kmeans = new KMeans(clusterDataset, new ClusterCentroidComputationMajorityVoting());
        
        Clustering metaClusteringResult = kmeans.execute(new JaccardClusterSim(), nClusters);
        
        //build final clustering by majority voting
        return buildFinalClusteringByWeightedMajorityVoting(metaClusteringResult,ws.weight(ensemble),clusterToClusteringMapping);
    }    
}


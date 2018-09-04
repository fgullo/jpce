package clusteringensembles;

import clustering.METIS;
import weighting.WeightingScheme;
import dataset.ClusterDataset;
import dataset.ClusteringDataset;
import evaluation.cluster.JaccardClusterSim;
import objects.Cluster;
import objects.Clustering;


public class MCLA extends ClusterBasedCEMethod {

    public MCLA (ClusteringDataset ensemble) 
    {
        this.ensemble = ensemble;
    }

    public Clustering execute (int nClusters) 
    {
        ClusterDataset clusterDataset = new ClusterDataset(ensemble.getClusters(),null);
        METIS metis = new METIS(clusterDataset);
        
        Clustering metaClusteringResult = metis.execute(new JaccardClusterSim(), nClusters);
        
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
                clusterToClusteringMapping[clusters[j].getID()] = clusterings[i].getID();
            }
        }
        
        ClusterDataset clusterDataset = new ClusterDataset(ensemble.getClusters(),null);
        METIS metis = new METIS(clusterDataset);
        
        Clustering metaClusteringResult = metis.execute(new JaccardClusterSim(), nClusters);
        
        //build final clustering by majority voting
        return buildFinalClusteringByWeightedMajorityVoting(metaClusteringResult, ws.weight(ensemble), clusterToClusteringMapping);
    }    
    
}


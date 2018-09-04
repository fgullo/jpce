package evaluation.clustering;

import evaluation.cluster.ClusterIntraSimilarity;
import evaluation.Similarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;

public class ClusteringIntraSimilarity extends ClusteringInternalValidityCriterion {

    /**
     * This is a costructor for ClusteringIntraSimilarity object
     */
    public ClusteringIntraSimilarity () {
    }

    /**
     * This method return the similarity between
     * @param i
     * @param sim
     * @return double
     */
    public double getSimilarity (Instance i, Similarity sim) {
        double sum=0;
        Cluster [] clusters=((Clustering)i).getClusters();
        if (clusters.length == 0)
        {
            return 0.0;
        }
        for(int j=0; j<clusters.length; j++){
            ClusterIntraSimilarity cis = new ClusterIntraSimilarity();
            sum += cis.getSimilarity(clusters[j], sim);
        }
        return sum=sum/clusters.length;
    }

    /**
     * This method return the distance between
     * @param i
     * @param sim
     * @return double
     */
    public double getDistance (Instance i, Similarity sim) {
        double sum=0;
        Cluster [] clusters=((Clustering)i).getClusters();
        if (clusters.length == 0)
        {
            return Double.POSITIVE_INFINITY;
        }
        for(int j=0; j<clusters.length; j++){
            ClusterIntraSimilarity cis = new ClusterIntraSimilarity();
            sum += cis.getDistance(clusters[j], sim);
        }
        return sum=sum/clusters.length;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ClusteringIntraSimilarity))
        {
            return false;
        }
        
        return true;
    }

}


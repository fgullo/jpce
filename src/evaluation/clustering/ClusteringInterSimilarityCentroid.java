package evaluation.clustering;

import evaluation.Similarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;

public class ClusteringInterSimilarityCentroid extends ClusteringInternalValidityCriterion {

    /**
     * This is a costructor for ClusteringInterSimilarityCentroid object
     */
    public ClusteringInterSimilarityCentroid () {
    }

    /**
     * This method return the similarity between all clusters, the Centroid similarity considers 
     * all possible combinations beetwen the centroids present in all clusters
     * @param i
     * @param sim
     * @return double
     */
    public double getSimilarity (Instance i, Similarity sim) {
        double sum=0;
        Cluster [] clusters=((Clustering)i).getClusters();
        for(int j=0; j<clusters.length-1; j++){
            for (int k=j+1; k<clusters.length; k++){
                sum+=sim.getSimilarity(clusters[j].getCentroid(), clusters[k].getCentroid());
                
            }
        }
        double n = clusters.length;
        double den = n*(n+1)/2;
        
        if (den > 0)
        {
            return sum/den;
        }
        
        return 0.0;        
    }

     /**
     * This method return the distance between all clusters, the Centroid similarity considers 
     * all possible combinations beetwen the centroids present in all clusters
     * @param i
     * @param sim
     * @return double
     */
    public double getDistance (Instance i, Similarity sim) {
       double sum=0;
        Cluster [] clusters=((Clustering)i).getClusters();
        for(int j=0; j<clusters.length-1; j++){
            for (int k=j+1; k<clusters.length; k++){
                sum+=sim.getDistance(clusters[j].getCentroid(), clusters[k].getCentroid());
                
            }
        }
        double n = clusters.length;
        double den = n*(n+1)/2;
        
        if (den > 0)
        {
            return sum/den;
        }
        
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ClusteringInterSimilarityCentroid))
        {
            return false;
        }
        
        return true;
    }
}


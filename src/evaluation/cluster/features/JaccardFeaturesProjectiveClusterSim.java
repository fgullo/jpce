package evaluation.cluster.features;

import objects.Instance;
import objects.ProjectiveCluster;

public class JaccardFeaturesProjectiveClusterSim extends ProjectiveClusterFeaturesSimilarity {

    
     /**
     * This method return the similarity between two Cluster, the similarity is calculated 
     * with Jaccard Similarity, the Jaccard Similarity for binary value is 
     * d(X,Y)=|X intersection B|/|X union Y|
     * @param i1
     * @param i2
     * @return double
     */
    public double getSimilarity (Instance i1, Instance i2) 
    {
        ProjectiveCluster c1 = (ProjectiveCluster)i1;
        ProjectiveCluster c2 = (ProjectiveCluster)i2;
        
        Double[] rep1 = c1.getFeatureToClusterAssignments();
        Double[] rep2 = c2.getFeatureToClusterAssignments();
        
        double dotProduct = 0.0;
        for (int i=0; i<rep1.length; i++)
        {
            dotProduct += rep1[i]*rep2[i];
        }
        
        double norm1 = 0.0;
        for (int i=0; i<rep1.length; i++)
        {
            norm1 += rep1[i]*rep1[i];
        }
        
        double norm2 = 0.0;
        for (int i=0; i<rep2.length; i++)
        {
            norm2 += rep2[i]*rep2[i];
        }
        
        double den = norm1+norm2-dotProduct;
        double ret = 1.0;
        if (den != 0.0)
        {
            ret = dotProduct/den;
        }
        
        if (ret < -0.000001 || ret > 1.000001)
        {
            throw new RuntimeException("ERROR: The value must be within [0,1]");
        }
        
        if (Double.isInfinite(ret) || Double.isNaN(ret))
        {
            //this.getSimilarity(i1, i2);
            throw new RuntimeException("ERROR: the value is INFINITY or NAN");
        }
        
        return ret;
    }

    /**
     * This method return the distance, the distance is (1/similarity)
     * @param i1
     * @param i2
     * @return double
     */
    public double getDistance (Instance i1, Instance i2) 
    {
        return 1.0-getSimilarity(i1,i2);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof JaccardFeaturesProjectiveClusterSim))
        {
            return false;
        }
        
        return true;
    }
}

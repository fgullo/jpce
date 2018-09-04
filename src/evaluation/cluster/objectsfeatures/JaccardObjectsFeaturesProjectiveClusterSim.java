package evaluation.cluster.objectsfeatures;

import objects.Instance;
import objects.ProjectiveCluster;

public class JaccardObjectsFeaturesProjectiveClusterSim extends ProjectiveClusterObjectsFeaturesSimilarity {

    
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
        
        Double[] repo1 = c1.getFeatureVectorRepresentationDouble();
        Double[] repo2 = c2.getFeatureVectorRepresentationDouble();
        if (repo1.length != repo2.length)
        {
            throw new RuntimeException("ERROR: the two object-based representation vectors must be equal-size");
        }
        
        Double[] repf1 = c1.getFeatureToClusterAssignments();
        Double[] repf2 = c2.getFeatureToClusterAssignments();
        if (repf1.length != repf2.length)
        {
            throw new RuntimeException("ERROR: the two feature-based representation vectors must be equal-size");
        }        
        
        
        
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i=0; i<repo1.length; i++)
        {
            for (int j=0; j<repf1.length; j++)
            {
                norm1 += Math.pow(repo1[i]*repf1[j],2);
                norm2 += Math.pow(repo2[i]*repf2[j],2);
            }
        }
        
        double dotProduct = 0.0;
        for (int i=0; i<repo1.length; i++)
        {
            for (int j=0; j<repf1.length; j++)
            {
                dotProduct += repo1[i]*repf1[j]*repo2[i]*repf2[j];
            }
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
        if (!(o instanceof JaccardObjectsFeaturesProjectiveClusterSim))
        {
            return false;
        }
        
        return true;
    }
}



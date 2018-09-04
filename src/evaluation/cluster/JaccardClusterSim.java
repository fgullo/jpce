package evaluation.cluster;

import objects.Cluster;
import objects.Instance;

public class JaccardClusterSim extends ClusterSimilarity {

    /**
     * This is a costructor for JaccardClusterSim object
     */
    public JaccardClusterSim () {
    }

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
        Cluster c1 = (Cluster)i1;
        Cluster c2 = (Cluster)i2;
        
        double num = (double)c1.numberOfAgreements(c2);
        double den = (double)(c1.getNumberOfInstances()+c2.getNumberOfInstances()-num);
        
        if (den > 0)
        {
            return num/den;
        }
        
        return 0.0;
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
        if (!(o instanceof JaccardClusterSim))
        {
            return false;
        }
        
        return true;
    }
}


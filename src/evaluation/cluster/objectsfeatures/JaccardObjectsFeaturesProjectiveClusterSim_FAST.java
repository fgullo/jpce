package evaluation.cluster.objectsfeatures;

import evaluation.cluster.objects.JaccardObjectsProjectiveClusterSim;
import evaluation.cluster.features.JaccardFeaturesProjectiveClusterSim;
import objects.Instance;
import objects.ProjectiveCluster;

public class JaccardObjectsFeaturesProjectiveClusterSim_FAST extends ProjectiveClusterObjectsFeaturesSimilarity {

    
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
        JaccardObjectsProjectiveClusterSim osim = new JaccardObjectsProjectiveClusterSim();
        JaccardFeaturesProjectiveClusterSim fsim = new JaccardFeaturesProjectiveClusterSim();
        
        return 0.5*(osim.getSimilarity(i1, i2)+fsim.getSimilarity(i1, i2));
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
        if (!(o instanceof JaccardObjectsFeaturesProjectiveClusterSim_FAST))
        {
            return false;
        }
        
        return true;
    }
}




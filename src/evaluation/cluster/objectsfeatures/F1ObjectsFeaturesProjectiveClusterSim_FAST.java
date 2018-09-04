package evaluation.cluster.objectsfeatures;

import evaluation.cluster.objects.F1ObjectsProjectiveClusterSim;
import evaluation.cluster.features.F1FeaturesProjectiveClusterSim;
import objects.Instance;

public class F1ObjectsFeaturesProjectiveClusterSim_FAST extends ProjectiveClusterObjectsFeaturesSimilarity {

    
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
        F1ObjectsProjectiveClusterSim osim = new F1ObjectsProjectiveClusterSim();
        F1FeaturesProjectiveClusterSim fsim = new F1FeaturesProjectiveClusterSim();
        
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
        if (!(o instanceof F1ObjectsFeaturesProjectiveClusterSim_FAST))
        {
            return false;
        }
        
        return true;
    }
}





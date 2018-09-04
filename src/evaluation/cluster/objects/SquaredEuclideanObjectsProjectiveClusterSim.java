package evaluation.cluster.objects;

import objects.Instance;
import objects.ProjectiveCluster;

public class SquaredEuclideanObjectsProjectiveClusterSim extends ProjectiveClusterObjectsSimilarity {


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
        double distance = getDistance(i1, i2);

        if (distance > 0)
        {
            return ((double)1.0)/distance;
        }

        return Double.POSITIVE_INFINITY;
    }

    /**
     * This method return the distance, the distance is (1/similarity)
     * @param i1
     * @param i2
     * @return double
     */
    public double getDistance (Instance i1, Instance i2)
    {
        ProjectiveCluster c1 = (ProjectiveCluster)i1;
        ProjectiveCluster c2 = (ProjectiveCluster)i2;

        Double[] rep1 = c1.getFeatureVectorRepresentationDouble();
        Double[] rep2 = c2.getFeatureVectorRepresentationDouble();

        double ret = 0.0;
        for (int i=0; i<rep1.length; i++)
        {
            ret += (rep1[i]-rep2[i])*(rep1[i]-rep2[i]);
        }

        if (Double.isInfinite(ret) || Double.isNaN(ret))
        {
            //this.getSimilarity(i1, i2);
            throw new RuntimeException("ERROR: the value is INFINITY or NAN");
        }

        return ret;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof SquaredEuclideanObjectsProjectiveClusterSim))
        {
            return false;
        }

        return true;
    }
}




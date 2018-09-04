package objects.centroid;

import objects.Instance;

public interface ProjectiveCentroidComputation extends CentroidComputation{

    /**
     * This method calculate the Centroid
     * @param data
     * @return Instance
     */
    public Instance getCentroid (Instance[] data, Double[] rep);

}

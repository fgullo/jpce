
package weighting;

import dataset.ClusteringDataset;

/*
 * This class is used by NMFWeighted class
*/
public class WeightingSchemeNaive extends WeightingScheme{

    double [] wheights;

    public WeightingSchemeNaive(double [] wheights){
        this.wheights = wheights;
    }
    
    public double[] weight(ClusteringDataset library) {
        return wheights;
    }
    
    public void setNaiveWeight (double [] newWeights){

        wheights = newWeights;
    }

    
}

package weighting;

import dataset.ClusteringDataset;

public abstract class WeightingScheme 
{
    public abstract double[] weight(ClusteringDataset library);
}

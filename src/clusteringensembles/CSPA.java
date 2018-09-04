package clusteringensembles;

import clustering.METIS;
import weighting.WeightingScheme;
import dataset.ClusteringDataset;
import objects.Clustering;

public class CSPA extends InstanceBasedCEMethod {

    public CSPA (ClusteringDataset ensemble) 
    {
        this.ensemble = ensemble;
    }

    public Clustering execute (int nClusters) 
    {
        double[][] simMatrix = ensemble.getCoOccurrenceMatrix();
        METIS metis = new METIS(ensemble.getInstancesDataset());
        
        return metis.execute(simMatrix, nClusters);
    }
    
    public Clustering weightedExecute (int nClusters, WeightingScheme ws) 
    {
        double[][] simMatrix = ensemble.getWeightedCoOccurrenceMatrix(ws);
        METIS metis = new METIS(ensemble.getInstancesDataset());
        
        return metis.execute(simMatrix, nClusters);
    }    

}


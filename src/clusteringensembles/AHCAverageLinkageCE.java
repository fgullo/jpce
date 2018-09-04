package clusteringensembles;

import dataset.ClusteringDataset;
import objects.Clustering;
import clustering.AHCAverageLinkage;
import weighting.WeightingScheme;

public class AHCAverageLinkageCE extends InstanceBasedCEMethod {

    public AHCAverageLinkageCE (ClusteringDataset ensemble) 
    {
        this.ensemble = ensemble;
    }

    public Clustering execute (int nClusters) 
    {
        double[][] simMatrix = ensemble.getCoOccurrenceMatrix();
        AHCAverageLinkage ahcAL = new AHCAverageLinkage(ensemble.getInstancesDataset(),false);
        
        return ahcAL.execute(simMatrix, nClusters);                
    }
    
    public Clustering weightedExecute (int nClusters, WeightingScheme  ws) 
    {
        double[][] simMatrix = ensemble.getWeightedCoOccurrenceMatrix(ws);
        AHCAverageLinkage ahcAL = new AHCAverageLinkage(ensemble.getInstancesDataset(),false);
        
        return ahcAL.execute(simMatrix, nClusters);                
    }    

}


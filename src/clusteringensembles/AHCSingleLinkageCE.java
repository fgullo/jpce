package clusteringensembles;

import dataset.ClusteringDataset;
import objects.Clustering;
import clustering.AHCSingleLinkage;
import weighting.WeightingScheme;

public class AHCSingleLinkageCE extends InstanceBasedCEMethod {

    public AHCSingleLinkageCE (ClusteringDataset ensemble) 
    {
        this.ensemble = ensemble;
    }

    public Clustering execute (int nClusters) 
    {
        double[][] simMatrix = ensemble.getCoOccurrenceMatrix();
        AHCSingleLinkage ahcSL = new AHCSingleLinkage(ensemble.getInstancesDataset(),false);
        
        return ahcSL.execute(simMatrix, nClusters);                
    }
    
    public Clustering weightedExecute (int nClusters, WeightingScheme ws) 
    {
        double[][] simMatrix = ensemble.getWeightedCoOccurrenceMatrix(ws);
        AHCSingleLinkage ahcSL = new AHCSingleLinkage(ensemble.getInstancesDataset(),false);
        
        return ahcSL.execute(simMatrix, nClusters);                
    }    

}


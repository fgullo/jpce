
package pce.enhancedtwoobjective;

import clustering.Metaclusters_ML2012;
import dataset.ProjectiveClusteringDataset;
import evaluation.cluster.objectsfeatures.ProjectiveClusterObjectsFeaturesSimilarity;
import java.util.HashMap;
import objects.Clustering;

public class OneObjectiveClusterBasedPCE_ECBPCE_ML2012 extends OneObjectiveClusterBasedPCE_CBPCE
{
    protected HashMap<Integer, Integer>[] mapping;
    
    public OneObjectiveClusterBasedPCE_ECBPCE_ML2012 (ProjectiveClusteringDataset ensemble, ProjectiveClusterObjectsFeaturesSimilarity objectFeatureClusterSim, int alpha, int beta, boolean featureCentroid)
    {
        super();
        this.ensemble = ensemble;
        this.objectFeatureClusterSim = objectFeatureClusterSim;
        
        if (alpha <= 1)
        {
            throw new RuntimeException("ERROR: alpha must be greater than 1");
        }
        this.alpha = alpha;

        if (beta <= 1)
        {
            throw new RuntimeException("ERROR: beta must be greater than 1");
        }
        this.beta = beta;

        if (featureCentroid)
        {
            this.version = OneObjectiveClusterBasedPCE_CBPCE.STANDARD_OBJ_CENTROID_FEAT;
        }
        else
        {
            this.version = OneObjectiveClusterBasedPCE_CBPCE.STANDARD;
        }
        
        long start = System.currentTimeMillis();
        this.mapping = ensemble.getMapping();
        this.clusterObjectsFeaturesDistMatrix = Metaclusters_ML2012.computeDistances(objectFeatureClusterSim, this.mapping, ensemble);
        this.offlineExecutionTime = System.currentTimeMillis() - start;
    }
    
    @Override
    protected Clustering buildMetaClusters(int nClusters) 
    {
        Metaclusters_ML2012 mcClustering = new Metaclusters_ML2012(ensemble,this.mapping);
        Clustering mcResult = mcClustering.execute(this.clusterObjectsFeaturesDistMatrix, nClusters);
        
        return mcResult;
    }
}

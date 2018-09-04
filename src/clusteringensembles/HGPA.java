package clusteringensembles;

import clustering.HMETIS;
import weighting.WeightingScheme;
import dataset.ClusteringDataset;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import java.util.ArrayList;

public class HGPA extends InstanceBasedCEMethod {

    public HGPA (ClusteringDataset ensemble) 
    {
        this.ensemble = ensemble;
    }

    public Clustering execute (int nClusters) 
    {
        //build hyperedges
        Cluster[] allClusters = ensemble.getClusters();
        ArrayList hyperedges = new ArrayList<int[]>(allClusters.length);
        
        for (int i=0; i<allClusters.length; i++)
        {
            Instance[] clusterI = allClusters[i].getInstances();
            int[] hyperedgeI = new int[clusterI.length];
            
            for (int j=0; j<clusterI.length; j++)
            {
                hyperedgeI[j] = clusterI[j].getID();
            }
            hyperedges.add(hyperedgeI);
        }
        
        HMETIS hmetis = new HMETIS(ensemble.getInstancesDataset());
        return hmetis.execute(hyperedges, nClusters);
    }
    
    public Clustering weightedExecute (int nClusters, WeightingScheme ws) 
    {
        //build hyperedges
        Cluster[] allClusters = ensemble.getClusters();
        ArrayList hyperedges = new ArrayList<int[]>(allClusters.length);
        
        for (int i=0; i<allClusters.length; i++)
        {
            Instance[] clusterI = allClusters[i].getInstances();
            int[] hyperedgeI = new int[clusterI.length];
            
            for (int j=0; j<clusterI.length; j++)
            {
                hyperedgeI[j] = clusterI[j].getID();
            }
            hyperedges.add(hyperedgeI);
        }
        
        HMETIS hmetis = new HMETIS(ensemble.getInstancesDataset());
        return hmetis.execute(hyperedges, nClusters);
    }    

}


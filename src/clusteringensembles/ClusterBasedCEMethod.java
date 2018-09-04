package clusteringensembles;

import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import java.util.ArrayList;


public abstract class ClusterBasedCEMethod extends CEMethod
{
    protected Clustering buildFinalClusteringByMajorityVoting(Clustering metaClusteringResult)
    {
        Cluster[] metaClusters = metaClusteringResult.getClusters();
        int[][] occurrences = new int[ensemble.getNumberOfInstances()][metaClusteringResult.getNumberOfClusters()];
        
        for (int i=0; i<metaClusters.length; i++)
        {
            Instance[] clustersI = metaClusters[i].getInstances();
            for (int j=0; j<clustersI.length; j++)
            {
                Instance[] instances = ((Cluster)clustersI[j]).getInstances();
                for (int k=0; k<instances.length; k++)
                {
                    int ID = instances[k].getID();
                    occurrences[ID][i]++;
                }
            }
        }
        
        ArrayList[] beBuilding = new ArrayList[metaClusteringResult.getNumberOfClusters()];
        for (int i=0; i<beBuilding.length; i++)
        {
            beBuilding[i] = new ArrayList(ensemble.getNumberOfInstances());
        }
        
        Instance[] instancesArray = ensemble.getInstances();
        for (int i=0; i<occurrences.length; i++)
        {
            int jMax = 0;
            int max = occurrences[i][0];
            for (int j=1; j<occurrences[i].length; j++)
            {
                if (occurrences[i][j] > max)
                {
                    max = occurrences[i][j];
                    jMax = j;
                }
            }
            
            beBuilding[jMax].add(instancesArray[i]);
        }
        
        Cluster[] finalClusters = new Cluster[beBuilding.length];
        for (int i=0; i<beBuilding.length; i++)
        {
            Instance[] clusterI = new Instance[beBuilding[i].size()];
            for (int j=0; j<clusterI.length; j++)
            {
                clusterI[j] = (Instance)beBuilding[i].get(j);
            }
            
            finalClusters[i] = new Cluster(clusterI,i,ensemble.getNumberOfInstances());           
        }
       
        return new Clustering(finalClusters);        
    }
    
    protected Clustering buildFinalClusteringByWeightedMajorityVoting(Clustering metaClusteringResult,double[] weights, int[] clusterToClusteringMapping)
    {
        Cluster[] metaClusters = metaClusteringResult.getClusters();
        int[][] occurrences = new int[ensemble.getNumberOfInstances()][metaClusteringResult.getNumberOfClusters()];
        
        for (int i=0; i<metaClusters.length; i++)
        {
            Instance[] clustersI = metaClusters[i].getInstances();
            for (int j=0; j<clustersI.length; j++)
            {
                Instance[] instances = ((Cluster)clustersI[j]).getInstances();
                int idCluster = ((Cluster)clustersI[j]).getID();
                for (int k=0; k<instances.length; k++)
                {
                    int ID = instances[k].getID();
                    occurrences[ID][i]+=weights[clusterToClusteringMapping[idCluster]];
                }
            }
        }
        
        ArrayList[] beBuilding = new ArrayList[metaClusteringResult.getNumberOfClusters()];
        for (int i=0; i<beBuilding.length; i++)
        {
            beBuilding[i] = new ArrayList(ensemble.getNumberOfInstances());
        }
        
        Instance[] instancesArray = ensemble.getInstances();
        for (int i=0; i<occurrences.length; i++)
        {
            int jMax = 0;
            int max = occurrences[i][0];
            for (int j=1; j<occurrences[i].length; j++)
            {
                if (occurrences[i][j] > max)
                {
                    max = occurrences[i][j];
                    jMax = j;
                }
            }
            
            beBuilding[jMax].add(instancesArray[i]);
        }
        
        Cluster[] finalClusters = new Cluster[beBuilding.length];
        for (int i=0; i<beBuilding.length; i++)
        {
            Instance[] clusterI = new Instance[beBuilding[i].size()];
            for (int j=0; j<clusterI.length; j++)
            {
                clusterI[j] = (Instance)beBuilding[i].get(j);
            }
            
            finalClusters[i] = new Cluster(clusterI,i,ensemble.getNumberOfInstances());           
        }
       
        return new Clustering(finalClusters);        
    }    
}


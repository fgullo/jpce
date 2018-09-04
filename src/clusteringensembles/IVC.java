package clusteringensembles;

import clustering.KMeans;
import weighting.WeightingScheme;
import dataset.ClusteringDataset;
import dataset.Dataset;
import dataset.NumericalInstanceDataset;
import evaluation.numericalinstance.HammingNumericalInstanceSim;
import evaluation.numericalinstance.WeightedHammingNumericalInstanceSim;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;
import objects.centroid.NumericalInstanceCentroidComputationMajorityVoting;

public class IVC extends InstanceBasedCEMethod {

    public IVC (ClusteringDataset ensemble) 
    {
        this.ensemble = ensemble;
    }

    public Clustering execute (int nClusters) 
    {
        //build instances with new features
        Instance[] instances = ensemble.getInstances(); 
        Clustering[] partitions = (Clustering[])ensemble.getData();
        
        Double[][] newFeatures = new Double[instances.length][partitions.length];
        for (int i=0; i<partitions.length; i++)
        {
            Cluster[] clustersI = partitions[i].getClusters();
            for (int j=0; j<clustersI.length; j++)
            {
                Instance[] clusterIJ = clustersI[j].getInstances();
                for (int k=0; k<clusterIJ.length; k++)
                {
                    Instance instanceIJK = clusterIJ[k];
                    newFeatures[instanceIJK.getID()][i] = new Double(clustersI[j].getID());
                }
            }
        }
        
        //build dataset
        Instance[] inst = new Instance[newFeatures.length];
        for (int i=0; i<newFeatures.length; i++)
        {
            inst[i] = new NumericalInstance(newFeatures[i],i);
        }
        Dataset dataset = new NumericalInstanceDataset(inst, null);
        
        //k-means
        KMeans kmeans = new KMeans(dataset, new NumericalInstanceCentroidComputationMajorityVoting());
        Clustering kmeansResult = kmeans.execute(new HammingNumericalInstanceSim(), nClusters);
        
        //build final clustering
        Cluster[] finalClusters = new Cluster[kmeansResult.getNumberOfClusters()];
        for (int i=0; i<kmeansResult.getNumberOfClusters(); i++)
        {
            Instance[] c = kmeansResult.getClusters()[i].getInstances();
            Instance[] cFinal = new Instance[c.length];
            for (int j=0; j<c.length; j++)
            {
                cFinal[j] = instances[c[j].getID()];
            }
            
            finalClusters[i] = new Cluster(cFinal,i,instances.length);            
        }
        
        return new Clustering(finalClusters);
    }
    
    public Clustering weightedExecute (int nClusters,WeightingScheme ws) 
    {
        //build instances with new features
        Instance[] instances = ensemble.getInstances(); 
        Clustering[] partitions = (Clustering[])ensemble.getData();
        
        Double[][] newFeatures = new Double[instances.length][partitions.length];
        for (int i=0; i<partitions.length; i++)
        {
            Cluster[] clustersI = partitions[i].getClusters();
            for (int j=0; j<clustersI.length; j++)
            {
                Instance[] clusterIJ = clustersI[j].getInstances();
                for (int k=0; k<clusterIJ.length; k++)
                {
                    Instance instanceIJK = clusterIJ[k];
                    newFeatures[instanceIJK.getID()][i] = new Double(clustersI[j].getID());
                }
            }
        }
        
        //build dataset
        Instance[] inst = new Instance[newFeatures.length];
        for (int i=0; i<newFeatures.length; i++)
        {
            inst[i] = new NumericalInstance(newFeatures[i],i);
        }
        Dataset dataset = new NumericalInstanceDataset(inst, null);
        
        //k-means
        KMeans kmeans = new KMeans(dataset, new NumericalInstanceCentroidComputationMajorityVoting());
        Clustering kmeansResult = kmeans.execute(new WeightedHammingNumericalInstanceSim(ws.weight(ensemble)), nClusters);
        
        //build final clustering
        Cluster[] finalClusters = new Cluster[kmeansResult.getNumberOfClusters()];
        for (int i=0; i<kmeansResult.getNumberOfClusters(); i++)
        {
            Instance[] c = kmeansResult.getClusters()[i].getInstances();
            Instance[] cFinal = new Instance[c.length];
            for (int j=0; j<c.length; j++)
            {
                cFinal[j] = instances[c[j].getID()];
            }
            
            finalClusters[i] = new Cluster(cFinal,i,instances.length);            
        }
        
        return new Clustering(finalClusters);
    }    

}


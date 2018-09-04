package pce.baseline;

import clustering.KMeans;
import dataset.Dataset;
import dataset.NumericalInstanceDataset;
import dataset.ProjectiveClusteringDataset;
import evaluation.numericalinstance.HammingNumericalInstanceSim;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;
import objects.centroid.NumericalInstanceCentroidComputationMajorityVoting;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

public class IVC_CEbasedPCE extends CEbasedPCE
{
    protected ProjectiveClustering result;
    protected Dataset newDataset;

    public IVC_CEbasedPCE(ProjectiveClusteringDataset ensemble)
    {
        long start = System.currentTimeMillis();
        this.ensemble = ensemble;
        computeNewFeatures();
        this.offlineExecutionTime = System.currentTimeMillis()-start;
    }

    public ProjectiveClustering execute (int nClusters)
    {
        long start = System.currentTimeMillis();
        
        KMeans kmeans = new KMeans(this.newDataset, new NumericalInstanceCentroidComputationMajorityVoting());
        Clustering kmeansResult = kmeans.execute(new HammingNumericalInstanceSim(), nClusters);

        //build final clustering
        Cluster[] finalClusters = new Cluster[kmeansResult.getNumberOfClusters()];
        for (int i=0; i<kmeansResult.getNumberOfClusters(); i++)
        {
            Instance[] c = kmeansResult.getClusters()[i].getInstances();
            Instance[] cFinal = new Instance[c.length];
            for (int j=0; j<c.length; j++)
            {
                cFinal[j] = this.newDataset.getData()[c[j].getID()];
            }

            finalClusters[i] = new Cluster(cFinal,i,this.newDataset.getData().length);
        }

        this.result = hard2projective(new Clustering(finalClusters));

        this.onlineExecutionTime = System.currentTimeMillis()-start;

        return this.result;
    }

    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }

    protected void computeNewFeatures()
    {
        //build instances with new features
        Instance[] instances = ensemble.getInstances();
        Instance[] newInstances = new Instance[instances.length];
        ProjectiveClustering[] partitions = (ProjectiveClustering[])ensemble.getData();

        for (int i=0; i<newInstances.length; i++)
        {
            Double[] newFeatures = new Double[partitions.length];
            for (int j=0; j<partitions.length; j++)
            {
                ProjectiveCluster[] clusters = partitions[j].getClusters();

                double max = Double.NEGATIVE_INFINITY;
                int index = -1;
                for (int k=0; k<clusters.length; k++)
                {
                    double value = clusters[k].getFeatureVectorRepresentationDouble()[i];
                    if (value > max)
                    {
                        max = value;
                        index = k;
                    }
                }

                newFeatures[j] = (double)index;
            }

            newInstances[i] = new NumericalInstance(newFeatures,i);
        }

        //build dataset
        this.newDataset = new NumericalInstanceDataset(newInstances, null);
    }

    protected ProjectiveClustering hard2projective(Clustering fc)
    {
        ProjectiveCluster[] pcs = new ProjectiveCluster[fc.getNumberOfClusters()];
        for (int i=0; i<pcs.length; i++)
        {
            Double[] features = new Double[this.ensemble.getNumberOfFeaturesInEachCluster()];
            randomFeatures(features);
            pcs[i] = new ProjectiveCluster(this.ensemble.getInstances(),fc.getClusters()[i].getFeatureVectorRepresentationDouble(),features,i,false,true);
        }

        return new ProjectiveClustering(pcs, -1);
    }
}

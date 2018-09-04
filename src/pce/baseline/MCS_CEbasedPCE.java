package pce.baseline;

import clustering.KMeans;
import dataset.Dataset;
import dataset.ProjectiveClusterDataset;
import dataset.ProjectiveClusteringDataset;
import evaluation.cluster.objects.JaccardObjectsProjectiveClusterSim;
import objects.Clustering;
import objects.ProjectiveCluster;
import objects.centroid.ProjectiveClusterCentroidComputationMajorityVoting_OBJECTSOnly;
import objects.ProjectiveClustering;

public class MCS_CEbasedPCE  extends CEbasedPCE
{
    protected ProjectiveClustering result;
    protected Dataset newDataset;

    public MCS_CEbasedPCE(ProjectiveClusteringDataset ensemble)
    {
        long start = System.currentTimeMillis();
        this.ensemble = ensemble;
        ProjectiveCluster[] clusters = ensemble.getClusters();
        this.newDataset = new ProjectiveClusterDataset(clusters,null);
        this.offlineExecutionTime = System.currentTimeMillis()-start+ensemble.getBuildAllClustersTime();
    }

    public ProjectiveClustering execute (int nClusters)
    {
        long start = System.currentTimeMillis();

        KMeans kmeans = new KMeans(this.newDataset, new ProjectiveClusterCentroidComputationMajorityVoting_OBJECTSOnly());

        Clustering metaClusteringResult = kmeans.execute(new JaccardObjectsProjectiveClusterSim(), nClusters);

        //build final clustering by majority voting
        this.result = buildFinalClusteringByMajorityVoting(metaClusteringResult);

        this.onlineExecutionTime = System.currentTimeMillis()-start;

        return result;
    }

    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }

    protected ProjectiveClustering buildFinalClusteringByMajorityVoting(Clustering c)
    {
        int[] assignments = new int[this.ensemble.getNumberOfInstances()];
        for (int i=0; i<assignments.length; i++)
        {
            double max = Double.NEGATIVE_INFINITY;
            assignments[i] = -1;
            for (int k=0; k<c.getNumberOfClusters(); k++)
            {
                double sum = 0.0;
                for (int h=0; h<c.getClusters()[k].getNumberOfInstances(); h++)
                {
                    sum += ((ProjectiveCluster)c.getClusters()[k].getInstances()[h]).getFeatureVectorRepresentationDouble()[i];
                }

                if (sum > max)
                {
                    max = sum;
                    assignments[i] = k;
                }
            }

            util.Util.throwException(assignments[i], -1.0, Double.POSITIVE_INFINITY);
        }

        ProjectiveCluster[] clusters = new ProjectiveCluster[c.getNumberOfClusters()];
        for (int k=0; k<clusters.length; k++)
        {
            Double[] orep = new Double[assignments.length];
            for (int i=0; i<assignments.length; i++)
            {
                if (assignments[i]==k)
                {
                    orep[i] = 1.0;
                }
                else
                {
                    orep[i] = 0.0;
                }
            }

            Double[] frep = new Double[this.ensemble.getNumberOfFeaturesInEachCluster()];
            this.randomFeatures(frep);

            clusters[k] = new ProjectiveCluster(this.ensemble.getInstances(),orep,frep,k,false,true);
        }

        return new ProjectiveClustering(clusters, -1);
    }
}

package pce.baseline;

import clustering.FuzzyKMedoids;
import dataset.Dataset;
import dataset.ProjectiveClusteringDataset;
import evaluation.pdf.ProductPDFSimHard;
import objects.FuzzyClustering;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

public class CoOccurrence_CEBasedPCE extends CEbasedPCE
{
    protected ProjectiveClustering result;
    protected double[][] distMatrix;

    public CoOccurrence_CEBasedPCE (ProjectiveClusteringDataset ensemble)
    {
        this.ensemble = ensemble;
        this.distMatrix = this.ensemble.getCoNonOccurrenceObjectMatrix(new ProductPDFSimHard());

        this.offlineExecutionTime = this.ensemble.getCoNonOccurrenceObjectMatrixTime();
    }
    
    

    public ProjectiveClustering execute (int nClusters) 
    {
        long start = System.currentTimeMillis();

        if (nClusters != this.ensemble.getNumberOfClustersInEachClustering())
        {
            System.out.println("WARNING: the number of clusters is different from the number of clusters in each clustering of the ensemble");
        }
        
        Dataset d = this.ensemble.getInstancesDataset();
        FuzzyKMedoids fkm = new FuzzyKMedoids(d);
        FuzzyClustering res = fkm.execute(this.distMatrix, nClusters);
        
        this.result = fuzzy2projective(res);

        this.onlineExecutionTime = System.currentTimeMillis()-start;

        return this.result;
    }


    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }

    protected ProjectiveClustering fuzzy2projective(FuzzyClustering fc)
    {
        ProjectiveCluster[] pcs = new ProjectiveCluster[fc.getNumberOfClusters()];
        for (int i=0; i<pcs.length; i++)
        {
            Double[] features = new Double[this.ensemble.getNumberOfFeaturesInEachCluster()];
            randomFeatures(features);
            pcs[i] = new ProjectiveCluster(this.ensemble.getInstances(),fc.getClusters()[i].getFeatureVectorRepresentationDouble(),features,i,true,true);
        }

        return new ProjectiveClustering(pcs, -1);
    }

}


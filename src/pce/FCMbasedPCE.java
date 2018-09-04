/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce;

import clustering.FuzzyCMeans;
import dataset.NumericalInstanceDataset;
import dataset.ProjectiveClusteringDataset;
import evaluation.numericalinstance.MinkowskiNumericalInstanceSim;
import objects.FuzzyClustering;
import objects.centroid.FuzzyNumericalInstanceCentroidComputationAVG;
import objects.Instance;
import objects.NumericalInstance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;


public class FCMbasedPCE extends PCEMethod
{
    protected double[][] objectByFeatureMatrix;
    protected int maxIterations = 100;
    protected int m1=2;
    protected int m2=2;
    
    protected ProjectiveClustering result;
    
    //public EMlikeSubspaceEnsembles (ProjectiveClusteringDataset ensemble, SubspaceClusterObjectsSimilarity objectClusterSim, SubspaceClusterFeaturesSimilarity featureClusterSim)
    public FCMbasedPCE (ProjectiveClusteringDataset ensemble)
    {
        this.ensemble = ensemble;
    }
    
    public ProjectiveClustering execute (int nClusters)
    {
        if (nClusters != this.ensemble.getNumberOfClustersInEachClustering())
        {
            System.out.println("WARNING: the number of clusters is different from the number of clusters in each clustering of the ensemble");
        }
        
        NumericalInstanceDataset d = buildNumericalInstanceDatasetFromObjectByFeatureMatrix();
        FuzzyCMeans fcm = new FuzzyCMeans(d, new FuzzyNumericalInstanceCentroidComputationAVG(null,2));
        
        FuzzyClustering fcmResult = fcm.execute(new MinkowskiNumericalInstanceSim(2),nClusters);
        Instance[] centroids = fcm.getCentroids();
        
        ProjectiveCluster[] clusters = new ProjectiveCluster[nClusters];
        for (int i=0; i<clusters.length; i++)
        {
            Double[] objectAssignment = fcmResult.getClusters()[i].getFeatureVectorRepresentationDouble();
            Double[] featureAssignment = ((NumericalInstance)centroids[i]).getDataVector();
            
            double sum = 0.0;
            for (int j=0; j<featureAssignment.length; j++)
            {
                sum += featureAssignment[j];
            }
            
            if (sum != 0.0)
            {
                for (int j=0; j<featureAssignment.length; j++)
                {
                    featureAssignment[j] /= sum;
                }
            }
            else
            {
                for (int j=0; j<featureAssignment.length; j++)
                {
                    featureAssignment[j] = ((double)1.0)/featureAssignment.length;
                }                
            }
            
            clusters[i] = new ProjectiveCluster(this.ensemble.getInstances(), objectAssignment, featureAssignment, -(i+1), true, true);
        }
        
        this.result = new ProjectiveClustering(clusters, -1);
        return this.result;
    }
    
    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }
    

    protected NumericalInstanceDataset buildNumericalInstanceDatasetFromObjectByFeatureMatrix()
    {
        double[][] m = this.ensemble.getObjectByFeatureMatrix();
        
        Instance[] data = new Instance[m.length];
        for (int i=0; i<m.length; i++)
        {
            Double[] features = new Double[m[i].length];
            for (int j=0; j<features.length; j++)
            {
                features[j] = new Double(m[i][j]);
            }
            
            data[i] = new NumericalInstance(features,i);
        }
        
        return new NumericalInstanceDataset(data, null);        
    }

}



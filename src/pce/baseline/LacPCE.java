package pce.baseline;

import clustering.LAC;
import dataset.Dataset;
import dataset.NumericalInstanceDataset;
import dataset.ProjectiveClusteringDataset;
import evaluation.numericalinstance.MinkowskiNumericalInstanceSim;
import objects.Clustering;
import objects.Instance;
import objects.ProjectiveClustering;
import pce.PCEMethod;
import test.GenerateEnsemble;

public class LacPCE extends PCEMethod
{
    protected ProjectiveClustering result;
    protected double h;
    protected Dataset data;

    public LacPCE (ProjectiveClusteringDataset ensemble, Dataset data)
    {
        this.ensemble = ensemble;
        this.h = 0.2;
        this.data = data;
    }
    
    public LacPCE (ProjectiveClusteringDataset ensemble, NumericalInstanceDataset data, double h)
    {
        this.ensemble = ensemble;
        this.h = h;
        this.data = data;
    }
    
    

    public ProjectiveClustering execute (int nClusters) 
    {
        long start = System.currentTimeMillis();
        
        LAC lac = new LAC(((NumericalInstanceDataset)this.data),this.h);
        Clustering clust = lac.execute(new MinkowskiNumericalInstanceSim(2), nClusters);
        double[][] w = lac.getWeights();
        Instance[] c = lac.getCentroids();
        
        this.result = GenerateEnsemble.softSoftLACProjectiveClusteringComputing(clust, w, c);

        this.onlineExecutionTime = System.currentTimeMillis()-start;

        return this.result;
    }


    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }

}


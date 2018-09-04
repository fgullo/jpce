package clustering;

import dataset.Dataset;
import evaluation.Similarity;
import objects.FuzzyClustering;

public abstract class FuzzyClusteringMethod {

    protected Dataset dataset;
    
    protected double[][] distMatrix;

    /**
     *  <p style="margin-top: 0">
     *        The stop criterion is automatically determined by the algorithm.
     *      </p>
     */
    public abstract FuzzyClustering execute (Similarity sim);

    /**
     *  <p style="margin-top: 0">
     *        The algorithm ends when the number of clusters specified by nClusters is 
     *        reached.
     *      </p>
     */
    public abstract FuzzyClustering execute (Similarity sim, int nClusters);
    
    public abstract FuzzyClustering execute (double[][] distMatrix);
    
    public abstract FuzzyClustering execute (double[][] distMatrix, int nClusters);

    public FuzzyClustering[] executeMultiRun (Similarity sim, int runs) 
    {
        FuzzyClustering[] ret = new FuzzyClustering[runs];
        
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = execute(sim);
            System.out.print((i+1)+" ");
        }
        System.out.println();
        
        return ret;
    }

    public FuzzyClustering[] executeMultiRun (Similarity sim, int nClusters, int runs) 
    {
        FuzzyClustering[] ret = new FuzzyClustering[runs];
        
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = execute(sim,nClusters);
            System.out.print((i+1)+" ");
        }
        System.out.println();
        
        return ret;
    }
 
    public FuzzyClustering[] executeMultiRun (double[][] distMatrix, int runs) 
    {
        FuzzyClustering[] ret = new FuzzyClustering[runs];
        
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = execute(distMatrix);
            System.out.print((i+1)+" ");
        }
        System.out.println();
        
        return ret;
    }
    
    public FuzzyClustering[] executeMultiRun (double[][] distMatrix, int nClusters, int runs) 
    {
        FuzzyClustering[] ret = new FuzzyClustering[runs];
        
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = execute(distMatrix,nClusters);
            System.out.print((i+1)+" ");
        }
        System.out.println();
        
        return ret;
    }

    public Dataset getDataset () 
    {
        return dataset;
    }

}

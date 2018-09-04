package clustering;

import dataset.Dataset;
import evaluation.Similarity;
import objects.Clustering;

public abstract class ClusteringMethod {

    protected Dataset dataset;
    
    protected double[][] simMatrix;

    /**
     *  <p style="margin-top: 0">
     *        The stop criterion is automatically determined by the algorithm.
     *      </p>
     */
    public abstract Clustering execute (Similarity sim);

    /**
     *  <p style="margin-top: 0">
     *        The algorithm ends when the number of clusters specified by nClusters is 
     *        reached.
     *      </p>
     */
    public abstract Clustering execute (Similarity sim, int nClusters);
    
    public abstract Clustering execute (double[][] simMatrix);
    
    public abstract Clustering execute (double[][] simMatrix, int nClusters);

    public Clustering[] executeMultiRun (Similarity sim, int runs) 
    {
        Clustering[] ret = new Clustering[runs];
        
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = execute(sim);
            System.out.print((i+1)+" ");
        }
        System.out.println();
        
        return ret;
    }

    public Clustering[] executeMultiRun (Similarity sim, int nClusters, int runs) 
    {
        Clustering[] ret = new Clustering[runs];
        
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = execute(sim,nClusters);
            System.out.print((i+1)+" ");
        }
        System.out.println();
        
        return ret;
    }
 
    public Clustering[] executeMultiRun (double[][] simMatrix, int runs) 
    {
        Clustering[] ret = new Clustering[runs];
        
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = execute(simMatrix);
            System.out.print((i+1)+" ");
        }
        System.out.println();
        
        return ret;
    }
    
    public Clustering[] executeMultiRun (double[][] simMatrix, int nClusters, int runs) 
    {
        Clustering[] ret = new Clustering[runs];
        
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = execute(simMatrix,nClusters);
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


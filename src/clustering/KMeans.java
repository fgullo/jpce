package clustering;

import dataset.Dataset;
import evaluation.Similarity;
import objects.centroid.CentroidComputation;
import objects.Clustering;
import objects.Instance;
import objects.Cluster;
import java.util.ArrayList;

public class KMeans extends ClusteringMethod {

    protected CentroidComputation centroidComputation;
    protected int maxIterations = 50;
    protected Instance[] cent;

    public KMeans (Dataset data, CentroidComputation cc)
    {
        this.dataset = data;
        this.centroidComputation = cc;
        this.simMatrix = null;
    }
    
    public CentroidComputation getCentroidComputation () 
    {
        return centroidComputation;
    }

    /**
     *  <p style="margin-top: 0">
     *    The stop criterion is automatically determined by the algorithm.
     *      </p>
     */
    public Clustering execute (Similarity sim) 
    {
        return execute(sim,2);
    }
    
    public Clustering execute (double[][] simMatrix) 
    {
        throw new RuntimeException("Such a method can not be invoked!");
    }

    
    public Clustering execute (double[][] simMatrix, int nClusters) 
    {
        throw new RuntimeException("Such a method can not be invoked!");
    }

    /**
     *  <p style="margin-top: 0">
     *    The algorithm ends when the number of clusters specified by nClusters is 
     *    reached.
     *      </p>
     */
    public Clustering execute (Similarity sim, int nClusters) 
    {        
        Instance[] centroids = randomInitialCentroids(nClusters);
        Cluster[] clusters = new Cluster[nClusters];
        
        
        Instance[] data = dataset.getData();
        int[] assignment = new int[data.length];
        int[] clusterSizes = new int[centroids.length];
        for (int i=0; i<assignment.length; i++)
        {
            assignment[i] = -1;
        }
        
        boolean finito = false;
        int it=1;
        
        while (!finito && it<=this.maxIterations)
        {
            finito = true;
            for (int i=0; i<clusterSizes.length; i++)
            {
                clusterSizes[i] = 0;
            }
            
            //assignment phase
            for (int i=0; i<data.length; i++)
            {
                Instance curr = data[i];
                double minDist = Double.POSITIVE_INFINITY;
                int assI = -1;

                for(int j=0; j<centroids.length; j++)
                {
                    if (centroids[j] != null)
                    {
                        double distCorr = sim.getDistance(curr, centroids[j]);
                        if ( distCorr <= minDist)
                        {
                            minDist = distCorr;
                            assI = j;
                        }
                    }
                }
                
                if (assI != assignment[i])
                {
                    finito = false;
                    assignment[i] = assI;
                }
                clusterSizes[assI]++;
            }
            
            //computing centroids
            for (int i=0; i<clusterSizes.length; i++)
            {
                if (clusterSizes[i] == 0)
                {
                    centroids[i] = null;
                    Instance[] cI = new Instance[0];
                    Cluster cl = new Cluster(cI,i,this.centroidComputation,dataset.getDataLength());
                    clusters[i] = cl;
                }
                else
                {
                    Instance[] cI = new Instance[clusterSizes[i]];
                    int j=0;

                    for (int k=0; k<assignment.length; k++)
                    {
                        if (assignment[k] == i)
                        {
                            cI[j] = data[k];
                            j++;
                        }
                    }

                    Instance centroid = this.centroidComputation.getCentroid(cI);
                    centroids[i] = centroid;

                    Cluster cl = new Cluster(cI,i,this.centroidComputation,dataset.getDataLength());
                    clusters[i] = cl;
                }
            }
            
            it++;
        }
        
        this.cent = centroids;
        
        //System.out.println("K-Means --- Number of iterations="+it);
        
        //build Clustering
        return new Clustering(clusters);
    }

    protected Instance[] randomInitialCentroids(int nClusters)
    {
        Instance[] data = dataset.getData();
        boolean[] chosen = new boolean[data.length];
        for (int i=0; i<chosen.length; i++)
        {
            chosen[i] = false;
        }
        
        Instance[] centroids = new Instance[nClusters];
        
        for (int i=0; i<nClusters; i++)
        {
            int x=-1;
            do
            {
                x = (int)Math.rint(Math.random()*(data.length-1)); 
            }
            while(chosen[x]);
            
            chosen[x] = true;
            centroids[i] = data[x];
        }
        
        return centroids;           
    }
    
    public Instance[] getCentroids()
    {
        return this.cent;
    }
}


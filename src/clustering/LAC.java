package clustering;
 
import dataset.NumericalInstanceDataset;
import evaluation.Similarity;
import evaluation.numericalinstance.WeightedMinkowskiNumericalInstanceSim;
import objects.centroid.CentroidComputation;
import objects.Clustering;
import objects.Instance;
import objects.Cluster;
import objects.NumericalInstance;
import objects.centroid.NumericalInstanceCentroidComputationAVG;


public class LAC extends ClusteringMethod 
{
    protected CentroidComputation centroidComputation;
    protected int maxIterations = 50;
    protected double[][] weights;
    protected Instance[] centroids;
    protected double h;

    public LAC (NumericalInstanceDataset data, double h)
    {
        this.dataset = data;
        this.centroidComputation = new NumericalInstanceCentroidComputationAVG(); 
        this.simMatrix = null;
        this.h = h;
    }
    
    public CentroidComputation getCentroidComputation () 
    {
        return centroidComputation;
    }
    
    public double[][] getWeights()
    {
        return this.weights;
    }
    
    public Instance[] getCentroids()
    {
        return this.centroids;
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
    public Clustering execute (Similarity sim1, int nClusters) 
    {        
        //sim: ignored
        
        this.weights = new double[nClusters][dataset.getNumberOfFeatures()];
        for (int i=0; i<this.weights.length; i++)
        {
            for (int j=0; j<this.weights[i].length; j++)
            {
                weights[i][j] = ((double)1.0)/dataset.getNumberOfFeatures();
            }
        }
        
        this.centroids = randomInitialCentroids(nClusters);
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
                        Similarity sim = new WeightedMinkowskiNumericalInstanceSim(2,weights[j]);
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
            
            //computing weights
            for (int j=0; j<clusterSizes.length; j++)
            {
                if (clusterSizes[j] == 0)
                {
                     for (int i=0; i<weights[j].length; i++)
                     {
                         weights[j][i] = ((double)1.0)/dataset.getNumberOfFeatures();
                     }
                }
                else
                {
                    Instance[] Sj = new Instance[clusterSizes[j]];
                    int z=0;

                    for (int k=0; k<assignment.length; k++)
                    {
                        if (assignment[k] == j)
                        {
                            Sj[z] = data[k];
                            z++;
                        }
                    }

                    double den = 0.0;
                    double tmpSum = 0.0;
                    double[] Xj = new double[weights[0].length];
                    for (int i=0; i<weights[0].length; i++)
                    {
                        double Xji = 0;
                        for (int k=0; k<Sj.length; k++)
                        {
                            Xji += Math.pow((((NumericalInstance)centroids[j]).getDataVector()[i]-((NumericalInstance)Sj[k]).getDataVector()[i]),2);
                        }
                        Xji/=Sj.length;
                        Xj[i] = Xji;
                        tmpSum += Xj[i];

                        den+=Math.exp(-Xji/this.h);
                    }

                    
                    if (den == 0.0)
                    {
                        double tmpSum2 = 0.0;
                        for (int i=0; i<Xj.length; i++)
                        {
                            Xj[i] /= tmpSum;
                            tmpSum2 += Math.exp(-Xj[i]/this.h);
                        }
                        
                        den = tmpSum2;
                    }
                    
                    //if (den > 0.0)
                    //{
                        for (int i=0; i<Xj.length; i++)
                        {
                            double Xji = Xj[i];                    
                            double num = Math.exp(-Xji/this.h);
                            weights[j][i] = num/den;                    
                        }
                    //}
                    /*
                    else
                    {
                        for (int i=0; i<Xj.length; i++)
                        { 
                            weights[j][i] = ((double)1.0)/Xj.length;                    
                        }                        
                    }
                    */ 
                }
            }
            
            //re-assignment phase
            //finito = true;
            for (int i=0; i<clusterSizes.length; i++)
            {
                clusterSizes[i] = 0;
            }
            
            for (int i=0; i<data.length; i++)
            {
                Instance curr = data[i];
                double minDist = Double.POSITIVE_INFINITY;
                int assI = -1;

                for(int j=0; j<centroids.length; j++)
                {
                    if (centroids[j] != null)
                    {
                        Similarity sim = new WeightedMinkowskiNumericalInstanceSim(2,weights[j]);
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
        
        //System.out.println("LAC --- Number of iterations="+it);
        
        
        for (int i=0; i<this.weights.length; i++)
        {
            double sumCheck = 0.0;
            for (int j=0; j<this.weights[i].length; j++)
            {
                sumCheck += this.weights[i][j];
            }
            
            if (sumCheck < 0.9999999 || sumCheck > 1.0000001)
            {
                throw new RuntimeException("ERROR: sumCheck must be equal to 1----sumCheck="+sumCheck+" ("+(i+1)+"-th cluster");
            }
        }
        
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
        
        Instance[] centroidsR = new Instance[nClusters];
        
        for (int i=0; i<nClusters; i++)
        {
            int x=-1;
            do
            {
                x = (int)Math.rint(Math.random()*(data.length-1)); 
            }
            while(chosen[x]);
            
            chosen[x] = true;
            centroidsR[i] = data[x];
        }
        
        return centroidsR;           
    }
}


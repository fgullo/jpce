package clustering;

import dataset.Dataset;
import evaluation.Similarity;
import objects.centroid.FuzzyCentroidComputation;
import objects.Instance;
import objects.FuzzyCluster;
import objects.FuzzyClustering;


public class FuzzyCMeans extends FuzzyClusteringMethod {

    protected FuzzyCentroidComputation centroidComputation;
    protected int maxIterations = 100;
    protected int m = 2;
    protected Instance[] centroids;
    
    protected double epsilon = 0.00000001;

    public FuzzyCMeans(Dataset data, FuzzyCentroidComputation cc)
    {
        this.dataset = data;
        this.centroidComputation = cc;
    }

    /**
     *  <p style="margin-top: 0">
     *    The stop criterion is automatically determined by the algorithm.
     *      </p>
     */
    public FuzzyClustering execute (Similarity sim) 
    {
        return execute(sim,2);
    }
    
    public FuzzyClustering execute (double[][] simMatrix) 
    {
        throw new RuntimeException("Such a method can not be invoked!");
    }

    
    public FuzzyClustering execute (double[][] simMatrix, int nClusters) 
    {
        throw new RuntimeException("Such a method can not be invoked!");      
    }


    public FuzzyClustering execute (Similarity sim, int nClusters) 
    {        
        //checkNaNandInftyInDistanceMatrix();
        
        Instance[] data = this.dataset.getData();
        centroids = randomInitialCentroids(nClusters);
        
        double[][] objectToClusterAssignments = new double[nClusters][this.dataset.getDataLength()];
        boolean finito = false;
        int it=1;
        double previousError = Double.POSITIVE_INFINITY;
        
        while (!finito && it<=this.maxIterations)
        {
            finito = true;
            
            //update uij
            for (int i=0; i<objectToClusterAssignments[0].length; i++)
            {   
                double dist[] = new double[objectToClusterAssignments.length];
                int exactAssignment = -1;
                for (int j=0; j<dist.length; j++)
                {
                    dist[j] = sim.getDistance(data[i], centroids[j]);                    
                    if (dist[j] == 0.0)
                    {
                        exactAssignment = j;
                    }
                }
                
                if (exactAssignment != -1)
                {
                    double newAssignment = 1.0;
                    if (Math.abs(newAssignment-objectToClusterAssignments[exactAssignment][i]) > 0.00000001)
                    {
                        finito = false;
                    }
                    
                    for (int j=0; j<objectToClusterAssignments.length; j++)
                    {
                        objectToClusterAssignments[j][i] = 0.0;
                    }
                    objectToClusterAssignments[exactAssignment][i] = 1.0;
                }
                else
                {
                    double sum = 0.0;
                    for (int j=0; j<objectToClusterAssignments.length; j++)
                    {
                        if (dist[j] == 0.0)
                        {
                            throw new RuntimeException("ERROR: dist[j] should not be equal to zero");
                        }
                        sum += Math.pow(((double)1.0)/dist[j],((double)1.0)/(this.m-1));
                    }
                    
                    for (int j=0; j<objectToClusterAssignments.length; j++)
                    {
                        
                        double newAssignment = ((double)1.0)/((Math.pow(dist[j], ((double)1.0)/(this.m-1)))*sum);
                        if (Math.abs(newAssignment-objectToClusterAssignments[j][i]) > 0.00000001)
                        {
                            finito = false;
                        }
                        objectToClusterAssignments[j][i] = newAssignment;
                    }
                }
            }
            
            for (int j=0; j<centroids.length; j++)
            {
                this.centroidComputation.setAssignments(objectToClusterAssignments[j]);
                centroids[j] = this.centroidComputation.getCentroid(data);
            }
            
            double error = computeError(objectToClusterAssignments,sim);
            
            if (error > previousError)
            {
                //throw new RuntimeException("ERROR: the algorithm does not converge---it="+it+",error="+error+",previousError="+previousError);
            }
            
            if (!Double.isInfinite(previousError) && previousError-error <= this.epsilon)
            {
                finito = true;
            }
            else
            {
                previousError = error;
                finito = false;
            }
            
            it++;
        }
        
        
        FuzzyCluster[] clusters = new FuzzyCluster[nClusters];
        
        for (int j=0; j<clusters.length; j++)
        {
            Double[] assignment = new Double[objectToClusterAssignments[j].length];
            for (int i=0; i<assignment.length; i++)
            {
                assignment[i] = new Double(objectToClusterAssignments[j][i]);
            }
            
            clusters[j] = new FuzzyCluster(data, assignment, j);
        }
        
        System.out.println("Fuzzy C-Means---NumberOfIterations="+(it-1));
        
        return new FuzzyClustering(clusters);        
    }

    protected Instance[] randomInitialCentroids(int nClusters)
    {
        Instance[] data = dataset.getData();
        boolean[] chosen = new boolean[data.length];
        for (int i=0; i<chosen.length; i++)
        {
            chosen[i] = false;
        }
        
        Instance[] c = new Instance[nClusters];
        
        for (int i=0; i<nClusters; i++)
        {
            int x=-1;
            do
            {
                x = (int)Math.rint(Math.random()*(data.length-1)); 
            }
            while(chosen[x]);
            
            chosen[x] = true;
            c[i] = data[x];
        }
        
        return c;           
    }

    
    public Instance[] getCentroids()
    {
        return this.centroids;
    }
    
    protected double computeError(double[][] matrix, Similarity sim)
    {
        double err = 0.0;
        
        for (int i=0; i<matrix.length; i++)
        {
            for (int j=0; j<matrix[i].length; j++)
            {
                double dist = sim.getDistance(this.dataset.getData()[j], this.centroids[i]);
                err += Math.pow(matrix[i][j], this.m)*dist;
            }
        }
        
        return err;
    }
    
    protected void checkNaNandInftyInDistanceMatrix()
    {
        int tot = 0;
        int nan = 0;
        int inf = 0;
        
        for (int i=0; i<this.distMatrix.length-1; i++)
        {
            for (int j=i; j<this.distMatrix[i].length; j++)
            {
                tot++;
                if (Double.isNaN(this.distMatrix[i][j]))
                {
                    nan++;
                }
                if (Double.isInfinite(this.distMatrix[i][j]))
                {
                    inf++;
                }
            }
        }
        
        String s = "TOT: "+tot+"  NAN: "+nan+"  INF:"+inf;
        System.out.println(s);
    }
}



















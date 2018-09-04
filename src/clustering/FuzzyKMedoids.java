package clustering;

import dataset.Dataset;
import evaluation.Similarity;
import objects.Instance;
import objects.FuzzyCluster;
import objects.FuzzyClustering;


public class FuzzyKMedoids extends FuzzyClusteringMethod {

    protected int maxIterations = 50;
    protected int m = 2;

    public FuzzyKMedoids(Dataset data)
    {
        this.dataset = data;
        this.distMatrix = null;
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
    
    public FuzzyClustering execute (double[][] distMatrix) 
    {
        return execute(distMatrix, 2);
    }

    
    public FuzzyClustering execute (double[][] distMatrix, int nClusters) 
    {
        this.distMatrix = distMatrix;        
        
        return runAlgorithm(nClusters);        
    }


    public FuzzyClustering execute (Similarity sim, int nClusters) 
    {        
        distMatrix = new double[dataset.getDataLength()][dataset.getDataLength()];
        double[][] simDataset = dataset.getDistMatrix(sim);
        
        for (int i=0; i<distMatrix.length; i++)
        {
            for (int j=0; j<distMatrix[i].length; j++)
            {
                distMatrix[i][j] = simDataset[i][j];
            }
        }
        
        return runAlgorithm(nClusters);
    }
    
    protected FuzzyClustering runAlgorithm(int nClusters)
    {        
        //checkNaNandInftyInDistanceMatrix();
        
        Instance[] medoids = randomInitialMedoids(nClusters);
        
        double previousError = Double.POSITIVE_INFINITY;
        
        double[][] objectToClusterAssignments = new double[this.dataset.getDataLength()][nClusters];
        boolean finito = false;
        int it=1;
        
        boolean finito2 = false;
        
        //while (!finito2 && it<=this.maxIterations)
        while (!finito && it<=this.maxIterations)
        {
            //update uij
            for (int i=0; i<objectToClusterAssignments.length; i++)
            {
                int id1 = i;
                double sum = 0.0;
                int exactAssignment = -1;
                for (int j=0; j<objectToClusterAssignments[i].length; j++)
                {
                    int id2 = medoids[j].getID();
                    double dist = (id1<id2)?this.distMatrix[id1][id2]:this.distMatrix[id2][id1];
                    
                    if (dist == 0)
                    {
                        objectToClusterAssignments[i][j] = Double.POSITIVE_INFINITY;
                        exactAssignment = j;
                    }
                    else
                    {
                        objectToClusterAssignments[i][j] = Math.pow(((double)1.0)/dist,((double)1.0)/(this.m-1));
                    }
                    
                    sum += objectToClusterAssignments[i][j];
                }
                
                if (exactAssignment == -1)
                {
                    for (int j=0; j<objectToClusterAssignments[i].length; j++)
                    {
                        objectToClusterAssignments[i][j] /= sum;
                    }
                }
                else
                {
                    for (int j=0; j<objectToClusterAssignments[i].length; j++)
                    {
                        if (j == exactAssignment)
                        {
                            objectToClusterAssignments[i][j] = 1.0;
                        }
                        else
                        {
                            objectToClusterAssignments[i][j] = 0.0;
                        }
                    }               
                }
            }
            
            /*
            double currentError = 0.0;
            for (int i=0; i<objectToClusterAssignments.length; i++)
            {
                int id1 = i;
                for (int j=0; j<objectToClusterAssignments[i].length; j++)
                {
                    int id2 = medoids[j].getID();
                    double dist = (id1<id2)?this.distMatrix[id1][id2]:this.distMatrix[id2][id1];
                    currentError += Math.pow(objectToClusterAssignments[i][j],this.m)*dist;
                }
            }
            
            if (Math.abs(currentError-previousError)<0.0000001)
            {
                finito2 = true;
            }
            
            System.out.println("ERROR: "+currentError);
            previousError = currentError;
            */
            
            //store old medoids
            Instance[] oldMedoids = new Instance[medoids.length];
            for (int i=0; i<medoids.length; i++)
            {
                oldMedoids[i] = medoids[i];
            }
            
            //compute new medoids
            medoids = recomputeMedoids(objectToClusterAssignments);
            
            finito = true;
            for (int i=0; i<medoids.length && finito; i++)
            {
                boolean match = false;
                for (int j=0; j<oldMedoids.length && !match; j++)
                {
                    if (medoids[i].equals(oldMedoids[j]))
                    {
                        match = true;
                    }
                }
                
                if (!match)
                {
                    finito = false;
                }
            }
            
            it++;
        }
        
        
        FuzzyCluster[] clusters = new FuzzyCluster[nClusters];
        
        
        Instance[] data = dataset.getData();
        for (int i=0; i<objectToClusterAssignments[0].length; i++)
        {
            Double[] assignment = new Double[objectToClusterAssignments.length];
            for (int j=0; j<assignment.length; j++)
            {
                assignment[j] = objectToClusterAssignments[j][i];
            }
            
            clusters[i] = new FuzzyCluster(data, assignment, i);
        }
        
        //System.out.println("Fuzzy K-Medoids---NumberOfIterations="+(it-1));
        
        return new FuzzyClustering(clusters);
    }

    protected Instance[] randomInitialMedoids(int nClusters)
    {
        Instance[] data = dataset.getData();
        boolean[] chosen = new boolean[data.length];
        for (int i=0; i<chosen.length; i++)
        {
            chosen[i] = false;
        }
        
        Instance[] medoids = new Instance[nClusters];
        
        for (int i=0; i<nClusters; i++)
        {
            int x=-1;
            do
            {
                x = (int)Math.rint(Math.random()*(data.length-1)); 
            }
            while(chosen[x]);
            
            chosen[x] = true;
            medoids[i] = data[x];
        }
        
        return medoids;           
    }
    
    protected Instance[] recomputeMedoids(double[][] objectToClusterAssignments)
    {
        Instance[] newMedoids = new Instance[objectToClusterAssignments[0].length];
        Instance[] data = this.dataset.getData();
        
        
        for (int i=0; i<newMedoids.length; i++)
        {
            double minSum = Double.POSITIVE_INFINITY;
            int index = -1;
            
            for (int j=0; j<data.length; j++)
            {
                double sum = 0.0;
                for (int k=0; k<data.length; k++)
                {
                    double dist = (j<k)?this.distMatrix[j][k]:this.distMatrix[k][j];
                    if (Double.isInfinite(dist) || Double.isNaN(dist))
                    {
                        throw new RuntimeException("ERROR: dist value is NAN or INFINITY");
                    }
                    sum += Math.pow(objectToClusterAssignments[k][i],this.m)*dist;
                }
                
                if (sum < minSum)
                {
                    minSum = sum;
                    index = j;
                }
            }
            
            if (index == -1)
            {
                System.out.println();
            }
            
            newMedoids[i] = data[index];
        }
        
        return newMedoids;
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



















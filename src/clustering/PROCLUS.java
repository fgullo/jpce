package clustering;
 
import dataset.NumericalInstanceDataset;
import evaluation.Similarity;
import java.util.List;
import objects.*;
import weka.subspaceClusterer.Proclus;
import weka.subspaceClusterer.SubspaceClusterEvaluation;
import weka.subspaceClusterer.SubspaceClusterer;


public class PROCLUS extends ClusteringMethod 
{
    protected String inputPath;
    protected int d;
    protected double[][] weights;


    public PROCLUS (NumericalInstanceDataset data, String inputPath, int d)
    {
        this.dataset = data;
        this.inputPath = inputPath;
        this.d = d;
    }
    
    public double[][] getWeights()
    {
        return this.weights;
    }

    public Clustering execute (Similarity sim1, int nClusters) 
    {        
        //sim1: ignored        
        SubspaceClusterer sc = new Proclus();
        String[] options = new String[]{"-t",this.inputPath,"-K",""+nClusters,"-D",""+this.d};
        /*
        String[] options = new String[3];
        options[0] = "-t "+this.inputPath;
        options[1] = "-K "+nClusters;
        options[2] = "-D "+this.d;
        */
        try
        {
            SubspaceClusterEvaluation.evaluateClusterer(sc, options);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        List<i9.subspace.base.Cluster> clusters = sc.getSubspaceClustering();
        
        Clustering result = createClustering(clusters);

        return result;

    }


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
    
    protected Clustering createClustering(List<i9.subspace.base.Cluster> sClusters) 
    {
        Cluster[] clusters = new Cluster[sClusters.size()];
        int[] assignments = new int[this.dataset.getDataLength()];
        for (int i=0; i<assignments.length; i++)
        {
            assignments[i] = -1;
        }
        
        int k=0;
        for (i9.subspace.base.Cluster c:sClusters)
        { 
            if (c.m_subspace.length != this.dataset.getNumberOfFeatures())
            {
                throw new RuntimeException("ERROR: x and y must be equal---x="+c.m_subspace.length+", y="+this.dataset.getNumberOfFeatures());
            }
            for(int x:c.m_objects)
            {
                assignments[x] = k;
            }
            k++;
        }
        
        for (int i=0; i<assignments.length; i++)
        {
            if (assignments[i] == -1)
            {
                //Proclus did not assign this object to any cluster => random assignment
                int a = (int)Math.round(Math.random()*(sClusters.size()-1));
                assignments[i] = a;
                
                if (a < 0 || a >= clusters.length)
                {
                    throw new RuntimeException("ERROR: cluster assignment not valid---assignment="+a);
                }
            }
        }
        
        int check = 0;
        for (int i=0; i<assignments.length; i++)
        {
            if (assignments[i] >= 0 && assignments[i] < clusters.length)
            {
                check++;
            }
        }
        
        if (check != this.dataset.getDataLength())
        {
            System.out.println("");
        }
        
        
        
        k=0;
        this.weights = new double[sClusters.size()][this.dataset.getNumberOfFeatures()];
        for (i9.subspace.base.Cluster c:sClusters)
        {
            int size = 0;
            for (int i=0; i<assignments.length; i++)
            {
                if (assignments[i] == k)
                {
                    size++;
                }
            }
            Instance[] cI = new Instance[size];
            int i=0;
            for (int j=0; j<assignments.length; j++)
            {
                if (assignments[j] == k)
                {
                    cI[i++] = dataset.getData()[j];
                }
            }
            
            for(int j=0; j<c.m_subspace.length; j++)
            {
                if (c.m_subspace[j])
                {
                    weights[k][j] = 1.0;
                }
            }
            
            Cluster cl = new Cluster(cI,k,dataset.getDataLength());
            clusters[k] = cl;
            k++;
        }
        
        return new Clustering(clusters);
    }
}


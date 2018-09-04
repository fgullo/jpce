package pce.baseline;

import dataset.ProjectiveClusteringDataset;
import i9.subspace.base.Cluster;
import java.util.List;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.PCEMethod;
import weka.subspaceClusterer.Proclus;
import weka.subspaceClusterer.SubspaceClusterEvaluation;
import weka.subspaceClusterer.SubspaceClusterer;

public class ProclusPCE extends PCEMethod
{
    protected ProjectiveClustering result;
    protected String inputPath;
    protected int d;

    public ProclusPCE (ProjectiveClusteringDataset ensemble, String inputPath, int d)
    {
        this.ensemble = ensemble;
        this.inputPath = inputPath;
        this.d = d;
    }
    
    public ProclusPCE (ProjectiveClusteringDataset ensemble, String inputPath)
    {
        this.ensemble = ensemble;
        this.inputPath = inputPath;
        this.d = this.ensemble.getAvgNUmberOfFeaturesInProjectiveRefPartition();
    }
    
    

    public ProjectiveClustering execute (int nClusters) 
    {
        long start = System.currentTimeMillis();

        if (nClusters != this.ensemble.getNumberOfClustersInEachClustering())
        {
            System.out.println("WARNING: the number of clusters is different from the number of clusters in each clustering of the ensemble");
        }
        
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
        
        List<Cluster> clusters = sc.getSubspaceClustering();
        
        this.result = createProjectiveClustering(clusters);

        this.onlineExecutionTime = System.currentTimeMillis()-start;

        return this.result;
    }


    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }

    protected ProjectiveClustering createProjectiveClustering(List<Cluster> sClusters) 
    {
        ProjectiveCluster[] clusters = new ProjectiveCluster[sClusters.size()];
        int[] assignments = new int[this.ensemble.getNumberOfInstances()];
        for (int i=0; i<assignments.length; i++)
        {
            assignments[i] = -1;
        }
        
        int k=0;
        for (Cluster c:sClusters)
        { 
            if (c.m_subspace.length != this.ensemble.getNumberOfFeaturesInEachCluster())
            {
                throw new RuntimeException("ERROR: x and y must be equal---x="+c.m_subspace.length+", y="+this.ensemble.getNumberOfFeaturesInEachCluster());
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
            }
        }
        
        
        k=0;
        for (Cluster c:sClusters)
        {
            Double[] orep = new Double[this.ensemble.getNumberOfInstances()];
            Double[] frep = new Double[this.ensemble.getNumberOfFeaturesInEachCluster()];
            for (int i=0; i<orep.length; i++)
            {
                orep[i] = 0.0;
            }
            for (int i=0; i<frep.length; i++)
            {
                frep[i] = 0.0;
            }
            
            for (int i=0; i<assignments.length; i++)
            {
                if (assignments[i] == k)
                {
                    orep[i] = 1.0;
                }
            }
            for(int j=0; j<c.m_subspace.length; j++)
            {
                if (c.m_subspace[j])
                {
                    frep[j] = 1.0;
                }
            }
            clusters[k] = new ProjectiveCluster(this.ensemble.getInstances(),orep,frep,k,false,false);
            k++;
        }
        
        return new ProjectiveClustering(clusters, -1);
    }

}


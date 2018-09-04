package clustering;

import dataset.Dataset;
import evaluation.Similarity;
import objects.Clustering;
import objects.Instance;
import objects.Cluster;

public class AHCAverageLinkage extends AHC 
{

    public AHCAverageLinkage (Dataset data, boolean buildDendrogram) 
    {
        this.dataset = data;
        this.simMatrix = null;
        this.buildDendrogram = buildDendrogram;
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
        return execute(simMatrix,2);
    }
    /**
     *  <p style="margin-top: 0">
     *    The algorithm ends when the number of clusters specified by nClusters is 
     *    reached.
     *      </p>
     */
    public Clustering execute (Similarity sim, int nClusters) 
    {
        simMatrix = new double[dataset.getDataLength()][dataset.getDataLength()];
        double[][] simDataset = dataset.getSimMatrix(sim);

        for (int i=0; i<simMatrix.length; i++)
        {
            for (int j=0; j<simMatrix[i].length; j++)
            {
                simMatrix[i][j] = simDataset[i][j];
            }
        }
        
        if (buildDendrogram)
        {
            return runAlgorithmDendrogram(nClusters);
        }
        
        return runAlgorithm(nClusters);
    }
    
    public Clustering execute (double[][] simMatrix, int nClusters) 
    {
        this.simMatrix = simMatrix;
        
        if (buildDendrogram)
        {
            return runAlgorithmDendrogram(nClusters);
        }        
        
        return runAlgorithm(nClusters);
    }
    
    protected Clustering runAlgorithm(int nClusters)
    {
        Object[] clusters = new Object[simMatrix.length];
        for (int i=0; i<clusters.length; i++)
        {
            Instance[] array = new Instance[1];
            array[0] = dataset.getData()[i];
            clusters[i] = array;
        }
        
       
        int nClusterCurr = simMatrix.length;
        
        while (nClusterCurr>nClusters)
        {
            System.out.println("AHC AL: "+nClusterCurr);
            double maxSim = Double.NEGATIVE_INFINITY;
            int index1 = -1;
            int index2 = -1;
            
            for (int i=0; i<simMatrix.length-1; i++)
            {
                if (clusters[i] != null)
                {
                    for (int j=i+1; j<simMatrix.length; j++)
                    {
                        if (clusters[j] != null)
                        {
                            if (simMatrix[i][j] >= maxSim)
                            {
                                maxSim = simMatrix[i][j];
                                index1 = i;
                                index2 = j;
                            }
                        }
                    }
                }
            }
            
            if (index1 == -1 || index2 == -1)
            {
                double r = 0.0;
            }
            
            //merging clusters
            Instance[] i1 = (Instance[])clusters[index1];
            Instance[] i2 = (Instance[])clusters[index2];
            
            Instance[] i12 = new Instance[i1.length+i2.length];
            int z=0;
            for (int i=0; i<i1.length; i++)
            {
                i12[z] = i1[i];
                z++;
            }
            for (int i=0; i<i2.length; i++)
            {
                i12[z] = i2[i];
                z++;
            }
            
            clusters[index1] = i12;
            clusters[index2] = null;
            
            //distances updating
            //row update
            for (int j=index1+1; j<simMatrix.length; j++)
            {
                if (clusters[j] != null)
                {
                    double dOld1 = simMatrix[index1][j];
                    double dOld2 = (j>index2)?simMatrix[index2][j]:simMatrix[j][index2];
                    
                    simMatrix[index1][j] = (dOld1*i1.length+dOld2*i2.length)/(i1.length+i2.length);
                }
            }
            //column update
            {
                for (int i=0; i<index1; i++)
                {
                    if (clusters[i] != null)
                    {
                        double dOld1 = simMatrix[i][index1];
                        double dOld2 = (i>index2)?simMatrix[index2][i]:simMatrix[i][index2];
                        
                        simMatrix[i][index1] = (dOld1*i1.length+dOld2*i2.length)/(i1.length+i2.length);
                    }
                }
            }
            
            
            nClusterCurr--;
        }
        
        //build clustering
        Cluster[] finalClusters = new Cluster[nClusters];
        int idCluster=0;
        for (int i=0; i<clusters.length; i++)
        {
            if (clusters[i] != null)
            {
                Cluster cl = new Cluster(((Instance[])clusters[i]),idCluster,dataset.getDataLength());
                finalClusters[idCluster] = cl;
                idCluster++;
            }
        }  
     
        return new Clustering(finalClusters);        
    }
    
    protected Clustering runAlgorithmDendrogram(int nClusters)
    {
        this.dendrogram = new boolean[simMatrix.length-2][simMatrix.length];
        int currLevel = 0;
        
        Object[] clusters = new Object[simMatrix.length];
        for (int i=0; i<clusters.length; i++)
        {
            Instance[] array = new Instance[1];
            array[0] = dataset.getData()[i];
            clusters[i] = array;
        }
        
       
        int nClusterCurr = simMatrix.length;
        
        while (nClusterCurr>nClusters)
        {
            double maxSim = Double.NEGATIVE_INFINITY;
            int index1 = -1;
            int index2 = -1;
            
            for (int i=0; i<simMatrix.length-1; i++)
            {
                if (clusters[i] != null)
                {
                    for (int j=i+1; j<simMatrix.length; j++)
                    {
                        if (clusters[j] != null)
                        {
                            if (simMatrix[i][j] >= maxSim)
                            {
                                maxSim = simMatrix[i][j];
                                index1 = i;
                                index2 = j;
                            }
                        }
                    }
                }
            }
            
            //merging clusters
            Instance[] i1 = (Instance[])clusters[index1];
            Instance[] i2 = (Instance[])clusters[index2];
            
            //updating dendrogram
            for (int i=0; i<i1.length; i++)
            {
                this.dendrogram[currLevel][i1[i].getID()] = true; 
            }
            for (int i=0; i<i2.length; i++)
            {
                this.dendrogram[currLevel][i2[i].getID()] = true; 
            }
            currLevel++;
            
            Instance[] i12 = new Instance[i1.length+i2.length];
            int z=0;
            for (int i=0; i<i1.length; i++)
            {
                i12[z] = i1[i];
                z++;
            }
            for (int i=0; i<i2.length; i++)
            {
                i12[z] = i2[i];
                z++;
            }
            
            clusters[index1] = i12;
            clusters[index2] = null;
            
            //distances updating
            //row update
            for (int j=index1+1; j<simMatrix.length; j++)
            {
                if (clusters[j] != null)
                {
                    double dOld1 = simMatrix[index1][j];
                    double dOld2 = (j>index2)?simMatrix[index2][j]:simMatrix[j][index2];
                    
                    simMatrix[index1][j] = (dOld1*i1.length+dOld2*i2.length)/(i1.length+i2.length);
                }
            }
            //column update
            {
                for (int i=0; i<index1; i++)
                {
                    if (clusters[i] != null)
                    {
                        double dOld1 = simMatrix[i][index1];
                        double dOld2 = (i>index2)?simMatrix[index2][i]:simMatrix[i][index2];
                        
                        simMatrix[i][index1] = (dOld1*i1.length+dOld2*i2.length)/(i1.length+i2.length);
                    }
                }
            }
 
            nClusterCurr--;
        }
        
        //build clustering
        Cluster[] finalClusters = new Cluster[nClusters];
        int idCluster=0;
        for (int i=0; i<clusters.length; i++)
        {
            if (clusters[i] != null)
            {
                Cluster cl = new Cluster(((Instance[])clusters[i]),idCluster,dataset.getDataLength());
                finalClusters[idCluster] = cl;
                idCluster++;
            }
        }
        
        //build whole dendrogram
        while (currLevel < this.dendrogram.length-1)
        {
            double maxSim = Double.NEGATIVE_INFINITY;
            int index1 = -1;
            int index2 = -1;
            
            for (int i=0; i<simMatrix.length-1; i++)
            {
                if (clusters[i] != null)
                {
                    for (int j=i+1; j<simMatrix.length; j++)
                    {
                        if (clusters[j] != null)
                        {
                            if (simMatrix[i][j] >= maxSim)
                            {
                                maxSim = simMatrix[i][j];
                                index1 = i;
                                index2 = j;
                            }
                        }
                    }
                }
            }
            
            //merging clusters
            Instance[] i1 = (Instance[])clusters[index1];
            Instance[] i2 = (Instance[])clusters[index2];
            
            //updating dendrogram
            for (int i=0; i<i1.length; i++)
            {
                this.dendrogram[currLevel][i1[i].getID()] = true; 
            }
            for (int i=0; i<i2.length; i++)
            {
                this.dendrogram[currLevel][i2[i].getID()] = true; 
            }
            currLevel++;
            
            Instance[] i12 = new Instance[i1.length+i2.length];
            int z=0;
            for (int i=0; i<i1.length; i++)
            {
                i12[z] = i1[i];
                z++;
            }
            for (int i=0; i<i2.length; i++)
            {
                i12[z] = i2[i];
                z++;
            }
            
            clusters[index1] = i12;
            clusters[index2] = null;
            
            //distances updating
            //row update
            for (int j=index1+1; j<simMatrix.length; j++)
            {
                if (clusters[j] != null)
                {
                    double dOld1 = simMatrix[index1][j];
                    double dOld2 = (j>index2)?simMatrix[index2][j]:simMatrix[j][index2];
                    
                    simMatrix[index1][j] = (dOld1*i1.length+dOld2*i2.length)/(i1.length+i2.length);
                }
            }
            //column update
            {
                for (int i=0; i<index1; i++)
                {
                    if (clusters[i] != null)
                    {
                        double dOld1 = simMatrix[i][index1];
                        double dOld2 = (i>index2)?simMatrix[index2][i]:simMatrix[i][index2];
                        
                        simMatrix[i][index1] = (dOld1*i1.length+dOld2*i2.length)/(i1.length+i2.length);
                    }
                }
            }
        }
     
        return new Clustering(finalClusters);        
    }

}


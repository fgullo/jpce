package dataset;

import dataset.loading.DataLoader;
import weighting.WeightingScheme;
import evaluation.cluster.ClusterSimilarity;
import evaluation.clustering.ClusteringSimilarity;
import evaluation.Similarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

public class ClusteringDataset extends Dataset
{
    protected double[][] clusterSimMatrix;

    protected double[][] instanceSimMatrix;

    protected double[][] clusterDistMatrix;

    protected double[][] instanceDistMatrix;

    protected Object[][] clusterDataFeatureMatrix;

    protected Object[][] instanceDataFeatureMatrix;

    protected double diversity;

    protected double[][] coOccurrenceMatrix;

    protected ClusterSimilarity clusterSim;

    protected Similarity instanceSim;

    protected ClusteringSimilarity diversityMeasure;

    protected Cluster[] allClusters;
    
    protected WeightingScheme weightingScheme;

    public ClusteringDataset (Instance[] data, Clustering refPartition)
    {
        this.data=data;
        this.refPartition=refPartition;
        buildClusters();
    }

    public ClusteringDataset (DataLoader dl)
    {
        Object[] inst = dl.load();
        this.data = (Instance[])inst[0];
        this.refPartition = (Clustering)inst[1];
        buildClusters();
    }

    public Object[][] getClusterDataFeatureMatrix ()
    {
        if (this.clusterDataFeatureMatrix == null)
        {
            this.clusterDataFeatureMatrix = new Object[this.getNumberOfClusters()][this.getNumberOfInstances()];

            int indexRow=0;
            for(int i=0; i<data.length; i++)
            {
                Cluster [] CL = ((Clustering)data[i]).getClusters();
                for(int j=0; j<CL.length; j++)
                {
                    clusterDataFeatureMatrix [indexRow]=CL[j].getFeatureVectorRepresentation();
                    indexRow++;
                }
            }
        }

        return clusterDataFeatureMatrix;
    }

    public Object[][] getInstanceDataFeatureMatrix ()
    {
        if (this.instanceDataFeatureMatrix == null)
        {
            Instance[] instArray = ((Clustering)data[0]).getInstances();
            int features = instArray[0].getNumberOfFeatures();

            this.instanceDataFeatureMatrix = new Object[this.getNumberOfInstances()][features];

            for (int i=0; i<instArray.length; i++)
            {
                this.instanceDataFeatureMatrix[instArray[i].getID()] = instArray[i].getFeatureVectorRepresentation();
            }
        }

        return instanceDataFeatureMatrix;
    }

    public double[][] getClusterDistMatrix (ClusterSimilarity sim)
    {
        if (this.clusterDistMatrix == null)
        {
            clusterDistMatrix = new double[this.getNumberOfClusters()][this.getNumberOfClusters()];
            if (this.clusterSim == null || !this.clusterSim.equals(sim))
            {
                this.clusterSim = sim;
            }

            for (int x=0; x<clusterDistMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterDistMatrix[x].length; y++)
                {
                    clusterDistMatrix[x][y] = sim.getDistance(allClusters[x], allClusters[y]);
                }
            }
        }
        else if (this.clusterSim == null || !this.clusterSim.equals(sim))
        {
            this.clusterSim = sim;

            for (int x=0; x<clusterDistMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterDistMatrix[x].length; y++)
                {
                    clusterDistMatrix[x][y] = sim.getDistance(allClusters[x], allClusters[y]);
                }
            }
        }

        return clusterDistMatrix;
    }

    public double[][] getClusterSimMatrix (ClusterSimilarity sim)
    {
        if (this.clusterSimMatrix == null)
        {
            clusterSimMatrix = new double[this.getNumberOfClusters()][this.getNumberOfClusters()];
            if (this.clusterSim == null || !this.clusterSim.equals(sim))
            {
                this.clusterSim = sim;
            }

            for (int x=0; x<clusterSimMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterSimMatrix[x].length; y++)
                {
                    clusterSimMatrix[x][y] = sim.getSimilarity(allClusters[x], allClusters[y]);
                }
            }
        }
        else if (this.clusterSim == null || !this.clusterSim.equals(sim))
        {
            this.clusterSim = sim;

            for (int x=0; x<clusterSimMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterSimMatrix[x].length; y++)
                {
                    clusterSimMatrix[x][y] = sim.getSimilarity(allClusters[x], allClusters[y]);
                }
            }
        }

        return clusterSimMatrix;
    }


    public double[][] getInstanceDistMatrix (Similarity sim)
    {
        if (this.instanceDistMatrix == null)
        {
            instanceDistMatrix = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
            if (this.instanceSim == null || !this.instanceSim.equals(sim))
            {
                this.instanceSim = sim;
            }

            Instance[] instances = ((Clustering)data[0]).getInstances();

            for (int i=0; i<instanceDistMatrix.length-1; i++)
            {
                for (int j=i+1; j<instanceDistMatrix[i].length; j++)
                {
                    instanceDistMatrix[i][j] = sim.getDistance(instances[i], instances[j]);
                }
            }
        }
        else if (this.instanceSim == null || !this.instanceSim.equals(sim))
        {
            this.instanceSim = sim;

            Instance[] instances = ((Clustering)data[0]).getInstances();

            for (int i=0; i<instanceDistMatrix.length-1; i++)
            {
                for (int j=i+1; j<instanceDistMatrix[i].length; j++)
                {
                    instanceDistMatrix[i][j] = sim.getDistance(instances[i], instances[j]);
                }
            }
        }

        return instanceDistMatrix;
    }

    public double[][] getInstanceSimMatrix (Similarity sim)
    {
        if (this.instanceSimMatrix == null)
        {
            instanceSimMatrix = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
            if (this.instanceSim == null || !this.instanceSim.equals(sim))
            {
                this.instanceSim = sim;
            }

            Instance[] instances = ((Clustering)data[0]).getInstances();

            for (int i=0; i<instanceSimMatrix.length-1; i++)
            {
                for (int j=i+1; j<instanceSimMatrix[i].length; j++)
                {
                    instanceSimMatrix[i][j] = sim.getSimilarity(instances[i], instances[j]);
                }
            }
        }
        else if (this.instanceSim == null || !this.instanceSim.equals(sim))
        {
            this.instanceSim = sim;

            Instance[]instances = ((Clustering)data[0]).getInstances();

            for (int i=0; i<instanceSimMatrix.length-1; i++)
            {
                for (int j=i+1; j<instanceSimMatrix[i].length; j++)
                {
                    instanceSimMatrix[i][j] = sim.getSimilarity(instances[i], instances[j]);
                }
            }
        }

        return instanceSimMatrix;
    }

    public double getEnsembleDiversity (ClusteringSimilarity div)
    {
        if (data.length == 0)
        {
            return 0.0;
        }

        double sum = 0.0;
        for (int i=0; i<this.data.length-1; i++)
        {
            for (int j=i+1; j<this.data.length; j++)
            {
                double s = div.getDistance(data[i], data[j]);
                if (s < 0.0)
                {
                    System.out.println("DISTANZA NEGATIVA!!!!!!!!!!!!!!!");
                    double t = div.getDistance(data[i], data[j]);
                }
                sum += div.getDistance(data[i], data[j]);
            }
        }

        double den = data.length*(data.length-1)/2;
        return sum/den;
    }

    public double[][] getCoOccurrenceMatrix ()
    {
        /*
        if (this.coOccurrenceMatrix == null)
        {
            coOccurrenceMatrix = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
            
            for (int k=0; k<data.length; k++)
            {
                for (int i=0; i<coOccurrenceMatrix.length-1; i++)
                {
                    for (int j=i+1; j<coOccurrenceMatrix[i].length; j++)
                    {
                        if (sameCluster(i,j,k))
                        {
                            coOccurrenceMatrix[i][j]+= (new Double(1.0).doubleValue())/data.length;
                        }
                    }
                }
            }
        }

        return coOccurrenceMatrix;
        */
        double[][] coOccurrenceMatrix2 = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];

        for (int k=0; k<data.length; k++)
        {
            System.out.println("CO-OCCURRENCE MATRIX: "+(k+1));
            Clustering c = (Clustering)data[k];
            Cluster[] clusters = c.getClusters();
            
            for (int l=0; l<clusters.length; l++)
            {
                Instance[] instances = clusters[l].getInstances();
                
                for (int i=0; i<instances.length-1; i++)
                {
                    for (int j=i+1; j<instances.length; j++)
                    {
                        int ID1 = instances[i].getID();
                        int ID2 = instances[j].getID();
                        
                        if (ID1 < ID2)
                        {
                            coOccurrenceMatrix2[ID1][ID2]+= (new Double(1.0).doubleValue())/data.length;
                        }
                        else
                        {
                            coOccurrenceMatrix2[ID2][ID1]+= (new Double(1.0).doubleValue())/data.length;
                        }
                    }
                }
            }
        }
        
        
        /*
        for (int k=0; k<data.length; k++)
        {
            for (int i=0; i<coOccurrenceMatrix2.length-1; i++)
            {
                for (int j=i+1; j<coOccurrenceMatrix2[i].length; j++)
                {
                    if (sameCluster(i,j,k))
                    {
                        coOccurrenceMatrix2[i][j]+= (new Double(1.0).doubleValue())/data.length;
                    }
                }
            }
        }        
        */
        
        return coOccurrenceMatrix2;
    }
    
    public double[][] getWholeCoOccurrenceMatrix ()
    {
        double [][] M = getCoOccurrenceMatrix();
        
        for (int i=0; i<M.length; i++)
            for (int j=M.length-1; j>=0; j--)
                if(i != j)
                    M [j][i] = M [i][j];
                else
                    M[i][j] = 1;
        return M;
    }
    
    public double[][] getWholeWeightedCoOccurrenceMatrix (WeightingScheme ws)
    {
        double [][] M = getWeightedCoOccurrenceMatrix(ws);
        
        for (int i=0; i<M.length; i++)
            for (int j=M.length-1; j>=0; j--)
                if(i != j)
                    M [j][i] = M [i][j];
                else
                    M[i][j] = 1;
        return M;

    }
        
    public double[][] getWeightedCoOccurrenceMatrix (WeightingScheme ws)
    {
        /*
        if (this.coOccurrenceMatrix == null)
        {
            coOccurrenceMatrix = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
            if (this.weightingScheme == null || !this.weightingScheme.equals(ws))
            {
                this.weightingScheme = ws;
            }
            
            double[] weights = this.weightingScheme.weight(this);
            
            for (int k=0; k<data.length; k++)
            {
                for (int i=0; i<coOccurrenceMatrix.length-1; i++)
                {
                    for (int j=i+1; j<coOccurrenceMatrix[i].length; j++)
                    {
                        if (sameCluster(i,j,k))
                        {
                            coOccurrenceMatrix[i][j]+= weights[k]/data.length;
                        }
                    }
                }
            }
        }
        else if (this.weightingScheme == null || !this.weightingScheme.equals(ws))
        {
            this.weightingScheme = ws;
            
            double[] weights = this.weightingScheme.weight(this);
            
            for (int k=0; k<data.length; k++)
            {
                for (int i=0; i<coOccurrenceMatrix.length-1; i++)
                {
                    for (int j=i+1; j<coOccurrenceMatrix[i].length; j++)
                    {
                        if (sameCluster(i,j,k))
                        {
                            coOccurrenceMatrix[i][j]+= weights[k]/data.length;
                        }
                    }
                }
            }            
        }
        */

        double[][] coOccurrenceMatrix2 = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
        this.weightingScheme = ws;
        
        double[] weights = this.weightingScheme.weight(this);

        for (int k=0; k<data.length; k++)
        {
            Clustering c = (Clustering)data[k];
            Cluster[] clusters = c.getClusters();
            
            for (int l=0; l<clusters.length; l++)
            {
                Instance[] instances = clusters[l].getInstances();
                
                for (int i=0; i<instances.length-1; i++)
                {
                    for (int j=i+1; j<instances.length; j++)
                    {
                        int ID1 = instances[i].getID();
                        int ID2 = instances[j].getID();
                        
                        if (ID1 < ID2)
                        {
                            coOccurrenceMatrix2[ID1][ID2]+= weights[k]/data.length;
                        }
                        else
                        {
                            coOccurrenceMatrix2[ID2][ID1]+= weights[k]/data.length;
                        }
                    }
                }
            }
        }        
        
        
        /*
        for (int k=0; k<data.length; k++)
        {
            for (int i=0; i<coOccurrenceMatrix2.length-1; i++)
            {
                for (int j=i+1; j<coOccurrenceMatrix2[i].length; j++)
                {
                    if (sameCluster(i,j,k))
                    {
                        coOccurrenceMatrix2[i][j]+= weights[k]/data.length;
                    }
                }
            }
        }
        */ 
            
        return coOccurrenceMatrix2;
    }    

    protected boolean sameCluster(int i, int j, int k)
    {
        Clustering p = (Clustering)data[k];

        boolean bi = false;
        boolean bj = false;

        for (int z=0; z<p.getNumberOfClusters(); z++)
        {
            Cluster cz = p.getClusters()[z];
            for (int w=0; w<cz.getNumberOfInstances(); w++)
            {
                if (cz.getInstances()[w].getID() == i)
                {
                    bi = true;
                }
                if (cz.getInstances()[w].getID() == j)
                {
                    bj = true;
                }
            }

            if (bi && bj)
            {
                return true;
            }

            if ((bi && !bj) || (bj && !bi))
            {
                return false;
            }
        }

        return false;
    }

    public int getNumberOfInstances ()
    {
        return ((Clustering)data[0]).getNumberOfInstances();
    }

    public int getNumberOfClusters()
    {
        return this.allClusters.length;
    }

    public Instance[] getInstances()
    {
        return ((Clustering)data[0]).getInstances();
    }

    public Cluster[] getClusters()
    {
        return allClusters;
    }

    public Dataset getInstancesDataset()
    {
        if (getInstances()[0] instanceof NumericalInstance)
        {
            return new NumericalInstanceDataset(getInstances(),null);
        }

        if (getInstances()[0] instanceof Cluster)
        {
            return new ClusterDataset(getInstances(),null);
        }

        if (getInstances()[0] instanceof Clustering)
        {
            return new ClusteringDataset(getInstances(),null);
        }
        
        if (getInstances()[0] instanceof ProjectiveCluster)
        {
            return new ProjectiveClusterDataset(getInstances(),null);
        }
        
        if (getInstances()[0] instanceof ProjectiveClustering)
        {
            return new ProjectiveClusteringDataset(getInstances(),null,null);
        }

        return null;
    }

    protected void buildClusters()
    {
        int count = 0;
        for (int i=0; i<this.data.length; i++)
        {
            count+=((Clustering)data[i]).getNumberOfClusters();
        }

        allClusters = new Cluster[count];
        int i=0;
        for (int j=0; j<data.length; j++)
        {
            Cluster[] clustersJ = ((Clustering)data[j]).getClusters();
            for (int k=0; k<clustersJ.length; k++)
            {
                allClusters[i] = clustersJ[k];
                i++;
            }
        }
    }


    protected boolean same_cluster(Cluster cluster, Instance inst1, Instance inst2)
    {
        boolean present_inst1=false;
        boolean present_inst2=false;
        Instance[] instances = cluster.getInstances();
        for(int i=0; i<instances.length; i++)
        {
            if(instances[i].equals(inst1))
            {
                present_inst1 = true;
            }
            if(instances[i].equals(inst2))
            {
                present_inst2 = true;
            }
        }
        return (present_inst1 && present_inst2);
    }

}


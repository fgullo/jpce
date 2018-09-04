package clusteringensembles;

import clustering.LAC;
import clustering.METIS;
import weighting.WeightingScheme;
import dataset.ClusteringDataset;
import dataset.Dataset;
import dataset.NumericalInstanceDataset;
import evaluation.numericalinstance.MinkowskiNumericalInstanceSim;
import evaluation.Similarity;
import evaluation.numericalinstance.WeightedMinkowskiNumericalInstanceSim;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;

public class WBPA extends HybridCEMethod
{
    protected int m; //number of LAC clusterings
    protected NumericalInstanceDataset numDataset;
    protected double[][] A;

    public WBPA (NumericalInstanceDataset numDataset, int m) 
    {
        this.numDataset = numDataset;
        this.m = m;
    }
    
    public double[][] getA()
    {
        return this.A;
    }

    @Override
    public Clustering execute () 
    {
        return execute(2);
    }
    
    @Override
    public Clustering weightedExecute (WeightingScheme ws) 
    {
        return weightedExecute(2,ws);
    }    

    public Clustering execute (int nClusters) 
    {
        this.A = new double[numDataset.getDataLength()][nClusters*this.m];
        Clustering[] beingBuiltEnsemble = new Clustering[this.m];
        
        //build ensemble and matrix A
        for (int v=1; v<=m; v++)
        {
            double h = ((double)1.0)/v; //#################
            LAC lac = new LAC(numDataset,h);
            
            Clustering clust_v = lac.execute(new MinkowskiNumericalInstanceSim(2), nClusters);
            clust_v.setID(v-1);
            beingBuiltEnsemble[v-1] = clust_v;
            Instance[] c_v = lac.getCentroids();
            double[][] w_v = lac.getWeights();
            
            //double[][] P = new double[numDataset.getDataLength()][c_v.length];
            for (int i=0; i<numDataset.getDataLength(); i++)
            {
                NumericalInstance o_i = (NumericalInstance)numDataset.getData()[i];
                
                double[] d_i = new double[c_v.length];
                for (int l=0; l<d_i.length; l++)
                {
                    Similarity sim = new WeightedMinkowskiNumericalInstanceSim(2,w_v[l]);
                    d_i[l] = sim.getDistance(o_i, c_v[l]);
                }
                
                double D_i = d_i[0];
                double sum_d_i = 0.0;
                for (int l=0; l<d_i.length; l++)
                {
                    sum_d_i+=d_i[l];
                    if (d_i[l] > D_i)
                    {
                        D_i = d_i[l];
                    }
                }
                double k = (double)numDataset.getDataLength();
                
                //P_i contains the posterior probabilities of instance 'i' at each cluster of the partition 'v'
                double[] P_i = new double[c_v.length];
                for (int l=0; l<P_i.length; l++)
                {
                    double num = D_i-d_i[l]+1;
                    double den = k*D_i+k-sum_d_i;
                    P_i[l] = (den==0) ? 0.0 : (num/den);
                }
                
                int start = (v-1)*P_i.length;
                for (int c=0; c<P_i.length; c++)
                {
                    A[i][start] = P_i[c];
                    start++;
                }
            }
        }
        
        ensemble = new ClusteringDataset(beingBuiltEnsemble,null);        
        
        
        //build similarity matrix representing the edge weights in the hybrid graph (from A)
        Instance[] instances = numDataset.getData();
        
        int totInstances = instances.length;
        int totClusters = nClusters*this.m;
        
        double[][] simMatrix = new double[totInstances+totClusters][totInstances+totClusters];
        for (int i=0; i<totInstances; i++)
        {
            for (int j=totInstances; j<simMatrix[i].length; j++)
            {
                simMatrix[i][j] = A[i][j-totInstances];
            }
        }
        
        //build dataset
        Dataset d = buildHybridDataset(instances,totInstances,totClusters);
        
        //run metis
        METIS metis = new METIS(d);
        Clustering metisResult = metis.execute(simMatrix, nClusters);
        
        return buildFinalClustering(metisResult,totInstances);
    }
    
    public Clustering weightedExecute (int nClusters, WeightingScheme ws) 
    {
        this.A = new double[numDataset.getDataLength()][nClusters*this.m];
        Clustering[] beingBuiltEnsemble = new Clustering[this.m];
        
        //build ensemble and matrix A
        for (int v=1; v<=m; v++)
        {
            double h = ((double)1.0)/v; //#################
            LAC lac = new LAC(numDataset,h);
            
            Clustering clust_v = lac.execute(new MinkowskiNumericalInstanceSim(2), nClusters);
            clust_v.setID(v-1); 
            beingBuiltEnsemble[v-1] = clust_v;
            Instance[] c_v = lac.getCentroids();
            double[][] w_v = lac.getWeights();
            
            //double[][] P = new double[numDataset.getDataLength()][c_v.length];
            for (int i=0; i<numDataset.getDataLength(); i++)
            {
                NumericalInstance o_i = (NumericalInstance)numDataset.getData()[i];
                
                double[] d_i = new double[c_v.length];
                for (int l=0; l<d_i.length; l++)
                {
                    Similarity sim = new WeightedMinkowskiNumericalInstanceSim(2,w_v[l]);
                    d_i[l] = sim.getDistance(o_i, c_v[l]);
                }
                
                double D_i = d_i[0];
                double sum_d_i = 0.0;
                for (int l=0; l<d_i.length; l++)
                {
                    sum_d_i+=d_i[l];
                    if (d_i[l] > D_i)
                    {
                        D_i = d_i[l];
                    }
                }
                double k = (double)numDataset.getDataLength();
                
                //P_i contains the posterior probabilities of instance 'i' at each cluster of the partition 'v'
                double[] P_i = new double[c_v.length];
                for (int l=0; l<P_i.length; l++)
                {
                    double num = D_i-d_i[l]+1;
                    double den = k*D_i+k-sum_d_i;
                    P_i[l] = (den==0) ? 0.0 : (num/den);
                }
                
                int start = (v-1)*P_i.length;
                for (int c=0; c<P_i.length; c++)
                {
                    A[i][start] = P_i[c];
                    start++;
                }
            }
        }
        
        ensemble = new ClusteringDataset(beingBuiltEnsemble,null);        
        
        
        //build similarity matrix representing the edge weights in the hybrid graph (from A)
        double[] weights = ws.weight(ensemble);
        
        int[] clusterToClusteringMapping = new int[ensemble.getNumberOfClusters()];
        Clustering[] clusterings = (Clustering[])ensemble.getData();
        for (int i=0; i<clusterings.length; i++)
        {
            Cluster[] clust = clusterings[i].getClusters();
            for (int j=0; j<clust.length; j++)
            {
                clusterToClusteringMapping[clust[j].getID()] = i;
            }
        }        
        
        Instance[] instances = numDataset.getData();
        
        int totInstances = instances.length;
        int totClusters = nClusters*this.m;
        
        double[][] simMatrix = new double[totInstances+totClusters][totInstances+totClusters];
        for (int i=0; i<totInstances; i++)
        {
            for (int j=totInstances; j<simMatrix[i].length; j++)
            {
                int indexOfCluster = j-totInstances;
                double weight = weights[clusterToClusteringMapping[ensemble.getClusters()[indexOfCluster].getID()]];
                simMatrix[i][j] = weight*A[i][j-totInstances];
            }
        }
        
        //build dataset
        Dataset d = buildHybridDataset(instances,totInstances,totClusters);
        
        //run metis
        METIS metis = new METIS(d);
        Clustering metisResult = metis.execute(simMatrix, nClusters);
        
        return buildFinalClustering(metisResult,totInstances);
    }    
}
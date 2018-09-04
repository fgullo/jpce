package clusteringensembles;

import clustering.LAC;
import clustering.METIS;
import weighting.WeightingScheme;
import dataset.ClusteringDataset;
import dataset.NumericalInstanceDataset;
import evaluation.numericalinstance.CosineNumericalInstanceSim;
import evaluation.numericalinstance.MinkowskiNumericalInstanceSim;
import evaluation.Similarity;
import evaluation.numericalinstance.WeightedCosineNumericalInstanceSim;
import evaluation.numericalinstance.WeightedMinkowskiNumericalInstanceSim;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;

public class WSPA extends InstanceBasedCEMethod
{
    protected int m;
    protected NumericalInstanceDataset numDataset;
    protected double[][] PSI;

    public WSPA (NumericalInstanceDataset numDataset, int m) 
    {
        this.numDataset = numDataset;
        this.m = m;
    }
    
    public double[][] getPSI()
    {
        return this.PSI;
    }

    @Override
    public Clustering execute () 
    {
        return execute(2);
    }

    public Clustering execute (int nClusters) 
    {       
        this.PSI = new double[numDataset.getDataLength()][numDataset.getDataLength()];
        Clustering[] beingBuiltEnsemble = new Clustering[this.m];
        
        //build ensemble and PSI
        for (int v=1; v<=m; v++)
        {
            double h = ((double)1.0)/v; //#################
            LAC lac = new LAC(numDataset,h);
            
            Clustering clust_v = lac.execute(new MinkowskiNumericalInstanceSim(2), nClusters);
            clust_v.setID(v-1);
            beingBuiltEnsemble[v-1] = clust_v;
            Instance[] c_v = lac.getCentroids();
            double[][] w_v = lac.getWeights();
            
            double[][] P = new double[numDataset.getDataLength()][c_v.length];
            for (int i=0; i<numDataset.getDataLength(); i++)
            {
                NumericalInstance o_i = (NumericalInstance)numDataset.getData()[i];
                
                double[] d_i = new double[c_v.length];
                for (int l=0; l<d_i.length; l++)
                {
                    Similarity sim = new WeightedMinkowskiNumericalInstanceSim(2,w_v[l]);
                    if (c_v[l] != null)
                    {
                        d_i[l] = sim.getDistance(o_i, c_v[l]);
                    }
                    else
                    {
                        d_i[l] = 0.0;
                    }
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
                
                double[] P_i = new double[c_v.length];
                for (int l=0; l<P_i.length; l++)
                {
                    double num = D_i-d_i[l]+1;
                    double den = k*D_i+k-sum_d_i;
                    P_i[l] = (den==0) ? 0.0 : (num/den);
                }
                P[i] = P_i;
            }
            
            //compute cosine similarity between every pair of rows in the matrix P
            Similarity cosSim = new CosineNumericalInstanceSim();
            for (int x=0; x<P.length-1; x++)
            {
                Instance i1 = new NumericalInstance(P[x],x);
                for (int y=x+1; y<P.length; y++)
                {
                    Instance i2 = new NumericalInstance(P[y],y);
                    PSI[x][y] += cosSim.getSimilarity(i1, i2)/this.m;
                }
            }
        }
        
        ensemble = new ClusteringDataset(beingBuiltEnsemble,null);
        
        METIS metis = new METIS(numDataset);
        return metis.execute(PSI, nClusters);
    }
    
    public Clustering weightedExecute (int nClusters, WeightingScheme ws) 
    {       
        this.PSI = new double[numDataset.getDataLength()][numDataset.getDataLength()];
        Clustering[] beingBuiltEnsemble = new Clustering[this.m];
        
        //build ensemble and PSI
        for (int v=1; v<=m; v++)
        {
            double h = ((double)1.0)/v; //#################
            LAC lac = new LAC(numDataset,h);
            
            Clustering clust_v = lac.execute(new MinkowskiNumericalInstanceSim(2), nClusters);
            clust_v.setID(v-1);
            beingBuiltEnsemble[v-1] = clust_v;
            Instance[] c_v = lac.getCentroids();
            double[][] w_v = lac.getWeights();
            
            double[][] P = new double[numDataset.getDataLength()][c_v.length];
            for (int i=0; i<numDataset.getDataLength(); i++)
            {
                NumericalInstance o_i = (NumericalInstance)numDataset.getData()[i];
                
                double[] d_i = new double[c_v.length];
                for (int l=0; l<d_i.length; l++)
                {
                    Similarity sim = new WeightedMinkowskiNumericalInstanceSim(2,w_v[l]);
                    if (c_v[l] != null)
                    {
                        d_i[l] = sim.getDistance(o_i, c_v[l]);
                    }
                    else
                    {
                        d_i[l] = 0.0;
                    }
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
                
                double[] P_i = new double[c_v.length];
                for (int l=0; l<P_i.length; l++)
                {
                    double num = D_i-d_i[l]+1;
                    double den = k*D_i+k-sum_d_i;
                    P_i[l] = (den==0) ? 0.0 : (num/den);
                }
                P[i] = P_i;
            }
            
            //compute cosine similarity between every pair of rows in the matrix P
            Similarity wCosSim = new WeightedCosineNumericalInstanceSim(ws.weight(ensemble));
            for (int x=0; x<P.length-1; x++)
            {
                Instance i1 = new NumericalInstance(P[x],x);
                for (int y=x+1; y<P.length; y++)
                {
                    Instance i2 = new NumericalInstance(P[y],y);
                    PSI[x][y] += wCosSim.getSimilarity(i1, i2)/this.m;
                }
            }
        }
        
        ensemble = new ClusteringDataset(beingBuiltEnsemble,null);
        
        METIS metis = new METIS(numDataset);
        return metis.execute(PSI, nClusters);
    }    

}


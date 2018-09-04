package clustering;

import dataset.ClusteringDataset;
import dataset.Dataset;
import dataset.ProjectiveClusteringDataset;
import evaluation.Similarity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import objects.*;


public class Metaclusters_ML2012 extends ClusteringMethod 
{

    protected int maxIterations = 50;
    protected double[][] distMatrix;
    protected ProjectiveClusteringDataset ensemble;
    protected HashMap<Integer, Integer>[] mapping;
    protected double epsilon = 0.001;

    public Metaclusters_ML2012(ProjectiveClusteringDataset ensemble,HashMap<Integer, Integer>[] mapping)
    {
        this.distMatrix = null;
        this.ensemble = ensemble;
        this.mapping = mapping;
    }

    public Clustering execute (Similarity sim) 
    {
        return execute(sim,2);
    }
    
    public Clustering execute (double[][] distMatrix) 
    {
        return execute(distMatrix,2);
    }

    
    public Clustering execute (double[][] distMatrix, int nClusters) 
    {
        int n = this.ensemble.getNumberOfAllClusters();
        
        this.distMatrix = distMatrix;
        
        return runAlgorithm(nClusters);   
    }


    public Clustering execute (Similarity sim, int nClusters) 
    {     
        int n = this.ensemble.getNumberOfAllClusters();

        computeDistances(sim,n);
        
        
        
        return runAlgorithm(nClusters);
    }
    
    /*
    public HashMap<Integer, Integer>[] computeMapping()
    {
        HashMap<Integer, Integer>[] mapping = new HashMap[this.ensemble.getDataLength()];
        int id = 0;
        Instance[] clusterings = this.ensemble.getData();
        for (int a=0; a<mapping.length; a++)
        {
            ProjectiveClustering C = (ProjectiveClustering)clusterings[a];
            HashMap<Integer,Integer> map = new HashMap<Integer, Integer>(C.getNumberOfClusters());
            for (int b=0; b<C.getClusters().length; b++)
            {
                map.put(b, id);
                id++;
            }
            mapping[a] = map;
        }
        
        return mapping;
    }
    */        
    
    protected Clustering runAlgorithm(int nClusters)
    {        
        //checkNaNandInftyInDistanceMatrix();
        
        HashMap<Integer,HashSet<Integer>>[] metaClusters = initializeMetaClusters(nClusters);
        
        double V = computeInitialV(metaClusters);
        double V_prime = V;
        
        boolean end = false;
        while(!end)
        {
            V = V_prime;
            
            int[] m_move = new int[]{-1,-1}; 
            int[] m_swap = new int[]{-1,-1};
            int M_old = -1;
            int M_new = -1;

            for(int M_s = 0; M_s<metaClusters.length; M_s++)
            {
                HashMap<Integer,HashSet<Integer>> M_s_map = metaClusters[M_s];
                for (int a:M_s_map.keySet())
                {
                    for (int b:M_s_map.get(a))
                    {
                        for (int M_t=0; M_t<metaClusters.length; M_t++)
                        {
                            if (M_t != M_s)
                            {
                                HashMap<Integer,HashSet<Integer>> M_t_map = metaClusters[M_t];

                                double[] res = evaluateMove(a,b,M_s,M_t,metaClusters);
                                double V_hat = res[0];
                                int a_hat = (int)res[1];
                                int b_hat = (int)res[2];

                                if (V + V_hat < V_prime)
                                {
                                    V_prime = V + V_hat;
                                    m_move[0] = a;  m_move[1] = b;
                                    m_swap[0] = a_hat;  m_swap[1] = b_hat;
                                    M_old = M_s;
                                    M_new = M_t;
                                }
                            }
                        }
                    }
                }
            }
            
            if (V_prime < V)
            {
                //delete m_move from M_old
                metaClusters[M_old].get(m_move[0]).remove(m_move[1]);
                //add m_move to M_new
                metaClusters[M_new].get(m_move[0]).add(m_move[1]);
                
                if (m_swap[0] > -1 && m_swap[1] > -1)
                {
                    //delete m_swap from M_new
                    metaClusters[M_new].get(m_swap[0]).remove(m_swap[1]);
                    //add m_swap to M_old
                    metaClusters[M_old].get(m_swap[0]).add(m_swap[1]);
                }     
            }
            else
            {
                end = true;
            }
        }
        
        Clustering c = buildClustersFromMetaClusters(metaClusters);
        return c;
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

    protected HashMap<Integer,HashSet<Integer>>[] initializeMetaClusters(int nClusters) 
    {
       HashMap<Integer,HashSet<Integer>>[] metaClusters = new HashMap[nClusters];
       for (int i=0; i<metaClusters.length; i++)
       {
           metaClusters[i] = new HashMap<Integer, HashSet<Integer>>();
       }
       
       HashSet<Integer> validMetaClusters = new HashSet<Integer>(nClusters);
       for (int i=0; i<nClusters; i++)
       {
           validMetaClusters.add(i);
       }
       
       Instance[] clusterings = this.ensemble.getData();
       for (int a=0; a<clusterings.length; a++)
       {
           ProjectiveClustering C = (ProjectiveClustering)clusterings[a];
           ProjectiveCluster[] clusters = C.getClusters();
           for (int b=0; b<clusters.length; b++)
           {
               ProjectiveCluster c = clusters[b];
               if (validMetaClusters.isEmpty())
               {
                    for (int i=0; i<nClusters; i++)
                    {
                        validMetaClusters.add(i);
                    }
               }
               
               double rnd = Math.random()*(validMetaClusters.size()-1);
               int index = (int)Math.round(rnd);
               int x = -1;              
               Iterator<Integer> it = validMetaClusters.iterator();
               do
               {
                   x = it.next();
                   index--;
               }
               while (index >= 0);
               
               HashMap<Integer, HashSet<Integer>> map = metaClusters[x];
               HashSet<Integer> hash = (map.containsKey(a))?map.get(a):new HashSet<Integer>();
               hash.add(b);
               map.put(a, hash);
               
               validMetaClusters.remove(x);               
           }
           
           for (int m=0; m<metaClusters.length; m++)
           {
               if (!metaClusters[m].containsKey(a))
               {
                    int pc = (int)Math.round(Math.random()*(clusters.length-1));
                    HashSet<Integer> hash = new HashSet<Integer>();
                    hash.add(pc);
                    metaClusters[m].put(a, hash);
               }
           }
       }
       
       return metaClusters;
    }

    protected void computeDistances(Similarity sim, int n) 
    {
        this.distMatrix = new double[n][n];
        
        Instance[] clusterings = this.ensemble.getData();
        for (int a1=0; a1<clusterings.length; a1++)
        {
            ProjectiveClustering C1 = (ProjectiveClustering)clusterings[a1];
            ProjectiveCluster[] clusters1 = C1.getClusters();
            for (int b1=0; b1<C1.getNumberOfClusters(); b1++)
            {
                ProjectiveCluster c1 = clusters1[b1];
                int id1 = mapping[a1].get(b1);
                
                for (int b2=b1+1; b2<clusters1.length; b2++)
                {
                    ProjectiveCluster c2 = clusters1[b2];
                    int id2 = mapping[a1].get(b2);
                    this.distMatrix[id1][id2] = sim.getDistance(c1, c2);
                    this.distMatrix[id2][id1] = this.distMatrix[id1][id2];
                }
                
                for (int a2 = a1+1; a2<clusterings.length; a2++)
                {
                    ProjectiveClustering C2 = (ProjectiveClustering)clusterings[a2];
                    ProjectiveCluster[] clusters2 = C2.getClusters();
                    for (int b2=0; b2<C2.getNumberOfClusters(); b2++)
                    {
                        ProjectiveCluster c2 = clusters2[b2];
                        int id2 = mapping[a2].get(b2);
                        this.distMatrix[id1][id2] = sim.getDistance(c1, c2);
                        this.distMatrix[id2][id1] = this.distMatrix[id1][id2]; 
                    } 
                }
                
            }
        }
    }
    
    public static double[][] computeDistances(Similarity sim, HashMap<Integer, Integer>[] mappingStatic, ProjectiveClusteringDataset ensemble) 
    {
        int n = ensemble.getNumberOfAllClusters();
        double[][] distMatrix = new double[n][n];
        
        Instance[] clusterings = ensemble.getData();
        for (int a1=0; a1<clusterings.length; a1++)
        {
            ProjectiveClustering C1 = (ProjectiveClustering)clusterings[a1];
            ProjectiveCluster[] clusters1 = C1.getClusters();
            for (int b1=0; b1<C1.getNumberOfClusters(); b1++)
            {
                ProjectiveCluster c1 = clusters1[b1];
                int id1 = mappingStatic[a1].get(b1);
                
                for (int b2=b1+1; b2<clusters1.length; b2++)
                {
                    ProjectiveCluster c2 = clusters1[b2];
                    int id2 = mappingStatic[a1].get(b2);
                    distMatrix[id1][id2] = sim.getDistance(c1, c2);
                    distMatrix[id2][id1] = distMatrix[id1][id2];
                }
                
                for (int a2 = a1+1; a2<clusterings.length; a2++)
                {
                    ProjectiveClustering C2 = (ProjectiveClustering)clusterings[a2];
                    ProjectiveCluster[] clusters2 = C2.getClusters();
                    for (int b2=0; b2<C2.getNumberOfClusters(); b2++)
                    {
                        ProjectiveCluster c2 = clusters2[b2];
                        int id2 = mappingStatic[a2].get(b2);
                        distMatrix[id1][id2] = sim.getDistance(c1, c2);
                        distMatrix[id2][id1] = distMatrix[id1][id2]; 
                    } 
                }
                
            }
        }
        
        return distMatrix;                
    }
    
    
    
    protected double getdistance(int a1, int a2, int b1, int b2)
    {
        int id1 = mapping[a1].get(b1);
        int id2 = mapping[a2].get(b2);
        
        return this.distMatrix[id1][id2];
    }

    protected double computeInitialV(HashMap<Integer, HashSet<Integer>>[] metaClusters) 
    {
        double V = 0.0;
        
        for(HashMap<Integer, HashSet<Integer>> mc:metaClusters)
        {
            for (int a1:mc.keySet())
            {
                for (int b1:mc.get(a1))
                {
                    int id1 = mapping[a1].get(b1);
                    for (int a2:mc.keySet())
                    {
                        for (int b2:mc.get(a2))
                        {
                            int id2 = mapping[a2].get(b2);
                            V += this.distMatrix[id1][id2];
                        }
                    }
                }
            }
        }

        return V;
    }

    protected Clustering buildClustersFromMetaClusters(HashMap<Integer, HashSet<Integer>>[] metaClusters) 
    {
        Clustering c = null;
        
        Cluster[] clusters = new Cluster[metaClusters.length];
        Instance[] clusterings = this.ensemble.getData();
        int n = this.ensemble.getNumberOfAllClusters();
        for (int k=0; k<clusters.length; k++)
        {
            int size = 0;
            if(metaClusters[k].size() != clusterings.length)
            {
                throw new RuntimeException("ERROR: size must be equal to ensemble_size="+clusterings.length+", instead size ="+metaClusters[k].size());
            }
            for (int key:metaClusters[k].keySet())
            {
                int s = metaClusters[k].get(key).size();
                if (s == 0)
                {
                    throw new RuntimeException("ERROR: size cannot be zero");
                    
                }
                size += s;
            }
            
            Instance[] cluster = new Instance[size];
            int i=0;
            for (int a:metaClusters[k].keySet())
            {
                ProjectiveClustering C = (ProjectiveClustering)clusterings[a];
                for (int b:metaClusters[k].get(a))
                {
                    cluster[i] = C.getClusters()[b];
                    i++;
                }
            }
            
            clusters[k] = new Cluster(cluster,k,n);
        }
        
        c = new Clustering(clusters, -1);
        
        return c;
    }

    protected double[] evaluateMove(int a, int b, int M_s, int M_t, HashMap<Integer, HashSet<Integer>>[] metaClusters) 
    {
        double V_hat = 0.0;
        int a_hat = -1;
        int b_hat = -1;
        
        double ab_M_s = 0.0;
        double ab_M_t = 0.0;
        
        for (int aa:metaClusters[M_s].keySet())
        {
            for (int bb:metaClusters[M_s].get(aa))
            {
                ab_M_s += getdistance(a, aa, b, bb);
            }
        }
        
        for (int aa:metaClusters[M_t].keySet())
        {
            for (int bb:metaClusters[M_t].get(aa))
            {
                ab_M_t += getdistance(a, aa, b, bb);
            }
        }
        
        if (metaClusters[M_s].get(a).size() > 1)
        {
            V_hat = ab_M_t -ab_M_s;
        }
        else
        {
            V_hat = Double.POSITIVE_INFINITY;
            for (int bMin:metaClusters[M_t].get(a))
            {
                if (bMin != b)
                {
                    double tmp1 = ab_M_t - getdistance(a, a, b, bMin);
                    double tmp2 = ab_M_s;
                    double tmp3 = 0.0;
                    for (int atmp3:metaClusters[M_s].keySet())
                    {
                        for (int btmp3:metaClusters[M_s].get(atmp3))
                        {
                            if (atmp3 != a || btmp3 != b)
                            {
                                tmp3 += getdistance(a, atmp3, bMin, btmp3);
                            }
                        }
                    }
                    double tmp4 = 0.0;
                    for (int atmp4:metaClusters[M_t].keySet())
                    {
                        for (int btmp4:metaClusters[M_t].get(atmp4))
                        {
                            tmp4 += getdistance(a, atmp4, bMin, btmp4);
                        }
                    }

                    double tmp = tmp1 - tmp2 + tmp3 - tmp4;

                    if(tmp < V_hat)
                    {
                        V_hat = tmp;
                        a_hat = a;
                        b_hat = bMin;
                    }
                }
            }
        }
        
        return new double[]{V_hat,a_hat,b_hat};       
    }
}




















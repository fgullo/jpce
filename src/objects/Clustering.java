package objects;

import objects.centroid.NumericalInstanceCentroidComputationAVG;
import evaluation.clustering.ClusteringInternalValidityCriterion;
import evaluation.clustering.ClusteringSimilarity;
import evaluation.numericalinstance.MinkowskiNumericalInstanceSim;
import evaluation.Similarity;
import java.util.HashMap;
import java.util.Iterator;

 public class Clustering extends Instance
 {

    //protected HashMap instances;
    protected Instance[] instances;
    protected Cluster[] clusters;

    public Clustering (Cluster[] clusters)
    {
        ID=genID();
        this.clusters=clusters;
        genInstances();
    }

    public Clustering (Cluster[] clusters, int ID)
    {
        this.clusters=clusters;
        this.ID=ID;
        genInstances(); 
    }

    public int getNumberOfClusters ()
    {
        return clusters.length;
    }

    public int getNumberOfInstances ()
    {
        //return instances.size();
        return instances.length;
    }

    public Cluster[] getClusters ()
    {
        return clusters;
    }

    /*
    public HashMap getInstancesHashMap ()
    {
        return instances;
    }

    
    public Instance[] getInstances()
    {
        Instance[] instancesArray = new Instance[this.instances.size()];
        Iterator itKeys = this.instances.keySet().iterator();
        
        while (itKeys.hasNext())
        {
            Integer index = (Integer)itKeys.next();
            Instance value = (Instance)this.instances.get(index);
            
            instancesArray[index] = value;
        }
        
        return instancesArray;
    }
     */

    public Instance[] getInstances()
    {
        return this.instances;
    }

    public double externalClusteringEvaluation (ClusteringSimilarity sim, Clustering referencePartition)
    {
        return sim.getSimilarity(referencePartition, this);
    }

    public double internalClusteringEvaluation (ClusteringInternalValidityCriterion crit, Similarity sim)
    {
        return crit.getSimilarity(this, sim);
    }

    public Object[] getFeatureVectorRepresentation ()
    {
        return featureVectorRepresentation;
    }
    
    public double getMean()
    {
        double med=0;
        for(int i = 0; i<clusters.length; i++)
        {
           med=med+clusters[i].getMean();
        }
        return med;
    }

    
    public double getStdDev()
    {
        double std=0;
        for(int i=0;i<clusters.length; i++)
        {
            std=std+clusters[i].getStdDev();
        }
        return std;
    }

    
    public double getVariance()
    {
        double var=0;
        for(int i=0;i<clusters.length; i++)
        {
            var=var+clusters[i].getStdDev();
        }
        return var;
    }

    /*
    private HashMap genInstances()
    {
        HashMap<Integer, Instance> tmp = new HashMap<Integer, Instance>();
        for(int i=0; i<clusters.length;i++ )
        {
            Instance [] inst = clusters[i].getInstances();
            for(int j=0; j<inst.length; j++)
                tmp.put(inst[j].getID(), inst[j]);
        }
        return tmp;
    }
     */

    private void genInstances()
    {
        int size = 0;
        for (int k=0; k<this.clusters.length; k++)
        {
            size += this.clusters[k].getNumberOfInstances();
        }
        this.instances = new Instance[size];

        for (int k=0; k<this.clusters.length; k++)
        {
            Instance[] cluster = this.clusters[k].getInstances();
            for (int i=0; i<cluster.length; i++)
            {
                if (cluster[i].getID() >= this.instances.length)
                {
                    System.out.println("");
                }
                this.instances[cluster[i].getID()] = cluster[i];
            }
        }

        for (int i=0; i<this.instances.length; i++)
        {
            if (this.instances[i] == null)
            {
                throw new RuntimeException("ERROR: IDs must be progressive integers");
            }
        }
    }
    
    public boolean deepEquals(Clustering c)
    {
        if (this.getNumberOfClusters()!=c.getNumberOfClusters() || this.getNumberOfInstances()!=c.getNumberOfInstances())
        {
            return false;
        }
        
        boolean[] checked = new boolean[c.getNumberOfClusters()];
        for (int i=0; i<this.getNumberOfClusters(); i++)
        {
            boolean uguali=false;
            for (int j=0; j<c.getNumberOfClusters()&&!uguali; j++)
            {
                if (!checked[j] && this.getClusters()[i].deepEquals(c.getClusters()[j]))
                {
                    uguali = true;
                    checked[j] = true;
                }
            }
            
            if (!uguali)
            {
                return false;
            }
        }
        
        return true;
    }
    
    public double [][] getConnectivityMatrix()
    {
        double[][] m = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
        
        for (int i=0; i<this.clusters.length; i++)
        {
            Instance[] cluster_i = this.clusters[i].getInstances();
            
            for (int j1=0; j1<cluster_i.length; j1++)
            {
                for (int j2=0; j2<cluster_i.length; j2++)
                {
                    int id1 = cluster_i[j1].getID();
                    int id2 = cluster_i[j2].getID();
                    
                    m[id1][id2] = 1;
                }
            }
        }
        
        return m;
    }
    
    public static ProjectiveClustering computeSubspaceClusteringLAC(Clustering clust, Instance[] data, double h)
    {
        //double h = 0.2;

        Double[][] newFeatures = new Double[clust.getNumberOfClusters()][data[0].getNumberOfFeatures()];
        Double[][] newObjects = new Double[clust.getNumberOfClusters()][data.length];
        Cluster[] refClusters = clust.getClusters();
        ProjectiveCluster[] newClusters = new ProjectiveCluster[refClusters.length];

        for (int j=0; j<refClusters.length; j++)
        {
            Instance[] refInstances = refClusters[j].getInstances();
            
            if (refInstances.length == 0)
            {
                for (int i=0; i<newObjects[j].length; i++)
                {
                    newObjects[j][i] = new Double(0.0);
                }
                
                for (int i=0; i<newFeatures[j].length; i++)
                {
                    newFeatures[j][i] = new Double(((double)1.0)/newFeatures[j].length);
                }
            }
            else
            {
                Double[] refObjects = refClusters[j].getFeatureVectorRepresentationDouble();
                for (int l=0; l<newObjects[0].length; l++)
                {
                    newObjects[j][l] = new Double(refObjects[l].doubleValue());
                }

                Instance centroid = new NumericalInstanceCentroidComputationAVG().getCentroid(refInstances);
                Double[] dataVectorCentroid = ((NumericalInstance)centroid).getDataVector();
                //Similarity s = new MinkowskiNumericalInstanceSim(2);

                double sum = 0.0;
                for (int i=0; i<newFeatures[j].length; i++)
                {
                    double xji = 0.0;
                    for (int z=0; z<refInstances.length; z++)
                    {
                        Double[] dataVector = ((NumericalInstance)refInstances[z]).getDataVector();
                        xji += (dataVector[i]-dataVectorCentroid[i])*(dataVector[i]-dataVectorCentroid[i]);
                    }

                    newFeatures[j][i] = Math.exp(-xji/h);
                    sum += newFeatures[j][i];
                }

                double sumCheck = 0.0;
                for (int i=0; i<newFeatures[j].length; i++)
                {
                    newFeatures[j][i] /= sum;

                    if (Double.isInfinite(newFeatures[j][i]) || Double.isNaN(newFeatures[j][i]) || newFeatures[j][i]<-0.000000001 || newFeatures[j][i]>1.00000001)
                    {
                        throw new RuntimeException("ERROR: newFeatures[j][i] must be within [0,1]---newFeatures[j][i]="+newFeatures[j][i]);
                    }

                    sumCheck += newFeatures[j][i];
                }

                if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck<0.999999999 || sumCheck>1.0000000001)
                {
                        throw new RuntimeException("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);                
                }
            }
        }

        for (int i=0; i<newClusters.length; i++)
        {
            newClusters[i] = new ProjectiveCluster(data, newObjects[i], newFeatures[i], refClusters[i].getID(), false, true);
        }

        return new ProjectiveClustering(newClusters);
    }

}


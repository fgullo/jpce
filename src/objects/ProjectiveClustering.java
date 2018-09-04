package objects;

import objects.centroid.ProjectiveCentroidComputation;
import evaluation.Similarity;
import evaluation.clustering.ProjectiveClusteringInternalValidityCriterion;
import evaluation.clustering.ProjectiveClusteringSimilarity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

 public class ProjectiveClustering extends Instance
 {

    protected HashMap instances;
    protected NumericalInstance[] centroids;
     
    protected ProjectiveCluster[] clusters;
    
    protected Double[][] clusterByObjectsMatrix;
    protected Double[][] clusterByFeaturesMatrix;
    
    protected ProjectiveClustering hardObjectsAndFeatures;
    
    protected int totCoverage = -1;

    public ProjectiveClustering (ProjectiveCluster[] clusters)
    {
        ID=genID();
        this.clusters=clusters;
        instances=genInstances();
        validClustering();
    }

    public ProjectiveClustering (ProjectiveCluster[] clusters, int ID)
    {
        this.clusters=clusters;
        this.ID=ID;
        instances=genInstances();
        validClustering();
    }

    public int getNumberOfClusters ()
    {
        return clusters.length;
    }

    public int getNumberOfInstances ()
    {
        return instances.size();
    }

    public ProjectiveCluster[] getClusters ()
    {
        return clusters;
    }

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

    public double externalClusteringEvaluation (ProjectiveClusteringSimilarity sim, Clustering referencePartition)
    {
        return sim.getSimilarity(referencePartition, this);
    }

    public double internalClusteringEvaluation (ProjectiveClusteringInternalValidityCriterion crit, Similarity sim)
    {
        double ret = crit.getDistance(this, sim);
        
        if (Double.isInfinite(ret) || Double.isNaN(ret))
        {
            throw new RuntimeException("ERROR: the value is INFINITY or NAN");
        }
        
        return ret;
    }

    public Object[] getFeatureVectorRepresentation () {//resta così
        return featureVectorRepresentation;
    }
    
    
    public double getMean() {
        
        return 0.0;
        /*
        double med=0;
        for(int i = 0; i<clusters.length; i++){
           med=med+clusters[i].getMean();
        }
        return med;
         */
    }
    

    
    
    public double getStdDev() 
    {
        return 0.0;
        
        /*
        double std=0;
        for(int i=0;i<clusters.length; i++){
            std=std+clusters[i].getStdDev();
        }
        return std;
        */ 
    }
     

    
    
    public double getVariance() 
    {
        return 0.0;
        /*
        double var=0;
        for(int i=0;i<clusters.length; i++){
            var=var+clusters[i].getStdDev();
        }
        return var;
        */ 
    }

    
    private HashMap genInstances()
    {
        HashMap<Integer, Instance> tmp = new HashMap<Integer, Instance>();
        
        Instance [] inst = clusters[0].getInstances();
        for(int j=0; j<inst.length; j++)
        {
            tmp.put(inst[j].getID(), inst[j]);
        }
        return tmp;
    }
    
    public boolean deepEquals(ProjectiveClustering c)
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
    
    /*
    public double [][] getConnectivityMatrix(PDFSimilarity pdfSim)
    {
        double[][] m = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
        
        for (int i=0; i<this.clusters.length; i++)
        {
            Instance[] instancesArray = this.getInstances();
            for (int j1=0; j1<instancesArray.length; j1++)
            {
                int id1 = instancesArray[j1].getID();
                for (int j2=0; j2<instancesArray.length; j2++)
                {
                    int id2 = instancesArray[j2].getID();
                }
            }
            
            
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
    */
    
    public Double[][] getClusterByObjectsMatrix()
    {
        if (this.clusterByObjectsMatrix == null)
        {
            this.clusterByObjectsMatrix = new Double[this.clusters.length][this.clusters[0].getFeatureVectorRepresentation().length];
            
            for (int i=0; i<this.clusterByObjectsMatrix.length; i++)
            {
                Double[] v = clusters[i].getFeatureVectorRepresentationDouble();
                for (int j=0; j<this.clusterByObjectsMatrix[i].length; j++)
                {
                    this.clusterByObjectsMatrix[i][j] = new Double(v[j].doubleValue());
                    if (Double.isInfinite(this.clusterByObjectsMatrix[i][j]) || Double.isNaN(this.clusterByObjectsMatrix[i][j]))
                    {
                        throw new RuntimeException("ERROR: the value is INFINITY or NAN");
                    }
                }
            }
        }
        
        return this.clusterByObjectsMatrix;
    }
    
    public Double[][] getClusterByFeaturesMatrix()
    {
        if (this.clusterByFeaturesMatrix == null)
        {
            this.clusterByFeaturesMatrix = new Double[this.clusters.length][this.clusters[0].getFeatureToClusterAssignments().length];
            
            for (int i=0; i<this.clusterByFeaturesMatrix.length; i++)
            {
                Double[] v = (Double[])clusters[i].getFeatureToClusterAssignments();
                for (int j=0; j<this.clusterByFeaturesMatrix[i].length; j++)
                {
                    this.clusterByFeaturesMatrix[i][j] = new Double(v[j].doubleValue());
                    if (Double.isInfinite(this.clusterByFeaturesMatrix[i][j]) || Double.isNaN(this.clusterByFeaturesMatrix[i][j]))
                    {
                        throw new RuntimeException("ERROR: the value is INFINITY or NAN");
                    }
                }
            }
        }
        
        return this.clusterByFeaturesMatrix;
    }
    
    
    public static ProjectiveClustering randomGen(Instance[] inst, int nClusters, int nInstances, int nFeatures)
    {
        int IDcluster = -1;

        //random generation---objects
        Double[][] mObjects = new Double[nClusters][nInstances];
        for (int j=0; j<nInstances; j++)
        {
            double sum = 0.0;
            for (int z=0; z<nClusters; z++)
            {
                mObjects[z][j] = Math.random();
                sum += mObjects[z][j];
            }

            for (int z=0; z<nClusters; z++)
            {
                mObjects[z][j] /= sum;
            }
        }
            
        //random generation---features
        Double[][] mFeatures = new Double[nClusters][nFeatures];
        for (int z=0; z<nClusters; z++)
        {
            double sum = 0.0;
            for (int j=0; j<nFeatures; j++)
            {
                mFeatures[z][j] = Math.random();
                sum += mFeatures[z][j];
            }

            for (int j=0; j<nFeatures; j++)
            {
                if (sum > 0.0)
                {
                    mFeatures[z][j] /= sum;
                }
                else
                {
                    mFeatures[z][j] = ((double)1.0)/nFeatures;
                }
            }
        }
            
        //build ProjectiveClustering object from matrices mObjects and mFeatures
        ProjectiveCluster[] clusters = new ProjectiveCluster[nClusters];
        for (int u=0; u<clusters.length; u++)
        {
            Double[] objectsRep = mObjects[u];
            Double[] featuresRep = mFeatures[u];

            clusters[u] = new ProjectiveCluster(inst, objectsRep, featuresRep, IDcluster, true, true);
            IDcluster--;
        }
            
        return new ProjectiveClustering(clusters, -1);
      }
    
    public int getNumberOfFeaturesInProjectiveClusters()
    {
        return this.clusters[0].getFeatureToClusterAssignments().length;
    }
    
    public Clustering getClustering()
    {
        Cluster[] cl = new Cluster[this.clusters.length];
        ArrayList[] clInstances = new ArrayList[cl.length];
        for (int j=0; j<this.getNumberOfInstances(); j++)
        {
            double max = Double.NEGATIVE_INFINITY;
            int iMax = -1;
            for (int i=0; i<this.getNumberOfClusters(); i++)
            {
                double tmp = (Double)this.clusters[i].getFeatureVectorRepresentation()[j];
                if (tmp > max)
                {
                    max = tmp;
                    iMax = i;
                }
            }
            
            if (clInstances[iMax] == null)
            {
                clInstances[iMax] = new ArrayList<Instance>();
            }
            clInstances[iMax].add(this.instances.get(j));
        }
        
        for (int i=0; i<clInstances.length; i++)
        {
            Instance[] instancesI = null;
            if (clInstances[i] == null || clInstances[i].size() == 0)
            {
                instancesI = new Instance[0];
            }
            else
            {
                instancesI = new Instance[clInstances[i].size()];
                for (int j=0; j<clInstances[i].size(); j++)
                {
                    instancesI[j] = (Instance)clInstances[i].get(j);
                }
            }
            
            cl[i] = new Cluster(instancesI, this.clusters[i].getID(), this.getNumberOfInstances());
        }
        
        return new Clustering(cl, this.getID());
    }
    
    public Instance[] getCentroids(ProjectiveCentroidComputation cc)
    {
        Instance[] centroids = new Instance[this.clusters.length];
        for (int i=0; i<centroids.length; i++)
        {
            this.clusters[i].setCentroidComputation(cc);
            centroids[i] = this.clusters[i].getCentroid();
        }
        
        return centroids;
    }
    
    public double[][] getFeatureRepresentationMatrix()
    {
        double[][] m = new double[this.getNumberOfClusters()][this.getNumberOfFeaturesInProjectiveClusters()];
        for (int i=0; i<m.length; i++)
        {
            Double[] rep = this.clusters[i].getFeatureToClusterAssignments();
            for (int j=0; j<m[0].length; j++)
            {
                m[i][j] = rep[j]; 
            }
        }
        
        return m;
    }
    
    public NumericalInstance[] getNumericalInstanceCentroids(ProjectiveCentroidComputation cc)
    {
        if (this.centroids == null)
        {
            centroids = new NumericalInstance[this.clusters.length];
            for (int i=0; i<centroids.length; i++)
            {
                this.clusters[i].setCentroidComputation(cc);
                centroids[i] = (NumericalInstance)this.clusters[i].getCentroid();
            }
        }
        
        return centroids;
    }
    
    public ProjectiveClustering hardenizeObjectPartitioning()
    {
        ProjectiveCluster[] newClusters = new ProjectiveCluster[this.clusters.length];
        Double[][] newOrep = new Double[this.getNumberOfClusters()][this.getNumberOfInstances()]; 
        Double[][] newFrep = new Double[this.getNumberOfClusters()][this.getNumberOfFeaturesInProjectiveClusters()];
        
        for (int i=0; i<newClusters.length; i++)
        {
            Double[] frep = this.clusters[i].getFeatureToClusterAssignments();
            for (int j=0; j<frep.length; j++)
            {
                newFrep[i][j] = new Double(frep[j].doubleValue());
            }           
        }
        
        for (int j=0; j<newOrep[0].length; j++)
        {
            double max = Double.NEGATIVE_INFINITY;
            int iMax = -1;
            for (int i=0; i<newOrep.length; i++)
            {
                double tmp = this.clusters[i].getFeatureVectorRepresentationDouble()[j];
                if (tmp > max)
                {
                    max = tmp;
                    iMax = i;
                }
            }
            
            for (int i=0; i<newOrep.length; i++)
            {
                if (i == iMax)
                {
                    newOrep[i][j] = new Double(1.0);
                }
                else
                {
                    newOrep[i][j] = new Double(0.0);
                }
            }
        }
        
        for (int i=0; i<newClusters.length; i++)
        {
            newClusters[i] = new ProjectiveCluster(this.getInstances(), newOrep[i], newFrep[i], this.clusters[i].getID(), false, this.clusters[i].getFuzzyFeaturesAssignment());
        }
        
        return new ProjectiveClustering(newClusters, this.getID());
    }

 public ProjectiveClustering hardenizeObjectAndFeaturePartitioning()
    {
        if (this.hardObjectsAndFeatures == null)
        {
            ProjectiveCluster[] newClusters = new ProjectiveCluster[this.clusters.length];
            Double[][] newOrep = new Double[this.getNumberOfClusters()][this.getNumberOfInstances()];
            Double[][] newFrep = new Double[this.getNumberOfClusters()][this.getNumberOfFeaturesInProjectiveClusters()];

            for (int i=0; i<newClusters.length; i++)
            {
                Double[] frep = this.clusters[i].getFeatureToClusterAssignments();
                boolean allZero = true;
                for (int j=0; j<frep.length; j++)
                {
                    if (this.clusters[i].isFuzzyFeaturesAssignment())
                    {
                        if (frep[j] >= (((double)1.0)/frep.length))
                        {
                            newFrep[i][j] = 1.0;
                            allZero = false;
                        }
                        else
                        {
                            newFrep[i][j] = 0.0;
                        }
                    }
                    else
                    {
                        if (frep[j] > 0.0)
                        {
                            newFrep[i][j] = 1.0;
                            allZero = false;
                        }
                        else
                        {
                            newFrep[i][j] = 0.0;
                        }
                    }
                }
                
                if (allZero)
                {
                    for (int j=0; j<frep.length; j++)
                    {
                        newFrep[i][j] = 1.0;
                    }
                }
            }

            for (int j=0; j<newOrep[0].length; j++)
            {
                double max = Double.NEGATIVE_INFINITY;
                int iMax = -1;
                for (int i=0; i<newOrep.length; i++)
                {
                    double tmp = this.clusters[i].getFeatureVectorRepresentationDouble()[j];
                    if (tmp > max)
                    {
                        max = tmp;
                        iMax = i;
                    }
                }

                for (int i=0; i<newOrep.length; i++)
                {
                    if (i == iMax)
                    {
                        newOrep[i][j] = new Double(1.0);
                    }
                    else
                    {
                        newOrep[i][j] = new Double(0.0);
                    }
                }
            }

            for (int i=0; i<newClusters.length; i++)
            {
                newClusters[i] = new ProjectiveCluster(this.getInstances(), newOrep[i], newFrep[i], this.clusters[i].getID(), false, false);
            }

            this.hardObjectsAndFeatures = new ProjectiveClustering(newClusters, this.getID());
        }

        return this.hardObjectsAndFeatures;
    }
 
    protected void validClustering()
    {
        int nObjects = this.clusters[0].getFeatureVectorRepresentation().length;
        int nFeatures = this.clusters[0].getFeatureToClusterAssignments().length;
        
        for (int i=1; i<this.clusters.length; i++)
        {
            if (this.clusters[i].getFeatureVectorRepresentation().length != nObjects || this.clusters[i].getFeatureToClusterAssignments().length != nFeatures)
            {
                throw new RuntimeException("ERROR: all the clusters must have equal-size object- and feature-to-cluster assignments");
            }
        }
        
        for (int j=0; j<nObjects; j++)
        {
            double sum = 0.0;
            for (int i=0; i<this.clusters.length; i++)
            {
                Double[] rep = this.clusters[i].getFeatureVectorRepresentationDouble();
                sum += rep[j];
            }
            
            if (Math.abs(sum-1.0)>=0.0000000001)
            {
                throw new RuntimeException("ERROR: for each object, the sum of object-to-cluster assignments must be equal to 1---sum="+sum);
            }
        }
    }
    
    public int getTotalCoverage()
    {
        if (this.totCoverage < 0)
        {
            this.totCoverage = 0;
            for (int i=0; i<this.clusters.length; i++)
            {
               this.totCoverage += this.clusters[i].getSupport();
            }
        }
        
        return this.totCoverage;
    }
 }

 
 
 
 
 
 
 
 
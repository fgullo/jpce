package objects;

import objects.centroid.FuzzyNumericalInstanceCentroidComputationAVG;
import java.util.ArrayList;

 public class FuzzyClustering extends Instance {
     //in questo caso la diemnsione del vettore delle feature eè uguale a instance.length
    
    /**
     * In this HashMap the keys are the instance IDs and the values are the instances in each cluster
     */
    
    Instance[] instances; 
     
    /**
     * This variable is an array that contains clusters
     */
    protected FuzzyCluster[] clusters;

    /**
     * This is a costructor for Clustering object
     * @param clusters
     */
    public FuzzyClustering (FuzzyCluster[] clusters) {
        ID=genID();
        this.clusters=clusters;
        instances=genInstances();
        
        for (int i=0; i<this.instances.length; i++)
        {
            double sum = 0.0;
            for (int j=0; j<this.clusters.length; j++)
            {
                sum += this.clusters[j].getFeatureVectorRepresentationDouble()[i];
            }
            
            if (Double.isInfinite(sum) || Double.isNaN(sum) || sum < 0.9999999999 || sum > 1.0000000001)
            {
                throw new RuntimeException("ERROR: the sum of the object-to-cluster assignments must be equal to 1---sum="+sum);
            }
        }
    }

    /**
     * This is a costructor for Clustering object
     * @param clusters
     * @param ID
     */
    public FuzzyClustering (FuzzyCluster[] clusters, int ID) {
        this.clusters=clusters;
        this.ID=ID;
        instances=genInstances();
        
        for (int i=0; i<this.instances.length; i++)
        {
            double sum = 0.0;
            for (int j=0; j<this.clusters.length; j++)
            {
                sum += this.clusters[j].getFeatureVectorRepresentationDouble()[i];
            }
            
            if (Double.isInfinite(sum) || Double.isNaN(sum) || sum < 0.9999999999 || sum > 1.0000000001)
            {
                throw new RuntimeException("ERROR: the sum of the object-to-cluster assignments must be equal to 1---sum="+sum);
            }
        }        
    }


    /**
     * This method return the number of Clusters
     * @return int
     */
    public int getNumberOfClusters () {
        return clusters.length;
    }

    /**
     * This method return the number of instances for each cluster
     * @return int
     */
    public int getNumberOfInstances () {
        return instances.length;
    }

    /**
     * This method return all CLusters
     * @return Cluster[]
     */
    public FuzzyCluster[] getClusters () {
        return clusters;
    }
    

    public Instance[] getInstances()
    {
        return this.instances;
    }

    /*
    public double externalClusteringEvaluation (ClusteringSimilarity sim, FuzzyClustering referencePartition) {
        return sim.getSimilarity(referencePartition, this);
    }
     */

    /*
    public double internalClusteringEvaluation (ClusteringInternalValidityCriterion crit, Similarity sim) {
        return crit.getSimilarity(this, sim);
    }
    */

    public Object[] getFeatureVectorRepresentation ()
    {
        return featureVectorRepresentation;
    }
    
    public double getMean() {
        double med=0;
        for(int i = 0; i<clusters.length; i++){
           med=med+clusters[i].getMean();
        }
        return med;
    }

    
    public double getStdDev() {
        double std=0;
        for(int i=0;i<clusters.length; i++){
            std=std+clusters[i].getStdDev();
        }
        return std;
    }

    
    public double getVariance() {
        double var=0;
        for(int i=0;i<clusters.length; i++){
            var=var+clusters[i].getStdDev();
        }
        return var;
    }
    
    /**
     * This method return the HashMap where the keys are the clusters ID and the values are the elements in each cluster
     * @param clusters
     * @return HasMap
     */
    
    private Instance[] genInstances()
    {
        Instance[] instTmp = this.clusters[0].getInstances();
        
        for (int i=0; i<instTmp.length; i++)
        {
            if (instTmp[i].getID() != i)
            {
                throw new RuntimeException("ERROR: the ID does not match the position in the array");
            }
        }
        
        for (int k=1; k<this.clusters.length; k++)
        {
            Instance[] ck = this.clusters[k].getInstances();
            
            for (int i=0; i<ck.length; i++)
            {
                if (ck[i].getID() != i)
                {
                    throw new RuntimeException("ERROR: the ID does not match the position in the array");
                }
                
                if (!ck[i].equals(instTmp[i]))
                {
                    throw new RuntimeException("ERROR: the instances must be equal through each cluster");
                }
            }
        }
        
        return instTmp;
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
        
        for (int i=0; i<m.length; i++)
        {
            m[i][i] = 1;
        }
        
        for (int i=0; i<this.clusters.length; i++)
        {
            Double[] assignments = this.clusters[i].getFeatureVectorRepresentationDouble();
            
            for (int j=0; j<assignments.length-1; j++)
            {
                for (int k=j+1; k<assignments.length; k++)
                {
                    m[j][k] += assignments[j]*assignments[k];
                    m[k][j] = m[j][k];
                }
            }
        }
        
        return m;
    }
    
    public Clustering hardPartition()
    {
        //Double[][] m = new Double[this.getNumberOfClusters()][this.getNumberOfInstances()];
        
        Instance[] data =this.getInstances();
        
        ArrayList[] m = new ArrayList[this.getNumberOfClusters()];
        for (int i=0; i<m.length; i++)
        {
            m[i] = new ArrayList<Instance>();
        }
        
        for (int i=0; i<this.getNumberOfInstances(); i++)
        {
            double max = Double.NEGATIVE_INFINITY;
            int iMax = -1;
            for (int j=0; j<this.getNumberOfClusters(); j++)
            {
                double value = this.clusters[j].getFeatureVectorRepresentationDouble()[i];
                if (value > max)
                {
                    max = value;
                    iMax = j;
                }
            }
            
            m[iMax].add(data[i]);          
        }
        
        Cluster[] newClusters = new Cluster[this.getNumberOfClusters()];
        for (int i=0; i<m.length; i++)
        {
            ArrayList<Instance> list = m[i];
            Instance[] inst = new Instance[list.size()];
            for (int j=0; j<inst.length; j++)
            {
                inst[j] = list.get(j);
            }
            newClusters[i] = new Cluster(inst,this.clusters[i].getID(),data.length);
        }
        
        return new Clustering(newClusters, this.getID());
    }
    
    public static ProjectiveClustering computeSubspaceClusteringLAC(FuzzyClustering clust, Instance[] data, double h)
    {
        //double h = 0.2;

        Double[][] newFeatures = new Double[clust.getNumberOfClusters()][data[0].getNumberOfFeatures()];
        Double[][] newObjects = new Double[clust.getNumberOfClusters()][data.length];
        FuzzyCluster[] refClusters = clust.getClusters();
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
                double[] objTmp = new double[refObjects.length];
                for (int l=0; l<newObjects[0].length; l++)
                {
                    newObjects[j][l] = new Double(refObjects[l].doubleValue());
                    objTmp[l] = refObjects[l].doubleValue();
                }

                Instance centroid = new FuzzyNumericalInstanceCentroidComputationAVG(objTmp,2).getCentroid(refInstances);
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
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 



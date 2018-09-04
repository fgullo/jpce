package objects;

import objects.centroid.CentroidComputation;
import evaluation.cluster.ClusterInternalValidityCriterion;
import evaluation.Similarity;

public class FuzzyCluster extends Instance {

    /**
     * This variable represent a centroid
     */
    protected Instance centroid;

    /**
     * This variable contains the values that constitute the Cluster
     */
    protected Instance[] data;
    
    /**
     * This variable denotes the method that calculates the Centroid
     * 
     */
    private CentroidComputation mCentroidComputation;
    
    private Double[] featureVectorRepresentationDouble;

     

    /**
     * This is a costructor for Cluster object
     * @param data
     * @param ID
     */
    public FuzzyCluster (Instance[] data, Double[] featureVectorRepresentation, int ID) {
        //this.data=data.toArray(new Instance []{});
        this.data=data;
        this.ID=ID;
        
        this.featureVectorRepresentation = featureVectorRepresentation;
        
        if (data.length != featureVectorRepresentation.length)
        {
            throw new RuntimeException("ERROR: data.length MUST BE EQUAL TO featureVectorRepresentation.length");
        }
    }

    /**
     * This is a costructor for Cluster object
     * @param data
     * @param cc
     */
    public FuzzyCluster (Instance[] data, Double[] featureVectorRepresentation, CentroidComputation cc) {
        ID=genID();
        //this.data=data.toArray(new Instance []{});
        this.data=data;
        mCentroidComputation=cc;
        
        this.featureVectorRepresentation = featureVectorRepresentation; 
        
        if (data.length != featureVectorRepresentation.length)
        {
            throw new RuntimeException("ERROR: data.length MUST BE EQUAL TO featureVectorRepresentation.length");
        }             
    }

    /**
     * This is a costructor for Cluster object
     * @param Object[]
     * 
     */
    public FuzzyCluster (Instance[] data, Double[] featureVectorRepresentation) {
        ID=genID();
        this.data=data;
        
        this.featureVectorRepresentation = featureVectorRepresentation;

        if (data.length != featureVectorRepresentation.length)
        {
            throw new RuntimeException("ERROR: data.length MUST BE EQUAL TO featureVectorRepresentation.length");
        }
      
    }
    
    public FuzzyCluster (Instance[] data, Double[] featureVectorRepresentation, int ID, CentroidComputation cc) {
        
        //this.data=data.toArray(new Instance []{});
        this.data=data;
        this.ID=ID;
        mCentroidComputation=cc;
        
        this.featureVectorRepresentation = featureVectorRepresentation;
        
        if (data.length != featureVectorRepresentation.length)
        {
            throw new RuntimeException("ERROR: data.length MUST BE EQUAL TO featureVectorRepresentation.length");
        }      
    }
    
    public Object[] getFeatureVectorRepresentation ()
    {
        return featureVectorRepresentation;
    }

    /**
     * This method return the CentroidComputation
     * @return CentroidComputation
     */
    public CentroidComputation getCentroidComputation ()
    {
        return mCentroidComputation;
    }

    /**
     * This method set the different CentroidComputation
     * @param val
     */
    public void setCentroidComputation (CentroidComputation val)
    {
        mCentroidComputation = val;
    }

    /**
     * This method return the number of Indtances
     * @return int
     */
    public int getNumberOfInstances ()
    {
        return data.length;
    }

    /**
     * This method return all Instances
     * @return Instance[]
     */
    public Instance[] getInstances ()
    {
        return data;
    }
    
    /**
     * Thid method return Centroid
     * @return Instance
     */
    public Instance getCentroid ()
    {     
        centroid=mCentroidComputation.getCentroid(data);
        return centroid;
    }

    /**
     * This method evaluates the Cluster with the internal criterion
     * @param crit
     * @return double
     */
    public double internalClusterEvaluation (ClusterInternalValidityCriterion crit, Similarity sim)
    {
        return crit.getSimilarity(this, sim);
    }

    
    public double getMean() 
    {        
       if (data.length == 0)
       {
           return 0.0;
       }
       double med=0;
       for(int i = 0; i<data.length; i++){
           med=med+data[i].getMean();
       }
       med=med/data.length;
       return med;
    }

    
    public double getStdDev() 
    {
        if (data.length == 0)
        {
            return 0.0;
        }
        double std=0;       
        for(int i=0;i<data.length; i++){
            std=std+data[i].getStdDev();
        }
        return std/data.length;
    }

    
    public double getVariance() {
        
        if (data.length == 0)
        {
            return 0.0;
        }
        double var=0;
        for(int i=0;i<data.length; i++){
            var=var+data[i].getStdDev();
        }
        return var/data.length;
    }
    
    public boolean deepEquals(Cluster c)
    {
        if (c.getNumberOfInstances() != this.getNumberOfInstances())
        {
            return false;
        }
        
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            if (!this.getFeatureVectorRepresentation()[i].equals(c.getFeatureVectorRepresentation()[i]))
            {
                return false;
            }
        }

        
        return true;
    }
    
    public Double[] getFeatureVectorRepresentationDouble ()
    {
        if (this.featureVectorRepresentationDouble == null)
        {
            Double[] v = new Double[this.featureVectorRepresentation.length];
            for (int i=0; i<v.length; i++)
            {
                v[i] = new Double(((Double)this.featureVectorRepresentation[i]).doubleValue());
            }
            
            this.featureVectorRepresentationDouble = v;
        }
        
        return this.featureVectorRepresentationDouble;
    }
    
    /*
    public int numberOfAgreements(Cluster c)
    {
        if (c.n != this.n)
        {
            throw new RuntimeException("The two cluster must belong to the same dataset");
        }
        
        
        int count = 0;
        
        if (this.n < (this.getNumberOfInstances()*c.getNumberOfInstances()))
        {
            for (int i=0; i<n; i++)
            {
                if ((Double)this.getFeatureVectorRepresentation()[i] == 1 && (Double)c.getFeatureVectorRepresentation()[i] == 1)
                {
                    count++;
                }
            }
        }
        else
        {
            for (int i=0; i<this.data.length; i++)
            {
                for (int j=0; j<c.data.length; j++)
                {
                    if (this.data[i].equals(c.data[j]))
                    {
                        count++;
                    }
                }
            }
        }
        
        return count;        
    }
    */
    
}



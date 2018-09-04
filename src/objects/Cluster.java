package objects;

import objects.centroid.CentroidComputation;
import evaluation.cluster.ClusterInternalValidityCriterion;
import evaluation.Similarity;

public class Cluster extends Instance {
    /*
     * This variable represent the feature vector dimension, that is the number of instances in the dataset  
     */
    protected int n;
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
     */
   
    /*
    public Cluster (Instance[] data, int n) {
        ID=genID();
        //this.data=data.toArray(new Instance []{});
        this.data=data;
        this.n=n;
        
        this.featureVectorRepresentation = new Object[n];
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            this.featureVectorRepresentation[i] = new Double(0);
        }
        for (int i=0; i<this.data.length; i++)
        {
            this.featureVectorRepresentation[this.data[i].getID()] = new Double(1);
        }
    }
     * */
     

    /**
     * This is a costructor for Cluster object
     * @param data
     * @param ID
     */
    public Cluster (Instance[] data, int ID, int n) {
        //this.data=data.toArray(new Instance []{});
        this.data=data;
        this.ID=ID;
        this.n=n;
        
        this.featureVectorRepresentation = new Object[n];
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            this.featureVectorRepresentation[i] = new Double(0);
        }
        for (int i=0; i<this.data.length; i++)
        {
            this.featureVectorRepresentation[this.data[i].getID()] = new Double(1);
        }
    }

    /**
     * This is a costructor for Cluster object
     * @param data
     * @param cc
     */
    public Cluster (Instance[] data, CentroidComputation cc, int n) {
        ID=genID();
        //this.data=data.toArray(new Instance []{});
        this.data=data;
        mCentroidComputation=cc;
        this.n=n;
        
        this.featureVectorRepresentation = new Object[n];
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            this.featureVectorRepresentation[i] = new Double(0);
        }
        for (int i=0; i<this.data.length; i++)
        {
            this.featureVectorRepresentation[this.data[i].getID()] = new Double(1);
        }
        
    }

    /**
     * This is a costructor for Cluster object
     * @param Object[]
     * 
     */
    public Cluster (Instance[] data, Object[] feature) {
        ID=genID();
        this.featureVectorRepresentation=feature;
        this.n = this.featureVectorRepresentation.length;
        
        //this.data=data.toArray(new Instance []{});
        this.data=data;
        
    }
    
    public Cluster (Instance[] data, int ID, CentroidComputation cc,int n) {
        
        //this.data=data.toArray(new Instance []{});
        this.data=data;
        this.ID=ID;
        this.n=n;
        mCentroidComputation=cc;
        
        this.featureVectorRepresentation = new Object[n];
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            this.featureVectorRepresentation[i] = new Double(0);
        }
        for (int i=0; i<this.data.length; i++)
        {
            this.featureVectorRepresentation[this.data[i].getID()] = new Double(1);
        }        
    }
    
    public Object[] getFeatureVectorRepresentation () {
        return featureVectorRepresentation;
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

    /**
     * This method return the CentroidComputation
     * @return CentroidComputation
     */
    public CentroidComputation getCentroidComputation () {
        return mCentroidComputation;
    }

    /**
     * This method set the different CentroidComputation
     * @param val
     */
    public void setCentroidComputation (CentroidComputation val) {
        mCentroidComputation = val;
    }

    /**
     * This method return the number of Indtances
     * @return int
     */
    public int getNumberOfInstances () {
        return data.length;
    }

    /**
     * This method return all Instances
     * @return Instance[]
     */
    public Instance[] getInstances () {
        return data;
    }
    
    /**
     * Thid method return Centroid
     * @return Instance
     */
    public Instance getCentroid () {     
        centroid=mCentroidComputation.getCentroid(data);
        return centroid;
    }

    /**
     * This method evaluates the Cluster with the internal criterion
     * @param crit
     * @return double
     */
    public double internalClusterEvaluation (ClusterInternalValidityCriterion crit, Similarity sim) {
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
        if (c.n != this.n)
        {
            return false;
        }
        
        if (c.getNumberOfInstances() != this.getNumberOfInstances())
        {
            return false;
        }
        
        if (this.n < (this.getNumberOfInstances()*c.getNumberOfInstances()))
        {
            for (int i=0; i<n; i++)
            {
                if (!this.getFeatureVectorRepresentation()[i].equals(c.getFeatureVectorRepresentation()[i]))
                {
                    return false;
                }
            }
        }
        else
        {
            for (int i=0; i<this.data.length; i++)
            {
                boolean uguali = false;
                for (int j=0; j<c.data.length; j++)
                {
                    if (this.data[i].equals(c.data[j]))
                    {
                        uguali = true;
                    }
                }
                if(!uguali)
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
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
    
}


package objects;

import objects.centroid.ProjectiveCentroidComputation;
import evaluation.cluster.ClusterInternalValidityCriterion;
import evaluation.Similarity;

public class ProjectiveCluster extends Instance {
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
    private ProjectiveCentroidComputation mCentroidComputation;
    
    
    private Double[] featureToClusterAssignments;
    
    private Double[] featureVectorRepresentationDouble;
    
    
    private boolean fuzzyObjectsAssignment;
    private boolean fuzzyFeaturesAssignment;
    
    private double sumOfObjectAssignments = 0.0;
    private double sumOfFeatureAssignments = 0.0;
    
    private int support = -1;

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
    public ProjectiveCluster (Instance[] data, Double[] objectToClusterAssignments, Double[] featureToClusterAssignments, int ID, boolean fuzzyObjectsAssignment, boolean fuzzyFeaturesAssignment) {
        //this.data=data.toArray(new Instance []{});
        
        this.fuzzyFeaturesAssignment = fuzzyFeaturesAssignment;
        this.fuzzyObjectsAssignment = fuzzyObjectsAssignment;
        
        this.featureVectorRepresentation = objectToClusterAssignments;
        this.n = objectToClusterAssignments.length;
        this.featureToClusterAssignments = featureToClusterAssignments;
        
        this.data=data;
        this.ID=ID;
        
        if (data.length != objectToClusterAssignments.length)
        {
            throw new RuntimeException("ERROR: data.length MUST be equal to fuzzyAssignments.length");
        }
        
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            if (Double.isInfinite(((Double)this.featureVectorRepresentation[i]).doubleValue()) || Double.isNaN(((Double)this.featureVectorRepresentation[i]).doubleValue()))
            {
                throw new RuntimeException("ERROR: the object-to-cluster assignments cannot be neither INFINITY nor NAN---value="+this.featureVectorRepresentation[i]);
            }
            
            if ((Double)this.featureVectorRepresentation[i] < 0.0 || (Double)this.featureVectorRepresentation[i] > 1)
            {
                throw new RuntimeException("ERROR: the object-to-cluster assignments must be within [0,1]---value="+this.featureVectorRepresentation[i]);
            }
            
            this.sumOfObjectAssignments += (Double)this.featureVectorRepresentation[i];
            
        }
        
        for (int i=0; i<this.featureToClusterAssignments.length; i++)
        {
            if (Double.isInfinite(((Double)this.featureToClusterAssignments[i]).doubleValue()) || Double.isNaN(((Double)this.featureToClusterAssignments[i]).doubleValue()))
            {
                throw new RuntimeException("ERROR: the feature-to-cluster assignments cannot be neither INFINITY nor NAN---value="+this.featureToClusterAssignments[i]);
            }
            
            if ((Double)this.featureToClusterAssignments[i] < 0.0 || (Double)this.featureToClusterAssignments[i] > 1)
            {
                throw new RuntimeException("ERROR: the feature-to-cluster assignments must be within [0,1]---value="+this.featureToClusterAssignments[i]);
            }
            
            this.sumOfFeatureAssignments += (Double)this.featureToClusterAssignments[i];
        }        
        
        if (this.fuzzyFeaturesAssignment && Math.abs(this.sumOfFeatureAssignments-1.0)>=0.0000000001)
        {
            throw new RuntimeException("ERROR: the sum of feature-to-cluster assignments must be equal to 1---sum="+this.sumOfFeatureAssignments);
        }
       
        
        int tmp = featureToClusterAssignments.length;
        for (int i=0; i<data.length; i++)
        {
            if (data[i].getNumberOfFeatures() != tmp)
            {
                throw new RuntimeException("ERROR: There is a problem with the number of features of the instances in the clusters e the number of features of the cluster");
            }
        }
    }

    /**
     * This is a costructor for Cluster object
     * @param data
     * @param cc
     */
    public ProjectiveCluster (Instance[] data, Double[] objectToClusterAssignments, Double[] featureToClusterAssignments, ProjectiveCentroidComputation cc, boolean fuzzyObjectsAssignment, boolean fuzzyFeaturesAssignment) {
        
        this.fuzzyFeaturesAssignment = fuzzyFeaturesAssignment;
        this.fuzzyObjectsAssignment = fuzzyObjectsAssignment;
        
        this.n = objectToClusterAssignments.length;
        this.featureVectorRepresentation = objectToClusterAssignments;
        this.featureToClusterAssignments = featureToClusterAssignments;
        
        ID=genID();
        //this.data=data.toArray(new Instance []{});
        mCentroidComputation=cc;
        
        this.data=data;
        
        if (data.length != objectToClusterAssignments.length)
        {
            throw new RuntimeException("ERROR: data.length MUST be equal to fuzzyAssignments.length");
        }
        
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            if (Double.isInfinite(((Double)this.featureVectorRepresentation[i]).doubleValue()) || Double.isNaN(((Double)this.featureVectorRepresentation[i]).doubleValue()))
            {
                throw new RuntimeException("ERROR: the object-to-cluster assignments cannot be neither INFINITY nor NAN");
            }
            
            if ((Double)this.featureVectorRepresentation[i] < 0.0 || (Double)this.featureVectorRepresentation[i] > 1)
            {
                throw new RuntimeException("ERROR: the object-to-cluster assignments must be within [0,1]");
            }
            
            this.sumOfObjectAssignments += (Double)this.featureVectorRepresentation[i];
        }
        
        for (int i=0; i<this.featureToClusterAssignments.length; i++)
        {
            if (Double.isInfinite(((Double)this.featureToClusterAssignments[i]).doubleValue()) || Double.isNaN(((Double)this.featureToClusterAssignments[i]).doubleValue()))
            {
                throw new RuntimeException("ERROR: the feature-to-cluster assignments cannot be neither INFINITY nor NAN");
            }
            
            if ((Double)this.featureToClusterAssignments[i] < 0.0 || (Double)this.featureToClusterAssignments[i] > 1)
            {
                throw new RuntimeException("ERROR: the feature-to-cluster assignments must be within [0,1]");
            }
            
            this.sumOfFeatureAssignments += (Double)this.featureToClusterAssignments[i];
        }  
        
        if (this.fuzzyFeaturesAssignment && Math.abs(this.sumOfFeatureAssignments-1.0)>=0.0000000001)
        {
            throw new RuntimeException("ERROR: the sum of feature-to-cluster assignments must be equal to 1---sum="+this.sumOfFeatureAssignments);
        }
        

        
        int tmp = featureToClusterAssignments.length;
        for (int i=0; i<data.length; i++)
        {
            if (data[i].getNumberOfFeatures() != tmp)
            {
                throw new RuntimeException("ERROR: There is a problem with the number of features of the instances in the clusters e the number of features of the cluster");
            }
        }
        
    }

    /**
     * This is a costructor for Cluster object
     * @param Object[]
     * 
     */
    /*
    public Cluster (Instance[] data, Object[] feature) {
        ID=genID();
        this.featureVectorRepresentation=feature;
        this.n = this.featureVectorRepresentation.length;
        
        //this.data=data.toArray(new Instance []{});
        this.data=data;
        
    }
    */
    
    public ProjectiveCluster (Instance[] data, Double[] objectToClusterAssignments, Double[] featureToClusterAssignments, int ID, ProjectiveCentroidComputation cc, boolean fuzzyObjectsAssignment, boolean fuzzyFeaturesAssignment) {
        
        this.fuzzyFeaturesAssignment = fuzzyFeaturesAssignment;
        this.fuzzyObjectsAssignment = fuzzyObjectsAssignment;
        
        this.n = objectToClusterAssignments.length;
        this.featureVectorRepresentation = objectToClusterAssignments;
        this.featureToClusterAssignments = featureToClusterAssignments; 
        
        
        //this.data=data.toArray(new Instance []{});
        this.ID=ID;
        mCentroidComputation=cc;
        
        this.data=data;
        
        if (data.length != objectToClusterAssignments.length)
        {
            throw new RuntimeException("ERROR: data.length MUST be equal to fuzzyAssignments.length");
        }
        
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            if (Double.isInfinite(((Double)this.featureVectorRepresentation[i]).doubleValue()) || Double.isNaN(((Double)this.featureVectorRepresentation[i]).doubleValue()))
            {
                throw new RuntimeException("ERROR: the object-to-cluster assignments cannot be neither INFINITY nor NAN");
            }
            
            if ((Double)this.featureVectorRepresentation[i] < 0.0 || (Double)this.featureVectorRepresentation[i] > 1)
            {
                throw new RuntimeException("ERROR: the object-to-cluster assignments must be within [0,1]");
            }
            
            this.sumOfObjectAssignments += (Double)this.featureVectorRepresentation[i];
        }
        
        for (int i=0; i<this.featureToClusterAssignments.length; i++)
        {
            if (Double.isInfinite(((Double)this.featureToClusterAssignments[i]).doubleValue()) || Double.isNaN(((Double)this.featureToClusterAssignments[i]).doubleValue()))
            {
                throw new RuntimeException("ERROR: the feature-to-cluster assignments cannot be neither INFINITY nor NAN");
            }
            
            if ((Double)this.featureToClusterAssignments[i] < 0.0 || (Double)this.featureToClusterAssignments[i] > 1)
            {
                throw new RuntimeException("ERROR: the feature-to-cluster assignments must be within [0,1]");
            }
            
            this.sumOfFeatureAssignments += (Double)this.featureToClusterAssignments[i];          
        } 
        
        if (this.fuzzyFeaturesAssignment && Math.abs(this.sumOfFeatureAssignments-1.0)>=0.0000000001)
        {
            throw new RuntimeException("ERROR: the sum of feature-to-cluster assignments must be equal to 1---sum="+this.sumOfFeatureAssignments);
        }
        
        int tmp = featureToClusterAssignments.length;
        for (int i=0; i<data.length; i++)
        {
            if (data[i].getNumberOfFeatures() != tmp)
            {
                throw new RuntimeException("ERROR: There is a problem with the number of features of the instances in the clusters e the number of features of the cluster");
            }
        }
        
     
    }
    
    public Object[] getFeatureVectorRepresentation () {
        return featureVectorRepresentation;
    }
    
    public Double[] getFeatureToClusterAssignments () {
        return this.featureToClusterAssignments;
    }

    /**
     * This method return the CentroidComputation
     * @return CentroidComputation
     */
    public ProjectiveCentroidComputation getCentroidComputation () {
        return mCentroidComputation;
    }

    /**
     * This method set the different CentroidComputation
     * @param val
     */
    public void setCentroidComputation (ProjectiveCentroidComputation val) {
        mCentroidComputation = val;
    }

    /**
     * This method return the number of Indtances
     * @return int
     */
    public int getNumberOfInstances () {
        return data.length;
    }
    
    public int getNumberOfFeatures()
    {
        return this.featureVectorRepresentation.length;
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
    public Instance getCentroid ()
    {     
        centroid=mCentroidComputation.getCentroid(data,this.getFeatureVectorRepresentationDouble());
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
        
        return 0.0;
        
        /*
        if (data.length == 0)
        {
            return 0.0;
        }
        double var=0;
        for(int i=0;i<data.length; i++){
            var=var+data[i].getStdDev();
        }
        return var/data.length;
        */
   }    
 
    public boolean deepEquals(ProjectiveCluster c)
    {
        if (c.n != this.n)
        {
            return false;
        }
        
        if (c.getNumberOfInstances() != this.getNumberOfInstances())
        {
            return false;
        }
        
        for (int i=0; i<this.data.length; i++)
        {
            int id = this.data[i].getID();
            if (this.featureVectorRepresentation[id] != c.getFeatureVectorRepresentation()[id])
            {
                return false;
            }            
        }
        
        for (int i=0; i<this.featureToClusterAssignments.length; i++)
        {
            if (this.featureToClusterAssignments[i] != c.getFeatureToClusterAssignments()[i])
            {
                return false;
            }            
        }
        
        return true;
    }
 
    

    public int numberOfObjectsAgreements(ProjectiveCluster c)
    {
        if (this.isFuzzyObjectsAssignment() || c.isFuzzyObjectsAssignment())
        {
            throw new RuntimeException("The two clusters must not have a fuzzy object-to-cluster assignment");
        }
        
        if (c.n != this.n)
        {
            throw new RuntimeException("The two cluster must belong to the same dataset");
        }
        
        
        int count = 0;
        
        for (int i=0; i<n; i++)
        {
            if ((Double)this.getFeatureVectorRepresentation()[i] == 1 && (Double)c.getFeatureVectorRepresentation()[i] == 1)
            {
                count++;
            }
        }
        
        return count;        
    }
    
    public int numberOfFeaturesAgreements(ProjectiveCluster c)
    {
        if (this.isFuzzyFeaturesAssignment() || c.isFuzzyFeaturesAssignment())
        {
            throw new RuntimeException("The two clusters must not have a fuzzy feature-to-cluster assignment");
        }
        
        if (c.featureToClusterAssignments.length != this.featureToClusterAssignments.length)
        {
            throw new RuntimeException("The two cluster must belong to the same dataset");
        }
        
        
        int count = 0;
        
        for (int i=0; i<this.featureToClusterAssignments.length; i++)
        {
            if ((Double)this.getFeatureToClusterAssignments()[i] == 1 && (Double)c.getFeatureToClusterAssignments()[i] == 1)
            {
                count++;
            }
        }
        
        return count;        
    }
    
    public boolean isFuzzyObjectsAssignment()
    {
        return this.fuzzyObjectsAssignment;
    }
    
    public boolean isFuzzyFeaturesAssignment()
    {
        return this.fuzzyFeaturesAssignment;
    }
    
    public double getSumOfObjectAssignments()
    {
        return this.sumOfObjectAssignments;
    }
    
    public double getSumOfFeatureAssignments()
    {
        return this.sumOfFeatureAssignments;
    }
    
    public Double[] getFeatureVectorRepresentationDouble ()
    {
        if (this.featureVectorRepresentationDouble == null)
        {
            Double[] v = new Double[this.featureVectorRepresentation.length];
            for (int i=0; i<v.length; i++)
            {
                //v[i] = new Double(((Double)this.featureVectorRepresentation[i]).doubleValue());
                v[i] = ((Double)this.featureVectorRepresentation[i]);
            }
            
            this.featureVectorRepresentationDouble = v;
        }
        
        return this.featureVectorRepresentationDouble;
    }
    
    public boolean getFuzzyObjectsAssignment()
    {
        return this.fuzzyObjectsAssignment;
    }
    
    public boolean getFuzzyFeaturesAssignment()
    {
        return this.fuzzyFeaturesAssignment;
    }

    public int getSupport()
    {
        if(this.support < 0)
        {
            if (this.fuzzyObjectsAssignment || this.fuzzyFeaturesAssignment)
            {
                throw new RuntimeException("ERROR: the support of a subspace cluster is defined only for hard object and feature assignemnt");
            }

            int suppO = 0;
            for (int i=0; i<this.featureVectorRepresentationDouble.length; i++)
            {
                if (this.featureVectorRepresentationDouble[i].doubleValue() != 1 && this.featureVectorRepresentationDouble[i].doubleValue() != 0)
                {
                    throw new RuntimeException("ERROR: object assignment must be 0 or 1");
                }            

                if (this.featureVectorRepresentationDouble[i].doubleValue() == 1)
                {
                    suppO++;
                }
            }

            int suppF = 0;
            for (int i=0; i<this.featureToClusterAssignments.length; i++)
            {
                if (this.featureToClusterAssignments[i].doubleValue() != 1 && this.featureToClusterAssignments[i].doubleValue() != 0)
                {
                    throw new RuntimeException("ERROR: feature assignment must be 0 or 1");
                }  

                if (this.featureToClusterAssignments[i].doubleValue() == 1)
                {
                    suppF++;
                }
            }
            this.support = suppO*suppF;
        }

        return this.support;
    }
    
}

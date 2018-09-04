package evaluation.cluster;

import evaluation.Similarity;
import evaluation.numericalinstance.WeightedMinkowskiNumericalInstanceSim;
import evaluation.cluster.ProjectiveClusterInternalValidityCriterion;
import objects.Instance;
import objects.NumericalInstance;
import objects.ProjectiveCluster;

public class ProjectiveClusterWeightedIntraSimilarity extends ProjectiveClusterInternalValidityCriterion {

    private NumericalInstance centroid;
    private double[] weights;
    
  /**
   * This is a costructor for ClusterIntraSimilarity object
   */
   public ProjectiveClusterWeightedIntraSimilarity (NumericalInstance centroid, double[] weights)
   {
       this.centroid = centroid;
       this.weights = weights;
       
       if (this.weights.length != this.centroid.getNumberOfFeatures())
       {
           throw new RuntimeException("ERROR: weights.length must be equal to the number of features of the centroid!");
       }
   }


   /**
    * This method return the similarity betwwen objects present in the cluster, this method considers 
    * all possible combinations beetwen the elements in the cluster
    * @param i
    * @param sim
    * @return double
    */
   public double getSimilarity (Instance i, Similarity s)
   {
        double sum=0.0;
        double norm = 0.0;
       
        WeightedMinkowskiNumericalInstanceSim wsim = new WeightedMinkowskiNumericalInstanceSim(2,this.weights);
        
        Instance[] data = ((ProjectiveCluster)i).getInstances();
        Double[] rep = ((ProjectiveCluster)i).getFeatureVectorRepresentationDouble();
        double n = data.length;
        
        if (n == 0)
        {
            return 0.0;
        }
        
        for(int j=0; j<rep.length-1; j++)
        {
            sum += rep[j]*wsim.getSimilarity(data[j], this.centroid);
            norm += rep[j];
        }
        
        if (norm == 0)
        {
            return 0.0;
        }
        
        return sum/norm;
    }

   /**
    * This method return the distance betwwen objects present in the cluster, this method considers 
    * all possible combinations beetwen the elements in the cluster
    * @param i
    * @param sim
    * @return double
    */
    public double getDistance (Instance i, Similarity s)
    {
        double sum=0.0;
        double norm = 0.0;
       
        WeightedMinkowskiNumericalInstanceSim wsim = new WeightedMinkowskiNumericalInstanceSim(2,this.weights);
        
        Instance[] data = ((ProjectiveCluster)i).getInstances();
        Double[] rep = ((ProjectiveCluster)i).getFeatureVectorRepresentationDouble();
        double n = data.length;
        
        if (n == 0)
        {
            return 0.0;
        }
        
        for(int j=0; j<rep.length-1; j++)
        {
            sum += rep[j]*wsim.getDistance(data[j], this.centroid);
            norm += rep[j];
        }
        
        if (norm == 0)
        {
            return 0.0;
        }
        
        return sum/norm;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ProjectiveClusterWeightedIntraSimilarity))
        {
            return false;
        }
        
        return true;
    }

}



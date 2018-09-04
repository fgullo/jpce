package evaluation.cluster;

import evaluation.Similarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;

public class ClusterIntraSimilarity extends ClusterInternalValidityCriterion {

  /**
   * This is a costructor for ClusterIntraSimilarity object
   */
   public ClusterIntraSimilarity () {
   }

   /**
    * This method return the similarity betwwen objects present in the cluster, this method considers 
    * all possible combinations beetwen the elements in the cluster
    * @param i
    * @param sim
    * @return double
    */
   public double getSimilarity (Instance i, Similarity sim) {
        double sum=0;
       
        Instance []data = ((Cluster)i).getInstances();
        double n = data.length;
        
        if (n == 0)
        {
            return 0.0;
        }
        
        for(int j=0; j<data.length-1; j++){
            for (int k=j+1; k<data.length; k++){
                    sum+=sim.getSimilarity(data[j], data[k]);
            }
        }
       
        double den = n*(n+1)/2;
        return sum/den;
    }

   /**
    * This method return the distance betwwen objects present in the cluster, this method considers 
    * all possible combinations beetwen the elements in the cluster
    * @param i
    * @param sim
    * @return double
    */
    public double getDistance (Instance i, Similarity sim) {
        double sum=0;
       
        Instance []data = ((Cluster)i).getInstances();
        double n = data.length;
        if (n == 0)
        {
            return Double.POSITIVE_INFINITY;
        }
            
        for(int j=0; j<data.length-1; j++){
            for (int k=j+1; k<data.length; k++){
                    sum+=sim.getDistance(data[j], data[k]);
            }
        }
        
        double den = n*(n+1)/2;
        return sum/den;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ClusterIntraSimilarity))
        {
            return false;
        }
        
        return true;
    }

}


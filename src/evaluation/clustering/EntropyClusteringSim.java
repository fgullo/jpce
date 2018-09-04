package evaluation.clustering;

import objects.Cluster;
import objects.Clustering;
import objects.Instance;


/**
 * Similarity between two clustering based on Relative Entropy
 */
public class EntropyClusteringSim extends ClusteringSimilarity {

    public EntropyClusteringSim () 
    {
    }

    public double getSimilarity (Instance i1, Instance i2) 
    {
        double Entropy_dist = getDistance(i1, i2);
        return (1-Entropy_dist);
    }

    public double getDistance (Instance i1, Instance i2) 
    {        
        Cluster [] partition_1 = ((Clustering)i1).getClusters();
        Cluster [] partition_2 = ((Clustering)i2).getClusters();
        double prob_i_j = 0;
        double prob_ij = 0;
        double n = ((Clustering)i1).getNumberOfInstances();
        
        if (n <= 0)
        {
            throw new RuntimeException("Total number of instances must be greater than zero");
        }
        
        if (n != ((Clustering)i2).getNumberOfInstances())
        {
            throw new RuntimeException("The two partitions must have the same number of instances");
        }
        
        double num = 0;
        
        for(int j=0; j<partition_2.length; j++)
        {
            for (int i=0; i<partition_1.length; i++)
            {    
                //double agreements = (double)getNumberOfAgreement(partition_2[j].getInstances(), partition_1[i].getInstances());   
                double agreements = (double)(partition_2[j].numberOfAgreements(partition_1[i]));   
                prob_i_j = agreements/n;
                   
                   if (partition_2[j].getNumberOfInstances()!= 0 && agreements != 0)
                       prob_ij = Math.log(agreements/partition_2[j].getNumberOfInstances());
                   else
                       prob_ij = 0;
                   
                   num+=prob_i_j*prob_ij;
   
            }
        }
        
        double den = partition_2.length*Math.log(partition_1.length);
        
        if (den != 0)
        {
            return -num/den;
        }
        
        return 0.0;        
    }
 
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof EntropyClusteringSim))
        {
            return false;
        }
        
        return true;
    }    
}


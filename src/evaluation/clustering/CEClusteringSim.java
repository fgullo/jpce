package evaluation.clustering;

import evaluation.cluster.ClusterSimilarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;


/**
 *Similarity between two clustering based on Normalized Mutual Information
 */
public class CEClusteringSim extends ClusteringSimilarity
{
    private ClusterSimilarity clusterSim;

    public CEClusteringSim (ClusterSimilarity clusterSim)
    {
        this.clusterSim = clusterSim;
    }
    
    public double getSimilarity (Instance i1, Instance i2)
    {       
        Cluster [] partition1 = ((Clustering)i1).getClusters();
        Cluster [] partition2 = ((Clustering)i2).getClusters();
        
        double[][] confusionMatrix = computeSimConfusionMatrix(partition1, partition2);
        
        return computeCE(confusionMatrix);
    }
    
    public double getDistance (Instance i1, Instance i2) 
    {
        Cluster [] partition1 = ((Clustering)i1).getClusters();
        Cluster [] partition2 = ((Clustering)i2).getClusters();
        
        double[][] confusionMatrix = computeDistConfusionMatrix(partition1, partition2);
        
        return computeCE(confusionMatrix);
    }
    
    private double[][] computeSimConfusionMatrix(Cluster[] partition1, Cluster[] partition2)
    {
        double[][] confusionSimMatrix = new double[partition1.length][partition2.length];
        for (int i=0; i<confusionSimMatrix.length; i++)
        {
            for (int j=0; j<confusionSimMatrix[i].length; j++)
            {
                confusionSimMatrix[i][j] = this.clusterSim.getSimilarity(partition1[i], partition2[j]);
            }
        } 
        
        return confusionSimMatrix;
    }
    
    private double[][] computeDistConfusionMatrix(Cluster[] partition1, Cluster[] partition2)
    {
        double[][] confusionDistMatrix = new double[partition1.length][partition2.length];
        for (int i=0; i<confusionDistMatrix.length; i++)
        {
            for (int j=0; j<confusionDistMatrix[i].length; j++)
            {
                confusionDistMatrix[i][j] = this.clusterSim.getDistance(partition1[i], partition2[j]);
            }
        }
        
        return confusionDistMatrix;
    }
    
    private double computeCE(double[][] confusionMatrix)
    {
        double sum1 = 0.0;
        for (int i=0; i<confusionMatrix.length; i++)
        {
            double max = Double.NEGATIVE_INFINITY;
            for (int j=0; j<confusionMatrix[i].length; j++)
            {
                if (confusionMatrix[i][j] > max)
                {
                    max = confusionMatrix[i][j];
                }
            }
            sum1 += max;
        }
        sum1 /= confusionMatrix.length;
        
        double sum2 = 0.0;
        for (int j=0; j<confusionMatrix[0].length; j++)
        {
            double max = Double.NEGATIVE_INFINITY;
            for (int i=0; i<confusionMatrix.length; i++)
            {
                if (confusionMatrix[i][j] > max)
                {
                    max = confusionMatrix[i][j];
                }
            }
            sum2 += max;
        }
        sum2 /= confusionMatrix[0].length;
        
        return (sum1+sum2)/2;        
    }
    

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof CEClusteringSim))
        {
            return false;
        }
        
        return true;
    }
}

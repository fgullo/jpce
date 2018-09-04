package evaluation.clustering;

import evaluation.Similarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;

public class ClusteringInterSimilarityAVG extends ClusteringInternalValidityCriterion {

    /**
     * This is a costructor for ClusteringInterSimilarityAVG object
     */
    public ClusteringInterSimilarityAVG () {
    }

    /**
     * This method return the similarity between all clusters, the AVG similarity considers 
     * all possible combinations between elements in the same cluster and between elements in different cluster
     * @param i
     * @param sim
     * @return double
     */
    public double getSimilarity (Instance i, Similarity sim) {
        double sum=0;
        Cluster [] clusters=((Clustering)i).getClusters();
        for(int j=0; j<clusters.length-1; j++)
        {
            for(int k=j+1; k<clusters.length; k++)
            {
                double tmp=0;
                Instance []data = clusters[j].getInstances();
                Instance []data1 = clusters [k].getInstances();
                for(int z=0; z<data.length; z++)
                {
                    for (int m=0; m<data1.length; m++)
                    {
                        tmp+=sim.getSimilarity(data[z], data1[m]);
                    }
                }
                double n = data.length*data1.length;
                
                if (n > 0)
                {
                    tmp/=n;
                }
                sum=sum+tmp;               
               
            }
        }
        double den = clusters.length*(clusters.length+1)/2;
        
        if (den != 0)
        {
            return sum/den;
        }
        
        return 0.0;
        //la somma di tutte le medie ottenute dalle varie coppie di cluster deve essere a sua volta divisa
        //per il numero totale di combinazioni di cluster esistenti?
    }

    /**
     * This method return the distance between all clusters, the AVG similarity considers 
     * all possible combinations between elements in the same cluster and between elements in different cluster
     * @param i
     * @param sim
     * @return double
     */
    public double getDistance (Instance i, Similarity sim) {
        double sum=0;
        Cluster [] clusters=((Clustering)i).getClusters();
        for(int j=0; j<clusters.length-1; j++)
        {
            for(int k=j+1; k<clusters.length; k++)
            {
                double tmp=0;
                Instance []data = clusters[j].getInstances();
                Instance []data1 = clusters [k].getInstances();
                for(int z=0; z<data.length-1; z++)
                {
                    for (int m=0; m<data1.length; m++)
                    {
                        tmp+=sim.getDistance(data[z], data1[m]);
                    }
                }
                double n = data.length*data1.length;
                
                if (n > 0)
                {
                    tmp /= n;
                }
                sum=sum+tmp;
            }
        }
        double den = clusters.length*(clusters.length+1)/2;
        
        if (den != 0)
        {
            return sum/den;
        }
        
        return Double.POSITIVE_INFINITY;
        //la somma di tutte le medie ottenute dalle varie coppie di cluster deve essere a sua volta divisa
        //per il numero totale di combinazioni di cluster esistenti?
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ClusteringInterSimilarityAVG))
        {
            return false;
        }
        
        return true;
    }

}


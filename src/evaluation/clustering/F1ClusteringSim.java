package evaluation.clustering;

import objects.Cluster;
import objects.Clustering;
import objects.Instance;

/**
 * Similarity between two clustering based on F-Measure
 */
public class F1ClusteringSim extends ClusteringSimilarity {

    public F1ClusteringSim () {
    }

    public double getSimilarity (Instance i1, Instance i2) {
        double precision = getPrecision(i1, i2);
        double recall = getRecall(i1, i2);
        
        if (precision == 0 || recall == 0)
        {
            return 0.0;
        }
        
        return (2*precision*recall)/(precision+recall);
    }

    public double getDistance (Instance i1, Instance i2) {
        double FM_sim = getSimilarity(i1, i2);
        return (1-FM_sim);
    }
    //effettuare il controllo in cui un cluster è vuoto  tramite le eccezzioni
    public double getPrecision (Instance i1, Instance i2) 
    {
        
        int n = ((Clustering)i1).getNumberOfInstances();
        if (n <= 0)
        {
            throw new RuntimeException("Total number of instances must be greater than zero");
        }
        
        if (n != ((Clustering)i2).getNumberOfInstances())
        {
            throw new RuntimeException("The two partitions must have the same number of instances");
        }        
        
        
        Cluster [] partition_1 = ((Clustering)i1).getClusters();
        Cluster [] partition_2 = ((Clustering)i2).getClusters();
        double precision = 0;
        double best_precision = 0;
        
        for(int i=0; i<partition_1.length; i++)
        {
            for(int j=0; j<partition_2.length; j++)
            {
                double precision_ij=0.0;
                if (partition_2[j].getInstances().length > 0)
                {
                    //precision_ij = ((double)getNumberOfAgreement(partition_2[j].getInstances(), partition_1[i].getInstances()))/partition_2[j].getInstances().length;
                    precision_ij = ((double)partition_2[j].numberOfAgreements(partition_1[i]))/partition_2[j].getNumberOfInstances();
                }

                if(precision_ij > best_precision)
                {
                    best_precision = precision_ij;
                }                        
            
            }
            precision += best_precision;
            best_precision = 0;
        }
        
        if (partition_1.length > 0)
        {
            precision/=partition_1.length;
        }
        return precision;
    }

    public double getRecall (Instance i1, Instance i2) {
        Cluster [] partition_1 = ((Clustering)i1).getClusters();
        Cluster [] partition_2 = ((Clustering)i2).getClusters();
        double recall = 0.0;
        double best_recall = 0.0;
        
        for(int i=0; i<partition_1.length; i++)
        {
            for(int j=0; j<partition_2.length; j++)
            {
                double recall_ij = 0;
                if (partition_1[i].getInstances().length > 0)
                {
                    //recall_ij = ((double)getNumberOfAgreement(partition_2[j].getInstances(), partition_1[i].getInstances()))/partition_1[i].getInstances().length;
                    recall_ij = ((double)partition_2[j].numberOfAgreements(partition_1[i]))/partition_1[i].getNumberOfInstances();
                }

                if(recall_ij > best_recall)
                {
                    best_recall = recall_ij; 
                }                     
            }
            recall += best_recall;
            best_recall = 0;
        }
        
        if (partition_1.length > 0)
        {
            recall/=partition_1.length;
        }
        
        return recall;
    }
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof F1ClusteringSim))
        {
            return false;
        }
        
        return true;
    }  

}


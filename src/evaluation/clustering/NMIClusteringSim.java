package evaluation.clustering;

import evaluation.clustering.ClusteringSimilarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;

/**
 *Similarity between two clustering based on Normalized Mutual Information
 */
public class NMIClusteringSim extends ClusteringSimilarity {

    public NMIClusteringSim () {
    }

    /*
    public double getSimilarity (Instance i1, Instance i2) {      
        
        Cluster [] partition_1 = ((Clustering)i1).getClusters();
        Cluster [] partition_2 = ((Clustering)i2).getClusters();
        double num = 0;
        double den = 0;
       
        double totalElements = ((Clustering)i1).getNumberOfInstances();//lancia er eccezzione se le dimensione delle due partizioni e diversa;
        double H_X = 0;
        double H_Y = -1;
        
        for(int h=0; h<partition_1.length; h++){
                    
            //calcola l'entropia relativa alla variabile X
            Instance [] cluster_i = partition_1[h].getInstances();
            H_X += (cluster_i.length * Math.log10(cluster_i.length/totalElements));
            
            for(int l=0; l<partition_2.length; l++){     
                
                //calcola l'entropia relativa alla variabile Y solo la prima volta che viene eseguito questo for
                Instance [] cluster_j = partition_2[l].getInstances();
                
                if(H_Y < 0)
                    H_Y += (cluster_j.length * Math.log10(cluster_j.length/totalElements));
                
                //calcolo della mutual information fra X e Y
                int agreementNumber = getNumberOfAgreement(cluster_i, cluster_j);
                //effettuare il controllo nel caso in cui uno dei due cluster è 0
                double val_log = Math.log10((totalElements*agreementNumber)/(cluster_i.length*cluster_j.length));
                num += agreementNumber * val_log;

            }            
        }
        
        den = Math.sqrt(H_X*H_Y);
        return num/den;
        
    }
    */
    
    /*
    public double getSimilarity (Instance i1, Instance i2) {      
        
        Cluster [] partition_1 = ((Clustering)i1).getClusters();
        Cluster [] partition_2 = ((Clustering)i2).getClusters();
        double num = 0;
        double den = 0;
       
        double totalElements = ((Clustering)i1).getNumberOfInstances();//lancia er eccezzione se le dimensione delle due partizioni e diversa;
        double H_X = 0;
        double H_Y = -1;
        
        for(int h=0; h<partition_1.length; h++){
                    
            //calcola l'entropia relativa alla variabile X
            Instance [] cluster_i = partition_1[h].getInstances();
            H_X += Utility.returnLog10(cluster_i.length, cluster_i.length/totalElements);
            
            for(int l=0; l<partition_2.length; l++){     
                
                //calcola l'entropia relativa alla variabile Y solo la prima volta che viene eseguito questo for
                Instance [] cluster_j = partition_2[l].getInstances();
                
                if(H_Y < 0)
                    H_Y +=Utility.returnLog10(cluster_j.length, cluster_j.length/totalElements);
                 
                //calcolo della mutual information fra X e Y
                int agreementNumber = getNumberOfAgreement(cluster_i, cluster_j);
               
                double val_log =(totalElements*agreementNumber)/(cluster_i.length*cluster_j.length);
                
                num += Utility.returnLog10(agreementNumber, val_log);

            }            
        }
        
        den = Math.sqrt(H_X*H_Y);
        return num/den;
        
    }
    */
    
        public double getSimilarity (Instance i1, Instance i2) {      
        
        Cluster [] partition_1 = ((Clustering)i1).getClusters();
        Cluster [] partition_2 = ((Clustering)i2).getClusters();
        double num = 0;
        double den = 0;
       
        double totalElements = ((Clustering)i1).getNumberOfInstances();
        
        if (totalElements <= 0)
        {
            throw new RuntimeException("Total number of instances must be greater than zero");
        }
        
        if (totalElements != ((Clustering)i2).getNumberOfInstances())
        {
            throw new RuntimeException("The two partitions must have the same number of instances");
        }
        
        double H1 = 0;
        double H2 = 0;
        for (int h=0; h<partition_1.length; h++)
        {
            double nh1 = (double)partition_1[h].getNumberOfInstances();
            if (nh1 > 0)
            {
                H1 += nh1*Math.log(nh1/totalElements);
            }
        }
        
        for (int l=0; l<partition_2.length; l++)
        {
            double nh2 = (double)partition_2[l].getNumberOfInstances();
            if (nh2 > 0)
            {
                H2 += nh2*Math.log(nh2/totalElements);
            }
        }
        
        if (H1*H2 < 0)
        {
            den = -H1*H2;
        }
        else
        {
            den = H1*H2;
        }
        den = Math.sqrt(den);
        
        
        
        for (int h=0; h<partition_1.length; h++)
        {
            Instance [] cluster_h = partition_1[h].getInstances();
            double nh1 = (double) cluster_h.length;
            for (int l=0; l<partition_2.length; l++)
            {
                Instance [] cluster_l = partition_2[l].getInstances();
                double nh2 = (double)cluster_l.length;
                
                //double nhl = (double)getNumberOfAgreement(cluster_h, cluster_l);
                double nhl = (double)partition_1[h].numberOfAgreements(partition_2[l]);
                
                if (nhl>0 && nh1>0 && nh2>0)
                {
                    num += nhl*Math.log(totalElements*nhl/(nh1*nh2));
                }
                
            }
        }
        
        /*
        if (num >= den)
        {
            System.out.println("P1:");
            for (int h=0; h<partition_1.length; h++)
            {
                Instance [] cluster_h = partition_1[h].getInstances();
                for (int l=0; l<cluster_h.length; l++)
                {
                    System.out.print(cluster_h[l].getID()+" ");
                }
                System.out.println();
            }
            System.out.println("P2:");
            for (int h=0; h<partition_2.length; h++)
            {
                Instance [] cluster_h = partition_2[h].getInstances();
                for (int l=0; l<cluster_h.length; l++)
                {
                    System.out.print(cluster_h[l].getID()+" ");
                }
                System.out.println();
            }                  
        }
        */
        
        if (den != 0)
        {
            if (num >= den)
            {
                System.out.println("SCARTO: "+(num-den));
                return (double)1.0;
            }
            
            return num/den;
        }
        
        return 0.0;
        
    }
    
    public double getDistance (Instance i1, Instance i2) 
    {
        double NMI_sim = getSimilarity(i1, i2);
        return (1-NMI_sim);
    }
    

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof NMIClusteringSim))
        {
            return false;
        }
        
        return true;
    }
}


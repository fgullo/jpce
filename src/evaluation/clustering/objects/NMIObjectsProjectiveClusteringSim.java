package evaluation.clustering.objects;

import evaluation.clustering.objects.ProjectiveClusteringObjectsSimilarity;
import objects.Instance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

/**
 *Similarity between two clustering based on Normalized Mutual Information
 */
public class NMIObjectsProjectiveClusteringSim extends ProjectiveClusteringObjectsSimilarity {

    public NMIObjectsProjectiveClusteringSim ()
    {
       super(true);
    }
    
    public NMIObjectsProjectiveClusteringSim (boolean b)
    {
       super(b);
    }
    
        public double getSimilarity (Instance i1, Instance i2) {      
        
        ProjectiveCluster [] partition_1 = ((ProjectiveClustering)i1).getClusters();
        ProjectiveCluster [] partition_2 = ((ProjectiveClustering)i2).getClusters();
        double num = 0;
        double den = 0;
       
        double totalElements = ((ProjectiveClustering)i1).getNumberOfInstances();
        
        if (totalElements <= 0)
        {
            throw new RuntimeException("Total number of instances must be greater than zero");
        }
        
        if (totalElements != ((ProjectiveClustering)i2).getNumberOfInstances())
        {
            throw new RuntimeException("The two partitions must have the same number of instances");
        }
        
        double H1 = 0;
        double H2 = 0;
        for (int h=0; h<partition_1.length; h++)
        {
            Double[] rep = partition_1[h].getFeatureVectorRepresentationDouble();
            double nh1 = 0.0;
            for (int z=0; z<rep.length; z++)
            {
                nh1 += rep[z];
            }
            
            
            if (nh1 > 0)
            {
                H1 += nh1*(Math.log(nh1)-Math.log(totalElements));
            }
        }
        
        for (int l=0; l<partition_2.length; l++)
        {
            Double[] rep = partition_2[l].getFeatureVectorRepresentationDouble();
            double nh2 = 0.0;
            for (int z=0; z<rep.length; z++)
            {
                nh2 += rep[z];
            }
            
            
            if (nh2 > 0)
            {
                H2 += nh2*(Math.log(nh2)-Math.log(totalElements));
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
            Double[] rep1 = partition_1[h].getFeatureVectorRepresentationDouble();
            double nh1 = 0.0;
            for (int z=0; z<rep1.length; z++)
            {
                nh1 += rep1[z];
            }
            
            for (int l=0; l<partition_2.length; l++)
            {
                Double[] rep2 = partition_2[l].getFeatureVectorRepresentationDouble();
                
                double nh2 = 0.0;
                double nh12 = 0.0; 
                for (int z=0; z<rep2.length; z++)
                {
                    nh2 += rep2[z];
                    nh12 += rep1[z]*rep2[z];
                }
                
                if (nh12>0 && nh1>0 && nh2>0)
                {
                    num += nh12*(Math.log(totalElements)+Math.log(nh12)-Math.log(nh1)-Math.log(nh2));
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
            if (num-den >= 0.000000001)
            {
                System.out.println("NMI (OBJECTS)---GREATER THAN 1---SCARTO: "+(num-den));
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
        if (!(o instanceof NMIObjectsProjectiveClusteringSim))
        {
            return false;
        }
        
        return true;
    }
}
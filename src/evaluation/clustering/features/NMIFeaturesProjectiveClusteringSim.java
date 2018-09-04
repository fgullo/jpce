package evaluation.clustering.features;

import evaluation.clustering.features.ProjectiveClusteringFeaturesSimilarity;
import objects.Instance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

/**
 *Similarity between two clustering based on Normalized Mutual Information
 */
public class NMIFeaturesProjectiveClusteringSim extends ProjectiveClusteringFeaturesSimilarity
{

    public NMIFeaturesProjectiveClusteringSim ()
    {
       super(true);
    }
    
    public NMIFeaturesProjectiveClusteringSim (boolean b)
    {
       super(b);
    }
    
    public double getSimilarity (Instance i1, Instance i2)
    {      

        ProjectiveCluster[] partition_1 = ((ProjectiveClustering)i1).getClusters();
        ProjectiveCluster[] partition_2 = ((ProjectiveClustering)i2).getClusters();
        
        /*
        if (partition_1.length != partition_2.length)
        {
            throw new RuntimeException("ERROR: the two partitions must have the same numberof clusters");
        }
        */
        
        int totalFeatures = 0;
        int size = partition_1[0].getFeatureToClusterAssignments().length;
        for (int x=0; x<size; x++)
        {
            int first = 0;
            for (int a=0; a<partition_1.length; a++)
            {
                if (partition_1[a].getFeatureToClusterAssignments()[x].doubleValue() > 0)
                {
                    first++;
                }
            }
            
            int second = 0;
            for (int b=0; b<partition_2.length; b++)
            {
                if (partition_2[b].getFeatureToClusterAssignments()[x].doubleValue() > 0)
                {
                    second++;
                }
            }
            
            totalFeatures += first*second;
        }
        
        if (totalFeatures == 0)
        {
            return 0.0;
        }
        
        
        
        int totalFeatures1 = 0;
        int totalFeatures2 = 0;
        
        for (int i=0; i<partition_1.length; i++)
        {
            Double[] rep = (Double[])partition_1[i].getFeatureToClusterAssignments();
            for (int j=0; j<rep.length; j++)
            {
                if (rep[j]>0)
                {
                    totalFeatures1++;
                }
            }
        }
        
        for (int i=0; i<partition_2.length; i++)
        {
            Double[] rep = (Double[])partition_2[i].getFeatureToClusterAssignments();
            for (int j=0; j<rep.length; j++)
            {
                if (rep[j]>0)
                {
                    totalFeatures2++;
                }
            }
        }
        
        //int minTotalFeatures = (totalFeatures1>totalFeatures2)?totalFeatures1:totalFeatures2;

        
        
        double num = 0;
        double den = 0;

        //double totalFeatures = ((ProjectiveClustering)i1).getNumberOfFeaturesInSubspaceClusters()*partition_1.length;

        if (totalFeatures1 <= 0 || totalFeatures2<= 0)
        {
            throw new RuntimeException("Total number of features must be greater than zero");
        }

        /*
        if (totalFeatures != ((ProjectiveClustering)i2).getNumberOfFeaturesInSubspaceClusters()*partition_1.length)
        {
            throw new RuntimeException("The two partitions must have the same number of features");
        }
        */

        double H1 = 0;
        double H2 = 0;
        for (int h=0; h<partition_1.length; h++)
        {
            Double[] rep = (Double[])partition_1[h].getFeatureToClusterAssignments();
            int nh1 = 0;
            for (int z=0; z<rep.length; z++)
            {
                if (rep[z]>0.0)
                {
                    nh1++;
                }
            }


            if (nh1 > 0)
            {
                H1 += ((double)nh1)/totalFeatures1*(Math.log(nh1)-Math.log(totalFeatures1));
            }
        }

        for (int l=0; l<partition_2.length; l++)
        {
            Double[] rep = (Double[])partition_2[l].getFeatureToClusterAssignments();
            int nh2 = 0;
            for (int z=0; z<rep.length; z++)
            {
                if (rep[z]>0)
                {
                    nh2++;
                }
            }


            if (nh2 > 0)
            {
                H2 += ((double)nh2)/totalFeatures2*(Math.log(nh2)-Math.log(totalFeatures2));
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
            Double[] rep1 = (Double[])partition_1[h].getFeatureToClusterAssignments();
            int nh1 = 0;
            for (int z=0; z<rep1.length; z++)
            {
                if (rep1[z]>0)
                {
                    nh1++;
                }
            }

            for (int l=0; l<partition_2.length; l++)
            {
                Double[] rep2 = (Double[])partition_2[l].getFeatureToClusterAssignments();

                int nh2 = 0;
                int nh12 = 0; 
                for (int z=0; z<rep2.length; z++)
                {
                    if (rep2[z]>0)
                    {
                        nh2++;
                        if (rep1[z]>0)
                        {
                            nh12++;
                        }
                    }
                }

                if (nh12>0 && nh1>0 && nh2>0)
                {
                    num += ((double)nh12)/totalFeatures*(Math.log(totalFeatures1)+Math.log(totalFeatures2)+Math.log(nh12)-Math.log(nh1)-Math.log(nh2)-Math.log(totalFeatures));
                }
            }
        }


        if (den != 0)
        {
            if (num-den >= 0.000000001)
            {
                //System.out.println("NMI (FEATURES)---GREATER THAN 1---SCARTO: "+(num-den));
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
        if (!(o instanceof NMIFeaturesProjectiveClusteringSim))
        {
            return false;
        }

        return true;
    }
}

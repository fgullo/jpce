package evaluation.clustering.objectsfeatures;

import evaluation.clustering.objectsfeatures.ProjectiveClusteringObjectsFeaturesSimilarity;
import objects.Instance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

/**
 *Similarity between two clustering based on Normalized Mutual Information
 */
public class NMIObjectsFeaturesProjectiveClusteringSim extends ProjectiveClusteringObjectsFeaturesSimilarity
{
    

    public NMIObjectsFeaturesProjectiveClusteringSim ()
    {
        super(true);
    }
    
    public NMIObjectsFeaturesProjectiveClusteringSim (boolean b)
    {
        super(b);
    }
    
    public double getSimilarity (Instance i1, Instance i2)
    {      

        ProjectiveCluster[] partition1 = ((ProjectiveClustering)i1).getClusters();
        ProjectiveCluster[] partition2 = ((ProjectiveClustering)i2).getClusters();
        
        /*
        if (partition_1.length != partition_2.length)
        {
            throw new RuntimeException("ERROR: the two partitions must have the same numberof clusters");
        }
        */
 
        int totalSupp = 0;
        for (int h=0; h<partition1.length; h++)
        {
            Double[] repO1 = partition1[h].getFeatureVectorRepresentationDouble();
            Double[] repF1 = (Double[])partition1[h].getFeatureToClusterAssignments();

            for (int l=0; l<partition2.length; l++)
            {
                Double[] repO2 = partition2[l].getFeatureVectorRepresentationDouble();
                Double[] repF2 = (Double[])partition2[l].getFeatureToClusterAssignments();
                int first = 0;
                int second = 0;
                
                for (int x=0; x<repO1.length; x++)
                {
                    first += repO1[x]*repO2[x];
                }
                
                for (int y=0; y<repF1.length; y++)
                {
                    second += repF1[y]*repF2[y];
                }
                
                totalSupp += first*second; 
            }
        }
        
        if (totalSupp == 0.0)
        {
            return 0.0;
        }
        
        
        
        
        int totCoverage1 = ((ProjectiveClustering)i1).getTotalCoverage();
        int totCoverage2 = ((ProjectiveClustering)i2).getTotalCoverage();
        
        if (totCoverage1 <= 0 || totCoverage2 <= 0)
        {
            throw new RuntimeException("ERROR: totCoverage must be grater than zero---totCoverage1="+totCoverage1+", totCoverage2="+totCoverage2);
        }

        
        //int minCoverage = (totCoverage1<totCoverage2)?totCoverage1:totCoverage2;
        
        
       
        
        double num = 0;
        double den = 0;


        double H1 = 0;
        double H2 = 0;
        for (int h=0; h<partition1.length; h++)
        {
            int supp = partition1[h].getSupport();
            
            if (supp > 0)
            {
                H1 += ((double)supp)/totCoverage1*(Math.log(supp)-Math.log(totCoverage1));
            }
        }

        for (int h=0; h<partition2.length; h++)
        {
            int supp = partition2[h].getSupport();
            
            if (supp > 0)
            {
                H2 += ((double)supp)/totCoverage2*(Math.log(supp)-Math.log(totCoverage2));
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

        for (int h=0; h<partition1.length; h++)
        {
            Double[] repO1 = partition1[h].getFeatureVectorRepresentationDouble();
            Double[] repF1 = (Double[])partition1[h].getFeatureToClusterAssignments();
            int supp1 = partition1[h].getSupport();

            for (int l=0; l<partition2.length; l++)
            {
                Double[] repO2 = partition2[l].getFeatureVectorRepresentationDouble();
                Double[] repF2 = (Double[])partition2[l].getFeatureToClusterAssignments();
                int supp2 = partition2[l].getSupport();
 
                if (repO1.length != repO2.length || repF1.length != repF2.length)
                {
                    throw new RuntimeException("ERROR: object- and feature-to-cluster assignments must be equal size");
                }
                
                int first = 0;
                for (int x=0; x<repO1.length; x++)
                {
                    first += repO1[x]*repO2[x];
                }
                
                int second = 0;
                for (int y=0; y<repF1.length; y++)
                {
                    second += repF1[y]*repF2[y];
                }
                
                int supp12 = first*second; 

                
                if (supp1 >0 && supp2 >0 && supp12>0)
                {
                    num += ((double)supp12)/totalSupp*(Math.log(supp12)+Math.log(totCoverage1)+Math.log(totCoverage2)-Math.log(totalSupp)-Math.log(supp1)-Math.log(supp2));
                }
            }
        }


        if (den != 0)
        {
            if (num-den >= 0.000000001)
            {
                //System.out.println("NMI (OBJECTS-FEATURES)---GREATER THAN 1---SCARTO: "+(num-den));
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
        if (!(o instanceof NMIObjectsFeaturesProjectiveClusteringSim))
        {
            return false;
        }

        return true;
    }
}

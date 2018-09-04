package evaluation.cluster.objectsfeatures;

import objects.Instance;
import objects.ProjectiveCluster;

public class F1ObjectsFeaturesProjectiveClusterSim extends ProjectiveClusterObjectsFeaturesSimilarity
{
   

    public double getSimilarity (Instance i1, Instance i2) 
    {
       
        ProjectiveCluster c1 = (ProjectiveCluster)i1;
        ProjectiveCluster c2 = (ProjectiveCluster)i2;
        
        Double[] repo1 = c1.getFeatureVectorRepresentationDouble();
        Double[] repo2 = c2.getFeatureVectorRepresentationDouble();
        if (repo1.length != repo2.length)
        {
            throw new RuntimeException("ERROR: the two object-based representation vectors must be equal-size");
        }
        
        Double[] repf1 = c1.getFeatureToClusterAssignments();
        Double[] repf2 = c2.getFeatureToClusterAssignments();
        if (repf1.length != repf2.length)
        {
            throw new RuntimeException("ERROR: the two feature-based representation vectors must be equal-size");
        }        

        double den1 = c1.getSumOfObjectAssignments()*c1.getSumOfFeatureAssignments();
        double den2 = c2.getSumOfObjectAssignments()*c2.getSumOfFeatureAssignments();

        if (den1 + den2 == 0.0)
        {
            return 0.0;
        }

        double num = 0.0;
        double first = 0.0;
        double second = 0.0;

        for (int x=0; x<repo1.length; x++)
        {
            first += repo1[x]*repo2[x];
        }
        for (int y=0; y<repf1.length; y++)
        {
            second += repf1[y]*repf2[y];
        }
        num = first*second;

        double ret = 2*num/(den1+den2);
        

        if (Double.isInfinite(ret) || Double.isNaN(ret) || ret < -0.000001 || ret>1.000001)
        {
            throw new RuntimeException("ERROR: FM must be within [0,1]---FM="+ret);
        }

        if (ret<0.0)
        {
            ret = 0.0;
        }

        if (ret>1.0)
        {
            ret = 1.0;
        }
        
        return ret;         
    }

    /**
     * This method return the distance, the distance is (1/similarity)
     * @param i1
     * @param i2
     * @return double
     */
    public double getDistance (Instance i1, Instance i2) 
    {
        return 1.0-getSimilarity(i1,i2);
    }
    

    
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof F1ObjectsFeaturesProjectiveClusterSim))
        {
            return false;
        }
        
        return true;
    }
}



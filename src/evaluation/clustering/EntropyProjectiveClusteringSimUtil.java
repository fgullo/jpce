/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evaluation.clustering;

import objects.Instance;
import objects.ProjectiveClustering;

/**
 *
 * @author gullo
 */
public class EntropyProjectiveClusteringSimUtil 
{
    public static double computeDistance(Instance i1, Instance i2, boolean objects, boolean features)
    {
        if (!objects && !features)
        {
            throw new RuntimeException("ERROR!!!");
        }
        
        ProjectiveClustering sc1 = (ProjectiveClustering)i1;
        ProjectiveClustering sc2 = (ProjectiveClustering)i2;
        
        //ProjectiveClustering sc1 = sc1tmp.hardenizeObjectAndFeaturePartitioning();
        //ProjectiveClustering sc2 = sc2tmp.hardenizeObjectAndFeaturePartitioning();

        double E = 0.0;
        double totSize = 0.0;
        for (int j=0; j<sc2.getNumberOfClusters(); j++)
        {
            Double[] orep2 = sc2.getClusters()[j].getFeatureVectorRepresentationDouble();
            Double[] frep2 = sc2.getClusters()[j].getFeatureToClusterAssignments();
            
            double one = (objects)?sc2.getClusters()[j].getSumOfObjectAssignments():1.0;
            double two = (features)?sc2.getClusters()[j].getSumOfFeatureAssignments():1.0;
            
            double size = one*two;
            totSize += size;
            
            double den = 0.0; 
            double e = 0.0;
            for (int i=0; i<sc1.getNumberOfClusters(); i++)
            {
                Double[] orep1 = sc1.getClusters()[i].getFeatureVectorRepresentationDouble();
                Double[] frep1 = sc1.getClusters()[i].getFeatureToClusterAssignments();

                double num = 0.0;
                double first = 1.0;
                double second = 1.0;
                if (objects)
                {
                    first = 0.0;
                    for (int x=0; x<orep1.length; x++)
                    {
                        first += orep1[x]*orep2[x];
                    }
                }
                if (features)
                {
                    second = 0.0;
                    for (int y=0; y<frep1.length; y++)
                    {
                        second += frep1[y]*frep2[y];
                    }
                }
                num = first*second;

                if (num > 0.000000000001)
                {
                    den +=num;
                    e += num*Math.log(num);
                }
            }
            
            if (den < 0.000000000001)
            {
                den = 0.0;
            }
            
            if (den > 0.0)
            {
                e /= den;
                e -= Math.log(den);
            }
            else
            {
                e = -Math.log(sc1.getNumberOfClusters());//if den==0.0, it means that there has not been any match for cluster j with any class i, thus the entropy is maximum
            }
            
            if (Double.isInfinite(e) || Double.isNaN(e))
            {
                throw new RuntimeException("ERROR: e must be smaller than zero---e="+e);
            }
            if (e > 0.0000001)
            {
                System.out.println("WARNING: e should be smaller than zero---e="+e+"\n");
            }
            
            if (e > 0.0){e = 0.0;}
            
            E -= size*e;         
        }
        
        E /= (Math.log(sc1.getNumberOfClusters())*totSize);
        
        if (Double.isInfinite(E) || Double.isNaN(E) || E > 1.000001 || E < -0.000001)
        {
            throw new RuntimeException("ERROR: E must be within [0,1]---E="+E);
        }
        
        if (E<0.0){E=0.0;}
        if (E>1.0){E=1.0;}

        if (E == 0.0 || E == 1.0)
        {
            int x = 0;
        }
        
        return E;       
    }
}

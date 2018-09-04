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
public class F1ProjectiveClusteringSimUtil
{
    public static double computeMicroFM(Instance i1, Instance i2, boolean objects, boolean features)
    {
        if (!objects && !features)
        {
            throw new RuntimeException("ERROR!!!");
        }
        
        ProjectiveClustering sc1 = (ProjectiveClustering)i1;
        ProjectiveClustering sc2 = (ProjectiveClustering)i2;

        double FM = 0.0;
        for (int i=0; i<sc1.getNumberOfClusters(); i++)
        {
            Double[] objectRep1 = sc1.getClusters()[i].getFeatureVectorRepresentationDouble();
            Double[] featureRep1 = sc1.getClusters()[i].getFeatureToClusterAssignments();
            
            double one = (objects)?sc1.getClusters()[i].getSumOfObjectAssignments():1.0;
            double two = (features)?sc1.getClusters()[i].getSumOfFeatureAssignments():1.0;
            
            double den1 = one*two;
            
            double max = Double.NEGATIVE_INFINITY;
                        
            for (int j=0; j<sc2.getNumberOfClusters(); j++)
            {
                Double[] objectRep2 = sc2.getClusters()[j].getFeatureVectorRepresentationDouble();
                if (objectRep1.length != objectRep2.length)
                {
                    throw new RuntimeException("ERROR: the vectors must be equal size");
                }
                Double[] featureRep2 = sc2.getClusters()[j].getFeatureToClusterAssignments();
                if (featureRep1.length != featureRep2.length)
                {
                    throw new RuntimeException("ERROR: the vectors must be equal size");
                }
                
                one = (objects)?sc2.getClusters()[j].getSumOfObjectAssignments():1.0;
                two = (features)?sc2.getClusters()[j].getSumOfFeatureAssignments():1.0;
                double den2 = one*two;
        
                double num = 0.0;
                double first = 1.0;
                double second = 1.0;
                if (objects)
                {
                    first = 0.0;
                    for (int x=0; x<objectRep1.length; x++)
                    {
                        first += objectRep1[x]*objectRep2[x];
                    }
                }
                if (features)
                {
                    second = 0.0;
                    for (int y=0; y<featureRep1.length; y++)
                    {
                        second += featureRep1[y]*featureRep2[y];
                    }
                }
                num = first*second;

                double fmtmp = 0.0;
                if (den1+den2 > 0.0)
                {
                    fmtmp = 2*num/(den1+den2);
                }
                 
                if (Double.isInfinite(fmtmp) || Double.isNaN(fmtmp) || fmtmp < -0.000001 || fmtmp>1.000001)
                {
                    throw new RuntimeException("ERROR: fmtmp must be within [0,1]---fmtmp="+fmtmp);
                }
                 
                if (fmtmp<0.0)
                {
                    fmtmp = 0.0;
                }
                 
                if (fmtmp>1.0)
                {
                    fmtmp = 1.0;
                }
                
                if (fmtmp > max)
                {
                    max = fmtmp;
                }
             }
            
            FM += max;
        }
        
        FM/=sc1.getNumberOfClusters();
        
        if (Double.isInfinite(FM) || Double.isNaN(FM) || FM < -0.000001 || FM>1.000001)
        {
            throw new RuntimeException("ERROR: FM must be within [0,1]---FM="+FM);
        }
        
        if (FM<0.0){FM=0.0;}
        if (FM>1.0){FM=1.0;}
        
        return FM;
    }
    
    public static double computeMacroFM(Instance i1, Instance i2, boolean objects, boolean features)
    {
        if (!objects && !features)
        {
            throw new RuntimeException("ERROR!!!");
        }
        
        ProjectiveClustering sc1 = (ProjectiveClustering)i1;
        ProjectiveClustering sc2 = (ProjectiveClustering)i2;
        
        //ProjectiveClustering sc1 = sc1tmp.hardenizeObjectAndFeaturePartitioning();
        //ProjectiveClustering sc2 = sc2tmp.hardenizeObjectAndFeaturePartitioning();

        double FM = 0.0;
        for (int i=0; i<sc1.getNumberOfClusters(); i++)
        {
            Double[] orep1 = sc1.getClusters()[i].getFeatureVectorRepresentationDouble();
            Double[] frep1 = sc1.getClusters()[i].getFeatureToClusterAssignments();
            
            double one = (objects)?sc1.getClusters()[i].getSumOfObjectAssignments():1.0;
            double two = (features)?sc1.getClusters()[i].getSumOfFeatureAssignments():1.0;
            
            double den1 = one*two;
            
            if (den1 > 0.0)
            {
                double max = Double.NEGATIVE_INFINITY;
                double p = -1;
                double r = -1;                
                for (int j=0; j<sc2.getNumberOfClusters(); j++)
                {
                    Double[] orep2 = sc2.getClusters()[j].getFeatureVectorRepresentationDouble();
                    Double[] frep2 = sc2.getClusters()[j].getFeatureToClusterAssignments();
                    
                    
                    if (orep1.length != orep2.length)
                    {
                        throw new RuntimeException("ERROR: the two object-based representation vectors must be equal-size");
                    }
                    if (frep1.length != frep2.length)
                    {
                        throw new RuntimeException("ERROR: the two feature-based representation vectors must be equal-size");
                    }
                    
                    one = (objects)?sc2.getClusters()[j].getSumOfObjectAssignments():1.0;
                    two = (features)?sc2.getClusters()[j].getSumOfFeatureAssignments():1.0;
                    double den2 = one*two;
                    
                    double ptmp = 0.0;
                    double rtmp = 0.0;
                    if (den2 > 0.0)
                    {
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
                        
                        ptmp = num/den1;
                        if (Double.isNaN(ptmp) || Double.isInfinite(ptmp) || ptmp < -0.0000001 || ptmp > 1.0000001)
                        {
                            throw new RuntimeException("ERROR: ptmp must be within [0,1]---ptmp="+ptmp);
                        }
                        
                        rtmp = num/den2;
                        if (Double.isNaN(rtmp) || Double.isInfinite(rtmp) || rtmp < -0.0000001 || rtmp > 1.0000001)
                        {
                            throw new RuntimeException("ERROR: rtmp must be within [0,1]---ptmp="+rtmp);
                        }
                    }
                        
                    if (ptmp > max)
                    {
                        max = ptmp;
                        p = ptmp;
                        r = rtmp;
                    }
                    else if (rtmp > max)
                    {
                        max = rtmp;
                        p = ptmp;
                        r = rtmp;
                    } 
                }
                
                if (Double.isNaN(p) || Double.isInfinite(p) || p < -0.0000001 || p > 1.0000001)
                {
                    throw new RuntimeException("ERROR: p must be within [0,1]---p="+p);
                }
                if (Double.isNaN(r) || Double.isInfinite(r) || r < -0.0000001 || r > 1.0000001)
                {
                    throw new RuntimeException("ERROR: r must be within [0,1]---r="+r);
                }
                
                if (p+r > 0.0)
                {
                    FM += 2*p*r/(p+r);
                }
            }
            
        }
        
        if (sc1.getNumberOfClusters() > 0)
        {
            FM /= sc1.getNumberOfClusters(); 
        }
        
        if (Double.isNaN(FM) || Double.isInfinite(FM) || FM < -0.0000001 || FM > 1.0000001)
        {
            throw new RuntimeException("ERROR: FM must be within [0,1]---FM="+FM);
        }
        
        if (FM<0.0){FM=0.0;}
        if (FM>1.0){FM=1.0;}

        return FM;
    }
}

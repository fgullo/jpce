/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moea;

import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

public class FeatureBasedObjectPartitioningGAFunction extends PCEGAFunction
{
    protected double[][] pairwiseFeatureDistances;
    
    public FeatureBasedObjectPartitioningGAFunction(double[][] pairwiseFeatureDistances)
    {
        this.pairwiseFeatureDistances = pairwiseFeatureDistances;
    }
   
    public double evaluate(Object s)
    {
        ProjectiveClustering solution = (ProjectiveClustering)s;
        
        ProjectiveCluster[] clusters = solution.getClusters();
        double value = 0.0;
        for (int i=0; i<clusters.length; i++)
        {
            Double[] featuresRep = (Double[])clusters[i].getFeatureToClusterAssignments();
            double h = 0.0;
            double valueTmp = 0.0;
            for (int j1=0; j1<featuresRep.length; j1++)
            {
                for (int j2=0; j2<featuresRep.length; j2++)
                {
                    if (j1 != j2)
                    {
                        double coeff = featuresRep[j1]*featuresRep[j2];
                        h += coeff;
                        valueTmp += coeff*this.pairwiseFeatureDistances[j1][j2];                       
                    }
                }
            }
            value += (valueTmp/h);         
        }
        
        return value;        
    }

}

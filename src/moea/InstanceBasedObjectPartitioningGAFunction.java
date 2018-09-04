/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moea;

import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

public class InstanceBasedObjectPartitioningGAFunction extends PCEGAFunction
{
    protected double[][] coNonOccurrenceObjectMatrix;
    
    public InstanceBasedObjectPartitioningGAFunction(double[][] coNonOccurrenceObjectMatrix)
    {
        this.coNonOccurrenceObjectMatrix = coNonOccurrenceObjectMatrix;
    }
   
    public double evaluate(Object s)
    {
        ProjectiveClustering solution = (ProjectiveClustering)s;
        
        ProjectiveCluster[] clusters = solution.getClusters();
        double value = 0.0;
        for (int i=0; i<clusters.length; i++)
        {
            Double[] objectsRep = clusters[i].getFeatureVectorRepresentationDouble();
            double h = 0.0;
            double valueTmp = 0.0;
            for (int j1=0; j1<objectsRep.length; j1++)
            {
                for (int j2=0; j2<objectsRep.length; j2++)
                {
                    if (j1 != j2)
                    {
                        double coeff = objectsRep[j1]*objectsRep[j2];
                        h += coeff;
                        valueTmp += coeff*this.coNonOccurrenceObjectMatrix[j1][j2];                       
                    }
                }
            }
            value += (valueTmp/h);         
        }
        
        return value;        
    }

}

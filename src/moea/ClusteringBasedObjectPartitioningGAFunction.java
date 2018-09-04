/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moea;

import evaluation.clustering.objects.ProjectiveClusteringObjectsSimilarity;
import objects.ProjectiveClustering;

public class ClusteringBasedObjectPartitioningGAFunction extends PCEGAFunction
{
    protected ProjectiveClusteringObjectsSimilarity sim;
    protected ProjectiveClustering[] ensemble;
    
    public ClusteringBasedObjectPartitioningGAFunction (ProjectiveClustering[] ensemble, ProjectiveClusteringObjectsSimilarity sim)
    {
        this.sim = sim;
        this.ensemble = ensemble;
    }
   
    public double evaluate(Object s)
    {
        ProjectiveClustering solution = (ProjectiveClustering)s;
        
        double value = 0.0;
        for (int i=0; i<this.ensemble.length; i++)
        {
            value += this.sim.getDistance(solution, ensemble[i]);
        }
        value /= this.ensemble.length;
        
        return value;        
    }

}

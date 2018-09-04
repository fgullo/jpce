/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evaluation.clustering.objects;

import evaluation.clustering.EntropyProjectiveClusteringSimUtil;
import evaluation.clustering.objects.ProjectiveClusteringObjectsSimilarity;
import objects.Instance;

public class EntropyObjectsProjectiveClusteringSim extends ProjectiveClusteringObjectsSimilarity
{
    
    public EntropyObjectsProjectiveClusteringSim()
    {
        super(false);
    }
    
    public EntropyObjectsProjectiveClusteringSim(boolean b)
    {
        super(b);
    }
    
    // i1 is the reference classification
    public double getDistance(Instance i1, Instance i2)
    {
        boolean objects = true;
        boolean features = false;
        
        return Math.min(EntropyProjectiveClusteringSimUtil.computeDistance(i1, i2, objects, features), EntropyProjectiveClusteringSimUtil.computeDistance(i2, i1, objects, features));
    }
    
    public double getSimilarity(Instance i1, Instance i2)
    {
        return (double)1.0 - this.getDistance(i1, i2);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof EntropyObjectsProjectiveClusteringSim))
        {
            return false;
        }

        return true;
    }

}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evaluation.clustering.objectsfeatures;

import evaluation.clustering.EntropyProjectiveClusteringSimUtil;
import evaluation.clustering.objectsfeatures.ProjectiveClusteringObjectsFeaturesSimilarity;
import objects.Instance;

public class EntropyObjectsFeaturesProjectiveClusteringSim extends ProjectiveClusteringObjectsFeaturesSimilarity
{
    
    public EntropyObjectsFeaturesProjectiveClusteringSim()
    {
        super(false);
    }
    
    public EntropyObjectsFeaturesProjectiveClusteringSim(boolean b)
    {
        super(b);
    }
    
    
    // i1 is the reference classification
    public double getDistance(Instance i1, Instance i2)
    {
        boolean objects = true;
        boolean features = true;
        
        return Math.min(EntropyProjectiveClusteringSimUtil.computeDistance(i1, i2, objects, features), EntropyProjectiveClusteringSimUtil.computeDistance(i2, i1, objects, features));
    }
    
    public double getSimilarity(Instance i1, Instance i2)
    {
        return (double)1.0 - this.getDistance(i1, i2);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof EntropyObjectsFeaturesProjectiveClusteringSim))
        {
            return false;
        }

        return true;
    }

}

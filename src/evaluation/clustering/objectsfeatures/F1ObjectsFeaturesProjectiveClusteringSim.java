/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evaluation.clustering.objectsfeatures;

import evaluation.clustering.F1ProjectiveClusteringSimUtil;
import evaluation.clustering.objectsfeatures.ProjectiveClusteringObjectsFeaturesSimilarity;
import objects.Instance;


public class F1ObjectsFeaturesProjectiveClusteringSim extends ProjectiveClusteringObjectsFeaturesSimilarity
{
    protected boolean macro;
    
    public F1ObjectsFeaturesProjectiveClusteringSim(boolean macro)
    {
        super(false);
        this.macro = macro;
    }
    
    public F1ObjectsFeaturesProjectiveClusteringSim(boolean macro, boolean b)
    {
        super(b);
        this.macro = macro;
    }
    
    
    // i1 is the reference classification
    public double getSimilarity (Instance i1, Instance i2)
    {
        boolean objects = true;
        boolean features = true;
        
        if (macro)
        {
            return Math.max(F1ProjectiveClusteringSimUtil.computeMacroFM(i1, i2, objects, features),  F1ProjectiveClusteringSimUtil.computeMacroFM(i2, i1, objects, features));
        }
        
        return Math.max(F1ProjectiveClusteringSimUtil.computeMicroFM(i1, i2, objects, features),  F1ProjectiveClusteringSimUtil.computeMicroFM(i2, i1, objects, features));
    }


    public double getDistance (Instance i1, Instance i2)
    {
        return 1-this.getSimilarity(i1, i2);
    }


    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof F1ObjectsFeaturesProjectiveClusteringSim))
        {
            return false;
        }

        return true;
    }

}

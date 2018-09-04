/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evaluation.clustering.objects;

import evaluation.clustering.F1ProjectiveClusteringSimUtil;
import evaluation.clustering.objects.ProjectiveClusteringObjectsSimilarity;
import objects.Instance;
import objects.ProjectiveClustering;

public class F1ObjectsProjectiveClusteringSim extends ProjectiveClusteringObjectsSimilarity
{
    protected boolean macro;
    
    public F1ObjectsProjectiveClusteringSim(boolean macro)
    {
        super(false);
        this.macro = macro;
    }
    
    public F1ObjectsProjectiveClusteringSim(boolean macro, boolean b)
    {
        super(b);
        this.macro = macro;
    }
    
    
    // i1 is the reference classification
    public double getSimilarity (Instance i1, Instance i2)
    {
        boolean objects = true;
        boolean features = false;
        
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
        if (!(o instanceof F1ObjectsProjectiveClusteringSim))
        {
            return false;
        }

        return true;
    }

}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evaluation.clustering.objects;

import evaluation.clustering.objects.ProjectiveClusteringObjectsSimilarity;
import objects.Instance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

public class F1ObjectsProjectiveClusteringSimHARD extends ProjectiveClusteringObjectsSimilarity
{
    protected boolean macro;
    
    public F1ObjectsProjectiveClusteringSimHARD(boolean macro)
    {
        super(true);
        this.macro = macro;
    }
    
    public F1ObjectsProjectiveClusteringSimHARD(boolean macro, boolean b)
    {
        super(b);
        this.macro = macro;
    }
    
    
    // i1 is the reference classification
    public double getSimilarity (Instance i1, Instance i2)
    {
        ProjectiveClustering pc1 = ((ProjectiveClustering)i1).hardenizeObjectAndFeaturePartitioning();
        ProjectiveClustering pc2 = ((ProjectiveClustering)i2).hardenizeObjectAndFeaturePartitioning();
        F1ObjectsProjectiveClusteringSim fm = new F1ObjectsProjectiveClusteringSim(macro);
        
        return fm.getSimilarity(pc1, pc2);
    }


    public double getDistance (Instance i1, Instance i2)
    {
        return 1-this.getSimilarity(i1, i2);
    }


    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof F1ObjectsProjectiveClusteringSimHARD))
        {
            return false;
        }

        return true;
    }

}


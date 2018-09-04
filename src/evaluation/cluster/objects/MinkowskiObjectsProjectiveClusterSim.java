package evaluation.cluster.objects;

import evaluation.numericalinstance.MinkowskiNumericalInstanceSim;
import evaluation.cluster.objects.ProjectiveClusterObjectsSimilarity;
import objects.Instance;
import objects.NumericalInstance;
import objects.ProjectiveCluster;

public class MinkowskiObjectsProjectiveClusterSim extends ProjectiveClusterObjectsSimilarity {

    protected int p;
    
    public MinkowskiObjectsProjectiveClusterSim (int p)
    {
        this.p = p;
    }
    
    public double getSimilarity (Instance i1, Instance i2) 
    {
        ProjectiveCluster c1 = (ProjectiveCluster)i1;
        ProjectiveCluster c2 = (ProjectiveCluster)i2;
        
        Double[] rep1 = c1.getFeatureVectorRepresentationDouble();
        Double[] rep2 = c2.getFeatureVectorRepresentationDouble();
        
        NumericalInstance inst1 = new NumericalInstance(rep1);
        NumericalInstance inst2 = new NumericalInstance(rep2);
        
        return new MinkowskiNumericalInstanceSim(this.p).getSimilarity(inst1, inst2);
    }
        
 
    public double getDistance (Instance i1, Instance i2) 
    {
        ProjectiveCluster c1 = (ProjectiveCluster)i1;
        ProjectiveCluster c2 = (ProjectiveCluster)i2;
        
        Double[] rep1 = c1.getFeatureVectorRepresentationDouble();
        Double[] rep2 = c2.getFeatureVectorRepresentationDouble();
        
        NumericalInstance inst1 = new NumericalInstance(rep1);
        NumericalInstance inst2 = new NumericalInstance(rep2);
        
        return new MinkowskiNumericalInstanceSim(this.p).getDistance(inst1, inst2);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MinkowskiObjectsProjectiveClusterSim))
        {
            return false;
        }
        
        return true;
    }
}




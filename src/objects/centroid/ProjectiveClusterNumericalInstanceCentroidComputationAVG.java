package objects.centroid;

import objects.Instance;
import objects.NumericalInstance;


public class ProjectiveClusterNumericalInstanceCentroidComputationAVG implements ProjectiveCentroidComputation {

    private Double[] rep;
    
    public ProjectiveClusterNumericalInstanceCentroidComputationAVG (Double[] rep)
    {
        this.rep = rep;
    }
    
    public ProjectiveClusterNumericalInstanceCentroidComputationAVG ()
    {

    }

    public Instance getCentroid (Instance[] data, Double[] rep)
    {
        this.rep = rep;
        return this.getCentroid(data);
    }
    
    
    
    public Instance getCentroid (Instance[] data) 
    {        
        if (data.length != rep.length)
        {
            throw new RuntimeException("ERROR: data.length must be equal to rep.length!");
        }
        
        double[] dataCentroid = new double[((NumericalInstance)data[0]).getNumberOfFeatures()];
        
        for (int j=0; j<dataCentroid.length; j++)
        {
            double sum = 0.0;
            for (int i=0; i<data.length; i++)
            {
                Double[] curr = ((NumericalInstance)data[i]).getDataVector();
                dataCentroid[j] += rep[i]*curr[j];
                sum += rep[i];
            }
            dataCentroid[j] /= sum; 
        }
        
        return new NumericalInstance(dataCentroid);

    }
}


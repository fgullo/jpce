package objects.centroid;

import objects.Instance;
import objects.NumericalInstance;

public class FuzzyNumericalInstanceCentroidComputationAVG extends FuzzyCentroidComputation {
    

    public FuzzyNumericalInstanceCentroidComputationAVG (double[] assignments, int m)
    {
        this.assignments = assignments;
        this.m = m;
    }
    
    public Instance getCentroid (Instance[] data) 
    {        
        double[] dataCentroid = new double[((NumericalInstance)data[0]).getNumberOfFeatures()];
        
        double den = 0.0;
        for (int i=0; i<this.assignments.length; i++)
        {
            den += Math.pow(this.assignments[i],this.m);
        }
        
        if (den == 0.0)
        {
            return new NumericalInstance(dataCentroid);
        }
        
        for (int i=0; i<dataCentroid.length; i++)
        {
            for (int j=0; j<data.length; j++)
            {
                dataCentroid[i] += Math.pow(this.assignments[j], this.m)*((NumericalInstance)data[j]).getDataVector()[i];
            }
            dataCentroid[i] /= den;
        }
        
        return new NumericalInstance(dataCentroid);

    }
}



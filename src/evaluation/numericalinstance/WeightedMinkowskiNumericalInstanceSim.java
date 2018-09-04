package evaluation.numericalinstance;

import objects.Instance;
import objects.NumericalInstance;

public class WeightedMinkowskiNumericalInstanceSim extends NumericalInstanceSimilarity {

    /**
     * This variable reppresent 
     */
    protected int p;
    protected double[] weights;

    /**
     * This is a costructor for CosineNumericalInstanceSim object
     */
    public WeightedMinkowskiNumericalInstanceSim (int p, double[] weights) 
    {
        this.p=p;
        this.weights = weights;
    }
    
    public WeightedMinkowskiNumericalInstanceSim (int p) 
    {
        this.p=p;
    }

    /**
     * This method return the variable p
     * @return int
     */
    public int getP () 
    {
        return p;
    }

    /**
     * This method return the similarity, the similarity is (1/distance)
     * @param i1
     * @param i2
     * @return double
     */
    public double getSimilarity (Instance i1, Instance i2) 
    {
        double distance = getDistance(i1, i2);
        
        if (distance > 0)
        {
            return 1/distance;    
        }
        
        return Double.POSITIVE_INFINITY;
    }

    /**
     * This method return the distance between two NumericalInstance, this method use the Minkowski Distance
     * when p=1 this distance is called Manhattan Distance
     * when p=2 this distance is called Euclidean Distance
     * when p=infinite this distance is called Chebyshev Distance 
     * @param i1
     * @param i2
     * @return double
     */
    public double getDistance (Instance i1, Instance i2) 
    {
        Double data[] = ((NumericalInstance)i1).getDataVector();
        Double data1[] = ((NumericalInstance)i2).getDataVector();
        
        if (data.length != data.length)
        {
            throw new RuntimeException("The instances must have the same number of features");
        }
        
        if (weights.length != data.length)
        {
            throw new RuntimeException("The size of the vector of weights must be equal to the number of features of the two instances");
        }        
        
        double sum=0;
        for(int i=0; i<data.length;i++)
        {
            sum = sum + weights[i]*Math.pow(Math.abs(data[i]-data1[i]), p); 
        }
        return sum = Math.pow(sum, ((double)1.0)/p); 
    }
    
    public double getSimilarity (Instance i1, Instance i2, double[] w) 
    {
        this.weights = w;
        return this.getSimilarity(i1, i2);
    }


    public double getDistance (Instance i1, Instance i2, double[] w) 
    {
        this.weights = w;
        return this.getDistance(i1, i2);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof WeightedMinkowskiNumericalInstanceSim))
        {
            return false;
        }
        
        return true;
    }
}



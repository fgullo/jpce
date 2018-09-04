package evaluation.numericalinstance;

import objects.Instance;
import objects.NumericalInstance;

public class WeightedHammingNumericalInstanceSim extends NumericalInstanceSimilarity {

    protected double[] weights;

    /**
     * This is a costructor for CosineNumericalInstanceSim object
     */
    public WeightedHammingNumericalInstanceSim (double[] weights) 
    {
        this.weights = weights;
    }


    /**
     * This method return the similarity, the similarity is (1/distance)
     * @param i1
     * @param i2
     * @return double
     */
    public double getSimilarity (Instance i1, Instance i2) 
    {
        Double data1[] = ((NumericalInstance)i1).getDataVector();
        Double data2[] = ((NumericalInstance)i2).getDataVector();
        
        if (data1.length != data2.length)
        {
            throw new RuntimeException("The instances must have the same number of features");
        }
        
        double count=0;
        for(int i=0; i<data1.length; i++)
        {
            if(data1[i].equals(data2[i]))
            {
                count+=weights[i];
            }
        }
        return count;
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
        Double data1[] = ((NumericalInstance)i1).getDataVector();
        Double data2[] = ((NumericalInstance)i2).getDataVector();
        
        if (data1.length != data2.length)
        {
            throw new RuntimeException("The instances must have the same number of features");
        }
        
        double count=0;
        
        for(int i=0; i<data1.length; i++)
        {
            if(!data1[i].equals(data2[i]))
            {
                count+=weights[i];
            }
        }
        return count;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof WeightedHammingNumericalInstanceSim))
        {
            return false;
        }
        
        return true;
    }
}



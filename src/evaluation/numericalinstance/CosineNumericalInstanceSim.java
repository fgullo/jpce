package evaluation.numericalinstance;

import objects.Instance;
import objects.NumericalInstance;

public class CosineNumericalInstanceSim extends NumericalInstanceSimilarity {
    
    /**
     * This is a costructor for CosineNumericalInstanceSim object
     */
    public CosineNumericalInstanceSim () {
    }
    
    /**
     * This method return the similarity between two NumericalInstance, the similarity is calculated 
     * with Cosine Similarity
     * @param i1
     * @param i2
     * @return double
     */
    public double getSimilarity (Instance i1, Instance i2) 
    {
        double num=0;
        double den1=0;
        double den2=0;
        Double data[] = ((NumericalInstance)i1).getDataVector();
        Double data1[] = ((NumericalInstance)i2).getDataVector();
        
        if (data.length != data.length)
        {
            throw new RuntimeException("The instances must have the same number of features");
        }        
        
        for(int i =0; i<data.length; i++)
        {
            num = num + (data[i]*data1[i]);
            den1 = den1 + Math.pow(data[i], 2);
            den2 = den2 + Math.pow(data1[i], 2);
        }
        den1 = Math.sqrt(den1);
        den2 = Math.sqrt(den2);
        
        double den = den1*den2;
        
        if (den != 0)
        {
            return num/(den1*den2);
        }
        
        return 0.0;
    }
    
    /**
     * This method return the distance, the distance is (1/similarity)
     * @param i1
     * @param i2
     * @return double
     */
    public double getDistance (Instance i1, Instance i2) {
        double similarity = getSimilarity(i1,i2);
        
        if (similarity != 0)
        {
            return ((double)1.0)/similarity;
        }
        
        return Double.POSITIVE_INFINITY;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof CosineNumericalInstanceSim))
        {
            return false;
        }
        
        return true;
    }    
    
}


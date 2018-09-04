package evaluation.numericalinstance;

import objects.Instance;
import objects.NumericalInstance;

public class JaccardNumericalInstanceSim extends NumericalInstanceSimilarity {
    
    /**
     * This is a costructor for JaccardNumericalInstanceSim object
     */
    public JaccardNumericalInstanceSim () {
    }
    
    /**
     * This method return the similarity between two NumericalInstance, the similarity is calculated 
     * with Jaccard Similarity
     * @param i1
     * @param i2
     * @return double
     */
    public double getSimilarity (Instance i1, Instance i2) {
        double num=0;
        double den1=0;
        double den2=0;
        Double data[] = ((NumericalInstance)i1).getDataVector();
        Double data1[] = ((NumericalInstance)i2).getDataVector();
        
        if (data.length != data1.length)
        {
            throw new RuntimeException("The instances must have the same number of features");
        }        
        
        for(int i =0; i<data.length; i++)
        {
            num += data[i]*data1[i];
            den1 += data[i]*data[i];
            den2 += data1[i]*data1[i];
        }
        //den1 = Math.sqrt(den1);
        //den2 = Math.sqrt(den2);
        double den = den1+den2-num;
        
        double ret = 1.0;
        if (den != 0.0)
        {
            ret = num/den;
        }
        
        if (ret < -0.000001 || ret > 1.000001)
        {
            throw new RuntimeException("ERROR: The value must be within [0,1]");
        }
        
        return ret;
    }
    
    /**
     * This method return the distance, the distance is (1/similarity)
     * @param i1
     * @param i2
     * @return double
     */
    public double getDistance (Instance i1, Instance i2)
    {
        return 1-this.getSimilarity(i1, i2);        
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof JaccardNumericalInstanceSim))
        {
            return false;
        }
        
        return true;
    }
}


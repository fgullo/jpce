package evaluation.numericalinstance;

import objects.Instance;
import objects.NumericalInstance;

public class HammingNumericalInstanceSim extends NumericalInstanceSimilarity {
    
    /**
     * This is a costructor for JaccardNumericalInstanceSim object
     */
    public HammingNumericalInstanceSim () {
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
                count++;
            }
        }
        return count;
    }

    /**
     * This method return the distance between two NumericalInstance, this method use the Hamming Distance
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
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof HammingNumericalInstanceSim))
        {
            return false;
        }
        
        return true;
    }    
}


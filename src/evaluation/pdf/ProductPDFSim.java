package evaluation.pdf;

import util.Util;

public class ProductPDFSim extends PDFSimilarity
{
    public double getSimilarity (Double[] i1, Double[] i2) 
    {
        if (i1.length != i2.length)
        {
            throw new RuntimeException("Vectors of probabilities must be equal length");
        }
        
        double sum = 0.0;
        for (int i=0; i<i1.length; i++)
        {
            sum += i1[i]*i2[i];
        }
        
        Util.throwException(sum, 0.0, 1.0);
        
        if (sum < 0.0){sum = 0.0;}
        else if (sum > 1.0){sum = 1.0;}
        
        
        return sum;
        
    }

    public double getDistance (Double[] i1, Double[] i2)
    {
        return 1.0 - getSimilarity(i1, i2);
    }


    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ProductPDFSim))
        {
            return false;
        }

        return true;
    }
}



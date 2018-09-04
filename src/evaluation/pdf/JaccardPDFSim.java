package evaluation.pdf;


public class JaccardPDFSim extends PDFSimilarity {



    /**
     * This method return the similarity, the similarity is (1/distance)
     * @param i1
     * @param i2
     * @return double
     */
    public double getSimilarity (Double[] i1, Double[] i2)
    {
        double dotProduct = 0.0;
        for (int i=0; i<i1.length; i++)
        {
            dotProduct += i1[i]*i2[i];
        }
        
        double norm1 = 0.0;
        for (int i=0; i<i1.length; i++)
        {
            norm1 += i1[i]*i1[i];
        }
        
        double norm2 = 0.0;
        for (int i=0; i<i2.length; i++)
        {
            norm2 += i2[i]*i2[i];
        }
        
        double den = norm1+norm2-dotProduct;
        double ret = 1.0;
        if (den != 0.0)
        {
            ret = dotProduct/den;
        }
        
        if (ret < -0.000001 || ret > 1.000001)
        {
            throw new RuntimeException("ERROR: The value must be within [0,1]");
        }
        
        return ret;
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
    public double getDistance (Double[] i1, Double[] i2) 
    {
        return 1-this.getSimilarity(i1, i2);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof JaccardPDFSim))
        {
            return false;
        }
        
        return true;
    }
}


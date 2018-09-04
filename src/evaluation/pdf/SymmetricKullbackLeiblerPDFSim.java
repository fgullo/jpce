package evaluation.pdf;


public class SymmetricKullbackLeiblerPDFSim extends PDFSimilarity {


    /**
     * This method return the similarity, the similarity is (1/distance)
     * @param i1
     * @param i2
     * @return double
     */
    public double getSimilarity (Double[] i1, Double[] i2) {
       
        
        double distance = getDistance(i1, i2);
        
        if (distance > 0)
        {
            return ((double)1.0)/distance;    
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
    public double getDistance (Double[] i1, Double[] i2) 
    {
        if (i1.length != i2.length)
        {
            throw new RuntimeException("Vectors of probabilities must be equal length");
        }
        
        double sum1=0;
        for(int i=0; i<i1.length;i++)
        {
            if (i1[i] > 0.0 && i2[i] > 0.0)
            {
                sum1 += i1[i]*(Math.log(i1[i])-Math.log(i2[i])); 
            }
        }
        
        double sum2=0;
        for(int i=0; i<i2.length;i++)
        {
            if (i1[i] > 0.0 && i2[i] > 0.0)
            {
                sum2 += i2[i]*(Math.log(i2[i])-Math.log(i1[i])); 
            }
        }
        return (sum1+sum2)/2;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof SymmetricKullbackLeiblerPDFSim))
        {
            return false;
        }
        
        return true;
    }
}

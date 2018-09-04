package evaluation.numericalinstance;

import objects.Instance;
import objects.NumericalInstance;

public class SquaredEuclideanNumericalInstanceSim extends NumericalInstanceSimilarity {

    /**
     * This method return the similarity, the similarity is (1/distance)
     * @param i1
     * @param i2
     * @return double
     */
    public double getSimilarity (Instance i1, Instance i2) {
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
    public double getDistance (Instance i1, Instance i2)
    {
        Double data[] = ((NumericalInstance)i1).getDataVector();
        Double data1[] = ((NumericalInstance)i2).getDataVector();

        if (data.length != data1.length)
        {
            throw new RuntimeException("The instances must have the same number of features");
        }

        double sum=0;
        for(int i=0; i<data.length;i++){
            sum = sum + (data[i]-data1[i])*(data[i]-data1[i]);
        }
        return sum;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof SquaredEuclideanNumericalInstanceSim))
        {
            return false;
        }

        return true;
    }
}
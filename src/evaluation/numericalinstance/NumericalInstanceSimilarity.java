package evaluation.numericalinstance;

import evaluation.Similarity;
import objects.Instance;

public abstract class NumericalInstanceSimilarity implements Similarity {

    /**
     * Return the similarity between the two instances i1 and i2
     */
    public abstract double getSimilarity (Instance i1, Instance i2);

    public abstract double getDistance (Instance i1, Instance i2);

}


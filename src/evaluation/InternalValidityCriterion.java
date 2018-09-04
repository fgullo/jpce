package evaluation;

import objects.Instance;

public interface InternalValidityCriterion {

    public double getSimilarity (Instance i, Similarity sim);

    public double getDistance (Instance i, Similarity sim);

}


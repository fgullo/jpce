package evaluation.cluster;

import evaluation.InternalValidityCriterion;
import evaluation.Similarity;
import objects.Instance;

public abstract class ProjectiveClusterInternalValidityCriterion implements InternalValidityCriterion {

    public abstract double getSimilarity (Instance i, Similarity sim);

    public abstract double getDistance (Instance i, Similarity sim);

}

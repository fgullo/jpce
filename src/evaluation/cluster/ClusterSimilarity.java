package evaluation.cluster;

import evaluation.Similarity;
import objects.Instance;

public abstract class ClusterSimilarity implements Similarity {

    public abstract double getSimilarity (Instance i1, Instance i2);

    public abstract double getDistance (Instance i1, Instance i2);
    

}


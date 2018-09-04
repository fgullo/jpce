package evaluation;

import objects.Instance;

public interface Similarity {

    public double getSimilarity (Instance i1, Instance i2);

    public double getDistance (Instance i1, Instance i2);

}


package evaluation.clustering;

import evaluation.Similarity;
import objects.Instance;

public abstract class ProjectiveClusteringSimilarity implements Similarity {

    protected boolean onlyHard;
    
    public ProjectiveClusteringSimilarity(boolean b)
    {
        this.onlyHard = b;
    }
    
    public abstract double getSimilarity (Instance i1, Instance i2);

    public abstract double getDistance (Instance i1, Instance i2);
    
    public boolean onlyHard()
    {
        return this.onlyHard;
    }
    
    //public abstract double pippo();
    
    public void setOnlyHard(boolean b)
    {
        this.onlyHard = b;
    }

}

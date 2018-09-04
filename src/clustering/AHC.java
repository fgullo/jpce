package clustering;

public abstract class AHC extends ClusteringMethod
{
    protected boolean[][] dendrogram;
    protected boolean buildDendrogram;
    
    public boolean[][] getDendrogram()
    {
        return dendrogram;
    }
}

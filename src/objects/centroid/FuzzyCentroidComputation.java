package objects.centroid;

public abstract class FuzzyCentroidComputation implements CentroidComputation{

    protected int m;
    protected double[] assignments;
    
    public int getM()
    {
        return this.m;
    }
    
    public double[] getAssignments()
    {
        return this.assignments;
    }
    
    public void setM(int m)
    {
        this.m = m;
    }
    
    public void setAssignments(double[] assignments)
    {
        this.assignments = assignments;
    }
            

}

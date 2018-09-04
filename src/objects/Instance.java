package objects;


public abstract class Instance
{

    protected int ID;
    protected double mean;
    protected double variance;
    protected double stdDev;
    protected Object[] featureVectorRepresentation;

    public int getID ()
    {
        return ID;
    }

    public void setID (int ID)
    {
        this.ID = ID;
    }

    public abstract double getMean ();
    public abstract double getStdDev ();

    public boolean equals (Object o) 
    {
        if(!(o instanceof Instance))
        {
            return false;
        }
        
        Instance I1=(Instance)o;
        if (I1.getID()!=this.getID())
        {
            return false;
        }
        
        //verificare se l'uguaglianza solo su Id o su tutti gli altri attributi
        return true;  
    }

    public abstract Object[] getFeatureVectorRepresentation ();

    protected int genID()
    {
        long millis = System.currentTimeMillis();
	long second = millis / 1000;
	long day = second / 86400;
	long secondiGiorno = second - (day*86400);
		
	int rnd = (int)(9999 * Math.random());
		
	int value = (((int)secondiGiorno) * 10000) + rnd;
		
	return value;
    }

    public int getNumberOfFeatures ()
    {
       return  featureVectorRepresentation.length;
    }
}

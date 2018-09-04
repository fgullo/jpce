package objects;


public class NumericalInstance extends Instance {

    /**
     * This variable contains the elements that represent the NumericalInstance  
     */
    protected Double[] dataVector;
    
    /**
    * This is a costructor for NumericalInstance object 
    * @param data recive a double array that contains the values of istance.         
    */
    public NumericalInstance (Double[] data) {
        ID=genID();
        dataVector=data;
        
        this.featureVectorRepresentation = new Object[this.dataVector.length];
        
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            this.featureVectorRepresentation[i] = this.dataVector[i];
        }
    }
    
     /**
    * This is a costructor for NumericalInstance object 
    * @param data recive a double array that contains the values of istance.         
    */
    public NumericalInstance (double[] data) {
        ID=genID();
        dataVector=new Double[data.length];
        for (int i=0; i<data.length; i++)
        {
            dataVector[i] = data[i];
        }
        
        this.featureVectorRepresentation = new Object[this.dataVector.length];
        
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            this.featureVectorRepresentation[i] = this.dataVector[i];
        }
    }
    
    /**
    * This is a costructor for NumericalInstance object 
    * @param data recive a double array that contains the values of istance.
    * @param ID recive a an univocal identifier         
    */
    public NumericalInstance (Double[] data, int ID) {
        this.ID=ID;
        dataVector=data;
        
        this.featureVectorRepresentation = new Object[this.dataVector.length];
        
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            this.featureVectorRepresentation[i] = this.dataVector[i];
        }        
    }

    /**
    * This is a costructor for NumericalInstance object 
    * @param data receives a double array that contains the values of istance.
    * @param ID receives an univocal identifier         
    */
    public NumericalInstance (double[] data, int ID) {
        this.ID=ID;
        dataVector=new Double[data.length];
        for (int i=0; i<data.length; i++)
        {
            dataVector[i] = data[i];
        }
        
        this.featureVectorRepresentation = new Object[this.dataVector.length];
        
        for (int i=0; i<this.featureVectorRepresentation.length; i++)
        {
            this.featureVectorRepresentation[i] = this.dataVector[i];
        }        
    }

    
    /**
    * Return double array dataVector
    */
    public Double[] getDataVector () {
        return dataVector;
    }



    public Object[] getFeatureVectorRepresentation () {
        return featureVectorRepresentation;
    }
    
    public double getMean() {
        
        if (this.dataVector.length == 0)
        {
            return 0.0;
        }
        
        double med = 0;
        for (int i=0; i<dataVector.length; i++){
            med = med + dataVector[i];
        }
        med=med/dataVector.length;
        return med;
    }

    
    public double getVariance() {
        
        if (this.dataVector.length == 0)
        {
            return 0.0;
        }
        
        double var=0;
        double mean=getMean();
        for(int i=0; i<dataVector.length; i++){
            double tmp=dataVector[i]-mean;
            tmp=Math.pow(tmp, 2);
            var=var+tmp;
        }
        var=var/dataVector.length;
        return var;
    }
    
    public double getStdDev() {
        double std=Math.sqrt(getVariance());
        return std;
    }

}


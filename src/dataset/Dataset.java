package dataset;

import evaluation.Similarity;
import objects.Clustering;
import objects.Instance;


public abstract class Dataset {

    protected Object[][] dataFeatureMatrix;

    //ipotizzo che l'indice usato per indicizzare data sia l'ID delle istanze contenute in esso
    //implementare tale caratteristica nel DataLoader
    protected Instance[] data;
    
    protected Clustering refPartition;

    protected Similarity sim;

    private double[][] simMatrix;

    /**
     *  <html>
     *    <head>
     *      
     *    </head>
     *    <body>
     *      <p style="margin-top: 0">
     *        This field is probably not mandatory...
     *      </p>
     *    </body>
     *  </html>
     */
    private double[][] distMatrix;

    public Instance[] getData () {
        return data;
    }

    public Object[][] getDataFeatureMatrix () 
    {
        if (dataFeatureMatrix == null)
        {
            dataFeatureMatrix = new Object[data.length][data[0].getNumberOfFeatures()];
            for (int i=0; i<data.length; i++)
            {
                dataFeatureMatrix[i] = data[i].getFeatureVectorRepresentation();
            }
        }
        return dataFeatureMatrix;
    }

    public double[][] getDistMatrix (Similarity sim) 
    {
        if (distMatrix == null)
        {
            if (this.sim==null || !sim.equals(this.sim))
            {
                this.sim = sim;
            }
            distMatrix = new double[data.length][data.length];
            for(int i=0; i<data.length-1; i++)
            {
                for(int j=i+1; j<data.length; j++)
                {
                    distMatrix [i][j]=sim.getDistance(data[i], data[j]);
                }
            }                 
        }
        else if (this.sim==null || !sim.equals(this.sim))
        {
            this.sim = sim;
            for(int i=0; i<data.length-1; i++)
            {
                for(int j=i+1; j<data.length; j++)
                {
                    distMatrix [i][j]=sim.getDistance(data[i], data[j]);
                }
            }   
        }
        
        return distMatrix;
    }

    public double[][] getSimMatrix (Similarity sim) 
    {
        if (simMatrix == null)
        {
            if (this.sim==null || !sim.equals(this.sim))
            {
                this.sim = sim;
            }
            simMatrix = new double[data.length][data.length];
            for(int i=0; i<data.length-1; i++)
            {
                for(int j=i+1; j<data.length; j++)
                {
                    simMatrix [i][j]=sim.getSimilarity(data[i], data[j]);
                }
            }                 
        }
        else if (this.sim==null || !sim.equals(this.sim))
        {
            this.sim = sim;
            for(int i=0; i<data.length-1; i++)
            {
                for(int j=i+1; j<data.length; j++)
                {
                    simMatrix [i][j]=sim.getSimilarity(data[i], data[j]);
                }
            }   
        }
        
        return simMatrix;
    }

    public int getDataLength () 
    {
        return data.length;
    }

    public int getNumberOfFeatures () 
    {
        return data[0].getNumberOfFeatures();     
    }
    
    public Clustering getReferencePartition()
    {
        return this.refPartition;
    }

}


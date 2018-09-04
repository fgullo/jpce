package objects.centroid;

import java.util.ArrayList; 
import objects.Instance;
import objects.NumericalInstance;

public class NumericalInstanceCentroidComputationMajorityVoting implements CentroidComputation {

    public NumericalInstanceCentroidComputationMajorityVoting () {
    }

    /*
    public Instance getCentroid (Instance[] data) {
        int val;
        int cont=0;
        Double num = new Double(1);
        int numFeatures = data[0].getFeatureVectorRepresentation().length;
        Double[] newFeatures = new Double[numFeatures];
         for(int i=0;i<numFeatures;i++){
            val=0;
            for(int j=0;j<data.length;j++){
                Object feature = data[j].getFeatureVectorRepresentation()[i];
                if(feature.equals(num)){
                    val++;
                }  
                cont++;
            }
            double avg=(double)val/cont;
            newFeatures[i]=avg;
        }
        
        //DA RIVEDERE
        return new Cluster(newFeatures);
    }
     */
    
    public Instance getCentroid (Instance[] data) 
    {
        int nFeatures = ((NumericalInstance)data[0]).getNumberOfFeatures();
        double[] dataCentroid = new double[nFeatures];
        
        for (int j=0; j<dataCentroid.length; j++)
        {
            int iMax = -1;
            int max = Integer.MIN_VALUE;
            
            for (int i=0; i<data.length; i++)
            {
                double curr = ((NumericalInstance)data[i]).getDataVector()[j];
                int count = 0;
                
                for (int k=0; k<data.length; k++)
                {
                    if (((NumericalInstance)data[k]).getDataVector()[j]==curr)
                    {
                      count++;  
                    }
                }
                
                if (count > max)
                {
                    max = count;
                    iMax = i;
                }
            }
            
            dataCentroid[j] = ((NumericalInstance)data[iMax]).getDataVector()[j];
        }
        
        return new NumericalInstance(dataCentroid);
    }
}
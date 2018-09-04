package objects.centroid;

import java.util.ArrayList; 
import objects.Instance;
import objects.NumericalInstance;

public class NumericalInstanceCentroidComputationAVG implements CentroidComputation {

    public NumericalInstanceCentroidComputationAVG () {
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
        double[] dataCentroid = new double[((NumericalInstance)data[0]).getNumberOfFeatures()];
        
        for (int i=0; i<data.length; i++)
        {
            Double[] curr = ((NumericalInstance)data[i]).getDataVector();
            for (int j=0; j<curr.length; j++)
            {
                dataCentroid[j]+=curr[j]/data.length;
            }
        }
        
        return new NumericalInstance(dataCentroid);

    }
}


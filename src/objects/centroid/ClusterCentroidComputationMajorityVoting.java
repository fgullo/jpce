package objects.centroid;

import java.util.ArrayList; 
import objects.Cluster;
import objects.Instance;

public class ClusterCentroidComputationMajorityVoting implements CentroidComputation {

    public ClusterCentroidComputationMajorityVoting () {
    }

    public Instance getCentroid (Instance [] data) 
    {
        ArrayList<Instance> centroid = new ArrayList<Instance>();
        int val1;
        int val0;
        int numFeatures = data[0].getFeatureVectorRepresentation().length;
        Double[] newFeatures = new Double[numFeatures];
        for(int i=0;i<numFeatures;i++)
        {
            val1=0;
            val0=0;
            Instance toAdd = null;
            boolean toAddComputed = false;
            for(int j=0;j<data.length;j++)
            {
                double feature = (Double)data[j].getFeatureVectorRepresentation()[i];
                if(feature == 1.0)
                {
                    val1++;
                    if (!toAddComputed)
                    {
                        toAdd = getInstanceFromID(((Cluster)data[j]).getInstances(), i);
                        toAddComputed = true;
                    }
                }
                else if(feature == 0.0)
                {
                    val0++;
                }
                else
                {
                    //inatteso!!
                }
            }
            
            if(val1>val0)
            {
                newFeatures[i] = 1.0;
                if (toAdd != null)
                {
                    centroid.add(toAdd);
                }
            }
            else
            {
                newFeatures[i] = 0.0;
            }
        }
        
        Instance[] finalInstances = new Instance[centroid.size()];
        for (int i=0; i<finalInstances.length; i++)
        {
            finalInstances[i] = centroid.get(i);
        }
        
        return new Cluster(finalInstances,newFeatures);
    }
    
    private Instance getInstanceFromID(Instance[] data, int ID)
    {
        for (int i=0; i<data.length; i++)
        {
            if (data[i].getID() == ID)
            {
                return data[i];
            }
        }
        
        return null;
    }

}


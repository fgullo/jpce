package dataset.loading;

import objects.Clustering;
import objects.Instance;

public class ClusterDataLoader extends DataLoader
{
    public ClusterDataLoader (String datasetPath) 
    {

    }

    public ClusterDataLoader (String datasetPath, String refPartitionPath)
    {

    }

    public Instance[] load ()
    {
        return null;
    }
    
    public Object[] optimizedLoad (){return null;}
    
    public Object[] optimizedLoadGivenRawData (Instance[] instances, Clustering refPartition){return null;}
}


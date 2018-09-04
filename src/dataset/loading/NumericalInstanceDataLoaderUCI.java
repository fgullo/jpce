package dataset.loading;

import objects.Clustering;
import objects.Instance;

public class NumericalInstanceDataLoaderUCI extends DataLoader {

    public NumericalInstanceDataLoaderUCI (String datasetPath) {
    }

    public NumericalInstanceDataLoaderUCI (String datasetPath, String refPartitionPath) {
    }

    /**
     *  <html>
     *    <head>
     *      
     *    </head>
     *    <body>
     *      <p style="margin-top: 0">
     *    The method reads the file whose path is specified by datasetPath 
     *    instance varable and loads the data and the reference partion (if any).<br>
     *      </p>
     *      <p style="margin-top: 0">
     *    The method returns an array of 2 elements which are instances of the 
     *    class Instance: the first one is the array
     *      </p>
     *      <p style="margin-top: 0">
     *    containing the data (represented by objects of the class Instance), 
     *    whereas the second element is the reference partition (represesented by 
     *    an object of the class Clustering). 
     *  <br>    </p>
     *      <p style="margin-top: 0">
     *    If the data and the reference partition are stored in different files, 
     *    the reference partition is loaded from the file whose path is specified 
     *    by refPartitionPath instance variable.
     *      </p>
     *    </body>
     *  </html>
     */
    public Object[] load () {
        return null;
    }
    
    public Object[] optimizedLoad (){return null;}
    
    public Object[] optimizedLoadGivenRawData (Instance[] instances, Clustering refPartition){return null;}

}


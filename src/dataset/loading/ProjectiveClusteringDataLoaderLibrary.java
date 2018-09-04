package dataset.loading;

import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ProjectiveClusteringDataLoaderLibrary extends DataLoader {
    
    protected DataLoader rawDL;
    
    
    public ProjectiveClusteringDataLoaderLibrary (String datasetPath) {
        this.datasetPath=datasetPath;
    }

    public ProjectiveClusteringDataLoaderLibrary (String datasetPath, String refPartitionPath) {
        this.datasetPath=datasetPath;
        this.refPartitionPath=refPartitionPath;
    }
    
    public ProjectiveClusteringDataLoaderLibrary (String datasetPath, String refPartitionPath, DataLoader rawDL) {
        this.datasetPath=datasetPath;
        this.refPartitionPath=refPartitionPath;
        this.rawDL=rawDL;
    }
    
    public ProjectiveClusteringDataLoaderLibrary (String datasetPath, DataLoader rawDL) {
        this.datasetPath=datasetPath;
        this.rawDL=rawDL;
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
        
        Clustering refPartition=null;
        ProjectiveClustering [] totalClustering =null;
        
        try
        {
            FileInputStream fis=new FileInputStream(datasetPath);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            String line;
            
            int idCluster = 0;
            int idClustering = 0;
            int numberOfInstances =0;
            int rigaAttuale = 0;
            //tutte le instance del dataset con id che parte da 0
            Instance[] instances = null;
            int numberOfFeatures = 0;
            
            do{
                line = br.readLine();
                if(line != null)
                {
                    if(line.equalsIgnoreCase("# instances:"))
                    {
                        line = br.readLine();                        
                        numberOfInstances = Integer.parseInt(line);
                        instances = new Instance[numberOfInstances];
                        
                        for(int i=0; i<numberOfInstances; i++)
                        {
                            line = br.readLine();
                            StringTokenizer st = new StringTokenizer(line," ");
                            ArrayList<Double> listDouble = new ArrayList<Double>();
        
                            String val;
                            while(st.hasMoreTokens())
                            {
                                val = st.nextToken().trim();           
                                listDouble.add( Double.parseDouble(val) );
                            }
                            Double[] tmp = listDouble.toArray(new Double[]{});
                            if (i == 0)
                            {
                                numberOfFeatures = tmp.length;
                            }
                            else
                            {
                                if (tmp.length != numberOfFeatures)
                                {
                                    throw new RuntimeException("ERROR: the number of features must be the same for all the instances!");
                                }
                            }
                            Instance inst = new NumericalInstance(tmp,i);
                            instances[i] = inst;                                      
                        }
                    }
                    if(line.equalsIgnoreCase("# refPartition:"))
                    {
                        line = br.readLine();
                        int nCluster = Integer.parseInt(line);
                        Cluster [] clusteringElements = new Cluster[nCluster];
                        
                        for(int j=0; j<nCluster; j++)
                        {
                            line = br.readLine();
                            StringTokenizer st = new StringTokenizer(line," ");
                            ArrayList<Integer> listID = new ArrayList<Integer>();
                            String val;
                            while(st.hasMoreTokens())
                            {
                                val = st.nextToken().trim();
                                listID.add( Integer.parseInt(val));
                            }
                            
                            Instance [] clusterElements = new Instance[listID.size()];
                            
                            for(int k=0; k<listID.size(); k++)
                            {
                                clusterElements[k]=instances[listID.get(k)];
                            }
                            Cluster cluster = new Cluster(clusterElements,j,numberOfInstances);
                            clusteringElements[j]=cluster;
                        }
                        
                        refPartition = new Clustering(clusteringElements);   
                    }
                    
                    if(line.equalsIgnoreCase("# Clustering:"))
                    {
                        line = br.readLine();
                        int numberOfClustering = Integer.parseInt(line);
                        totalClustering = new ProjectiveClustering[numberOfClustering];
                        
                            for(int z=0; z<numberOfClustering; z++)
                            {
                                line = br.readLine();
                                int nCluster = Integer.parseInt(line);
                                ProjectiveCluster [] clusteringElements = new ProjectiveCluster[nCluster];
                                line = br.readLine();
                                boolean fuzzyObjectsAssignment = Boolean.parseBoolean(line);
                                line = br.readLine();
                                boolean fuzzyFeaturesAssignment = Boolean.parseBoolean(line);

                                for(int j=0; j<nCluster; j++)
                                {
                                    Double[] objectsRep = new Double[instances.length];
                                    int pos = 0;
                                    line = br.readLine();
                                    StringTokenizer st = new StringTokenizer(line," ");
                                    String val;
                                    while(st.hasMoreTokens())
                                    {
                                        val = st.nextToken();
                                        objectsRep[pos] = Double.parseDouble(val);
                                        pos++;
                                    }
                                    
                                    if (pos != instances.length)
                                    {
                                        throw new RuntimeException("ERROR: size of objectsRep must be equal to the number of instances in the dataset");
                                    }
                                   
                                    Double[] featuresRep = new Double[numberOfFeatures];
                                    pos = 0;
                                    line = br.readLine();
                                    st = new StringTokenizer(line," ");
                                    while(st.hasMoreTokens())
                                    {
                                        val = st.nextToken();
                                        featuresRep[pos] = Double.parseDouble(val);
                                        pos++;
                                    }

                                    if (pos != numberOfFeatures)
                                    {
                                        throw new RuntimeException("ERROR: size of featuresRep must be equal to the number of instances in the dataset");
                                    }
                                    
                                    ProjectiveCluster c = new ProjectiveCluster(instances,objectsRep,featuresRep,idCluster++,fuzzyObjectsAssignment,fuzzyFeaturesAssignment);
                                    clusteringElements[j]=c;
                                }

                                ProjectiveClustering iClustering = new ProjectiveClustering(clusteringElements,idClustering++);
                                totalClustering[z] = iClustering;
                            }         
                        }
                }        
            }
            while(line != null);

            return new Object[]{totalClustering, refPartition};
       
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);            
        }
        
        //caso improbabile (cattura con il System.exit)
        return null;
    }
    
    public Object[] optimizedLoad ()
    {
        Instance[] instances = null;
        Clustering refPartition=null;        
        ProjectiveClustering [] totalClustering =null;
        
        Object[] obj = this.rawDL.load();
        instances = (Instance[])obj[0];
        refPartition = (Clustering)obj[1];
        
        
        try
        {
            FileInputStream fis=new FileInputStream(datasetPath);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            String line;
            
            int idCluster = 0;
            int idClustering = 0;
            int rigaAttuale = 0;
            //tutte le instance del dataset con id che parte da 0
            
            int numberOfFeatures = instances[0].getNumberOfFeatures();
            
            do{
                line = br.readLine();
                if(line != null)
                {                    
                    if(line.equalsIgnoreCase("# Clustering:"))
                    {
                        line = br.readLine();
                        int numberOfClustering = Integer.parseInt(line);
                        totalClustering = new ProjectiveClustering[numberOfClustering];
                        
                            for(int z=0; z<numberOfClustering; z++)
                            {
                                line = br.readLine();
                                int nCluster = Integer.parseInt(line);
                                ProjectiveCluster [] clusteringElements = new ProjectiveCluster[nCluster];
                                line = br.readLine();
                                boolean fuzzyObjectsAssignment = Boolean.parseBoolean(line);
                                line = br.readLine();
                                boolean fuzzyFeaturesAssignment = Boolean.parseBoolean(line);

                                for(int j=0; j<nCluster; j++)
                                {
                                    Double[] objectsRep = new Double[instances.length];
                                    int pos = 0;
                                    line = br.readLine();
                                    StringTokenizer st = new StringTokenizer(line," ");
                                    String val;
                                    while(st.hasMoreTokens())
                                    {
                                        val = st.nextToken();
                                        objectsRep[pos] = Double.parseDouble(val);
                                        pos++;
                                    }
                                    
                                    if (pos != instances.length)
                                    {
                                        throw new RuntimeException("ERROR: size of objectsRep must be equal to the number of instances in the dataset");
                                    }
                                   
                                    Double[] featuresRep = new Double[numberOfFeatures];
                                    pos = 0;
                                    line = br.readLine();
                                    st = new StringTokenizer(line," ");
                                    while(st.hasMoreTokens())
                                    {
                                        val = st.nextToken();
                                        featuresRep[pos] = Double.parseDouble(val);
                                        pos++;
                                    }

                                    if (pos != numberOfFeatures)
                                    {
                                        throw new RuntimeException("ERROR: size of featuresRep must be equal to the number of instances in the dataset");
                                    }
                                    
                                    ProjectiveCluster c = new ProjectiveCluster(instances,objectsRep,featuresRep,idCluster++,fuzzyObjectsAssignment,fuzzyFeaturesAssignment);
                                    clusteringElements[j]=c;
                                }

                                ProjectiveClustering iClustering = new ProjectiveClustering(clusteringElements,idClustering++);
                                totalClustering[z] = iClustering;
                            }         
                        }
                }        
            }
            while(line != null);

            return new Object[]{totalClustering, refPartition};
       
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);            
        }
        
        //caso improbabile (cattura con il System.exit)
        return null;
    }
    
    public Object[] optimizedLoadGivenRawData (Instance[] instances, Clustering refPartition)
    {
        //Instance[] instances = null;
        //Clustering refPartition=null;        
        ProjectiveClustering [] totalClustering =null;
        
        //instances = (Instance[])obj[0];
        //refPartition = (Clustering)obj[1];
        
        
        try
        {
            FileInputStream fis=new FileInputStream(datasetPath);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            String line;
            
            int idCluster = 0;
            int idClustering = 0;
            int rigaAttuale = 0;
            //tutte le instance del dataset con id che parte da 0
            
            int numberOfFeatures = instances[0].getNumberOfFeatures();
            do{
                line = br.readLine();
                if(line != null)
                {                    
                    if(line.equalsIgnoreCase("# Clustering:"))
                    {
                        line = br.readLine();
                        int numberOfClustering = Integer.parseInt(line);
                        totalClustering = new ProjectiveClustering[numberOfClustering];
                        
                            for(int z=0; z<numberOfClustering; z++)
                            {
                                line = br.readLine();
                                int nCluster = Integer.parseInt(line);
                                ProjectiveCluster [] clusteringElements = new ProjectiveCluster[nCluster];
                                line = br.readLine();
                                boolean fuzzyObjectsAssignment = Boolean.parseBoolean(line);
                                line = br.readLine();
                                boolean fuzzyFeaturesAssignment = Boolean.parseBoolean(line);

                                for(int j=0; j<nCluster; j++)
                                {
                                    Double[] objectsRep = new Double[instances.length];
                                    int pos = 0;
                                    line = br.readLine();
                                    StringTokenizer st = new StringTokenizer(line," ");
                                    String val;
                                    while(st.hasMoreTokens())
                                    {
                                        val = st.nextToken();
                                        objectsRep[pos] = Double.parseDouble(val);
                                        pos++;
                                    }
                                    
                                    if (pos != instances.length)
                                    {
                                        throw new RuntimeException("ERROR: size of objectsRep must be equal to the number of instances in the dataset");
                                    }
                                   
                                    Double[] featuresRep = new Double[numberOfFeatures];
                                    pos = 0;
                                    line = br.readLine();
                                    st = new StringTokenizer(line," ");
                                    while(st.hasMoreTokens())
                                    {
                                        val = st.nextToken();
                                        featuresRep[pos] = Double.parseDouble(val);
                                        pos++;
                                    }

                                    if (pos != numberOfFeatures)
                                    {
                                        throw new RuntimeException("ERROR: size of featuresRep must be equal to the number of instances in the dataset");
                                    }
                                    
                                    ProjectiveCluster c = new ProjectiveCluster(instances,objectsRep,featuresRep,idCluster++,fuzzyObjectsAssignment,fuzzyFeaturesAssignment);
                                    clusteringElements[j]=c;
                                }

                                ProjectiveClustering iClustering = new ProjectiveClustering(clusteringElements,idClustering++);
                                totalClustering[z] = iClustering;
                            }         
                        }
                }        
            }
            while(line != null);

            return new Object[]{totalClustering, refPartition};
       
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);            
        }
        
        //caso improbabile (cattura con il System.exit)
        return null;
    }
 
}


package dataset.loading;

import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;


public class NumericalInstanceDataLoaderUCI_ImageSegmentation extends DataLoader{
    
    /**
     * Representing the line of dataset.
     */
    class DataInstance{
        int ID;
        String classValue;
        Double[] array;
    }

    public NumericalInstanceDataLoaderUCI_ImageSegmentation (String datasetPath) {
        this.datasetPath=datasetPath;
    }

    public NumericalInstanceDataLoaderUCI_ImageSegmentation (String datasetPath, String refPartitionPath) {
        this.datasetPath=datasetPath;
        this.refPartitionPath=refPartitionPath;
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

    
    
     public Object[] load(){
        
        try{
            FileInputStream fis=new FileInputStream(datasetPath);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            
            int instanceID = 0;
            int nTotalInstances=getNumberOfInstances(datasetPath);
            
            //strutture utili al primo elemento
            Vector<Instance> instances = new Vector<Instance>();
            
            //strutture utili al secondo elemento
            HashMap<String,Integer> mapIDcluster = new HashMap<String, Integer>();
            HashMap<Integer,Vector<Instance>> mapInstance = new HashMap<Integer, Vector<Instance>>();
            int clusterID=0;
            
            
            String line=br.readLine();
            do{
                line = br.readLine();
                if(line != null){
                    
                    DataInstance dataInstance = parseDataInstance(line);
                    Instance instance = new NumericalInstance(dataInstance.array,instanceID++);
                    
                    //primo elemento dell'array da ritornare
                    instances.add( instance );
                    
                    //secondo elemento dell'array da ritornare
                    Vector<Instance> vInstance;
                    if(mapIDcluster.containsKey(dataInstance.classValue)){
                        int key = mapIDcluster.get(dataInstance.classValue);
                        vInstance = mapInstance.get(key);
                    }else{
                        int key = clusterID++;
                        vInstance = new Vector<Instance>();
                        
                        mapIDcluster.put(dataInstance.classValue, key);
                        mapInstance.put(key,vInstance);
                    }
                    
                    vInstance.add( instance );
                }
                
            }while(line != null);
            
            
            //generazione clustering
            Vector<Cluster> vCluster = new Vector<Cluster>();
            for(int key : mapInstance.keySet()){
                Vector<Instance> vInstances = mapInstance.get(key);
                
                Instance[] arrayInstance = vInstances.toArray(new Instance[]{});
                Cluster cluster = new Cluster(arrayInstance, key,nTotalInstances);
                vCluster.add(cluster);
            }
            
            Cluster[] clusters = vCluster.toArray(new Cluster[]{});
            Clustering clustering = new Clustering( clusters );
            
            //costruzione oggetto di ritorno
            Instance[] istanzeTotali = instances.toArray(new Instance[]{});
            
            return new Object[]{istanzeTotali, clustering};        
            
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);            
        }
        
        //caso improbabile (cattura con il System.exit)
        return null;
    }

    
    
    /**
     * Receive a input <code>String</code> line and return
     * a <code>DataInstance</code>.
     * <br>
     * The line in input must be in the form:<br>
     * ID,val1,val2,...,valN,class
     * 
     * @param line the line to parse
     * @return DataInstance representing the line received
     */
    private DataInstance parseDataInstance(String line){
        
        StringTokenizer st = new StringTokenizer(line,",");
        
        DataInstance dataInstance = new DataInstance();
        ArrayList<Double> listDouble = new ArrayList<Double>();
        
        boolean isFirst = true;
        String val;
        while(st.hasMoreTokens()){
            val = st.nextToken().trim();
            
            if(isFirst){
                dataInstance.ID=(int) Math.random();
                dataInstance.classValue = val;
                isFirst = false;
            }
            else{
                    listDouble.add( Double.parseDouble(val) );
                }
            
        }
         
        dataInstance.array = listDouble.toArray(new Double[]{});
        
        return dataInstance;
    }
    
    private int getNumberOfInstances(String datasetPath) {
        
        int count=0;
        
        try{
        FileInputStream fis=new FileInputStream(datasetPath);
        InputStreamReader isr=new InputStreamReader(fis);
        BufferedReader br=new BufferedReader(isr);
        
        
        String line=br.readLine();
        do
        {
            line = br.readLine();
            if(line != null)
                count++;

        }
        while(line != null);
        
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);            
        }
        return count;
    }
    
   
   public Object[] optimizedLoad (){return null;}
   
   public Object[] optimizedLoadGivenRawData (Instance[] instances, Clustering refPartition){return null;}

}

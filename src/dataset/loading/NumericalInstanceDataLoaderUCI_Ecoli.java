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
import java.util.StringTokenizer;
import java.util.Vector;


public class NumericalInstanceDataLoaderUCI_Ecoli extends DataLoader {

    /**
     * Representing the line of dataset.
     */
    class DataInstance{
        String ID;
        String classValue;
        Double[] array;
    }

    public NumericalInstanceDataLoaderUCI_Ecoli (String datasetPath) {
        this.datasetPath=datasetPath;
    }

    public NumericalInstanceDataLoaderUCI_Ecoli (String datasetPath, String refPartitionPath) {
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
            Vector<Instance> vectorInstance = new Vector<Instance>();
            Vector<Cluster> vectorCluster = new Vector<Cluster>();
            String lastClusterVal="---";
            int clusterID=0;
            
            
            String line;
            do{
                line = br.readLine();
                if(line != null)
                {
                    
                    DataInstance dataInstance = parseDataInstance(line);
    
                    //salto tutte le istanze relative alle classi omL-imL-imS
                    if(!dataInstance.classValue.equals("omL") && !dataInstance.classValue.equals("imL") && !dataInstance.classValue.equals("imS"))
                    {
                       //primo elemento dell'array da ritornare
                        Instance instance = new NumericalInstance(dataInstance.array,instanceID++);
                        instances.add( instance );

                        //secondo elemento dell'array da ritornare
                        if(!lastClusterVal.equals(dataInstance.classValue))
                        {

                            if(vectorInstance.size()>0)
                            {
                                Instance[] istanze = vectorInstance.toArray(new Instance[]{});
                                Cluster cluster = new Cluster(istanze,clusterID++,nTotalInstances);
                                vectorCluster.add( cluster );
                            }

                            //reset array di supporto
                            vectorInstance.clear();
                            vectorInstance.add(instance);
                            lastClusterVal = dataInstance.classValue;

                        }
                        else
                        {   
                            vectorInstance.add( instance );
                        }
                    }
                }
                
            }
            while(line != null);
            
            //ultimo cluster da valutare (del 2° elemento)
            if(vectorInstance.size()>0)
            {
                Instance[] istanze = vectorInstance.toArray(new Instance[]{});
                Cluster cluster = new Cluster(istanze,clusterID++,nTotalInstances);
                vectorCluster.add( cluster );
            }
            
            //creazione clustering (del 2° elemento)
            Cluster[] clusters = vectorCluster.toArray(new Cluster[]{});
            Clustering refPartition = new Clustering( clusters );
            
            //costruzione oggetto di ritorno
            Instance[] istanzeTotali = instances.toArray(new Instance[]{});
            
            return new Object[]{istanzeTotali, refPartition};        
            
        }
        catch(IOException e)
        {
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
    private DataInstance parseDataInstance(String line)
    {
        
        StringTokenizer st = new StringTokenizer(line," ");//verificare se basta considerare solo lo spazio
        
        DataInstance dataInstance = new DataInstance();
        ArrayList<Double> listDouble = new ArrayList<Double>();
        
        boolean isFirst = true;
        String val;
        while(st.hasMoreTokens()){
            val = st.nextToken().trim();
            
            if(isFirst){
                dataInstance.ID = val;
                isFirst = false;
            }else{
                
                if(st.hasMoreTokens()){
                    //is a value of array
                    listDouble.add( Double.parseDouble(val) );
                }else{
                    //is last value (represent the class)
                    dataInstance.classValue = val;
                }
            }
        }
         
        dataInstance.array = listDouble.toArray(new Double[]{});
        
        return dataInstance;
    }
    
    
    private int getNumberOfInstances(String datasetPath) {
    
        int count=0;
        
        try
        {
            FileInputStream fis=new FileInputStream(datasetPath);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);


            String line;
            do
            {
                line = br.readLine();

                if(line != null)
                {
                    DataInstance dataInstance = parseDataInstance(line);
                    if(!dataInstance.classValue.equals("omL") && !dataInstance.classValue.equals("imL") && !dataInstance.classValue.equals("imS"))
                    {
                        count++;
                    }
                }

            }
            while(line != null);
        
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);            
        }
        return count;
    }
    
    public Object[] optimizedLoad (){return null;}
    
    public Object[] optimizedLoadGivenRawData (Instance[] instances, Clustering refPartition){return null;}
        
   
}

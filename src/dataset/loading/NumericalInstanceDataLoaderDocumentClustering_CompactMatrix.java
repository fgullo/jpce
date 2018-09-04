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

public class NumericalInstanceDataLoaderDocumentClustering_CompactMatrix extends DataLoader {
    
    /**
     * Representing the line of dataset.
     */
    class DataInstance{
        int ID;
        int classValue;
        Double[] array;
    }


    public NumericalInstanceDataLoaderDocumentClustering_CompactMatrix (String datasetPath, String refPartitionPath) 
    {
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
 
    
    
    public Object[] load()
    {
        try
        {
            FileInputStream fis=new FileInputStream(datasetPath);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            
            int instanceID = 0;
            
            String line = br.readLine();
            StringTokenizer st = new StringTokenizer(line,"; ");
            
            int size = Integer.parseInt(st.nextToken());
            int numberOfFeatures = Integer.parseInt(st.nextToken());
            
            Instance[] instances = new NumericalInstance[size];
            line = br.readLine();
            while (line != null)
            {                
                st = new StringTokenizer(line,"; ");
                double[] values = new double[numberOfFeatures];

                while (st.hasMoreTokens())
                {
                    String s1 = st.nextToken();
                    String s2 = st.nextToken();
                    if (s2.charAt(0)=='.')
                    {
                        s2 = "0"+s2;
                    }
                    
                    double d = 0.0;
                    int n = -1;
                    try
                    {
                        n = Integer.parseInt(s1);
                        d = Double.parseDouble(s2);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Stringa che ha generato l'errore: ("+s1+","+s2+")");
                        e.printStackTrace();
                        throw new RuntimeException();
                    }
                    values[n-1] = d;
                }
                
                instances[instanceID] = new NumericalInstance(values,instanceID);
                instanceID++;
                
                line = br.readLine();
            }
            
            FileInputStream fis2 = new FileInputStream(refPartitionPath);
            InputStreamReader isr2 = new InputStreamReader(fis2);
            BufferedReader br2 = new BufferedReader(isr2);
            
            ArrayList<ArrayList<NumericalInstance>> array = new ArrayList<ArrayList<NumericalInstance>>();
            ArrayList<NumericalInstance> currArray = null;
            
            String line2 = br2.readLine();
            
            while(line2 != null)
            {
                currArray = new ArrayList<NumericalInstance>();
                
                StringTokenizer st2 = new StringTokenizer(line2,"; ");
                while (st2.hasMoreTokens())
                {
                    String s = st2.nextToken();

                    int x = 0;
                    try
                    {
                        x = Integer.parseInt(s);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Stringa che ha generato l'errore:"+s);
                        e.printStackTrace();
                        throw new RuntimeException();
                    } 
                    
                    currArray.add(((NumericalInstance)instances[x]));                    
                }
                
                array.add(currArray);
                line2 = br2.readLine();
            }
            
            int sumCheck = 0;
            Cluster[] clusters = new Cluster[array.size()];
            for (int i=0; i<clusters.length; i++)
            {
                ArrayList<NumericalInstance> nii = array.get(i);
                Instance[] cluster = new Instance[nii.size()];
                for (int j=0; j<nii.size(); j++)
                {
                    cluster[j] = nii.get(j);
                }
                
                sumCheck += nii.size();
                
                clusters[i] = new Cluster(cluster, i, instances.length);
            }
            
            if (sumCheck != instances.length)
            {
                throw new RuntimeException("ERROR: sumCheck must be equal to totNumberOfInstances");
            }
            
            for (int i=0; i<instances.length; i++)
            {
                if (instances[i]==null)
                {
                    throw new RuntimeException("ERROR: no one instance may be null");
                }
                
                if (instances[i].getID() != i)
                {
                    throw new RuntimeException("ERROR: the ID of the instance does not match the position in the array");
                }
            }
            
            Clustering refPartition = new Clustering(clusters);
            return new Object[]{instances,refPartition};
 
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
        return null;        
    }
    
    public Object[] optimizedLoadGivenRawData (Instance[] instances, Clustering refPartition){return null;}
}



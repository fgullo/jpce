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

public class NumericalInstanceDataLoaderDocumentClustering extends DataLoader
{

    class DataInstance{
        int ID;
        int classValue;
        Double[] array;
    }


    public NumericalInstanceDataLoaderDocumentClustering (String datasetPath, String refPartitionPath)
    {
        this.datasetPath=datasetPath;
        this.refPartitionPath=refPartitionPath;
    }

 
    
    
    public Object[] load()
    {
        try
        {
            FileInputStream fis=new FileInputStream(datasetPath);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            
            int instanceID = 0;
            
            String line = br.readLine();

            int numberOfFeatures = -1;
            
  
            int size = Integer.parseInt(line);
            Instance[] instances = new NumericalInstance[size];
            line = br.readLine();
            while (line != null)
            {                
                StringTokenizer st = new StringTokenizer(line,"; ");
                
                ArrayList<Double> doubles = new ArrayList<Double>();
                while (st.hasMoreTokens())
                {
                    String s = st.nextToken();
                    if (s.charAt(0)=='.')
                    {
                        s = "0"+s;
                    }
                    
                    double d = 0.0;
                    try
                    {
                        d = Double.parseDouble(s);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Stringa che ha generato l'errore:"+s);
                        e.printStackTrace();
                        throw new RuntimeException();
                    }
                    doubles.add(new Double(d));
                }
                
                if (numberOfFeatures==-1)
                {
                    numberOfFeatures = doubles.size();
                }
                else
                {
                    if (doubles.size() != numberOfFeatures)
                    {
                        throw new RuntimeException("ERROR: all the instancs must have the same number of features");
                    }
                }
                
                double[] values = new double[doubles.size()];
                for (int i=0; i<values.length; i++)
                {
                    values[i] = doubles.get(i).doubleValue();
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
    
    public Object[] optimizedLoad (){return null;}
    
    public Object[] optimizedLoadGivenRawData (Instance[] instances, Clustering refPartition){return null;}
}



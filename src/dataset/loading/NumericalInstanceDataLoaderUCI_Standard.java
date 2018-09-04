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
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

public class NumericalInstanceDataLoaderUCI_Standard extends DataLoader
{

    class DataInstance
    {
        int ID;
        int classValue;
        Double[] array;
    }

    public NumericalInstanceDataLoaderUCI_Standard (String datasetPath)
    {
        this.datasetPath=datasetPath;
    }

    public NumericalInstanceDataLoaderUCI_Standard (String datasetPath, String refPartitionPath)
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
            Double[][] subspaces = null;
            if (line.equals("subspaces"))
            {
                subspaces = loadSubspaces(br);
                line = br.readLine();
            }
            int currentCl = 0;
            ArrayList<ArrayList<NumericalInstance>> array = new ArrayList<ArrayList<NumericalInstance>>();
            ArrayList<NumericalInstance> currArray = new ArrayList<NumericalInstance>();
            int numberOfFeatures = -1;
            while (line != null)
            {
                StringTokenizer st = new StringTokenizer(line,"; ");
                int clTmp = Integer.parseInt(st.nextToken());
                if (clTmp != currentCl)
                {
                    currentCl++;
                    array.add(currArray);
                    currArray = new ArrayList<NumericalInstance>();
                }
                
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
                
                currArray.add(new NumericalInstance(values,instanceID));
                instanceID++;
                
                line = br.readLine();
            }
            
            array.add(currArray);
            int totNumberOfInstances = instanceID;
            Instance[] totInstances = new Instance[totNumberOfInstances];

            Clustering refPartition = computeRefPartition(totInstances,array);
            return new Object[]{totInstances,refPartition,subspaces};

            //ProjectiveClustering refPartitionProj = computeRefPartitionProj(totInstances,array,subspaces);
            //return new Object[]{totInstances,refPartitionProj};
        }
        catch(IOException e)
        {
            e.printStackTrace();         
        }
        
        //caso improbabile (cattura con il System.exit)
        return null;
    }

    protected Double[][] loadSubspaces(BufferedReader br)
    {
        Double[][] subspaces = null;

        try
        {
            ArrayList<Double[]> subsal = new ArrayList<Double[]>();
            String line = br.readLine();
            while (!line.equals("objects"))
            {
                StringTokenizer st = new StringTokenizer(line, "; ");
                double sum = 0.0;
                ArrayList<Double> lineal = new ArrayList<Double>();

                while(st.hasMoreTokens())
                {
                    String s = st.nextToken();
                    if (s.charAt(0)=='.')
                    {
                        s = "0"+s;
                    }
                    Double d = Double.parseDouble(s);
                    lineal.add(d);
                    sum += d;
                }

                Double[] linearray = new Double[lineal.size()];
                for (int i=0; i<linearray.length; i++)
                {
                    linearray[i] = lineal.get(i)/sum;
                }
                subsal.add(linearray);

                line = br.readLine();
            }

            subspaces = new Double[subsal.size()][subsal.get(0).length];
            for (int i=0; i<subspaces.length; i++)
            {
                subspaces[i] = subsal.get(i);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return subspaces;
    }

    protected Clustering computeRefPartition(Instance[] totInstances, ArrayList<ArrayList<NumericalInstance>> array)
    {
        int sumCheck = 0;
        Cluster[] clusters = new Cluster[array.size()];
        for (int i=0; i<clusters.length; i++)
        {
            ArrayList<NumericalInstance> nii = array.get(i);
            Instance[] instances = new Instance[nii.size()];
            for (int j=0; j<nii.size(); j++)
            {
                instances[j] = nii.get(j);
                totInstances[instances[j].getID()] = instances[j];
            }

            sumCheck += instances.length;

            clusters[i] = new Cluster(instances, i, totInstances.length);
        }

        if (sumCheck != totInstances.length)
        {
            throw new RuntimeException("ERROR: sumCheck must be equal to totNumberOfInstances");
        }

        for (int i=0; i<totInstances.length; i++)
        {
            if (totInstances[i]==null)
            {
                throw new RuntimeException("ERROR: no one instance may be null");
            }

            if (totInstances[i].getID() != i)
            {
                throw new RuntimeException("ERROR: the ID of the instance does not match the position in the array");
            }
        }

        return new Clustering(clusters);
    }

    protected ProjectiveClustering computeRefPartitionProj(Instance[] totInstances, ArrayList<ArrayList<NumericalInstance>> array, Double[][] subspaces)
    {
        if (subspaces.length != array.size())
        {
            throw new RuntimeException("ERROR: the number of subspaces must be equal to the number of classes");
        }

        int sumCheck = 0;
        for (int i=0; i<array.size(); i++)
        {
            ArrayList<NumericalInstance> nii = array.get(i);
            for (int j=0; j<nii.size(); j++)
            {
                Instance in = nii.get(j);
                if (in.getNumberOfFeatures() != subspaces[0].length)
                {
                    throw new RuntimeException("ERROR: the number of features in each object must be equal to the dimensionality of the subspaces");
                }
                totInstances[in.getID()] = in;
            }

            sumCheck += nii.size();
        }

        ProjectiveCluster[] clusters = new ProjectiveCluster[array.size()];
        for (int i=0; i<clusters.length; i++)
        {
            Double[] obj = new Double[totInstances.length];
            for (int y=0; y<obj.length; y++)
            {
                obj[y] = 0.0;
            }
            ArrayList<NumericalInstance> nii = array.get(i);
            for (int j=0; j<nii.size(); j++)
            {
                Instance in = nii.get(j);
                obj[in.getID()] = 1.0;
            }

            clusters[i] = new ProjectiveCluster(totInstances, obj, subspaces[i], i, false, false);
        }

        if (sumCheck != totInstances.length)
        {
            throw new RuntimeException("ERROR: sumCheck must be equal to totNumberOfInstances");
        }

        for (int i=0; i<totInstances.length; i++)
        {
            if (totInstances[i]==null)
            {
                throw new RuntimeException("ERROR: no one instance may be null");
            }

            if (totInstances[i].getID() != i)
            {
                throw new RuntimeException("ERROR: the ID of the instance does not match the position in the array");
            }
        }

        return new ProjectiveClustering(clusters);
    }
    
    public Object[] optimizedLoad (){return null;}
    
    public Object[] optimizedLoadGivenRawData (Instance[] instances, Clustering refPartition){return null;}
}


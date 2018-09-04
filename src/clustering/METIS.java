package clustering;

import dataset.Dataset;
import evaluation.Similarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

    
/**
 *  <html>
 *    <head>
 *  
 *    </head>
 *    <body>
 *      <p style="margin-top: 0">
 *        The path of metis.exe is read from a configuration XML file
 *      </p>
 *    </body>
 *  </html>
 */
public class METIS extends ClusteringMethod 
{

    //protected final String metisPrefix = "C:\\Documents and Settings\\gullo\\Documenti\\NetBeansProjects\\ClusterEnsembleProject\\metis\\";
    protected final String metisPrefix = "metis\\";
    
    public METIS (Dataset dataset) 
    {
        this.dataset = dataset;
    }

    /**
     *  <p style="margin-top: 0">
     *    The stop criterion is automatically determined by the algorithm.
     *      </p>
     */
    public Clustering execute (Similarity sim) 
    {
        return execute(sim,2);
    }
    
    public Clustering execute (double[][] simMatrix)
    {
        return execute(simMatrix,2);
    }
    
    public Clustering execute (double[][] simMatrix, int nClusters)
    {
        this.simMatrix = simMatrix;
         
        return runAlgorithm(nClusters);
    }

    /**
     *  <p style="margin-top: 0">
     *    The algorithm ends when the number of clusters specified by nClusters is 
     *    reached.
     *      </p>
     */
    public Clustering execute (Similarity sim, int nClusters) 
    {
        simMatrix = new double[dataset.getDataLength()][dataset.getDataLength()];
        double[][] simDataset = dataset.getSimMatrix(sim);

        for (int i=0; i<simMatrix.length; i++)
        {
            for (int j=0; j<simMatrix[i].length; j++)
            {
                simMatrix[i][j] = simDataset[i][j];
            }
        }
        
        return runAlgorithm(nClusters);        
    }
    
    protected Clustering runAlgorithm(int nClusters)
    {
        clearGraphsDirectory();
        
        //build graph file
            generateGraphFile();
   
        //run pmetis.exe
            try
            {
                Runtime rt=Runtime.getRuntime();
                Process proc=rt.exec(metisPrefix+"pmetis.exe "+metisPrefix+"Graphs\\graphFile.graph "+nClusters);
                //System.out.println(proc.exitValue());
                
                //Process.waitFor() appears to enter a deadlock when the child process writes 
                //a lot of data to its output stream and the parent process never reads the data.
                //The Runtime.exec() method creates a pipe for the standard output. When the child process 
                //writes a large amount of data to this pipe so that the buffer is full, it blocks on the pipe 
                //until the data in the pipe buffer is read by the parent process. If the parent process never 
                //reads the standard output, Process.waitFor() does not return. 
                //To prevent blocking, make sure that the parent process always reads 
                //the standard output from the child process.
                new PrintStreaming(proc.getInputStream()).start();
                proc.waitFor();

            }
            
            catch(Exception e)
            {
                e.printStackTrace();
            
            }
        
    
        //build clusters from file outputted by pmetis
        HashMap clusters = getResultFileGraph(nClusters);
        Instance[] data = dataset.getData();
        Cluster[] finalClusters = new Cluster[nClusters];
        for (int i=0; i<nClusters; i++)
        {
            ArrayList clusterI = (ArrayList)clusters.get(new Integer(i));
            Instance[] clusterIarray = new Instance[clusterI.size()];
            for (int j=0; j<clusterI.size(); j++)
            {
                int x = (Integer)clusterI.get(j);
                clusterIarray[j] = data[x];               
            }
            
            Cluster c= new Cluster(clusterIarray,i,data.length);
            finalClusters[i] = c;            
        }
       
        //build clustering
        return new Clustering(finalClusters);
    }

    private void generateGraphFile() 
    {
        int maxDigits = maxDigits();
        int digitsLong = 4;        
        
        try 
        {
            int nNodes = simMatrix.length;
            int nEdges = (nNodes*(nNodes+1)/2)-nNodes;
            
            for(int i=0; i<simMatrix.length-1; i++)
            {
                for(int j=i+1; j<simMatrix[i].length; j++)
                {
                    double edgeWeight = (j>i)?simMatrix[i][j]:simMatrix[j][i];//controllare se funziona con i double                        
                        
                    if (edgeWeight <= 0.0)
                    {
                            nEdges--;
                    }
                }
            }
                 
         
            File f = new File(metisPrefix+"Graphs\\graphFile.graph");
            f.createNewFile();    
            
       
            //il file contenete il grafo sarÃƒÂ  memorizzato nella cartella dove si trova pmatis
            FileOutputStream file = new FileOutputStream(metisPrefix+"Graphs\\graphFile.graph");
            PrintStream output = new PrintStream(file);
            
            //ora scrivimao i dati del grafo nel file .txt
            output.println(nNodes+" "+nEdges+" 1");//il valore 1 inserito rappresenta un parametro di pmetis, indica che gli archi sono pesati
            for(int i=0; i<simMatrix.length; i++)
            {
                for(int j=0; j<simMatrix[i].length; j++)
                {
                    if (j != i)
                    {
                        double edgeWeight = (j>i)?simMatrix[i][j]:simMatrix[j][i];//controllare se funziona con i double                        
                        
                        if (edgeWeight > 0.0)
                        {
                            int zeros = digitsLong-maxDigits;
                            for (int q=1; q<=zeros; q++)
                            {
                                edgeWeight*=10;
                            }
                            long edgeWeightLong = (long)Math.rint(edgeWeight);

                            output.print(" "+(j+1)+" "+edgeWeightLong);
                            //output.print(" "+(j+1)+" "+edgeWeight);
                        }
                    }
                }
                output.println();
            }
            output.close();         
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private HashMap getResultFileGraph(int nClusters) 
    {
        try
        {
            FileInputStream fis=new FileInputStream(metisPrefix+"Graphs\\graphFile.graph.part."+nClusters);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            
            int rigaAttuale = 0;
                    
            //<cluster,lista di righe>
            HashMap<Integer,ArrayList<Integer>> mapCluster = new HashMap<Integer,ArrayList<Integer>>();
            String line=null;
            do{
                line = br.readLine();
                if(line != null)
                {
                    int clusterVal = Integer.parseInt(line.trim());
                    
                    ArrayList<Integer> elementInCluster;
                    if( mapCluster.containsKey(clusterVal))
                    {
                        elementInCluster = mapCluster.get(clusterVal);
                        elementInCluster.add(rigaAttuale);
                    }
                    else
                    {
                        elementInCluster = new ArrayList<Integer>();
                        elementInCluster.add(rigaAttuale);
                        mapCluster.put(clusterVal, elementInCluster);
                    }
                    
                    rigaAttuale++;
                }
            }
            while(line != null);
           
            isr.close();
            return mapCluster;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;      
    }
    
    protected int maxDigits()
    {
        int c = 0;
        for (int i=0; i<this.simMatrix.length-1; i++)
        {
            for (int j=i+1; j<this.simMatrix[i].length; j++)
            {
                int tmp = numberOfDigits(simMatrix[i][j]);
                if (tmp > c)
                {
                    c = tmp;
                }
            }
        }
        
        return c;
    }
    
    protected int numberOfDigits(double d)
    {
        if (d < 0)
        {
            return 0;
        }
        
        long dI = (long)Math.rint(d);
        int count=0;
        while (dI>0)
        {
            count++;
            dI/=10;
        }
        
        return count;
    }
    
    protected void clearGraphsDirectory()
    {
        File f = new File(this.metisPrefix+"Graphs");
        String[] files = f.list();
        for (int i=0; i<files.length; i++)
        {
            File f1 = new File(this.metisPrefix+"Graphs\\"+files[i]);
            f1.delete();
        }
    }
    
    
    class PrintStreaming extends Thread 
    {
	InputStream is = null;
	public PrintStreaming(InputStream ist) 
	{
            is = ist;
	} 

	public void run() 
	{
            try 
            {
                while(this != null) 
                {
                    int ch = is.read();
                    if(ch != -1) 
                            System.out.print((char)ch); 
                    else break;
                }
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            } 
	}
    }



}
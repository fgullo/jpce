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
import java.util.Iterator;


/**
 *  <html>
 *    <head>
 *      
 *    </head>
 *    <body>
 *      <p style="margin-top: 0">
 *        The path of hmetis.exe is read from a configuration XML file
 *      </p>
 *    </body>
 *  </html>
 */
public class HMETIS extends ClusteringMethod 
{
    
    protected final String hmetisPrefix = "hmetis\\";

    public HMETIS (Dataset data) 
    {
        this.dataset=data;
    }
    
    public Clustering execute (double[][] simMatrix)
    {
        throw new RuntimeException("Such a method can not be invoked!");
    }
    
    public Clustering execute (double[][] simMatrix, int nClusters)
    {
        throw new RuntimeException("Such a method can not be invoked!");
    }

    public Clustering execute (ArrayList<int[]> hyperEdgesList, int nClusters) 
    {
        clearGraphsDirectory();
        
        // UBfactor must be >=1 and <=49 
        int UBfactor = 5;
        
        int nVertices = dataset.getData().length;
        int nHyperedges = hyperEdgesList.size();
        
        
        //COSTRUZIONE FILE CONTENENTE L'IPERGRAFO
        generateHyperGraphFile(hyperEdgesList, nVertices, nHyperedges);
        
        
        //ESECUZIONE HMETIS SUL FILE CREATO
        try
        {
            Runtime rt=Runtime.getRuntime();
            Process proc=rt.exec(hmetisPrefix+"shmetis.exe "+hmetisPrefix+"Graphs\\hyperGraphFile.hgr "+nClusters+" "+UBfactor);
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
        HashMap clusters = getResultFileHyperGraph(nClusters);
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

    
    
    /**
     *  <p style="margin-top: 0">
     *    The stop criterion is automatically determined by the algorithm.
     *      </p>
     */
    public Clustering execute (Similarity sim) 
    {
        throw new RuntimeException("Such a method can not be invoked!");
    }

    /**
     *  <p style="margin-top: 0">
     *    The algorithm ends when the number of clusters specified by nClusters is 
     *    reached.
     *      </p>
     */
    public Clustering execute (Similarity sim, int nClusters) 
    {
       throw new RuntimeException("Such a method can not be invoked!");
    }

    private void generateHyperGraphFile(ArrayList<int[]> hyperEdgesList, int nVertices, int nHyperedges) {
        try {
            //il file contenete il grafo sarÃ  memorizzato nella cartella dove si trova pmatis
            FileOutputStream file = new FileOutputStream(hmetisPrefix+"Graphs\\hyperGraphFile.hgr");
            PrintStream Output = new PrintStream(file);
            
            //scrittura dati del grafo nel file .txt
            Output.println(nHyperedges+" "+nVertices);//la struttura non presenta pesi associati agli iperarchi
            
            Iterator i= hyperEdgesList.iterator();
            while(i.hasNext())
            {
                int [] hyperEdge = (int[])i.next();
                for(int j=0; j<hyperEdge.length; j++)
                {
                     Output.print(" "+(hyperEdge[j]+1));
                }
                Output.println();      
            }
            
            Output.close();
            
        }catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    
    }

    private HashMap getResultFileHyperGraph(int nClusters) {
         try{
            FileInputStream fis=new FileInputStream(hmetisPrefix+"Graphs\\hyperGraphFile.hgr.part."+nClusters);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            
            int rigaAttuale = 0;
                    //<cluster,lista di righe>
            HashMap<Integer,ArrayList<Integer>> mapCluster = new HashMap<Integer,ArrayList<Integer>>();
            String line=null;
            do{
                line = br.readLine();
                if(line != null){
                    int clusterVal = Integer.parseInt(line.trim());
                    
                    ArrayList<Integer> elementInCluster;
                    if( mapCluster.containsKey(clusterVal)){
                        elementInCluster = mapCluster.get(clusterVal);
                    }else{
                        elementInCluster = new ArrayList<Integer>();
                        mapCluster.put(clusterVal, elementInCluster);
                    }
                    
                    elementInCluster.add(rigaAttuale);
                    
                    rigaAttuale++;
                }
            }while(line != null);
           
            isr.close();
            return mapCluster;
        }catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
         return null;
    }
    
    protected void clearGraphsDirectory()
    {
        File f = new File(this.hmetisPrefix+"Graphs");
        String[] files = f.list();
        for (int i=0; i<files.length; i++)
        {
            File f1 = new File(this.hmetisPrefix+"Graphs\\"+files[i]);
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





package dataset.loading;

import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class NumericalInstanceDataLoaderTimeSeries extends DataLoader {

    public NumericalInstanceDataLoaderTimeSeries (String datasetPath) 
    {
        this.datasetPath = datasetPath;
    }

    public NumericalInstanceDataLoaderTimeSeries (String datasetPath, String refPartitionPath) 
    {
        this.datasetPath = datasetPath;
        this.refPartitionPath = refPartitionPath;
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
    public Object[] load () 
    {
        Object[] ret = new Object[2];
        ArrayList<Instance> instances = new ArrayList<Instance>();	//insieme da restituire
        int readingProgress = 0;
        
        try
        {
            FileInputStream inputFile = new FileInputStream(this.datasetPath);
            int bufferSize = (int)( new File(this.datasetPath) ).length();	//dimensione del file di input
            char[] buffer = new char[bufferSize];					//allocazione di memoria per la lettura dei dati
            int index = 0;											//posizione nel buffer
            int nextChar = inputFile.read();						//codice ASCII del primo carattere letto
            readingProgress = 0;									//azzeramento dello stato di lettura
            while(nextChar != -1)
            {									//legge fino alla fine del file di input
                buffer[index] = (char)nextChar;						//inserisco nel buffer il carattere appena letto
                nextChar = inputFile.read();						//successivo carattere
                index++;
                readingProgress++;									//aumenta lo stato della lettura
            }
            readingProgress = 0;								//riazzeramento dello stato di lettura
            inputFile.close();									//chiusura del file

            //CONVERSIONE DEL TESTO IN DATI NUMERICI
            String contentFile = new String(buffer);			//testo contenuto nel file
            StringTokenizer fileRows = new StringTokenizer(contentFile, "\n");	//support to reading file rows
            int numInstances = fileRows.countTokens();							//numero di serie temporali nel file

            for(int i = 0; i < numInstances; i++)
            {				//per ogni riga del file
                String row = fileRows.nextToken();								//preleva la prossima riga (serie temporale)
                StringTokenizer sequence = new StringTokenizer(row);		//insieme di numeri separati da uno spazio
                int sequenceLength = sequence.countTokens();					//lunghezza dell'iesima sequenza
                double[] X = new double[sequenceLength];						//valori numerici dell'iesima sequenza
                for(int j = 0; j < sequenceLength; j++)
                {						//per ogni punto della sequenza
                    double value = new Double(sequence.nextToken()).doubleValue();//CONVERSIONE
                    X[j] = value;
		}
                
                Instance inst = new NumericalInstance(X,i);
                instances.add(inst);
            }
            
            Instance[] v = new Instance[instances.size()];
            for (int i=0; i<instances.size(); i++)
            {
                v[i] = instances.get(i);
            }
            ret[0] = v;
            
            
            //creation of the referencePartition
            
            BufferedReader in = new BufferedReader(new FileReader(this.refPartitionPath));
            in.readLine();
            String line = in.readLine();
            int nClasses = Integer.parseInt(line);
            Cluster[] clusters = new Cluster[nClasses];
            int count = 0;
            for (int i=0; i<nClasses; i++)
            {
                line = in.readLine();
                int n = Integer.parseInt(line);
                Instance[] inst = new Instance[n];
                for (int j=0; j<inst.length; j++)
                {
                    inst[j] = v[count];
                    count++;
                }
                
                Cluster c = new Cluster(inst,i,v.length);
                clusters[i] = c;                
            }
            ret[1] = new Clustering(clusters);     
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }

        return ret;
    }
    
    public Object[] optimizedLoad (){return null;}
    
    public Object[] optimizedLoadGivenRawData (Instance[] instances, Clustering refPartition){return null;}

}



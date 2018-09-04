/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import clustering.LAC;
import clustering.PROCLUS;
import dataset.loading.DataLoader;
import dataset.Dataset;
import dataset.loading.NumericalInstanceDataLoaderUCI_Standard;
import dataset.NumericalInstanceDataset;
import evaluation.numericalinstance.MinkowskiNumericalInstanceSim;
import evaluation.Similarity;
import evaluation.numericalinstance.WeightedMinkowskiNumericalInstanceSim;
import objects.Clustering;
import objects.FuzzyCluster;
import objects.FuzzyClustering;
import objects.Instance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;


public class GenerateEnsemble
{
    
   
    public static void main(String[] args)
    {
        String[] datasetNames = new String[]{"ecoli","glass","iris","wine","yeast","abalone","isolet","segmentation","letter-recognition","20News","controlchart"};

        int numberOfEnsembles = 20;
        int ensembleSize = 200;
        
        String inputPrefix = "raw_datasets"+File.separator;
        String outputPrefix = "ensemblesPROCLUS"+File.separator;
        
        boolean LAC = false;
        
        generate(datasetNames, numberOfEnsembles, ensembleSize, inputPrefix, outputPrefix, null, LAC, -1);  
    }
   
    
    
    public static void generate(String[] datasetNames, int numberOfEnsembles, int ensembleSize, String inputPrefix, String outputPrefix, PrintStream log, boolean LAC, int numberOfClusters)
    {
        if(log != null){log.println("ENSEMBLE GENERATION---STARTED");}
        
        
        for (int dataset=0; dataset<datasetNames.length; dataset++)
        //for (int dataset=0; dataset<datasetNames.length; dataset++)
        {
            if(log != null)
            {
                log.println();
                log.println("DATASET "+(dataset+1)+"of"+datasetNames.length+": "+datasetNames[dataset]);
            }
            
            String datasetPath = inputPrefix+datasetNames[dataset]+".data";
            String datasetPathARFF = inputPrefix+"ARFF"+File.separator+datasetNames[dataset]+".arff";
            DataLoader dl = new NumericalInstanceDataLoaderUCI_Standard(datasetPath);
            Dataset d = new NumericalInstanceDataset(dl);        
            Object[] dataOfDataset = dl.load();
            Clustering refPartition = (Clustering)dataOfDataset[1];
            Instance[] instancesOfDataset = (Instance[])dataOfDataset[0];

            if (numberOfClusters == -1)
            {
                numberOfClusters = refPartition.getNumberOfClusters();
            }
            int numberOfInstances = d.getDataLength();
            int numberOfFeatures = d.getNumberOfFeatures();

            for (int ens=1; ens<=numberOfEnsembles; ens++)
            {
                if(log != null){log.print("ENSEMBLE "+ens+"of"+numberOfEnsembles);}
                String librarySaving = outputPrefix+datasetNames[dataset]+"_ENSEMBLE_"+ens+".data";
                //int nClusteringForEachConfiguration = 15;
                //int indexLibrary = 0;
                int clusterId = 0;
                
                File f = null;
                FileOutputStream file = null;
                PrintStream output = null;                
                try
                {
                    f = new File(librarySaving);
                    f.createNewFile();

                    file = new FileOutputStream(librarySaving);
                    output = new PrintStream(file);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                output.println("# Clustering:");
                output.println(ensembleSize);

                int v = 1;
                int conf = 1;
                while (v <= ensembleSize)
                {
                    if (LAC)
                    {
                        if (v <= ensembleSize) //standard LAC generation (hard object partitioning, soft feature partitioning)
                        {
                            double h = ((double)1.0)/conf; //#################
                            LAC lac = new LAC((NumericalInstanceDataset)d,h);

                            Clustering clust = lac.execute(new MinkowskiNumericalInstanceSim(2), numberOfClusters);
                            double[][] w = lac.getWeights();

                            ProjectiveClustering toPrint = hardSoftLACProjectiveClusteringComputing(clust, w);
                            for (int i=0; i<numberOfClusters; i++)
                            {
                                toPrint.getClusters()[i].setID(clusterId);
                                clusterId++;
                            }
                            printClustering(toPrint,output);
                            v++;
                        }

                        if (v <= ensembleSize)//LAC generation with hard object partitioning and hard feature partitioning
                        {
                            double h = ((double)1.0)/conf; //#################
                            LAC lac = new LAC((NumericalInstanceDataset)d,h);

                            Clustering clust = lac.execute(new MinkowskiNumericalInstanceSim(2), numberOfClusters);
                            double[][] w = lac.getWeights();

                            ProjectiveClustering toPrint = hardHardLACProjectiveClusteringComputing(clust, w);
                            for (int i=0; i<numberOfClusters; i++)
                            {
                                toPrint.getClusters()[i].setID(clusterId);
                                clusterId++;
                            }
                            printClustering(toPrint,output);
                            v++;
                        }

                        if (v <= ensembleSize)//LAC generation with soft object partitioning and soft feature partitioning
                        {
                            double h = ((double)1.0)/conf; //#################
                            LAC lac = new LAC((NumericalInstanceDataset)d,h);

                            Clustering clust = lac.execute(new MinkowskiNumericalInstanceSim(2), numberOfClusters);
                            double[][] w = lac.getWeights();
                            Instance[] c = lac.getCentroids();

                            ProjectiveClustering toPrint = softSoftLACProjectiveClusteringComputing(clust, w, c);
                            for (int i=0; i<numberOfClusters; i++)
                            {
                                toPrint.getClusters()[i].setID(clusterId);
                                clusterId++;
                            }
                            printClustering(toPrint,output);
                            v++;
                        }

                        if (v <= ensembleSize)//LAC generation with soft object partitioning and hard feature partitioning
                        {
                            double h = ((double)1.0)/conf; //#################
                            LAC lac = new LAC((NumericalInstanceDataset)d,h);

                            Clustering clust = lac.execute(new MinkowskiNumericalInstanceSim(2), numberOfClusters);
                            double[][] w = lac.getWeights();
                            Instance[] c = lac.getCentroids();

                            ProjectiveClustering toPrint = softHardLACProjectiveClusteringComputing(clust, w, c);
                            for (int i=0; i<numberOfClusters; i++)
                            {
                                toPrint.getClusters()[i].setID(clusterId);
                                clusterId++;
                            }
                            printClustering(toPrint,output);
                            v++;
                        }
                    }
                    else if(v <= ensembleSize)//proclus
                    {
                        int p = conf;
                        if (conf == 1 || conf > d.getNumberOfFeatures()-1)
                        {
                            p = d.getNumberOfFeatures()/2;
                        }

                        PROCLUS proclus = new PROCLUS((NumericalInstanceDataset)d,datasetPathARFF,p);
                        
                        Similarity sim = null;
                        Clustering clust = proclus.execute(sim, numberOfClusters);
                        double[][] w = proclus.getWeights();

                        ProjectiveClustering toPrint = proclusProjectiveClusteringComputing(clust, w);
                        for (int i=0; i<numberOfClusters; i++)
                        {
                            toPrint.getClusters()[i].setID(clusterId);
                            clusterId++;
                        }
                        printClustering(toPrint,output);
                        v++;
                    }
                    conf++;
                }
                
                if(log != null){log.println("---GENERATED");}
            }
        }
        
        if(log != null)
        {
            log.println();
            log.println("ENSEMBLE GENERATION---DONE");
            log.println();
            log.println();
            log.println();
            log.println();
        }
    }
    
    private static void printClustering(ProjectiveClustering sc, PrintStream output)
    {
        ProjectiveCluster[] clusters = sc.getClusters();
        output.println(clusters.length);

        for (int g=0; g<clusters.length; g++)
        {
            if (g == 0)
            {
                output.println(clusters[0].getFuzzyObjectsAssignment());
                output.println(clusters[0].getFuzzyFeaturesAssignment());
            }

            Double[] objectsRep = clusters[g].getFeatureVectorRepresentationDouble();
            for (int c=0; c<objectsRep.length; c++)
            {
                output.print(objectsRep[c]+" ");
            }
            output.println();

            Double[] featuresRep = (Double[])clusters[g].getFeatureToClusterAssignments();
            for (int c=0; c<featuresRep.length; c++)
            {
                output.print(featuresRep[c]+" ");
            }
            output.println();
        }
        
        output.flush();
    }
    
    public static ProjectiveClustering hardSoftLACProjectiveClusteringComputing(Clustering clust, double[][] w)
    {
        //normalize w
        //normalize(w);
        
        //generate subspace clusters
        ProjectiveCluster[] clusters = new ProjectiveCluster[clust.getNumberOfClusters()];
        for (int i=0; i<clusters.length; i++)
        {
            Double[] objectsRep = clust.getClusters()[i].getFeatureVectorRepresentationDouble();
            Double[] featuresRep = new Double[w[i].length];
            for (int j=0; j<featuresRep.length; j++)
            {
                featuresRep[j] = new Double(w[i][j]);
            }
            
            clusters[i] = new ProjectiveCluster(clust.getInstances(), objectsRep, featuresRep, i, false, true);
        }
        
        return new ProjectiveClustering(clusters);
    }
    
    public static ProjectiveClustering hardHardLACProjectiveClusteringComputing(Clustering clust, double[][] w)
    {        
        //hardenize w
        hardenize(w);      
        
        //generate subspace clusters
        ProjectiveCluster[] clusters = new ProjectiveCluster[clust.getNumberOfClusters()];
        for (int i=0; i<clusters.length; i++)
        {
            Double[] objectsRep = clust.getClusters()[i].getFeatureVectorRepresentationDouble();
            Double[] featuresRep = new Double[w[i].length];
            for (int j=0; j<featuresRep.length; j++)
            {
                featuresRep[j] = new Double(w[i][j]);
            }
            
            clusters[i] = new ProjectiveCluster(clust.getInstances(), objectsRep, featuresRep, i, false, false);
        }
        
        return new ProjectiveClustering(clusters);
    }
    
    public static ProjectiveClustering softSoftLACProjectiveClusteringComputing(Clustering clust, double[][] w, Instance[] c)
    {
        //normalize w
        //normalize(w);
        
        //fuzzify clust
        FuzzyClustering softClust = fuzzify(clust, w, c);
       
        
        //generate subspace clusters
        ProjectiveCluster[] clusters = new ProjectiveCluster[softClust.getNumberOfClusters()];
        for (int i=0; i<clusters.length; i++)
        {
            Double[] objectsRep = softClust.getClusters()[i].getFeatureVectorRepresentationDouble();
            Double[] featuresRep = new Double[w[i].length];
            for (int j=0; j<featuresRep.length; j++)
            {
                featuresRep[j] = new Double(w[i][j]);
            }
            
            clusters[i] = new ProjectiveCluster(softClust.getInstances(), objectsRep, featuresRep, i, true, true);
        }
        
        return new ProjectiveClustering(clusters);
    }
    
    public static ProjectiveClustering softHardLACProjectiveClusteringComputing(Clustering clust, double[][] w, Instance[] c)
    {        
        //fuzzify clust
        FuzzyClustering softClust = fuzzify(clust, w, c);
        
        //hardenize w
        hardenize(w); 
        
        //generate subspace clusters
        ProjectiveCluster[] clusters = new ProjectiveCluster[softClust.getNumberOfClusters()];
        for (int i=0; i<clusters.length; i++)
        {
            Double[] objectsRep = softClust.getClusters()[i].getFeatureVectorRepresentationDouble();
            Double[] featuresRep = new Double[w[i].length];
            for (int j=0; j<featuresRep.length; j++)
            {
                featuresRep[j] = new Double(w[i][j]);
            }
            
            clusters[i] = new ProjectiveCluster(softClust.getInstances(), objectsRep, featuresRep, i, true, false);
        }
        
        return new ProjectiveClustering(clusters);
    }
    
    private static void normalize(double[][] w)
    {
        for (int j=0; j<w[0].length; j++)
        {
            double sum = 0.0;
            for (int i=0; i<w.length; i++)
            {
                sum += w[i][j];
            }
            
            for (int i=0; i<w.length; i++)
            {
                w[i][j] /= sum;
            }
        }        
    }
    
    private static void hardenize(double[][] w)
    {
        double threshold = ((double)1.0)/w[0].length;
        
        for (int i=0; i<w.length; i++)
        {
            int count = 0;
            for (int j=0; j<w[i].length; j++)
            {
                if (w[i][j] >= threshold)
                {
                    w[i][j] = 1;
                    count++;
                }
                else
                {
                    w[i][j] = 0;
                }
            }
            
            for (int j=0; j<w[i].length; j++)
            {
                    w[i][j] /= count;
            }
        }
    }
    
    private static FuzzyClustering fuzzify(Clustering clust, double[][] w, Instance[] c)
    {
        Double[][] u = new Double[clust.getNumberOfClusters()][clust.getNumberOfInstances()];
        Instance[] instances = clust.getInstances();
        
        for (int j=0; j<u[0].length; j++)
        {
            double sum = 0.0;
            double max = Double.NEGATIVE_INFINITY;
            for (int i=0; i<u.length; i++)
            {
                double dist = 0.0;
                if (c[i] != null)
                {
                    Instance currInst = instances[j];
                    double[] weights = w[i];                
                    Similarity sim = new WeightedMinkowskiNumericalInstanceSim(2,weights);

                    dist = sim.getDistance(currInst, c[i]);
                }               

                u[i][j] = dist;
                
                sum += dist;
                if (dist > max)
                {
                    max = dist;
                }
                
            }
            
            double sumCheck = 0.0;
            for (int i=0; i<u.length; i++)
            {
                u[i][j] = (max-u[i][j]+1)/(u.length*max+u.length-sum);
                sumCheck += u[i][j];
            }
            
            if (sumCheck < 0.99999 || sumCheck > 1.00001)
            {
                throw new RuntimeException("ERROR: sumCheckmust be equal to 1");
            }
        }
        
        FuzzyCluster[] clusters = new FuzzyCluster[clust.getNumberOfClusters()];
        for (int i=0; i<u.length; i++)
        {
            clusters[i] = new FuzzyCluster(clust.getInstances(), u[i], (i+1));
        }
        
        return new FuzzyClustering(clusters);
    }

    protected static ProjectiveClustering proclusProjectiveClusteringComputing(Clustering clust, double[][] w) 
    {
        ProjectiveCluster[] clusters = new ProjectiveCluster[clust.getNumberOfClusters()];
        for (int i=0; i<clusters.length; i++)
        {
            Double[] objectsRep = clust.getClusters()[i].getFeatureVectorRepresentationDouble();
            Double[] featuresRep = new Double[w[i].length];
            
            int count = 0;
            for (int j=0; j<featuresRep.length; j++)
            {
                if (w[i][j] == 1.0)
                {
                    count++;
                }
            }
            
            if (count == 0)
            {
                throw new RuntimeException("ERROR: at least 1 feature should have been specified");
            }
            
            for (int j=0; j<featuresRep.length; j++)
            {
                featuresRep[j] = new Double(w[i][j]/count);
            }
            
            clusters[i] = new ProjectiveCluster(clust.getInstances(), objectsRep, featuresRep, i, false, false);
        }
        
        return new ProjectiveClustering(clusters);
    }
}

















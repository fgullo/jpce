package test;

import i9.subspace.proclus.Proclus;
import dataset.loading.DataLoader;
import dataset.Dataset;
import dataset.loading.NumericalInstanceDataLoaderUCI_Standard;
import dataset.NumericalInstanceDataset;
import dataset.loading.ProjectiveClusteringDataLoaderLibrary;
import dataset.ProjectiveClusteringDataset;
import evaluation.clustering.features.CEFeaturesProjectiveClusteringSim;
import evaluation.clustering.objectsfeatures.CEObjectsFeaturesProjectiveClusteringSim;
import evaluation.clustering.objects.CEObjectsProjectiveClusteringSim;
import evaluation.clustering.features.EntropyFeaturesProjectiveClusteringSim;
import evaluation.clustering.objectsfeatures.EntropyObjectsFeaturesProjectiveClusteringSim;
import evaluation.clustering.objects.EntropyObjectsProjectiveClusteringSim;
import evaluation.clustering.features.F1FeaturesProjectiveClusteringSim;
import evaluation.clustering.features.F1FeaturesProjectiveClusteringSimHARD;
import evaluation.cluster.objectsfeatures.F1ObjectsFeaturesProjectiveClusterSim;
import evaluation.cluster.objectsfeatures.F1ObjectsFeaturesProjectiveClusterSim_FAST;
import evaluation.cluster.objectsfeatures.F1ObjectsFeaturesProjectiveClusterSim_SLOW;
import evaluation.clustering.objects.F1ObjectsProjectiveClusteringSim;
import evaluation.clustering.objects.F1ObjectsProjectiveClusteringSimHARD;
import evaluation.clustering.objectsfeatures.F1ObjectsFeaturesProjectiveClusteringSim;
import evaluation.cluster.features.JaccardFeaturesProjectiveClusterSim;
import evaluation.numericalinstance.JaccardNumericalInstanceSim;
import evaluation.cluster.objectsfeatures.JaccardObjectsFeaturesProjectiveClusterSim;
import evaluation.cluster.objectsfeatures.JaccardObjectsFeaturesProjectiveClusterSim_FAST;
import evaluation.cluster.objects.JaccardObjectsProjectiveClusterSim;
import evaluation.pdf.JaccardPDFSim;
import evaluation.pdf.MinkowskiPDFSim;
import evaluation.clustering.features.NMIFeaturesProjectiveClusteringSim;
import evaluation.clustering.objectsfeatures.NMIObjectsFeaturesProjectiveClusteringSim;
import evaluation.clustering.objects.NMIObjectsProjectiveClusteringSim;
import evaluation.numericalinstance.NumericalInstanceSimilarity;
import evaluation.pdf.PDFSimilarity;
import evaluation.cluster.features.ProjectiveClusterFeaturesSimilarity;
import evaluation.cluster.objectsfeatures.ProjectiveClusterObjectsFeaturesSimilarity;
import evaluation.cluster.objects.ProjectiveClusterObjectsSimilarity;
import evaluation.clustering.features.ProjectiveClusteringFeaturesSimilarity;
import evaluation.clustering.objectsfeatures.ProjectiveClusteringObjectsFeaturesSimilarity;
import evaluation.clustering.objects.ProjectiveClusteringObjectsSimilarity;
import evaluation.clustering.ProjectiveClusteringSimilarity;
import evaluation.testevaluation.EnsemblePCETestEvaluation;
import evaluation.testevaluation.InternalPCETestEvaluation;
import evaluation.testevaluation.ReferencePartitionPCETestEvaluation;
import objects.Clustering;
import objects.centroid.ProjectiveCentroidComputation;
import objects.centroid.ProjectiveClusterNumericalInstanceCentroidComputationAVG;
import objects.ProjectiveClustering;
import pce.singleobjective.EMlikePCE;
import pce.singleobjective.EMlikePCE_FuzzyCM;
import pce.singleobjective.EMlikePCE_ObjectByFeatureMatrix_OPTIMIZED;
import pce.FCMbasedPCE;
import pce.twoobjective.MOEAFeatureBasedPCE;
import pce.twoobjective.MOEAInstanceBasedPCE;
import pce.OneObjectiveClusterBasedPCE;
import pce.enhancedtwoobjective.OneObjectiveClusterBasedPCE_CBPCE;
import pce.enhancedtwoobjective.OneObjectiveClusterBasedPCE_EUCLIDEAN;
import pce.enhancedtwoobjective.OneObjectiveClusterBasedPCE_KMEANS;
import pce.enhancedtwoobjective.OneObjectiveClusterBasedPCE_KMEANS_NORMALIZATION;
import pce.OneObjectiveFeatureBasedPCE;
import pce.enhancedsingleobjective.OneObjectiveInstanceBasedPCE_EUCLIDEAN;
import pce.PCEMethod;
import evaluation.testevaluation.PCETestEvaluation;
import evaluation.testevaluation.TimesPCETestEvaluation;
import java.io.BufferedReader;
import objects.Instance;
import objects.ProjectiveCluster;
import pce.singleobjective.EMlikeMethod;
import pce.singleobjective.EMlikePCE_Bhattacharyya;
import pce.singleobjective.EMlikePCE_EMFBPCE;
import pce.enhancedsingleobjective.EMlikePCE_EEMPCE;
import pce.enhancedsingleobjective.EMlikePCE_EEMPCE_CENTROID;
import pce.enhancedsingleobjective.EMlikePCE_EEMPCE_LATEST;
import pce.singleobjective.EMlikePCE_EMPCE_LATEST;
import pce.singleobjective.EMlikePCE_HYBRID;
import pce.singleobjective.EMlikePCE_HYBRIDII;
import pce.twoobjective.MOEAClusteringBasedPCE;
import pce.twoobjective.MOEAClusteringBasedPCE_OPTIMIZED;
import pce.enhancedsingleobjective.OneObjectiveInstanceBasedPCE_E2SPCE;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import pce.AlphaBetaPCEMethod;
import pce.baseline.*;
import pce.enhancedtwoobjective.*;



public class JPCETest
{
    //output files to be written
    private static PrintStream log = null;
    //private static String logFileName = "SIGMOD11_Repeatability_gullo550---";
    private static String logFileName = "JPCE---";
    private static PrintStream cons = null;
    private static String consFileName = "ConsensusClusterings---";

    //paths
    private static String datasetPrefix = "raw_datasets"+File.separator;
    private static String datasetPrefixARFF = datasetPrefix+"ARFF"+File.separator;
    private static String ensemblePrefix = "ensembles"+File.separator;
    private static String outputPath = "results"+File.separator;

    //parameters in 'parameters.properties' file
    private static int nEnsembles = 1; //number_of_ensembles_in_PCE
    private static int numberOfRuns = 20; //number_of_runs
    private static int populationSize = 30; //MOEA-PCE_population_size
    private static int maxIterations = 200; //MOEA-PCE_max_iterations
    private static boolean evaluationWrtReferenceClassification = true; //evaluation_wrt_reference_classification
    private static boolean evaluationWrtEnsembleSolutions = true; //evaluation_wrt_ensemble_solutions
    private static boolean do_ensembleGeneration = false; //do_ensemble_generation
    private static int nEnsemblesEnsembleGeneration = -1; //number_of_ensembles_in_ensemble_generation (REQUIRED)
    private static boolean projectiveClusteringAlgorithmInEnsembleGeneration = true; //algorithm_in_ensemble_generation (if true => LAC, if false => PROCLUS)
    private static int clustersInEnsembleGeneration = -1; //number_of_clusters_in_ensemble_generation (if -1, the same number as the ideal classes)
    private static int ensembleSize = -1; //ensemble_size_in_ensemble_generation (REQUIRED)
    private static String[] datasetNamesEnsembleGeneration = null; //datasets_in_ensemble_generation (REQUIRED)
    private static boolean do_PCE = true; //do_PCE
    private static boolean saveConsensusClusterings = false; //save_consensus_clusterings
    private static int alphaEMPCE = 2; //EM-PCE_alpha_parameter
    private static int alphaCBPCE = 2; //CB-PCE_alpha_parameter
    private static int betaCBPCE = 2; //CB-PCE_beta_parameter
    private static int alphaFCBPCE = 2; //FCB-PCE_alpha_parameter
    private static int betaFCBPCE = 2; //FCB-PCE_beta_parameter
    private static String[] datasetNames = null; //datasets_in_PCE (REQUIRED)
    private static String[] validDatasetNames = null;
    private static int[] nClustersArray = null; //number_of_clusters_in_consensus_clusterings
    private static String[] validMethodNames = null; //PCE_algorithms (REQUIRED)
    private static int[] validMethods = null;

    //other parameters
    private static boolean onlyHardEvaluation = true;//only_hard_evaluation
    private static boolean fm = false;//fm --- if true, evaluation according to F-Measure
    private static boolean entropy = true;//entropy --- if true evaluation according to Entropy
    private static boolean printCompleteCSV = false;//print_complete_CSV
    private static boolean printSummaryCSV = true;//print_summary_CSV
    private static boolean printSummaryCSV_extended = true;//print_summary_CSV_extended
    private static boolean printSingleRunResults = true;//print_single_run_results    
    private static boolean onlyAVGtimes = true;
    //private static String[] ensembleTypes = new String[]{"fuzzy", "hard", "fuzzyhard"};
    private static String[] ensembleTypes = new String[]{"fuzzyhard"};
    private static int maxM = 6;
    private static boolean tryOtherMs = false;
    private static boolean macroFM = true;
    
    private static String single_ensemble = null;
    private static String single_dataset = null;


    public static void main(String[] args)
    {
        initializeOutputPath();

        initializeLog(outputPath);
        
        loadProperties(args);

        System.out.println();
        System.out.println("Running...");
        System.out.println();


        if (do_ensembleGeneration)
        {
            GenerateEnsemble.generate(datasetNamesEnsembleGeneration, nEnsemblesEnsembleGeneration, ensembleSize, datasetPrefix, ensemblePrefix, log, projectiveClusteringAlgorithmInEnsembleGeneration, clustersInEnsembleGeneration);
        }

        if (do_PCE)
        {
            doPCE();
        }
        renameFinalSummary();

        log.println("TEST COMPLETED SUCCESSFULLY!");
        System.out.println();
        System.out.println("TEST COMPLETED SUCCESSFULLY!");
        System.out.println();
    }
    
    public static void doPCE(Properties props)
    {

        initializeLog(".");
        loadProperties(props);
        

        System.out.println();
        System.out.println("Running...");
        System.out.println();
        doPCE();
        renameFinalSummary();

        log.println("TEST COMPLETED SUCCESSFULLY!");
        System.out.println();
        System.out.println("TEST COMPLETED SUCCESSFULLY!");
        System.out.println();
        
        
        int alpha = 2;
        int beta = 2;
        String ensemble = "";
        String dataset = "";
        
        

    }
    
    private static void initializeLog(String path)
    {
        File file = null;
        FileOutputStream fos = null;
        try
        {
            file = File.createTempFile(logFileName, ".log", new File(path));
            fos = new FileOutputStream(file, true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        log = new PrintStream(fos);
        log.println("JPCE running log");
        log.println();
        log.println();
    }
    
    
    private static void loadProperties(Properties props)
    {
        String t = props.getProperty("single_ensemble");
        if (t != null)
        {
            single_ensemble = t;
        }
        
        t = props.getProperty("algorithm_in_ensemble_generation");
        if (t != null)
        {
            if (t.equals("LAC"))
            {
                projectiveClusteringAlgorithmInEnsembleGeneration = true;
            }
            else if (t.equals("PROCLUS"))
            {
                projectiveClusteringAlgorithmInEnsembleGeneration = false;
            }
            else
            {
                throw new RuntimeException("ERROR: parameter 'algorithm_in_ensemble_generation', algorithm not recognized---algorithm="+t);
            }
                
            single_ensemble = t;
        }
        
        t = props.getProperty("number_of_clusters_in_ensemble_generation");
        if (t != null)
        {
            clustersInEnsembleGeneration = Integer.parseInt(t);
        }
            
        t = props.getProperty("single_dataset");
        if (t != null)
        {
            single_dataset = t;
        }
        
        if (single_dataset != null)
        {
            outputPath = ".";
        }
        
        
        log.println("PARAMETERS:");
        log.println();

        Enumeration properties = props.keys();
        while (properties.hasMoreElements())
        {
            String key = (String)properties.nextElement();
            String value = props.getProperty(key);
            log.println(key+ " = "+value);
        }
        log.println();
        log.println();
        log.println();



        t = props.getProperty("do_ensemble_generation");
        if (t != null)
        {
            do_ensembleGeneration = Boolean.parseBoolean(t);
        }

        if (do_ensembleGeneration)
        {
            t = props.getProperty("datasets_in_ensemble_generation");
            if (t == null)
            {
                throw new RuntimeException("ERROR: parameter 'datasets_in_ensemble_generation' is required in the parameters.properties file");
            }
            StringTokenizer datasetsProp = new StringTokenizer(t, ", ");
            ArrayList<String> datasetNamesTmp = new ArrayList<String>();
            while (datasetsProp.hasMoreTokens())
            {
                String s = datasetsProp.nextToken();
                datasetNamesTmp.add(s);
            }

            datasetNamesEnsembleGeneration = new String[datasetNamesTmp.size()];
            for (int x=0; x<datasetNamesEnsembleGeneration.length; x++)
            {
                datasetNamesEnsembleGeneration[x] = datasetNamesTmp.get(x);
            }
            datasetNamesTmp = null;

            t = props.getProperty("number_of_ensembles_in_ensemble_generation");
            if (t == null)
            {
                throw new RuntimeException("ERROR: parameter 'number_of_ensembles_in_ensemble_generation' is required in the parameters.properties file");
            }
            nEnsemblesEnsembleGeneration = Integer.parseInt(t);

            t = props.getProperty("ensemble_size_in_ensemble_generation");
            if (t == null)
            {
                throw new RuntimeException("ERROR: parameter 'ensemble_size_in_ensemble_generation' is required in the parameters.properties file");
            }
            ensembleSize = Integer.parseInt(t);
        }




        t = props.getProperty("do_PCE");
        if (t != null)
        {
            do_PCE = Boolean.parseBoolean(t);
        }

        if (do_PCE)
        {
            t = props.getProperty("datasets_in_PCE");
            if (t == null)
            {
                throw new RuntimeException("ERROR: parameter 'datasets_in_PCE' is required in the parameters.properties file");
            }
            StringTokenizer datasetsProp = new StringTokenizer(t, ", ");
            ArrayList<String> datasetNamesTmp = new ArrayList<String>();
            while (datasetsProp.hasMoreTokens())
            {
                String s = datasetsProp.nextToken();
                datasetNamesTmp.add(s);
            }

            if (datasetNamesTmp.isEmpty())
            {
                throw new RuntimeException("ERROR: you must specify at least one dataset in the parameters.properties file");
            }

            datasetNames = new String[datasetNamesTmp.size()];
            for (int i=0; i<datasetNames.length; i++)
            {
                datasetNames[i] = datasetNamesTmp.get(i);
            }
            datasetNamesTmp = null;

            validDatasetNames = new String[datasetNames.length*ensembleTypes.length];
            int k = 0;
            for (int i=0; i<datasetNames.length; i++)
            {
                for (int j=0; j<ensembleTypes.length; j++)
                {
                   //validDatasetNames[k] = datasetNames[validDataset[i]]+" ("+ensembleTypes[j]+")";
                    validDatasetNames[k] = datasetNames[i];
                    k++;
                }
            }


            t = props.getProperty("number_of_ensembles_in_PCE");
            if (t != null)
            {
                nEnsembles = Integer.parseInt(t);
            }


            t = props.getProperty("PCE_algorithms");
            if (t == null)
            {
                throw new RuntimeException("ERROR: parameter 'PCE_algorithms' is required in the parameters.properties file");
            }
            StringTokenizer methodsProp = new StringTokenizer(t, ", ");
            ArrayList<String> methodsTmp = new ArrayList<String>();
            while (methodsProp.hasMoreTokens())
            {
                String s = methodsProp.nextToken();
                methodsTmp.add(s);
            }

            if (methodsTmp.isEmpty())
            {
                throw new RuntimeException("ERROR: you must specify at least one valid PCE algorithm");
            }

            //int[] validMethods = new int[]{0,1,10,7,4,14,16};
            //int[] validMethods = new int[]{4};
            validMethodNames = new String[methodsTmp.size()];
            validMethods = new int[validMethodNames.length];


            for (int i=0; i<validMethods.length; i++)
            {
                String s = methodsTmp.get(i);
                validMethodNames[i] = s;
            }
            methodsTmp = null;


            t = props.getProperty("number_of_runs");
            if (t != null)
            {
                numberOfRuns = Integer.parseInt(t);
            }


            String value = props.getProperty("number_of_clusters_in_consensus_clusterings");
            if (value != null)
            {
                StringTokenizer nClustersTmp = new StringTokenizer(value, ", ");
                ArrayList<String> array = new ArrayList<String>();
                while (nClustersTmp.hasMoreTokens())
                {
                    String s = nClustersTmp.nextToken();
                    array.add(s);
                }

                if (array.size() != datasetNames.length)
                {
                    throw new RuntimeException("ERROR: you must specify exactly as much numbers of clusters in the consensus clusterings as the number of datasets");
                }

                nClustersArray = new int[array.size()];
                for (int i=0; i<nClustersArray.length; i++)
                {
                    nClustersArray[i] = Integer.parseInt(array.get(i));
                }
                array = null;
            }


            t = props.getProperty("evaluation_wrt_reference_classification");
            if (t != null)
            {
                evaluationWrtReferenceClassification = Boolean.parseBoolean(t);
            }


            t = props.getProperty("evaluation_wrt_ensemble_solutions");
            if (t != null)
            {
                evaluationWrtEnsembleSolutions = Boolean.parseBoolean(t);
            }


            t = props.getProperty("save_consensus_clusterings");
            if (t != null)
            {
                saveConsensusClusterings = Boolean.parseBoolean(t);
            }

            if (saveConsensusClusterings)
            {
                File file = null;
                FileOutputStream fos = null;
                try
                {
                    file = File.createTempFile(consFileName, ".data", new File(outputPath));
                    fos = new FileOutputStream(file, true);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                cons = new PrintStream(fos);
                cons.println("CONSENSUS CLUSTERINGS OUTPUTTED BY EACH PCE METHOD FOR EACH DATASET, ENSEMBLE, AND RUN");
                cons.println();
                cons.println();
            }


            t = props.getProperty("MOEA-PCE_population_size");
            if (t != null)
            {
                populationSize = Integer.parseInt(t);
            }

            t = props.getProperty("MOEA-PCE_max_iterations");
            if (t != null)
            {
                maxIterations = Integer.parseInt(t);
            }

            t = props.getProperty("EM-PCE_alpha_parameter");
            if (t != null)
            {
                alphaEMPCE = Integer.parseInt(t);
            }

            t = props.getProperty("CB-PCE_alpha_parameter");
            if (t != null)
            {
                alphaCBPCE = Integer.parseInt(t);
            }

            t = props.getProperty("CB-PCE_beta_parameter");
            if (t != null)
            {
                betaCBPCE = Integer.parseInt(t);
            }

            t = props.getProperty("FCB-PCE_alpha_parameter");
            if (t != null)
            {
                alphaFCBPCE = Integer.parseInt(t);
            }

            t = props.getProperty("FCB-PCE_beta_parameter");
            if (t != null)
            {
                betaFCBPCE = Integer.parseInt(t);
            }




            t = props.getProperty("only_hard_evaluation");
            if (t != null)
            {
                onlyHardEvaluation = Boolean.parseBoolean(t);
            }

            t = props.getProperty("fm");
            if (t != null)
            {
                fm = Boolean.parseBoolean(t);
            }

            t = props.getProperty("entropy");
            if (t != null)
            {
                entropy = Boolean.parseBoolean(t);
            }

            t = props.getProperty("print_complete_CSV");
            if (t != null)
            {
                printCompleteCSV = Boolean.parseBoolean(t);
            }

            t = props.getProperty("print_summary_CSV");
            if (t != null)
            {
                printSummaryCSV = Boolean.parseBoolean(t);
            }

            t = props.getProperty("print_summary_CSV_extended");
            if (t != null)
            {
                printSummaryCSV_extended = Boolean.parseBoolean(t);
            }
            
            


        }
    }

    private static void printConsensusClusterings(ProjectiveClustering[] results, int multiResultBehavior)
    {
        if (multiResultBehavior == PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG || results.length == 1)
        {
            for (int i=0; i<results.length; i++)
            {
                ProjectiveCluster[] clusters = results[i].getClusters();
                for (int j=0; j<clusters.length; j++)
                {
                    Double[] obj = clusters[j].getFeatureVectorRepresentationDouble();
                    Double[] feat = clusters[j].getFeatureToClusterAssignments();

                    for (int x=0; x<obj.length; x++)
                    {
                        cons.print(obj[x]+" ");
                    }
                    cons.println();

                    for (int x=0; x<feat.length; x++)
                    {
                        cons.print(feat[x]+" ");
                    }
                    cons.println();
                }
                cons.println();
            }
        }
        else
        {
            ProjectiveCluster[] clusters = results[0].getClusters();
            for (int j=0; j<clusters.length; j++)
            {
                Double[] obj = clusters[j].getFeatureVectorRepresentationDouble();
                Double[] feat = clusters[j].getFeatureToClusterAssignments();

                for (int x=0; x<obj.length; x++)
                {
                    cons.print(obj[x]+" ");
                }
                cons.println();

                for (int x=0; x<feat.length; x++)
                {
                    cons.print(feat[x]+" ");
                }
                cons.println();
            }
            cons.println();
        }
    }

    private static void loadProperties(String[] args)
    {
        String fileName = (args != null && args.length >= 1)?args[0]:"parameters.properties";
        Properties props = new Properties();
        try
        {
            props.load(new FileInputStream(fileName));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        loadProperties(props);
    }

    protected static void printFinalSummary()
    {
        int theta = 0;
        int upsilon = 1;
        int dim1 = 2;

        int o = 0;
        int f = 1;
        int of = 2;
        int dim2 = 3;

        int fm = 0;
        int entropy = 1;
        int dim3 = 2;

        //String[][][] names = new String[dim1][dim2][dim3];
        String[][][] names = new String[][][]{{{"Theta_o (FM) (summary)","Theta_o (ENTROPY) (summary)"},{"Theta_f (FM) (summary)","Theta_f (ENTROPY) (summary)"},{"Theta_{of} (FM) (summary)","Theta_{of} (ENTROPY) (summary)"}},{{"Upsilon_o (FM) (summary)","Upsilon_o (ENTROPY) (summary)"},{"Upsilon_f (FM) (summary)","Upsilon_f (ENTROPY) (summary)"},{"Upsilon_{of} (FM) (summary)","Upsilon_{of} (ENTROPY) (summary)"}}};
        String timeName = "Execution times";
        String[][][] paths = new String[dim1][dim2][dim3];
        String timePath = "";
        long[][][] lastUpdates = new long[dim1][dim2][dim3];
        for (int i=0; i<lastUpdates.length; i++)
        {
            for (int j=0; j<lastUpdates[i].length; j++)
            {
                for (int k=0; k<lastUpdates[i][j].length; k++)
                {
                    lastUpdates[i][j][k] = Long.MIN_VALUE;
                }
            }
        }
        long timeLastUpdate = Long.MIN_VALUE;


        try
        {
            //retrieve filePaths;
            File folder = new File(outputPath);
            String[] files = folder.list();

            for (int i=0; i<files.length; i++)
            {
                boolean stop = false;
                int ix=-1, iy=-1, iz=-1;
                for (int x=0; x<names.length && !stop; x++)
                {
                    for (int y=0; y<names[0].length && !stop; y++)
                    {
                        for (int z=0; z<names[0][0].length && !stop; z++)
                        {
                            if (files[i].startsWith(timeName))
                            {
                                long l = new File(outputPath+files[i]).lastModified();
                                if (l > timeLastUpdate)
                                {
                                    timeLastUpdate = l;
                                    timePath = outputPath+files[i];
                                }

                                x = names.length;
                                y = names[0].length;
                                z = names[0][0].length;
                            }
                            else if (files[i].contains(names[x][y][z]))
                            {
                                stop = true;
                                ix = x;
                                iy = y;
                                iz = z;
                            }
                        }
                    }
                }

                if (stop)
                {
                    File file = new File(outputPath+files[i]);
                    long lastUpdate = file.lastModified();
                    //long lastUpdate = new File(outputPath+files[i]).lastModified();
                    if (new File(outputPath+files[i]).lastModified() > lastUpdates[ix][iy][iz])
                    {
                        lastUpdates[ix][iy][iz] = lastUpdate;
                        paths[ix][iy][iz] = outputPath+files[i];
                    }
                }
            }

            File fileHard = new File(outputPath+"FinalSummaryHARD.csv");
            if (fileHard.exists())
            {
                fileHard.delete();
            }
            fileHard.createNewFile();
            PrintStream out = new PrintStream(new FileOutputStream(fileHard));

            //HARD
            //FM
            //theta fm
            printHeadingTable(out, "THETA", "HARD", "FM");
            printTable(out, paths[theta][of][fm], paths[theta][o][fm], paths[theta][f][fm], "HARD");
            //upsilon fm
            printHeadingTable(out, "UPSILON", "", "");
            printTable(out, paths[upsilon][of][fm], paths[upsilon][o][fm], paths[upsilon][f][fm], "HARD");
            //theta entropy
            printHeadingTable(out, "THETA", "HARD", "ENTROPY");
            printTable(out, paths[theta][of][entropy], paths[theta][o][entropy], paths[theta][f][entropy], "HARD");
            //upsilon entropy
            printHeadingTable(out, "UPSILON", "", "");
            printTable(out, paths[upsilon][of][entropy], paths[upsilon][o][entropy], paths[upsilon][f][entropy], "HARD");
            //times
            printTimes(out, timePath);
            out.close();

            //FUZZY
            File fileFuzzy = new File(outputPath+"FinalSummaryFUZZY.csv");
            if (fileFuzzy.exists())
            {
                fileFuzzy.delete();
            }
            fileFuzzy.createNewFile();
            out = new PrintStream(new FileOutputStream(fileFuzzy));
            //theta fm
            printHeadingTable(out, "THETA", "FUZZY", "FM");
            printTable(out, paths[theta][of][fm], paths[theta][o][fm], paths[theta][f][fm], "FUZZY");
            //upsilon fm
            printHeadingTable(out, "UPSILON", "", "");
            printTable(out, paths[upsilon][of][fm], paths[upsilon][o][fm], paths[upsilon][f][fm], "FUZZY");
            //theta entropy
            printHeadingTable(out, "THETA", "FUZZY", "ENTROPY");
            printTable(out, paths[theta][of][entropy], paths[theta][o][entropy], paths[theta][f][entropy], "FUZZY");
            //upsilon entropy
            printHeadingTable(out, "UPSILON", "", "");
            printTable(out, paths[upsilon][of][entropy], paths[upsilon][o][entropy], paths[upsilon][f][entropy], "FUZZY");
            //times
            printTimes(out, timePath);

            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    protected static void printHeadingTable(PrintStream out, String measure, String hard, String entropy)
    {
        String[] methods = validMethodNames;

        String filler = "";
        for (int i=1; i<=1+methods.length; i++)
        {
            filler += ";";
        }

        //first row
        out.println(hard+";");
        out.println();

        //second row
        out.println(entropy+";");
        out.println();

        //third row
        out.print("DATASET;");
        out.print(measure+"_of;");
        out.print(filler);
        out.print(";");
        out.print(measure+"_o;");
        out.print(filler);
        out.print(";");
        out.print(measure+"_f;");
        out.print(filler);
        out.print(";");
        out.println();

        //fourth row
        out.print(";");
        for (int i=1; i<=3; i++)
        {
            out.print("AVG ensemble;");
            out.print("MAX ensemble;");
            for (int j=0; j<methods.length; j++)
            {
                out.print(methods[j]);
                out.print(";");
            }
            out.print(";");
        }
        out.println();
    }

   protected static void printTable(PrintStream out, String pathof, String patho, String pathf, String hard)
   {
       int nMethods = validMethodNames.length;
       try
        {
            BufferedReader brof=new BufferedReader(new InputStreamReader(new FileInputStream(pathof)));
            BufferedReader bro=new BufferedReader(new InputStreamReader(new FileInputStream(patho)));
            BufferedReader brf=new BufferedReader(new InputStreamReader(new FileInputStream(pathf)));

            //skip the first three lines
            String lineof="", lineo="", linef="";
            for (int i=1; i<=4; i++)
            {
                lineof = brof.readLine();
                lineo = bro.readLine();
                linef = brf.readLine();
            }

            int size = 1+((2+nMethods+1)*3);
            String[] toWrite = new String[size];
            while (lineof != null)
            {
                int pos = 0;
                StringTokenizer stof = new StringTokenizer(lineof,"; ");
                StringTokenizer sto = new StringTokenizer(lineo,"; ");
                StringTokenizer stf = new StringTokenizer(linef,"; ");

                toWrite[pos++] = stof.nextToken();
                sto.nextToken();
                stf.nextToken();

                if (hard.equals("HARD"))
                {
                    for (int i=1; i<=2+nMethods; i++)
                    {
                        String sof = stof.nextToken();
                        String so = sto.nextToken();
                        String sf = stf.nextToken();
                        String h = "";
                    }
                }

                for (int i=1; i<=2+nMethods; i++)
                {
                    toWrite[pos++] = stof.nextToken();
                }
                toWrite[pos++] = "";

                for (int i=1; i<=2+nMethods; i++)
                {
                    toWrite[pos++] = sto.nextToken();
                }
                toWrite[pos++] = "";

                for (int i=1; i<=2+nMethods; i++)
                {
                    toWrite[pos++] = stf.nextToken();
                }
                toWrite[pos++] = "";

                for (int h=0; h<toWrite.length; h++)
                {
                    out.print(toWrite[h]+";");
                }
                out.println();

                lineof = brof.readLine();
                lineo = bro.readLine();
                linef = brf.readLine();
            }

            out.println();
            out.println();
            out.println();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
   }

   protected static void printTimes(PrintStream out, String path)
   {
        try
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path)));

            String[] methods = validMethodNames;
            int nMethods = methods.length;

            String filler = "";
            for (int i=1; i<nMethods+1; i++)
            {
                filler += ";";
            }

            //print heading
            out.print(";");
            out.print("TOTAL;");
            out.print(filler);
            out.print("ONLINE;");
            out.print(filler);
            out.print("OFFLINE;");
            out.println(filler);

            out.print("DATASET;");
            for (int i=0; i<3; i++)
            {
                for (int j=0; j<nMethods; j++)
                {
                    out.print(methods[j]);
                    out.print(";");
                }
                out.print(";");
            }
            out.println();


            //print body
            //skip the first three lines
            String line="";
            for (int i=1; i<=4; i++)
            {
                line = br.readLine();
            }

            int size = 1+3*nMethods;
            String[] toWrite = new String[size];
            while (line != null)
            {
                int pos = 0;
                StringTokenizer st = new StringTokenizer(line,"; ");

                for (int i=0; i<toWrite.length; i++)
                {
                    toWrite[i] = st.nextToken();
                }

                out.print(toWrite[0]+";");
                for(int x=0; x<3; x++)//total, online, offline
                {
                    for(int y=0; y<nMethods; y++)
                    {
                        int z = 1+3*y+Math.abs(x-2);
                        out.print(toWrite[z]+";");
                    }
                    out.print(";");
                }
                out.println();

                line = br.readLine();
            }
            out.println();
            out.println();
            out.println();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
   }

   protected static void renameFinalSummary()
   {
       try
       {
           String hard = "FinalSummaryHARD";
           String fuzzy = "FinalSummaryFUZZY";
           File fh = new File(outputPath+hard+".csv");
           File ff = new File(outputPath+fuzzy+".csv");

           if (fh.exists())
           {
               fh.renameTo(new File(outputPath+hard+"---"+Math.random()+".csv"));
           }

           if (ff.exists())
           {
               ff.renameTo(new File(outputPath+fuzzy+"---"+Math.random()+".csv"));
           }
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
   }

    private static void doPCE() 
    {
        
        //METHODS
        ProjectiveCentroidComputation scc = new ProjectiveClusterNumericalInstanceCentroidComputationAVG();
        ProjectiveClusterObjectsSimilarity clusterObjectsSim = new JaccardObjectsProjectiveClusterSim();
        //ProjectiveClusterObjectsSimilarity clusterObjectsSim = new SquaredEuclideanObjectsSubspaceClusterSim();
        ProjectiveClusterFeaturesSimilarity clusterFeaturesSim = new JaccardFeaturesProjectiveClusterSim();
        //ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim = new JaccardObjectsFeaturesProjectiveClusterSim();
        ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSimFM = new F1ObjectsFeaturesProjectiveClusterSim();
        //ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSimFM_SLOW = new F1ObjectsFeaturesProjectiveClusterSim_SLOW();
        //ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim = new SquaredEuclideanObjectsFeaturesSubspaceClusterSim();
        //ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim = new JaccardObjectsFeaturesSubspaceClusterSim_HARD();
        //ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim = new SquaredEuclideanObjectsFeaturesSubspaceClusterSim_HARD();
        //ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim_FAST = new JaccardObjectsFeaturesProjectiveClusterSim_FAST();
        ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSimFM_FAST = new F1ObjectsFeaturesProjectiveClusterSim_FAST();
        ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSimFM_SLOW = new F1ObjectsFeaturesProjectiveClusterSim_SLOW();
        //ProjectiveClusterFeaturesSimilarity clusterFeaturesSim = new SquaredEuclideanFeaturesSubspaceClusterSim();
        //ProjectiveClusteringObjectsSimilarity scObjectsSim = new NMIObjectsProjectiveClusteringSim();

        ProjectiveClusteringObjectsSimilarity scObjectsSim = new CEObjectsProjectiveClusteringSim(clusterObjectsSim,onlyHardEvaluation);
        //ProjectiveClusteringObjectsSimilarity scObjectsSimFM = new F1ObjectsProjectiveClusteringSimHARD(macroFM,onlyHardEvaluation);
        //ProjectiveClusteringFeaturesSimilarity scFeaturesSim = new NMIFeaturesProjectiveClusteringSim(onlyHardEvaluation);
        ProjectiveClusteringFeaturesSimilarity scFeaturesSim = new CEFeaturesProjectiveClusteringSim(clusterFeaturesSim,onlyHardEvaluation);
        //ProjectiveClusteringFeaturesSimilarity scFeaturesSimFM = new F1FeaturesProjectiveClusteringSimHARD(macroFM,onlyHardEvaluation);
        ProjectiveClusteringSimilarity fmObjectsSim = new F1ObjectsProjectiveClusteringSim(macroFM,onlyHardEvaluation);
        ProjectiveClusteringSimilarity fmFeaturesSim = new F1FeaturesProjectiveClusteringSim(macroFM,onlyHardEvaluation);
        ProjectiveClusteringSimilarity entropyObjectsSim = new EntropyObjectsProjectiveClusteringSim(onlyHardEvaluation);
        ProjectiveClusteringSimilarity entropyFeaturesSim = new EntropyFeaturesProjectiveClusteringSim(onlyHardEvaluation);
        ProjectiveClusteringSimilarity fmSim = new F1ObjectsFeaturesProjectiveClusteringSim(macroFM,onlyHardEvaluation);
        ProjectiveClusteringSimilarity entropySim = new EntropyObjectsFeaturesProjectiveClusteringSim(onlyHardEvaluation);
        //ProjectiveClusteringObjectsFeaturesSimilarity scObjectsFeaturesSim = new CEObjectsFeaturesProjectiveClusteringSim(clusterObjectsFeaturesSim,onlyHardEvaluation);
        //ProjectiveClusteringObjectsFeaturesSimilarity scObjectsFeaturesSimNMI = new NMIObjectsFeaturesProjectiveClusteringSim(onlyHardEvaluation);

        //PDFSimilarity pdfSim = new MinkowskiPDFSim(2);
        PDFSimilarity pdfSim = new JaccardPDFSim();
        //NumericalInstanceSimilarity numSim = new MinkowskiNumericalInstanceSim(2);
        //NumericalInstanceSimilarity numSim = new JaccardNumericalInstanceSim();
        //PDFSimilarity pdfSim2 = new MinkowskiPDFSim(2);




        //TEST EVALUATION MEASURES
        ArrayList<PCETestEvaluation> testEvaluationMeasures = new ArrayList<PCETestEvaluation>();

        //evaluation wrt reference partition
        //testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsSim, "Theta_o (JACCARD)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scFeaturesSim, "Theta_f (JACCARD)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsSimNMI, "Theta_o (NMI)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scFeaturesSimNMI, "Theta_f (NMI)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmObjectsSim, "Theta_o", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmFeaturesSim, "Theta_f", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmSim, "Theta_{of}", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        if(fm && evaluationWrtReferenceClassification)
        {
            testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmObjectsSim, "Theta_o (FM)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
            testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmFeaturesSim, "Theta_f (FM)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
            testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmSim, "Theta_{of} (FM)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        }
        if(entropy && evaluationWrtReferenceClassification)
        {
            testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropyObjectsSim, "Theta_o (ENTROPY)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
            testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropyFeaturesSim, "Theta_f (ENTROPY)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
            testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropySim, "Theta_{of} (ENTROPY)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        }
        //testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsFeaturesSim, "Theta_{of} (JACCARD)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsFeaturesSimNMI, "Theta_{of} (NMI)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));

        //evaluation wrt ensemble
        //testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsSim, "Upsilon_o (JACCARD)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scFeaturesSim, "Upsilon_f (JACCARD)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsSimNMI, "Upsilon_o (NMI)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scFeaturesSimNMI, "Upsilon_f (NMI)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        if (fm && evaluationWrtEnsembleSolutions)
        {
            testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmObjectsSim, "Upsilon_o (FM)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
            testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmFeaturesSim, "Upsilon_f (FM)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
            testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmSim, "Upsilon_{of} (FM)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        }
        if (entropy && evaluationWrtEnsembleSolutions)
        {
            testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropyObjectsSim, "Upsilon_o (ENTROPY)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
            testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropyFeaturesSim, "Upsilon_f (ENTROPY)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
            testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropySim, "Upsilon_{of} (ENTROPY)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        }
        //testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsFeaturesSim, "Upsilon_{of} (JACCARD)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));
        //testEvaluationMeasures.add(new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsFeaturesSimNMI, "Upsilon_{of} (NMI)", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));

        //internal evaluation
        //testEvaluationMeasures.add(new InternalPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, log, scc, "", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended));

        //times
        testEvaluationMeasures.add(new TimesPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, onlyAVGtimes));

        DataLoader dl = null;
        Dataset d = null;
        PCEMethod pceMethod = null;
        ProjectiveClusteringDataset ensemble = null;


//DATASET
        int datasetCount = 0;
        for (int dat=0; dat<datasetNames.length; dat++)
        {
            String datasetPath = "";
            String datasetPathARFF = "";
            String libraryPath = "";
            
            if (single_dataset == null)
            {
                datasetPath = datasetPrefix+datasetNames[dat]+".data";
                datasetPathARFF = datasetPrefixARFF+datasetNames[dat]+".arff";
            }
            else
            {
                datasetPath = single_dataset;
            }

            dl = new NumericalInstanceDataLoaderUCI_Standard(datasetPath);
            Object[] loaded = dl.load();
            Instance[] instances = (Instance[])loaded[0];
            Clustering refPartition = (Clustering)loaded[1];
            Double[][] subspaces = (Double[][])loaded[2];
            d = new NumericalInstanceDataset(instances,refPartition);

            String classes = "  #classes=";
            if (refPartition == null || refPartition.getNumberOfClusters() == 1)
            {
                classes+="n.a.";
                evaluationWrtReferenceClassification = false;
            }
            else
            {
                classes+=""+refPartition.getNumberOfClusters();
            }

            log.println("DATASET "+(dat+1)+"of"+datasetNames.length+": "+datasetNames[dat]+ " (#objects="+d.getDataLength()+"  #attributes="+d.getNumberOfFeatures()+classes +")");
            //log.println("#OBJECTS="+d.getDataLength()+"  #ATTRIBUTES="+d.getNumberOfFeatures()+classes);
            log.println();
            log.println();

            if (saveConsensusClusterings)
            {
                cons.println("DATASET "+(dat+1)+"of"+datasetNames.length+": "+datasetNames[dat]);
                cons.println();
            }


            int nClusters = 0;
            if (nClustersArray != null)
            {
                nClusters = nClustersArray[dat];
            }
            else if (refPartition != null || refPartition.getNumberOfClusters()>1)
            {
                nClusters = refPartition.getNumberOfClusters();
            }
            else
            {
                throw new RuntimeException("ERROR: you must specify the number of clusters in the consensus clusterings if the reference classification is not available");
            }





//ENSEMBLE TYPE
            for (int type=0; type<ensembleTypes.length; type++)
            {
//ENSEMBLES
                for (int ens=1; ens<=nEnsembles; ens++)
                {
                    if (single_ensemble == null)
                    {
                        String suffix = ""+datasetNames[dat]+"_ENSEMBLE_"+ens+".data";
                        libraryPath = ensemblePrefix+suffix;
                    }
                    else
                    {
                        libraryPath = single_ensemble;
                    }


                    /////////////////////////////////////////////////////////////////////////////
                    //caricamento clustering library
                    DataLoader dlLib = new ProjectiveClusteringDataLoaderLibrary(libraryPath,dl);
                    Object[] result = dlLib.optimizedLoadGivenRawData(instances, refPartition);


                    ProjectiveClustering[] library = (ProjectiveClustering[])result[0];

                    log.println("ENSEMBLE "+ens+"of"+nEnsembles+" (SIZE="+library.length+")");
                    log.println();

                    if (saveConsensusClusterings)
                    {
                        cons.println("ENSEMBLE "+ens+"of"+nEnsembles);
                        cons.println();
                    }

                    ensemble = new ProjectiveClusteringDataset(library,refPartition,subspaces);

//METHODS
                    for (int z=0; z<validMethodNames.length; z++)
                    {
                        int multiResultBehavior = -1;
                        boolean alpha = false;
                        int sizeResults = 1;
                        int startAlpha = 2;
                        int endAlpha = 2;
                        int startBeta = 2;
                        int endBeta = 2;
                        String currentMethodName = validMethodNames[z];

                        log.println(currentMethodName+" Algorithm---STARTED");
                        //log.println();

                        if (saveConsensusClusterings)
                        {
                            cons.println(currentMethodName+" Algorithm---BEGIN");
                            cons.println();
                        }

                        if (currentMethodName.equals("MOEA-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters+", population_size="+populationSize+", max_iterations="+maxIterations);
                            pceMethod = new MOEAClusteringBasedPCE(ensemble,maxIterations,scObjectsSim,scFeaturesSim,populationSize);
                            //multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVGMAX;
                            alpha = false;
                        }
                        else if (currentMethodName.equals("EM-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaEMPCE);
                            //pceMethod = new EMlikePCE_ObjectByFeatureMatrix_OPTIMIZED(ensemble);
                            pceMethod = new EMlikePCE_EMPCE_LATEST(ensemble);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                            sizeResults = (tryOtherMs)?(maxM-1):1;
                            alpha = true;
                            startAlpha = (tryOtherMs)?2:alphaEMPCE;
                            endAlpha = (tryOtherMs)?maxM:startAlpha;
                            startBeta = 2;
                            endBeta = 2;
                        }
                        else if (currentMethodName.equals("CB-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaCBPCE+", beta="+betaCBPCE);
                            //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim,2,2,false);
                            //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSimFM_SLOW,2,2,false);
                            pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSimFM_SLOW,2,2,false);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                            sizeResults = (tryOtherMs)?(maxM-1)*(maxM-1):1;
                            alpha = true;
                            startAlpha = (tryOtherMs)?2:alphaCBPCE;
                            endAlpha = (tryOtherMs)?maxM:startAlpha;
                            startBeta = (tryOtherMs)?2:betaCBPCE;
                            endBeta = (tryOtherMs)?maxM:startBeta;
                        }
                        else if (currentMethodName.equals("FCB-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaFCBPCE+", beta="+betaFCBPCE);
                            //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim_FAST,2,2,false);
                            pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSimFM_FAST,2,2,false);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                            sizeResults = (tryOtherMs)?(maxM-1)*(maxM-1):1;
                            alpha = true;
                            startAlpha = (tryOtherMs)?2:alphaFCBPCE;
                            endAlpha = (tryOtherMs)?maxM:startAlpha;
                            startBeta = (tryOtherMs)?2:betaFCBPCE;
                            endBeta = (tryOtherMs)?maxM:startBeta;
                        }
                        else if (currentMethodName.equals("CO-CE-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters);
                            //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim_FAST,2,2,false);
                            pceMethod = new CoOccurrence_CEBasedPCE(ensemble);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                            alpha = false;
                        }
                        else if (currentMethodName.equals("IVC-CE-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters);
                            //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim_FAST,2,2,false);
                            pceMethod = new IVC_CEbasedPCE(ensemble);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                            alpha = false;
                        }
                        else if (currentMethodName.equals("MCS-CE-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters);
                            //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim_FAST,2,2,false);
                            pceMethod = new MCS_CEbasedPCE(ensemble);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                            alpha = false;
                        }
                        else if (currentMethodName.equals("PROCLUS-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters+", d="+ensemble.getAvgNUmberOfFeaturesInProjectiveRefPartition());
                            //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim_FAST,2,2,false);
                            pceMethod = new ProclusPCE(ensemble, datasetPathARFF);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                            alpha = false;
                        }
                        else if (currentMethodName.equals("LAC-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters+", h=0.2");
                            //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim_FAST,2,2,false);
                            pceMethod = new LacPCE(ensemble, d);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                            alpha = false;
                        }
                        else if (currentMethodName.equals("E-EM-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaEMPCE);
                            //pceMethod = new EMlikePCE_ObjectByFeatureMatrix_OPTIMIZED(ensemble);
                            pceMethod = new EMlikePCE_EEMPCE_LATEST(ensemble,false);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                            sizeResults = (tryOtherMs)?(maxM-1):1;
                            alpha = true;
                            startAlpha = (tryOtherMs)?2:alphaEMPCE;
                            endAlpha = (tryOtherMs)?maxM:startAlpha;
                            startBeta = 2;
                            endBeta = 2;
                        }
                        else if (currentMethodName.equals("E-2S-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters);
                            pceMethod = new OneObjectiveInstanceBasedPCE_E2SPCE(ensemble, pdfSim);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;
                            //multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVGMAX;
                            alpha = false;
                        }
                        else if (currentMethodName.equals("E-CB-PCE"))
                        {
                            log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaCBPCE+", beta="+betaCBPCE);
                            //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim,2,2,false);
                            //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSimFM_SLOW,2,2,false);
                            pceMethod = new OneObjectiveClusterBasedPCE_ECBPCE_ML2012(ensemble,clusterObjectsFeaturesSimFM,2,2,false);
                            multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                            sizeResults = (tryOtherMs)?(maxM-1)*(maxM-1):1;
                            alpha = true;
                            startAlpha = (tryOtherMs)?2:alphaCBPCE;
                            endAlpha = (tryOtherMs)?maxM:startAlpha;
                            startBeta = (tryOtherMs)?2:betaCBPCE;
                            endBeta = (tryOtherMs)?maxM:startBeta;
                        }
                        else
                        {
                            throw new RuntimeException("ERROR: PCE algorithm unknown");
                        }


                        /*
                        switch(meth)
                        {
                            case 0:
                            {
                                log.println("PARAMETERS:  number_of_clusters="+nClusters+", population_size="+populationSize+", max_iterations="+maxIterations);
                                //pceMethod = new MOEAClusteringBasedPCE_OPTIMIZED(ensemble,maxIterations,scObjectsSim,scFeaturesSim,populationSize);
                                pceMethod = new MOEAClusteringBasedPCE_OPTIMIZED(ensemble,maxIterations,scObjectsSimFM,scFeaturesSimFM,populationSize);
                                //multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVGMAX;
                                multi = true;

                                break;
                            }
                            case 1:
                            {
                                pceMethod = null; //stessi risultati di case 0---cambia solo il multiResultBehavior
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                multi = true;
                                break;
                            }
                            case 2:
                            {
                                log.println("PARAMETERS:  number_of_clusters="+nClusters+", population_size="+populationSize+", max_iterations="+maxIterations);
                                pceMethod = new MOEAClusteringBasedPCE(ensemble,maxIterations,scObjectsSim,scFeaturesSim,populationSize);
                                //multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVGMAX;
                                multi = true;

                                break;
                            }
                            case 3:
                            {
                                pceMethod = null; //stessi risultati di case 3---cambia solo il multiResultBehavior
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                multi = true;
                                break;
                            }
                            case 4:
                            {
                                //pceMethod = new OneObjectiveInstanceBasedPCE_EUCLIDEAN(ensemble);
                                pceMethod = new OneObjectiveInstanceBasedPCE_E2SPCE(ensemble,pdfSim);
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;
                                break;
                            }
                            case 5:{pceMethod = new OneObjectiveClusterBasedPCE_KMEANS(ensemble);multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;break;}
                            case 6:{pceMethod = new OneObjectiveFeatureBasedPCE(ensemble,pdfSim,numSim);multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;break;}
                            case 7:
                            {
                                //pceMethod = new EMlikePCE_EEMPCE(ensemble);
                                pceMethod = new EMlikePCE_EEMPCE_LATEST(ensemble,true);
                                //pceMethod = new EMlikePCE_EEMPCE_CENTROID(ensemble);
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                alpha = true;
                                break;
                            }
                            case 8:
                            {
                                //pceMethod = new EMlikePCE_HYBRID(ensemble);
                                pceMethod = new EMlikePCE_HYBRIDII(ensemble);
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                alpha = true;
                                break;
                            }
                            case 9:
                            {
                                pceMethod = new EMlikePCE_EMFBPCE(ensemble);
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                alpha = true;
                                break;
                            }
                            case 10:
                            {
                                log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaEMPCE);
                                //pceMethod = new EMlikePCE_ObjectByFeatureMatrix_OPTIMIZED(ensemble);
                                pceMethod = new EMlikePCE_EMPCE_LATEST(ensemble);
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                alpha = true;

                                break;
                            }
                            case 11:
                            {
                                //pceMethod = new EMlikePCE_ObjectByFeatureMatrix_OPTIMIZED(ensemble);
                                pceMethod = new EMlikePCE_EMPCE_LATEST(ensemble);
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;
                                alpha = true;
                                break;
                            }
                            case 12:{pceMethod = new OneObjectiveClusterBasedPCE(ensemble,clusterObjectsSim,clusterFeaturesSim);multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;break;}
                            case 13:{pceMethod = new OneObjectiveClusterBasedPCE_KMEANS_NORMALIZATION(ensemble);multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;break;}
                            case 14:
                            {
                                log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaCBPCE+", beta="+betaCBPCE);
                                //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim,2,2,false);
                                //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSimFM_SLOW,2,2,false);
                                pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSimFM,2,2,false);
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                alpha = true;

                                break;
                            }
                            case 15:
                            {
                                pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim,2,2,true);
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                alpha = true;
                                break;
                            }
                            case 16:
                            {
                                log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaFCBPCE+", beta="+betaFCBPCE);
                                //pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim_FAST,2,2,false);
                                pceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSimFM_FAST,2,2,false);
                                multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                alpha = true;

                                break;
                            }
                        }
                        */


//RUNS
                        ProjectiveClustering[] results = null;
                        log.print("RUNS (total runs="+numberOfRuns+"):  ");
                        for (int run=1; run<=numberOfRuns; run++)
                        {
                            if (results != null)
                            {
                                for (int i=0; i<results.length; i++)
                                {
                                    results[i] = null;
                                }
                            }
                            results = null;
                            //System.gc();

                            if (saveConsensusClusterings)
                            {
                                cons.println("RUN "+run+"of"+numberOfRuns);
                                cons.println();
                            }

                            if (!alpha)
                            {
                                pceMethod.execute(nClusters);
                                results = pceMethod.getAllResults();
                            }
                            else
                            {
                                results = new ProjectiveClustering[sizeResults];
                                int pos = 0;

                                for (int x=startAlpha; x<=endAlpha; x++)
                                {
                                    for (int y=startBeta; y<=endBeta; y++)
                                    {
                                        ((AlphaBetaPCEMethod)pceMethod).setAlpha(x);
                                        ((AlphaBetaPCEMethod)pceMethod).setBeta(y);
                                        results[pos] = pceMethod.execute(nClusters);

                                        pos++;
                                    }
                                }
                            }

//EVALUATIONS
                            for (int meas=0; meas<testEvaluationMeasures.size(); meas++)
                            {
                                if (!(testEvaluationMeasures.get(meas) instanceof ReferencePartitionPCETestEvaluation) && !(testEvaluationMeasures.get(meas) instanceof EnsemblePCETestEvaluation))
                                {
                                    testEvaluationMeasures.get(meas).addEvaluation(run, datasetCount, ens-1, z, ensemble, results, multiResultBehavior, pceMethod);
                                }
                                else if ((testEvaluationMeasures.get(meas) instanceof ReferencePartitionPCETestEvaluation && evaluationWrtReferenceClassification && refPartition != null && refPartition.getNumberOfClusters() > 1))
                                {
                                    testEvaluationMeasures.get(meas).addEvaluation(run, datasetCount, ens-1, z, ensemble, results, multiResultBehavior, pceMethod);
                                }
                                else if(testEvaluationMeasures.get(meas) instanceof EnsemblePCETestEvaluation && evaluationWrtEnsembleSolutions)
                                {
                                    testEvaluationMeasures.get(meas).addEvaluation(run, datasetCount, ens-1, z, ensemble, results, multiResultBehavior, pceMethod);
                                }
                            }

                            ensemble.getProjectiveRefPartitionHard();
                            if (run<numberOfRuns)
                            {
                                log.print(run+", ");
                            }
                            else
                            {
                                log.println(run);
                                //log.println();
                            }

                            if (saveConsensusClusterings)
                            {
                                printConsensusClusterings(results, multiResultBehavior);
                            }
                        }

                        log.println(currentMethodName+" Algorithm---DONE");
                        log.println();

                        if (saveConsensusClusterings)
                        {
                            cons.println(currentMethodName+" Algorithm---END");
                            cons.println();
                        }

                    }

                    log.println();
                    log.println();

                    if (saveConsensusClusterings)
                    {
                        cons.println();
                        cons.println();
                    }
                }
                datasetCount++;

                log.println();
                log.println();
                log.println();

                if (saveConsensusClusterings)
                {
                    cons.println();
                    cons.println();
                }

            }
            
            if (single_dataset == null)
            {
                printFinalSummary();
            }
        }
    }

    private static void initializeOutputPath() 
    {
        try
        {
            
            String s = ""+System.currentTimeMillis();
            File f = new File(outputPath+s);
            f.mkdirs();
            outputPath += s+File.separator;            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
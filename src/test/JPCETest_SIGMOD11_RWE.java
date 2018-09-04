package test;

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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;



@Deprecated
public class JPCETest_SIGMOD11_RWE//deprecated: old version, used only for SIGMOD 2011 RWE; use JPCETest.java instead
{
    //output files to be written
    private static PrintStream log = null;
    //private static String logFileName = "SIGMOD11_Repeatability_gullo550---";
    private static String logFileName = "JPCE---";
    private static PrintStream cons = null;
    private static String consFileName = "ConsensusClusterings---";
    
    //paths
    private static String datasetPrefix = "raw_datasets"+File.separator;
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
    private static boolean printCompleteCSV = false;
    private static boolean printSummaryCSV = true;
    private static boolean printSummaryCSV_extended = false;
    private static boolean onlyAVGtimes = true;
    //private static String[] ensembleTypes = new String[]{"fuzzy", "hard", "fuzzyhard"};
    private static String[] ensembleTypes = new String[]{"fuzzyhard"};
    private static int maxM = 6;
    private static boolean tryOtherMs = true;
    private static int numberOfTestEvaluationMeasures = 7;
    private static int nMethods = 17;
    private static boolean macroFM = true;
    private static boolean fm = false;//if true, evaluation according to F-Measure, otherwise evaluation according to Entropy
    private static boolean onlyHardEvaluation = true;
    
    
    public static void main(String[] args)
    {
        File file = null;
        FileOutputStream fos = null;
        try
        {
            file = File.createTempFile(logFileName, ".log", new File(outputPath));
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
        
        loadProperties();
        

        System.out.println();
        System.out.println("Running...");
        System.out.println();


        if (do_ensembleGeneration)
        {
            GenerateEnsemble.generate(datasetNamesEnsembleGeneration, nEnsemblesEnsembleGeneration, ensembleSize, datasetPrefix, ensemblePrefix, log, true, -1);
        }

        if (do_PCE)
        {
            //METHODS
            String[] methodNames = new String[nMethods];
            for (int j=0; j<methodNames.length; j++)
            {
                switch(j)
                {
                    case 0:{methodNames[j] = "MOEA-PCE (optimized)"; break;}
                    case 1:{methodNames[j] = "MOEA-PCE (optimized,multiResultBehavior=MAX)"; break;}
                    case 2:{methodNames[j] = "MOEA-PCE"; break;}
                    case 3:{methodNames[j] = "MOEA-PCE (multiResultBehavior=MAX)"; break;}
                    case 4:{methodNames[j] = "1Objective Instance-based---2S-OB-PCE"; break;}
                    case 5:{methodNames[j] = "1Objective Cluster-based"; break;}
                    case 6:{methodNames[j] = "1Objective Feature-based"; break;}
                    case 7:{methodNames[j] = "EM-OB-PCE"; break;}
                    case 8:{methodNames[j] = "EM-PCE-HYBRID"; break;}
                    case 9:{methodNames[j] = "EM-FB-PCE"; break;}
                    case 10:{methodNames[j] = "EM-PCE"; break;}
                    case 11:{methodNames[j] = "EM-PCE (AVG)"; break;}
                    case 12:{methodNames[j] = "1Objective Cluster-based---JACCARD"; break;}
                    case 13:{methodNames[j] = "1Objective Cluster-based---KMEANS,NORMALIZATION"; break;}
                    case 14:{methodNames[j] = "CB-PCE"; break;}
                    case 15:{methodNames[j] = "CB-PCE (FEATURE CENTROID)"; break;}
                    case 16:{methodNames[j] = "FCB-PCE"; break;}
                }
            }

            ProjectiveCentroidComputation scc = new ProjectiveClusterNumericalInstanceCentroidComputationAVG();
            ProjectiveClusterObjectsSimilarity clusterObjectsSim = new JaccardObjectsProjectiveClusterSim();
            //ProjectiveClusterObjectsSimilarity clusterObjectsSim = new SquaredEuclideanObjectsSubspaceClusterSim();
            ProjectiveClusterFeaturesSimilarity clusterFeaturesSim = new JaccardFeaturesProjectiveClusterSim();
            ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim = new JaccardObjectsFeaturesProjectiveClusterSim();
            ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSimFM = new F1ObjectsFeaturesProjectiveClusterSim();
            ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSimFM_SLOW = new F1ObjectsFeaturesProjectiveClusterSim_SLOW();
            //ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim = new SquaredEuclideanObjectsFeaturesSubspaceClusterSim();
            //ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim = new JaccardObjectsFeaturesSubspaceClusterSim_HARD();
            //ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim = new SquaredEuclideanObjectsFeaturesSubspaceClusterSim_HARD();
            ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim_FAST = new JaccardObjectsFeaturesProjectiveClusterSim_FAST();
            ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSimFM_FAST = new F1ObjectsFeaturesProjectiveClusterSim_FAST();
            //ProjectiveClusterFeaturesSimilarity clusterFeaturesSim = new SquaredEuclideanFeaturesSubspaceClusterSim();
            //ProjectiveClusteringObjectsSimilarity scObjectsSim = new NMIObjectsProjectiveClusteringSim();

            ProjectiveClusteringObjectsSimilarity scObjectsSim = new CEObjectsProjectiveClusteringSim(clusterObjectsSim,onlyHardEvaluation);
            ProjectiveClusteringObjectsSimilarity scObjectsSimFM = new F1ObjectsProjectiveClusteringSimHARD(macroFM,onlyHardEvaluation);
            //ProjectiveClusteringFeaturesSimilarity scFeaturesSim = new NMIFeaturesProjectiveClusteringSim(onlyHardEvaluation);
            ProjectiveClusteringFeaturesSimilarity scFeaturesSim = new CEFeaturesProjectiveClusteringSim(clusterFeaturesSim,onlyHardEvaluation);
            ProjectiveClusteringFeaturesSimilarity scFeaturesSimFM = new F1FeaturesProjectiveClusteringSimHARD(macroFM,onlyHardEvaluation);
            ProjectiveClusteringSimilarity fmObjectsSim = new F1ObjectsProjectiveClusteringSim(macroFM,onlyHardEvaluation);
            ProjectiveClusteringSimilarity fmFeaturesSim = new F1FeaturesProjectiveClusteringSim(macroFM,onlyHardEvaluation);
            ProjectiveClusteringSimilarity entropyObjectsSim = new EntropyObjectsProjectiveClusteringSim(onlyHardEvaluation);
            ProjectiveClusteringSimilarity entropyFeaturesSim = new EntropyFeaturesProjectiveClusteringSim(onlyHardEvaluation);
            ProjectiveClusteringSimilarity fmSim = new F1ObjectsFeaturesProjectiveClusteringSim(macroFM,onlyHardEvaluation);
            ProjectiveClusteringSimilarity entropySim = new EntropyObjectsFeaturesProjectiveClusteringSim(onlyHardEvaluation);
            ProjectiveClusteringObjectsFeaturesSimilarity scObjectsFeaturesSim = new CEObjectsFeaturesProjectiveClusteringSim(clusterObjectsFeaturesSim,onlyHardEvaluation);
            ProjectiveClusteringObjectsFeaturesSimilarity scObjectsFeaturesSimNMI = new NMIObjectsFeaturesProjectiveClusteringSim(onlyHardEvaluation);

            //PDFSimilarity pdfSim = new MinkowskiPDFSim(2);
            PDFSimilarity pdfSim = new JaccardPDFSim();
            //NumericalInstanceSimilarity numSim = new MinkowskiNumericalInstanceSim(2);
            NumericalInstanceSimilarity numSim = new JaccardNumericalInstanceSim();
            PDFSimilarity pdfSim2 = new MinkowskiPDFSim(2);




            //TEST EVALUATION MEASURES
            PCETestEvaluation[] testEvaluationMeasures = new PCETestEvaluation[numberOfTestEvaluationMeasures];

            //evaluation wrt reference partition
            int index = 0;
            //testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsSim, "Theta_o (JACCARD)", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scFeaturesSim, "Theta_f (JACCARD)", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsSimNMI, "Theta_o (NMI)", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scFeaturesSimNMI, "Theta_f (NMI)", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmObjectsSim, "Theta_o", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmFeaturesSim, "Theta_f", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmSim, "Theta_{of}", printCompleteCSV, printSummaryCSV);
            if(fm)
            {
                testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmObjectsSim, "Theta_o", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);
                testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmFeaturesSim, "Theta_f", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);
                testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmSim, "Theta_{of}", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);                            
            }
            else
            {
                testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropyObjectsSim, "Theta_o", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);
                testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropyFeaturesSim, "Theta_f", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);
                testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropySim, "Theta_{of}", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);            
            }
            //testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsFeaturesSim, "Theta_{of} (JACCARD)", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new ReferencePartitionPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsFeaturesSimNMI, "Theta_{of} (NMI)", printCompleteCSV, printSummaryCSV);

            //evaluation wrt ensemble
            //testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsSim, "Upsilon_o (JACCARD)", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scFeaturesSim, "Upsilon_f (JACCARD)", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsSimNMI, "Upsilon_o (NMI)", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scFeaturesSimNMI, "Upsilon_f (NMI)", printCompleteCSV, printSummaryCSV);
            if (fm)
            {
                testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmObjectsSim, "Upsilon_o", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);
                testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmFeaturesSim, "Upsilon_f", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);
                testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, fmSim, "Upsilon_{of}", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);
            }
            else
            {
                testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropyObjectsSim, "Upsilon_o", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);
                testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropyFeaturesSim, "Upsilon_f", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);
                testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, entropySim, "Upsilon_{of}", printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);
            }
            //testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsFeaturesSim, "Upsilon_{of} (JACCARD)", printCompleteCSV, printSummaryCSV);
            //testEvaluationMeasures[index++] = new EnsemblePCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, scObjectsFeaturesSimNMI, "Upsilon_{of} (NMI)", printCompleteCSV, printSummaryCSV);

            //internal evaluation
            //testEvaluationMeasures[index++] = new InternalPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, out, scc, "", printCompleteCSV, printSummaryCSV);

            //times
            testEvaluationMeasures[index++] = new TimesPCETestEvaluation(validDatasetNames, validMethodNames, nEnsembles, numberOfRuns, outputPath, null, onlyAVGtimes);

            DataLoader dl = null;
            Dataset d = null;
            PCEMethod sceMethod = null;
            ProjectiveClusteringDataset ensemble = null;
    

//DATASET
            int datasetCount = 0;
            for (int dat=0; dat<datasetNames.length; dat++)
            {
                String datasetPath = datasetPrefix+datasetNames[dat]+".data";
                String libraryPath = "";

                dl = new NumericalInstanceDataLoaderUCI_Standard(datasetPath);
                d = new NumericalInstanceDataset(dl);
                Instance[] instances = d.getData();
                Clustering refPartition = d.getReferencePartition();

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

                //int nInstances = d.getDataLength();
                //int nFeatures = d.getNumberOfFeatures();




    //ENSEMBLE TYPE
                for (int type=0; type<ensembleTypes.length; type++)
                {
    //ENSEMBLES
                    for (int ens=1; ens<=nEnsembles; ens++)
                    {
                        String suffix = ""+datasetNames[dat]+"_ENSEMBLE_"+ens+".data";
                        libraryPath = ensemblePrefix+suffix;


                        /////////////////////////////////////////////////////////////////////////////
                        //caricamento clustering library
                        DataLoader dlLib = new ProjectiveClusteringDataLoaderLibrary(libraryPath,dl);
                        Object[] result = null;
                        result = dlLib.optimizedLoadGivenRawData(instances, refPartition);


                        ProjectiveClustering[] library = (ProjectiveClustering[])result[0];

                        log.println("ENSEMBLE "+ens+"of"+nEnsembles+" (SIZE="+library.length+")");
                        log.println();
                        
                        if (saveConsensusClusterings)
                        {
                            cons.println("ENSEMBLE "+ens+"of"+nEnsembles);
                            cons.println();
                        }

                        ensemble = new ProjectiveClusteringDataset(library,refPartition,null);



                        //System.out.println("TERMINATO --- Caricamento clustering library");
                        //System.out.println("ENSEMBLE NUMBER: "+ens+"of"+nEnsembles+"\n");

    //METHODS
                        long offline = 0;
                        ProjectiveClustering[][] previousResults = null;
                        for (int z=0; z<validMethods.length; z++)
                        //for (int j=0; j<nMethods; j++)
                        {
                            int multiResultBehavior = -1;
                            boolean alpha = false;
                            boolean multi = false;
                            int meth = validMethods[z];

                            log.println(methodNames[meth]+" Algorithm---STARTED");
                            //log.println();
                            
                            if (saveConsensusClusterings)
                            {
                                cons.println(methodNames[meth]+" Algorithm---BEGIN");
                                cons.println();
                            }


                            switch(meth)
                            {
                                case 0:
                                {
                                    log.println("PARAMETERS:  number_of_clusters="+nClusters+", population_size="+populationSize+", max_iterations="+maxIterations);
                                    //sceMethod = new MOEAClusteringBasedPCE_OPTIMIZED(ensemble,maxIterations,scObjectsSim,scFeaturesSim,populationSize);
                                    sceMethod = new MOEAClusteringBasedPCE_OPTIMIZED(ensemble,maxIterations,scObjectsSimFM,scFeaturesSimFM,populationSize);
                                    //multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVGMAX;
                                    multi = true;

                                    break;
                                }
                                case 1:
                                {
                                    sceMethod = null; //stessi risultati di case 0---cambia solo il multiResultBehavior
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                    multi = true;
                                    break;
                                }
                                case 2:
                                {
                                    log.println("PARAMETERS:  number_of_clusters="+nClusters+", population_size="+populationSize+", max_iterations="+maxIterations);
                                    sceMethod = new MOEAClusteringBasedPCE(ensemble,maxIterations,scObjectsSim,scFeaturesSim,populationSize);
                                    //multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVGMAX;
                                    multi = true;

                                    break;
                                }
                                case 3:
                                {
                                    sceMethod = null; //stessi risultati di case 3---cambia solo il multiResultBehavior
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                    multi = true;
                                    break;
                                }
                                case 4:
                                {
                                    //sceMethod = new OneObjectiveInstanceBasedPCE_EUCLIDEAN(ensemble);
                                    sceMethod = new OneObjectiveInstanceBasedPCE_E2SPCE(ensemble,pdfSim);
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;
                                    break;
                                }
                                case 5:{sceMethod = new OneObjectiveClusterBasedPCE_KMEANS(ensemble);multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;break;}
                                case 6:{sceMethod = new OneObjectiveFeatureBasedPCE(ensemble,pdfSim,numSim);multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;break;}
                                case 7:
                                {
                                    //sceMethod = new EMlikePCE_EEMPCE(ensemble);
                                    sceMethod = new EMlikePCE_EEMPCE_LATEST(ensemble,true);
                                    //sceMethod = new EMlikePCE_EEMPCE_CENTROID(ensemble);
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                    alpha = true;
                                    break;
                                }
                                case 8:
                                {
                                    //sceMethod = new EMlikePCE_HYBRID(ensemble);
                                    sceMethod = new EMlikePCE_HYBRIDII(ensemble);
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                    alpha = true;
                                    break;
                                }
                                case 9:
                                {
                                    sceMethod = new EMlikePCE_EMFBPCE(ensemble);
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                    alpha = true;
                                    break;
                                }
                                case 10:
                                {
                                    log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaEMPCE);
                                    //sceMethod = new EMlikePCE_ObjectByFeatureMatrix_OPTIMIZED(ensemble);
                                    sceMethod = new EMlikePCE_EMPCE_LATEST(ensemble);
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                    alpha = true;

                                    break;
                                }
                                case 11:
                                {
                                    //sceMethod = new EMlikePCE_ObjectByFeatureMatrix_OPTIMIZED(ensemble);
                                    sceMethod = new EMlikePCE_EMPCE_LATEST(ensemble);
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;
                                    alpha = true;
                                    break;
                                }
                                case 12:{sceMethod = new OneObjectiveClusterBasedPCE(ensemble,clusterObjectsSim,clusterFeaturesSim);multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;break;}
                                case 13:{sceMethod = new OneObjectiveClusterBasedPCE_KMEANS_NORMALIZATION(ensemble);multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG;break;}
                                case 14:
                                {
                                    log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaCBPCE+", beta="+betaCBPCE);
                                    //sceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim,2,2,false);
                                    sceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSimFM_SLOW,2,2,false);
                                    //sceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSimFM,2,2,false);
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                    alpha = true;

                                    break;
                                }
                                case 15:
                                {
                                    sceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim,2,2,true);
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                    alpha = true;
                                    break;
                                }
                                case 16:
                                {
                                    log.println("PARAMETERS:  number_of_clusters="+nClusters+", alpha="+alphaFCBPCE+", beta="+betaFCBPCE);
                                    //sceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSim_FAST,2,2,false);
                                    sceMethod = new OneObjectiveClusterBasedPCE_CBPCE(ensemble,clusterObjectsFeaturesSimFM_FAST,2,2,false);
                                    multiResultBehavior=PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX;
                                    alpha = true;

                                    break;
                                }
                            }


                            //System.out.println("---"+methodNames[meth]+"---");

                            boolean needForPreviousResults = false;
                            for (int x=0; x<validMethods.length; x++)
                            {
                                if (validMethods[x] == 1)
                                {
                                    needForPreviousResults = true;
                                }
                            }
                            if (needForPreviousResults && sceMethod instanceof MOEAClusteringBasedPCE_OPTIMIZED || sceMethod instanceof MOEAClusteringBasedPCE)
                            {
                                previousResults = new ProjectiveClustering[numberOfRuns][populationSize];
                            }

    //RUNS
                            ProjectiveClustering[] results = null;
                            log.print("RUNS (total runs="+numberOfRuns+"):  ");
                               
                            for (int run=1; run<=numberOfRuns; run++)
                            {
                                //System.out.println("Run: "+run+"di"+numberOfRuns);
                                if (results != null)
                                {
                                    for (int i=0; i<results.length; i++)
                                    {
                                        results[i] = null;
                                    }
                                }
                                results = null;
                                ArrayList<ProjectiveClustering> resultsArray = null;
                                System.gc();
                                
                                if (saveConsensusClusterings)
                                {
                                    cons.println("RUN "+run+"of"+numberOfRuns);
                                    cons.println();
                                }

                                if (sceMethod == null)
                                {
                                    results = previousResults[run-1];
                                }
                                else
                                {
                                    if (multi)
                                    {
                                        ProjectiveClustering sceResult = sceMethod.execute(nClusters);
                                        if (meth == 14)
                                        {
                                            offline = sceMethod.getOfflineExecutionTime();
                                        }
                                        else if (meth == 15)
                                        {
                                            sceMethod.setOfflineExecutionTime(offline);
                                        }
                                        results = sceMethod.getAllResults();
                                    }
                                    else
                                    {
                                        if (alpha)
                                        {
                                            if (meth > 13)
                                            {

                                                if (!tryOtherMs)
                                                {
                                                    results = new ProjectiveClustering[1];
                                                }
                                                else
                                                {
                                                    results = new ProjectiveClustering[(maxM-1)*(maxM-1)];
                                                }
                                                resultsArray = new ArrayList<ProjectiveClustering>(results.length);
                                                int pos = 0;
                                                int alphaInt = 2;
                                                int betaInt = 2;
                                                int maxAlphaInt = maxM;
                                                int maxBetaInt = maxM;
                                                if (!tryOtherMs)
                                                {
                                                    alphaInt = alphaCBPCE;
                                                    betaInt = betaCBPCE;
                                                    maxAlphaInt = alphaCBPCE;
                                                    maxBetaInt = betaCBPCE;
                                                }
                                                for (; alphaInt<=maxAlphaInt; alphaInt++)
                                                {
                                                    for (; betaInt<=maxBetaInt; betaInt++)
                                                    {
                                                        //System.out.println("Alpha="+alphaInt+"  Beta="+betaInt);
                                                        ((OneObjectiveClusterBasedPCE_CBPCE)sceMethod).setAlpha(alphaInt);
                                                        ((OneObjectiveClusterBasedPCE_CBPCE)sceMethod).setBeta(betaInt);
                                                        results[pos] = sceMethod.execute(nClusters);

                                                        ProjectiveClustering[] allResults = sceMethod.getAllResults();
                                                        for (int i=1; i<allResults.length; i++)
                                                        {
                                                            resultsArray.add(allResults[i]);
                                                        }


                                                        if (meth == 14)
                                                        {
                                                            offline = sceMethod.getOfflineExecutionTime();
                                                        }
                                                        else if (meth == 15)
                                                        {
                                                            sceMethod.setOfflineExecutionTime(offline);
                                                        }
                                                        pos++;
                                                    }

                                                    betaInt = 2;
                                                    if (!tryOtherMs)
                                                    {
                                                        betaInt = betaCBPCE;
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                int pos = 0;
                                                int emme = 2;
                                                int maxEmme = maxM;
                                                if (!tryOtherMs)
                                                {
                                                    emme = alphaEMPCE;
                                                    maxEmme = alphaEMPCE;
                                                }
                                                if (!tryOtherMs)
                                                {
                                                    results = new ProjectiveClustering[1];
                                                }
                                                else
                                                {
                                                    results = new ProjectiveClustering[maxM-1];
                                                }

                                                resultsArray = new ArrayList<ProjectiveClustering>(results.length);

                                                for (; emme<=maxEmme; emme++)
                                                {
                                                    //System.out.println("M="+emme);
                                                    ((EMlikeMethod)sceMethod).setM(emme);
                                                    results[pos] = sceMethod.execute(nClusters);

                                                    ProjectiveClustering[] allResults = sceMethod.getAllResults();
                                                    for (int i=1; i<allResults.length; i++)
                                                    {
                                                        resultsArray.add(allResults[i]);
                                                    }

                                                    if (meth == 14)
                                                    {
                                                        offline = sceMethod.getOfflineExecutionTime();
                                                    }
                                                    else if (meth == 15)
                                                    {
                                                        sceMethod.setOfflineExecutionTime(offline);
                                                    }
                                                    pos++;
                                                }
                                            }
                                        }
                                        else
                                        {
                                            results = new ProjectiveClustering[1];
                                            resultsArray = new ArrayList<ProjectiveClustering>(results.length);
                                            results[0] = sceMethod.execute(nClusters);

                                            ProjectiveClustering[] allResults = sceMethod.getAllResults();
                                            for (int i=1; i<allResults.length; i++)
                                            {
                                                resultsArray.add(allResults[i]);
                                            }

                                            if (meth == 14)
                                            {
                                                offline = sceMethod.getOfflineExecutionTime();
                                            }
                                            else if (meth == 15)
                                            {
                                                sceMethod.setOfflineExecutionTime(offline);
                                            }
                                        }
                                    }

                                    if (resultsArray != null && resultsArray.size() > 0)
                                    {
                                        ProjectiveClustering[] newResults = new ProjectiveClustering[results.length+resultsArray.size()];
                                        int i=0;
                                        for (; i<results.length; i++)
                                        {
                                            newResults[i] = results[i];
                                        }
                                        for (int j=0; j<resultsArray.size(); j++)
                                        {
                                            newResults[i] = resultsArray.get(j);
                                            i++;
                                        }

                                        results = newResults;
                                    }
                                }

                                if (needForPreviousResults && sceMethod instanceof MOEAClusteringBasedPCE_OPTIMIZED || sceMethod instanceof MOEAClusteringBasedPCE)
                                {
                                    previousResults[run-1] = results;
                                }

    //EVALUATIONS
                                for (int meas=0; meas<numberOfTestEvaluationMeasures; meas++)
                                {
                                    if (!(testEvaluationMeasures[meas] instanceof ReferencePartitionPCETestEvaluation) && !(testEvaluationMeasures[meas] instanceof EnsemblePCETestEvaluation))
                                    {
                                        testEvaluationMeasures[meas].addEvaluation(run, datasetCount, ens-1, z, ensemble, results, multiResultBehavior, sceMethod);
                                    }
                                    else if ((testEvaluationMeasures[meas] instanceof ReferencePartitionPCETestEvaluation && evaluationWrtReferenceClassification && refPartition != null && refPartition.getNumberOfClusters() > 1))
                                    {
                                        testEvaluationMeasures[meas].addEvaluation(run, datasetCount, ens-1, z, ensemble, results, multiResultBehavior, sceMethod);
                                    }
                                    else if(testEvaluationMeasures[meas] instanceof EnsemblePCETestEvaluation && evaluationWrtEnsembleSolutions)
                                    {
                                        testEvaluationMeasures[meas].addEvaluation(run, datasetCount, ens-1, z, ensemble, results, multiResultBehavior, sceMethod);
                                    }
                                }

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

                            if (sceMethod == null)
                            {
                                previousResults = null;
                            }

                            log.println(methodNames[meth]+" Algorithm---DONE");
                            log.println();
                            
                            if (saveConsensusClusterings)
                            {
                                cons.println(methodNames[meth]+" Algorithm---END");
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
            }
        }
        

        log.println("TEST COMPLETED SUCCESSFULLY!");
        System.out.println();
        System.out.println("TEST COMPLETED SUCCESSFULLY!");
        System.out.println();
    }
    
    private static void loadProperties()
    {
        Properties props = new Properties();
        try
        {
            props.load(new FileInputStream("parameters.properties"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
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
        
        
       
        String t = props.getProperty("do_ensemble_generation");
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
                validMethodNames[i] = methodsTmp.get(i);

                if (s.equals("MOEA-PCE"))
                {
                    validMethods[i] = 2;
                }
                else if (s.equals("EM-PCE"))
                {
                    validMethods[i] = 10;
                }
                else if (s.equals("CB-PCE"))
                {
                    validMethods[i] = 14;
                }
                else if (s.equals("FCB-PCE"))
                {
                    validMethods[i] = 16;
                }
                else if (s.equals("E-EM-PCE"))
                {
                    validMethods[i] = 7;
                }
                else if (s.equals("E-2S-PCE"))
                {
                    validMethods[i] = 4;
                }
                else
                {
                    throw new RuntimeException("ERROR: PCE algorithm unknown");
                }
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
    
}



























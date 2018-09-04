package evaluation.testevaluation;

import dataset.ProjectiveClusteringDataset;
import evaluation.clustering.objectsfeatures.F1ObjectsFeaturesProjectiveClusteringSim;
import evaluation.clustering.ProjectiveClusteringSimilarity;
import objects.Instance;
import objects.ProjectiveClustering;
import pce.PCEMethod;
import java.io.PrintStream;
import java.util.ArrayList;


public class ReferencePartitionPCETestEvaluation extends PCETestEvaluation
{
    protected ProjectiveClusteringSimilarity sim;

    public ReferencePartitionPCETestEvaluation(String[] datasetNames, String[] methodNames, int nEnsembles, int nRuns, String outputPath, PrintStream streamPartialResults, ProjectiveClusteringSimilarity sim, String name, boolean printCompleteCSV, boolean printSummaryCSV, boolean printSummaryCSV_extended)
    {
        super(datasetNames, methodNames, nEnsembles, nRuns, outputPath, streamPartialResults, printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);

        this.sim = sim;
        this.evaluationName = "Evaluation w.r.t. the reference classification---"+name;

        this.arrayListNames = new String[]{"FUZZY","HARD"};
        this.arrayListSize = this.arrayListNames.length;

        //this.referenceNames = new String[]{"AVG ENSEMBLE","MAX ENSEMBLE"};
        this.referenceNames = new String[]{"AVG ENSEMBLE","MAX ENSEMBLE"};
        this.nReferences = this.referenceNames.length;

        ref = new ArrayList[nDatasets][nEnsembles][nReferences];
        
        if (this.printCompleteCSV)
        {
            createCSV();
        }
        
        if (this.printSummaryCSV)
        {
            createSummaryCSV();
        }
    }

    protected void computeReferences(int datasetID, int ensembleID, ProjectiveClusteringDataset ensemble)
    {
        ProjectiveClustering refPartition = ensemble.getProjectiveRefPartition();
        ProjectiveClustering refPartitionHard = ensemble.getProjectiveRefPartitionHard();

        double meanF = 0.0;
        double meanH = 0.0;
        double maxF = Double.NEGATIVE_INFINITY;
        double maxH = Double.NEGATIVE_INFINITY;

        Instance[] library = ensemble.getData();
        ProjectiveClustering[] libraryHard = ensemble.getDataHard();
        
        for (int i=0; i<library.length; i++)
        {
            ProjectiveClustering solution = (ProjectiveClustering)library[i];
            ProjectiveClustering solutionHard = libraryHard[i];

            
            double valH = this.sim.getSimilarity(refPartitionHard, solutionHard);
            double valF = valH;
            if (!this.sim.onlyHard())
            {
                valF = this.sim.getSimilarity(refPartition, solution);
            }
            
            meanF += valF;
            meanH += valH;

            if (valF > maxF)
            {
                maxF = valF;
            }

            if (valH > maxH)
            {
                maxH = valH;
            }
        }
        meanF /= library.length;
        meanH /= library.length;

        ArrayList<Double> values1 = new ArrayList<Double>(2);
        values1.add(meanF);
        values1.add(meanH);

        ArrayList<Double> values2 = new ArrayList<Double>(2);
        values2.add(maxF);
        values2.add(maxH);

        this.ref[datasetID][ensembleID][0] = values1;
        this.ref[datasetID][ensembleID][1] = values2;
        
        //printPartialReferences(datasetID, ensembleID);
    }

    protected ArrayList<Double> evaluate(ProjectiveClusteringDataset ensemble, ProjectiveClustering[] results, int multiResultBehavior, PCEMethod method)
    {
        ArrayList<Double>[] multiResult = new ArrayList[results.length];

        /*
        if (this.sim instanceof F1ObjectsFeaturesProjectiveClusteringSim)
        {
            int g = 0;
        }
        */
        
        //body
        ProjectiveClustering refPartition = ensemble.getProjectiveRefPartition();
        ProjectiveClustering refPartitionHard = ensemble.getProjectiveRefPartitionHard();

        for (int i=0; i<results.length; i++)
        {
            ProjectiveClustering result = results[i];
            ProjectiveClustering resultHard = result.hardenizeObjectAndFeaturePartitioning();

            ArrayList<Double> values = new ArrayList<Double>(2);
            double valH = this.sim.getSimilarity(refPartitionHard, resultHard);
            double valF = valH;
            if (!this.sim.onlyHard())
            {
                valF = this.sim.getSimilarity(refPartition, result);
            }
            values.add(valF);
            values.add(valH);
            


            multiResult[i] = values;
        }
        
        if (multiResult.length == 1)
        {
            return multiResult[0];
        }
        
        return extractSingleResult(multiResult,multiResultBehavior);
    }

    protected void updateBestValues(ArrayList<Double> oldValues, ArrayList<Double> newValues)
    {
        updateBestValuesMax(oldValues, newValues);
    }
    
    protected double computeGain(double res, double ref)
    {
        return res-ref;
    }
}
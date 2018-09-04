package evaluation.testevaluation;

import dataset.ProjectiveClusteringDataset;
import evaluation.clustering.ProjectiveClusteringInternalValidityCriterion;
import evaluation.clustering.ProjectiveClusteringSimilarity;
import evaluation.clustering.ProjectiveClusteringWeightedIntraSimilarity;
import objects.Instance;
import objects.NumericalInstance;
import objects.centroid.ProjectiveCentroidComputation;
import objects.ProjectiveClustering;
import pce.PCEMethod;
import java.io.PrintStream;
import java.util.ArrayList;


public class InternalPCETestEvaluation extends PCETestEvaluation
{
    protected ProjectiveCentroidComputation scc;

    public InternalPCETestEvaluation(String[] datasetNames, String[] methodNames, int nEnsembles, int nRuns, String outputPath, PrintStream streamPartialResults, ProjectiveCentroidComputation scc, String name,boolean printCompleteCSV, boolean printSummaryCSV, boolean printSummaryCSV_extended)
    {
        super(datasetNames, methodNames, nEnsembles, nRuns, outputPath, streamPartialResults, printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);

        this.scc = scc;
        this.evaluationName = "Internal Evaluation---"+name;

        this.arrayListNames = new String[]{"FUZZY","HARD"};
        this.arrayListSize = this.arrayListNames.length;

        this.referenceNames = new String[]{"AVG ENSEMBLE","MIN ENSEMBLE","REF. PARTITION"};
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
        double meanF = 0.0;
        double meanH = 0.0;
        double minF = Double.POSITIVE_INFINITY;
        double minH = Double.POSITIVE_INFINITY;

        Instance[] library = ensemble.getData();
        for (int i=0; i<library.length; i++)
        {
            ProjectiveClustering solution = (ProjectiveClustering)library[i];
            ProjectiveClustering solutionHard = solution.hardenizeObjectAndFeaturePartitioning();

            NumericalInstance[] centr = solution.getNumericalInstanceCentroids(this.scc);
            double[][] weights = solution.getFeatureRepresentationMatrix();
            ProjectiveClusteringInternalValidityCriterion scInternalCrit = new ProjectiveClusteringWeightedIntraSimilarity(centr,weights);
            double valF = solution.internalClusteringEvaluation(scInternalCrit, null);

            NumericalInstance[] centrHard = solutionHard.getNumericalInstanceCentroids(this.scc);
            double[][] weightsHard = solutionHard.getFeatureRepresentationMatrix();
            ProjectiveClusteringInternalValidityCriterion scInternalCritHard = new ProjectiveClusteringWeightedIntraSimilarity(centrHard,weightsHard);
            double valH = solutionHard.internalClusteringEvaluation(scInternalCritHard, null);            

            meanF += valF;
            meanH += valH;

            if (valF < minF)
            {
                minF = valF;
            }

            if (valH < minH)
            {
                minH = valH;
            }
        }
        meanF /= library.length;
        meanH /= library.length;

        ArrayList<Double> values1 = new ArrayList<Double>(2);
        values1.add(meanF);
        values1.add(meanH);

        ArrayList<Double> values2 = new ArrayList<Double>(2);
        values2.add(minF);
        values2.add(minH);

        this.ref[datasetID][ensembleID][0] = values1;
        this.ref[datasetID][ensembleID][1] = values2;



        ProjectiveClustering refPartition = ensemble.getProjectiveRefPartition();
        ProjectiveClustering refPartitionHard = ensemble.getProjectiveRefPartitionHard();

        NumericalInstance[] centrRefPart = refPartition.getNumericalInstanceCentroids(this.scc);
        double[][] weightsRefPart = refPartition.getFeatureRepresentationMatrix();
        ProjectiveClusteringInternalValidityCriterion scInternalCritRefPart = new ProjectiveClusteringWeightedIntraSimilarity(centrRefPart,weightsRefPart);
        double refPartValue = refPartition.internalClusteringEvaluation(scInternalCritRefPart, null);

        NumericalInstance[] centrRefPartHard = refPartitionHard.getNumericalInstanceCentroids(this.scc);
        double[][] weightsRefPartHard = refPartitionHard.getFeatureRepresentationMatrix();
        ProjectiveClusteringInternalValidityCriterion scInternalCritRefPartHard = new ProjectiveClusteringWeightedIntraSimilarity(centrRefPartHard,weightsRefPartHard);
        double refPartValueHard = refPartitionHard.internalClusteringEvaluation(scInternalCritRefPartHard, null);

        ArrayList<Double> values3 = new ArrayList<Double>(2);
        values3.add(refPartValue);
        values3.add(refPartValueHard);

        this.ref[datasetID][ensembleID][2] = values3;
        
        
        //printPartialReferences(datasetID, ensembleID);
    }

    protected ArrayList<Double> evaluate(ProjectiveClusteringDataset ensemble, ProjectiveClustering[] results, int multiResultBehavior, PCEMethod method)
    {
        ArrayList<Double>[] multiResult = new ArrayList[results.length];

        //body
        for (int i=0; i<results.length; i++)
        {
            ProjectiveClustering result = results[i];
            ProjectiveClustering resultHard = result.hardenizeObjectAndFeaturePartitioning();

            NumericalInstance[] centroids = result.getNumericalInstanceCentroids(this.scc);
            NumericalInstance[] centroidsHard = resultHard.getNumericalInstanceCentroids(this.scc);

            double[][] weights = result.getFeatureRepresentationMatrix();
            double[][] weightsHard = resultHard.getFeatureRepresentationMatrix();

            ProjectiveClusteringInternalValidityCriterion scInternalCrit = new ProjectiveClusteringWeightedIntraSimilarity(centroids,weights);
            ProjectiveClusteringInternalValidityCriterion scInternalCritHard = new ProjectiveClusteringWeightedIntraSimilarity(centroidsHard,weightsHard);

            double value = result.internalClusteringEvaluation(scInternalCrit, null);
            double valueHard = resultHard.internalClusteringEvaluation(scInternalCritHard, null);

            ArrayList<Double> values = new ArrayList<Double>(2);
            values.add(value);
            values.add(valueHard);

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
        updateBestValuesMin(oldValues, newValues);
    }
    
    protected double computeGain(double res, double ref)
    {
        return res-ref;
    }
}

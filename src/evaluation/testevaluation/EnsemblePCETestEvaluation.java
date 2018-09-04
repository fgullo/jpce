package evaluation.testevaluation;

import dataset.ProjectiveClusteringDataset;
import evaluation.clustering.ProjectiveClusteringSimilarity;
import objects.Instance;
import objects.ProjectiveClustering;
import pce.PCEMethod;
import java.io.PrintStream;
import java.util.ArrayList;


public class EnsemblePCETestEvaluation extends PCETestEvaluation
{
    protected ProjectiveClusteringSimilarity sim;

    public EnsemblePCETestEvaluation(String[] datasetNames, String[] methodNames, int nEnsembles, int nRuns, String outputPath, PrintStream streamPartialResults, ProjectiveClusteringSimilarity sim, String name, boolean printCompleteCSV, boolean printSummaryCSV, boolean printSummaryCSV_extended)
    {
        super(datasetNames, methodNames, nEnsembles, nRuns, outputPath, streamPartialResults, printCompleteCSV, printSummaryCSV, printSummaryCSV_extended);

        this.sim = sim;
        this.evaluationName = "Evaluation w.r.t. the ensemble solutions---"+name;

        this.arrayListNames = new String[]{"FUZZY","HARD"};
        this.arrayListSize = this.arrayListNames.length;

        //this.referenceNames = new String[]{"Ensemble AVG intra-sim","Ensemble MAX intra-sim"};
        this.referenceNames = new String[]{"Ensemble AVG intra-sim","Ensemble MAX intra-sim"};
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
        Instance[] library = ensemble.getData();
        Instance[] libraryHard = ensemble.getDataHard();
        
        double fuzzySim = 0.0;
        double hardSim = 0.0;
        double fuzzySimMax = Double.NEGATIVE_INFINITY;
        double hardSimMax = Double.NEGATIVE_INFINITY; 
        for (int i=0; i<library.length; i++)
        {
            ProjectiveClustering sc1Fuzzy = (ProjectiveClustering)library[i];
            ProjectiveClustering sc1Hard = (ProjectiveClustering)libraryHard[i];
            double meanFuzzy = 0.0;
            double meanHard = 0.0;
            for (int j=0; j<library.length; j++)
            {
                if (j != i)
                {
                    
                    ProjectiveClustering sc2Hard = (ProjectiveClustering)libraryHard[j];
                    double valueHard = this.sim.getSimilarity(sc1Hard, sc2Hard);
                    hardSim += valueHard;
                    meanHard += valueHard;

                    if (!this.sim.onlyHard())
                    {
                        ProjectiveClustering sc2Fuzzy = (ProjectiveClustering)library[j];
                        double valueFuzzy = this.sim.getSimilarity(sc1Fuzzy, sc2Fuzzy);
                        fuzzySim += valueFuzzy;
                        meanFuzzy += valueFuzzy;
                    }
                    else
                    {
                        fuzzySim += valueHard;
                        meanFuzzy += valueHard;
                    }        
                }
            }
            meanHard /= (library.length-1);
            meanFuzzy /= (library.length-1);
            
            if (meanHard > hardSimMax)
            {
                hardSimMax = meanHard;
            }
            
            if (meanFuzzy > fuzzySimMax)
            {
                fuzzySimMax = meanFuzzy;
            }
        }
        
        fuzzySim /= library.length*(library.length-1);
        hardSim /= library.length*(library.length-1);
        
        ArrayList<Double> array = new ArrayList<Double>(2);
        array.add(fuzzySim);
        array.add(hardSim);
        this.ref[datasetID][ensembleID][0] = array;
        
        ArrayList<Double> array2 = new ArrayList<Double>(2);
        array2.add(fuzzySimMax);
        array2.add(hardSimMax);
        this.ref[datasetID][ensembleID][1] = array2;
    }

    
    protected ArrayList<Double> evaluate(ProjectiveClusteringDataset ensemble, ProjectiveClustering[] results, int multiResultBehavior, PCEMethod method)
    {
        ArrayList<Double>[] multiResult = new ArrayList[results.length];
        Instance[] library = ensemble.getData();
        ProjectiveClustering[] libraryHard = ensemble.getDataHard();
        
        double[] meanF = new double[results.length];
        double[] meanH = new double[results.length];
        
        //body
        for (int i=0; i<library.length; i++)
        {
            ProjectiveClustering solution = (ProjectiveClustering)library[i];
            ProjectiveClustering solutionHard = libraryHard[i];
            
            for (int j=0; j<results.length; j++)
            {
                ProjectiveClustering result = results[j];
                ProjectiveClustering resultHard = result.hardenizeObjectAndFeaturePartitioning();
                
                double valH = this.sim.getSimilarity(solutionHard, resultHard);
                double valF = valH;
                if (!this.sim.onlyHard())
                {
                    valF = this.sim.getSimilarity(solution, result);
                }

                meanF[j] += valF;
                meanH[j] += valH;
            }
        }
        
        for (int j=0; j<results.length; j++)
        {
            ArrayList<Double> values = new ArrayList<Double>(2);
            values.add(meanF[j]/library.length);
            values.add(meanH[j]/library.length);
            
            multiResult[j] = values;
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
    
    protected  double computeGain(double res, double ref)
    {
        if (ref == 0)
        {
            return res;
        }
        
        return res/ref;
    }
}


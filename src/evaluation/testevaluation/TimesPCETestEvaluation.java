package evaluation.testevaluation;

import dataset.ProjectiveClusteringDataset;
import objects.ProjectiveClustering;
import pce.PCEMethod;
import java.io.PrintStream;
import java.util.ArrayList;


public class TimesPCETestEvaluation extends PCETestEvaluation
{

    public TimesPCETestEvaluation(String[] datasetNames, String[] methodNames, int nEnsembles, int nRuns, String outputPath, PrintStream streamPartialResults, boolean onlyAVGtimes)
    {
        super(datasetNames, methodNames, nEnsembles, nRuns, outputPath, streamPartialResults, true, false, false);

        this.evaluationName = "Execution times (milliseconds)";

        this.arrayListNames = new String[]{"OFFLINE","ONLINE","TOT"};
        this.arrayListSize = this.arrayListNames.length;

        this.referenceNames = new String[0];
        this.nReferences = this.referenceNames.length;

        ref = new ArrayList[nDatasets][nEnsembles][nReferences];
        
        if (onlyAVGtimes)
        {
            this.best = null;
            this.std = null;
        }
        
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
        
    }

    protected ArrayList<Double> evaluate(ProjectiveClusteringDataset ensemble, ProjectiveClustering[] results, int multiResultBehavior, PCEMethod method)
    {
        ArrayList<Double> values = new ArrayList<Double>(3);
        
        double offlineMs = (method==null)?0.0:Math.rint(method.getOfflineExecutionTime());
        double onlineMs = (method==null)?0.0:Math.rint(method.getOnlineExecutionTime());
        double totMs = offlineMs + onlineMs;
        
        values.add(offlineMs);
        values.add(onlineMs);
        values.add(totMs);
        
        return values;
    }

    protected void updateBestValues(ArrayList<Double> oldValues, ArrayList<Double> newValues)
    {
        updateBestValuesMin(oldValues, newValues);
    }
    
    protected static String readableTime(double time)
    {
        String msecs = "";
        String secs = "";
        String mins = "";
        String hours = "";

        long seconds = (long)time/1000;
        msecs += time-seconds*1000;
        long minutes = seconds/60;
        secs += seconds-minutes*60;
        long hoursL = minutes/60;
        mins += minutes-hoursL*60;
        hours += hoursL;

        return hours+"h "+mins+"m "+secs+"s "+msecs+"ms";
    }
    
    protected double computeGain(double res, double ref)
    {
        return res-ref;
    }
}



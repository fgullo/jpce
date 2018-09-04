package evaluation.testevaluation;

import dataset.ProjectiveClusteringDataset;
import objects.ProjectiveClustering;
import pce.PCEMethod;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;


public abstract class PCETestEvaluation
{
    public static final int MULTI_RESULT_BEHAVIOR_AVG = 0;
    public static final int MULTI_RESULT_BEHAVIOR_MIN = 1;
    public static final int MULTI_RESULT_BEHAVIOR_MAX = 2;
    public static final int MULTI_RESULT_BEHAVIOR_AVGMAX = 3;
    
    protected boolean printCompleteCSV;
    protected boolean printSummaryCSV;
    protected boolean printSummaryCSV_extended;

    protected int nRuns;
    protected int nEnsembles;
    protected int nMethods;
    protected int nDatasets;
    protected int nReferences;
    protected int arrayListSize;

    protected String evaluationName;
    protected String[] datasetNames;
    protected String[] methodNames;
    protected String[] referenceNames;
    protected String[] arrayListNames;

    protected String outputPath;
    protected PrintStream streamPartialResults;
    protected PrintStream streamCSV;
    protected PrintStream streamSummaryCSV;

    protected ArrayList<Double>[][][] avg;
    //protected ArrayList<Boolean>[][][] avgAllEquals;
    protected ArrayList<Double>[][][] best;
    protected ArrayList<Double>[][][] std;

    protected ArrayList<Double>[][][] ref;


    public PCETestEvaluation(String[] datasetNames, String[] methodNames, int nEnsembles, int nRuns, String outputPath, PrintStream streamPartialResults, boolean printCompleteCSV, boolean printSummaryCSV, boolean printSummaryCSV_extended)
    {
        this.printCompleteCSV = printCompleteCSV;
        this.printSummaryCSV = printSummaryCSV;
        this.printSummaryCSV_extended = printSummaryCSV_extended;
        
        this.datasetNames = datasetNames;
        this.nDatasets = datasetNames.length;

        this.methodNames = methodNames;
        this.nMethods = methodNames.length;

        this.nEnsembles = nEnsembles;
        this.nRuns = nRuns;

        this.outputPath = outputPath;
        this.streamPartialResults = streamPartialResults;

        avg = new ArrayList[nDatasets][nEnsembles][nMethods];
        best = new ArrayList[nDatasets][nEnsembles][nMethods];
        std = new ArrayList[nDatasets][nEnsembles][nMethods];
        
        //avgAllEquals = new ArrayList[nDatasets][nEnsembles][nMethods];
        
        /*
        for (int i=0; i<nDatasets; i++)
        {
            for (int j=0; j<nEnsembles; j++)
            {
                for (int k=0; k<nMethods; k++)
                {
                    ArrayList<Boolean> v = new ArrayList<Boolean>(this.arrayListSize);
                    for (int l=0; l<this.arrayListSize; l++)
                    {
                        v.add(new Boolean(true));
                    }
                }
            }
        }
        */

    }

    public void addEvaluation(int run, int datasetID, int ensembleID, int methodID, ProjectiveClusteringDataset ensemble, ProjectiveClustering[] results, int multiResultBehavior, PCEMethod method)
    {
        if (this.nReferences > 0 && this.ref[datasetID][ensembleID][0] == null)
        {
            computeReferences(datasetID, ensembleID, ensemble);
            
            if (this.streamPartialResults != null)
            {
                printPartialReferences(datasetID, ensembleID);
            }
        }

        ArrayList<Double> doubleValues = evaluate(ensemble, results, multiResultBehavior, method);

        if (run == 1)
        {
            /*
            this.streamPartialResults.println();
            this.streamPartialResults.println("##########---"+this.methodNames[methodID]+"---##########");
            this.streamPartialResults.println();
            */
            
            if (this.avg != null)
            {
                ArrayList<Double> valuesAvg = new ArrayList<Double>(doubleValues.size());
                for (int i=0; i<doubleValues.size(); i++)
                {
                    valuesAvg.add(doubleValues.get(i).doubleValue()/this.nRuns);
                }
                this.avg[datasetID][ensembleID][methodID] = valuesAvg;
            }
            
            ArrayList<Boolean> valuesAvgBool = new ArrayList<Boolean>(doubleValues.size());
            for (int i=0; i<doubleValues.size(); i++)
            {
                valuesAvgBool.add(new Boolean(true));
            }
            //this.avgAllEquals[datasetID][ensembleID][methodID] = valuesAvgBool;

            
            if (this.best != null)
            {
                ArrayList<Double> valuesBest = new ArrayList<Double>(doubleValues.size());
                for (int i=0; i<doubleValues.size(); i++)
                {
                    valuesBest.add(doubleValues.get(i).doubleValue());
                }
                this.best[datasetID][ensembleID][methodID] = valuesBest;
            }
            
            if (this.std != null)
            {            
                ArrayList<Double> valuesStd = new ArrayList<Double>(doubleValues.size());
                for (int i=0; i<doubleValues.size(); i++)
                {
                    valuesStd.add(doubleValues.get(i).doubleValue()*doubleValues.get(i).doubleValue());
                }
                this.std[datasetID][ensembleID][methodID] = valuesStd;
            }
        }
        else
        {
            ArrayList<Double> valuesAvg = null;
            //ArrayList<Boolean> valuesAvgBool = this.avgAllEquals[datasetID][ensembleID][methodID];
            ArrayList<Double> valuesBest = null;
            ArrayList<Double> valuesStd = null;
            
            if (this.avg != null){valuesAvg = this.avg[datasetID][ensembleID][methodID];}
            //ArrayList<Boolean> valuesAvgBool = this.avgAllEquals[datasetID][ensembleID][methodID];
            if (this.best != null){valuesBest = this.best[datasetID][ensembleID][methodID];}
            if (this.std != null){valuesStd = this.std[datasetID][ensembleID][methodID];}
            
            

            for (int i=0; i<doubleValues.size(); i++)
            {
                if (this.avg != null)
                {
                    double oldValueAvg = valuesAvg.get(i);
                    double newValueAvg = oldValueAvg+doubleValues.get(i)/this.nRuns;
                    valuesAvg.set(i, newValueAvg);
                }
                
                /*
                if (Math.abs(oldValueAvg*this.nRuns/(run-1)-doubleValues.get(i))>=0.0000000001)
                {
                    valuesAvgBool.set(i, new Boolean(false));
                }
                */ 

                if (this.std != null)
                {
                    double newValueStd = valuesStd.get(i)+doubleValues.get(i)*doubleValues.get(i);
                    valuesStd.set(i, newValueStd);
                }
            }

            if (this.best != null)
            {
                updateBestValues(valuesBest, doubleValues);
            }

            if (run == this.nRuns)
            {
                if (this.std != null)
                {
                    for (int i=0; i<valuesStd.size(); i++)
                    {
                        double tmp = valuesStd.get(i)/this.nRuns-valuesAvg.get(i)*valuesAvg.get(i);
                        double newStdValue = Math.sqrt(tmp);
                        if (tmp <0.0 && tmp > -0.000000001)
                        {
                            newStdValue = 0.0;
                        }

                        valuesStd.set(i, newStdValue);
                    }
                }
                
                if (this.streamPartialResults != null)
                {
                    printPartialResults(datasetID, ensembleID, methodID);
                }
            }
        }

        if (run == this.nRuns && ensembleID == this.nEnsembles-1 && methodID == this.nMethods-1)
        {
                updateCSV(datasetID);
        }
    }

    protected abstract void computeReferences(int datasetID, int ensembleID, ProjectiveClusteringDataset ensemble);

    protected abstract ArrayList<Double> evaluate(ProjectiveClusteringDataset ensemble, ProjectiveClustering[] results, int multiResultBehavior, PCEMethod method);

    protected ArrayList<Double> extractSingleResult(ArrayList<Double>[] multiResult,int multiResultBehavior)
    {
        ArrayList<Double> singleResult = new ArrayList<Double>();
        for (int i=0; i<multiResult[0].size(); i++)
        {
            if (multiResultBehavior == PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVG)
            {
                double sum = 0;
                for (int j=0; j<multiResult.length; j++)
                {
                    sum += multiResult[j].get(i);
                }
                sum /= multiResult.length;
                singleResult.add(sum);
            }
            else if (multiResultBehavior == PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MIN)
            {
                double min = multiResult[0].get(i);
                for (int j=1; j<multiResult.length; j++)
                {
                    if (multiResult[j].get(i)<min)
                    {
                        min = multiResult[j].get(i);
                    }
                }
                singleResult.add(min);
            }
            else if (multiResultBehavior == PCETestEvaluation.MULTI_RESULT_BEHAVIOR_MAX)
            {
                double max = multiResult[0].get(i);
                //int imax = 0;
                for (int j=1; j<multiResult.length; j++)
                {
                    if (multiResult[j].get(i)>max)
                    {
                        max = multiResult[j].get(i);
                        //imax = j;
                    }
                }
                singleResult.add(max);
                //this.streamPartialResults.println("#####################################################################################---> MAX="+(imax+2));
            }
            else if (multiResultBehavior == PCETestEvaluation.MULTI_RESULT_BEHAVIOR_AVGMAX)
            {
                double max = multiResult[0].get(i);
                double sum = 0;
                for (int j=1; j<multiResult.length; j++)
                {
                    sum += multiResult[j].get(i);
                    if (multiResult[j].get(i)>max)
                    {
                        max = multiResult[j].get(i);
                        //imax = j;
                    }
                }
                sum /= multiResult.length;
                singleResult.add((sum+max)/2);
            }

        }

        return singleResult;
    }


    protected abstract void updateBestValues(ArrayList<Double> oldValues, ArrayList<Double> newValues);

    protected void updateBestValuesMin(ArrayList<Double> oldValues, ArrayList<Double> newValues)
    {
        for (int i=0; i<oldValues.size(); i++)
        {
            double valOld = oldValues.get(i).doubleValue();
            double valNew = newValues.get(i).doubleValue();

            if (valNew < valOld)
            {
                oldValues.set(i,valNew);
            }
        }
    }

    protected void updateBestValuesMax(ArrayList<Double> oldValues, ArrayList<Double> newValues)
    {
        for (int i=0; i<oldValues.size(); i++)
        {
            double valOld = oldValues.get(i).doubleValue();
            double valNew = newValues.get(i).doubleValue();

            if (valNew > valOld)
            {
                oldValues.set(i,valNew);
            }
        }
    }

    protected static double approximate(double d)
    {
        int nDecimalDigits = 6;

        double multiplier = Math.pow(10, nDecimalDigits);

        double trunc = Math.rint(d*multiplier);

        return trunc/multiplier;
    }

    protected void printPartialResults(int dataset, int ensemble, int method)
    {
        this.streamPartialResults.println("------------------------------------------------------------------------------------------------------------------");
        this.streamPartialResults.println("--- RESULTS --- "+this.evaluationName);

        for (int i=0; i<this.arrayListNames.length; i++)
        {
             this.streamPartialResults.println("------------------------------------------------------------------------------------------------------------------");
             this.streamPartialResults.print(this.arrayListNames[i]+"----->  ");
             if (this.avg != null){this.streamPartialResults.print("AVG="+approximate(this.avg[dataset][ensemble][method].get(i))+"     ");}
             if (this.best != null){this.streamPartialResults.print("BEST="+approximate(this.best[dataset][ensemble][method].get(i))+" ");}
             if (this.std != null){this.streamPartialResults.print("STD="+approximate(this.std[dataset][ensemble][method].get(i)));}
             
             /*
             if (this.avgAllEquals[dataset][ensemble][method].get(i))
             {
                 this.streamPartialResults.print("   [ALL EQUAL RESULTS]");
             }
             */
             
             this.streamPartialResults.println();
             for (int j=0; j<this.nReferences; j++)
             {
                if (this.avg != null){this.streamPartialResults.print("GAIN wrt "+this.referenceNames[j]+" = "+approximate(this.avg[dataset][ensemble][method].get(i)-this.ref[dataset][ensemble][j].get(i))+"          ");}
             }
             this.streamPartialResults.println();
        }

        this.streamPartialResults.println("------------------------------------------------------------------------------------------------------------------");
    }

    protected void printPartialReferences(int dataset, int ensemble)
    {
        this.streamPartialResults.println("------------------------------------------------------------------------------------------------------------------");
        this.streamPartialResults.println("--- REFERENCES --- "+this.evaluationName);

        for (int i=0; i<this.referenceNames.length; i++)
        {
            this.streamPartialResults.println("------------------------------------------------------------------------------------------------------------------");
            this.streamPartialResults.println(this.referenceNames[i]);
            for (int j=0; j<this.arrayListSize; j++)
            {
                this.streamPartialResults.print(this.arrayListNames[j]+"="+approximate(this.ref[dataset][ensemble][i].get(j))+"     ");
            }
            this.streamPartialResults.println();
        }

        this.streamPartialResults.println("------------------------------------------------------------------------------------------------------------------");
        
    }

    protected void createCSV()
    {
        File fileCSV = null;
        FileOutputStream fosCSV = null;
        try
        {
                fileCSV = File.createTempFile(this.evaluationName+"---", ".csv", new File(this.outputPath));
                fosCSV = new FileOutputStream(fileCSV, true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        this.streamCSV = new PrintStream(fosCSV);

        printHeadingCSV();
    }
    
    protected void createSummaryCSV()
    {
        File fileCSV = null;
        FileOutputStream fosCSV = null;
        try
        {
                fileCSV = File.createTempFile(this.evaluationName+" (summary)---", ".csv", new File(this.outputPath));
                fosCSV = new FileOutputStream(fileCSV, true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        this.streamSummaryCSV = new PrintStream(fosCSV);

        printHeadingSummaryCSV();
    }
    
    protected void printHeadingCSV()
    {
        int h = 0;
        if (this.best != null){h++;}
        if (this.avg != null){h++;}
        if (this.std != null){h++;}
        
        
        
        String filler = "";
        for (int i=0; i<this.arrayListSize-1; i++)
        {
            filler += " ;";
        }
        
        String firstRow = "DATASET;";
        String secondRow = " ;";
        if (this.nReferences > 0)
        {
            firstRow += "REFERENCES;"+filler;
            secondRow += this.referenceNames[0]+";"+filler;            
        }
        String thirdRow = " ;";
        
        for (int i=1; i<this.referenceNames.length; i++)
        {
            firstRow += " ;"+filler;
            secondRow += this.referenceNames[i]+";"+filler;
        }
        for (int i=0; i<this.methodNames.length; i++)
        {
            if (h >= 1){firstRow += this.methodNames[i]+";"+filler;}
            for (int x=2; x<=h; x++)
            {
                firstRow += " ;"+filler;
            }
            if (this.best != null){secondRow += "BEST;"+filler;}
            if (this.avg != null){secondRow += "AVG;"+filler;}
            if (this.std != null){secondRow += "STD;"+filler;}

            if (this.avg != null)
            {
                for (int j=0; j<this.referenceNames.length; j++)
                {
                    firstRow += " ;"+filler;
                    secondRow += "GAIN wrt "+this.referenceNames[j]+";"+filler;
                }
            }
        }
        
        int x = 0;
        if (this.best != null){x++;}
        if (this.avg != null){x++;}
        if (this.std != null){x++;}
        
        int y = (this.avg != null)?this.nReferences:0;
        
        int thirdRowSize = this.nReferences+this.nMethods*(x+y);
        for (int i=1; i<=thirdRowSize; i++)
        {
            for (int j=0; j<this.arrayListNames.length; j++)
            {
                thirdRow += this.arrayListNames[j]+";";
            }
        }
        
        //add summary
        if (this.avg != null && this.nReferences > 0)
        {
            firstRow += "GAIN SUMMARY;";
            secondRow += " ;";
            thirdRow += " ;";

            for (int i=0; i<this.nReferences; i++)
            {
                for (int j=0; j<this.arrayListSize; j++)
                {
                    for (int k=0; k<this.nMethods; k++)
                    {
                        thirdRow += this.methodNames[k]+";";
                        if (k == 0)
                        {
                            secondRow += this.arrayListNames[j]+";";
                        }
                        else
                        {
                            secondRow += " ;";
                        }

                        if (k == 0 && j == 0)
                        {
                            firstRow += this.referenceNames[i]+";";
                        }
                        else
                        {
                            firstRow += " ;";
                        }
                    }
                }
            }
        }
        

        this.streamCSV.println(firstRow);
        this.streamCSV.println(secondRow);
        this.streamCSV.println(thirdRow);
    }

    
    protected void printHeadingSummaryCSV()
    {
        String firstRow = this.evaluationName+";";
        String secondRow = "DATASET;";
        String thirdRow = ";";
        if (this.printSummaryCSV_extended)
        {
            for (int j=0; j<this.arrayListNames.length; j++)
            {
                secondRow += arrayListNames[j]+";";
                thirdRow += referenceNames[0]+";";
                for (int i=1; i<this.referenceNames.length; i++)
                {
                    secondRow +=";";
                    thirdRow += referenceNames[i]+";";
                }
                for (int i=0; i<this.methodNames.length; i++)
                {
                    secondRow +=";";
                    thirdRow += methodNames[i]+";";
                }
            }
        }
        else
        {
            for (int i=0; i<methodNames.length; i++)
            {
                firstRow += " ;";
                secondRow += methodNames[i]+";";
            }
        }

        this.streamSummaryCSV.println(firstRow);
        this.streamSummaryCSV.println(secondRow);
        
        if (this.printSummaryCSV_extended)
        {
            this.streamSummaryCSV.println(thirdRow);
        }
    }
    
    
    protected void updateCSV(int datasetID)
    {
        String newRow = this.datasetNames[datasetID]+";";

        //add references
        double[][] meanReferences = new double[this.nReferences][this.arrayListSize];
        for (int i=0; i<this.nReferences; i++)
        {
            for (int j=0; j<this.arrayListSize; j++)
            {
                double mean = 0.0;
                for (int k=0; k<this.nEnsembles; k++)
                {
                    mean += this.ref[datasetID][k][i].get(j);
                }
                mean /= this.nEnsembles;

                meanReferences[i][j] = approximate(mean);
                
                newRow += meanReferences[i][j]+";";
            }
        }

        //add method results
        double[][] meanAVG = new double[this.nMethods][this.arrayListSize];
        for (int i=0; i<this.nMethods; i++)
        {
            if (this.best != null)
            {
                //add BEST
                for (int j=0; j<this.arrayListSize; j++)
                {
                    double mean = 0.0;
                    for (int k=0; k<this.nEnsembles; k++)
                    {
                        mean += this.best[datasetID][k][i].get(j);
                    }
                    mean /= this.nEnsembles;

                    newRow += approximate(mean)+";";
                }
            }

            if (this.avg != null)
            {
                //add AVG
                for (int j=0; j<this.arrayListSize; j++)
                {
                    double mean = 0.0;
                    for (int k=0; k<this.nEnsembles; k++)
                    {
                        mean += this.avg[datasetID][k][i].get(j);
                    }
                    mean /= this.nEnsembles;
                    meanAVG[i][j] = approximate(mean);

                    newRow += meanAVG[i][j]+";";
                }
            }

            if (this.std != null)
            {
                //add STD
                for (int j=0; j<this.arrayListSize; j++)
                {
                    double mean = 0.0;
                    for (int k=0; k<this.nEnsembles; k++)
                    {
                        mean += this.std[datasetID][k][i].get(j);
                    }
                    mean /= this.nEnsembles;

                    newRow += approximate(mean)+";";
                }
            }

            if (this.avg != null)
            {
                //add GAINS
                for (int k=0; k<this.nReferences; k++)
                {
                    for (int j=0; j<this.arrayListSize; j++)
                    {
                        newRow += approximate(meanAVG[i][j]-meanReferences[k][j])+";";
                    }
                }
            }
        }
        
        if (this.avg != null)
        {
            //add summary
            newRow += " ;";

            for (int i=0; i<this.nReferences; i++)
            {
                for (int j=0; j<this.arrayListSize; j++)
                {
                    for (int k=0; k<this.nMethods; k++)
                    {
                        //newRow += approximate(meanAVG[k][j]-meanReferences[i][j])+";";
                        newRow += approximate(computeGain(meanAVG[k][j],meanReferences[i][j]))+";";
                    }
                }
            }
        }

        if (this.printCompleteCSV)
        {
            this.streamCSV.println(newRow);
        }
        
        if (this.printSummaryCSV)
        {
            updateSummaryCSV(datasetID,meanAVG,meanReferences);
        }
    }
    
    
    
    protected void updateSummaryCSV(int datasetID,double[][] meanAVG, double[][] meanReferences)
    {
        String newRow = this.datasetNames[datasetID]+";";
       
        if (printSummaryCSV_extended)
        {
            for (int j=0; j<this.arrayListSize; j++)
            {
                for (int i=0; i<meanReferences.length; i++)
                {
                    newRow += meanReferences[i][j]+";";
                }
                
                for (int k=0; k<this.nMethods; k++)
                {
                    newRow += meanAVG[k][j]+";";
                }
            }            
        }
        else
        {
            for (int k=0; k<this.nMethods; k++)
            {
                double max = Double.NEGATIVE_INFINITY;
                for (int j=0; j<this.arrayListSize; j++)
                {
                    double x = approximate(meanAVG[k][j]);
                    //double x = approximate(computeGain(meanAVG[k][j],meanReferences[0][j]));
                    if (x > max)
                    {
                        max = x;
                    }
                }
                newRow += max+";";
            }
        }

        this.streamSummaryCSV.println(newRow);
    }
    
    
    
    protected abstract double computeGain(double res, double ref);
    
    /*
    public ArrayList<Boolean>[][][] getAvgAllEquals() {
        return this.avgAllEquals;
    }
    */

    public int getArrayListSize() {
        return arrayListSize;
    }

    public String[] getDatasetNames() {
        return datasetNames;
    }

    public void setDatasetNames(String[] datasetNames) {
        this.datasetNames = datasetNames;
    }

    public String getEvaluationName() {
        return evaluationName;
    }

    public String[] getMethodNames() {
        return methodNames;
    }

    public int getnDatasets() {
        return nDatasets;
    }

    public int getnEnsembles() {
        return nEnsembles;
    }

    public int getnMethods() {
        return nMethods;
    }

    public int getnReferences() {
        return nReferences;
    }

    public int getnRuns() {
        return nRuns;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String[] getReferenceNames() {
        return referenceNames;
    }

    /*
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setnRuns(int nRuns) {
        this.nRuns = nRuns;
    }

    public void setnReferences(int nReferences) {
        this.nReferences = nReferences;
    }

    public void setnMethods(int nMethods) {
        this.nMethods = nMethods;
    }

    public void setnEnsembles(int nEnsembles) {
        this.nEnsembles = nEnsembles;
    }

    public void setMethodNames(String[] methodNames) {
        this.methodNames = methodNames;
    }

    public void setArrayListSize(int arrayListSize) {
        this.arrayListSize = arrayListSize;
    }

    public void setnDatasets(int nDatasets) {
        this.nDatasets = nDatasets;
    }

    public void setEvaluationName(String evaluationName) {
        this.evaluationName = evaluationName;
    }

    public void setReferenceNames(String[] referenceNames) {
        this.referenceNames = referenceNames;
    }
    */


}
























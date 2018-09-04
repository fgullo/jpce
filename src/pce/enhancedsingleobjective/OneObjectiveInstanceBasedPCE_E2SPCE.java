/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce.enhancedsingleobjective;

import clustering.FuzzyKMedoids;
import dataset.ProjectiveClusteringDataset;
import evaluation.numericalinstance.NumericalInstanceSimilarity;
import evaluation.pdf.PDFSimilarity;
import objects.FuzzyCluster;
import objects.FuzzyClustering;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.InstanceBasedPCEMethod;


public class OneObjectiveInstanceBasedPCE_E2SPCE extends InstanceBasedPCEMethod
{
    protected long offlineExecutionTime = 0;
    protected long onlineExecutionTime = 0;

    //protected double[][] pairwiseObjectDistances;
    protected double[][] coNonOccurrenceObjectMatrix;
    protected double[][] Cli;
    
    protected PDFSimilarity pdfSim;
    //protected NumericalInstanceSimilarity numSim;
    
    protected ProjectiveClustering result;
    
    public OneObjectiveInstanceBasedPCE_E2SPCE (ProjectiveClusteringDataset ensemble, PDFSimilarity pdfSim)
    {
        this.ensemble = ensemble;
        this.pdfSim = pdfSim;
        //this.numSim = numSim;
        
        //System.out.println("E-2S-PCE---Starting computing pair-wise distances matrix");
        long start = System.currentTimeMillis();
        this.coNonOccurrenceObjectMatrix = this.ensemble.getCoNonOccurrenceObjectMatrix(this.pdfSim);
        computeCli();
        //this.pairwiseObjectDistances = this.ensemble.getPairwiseObjectDistancesBasedOnTheirFeatureRepresentations(numSim);
        long stop = System.currentTimeMillis();
        //System.out.println("E-2S-PCE---Computing pair-wise distances matrix terminated");

        this.offlineExecutionTime = stop-start;
    }
    
    public ProjectiveClustering execute (int nClusters)
    {
        //double[][] distMatrix = computeCumulativeDistanceMatrix();
        
        long start = System.currentTimeMillis();
        FuzzyKMedoids fkm = new FuzzyKMedoids(ensemble.getInstancesDataset());
        //FuzzyClustering fkmResult = fkm.execute(distMatrix, nClusters);
        FuzzyClustering fkmResult = fkm.execute(this.coNonOccurrenceObjectMatrix, nClusters);
        
        this.result = assignFeatures(fkmResult);
        long stop = System.currentTimeMillis();

        this.onlineExecutionTime = stop-start;

        return this.result;
    }
    
    /*
    protected double[][] computeCumulativeDistanceMatrix()
    {
        double[][] m = new double[this.ensemble.getNumberOfInstances()][this.ensemble.getNumberOfInstances()];
        
        double max1 = Double.NEGATIVE_INFINITY;
        double max2 = Double.NEGATIVE_INFINITY;
        double min1 = Double.POSITIVE_INFINITY;
        double min2 = Double.POSITIVE_INFINITY;
        
        for (int i=0; i<this.coNonOccurrenceObjectMatrix.length-1; i++)
        {
            for (int j=i+1; j<this.coNonOccurrenceObjectMatrix[i].length; j++)
            {
                if (this.coNonOccurrenceObjectMatrix[i][j] > max1)
                {
                    max1 = this.coNonOccurrenceObjectMatrix[i][j];
                }
                if (this.coNonOccurrenceObjectMatrix[i][j] < min1)
                {
                    min1 = this.coNonOccurrenceObjectMatrix[i][j];
                }
                
                if (this.pairwiseObjectDistances[i][j] > max2)
                {
                    max2 = this.pairwiseObjectDistances[i][j];
                }
                if (this.pairwiseObjectDistances[i][j] < min2)
                {
                    min2 = this.pairwiseObjectDistances[i][j];
                } 
            }
        }
        
        for (int i=0; i<m.length-1; i++)
        {
            for (int j=i+1; j<m[i].length; j++)
            {
                //m[i][j] = 0.5*((this.coNonOccurrenceObjectMatrix[i][j]/max1)+(this.pairwiseObjectDistances[i][j]/max2));
                double val1 = 0.0;
                if (max1 > min1)
                {
                    val1 = (this.coNonOccurrenceObjectMatrix[i][j]-min1)/(max1-min1);
                }
                double val2 = 0.0;
                if (max2 > min2)
                {
                    val2 = (this.pairwiseObjectDistances[i][j]-min2)/(max2-min2);
                }
                m[i][j] = 0.5*(val1+val2);
                m[j][i] = m[i][j];
                
                if (Double.isInfinite(m[i][j]) || Double.isNaN(m[i][j]))
                {
                    throw new RuntimeException("ERROR: m[i][j] is INFINITY or NAN");
                }
                
                if (m[i][j] < -0.00001 | m[i][j] > 1.00001)
                {
                    throw new RuntimeException("ERROR: m[i][j] must be within [0,1]");
                }
            }
            
            
        }
        
        return m;
    }
    */ 
    
    protected ProjectiveClustering assignFeatures(FuzzyClustering fkmResult)
    {
        FuzzyCluster[] fuzzyClusters = fkmResult.getClusters();
        ProjectiveCluster[] subspaceClusters = new ProjectiveCluster[fuzzyClusters.length];
        Double[][] featureAssignments = new Double[subspaceClusters.length][this.ensemble.getNumberOfFeaturesInEachCluster()];
        
        for (int k=0; k<featureAssignments.length; k++)
        {
            Double[] objectRep = fuzzyClusters[k].getFeatureVectorRepresentationDouble();
            double sumCheck = 0.0;
            for (int d=0; d<featureAssignments[k].length; d++)
            {
                double num = 0.0;
                double den = 0.0;
                for (int n=0; n<objectRep.length; n++)
                {
                    num += objectRep[n]*this.Cli[n][d];
                    den += objectRep[n];
                }
                den *= this.ensemble.getDataLength();
                double value = (den>0.0)?num/den:0.0;
                
                if (Double.isInfinite(value) || Double.isNaN(value) || value < -0.000000001 || value > 1.000000001)
                {
                    throw new RuntimeException ("ERROR: value must be within [0,1]---value="+value);
                }

                if (value < 0.0)
                {
                    value = 0.0;
                }

                if (value > 1.0)
                {
                    value = 1.0;
                }
                
                sumCheck += value;
                
                featureAssignments[k][d] = new Double(value);
            }
            
            if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.999999999 || sumCheck > 1.000000001)
            {
                throw new RuntimeException ("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);
            }
        }

        
        for (int i=0; i<subspaceClusters.length; i++)
        {
            Double[] objectRep = fuzzyClusters[i].getFeatureVectorRepresentationDouble();
            Double[] featureRep = featureAssignments[i];
            
            subspaceClusters[i] = new ProjectiveCluster(this.ensemble.getInstances(), objectRep, featureRep, fuzzyClusters[i].getID(), true, true);
        }
      
        return new ProjectiveClustering(subspaceClusters);
    }
    
    public PDFSimilarity getPDFSim()
    {
        return this.pdfSim;
    }
    
    public void setPDFSim (PDFSimilarity pdfSim)
    {
        this.pdfSim = pdfSim;
    }
    
    /*
    public NumericalInstanceSimilarity getNumSim()
    {
        return this.numSim;
    }
    
    public void setNumSim (NumericalInstanceSimilarity numSim)
    {
        this.numSim = numSim;
    }
     */ 
    
    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }

    public long getOfflineExecutionTime()
    {
        return this.offlineExecutionTime;
    }

    public long getOnlineExecutionTime()
    {
        return this.onlineExecutionTime;
    }
    
    protected void computeCli()
    {
        this.Cli = new double[this.ensemble.getNumberOfInstances()][this.ensemble.getNumberOfFeaturesInEachCluster()];
        Double[][] allClusterObjectRep = this.ensemble.getAllClusterByObjectRepresentationMatrix();
        Double[][] allClusterFeatureRep = this.ensemble.getAllClusterByfeatureRepresentationMatrix();
        
        for (int l=0; l<this.Cli.length; l++)
        {
            for (int i=0; i<this.Cli[l].length; i++)
            {
                for (int c=0; c<allClusterObjectRep.length; c++)
                {
                    this.Cli[l][i] += allClusterObjectRep[c][l]*allClusterFeatureRep[c][i];
                }
            }
        }
    }

}

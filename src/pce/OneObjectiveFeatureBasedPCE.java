/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce;

import clustering.FuzzyKMedoids;
import dataset.ProjectiveClusteringDataset;
import evaluation.numericalinstance.NumericalInstanceSimilarity;
import evaluation.pdf.PDFSimilarity;
import objects.FuzzyCluster;
import objects.FuzzyClustering;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;


public class OneObjectiveFeatureBasedPCE extends FeatureBasedPCEMethod
{
    protected double[][] pairwiseFeatureDistances;
    protected double[][] coNonOccurrenceFeatureMatrix;
    
    protected PDFSimilarity pdfSim;
    protected NumericalInstanceSimilarity numSim;
    
    protected ProjectiveClustering result;
    
    public OneObjectiveFeatureBasedPCE (ProjectiveClusteringDataset ensemble, PDFSimilarity pdfSim, NumericalInstanceSimilarity numSim)
    {
        this.ensemble = ensemble;
        this.pdfSim = pdfSim;
        this.numSim = numSim;
        
        this.coNonOccurrenceFeatureMatrix = this.ensemble.getCoNonOccurrenceFeatureMatrix(this.pdfSim);
        this.pairwiseFeatureDistances = this.ensemble.getPairwiseFeatureDistancesBasedOnTheirFeatureRepresentations(numSim);             
    }
    
    public ProjectiveClustering execute (int nClusters)
    {
        double[][] distMatrix = computeCumulativeDistanceMatrix();
        
        FuzzyKMedoids fkm = new FuzzyKMedoids(ensemble.getFeaturesDataset());
        FuzzyClustering fkmResult = fkm.execute(distMatrix, nClusters);
        
        this.result = assignInstances(fkmResult);
        return this.result;
    }
    
    protected double[][] computeCumulativeDistanceMatrix()
    {
        double[][] m = new double[this.ensemble.getNumberOfFeaturesInEachCluster()][this.ensemble.getNumberOfFeaturesInEachCluster()];
        
        double max1 = Double.NEGATIVE_INFINITY;
        double max2 = Double.NEGATIVE_INFINITY;
        double min1 = Double.POSITIVE_INFINITY;
        double min2 = Double.POSITIVE_INFINITY;
        
        for (int i=0; i<this.coNonOccurrenceFeatureMatrix.length-1; i++)
        {
            for (int j=i+1; j<this.coNonOccurrenceFeatureMatrix[i].length; j++)
            {
                if (this.coNonOccurrenceFeatureMatrix[i][j] > max1)
                {
                    max1 = this.coNonOccurrenceFeatureMatrix[i][j];
                }
                if (this.coNonOccurrenceFeatureMatrix[i][j] < min1)
                {
                    min1 = this.coNonOccurrenceFeatureMatrix[i][j];
                }
                
                if (this.pairwiseFeatureDistances[i][j] > max2)
                {
                    max2 = this.pairwiseFeatureDistances[i][j];
                }
                if (this.pairwiseFeatureDistances[i][j] < min2)
                {
                    min2 = this.pairwiseFeatureDistances[i][j];
                } 
            }
        }
        
        for (int i=0; i<m.length-1; i++)
        {
            for (int j=i+1; j<m[i].length; j++)
            {
                //m[i][j] = 0.5*((this.coNonOccurrenceFeatureMatrix[i][j]/max1)+(this.pairwiseFeatureDistances[i][j]/max2));
                double val1 = 0.0;
                if (max1 > min1)
                {
                    val1 = (this.coNonOccurrenceFeatureMatrix[i][j]-min1)/(max1-min1);
                }
                double val2 = 0.0;
                if (max2 > min2)
                {
                    val2 = (this.pairwiseFeatureDistances[i][j]-min2)/(max2-min2);
                }
                m[i][j] = 1-0.5*(val1+val2);
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
    
    protected ProjectiveClustering assignInstances(FuzzyClustering fkmResult)
    {
        FuzzyCluster[] fuzzyClusters = fkmResult.getClusters();
        ProjectiveCluster[] subspaceClusters = new ProjectiveCluster[fuzzyClusters.length];
        double[][] objectByFeatureMatrix = this.ensemble.getObjectByFeatureMatrix();
        
        Double[][] objectAssignments = new Double[subspaceClusters.length][this.ensemble.getNumberOfInstances()];
        for (int i=0; i<objectAssignments.length; i++)
        {
            for (int j=0; j<objectAssignments[i].length; j++)
            {
                objectAssignments[i][j] = new Double(0.0);
            }
        }
        
        for (int i=0; i<subspaceClusters.length; i++)
        {
            Double[] featureRep = fuzzyClusters[i].getFeatureVectorRepresentationDouble();
            double den = 0.0;
            for (int j=0; j<featureRep.length; j++)
            {
                den += featureRep[j];
            }
            
            Double[] objectRep = new Double[this.ensemble.getNumberOfInstances()];
            for (int k=0; k<objectRep.length; k++)
            {
                double num = 0.0;
                for (int j=0; j<featureRep.length; j++)
                {
                    num += featureRep[j]*objectByFeatureMatrix[k][j];
                }
                
                objectRep[k] = num/den;
            }
            
            objectAssignments[i] = objectRep;            
        }
        
        for (int k=0; k<objectAssignments[0].length; k++)
        {
            double sum = 0.0;
            for (int i=0; i<objectAssignments.length; i++)
            {
                sum += objectAssignments[i][k];
            }
            
            for (int i=0; i<objectAssignments.length; i++)
            {
                objectAssignments[i][k] /= sum; 
            }
        }
        
        for (int i=0; i<subspaceClusters.length; i++)
        {
            Double[] featureRep = fuzzyClusters[i].getFeatureVectorRepresentationDouble();
            Double[] objectRep = objectAssignments[i];
            
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
    
    public NumericalInstanceSimilarity getNumSim()
    {
        return this.numSim;
    }
    
    public void setNumSim (NumericalInstanceSimilarity numSim)
    {
        this.numSim = numSim;
    }
    
    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }

}


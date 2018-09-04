/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce.twoobjective;

import dataset.ProjectiveClusteringDataset;
import evaluation.numericalinstance.NumericalInstanceSimilarity;
import evaluation.pdf.PDFSimilarity;
import moea.FeatureBasedFeaturePartitioningGAFunction;
import moea.FeatureBasedObjectPartitioningGAFunction;
import moea.GAFunction;
import moea.GASolution;
import moea.NSGA;
import moea.ProjectiveClusteringGASolution;
import moea.ProjectiveClusteringGASolutionUtilities;
import objects.ProjectiveClustering;
import pce.FeatureBasedPCEMethod;


public class MOEAFeatureBasedPCE extends FeatureBasedPCEMethod
{
    protected int maxIterations;
    protected PDFSimilarity pdfSim;
    protected NumericalInstanceSimilarity numSim;
    protected int populationSize;
    
    protected double[][] pairwiseFeatureDistances;
    protected double[][] coNonOccurrenceFeatureMatrix;
    
    protected ProjectiveClustering[] allResults;
    
    public MOEAFeatureBasedPCE(ProjectiveClusteringDataset ensemble, int maxIterations, PDFSimilarity pdfSim, NumericalInstanceSimilarity numSim, int populationSize)
    {
        this.ensemble = ensemble;
        this.maxIterations = maxIterations;
        this.pdfSim = pdfSim;
        this.numSim = numSim;
        this.populationSize = populationSize;
        
        this.pairwiseFeatureDistances = this.ensemble.getPairwiseFeatureDistancesBasedOnTheirFeatureRepresentations(numSim);
        this.coNonOccurrenceFeatureMatrix = this.ensemble.getWholeCoNonOccurrenceFeatureMatrix(pdfSim);
    }
    
    public ProjectiveClustering execute(int nc)
    {
        GAFunction objectFunction = new FeatureBasedObjectPartitioningGAFunction(this.pairwiseFeatureDistances);
        GAFunction featureFunction = new FeatureBasedFeaturePartitioningGAFunction(this.coNonOccurrenceFeatureMatrix);
        
        GAFunction[] functions = new GAFunction[2];
        functions[0] = objectFunction;
        functions[1] = featureFunction;
        
        int nClusters = this.ensemble.getNumberOfClustersInEachClustering();
        int nInstances = this.ensemble.getNumberOfInstances();
        int nFeatures = this.ensemble.getNumberOfFeaturesInEachCluster();
        
        //GASolution[] initialSolutions = ProjectiveClusteringGASolutionUtilities.randomSolutions(this.populationSize, functions, this.ensemble.getInstances(), nClusters, nInstances, nFeatures);
        GASolution[] initialSolutions = computeInitialSolutionsFromEnsemble(functions);
        
        System.out.println("MOEAFeatureBasedSubspaceEnsembles---started");
        NSGA nsga = new NSGA(initialSolutions, functions);
        GASolution[] result = nsga.execute(this.maxIterations);
        
        this.allResults = new ProjectiveClustering[result.length];
        for (int i=0; i<this.allResults.length; i++)
        {
            this.allResults[i] = (ProjectiveClustering)result[i].getSolution();
        }
        
        return this.allResults[0];
    }
    
    protected GASolution[] computeInitialSolutionsFromEnsemble(GAFunction[] functions)
    {
        ProjectiveClustering[] random = this.randomSelectionFromEnsemble(this.populationSize);
        GASolution[] solutions = new GASolution[random.length];
        
        for (int i=0; i<random.length; i++)
        {
            double[] objectives = new double[2];
            objectives[0] = functions[0].evaluate(random[i]);
            objectives[1] = functions[1].evaluate(random[i]);
            
            solutions[i] = new ProjectiveClusteringGASolution(random[i],objectives);
        }
        
        return solutions;
    }
    
    public ProjectiveClustering[] getAllResults()
    {
        return this.allResults;
    }
    
    public int getMaxIterations()
    {
        return this.maxIterations;
    }
    
    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    public int getPopulationSize()
    {
        return this.populationSize;
    }
    
    public void setPopulationSize(int psize)
    {
        this.populationSize = psize;
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
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce.twoobjective;

import dataset.ProjectiveClusteringDataset;
import evaluation.clustering.features.ProjectiveClusteringFeaturesSimilarity;
import evaluation.clustering.objects.ProjectiveClusteringObjectsSimilarity;
import moea.ClusteringBasedFeaturePartitioningGAFunction;
import moea.ClusteringBasedObjectPartitioningGAFunction;
import moea.GAFunction;
import moea.GASolution;
import moea.NSGA;
import moea.ProjectiveClusteringGASolution;
import moea.ProjectiveClusteringGASolutionUtilities;
import objects.Instance;
import objects.ProjectiveClustering;
import pce.ClusteringBasedPCEMethod;


public class MOEAClusteringBasedPCE extends ClusteringBasedPCEMethod
{
    protected int maxIterations;
    protected ProjectiveClusteringObjectsSimilarity objectClusteringSim;
    protected ProjectiveClusteringFeaturesSimilarity featureClusteringSim;
    protected int populationSize;
    
    protected ProjectiveClustering[] clusterings;
    
    protected ProjectiveClustering[] allResults;
    
    public MOEAClusteringBasedPCE(ProjectiveClusteringDataset ensemble, int maxIterations, ProjectiveClusteringObjectsSimilarity objectClusteringSim, ProjectiveClusteringFeaturesSimilarity featureClusteringSim,  int populationSize)
    {
        this.ensemble = ensemble;
        this.maxIterations = maxIterations;
        this.objectClusteringSim = objectClusteringSim;
        this.featureClusteringSim = featureClusteringSim;
        this.populationSize = populationSize;
        
        Instance[] data = this.ensemble.getData();
        this.clusterings = new ProjectiveClustering[data.length];
        for (int i=0; i<this.clusterings.length; i++)
        {
            this.clusterings[i] = (ProjectiveClustering)data[i];
        }
    }
    
    public ProjectiveClustering execute(int nc)
    {       
        long start = System.currentTimeMillis();
        GAFunction objectFunction = new ClusteringBasedObjectPartitioningGAFunction(this.clusterings, this.objectClusteringSim);
        GAFunction featureFunction = new ClusteringBasedFeaturePartitioningGAFunction(this.clusterings, this.featureClusteringSim);
        
        GAFunction[] functions = new GAFunction[2];
        functions[0] = objectFunction;
        functions[1] = featureFunction;
        
        int nClusters = this.ensemble.getNumberOfClustersInEachClustering();
        int nInstances = this.ensemble.getNumberOfInstances();
        int nFeatures = this.ensemble.getNumberOfFeaturesInEachCluster();
        
        //GASolution[] initialSolutions = ProjectiveClusteringGASolutionUtilities.randomSolutions(this.populationSize, functions, this.ensemble.getInstances(), nClusters, nInstances, nFeatures);
        GASolution[] initialSolutions = computeInitialSolutionsFromEnsemble(functions);
        
        NSGA nsga = new NSGA(initialSolutions, functions);
        //System.out.println("MOEAClusteringBasedPCE---started");
        GASolution[] result = nsga.execute(this.maxIterations);
        
        this.allResults = new ProjectiveClustering[result.length];
        for (int i=0; i<this.allResults.length; i++)
        {
            this.allResults[i] = (ProjectiveClustering)result[i].getSolution();
        }
        long stop = System.currentTimeMillis();

        this.onlineExecutionTime = stop-start;
        
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
    
    public ProjectiveClusteringObjectsSimilarity getObjectClusteringSim()
    {
        return this.objectClusteringSim;
    }
    
    public void setObjectClusteringSim (ProjectiveClusteringObjectsSimilarity objectClusteringSim)
    {
        this.objectClusteringSim = objectClusteringSim;
    }
    
    public ProjectiveClusteringFeaturesSimilarity getFeatureClusteringSim()
    {
        return this.featureClusteringSim;
    }
    
    public void setFeatureClusteringSim (ProjectiveClusteringFeaturesSimilarity featureClusteringSim)
    {
        this.featureClusteringSim = featureClusteringSim;
    }

}

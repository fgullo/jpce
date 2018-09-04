package evaluation.clustering;

import evaluation.cluster.ProjectiveClusterWeightedIntraSimilarity;
import evaluation.Similarity;
import objects.Instance;
import objects.NumericalInstance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

public class ProjectiveClusteringWeightedIntraSimilarity extends ProjectiveClusteringInternalValidityCriterion {

    private NumericalInstance[] centroids;
    private double[][] weights;
    /**
     * This is a costructor for ClusteringIntraSimilarity object
     */
    public ProjectiveClusteringWeightedIntraSimilarity (NumericalInstance[] centroids, double[][] weights)
    {
        this.centroids = centroids;
        this.weights = weights;
        
        if (this.weights.length != this.centroids.length)
        {
            throw new RuntimeException("ERROR: the number of rows of weights must be equal to the number of centroids!");
        }
        
        if (this.weights[0].length != this.centroids[0].getNumberOfFeatures())
        {
            throw new RuntimeException("ERROR: the number of columns of weights must be equal to the number of features of each centroid!");
        }
    }
    
    public ProjectiveClusteringWeightedIntraSimilarity ()
    {
        
    }

    /**
     * This method return the similarity between
     * @param i
     * @param sim
     * @return double
     */
    public double getSimilarity (Instance i, Similarity sim) {
        double sum=0;
        ProjectiveCluster[] clusters=((ProjectiveClustering)i).getClusters();
        if (clusters.length == 0)
        {
            return 0.0;
        }
        for(int j=0; j<clusters.length; j++)
        {
            ProjectiveClusterWeightedIntraSimilarity cis = new ProjectiveClusterWeightedIntraSimilarity(this.centroids[j], this.weights[j]);
            sum += cis.getSimilarity(clusters[j], sim);
        }
        return sum=sum/clusters.length;
    }

    /**
     * This method return the distance between
     * @param i
     * @param sim
     * @return double
     */
    public double getDistance (Instance i, Similarity sim) {
        double sum=0;
        ProjectiveCluster[] clusters=((ProjectiveClustering)i).getClusters();
        if (clusters.length == 0)
        {
            return 0.0;
        }
        for(int j=0; j<clusters.length; j++)
        {
            ProjectiveClusterWeightedIntraSimilarity cis = new ProjectiveClusterWeightedIntraSimilarity(this.centroids[j], this.weights[j]);
            sum += cis.getDistance(clusters[j], sim);
        }
        return sum=sum/clusters.length;
    }
    
    public double getSimilarity (Instance i, Similarity sim, NumericalInstance[] c, double[][] w)
    {
        this.centroids = c;
        this.weights = w;
        
        if (this.weights.length != this.centroids.length)
        {
            throw new RuntimeException("ERROR: the number of rows of weights must be equal to the number of centroids!");
        }
        
        if (this.weights[0].length != this.centroids[0].getNumberOfFeatures())
        {
            throw new RuntimeException("ERROR: the number of columns of weights must be equal to the number of features of each centroid!");
        }
        
        return this.getSimilarity(i, sim);
    }
    
    public double getDistance (Instance i, Similarity sim, NumericalInstance[] c, double[][] w)
    {
        this.centroids = c;
        this.weights = w;
        
        if (this.weights.length != this.centroids.length)
        {
            throw new RuntimeException("ERROR: the number of rows of weights must be equal to the number of centroids!");
        }
        
        if (this.weights[0].length != this.centroids[0].getNumberOfFeatures())
        {
            throw new RuntimeException("ERROR: the number of columns of weights must be equal to the number of features of each centroid!");
        }
        
        return this.getDistance(i, sim);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ProjectiveClusteringWeightedIntraSimilarity))
        {
            return false;
        }
        
        return true;
    }

}



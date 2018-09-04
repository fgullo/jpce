package evaluation.clustering.objects;

import evaluation.clustering.objects.ProjectiveClusteringObjectsSimilarity;
import evaluation.cluster.objects.ProjectiveClusterObjectsSimilarity;
import objects.Instance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;

/**
 *Similarity between two clustering based on Normalized Mutual Information
 */
public class CEObjectsProjectiveClusteringSim extends ProjectiveClusteringObjectsSimilarity
{
    private ProjectiveClusterObjectsSimilarity objectsSim;

    public CEObjectsProjectiveClusteringSim (ProjectiveClusterObjectsSimilarity objectsSim)
    {
        super(false);
        this.objectsSim = objectsSim;
        //this.onlyHard = false;
    }
    
    public CEObjectsProjectiveClusteringSim (ProjectiveClusterObjectsSimilarity objectsSim, boolean b)
    {
        super(b);
        this.objectsSim = objectsSim;
        //this.onlyHard = false;
    }
    
    public double getSimilarity (Instance i1, Instance i2)
    {       
        ProjectiveCluster [] partition1 = ((ProjectiveClustering)i1).getClusters();
        ProjectiveCluster [] partition2 = ((ProjectiveClustering)i2).getClusters();
        
        double[][] confusionMatrix = computeSimConfusionMatrix(partition1, partition2);

        double ret = computeCEsim(confusionMatrix);
        
        if (Double.isInfinite(ret) || Double.isNaN(ret))
        {
            throw new RuntimeException("ERROR: the value is INFINITY or NAN");
        }
        
        return ret;
    }
    
    public double getDistance (Instance i1, Instance i2) 
    {
        ProjectiveCluster [] partition1 = ((ProjectiveClustering)i1).getClusters();
        ProjectiveCluster [] partition2 = ((ProjectiveClustering)i2).getClusters();
        
        double[][] confusionMatrix = computeDistConfusionMatrix(partition1, partition2);
        
        double ret = computeCEdist(confusionMatrix);
        
        if (Double.isInfinite(ret) || Double.isNaN(ret))
        {
            throw new RuntimeException("ERROR: the value is INFINITY or NAN");
        }
        
        return ret;
    }
    
    private double[][] computeSimConfusionMatrix(ProjectiveCluster[] partition1, ProjectiveCluster[] partition2)
    {
        double[][] confusionSimMatrix = new double[partition1.length][partition2.length];
        for (int i=0; i<confusionSimMatrix.length; i++)
        {
            for (int j=0; j<confusionSimMatrix[i].length; j++)
            {
                confusionSimMatrix[i][j] = this.objectsSim.getSimilarity(partition1[i], partition2[j]);
            }
        } 
        
        return confusionSimMatrix;
    }
    
    private double[][] computeDistConfusionMatrix(ProjectiveCluster[] partition1, ProjectiveCluster[] partition2)
    {
        double[][] confusionDistMatrix = new double[partition1.length][partition2.length];
        for (int i=0; i<confusionDistMatrix.length; i++)
        {
            for (int j=0; j<confusionDistMatrix[i].length; j++)
            {
                confusionDistMatrix[i][j] = this.objectsSim.getDistance(partition1[i], partition2[j]);
            }
        }
        
        return confusionDistMatrix;
    }
    
    private double computeCEsim(double[][] confusionMatrix)
    {
        double sum1 = 0.0;
        for (int i=0; i<confusionMatrix.length; i++)
        {
            double max = Double.NEGATIVE_INFINITY;
            for (int j=0; j<confusionMatrix[i].length; j++)
            {
                if (confusionMatrix[i][j] > max)
                {
                    max = confusionMatrix[i][j];
                }
            }
            sum1 += max;
        }
        sum1 /= confusionMatrix.length;
        
        double sum2 = 0.0;
        for (int j=0; j<confusionMatrix[0].length; j++)
        {
            double max = Double.NEGATIVE_INFINITY;
            for (int i=0; i<confusionMatrix.length; i++)
            {
                if (confusionMatrix[i][j] > max)
                {
                    max = confusionMatrix[i][j];
                }
            }
            sum2 += max;
        }
        sum2 /= confusionMatrix[0].length;
        
        return (sum1+sum2)/2;        
    }
    
    private double computeCEdist(double[][] confusionMatrix)
    {
        double sum1 = 0.0;
        for (int i=0; i<confusionMatrix.length; i++)
        {
            double min = Double.POSITIVE_INFINITY;
            for (int j=0; j<confusionMatrix[i].length; j++)
            {
                if (confusionMatrix[i][j] < min)
                {
                    min = confusionMatrix[i][j];
                }
            }
            sum1 += min;
        }
        sum1 /= confusionMatrix.length;
        
        double sum2 = 0.0;
        for (int j=0; j<confusionMatrix[0].length; j++)
        {
            double min = Double.POSITIVE_INFINITY;
            for (int i=0; i<confusionMatrix.length; i++)
            {
                if (confusionMatrix[i][j] < min)
                {
                    min = confusionMatrix[i][j];
                }
            }
            sum2 += min;
        }
        sum2 /= confusionMatrix[0].length;
        
        return (sum1+sum2)/2;        
    }
    

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof CEObjectsProjectiveClusteringSim))
        {
            return false;
        }
        
        return true;
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moea;

import objects.Instance;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;


public class ProjectiveClusteringGASolutionUtilities
{
    public static ProjectiveClusteringGASolution[] randomSolutions(int nRandomGASolutions, GAFunction[] functions, Instance[] instances, int nClusters, int nInstances, int nFeatures)
    {
        int IDcluster = -1;
        ProjectiveClusteringGASolution[] population = new ProjectiveClusteringGASolution[nRandomGASolutions];
        for (int i=0; i<nRandomGASolutions; i++)
        {
            ProjectiveClustering s = ProjectiveClustering.randomGen(instances, nClusters, nInstances, nFeatures);
            s.setID(-(i+1));
            ProjectiveCluster[] clusters = s.getClusters();
            for (int h=0; h<clusters.length; h++)
            {
                clusters[h].setID(IDcluster);
                IDcluster--;
            }
            
            double[] objectiveValues = new double[functions.length];
            for (int p=0; p<objectiveValues.length; p++)
            {
                objectiveValues[p] = functions[p].evaluate(s);
            }
            
            population[i] = new ProjectiveClusteringGASolution(s, objectiveValues);
        }
        
        return population;
    }
    
    public static ProjectiveClusteringGASolution randomSolution(GAFunction[] functions, Instance[] instances, int nClusters, int nInstances, int nFeatures)
    {
        int IDcluster = -1;

        ProjectiveClustering s = ProjectiveClustering.randomGen(instances, nClusters, nInstances, nFeatures);
        s.setID(-1);
        ProjectiveCluster[] clusters = s.getClusters();
        for (int h=0; h<clusters.length; h++)
        {
            clusters[h].setID(IDcluster);
            IDcluster--;
        }

        double[] objectiveValues = new double[functions.length];
        for (int p=0; p<objectiveValues.length; p++)
        {
            objectiveValues[p] = functions[p].evaluate(s);
        }

        return new ProjectiveClusteringGASolution(s, objectiveValues);
    }

}

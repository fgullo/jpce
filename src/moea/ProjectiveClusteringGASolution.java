/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moea;

import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import java.util.Random;

public class ProjectiveClusteringGASolution extends GASolution
{
    
    public ProjectiveClusteringGASolution(ProjectiveClustering solution, double[] objectiveFunctionValues)
    {
        this.solution = solution;
        this.objectiveFunctionValues = objectiveFunctionValues;
    }
    
    public GASolution gaussianNoiseMutation(GAFunction[] functions)
    {
        Random rndGenerator = new Random();
        
        ProjectiveClustering sol = (ProjectiveClustering)solution;
        
        //mutate object assignments
        Double[][] m = sol.getClusterByObjectsMatrix();
        Double[][] mnewO = new Double[m.length][m[0].length];
        for (int i=0; i<mnewO.length; i++)
        {
            for (int j=0; j<mnewO[i].length; j++)
            {
                double val = m[i][j];
                //val += rndGenerator.nextGaussian();
                val *= 1+rndGenerator.nextGaussian();
                //val *= 1+(2*Math.random()-1);
                mnewO[i][j] = val;               
            }
        }
        //normalize mnewO
        for (int j=0; j<mnewO[0].length; j++)
        {
            double min = mnewO[0][j];
            double max = mnewO[0][j];
            //double sum = mnewO[0][j];
            for (int i=1; i<mnewO.length; i++)
            {
                //sum += mnewO[i][j];
                if (mnewO[i][j] < min)
                {
                    min = mnewO[i][j];
                }
                if (mnewO[i][j] > max)
                {
                    max = mnewO[i][j];
                }
            }
            double den = max-min;
            if (den == 0.0)
            {
                double sumCheck = 0.0;
                for (int i=0; i<mnewO.length; i++)
                {
                    mnewO[i][j] = ((double)1.0)/mnewO.length;
                    if (Double.isInfinite(mnewO[i][j]) || Double.isNaN(mnewO[i][j]) || mnewO[i][j] >1 || mnewO[i][j] < 0)
                    {
                        throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                    }
                    sumCheck += mnewO[i][j];
                }
                
                if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.99999 || sumCheck >= 1.00001)
                {
                    throw new RuntimeException("ERROR: sumCheck must be 1----sumCheck="+sumCheck);
                }
            }
            else if (den > 0.0)
            {
                double sum = 0.0;
                for (int i=0; i<mnewO.length; i++)
                {
                    double previous = mnewO[i][j];
                    mnewO[i][j] = (previous-min)/den;
                    if (Double.isInfinite(mnewO[i][j]) || Double.isNaN(mnewO[i][j]) || mnewO[i][j] >1 || mnewO[i][j] < 0)
                    {
                        throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                    }
                    sum += mnewO[i][j];
                }
                
                double sumCheck = 0.0;
                if (sum == 0)
                {
                    for (int i=0; i<mnewO.length; i++)
                    {
                        mnewO[i][j] = ((double)1.0)/mnewO.length;
                        if (Double.isInfinite(mnewO[i][j]) || Double.isNaN(mnewO[i][j]) || mnewO[i][j] >1 || mnewO[i][j] < 0)
                        {
                            throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                        }
                        sumCheck += mnewO[i][j];
                    }                    
                }
                else
                {
                    for (int i=0; i<mnewO.length; i++)
                    {
                        mnewO[i][j] /= sum;
                        if (Double.isInfinite(mnewO[i][j]) || Double.isNaN(mnewO[i][j]) || mnewO[i][j] >1 || mnewO[i][j] < 0)
                        {
                            throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                        }
                        sumCheck += mnewO[i][j];
                    }                      
                }
                
                if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.99999 || sumCheck >= 1.00001)
                {
                    throw new RuntimeException("ERROR: sumCheck must be 1----sumCheck="+sumCheck);
                }
            }
            else
            {
                throw new RuntimeException("ERROR: den must be greater than or equal to 0.0");
            }
            
            /*
            if (min < 0)
            {
                sum -= min*mnewO.length;
                double sumCheck = 0.0;
                for (int i=0; i<mnewO.length; i++)
                {
                    mnewO[i][j] = (mnewO[i][j]-min)/sum;
                    if (mnewO[i][j] >1 || mnewO[i][j] < 0)
                    {
                        throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                    }
                    sumCheck += mnewO[i][j];
                }
                
                if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.99999 || sumCheck >= 1.00001)
                {
                    throw new RuntimeException("ERROR: sumCheck must be 1");
                }
            }
            else
            {
                double sumCheck = 0.0;
                for (int i=0; i<mnewO.length; i++)
                {
                    mnewO[i][j] = mnewO[i][j]/sum;
                    if (mnewO[i][j] >1 || mnewO[i][j] < 0)
                    {
                        throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                    }
                    sumCheck += mnewO[i][j];
                }
                
                if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.99999 || sumCheck >= 1.00001)
                {
                    throw new RuntimeException("ERROR: sumCheck must be 1");
                }
            }
            */
        }
        
        
        
        //mutate features assignments
        m = sol.getClusterByFeaturesMatrix();
        Double[][] mnewF = new Double[m.length][m[0].length];
        for (int i=0; i<mnewF.length; i++)
        {
            for (int j=0; j<mnewF[i].length; j++)
            {
                double val = m[i][j];
                //val += rndGenerator.nextGaussian();
                val *= (1+rndGenerator.nextGaussian());
                mnewF[i][j] = val;               
            }
        }
        //normalize mnew
        for (int i=0; i<mnewF.length; i++)
        {
            double min = mnewF[i][0];
            double max = mnewF[i][0];
            //double sum = mnewF[0][j];
            for (int j=1; j<mnewF[i].length; j++)
            {
                //sum += mnewF[i][j];
                if (mnewF[i][j] < min)
                {
                    min = mnewF[i][j];
                }
                if (mnewF[i][j] > max)
                {
                    max = mnewF[i][j];
                }
            }
            double den = max-min;

            if (den == 0.0)
            {
                double sumCheck = 0.0;
                for (int j=0; j<mnewF[i].length; j++)
                {
                    mnewF[i][j] = ((double)1.0)/mnewF[i].length;
                    if (Double.isInfinite(mnewF[i][j]) || Double.isNaN(mnewF[i][j]) || mnewF[i][j] >1 || mnewF[i][j] < 0)
                    {
                        throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                    }
                    sumCheck += mnewF[i][j];
                }
                
                if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.99999 || sumCheck >= 1.00001)
                {
                    throw new RuntimeException("ERROR: sumCheck must be 1----sumCheck="+sumCheck);
                }
            }
            else if (den > 0.0)
            {
                double sum = 0.0;
                for (int j=0; j<mnewF[i].length; j++)
                {
                    mnewF[i][j] = (mnewF[i][j]-min)/den;
                    if (Double.isInfinite(mnewF[i][j]) || Double.isNaN(mnewF[i][j]) || mnewF[i][j] >1 || mnewF[i][j] < 0)
                    {
                        throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                    }
                    sum += mnewF[i][j];
                }
                
                double sumCheck = 0.0;
                if (sum == 0.0)
                {
                    for (int j=0; j<mnewF[i].length; j++)
                    {
                        mnewF[i][j] = ((double)1.0)/mnewF[i].length;
                        if (Double.isInfinite(mnewF[i][j]) || Double.isNaN(mnewF[i][j]) || mnewF[i][j] >1 || mnewF[i][j] < 0)
                        {
                            throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                        }
                        sumCheck += mnewF[i][j];
                    }                      
                }
                else
                {
                    for (int j=0; j<mnewF[i].length; j++)
                    {
                        mnewF[i][j] /= sum;
                        if (Double.isInfinite(mnewF[i][j]) || Double.isNaN(mnewF[i][j]) || mnewF[i][j] >1 || mnewF[i][j] < 0)
                        {
                            throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                        }
                        sumCheck += mnewF[i][j];
                    }                      
                }
                
                if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.99999 || sumCheck >= 1.00001)
                {
                    throw new RuntimeException("ERROR: sumCheck must be 1----sumCheck="+sumCheck);
                }
            }
            else
            {
                throw new RuntimeException("ERROR: den must be greater than or equal to 0.0");
            }
            
            /*
            if (min < 0)
            {
                sum -= min*mnewF.length;
                double sumCheck = 0.0;
                for (int i=0; i<mnewF.length; i++)
                {
                    mnewF[i][j] = (mnewF[i][j]-min)/sum;
                    if (mnewF[i][j] >1 || mnewF[i][j] < 0)
                    {
                        throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                    }
                    sumCheck += mnewF[i][j];
                }
                
                if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.99999 || sumCheck >= 1.00001)
                {
                    throw new RuntimeException("ERROR: sumCheck must be 1");
                }
            }
            else
            {
                double sumCheck = 0.0;
                for (int i=0; i<mnewF.length; i++)
                {
                    mnewF[i][j] = mnewF[i][j]/sum;
                    if (mnewF[i][j] >1 || mnewF[i][j] < 0)
                    {
                        throw new RuntimeException("ERROR: mnew[i][j] must be within [0,1]");
                    }
                    sumCheck += mnewF[i][j];
                }
                
                if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck < 0.99999 || sumCheck >= 1.00001)
                {
                    throw new RuntimeException("ERROR: sumCheck must be 1");
                }
            }
            */
        }
        
        //build ProjectiveClustering object from matrices mnewO and mnewF
        ProjectiveCluster[] clusters = new ProjectiveCluster[mnewO.length];
        for (int u=0; u<clusters.length; u++)
        {
            Double[] objectsRep = mnewO[u];
            Double[] featuresRep = mnewF[u];

            clusters[u] = new ProjectiveCluster(sol.getInstances(), objectsRep, featuresRep, u, true, true);
        }
       
        ProjectiveClustering newSol = new ProjectiveClustering(clusters, -1);
        double[] objectiveValues = new double[functions.length];
        for (int i=0; i<objectiveValues.length; i++)
        {
            objectiveValues[i] = functions[i].evaluate(newSol);
        }
        
        return new ProjectiveClusteringGASolution(newSol, objectiveValues);
    }
    
    public void recomputeIDs(GASolution[] solutions)
    {
        int IDcluster = -1;
        for (int i=0; i<solutions.length; i++)
        {
            ProjectiveClustering sc = (ProjectiveClustering)solutions[i].getSolution();
            sc.setID(-(i+1));
            ProjectiveCluster[] clusters = sc.getClusters();
            for (int j=0; j<clusters.length; j++)
            {
                clusters[j].setID(IDcluster);
                IDcluster--;
            }
        }      
    }
   
}

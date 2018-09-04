/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moea;

import java.util.ArrayList;

public class ParetoRanking
{
    protected GASolution[] population;
    //protected SubspaceClusteringEnsemblesGAFunction[] functions;
    protected int numberOfObjectives;
    
    protected int[] rankByID;
    
    public ParetoRanking(GASolution[] population)
    {
        this.population = population;
        this.numberOfObjectives = this.population[0].getNumberOfObjectives();
        
        for (int i=1; i<population.length; i++)
        {
            if (population[i].getNumberOfObjectives() != this.numberOfObjectives)
            {
                throw new RuntimeException("ERROR: all the individuals must have the same number of objectives!");
            }
        }
    }
    
    public ArrayList[] rank()
    {        
        ArrayList[] ranking = new ArrayList[population.length];
        
        int[] alreadyRanked = new int[population.length];
        for (int i=0; i<alreadyRanked.length; i++)
        {
            alreadyRanked[i] = -1;
        }
        
        boolean end = false;
        int currentRankValue = 0;
        while (!end)
        {
            ArrayList<GASolution> v = new ArrayList<GASolution>();
            int[] alreadyRankedTmp = new int[alreadyRanked.length];
            for (int i=0; i<ranking.length; i++)
            {
                alreadyRankedTmp[i] = alreadyRanked[i];
            }
            
            int changes = 0;
            for (int i=0; i<population.length; i++)
            {
                if (alreadyRanked[i] == -1)
                {
                    if (nonDominated(i,alreadyRanked))
                    {
                        v.add(population[i]);
                        alreadyRankedTmp[i] = currentRankValue;
                        changes++;
                    }
                }
            }
            
            alreadyRanked = alreadyRankedTmp;

            if (changes == 0)
            {
                end = true;
                for (int i=0; i<alreadyRanked.length; i++)
                {
                    if (alreadyRanked[i] == -1)
                    {
                        alreadyRanked[i] = currentRankValue;
                        v.add(population[i]);
                    }
                }
            }            
            
            if (v.size() > 0)
            {
                ranking[currentRankValue] = v;
                currentRankValue++;
            }            
            
        }
        
        ArrayList[] ret = new ArrayList[currentRankValue];
        int totRanked = 0;
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = ranking[i];
            totRanked += ret[i].size();
        }
        
        if (totRanked != population.length)
        {
            throw new RuntimeException("ERROR: all the individuals in the population must be ranked!");
        }       
        
        this.rankByID = alreadyRanked;
        
        return ret;
    }

    public ArrayList[] efficientRank()
    {
        ArrayList[] ranking = new ArrayList[population.length];

        int[] dominationCount = new int[population.length];
        boolean[][] dominatedSolutions = new boolean[population.length][population.length];
        efficientInitializing(dominationCount,dominatedSolutions);

        int[] alreadyRanked = new int[population.length];
        for (int i=0; i<alreadyRanked.length; i++)
        {
            alreadyRanked[i] = -1;
        }

        boolean end = false;
        int currentRankValue = 0;
        while (!end)
        {
            ArrayList<GASolution> v = new ArrayList<GASolution>();

            int changes = 0;
            for (int i=0; i<dominationCount.length; i++)
            {
                if (alreadyRanked[i] == -1 && dominationCount[i] == 0)
                {
                    v.add(this.population[i]);
                    alreadyRanked[i] = currentRankValue;
                    changes++;
                    for (int j=0; j<dominatedSolutions[i].length; j++)
                    {
                        if (i!=j && dominatedSolutions[i][j])
                        {
                            dominationCount[j]--;
                        }
                    }
                }
            }

            if (changes == 0)
            {
                end = true;
                for (int i=0; i<alreadyRanked.length; i++)
                {
                    if (alreadyRanked[i] == -1)
                    {
                        alreadyRanked[i] = currentRankValue;
                        v.add(population[i]);
                    }
                }
            }

            if (v.size() > 0)
            {
                ranking[currentRankValue] = v;
                currentRankValue++;
            }
        }

        ArrayList[] ret = new ArrayList[currentRankValue];
        int totRanked = 0;
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = ranking[i];
            totRanked += ret[i].size();
        }

        if (totRanked != population.length)
        {
            throw new RuntimeException("ERROR: all the individuals in the population must be ranked!");
        }

        this.rankByID = alreadyRanked;

        return ret;
    }


    private void efficientInitializing(int[] dominationCount, boolean[][] dominatedSolutions)
    {
        for (int i=0; i<dominationCount.length; i++)
        {
            dominationCount[i] = -1;
        }
        
        for (int i=0; i<dominatedSolutions.length; i++)
        {
            for (int j=0; j<dominatedSolutions[i].length; j++)
            {
                dominatedSolutions[i][j] = false;
            }
        }
        
        
        for (int i=0; i<this.population.length; i++)
        {
            for (int j=0; j<this.population.length; j++)
            {
                if (i!=j && isDominated(this.population[i], this.population[j]))
                {
                    dominationCount[i]++;
                }
                else if (i!=j && isDominated(this.population[j], this.population[i]))
                {
                    dominatedSolutions[i][j] = true;
                }
            }
        }
    }

    private boolean isDominated (GASolution i, GASolution j)
    {
        double[] objectiveValuesI = i.getObjectiveFunctionValues();
        double[] objectiveValuesJ = j.getObjectiveFunctionValues();

        if (objectiveValuesI.length != objectiveValuesJ.length)
        {
            throw new RuntimeException("ERROR: the two solutions must have the same number of objectives");
        }

        boolean isDominated = false;

        for (int k=0; k<objectiveValuesI.length; k++)
        {
            if(objectiveValuesJ[k] > objectiveValuesI[k])
            {
                return false;
            }

            if (objectiveValuesJ[k] < objectiveValuesI[k])
            {
                isDominated = true;
            }
        }

        return isDominated;
    }
    
    protected boolean nonDominated(int i, int[] ranking)
    {
        double[] objectiveValuesI =this.population[i].getObjectiveFunctionValues();
        
        for (int k=0; k<ranking.length; k++)
        {
            if (k != i && ranking[k] == -1)
            {
                boolean tutteMinoriUguali = true;
                boolean almenoUnaMinore = false;
                
                double[] objectiveValuesK = this.population[k].getObjectiveFunctionValues();
                
                for (int z=0; z<objectiveValuesK.length && tutteMinoriUguali; z++)
                {
                    if (objectiveValuesI[z] < objectiveValuesK[z])
                    {
                        almenoUnaMinore = true;
                    }
                    else if (objectiveValuesI[z] > objectiveValuesK[z])
                    {
                        tutteMinoriUguali = false;
                    }
                }
                
                if (!tutteMinoriUguali || !almenoUnaMinore)
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public int[] getRankByID()
    {
        return this.rankByID;
    }

}

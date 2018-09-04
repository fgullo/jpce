/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moea;

import java.util.ArrayList;

public class NSGA
{
    //private int maxIterations;
    private int populationSize;
    private GASolution[] population;
    private GAFunction[] functions;
    
    //private SubspaceClusteringDataset ensemble;
    
    public NSGA(GASolution[] initialPopulation, GAFunction[] functions)
    {
        if (initialPopulation.length%2 != 0)
        {
            throw new RuntimeException("THE SIZE OF THE POPULATION MUST BE AN EVEN INTEGER");
        }
        
        //this.ensemble = ensemble;
        this.population = initialPopulation;
        this.populationSize = this.population.length;
        
        this.functions = functions;        
    }
    
    public GASolution[] execute(int maxIterations)
    {        
        //System.out.print("NSGA---totIt="+maxIterations+"---");
        int currentIteration = 1;
        int numberOfRemainingSolutions = this.populationSize/2;
        while (currentIteration <= maxIterations)
        {
           //System.out.print("it="+currentIteration+",");
            
           ParetoRanking pareto = new ParetoRanking(this.population);
           ArrayList[] ranking = pareto.rank();
           //int[] rankingById = pareto.getRankByID();
           
           GASolution[] newPopulation = new GASolution[this.population.length];
           int toRemain = 0;
           for (int i=0; i<ranking.length && toRemain<numberOfRemainingSolutions; i++)
           {
               ArrayList<GASolution> rankI = ranking[i];
               for (int j=0; j<rankI.size() && toRemain<numberOfRemainingSolutions; j++)
               {
                   newPopulation[toRemain] = rankI.get(j);
                   toRemain++;
               }
           }
           
           mutate(newPopulation, toRemain);
           
           this.population = newPopulation;
           
           currentIteration++;          
        }
        
        //System.out.println();
        
        this.population = allPopulation();
        //this.population = dominantPopulation();
        
        //System.out.println("NSGA terminated---Size of dominant population="+this.population.length+" (size of initial population="+this.populationSize+")");

        return this.population;        
    }
    
    private void mutate(GASolution[] pop, int startingFrom)
    {
        for (int i=0; i<startingFrom; i++)
        {
            GASolution kid = population[i].gaussianNoiseMutation(this.functions);
            pop[i+startingFrom] = kid;
        }
        
        pop[0].recomputeIDs(pop);
    }
    
    private GASolution[] allPopulation()
    {
        ParetoRanking pareto = new ParetoRanking(this.population);
        ArrayList[] ranking = pareto.rank();
        
        GASolution[] newPopulation = new GASolution[population.length];
        int k = 0;
        for (int i=0; i<ranking.length; i++)
        {
            ArrayList<GASolution> rankI = ranking[i];
            for (int j=0; j<rankI.size(); j++)
            {
                newPopulation[k] = rankI.get(j);
                k++;
            }
        } 
        
        return newPopulation;
    }
    
    private GASolution[] dominantPopulation()
    {
        ParetoRanking pareto = new ParetoRanking(this.population);
        ArrayList[] ranking = pareto.rank();
        
        ArrayList<GASolution> rank0 = ranking[0];
        
        GASolution[] newPopulation = new GASolution[rank0.size()];            
        for (int j=0; j<rank0.size(); j++)
        {
            newPopulation[j] = rank0.get(j);
        }
 
        return newPopulation;
    }
}










































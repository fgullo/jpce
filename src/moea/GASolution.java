/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package moea;

public abstract class GASolution
{
    protected Object solution;
    protected double[] objectiveFunctionValues;
    
    public double[] getObjectiveFunctionValues()
    {
        return this.objectiveFunctionValues;
    }
    
    public Object getSolution()
    {
        return this.solution;
    }
    
    public int getNumberOfObjectives()
    {
        if (this.objectiveFunctionValues == null)
        {
            return 0;
        }
        
        return this.objectiveFunctionValues.length;
    }
    
    public void recomputeObjectiveFunctionValues(GAFunction[] functions)
    {
        if (functions.length != this.objectiveFunctionValues.length)
        {
            throw new RuntimeException("ERROR: the number of functions must be equal to the number of objectives!");
        }
        
        if (this.objectiveFunctionValues == null)
        {
            this.objectiveFunctionValues = new double[functions.length];
        }
        
        for (int i=0; i<this.objectiveFunctionValues.length; i++)
        {
            this.objectiveFunctionValues[i] = functions[i].evaluate(this.solution);
        }
    }
    
    public abstract GASolution gaussianNoiseMutation(GAFunction[] functions);
    
    public abstract void recomputeIDs(GASolution[] solutions);
}













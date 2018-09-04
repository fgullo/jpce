package weighting;

import java.util.Arrays;


public abstract class GeneralWeightingScheme extends WeightingScheme 
{
    
    protected double[] linearWeighting(double[] weights)
    {
        double[] weightsReturn = new double[weights.length]; 
        double sum = 0.0;
        for (int i=0; i<weights.length; i++)
        {
            if (weights[i]<0.0)
            {
                throw new RuntimeException("Weights must be greater than or equal to 0");
            }
            sum += weights[i];
        }
        
        if (sum==0)
        {
            return weightsReturn;
        }
        
        
        for (int i=0; i<weightsReturn.length; i++)
        {
            weightsReturn[i] = weights[i]/sum;
        }
        
        return weightsReturn;
    }
    
    protected double[] normalWeighting(double[] weights)
    {
        double[] weightsReturn = new double[weights.length]; 
        double sigma = calculateSigma(weights);
        double mu = calculateMu(weights);
        
        double sum=0.0;
        for (int i=0; i<weightsReturn.length; i++)
        {
            if (weights[i]<0)
            {
                throw new RuntimeException("Weights must be greater than or equal to 0");
            }            
            weightsReturn[i] = normalValue(weights[i],mu,sigma);
            sum += weightsReturn[i];
        }
        
        if (sum == 0)
        {        
            return weightsReturn;
        }
        
        for (int i=0; i<weightsReturn.length; i++)
        {
            weightsReturn[i] = weightsReturn[i]/sum;
        }
        
        return weightsReturn;
    }
    
    private double normalValue(double x, double mu, double sigma)
    {
        double a = ((double)1)/Math.sqrt(2*Math.PI*sigma);
        double b = -0.5*Math.pow((x-mu)/sigma, 2);
        
        return a*Math.exp(b);
    }
    
    private double calculateMu (double[] v)
    {
        double[] tmp = new double[v.length];
        for (int i=0; i<v.length; i++)
        {
            tmp[i] = v[i];
        }
        
        Arrays.sort(tmp);
        
        return tmp[tmp.length/2];
    }
    
    private double calculateSigma (double[] v)
    {
        double[] tmp = new double[v.length];
        for (int i=0; i<v.length; i++)
        {
            tmp[i] = v[i];
        }
        
        Arrays.sort(tmp);
        
        double min = tmp[0];
        double max = tmp[tmp.length-1];
        double median = tmp[tmp.length/2];
        
        double d1 = median-min;
        double d2 = max-median;
        
        double sigma = (d1>d2)?(d1/3):(d2/3);
        return sigma;
    }
}



package util;


public class Util {
    
    public static final double epsilon = 0.0000001;
    
    /**
     * This method calculate the factorial of a number
     * 
     * @param val
     * @return int
     */
    /*
    public static int factorial(int val){
        if(val <= 1)
            return 1;
        else
            return val * factorial (val-1);
    }
    */
    
    public static void throwException(double value, double minValue, double maxValue)
    {
        if (Double.isInfinite(value) || Double.isNaN(value))
        {
            throw new RuntimeException("ERROR: value is infiniti or nan---value="+value);
        }
        
        if ((!Double.isInfinite(minValue) && value < minValue-epsilon) || (!Double.isInfinite(maxValue) && value > maxValue+epsilon))
        {
            throw new RuntimeException("ERROR: value must be within ["+minValue+","+maxValue+"]---value="+value);
        }
    }
    
    public static double factorial(int val)
    {
        if (val <= 1)
        {
            return 1;
        }
        
        double f=1;
        for (int i=2; i<=val; i++)
        {
            f*=i;
        }
        
        return f;
    }
    
    /**
     * This method calculate the binomial coefficient
     * 
     * @param a
     * @param b
     * @return int
     */
    public static double binomialCoeff(int a, int b){
        if(a==0)
            return 0;
        
        if(b==0 || a==b)
            return 1;    
        
        return (factorial(a))/(factorial(b)*factorial(a-b));
      
    }
    
    /**
     * This method adopted the following convetion : 0 x log (0) = 0. This convetion is adopted 
     * because in some case the clusters are empty.
     * @param p_i_j
     * @param p_ij
     * @return double
     */
    public static double returnLog10 (double p_i_j, double p_ij){
        if(p_i_j==0 || p_ij==0)
            return 0;
        else
            return p_i_j * Math.log10(p_ij);   
    }

}

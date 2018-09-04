
package util;

import java.io.*;
import java.util.StringTokenizer;

public class RearrangeProclusEnsemble 
{
    public static String file = "ensembles"+File.separator+"p53mutants_PROCLUS_ENSEMBLE_1.data";
    public static int avgDimensions = 100;
    public static int dimensionality = 5409;
    
    public static void main(String[] args)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            String outFile = file+"_REARRANGED";
            new File(outFile).createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
            
            int l = 1;
            String line = br.readLine();
            while (line != null)
            {
                String newLine = line;
                if (l>=7 && (l%7==0 || (l-2)%7==0))
                {
                    newLine = computeNewLine(line);
                }

                bw.write(newLine);
                bw.newLine();
                bw.flush();
                l++;
                line = br.readLine();
            }
            br.close();
            bw.flush();
            bw.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static String computeNewLine(String line)
    {
        String newLine = "";
        
        StringTokenizer st = new StringTokenizer(line,",; \t");
        int count = 0;
        while (st.hasMoreTokens())
        {
            double d = Double.parseDouble(st.nextToken());
            if (d == 1.0)
            {
                count++;
            }
        }
        
        if (count > 0)
        {
            st = new StringTokenizer(line,",; \t");
            while (st.hasMoreTokens())
            {
                double d = Double.parseDouble(st.nextToken());
                d /= count;
                newLine += ""+d+" ";
            }
        }
        else
        {
            double[] v = new double[dimensionality];
            for (int i=1; i<=avgDimensions; i++)
            {
                double d = Math.random();
                int x = (int)Math.round(d*dimensionality-i);
                int y = 0;
                int j = 0;
                while (y < x)
                {
                    if (v[i] == 0.0)
                    {
                        y++;
                    }
                    j++;
                }
                
                v[j-1] = ((double)1.0)/avgDimensions;
            }
            
            for (int x=0; x<v.length; x++)
            {
                newLine += ""+v[x];
            }
        }
        
        return newLine;
    }
}

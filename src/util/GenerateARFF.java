
package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

public class GenerateARFF 
{
    public static String path = "raw_datasets";
    
    public static void main(String[] args)
    {
        try
        {
            File outDir = new File(path+File.separator+"ARFF");
            if (!outDir.exists())
            {
                outDir.mkdir();
            }
            
            File[] inFiles = new File(path).listFiles();
            for (File f:inFiles)
            {
                if (!f.isDirectory() && !f.getName().startsWith("20News_full"))
                {
                    int attributes = 0;
                    int classes = -1;
                    
                    System.out.println(f.getName());
                    
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String line = br.readLine();
                    
                    if (line.equals("subspaces"))
                    {
                        while(!line.equals("objects"))
                        {
                            line = br.readLine();
                        }
                        
                        line = br.readLine(); 
                    }
                    
                    
                    StringTokenizer st = new StringTokenizer(line,";, \t");
                    st.nextToken();
                    while(st.hasMoreTokens())
                    {
                        attributes++;
                        st.nextToken();
                    }
                    
                    line = br.readLine();
                    
                    
                    while (line != null)
                    {
                        st = new StringTokenizer(line,",; \t");
                        classes = Integer.parseInt(st.nextToken());
                        
                        line = br.readLine();
                    }
                    
                    String name = f.getName().substring(0,f.getName().length()-5);
                    String newName = name+".arff";
                    new File(outDir+File.separator+newName).createNewFile();
                    BufferedWriter bw = new BufferedWriter(new FileWriter(outDir+File.separator+newName));
                    
                    bw.write("@relation "+name); bw.newLine();
                    for (int i=0; i<attributes; i++)
                    {
                        bw.write("@attribute a"+i+" real");
                        bw.newLine();
                    }
                    
                    bw.write("@attribute class {0");
                    for (int i=1; i<=classes; i++)
                    {
                        bw.write(","+i);
                    }
                    bw.write("}");
                    bw.newLine();
                    bw.write("@data");
                    bw.newLine();
                    
                    br = new BufferedReader(new FileReader(f));
                    line = br.readLine();
                    if (line.equals("subspaces"))
                    {
                        while(!line.equals("objects"))
                        {
                            line = br.readLine();
                        }
                        
                        line = br.readLine(); 
                    }
                    while (line != null)
                    {
                        st = new StringTokenizer(line, ",; \t");
                        String cl = st.nextToken();
                        
                        while (st.hasMoreTokens())
                        {
                            bw.write(st.nextToken()+",");
                        }
                        bw.write(cl);
                        bw.newLine();
                        line = br.readLine();
                    }
                    
                    bw.flush();
                    bw.close();
                    br.close();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

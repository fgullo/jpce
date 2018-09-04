

package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.StringTokenizer;

public class NewClass
{
    public static void main(String[] args)
    {
        try
        {
            generateSparseMatrix();


            int size = 16180;
            FileInputStream fis1=new FileInputStream("raw_datasets"+File.separator+"20News_full_sparse.data");
            BufferedReader br1=new BufferedReader(new InputStreamReader(fis1));

            FileInputStream fis2=new FileInputStream("raw_datasets"+File.separator+"20News_full_RefPart.data");
            BufferedReader br2=new BufferedReader(new InputStreamReader(fis2));

            int[] classes = new int[size];
            for (int i=0; i<classes.length; i++)
            {
                classes[i] = -1;
            }
            int c = 0;
            String line = br2.readLine();
            do
            {
                StringTokenizer st = new StringTokenizer(line, "; ");
                while(st.hasMoreTokens())
                {
                    int x = Integer.parseInt(st.nextToken());
                    classes[x] = c;
                }
                line = br2.readLine();
                c++;
            }
            while(line != null);

            System.out.println("tot (it should be equal to 20) = "+c);

            for (int i=0; i<classes.length; i++)
            {
                if(classes[i] == -1)
                {
                    throw new RuntimeException("ERROR---i="+i);
                }
            }


            File file = File.createTempFile("20News", ".data", new File("raw_datasets"+File.separator));
            FileOutputStream fos = new FileOutputStream(file, true);
            PrintStream ps = new PrintStream(fos);

            line = br1.readLine();
            int x = 0;
            do
            {
                ps.print(classes[x]+";");
                ps.println(line);
                line = br1.readLine();
                x++;
            }
            while(line != null);

            System.out.println("tot (it should be equal to "+(size)+")="+x);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected static void generateSparseMatrix()
    {
        try
        {
            FileInputStream fis=new FileInputStream("raw_datasets"+File.separator+"20News_full.data");
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));

            File file = new File("raw_datasets"+File.separator+"20News_full_sparse.data");
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file, true);
            PrintStream ps = new PrintStream(fos);

            String line = br.readLine();
            StringTokenizer st = new StringTokenizer(line, " ");
            st.nextToken();
            int size = Integer.parseInt(st.nextToken());

            line = br.readLine();
            while (line != null)
            {
                st = new StringTokenizer(line, " ");
                double[] d = new double[size];
                while(st.hasMoreTokens())
                {
                    int i = Integer.parseInt(st.nextToken())-1;
                    double x = Double.parseDouble(st.nextToken());
                    d[i] = x;
                }

                for (int j=0; j<d.length; j++)
                {
                    ps.print(d[j]);
                    ps.print(";");
                }
                ps.println();

                line = br.readLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

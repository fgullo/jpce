package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.StringTokenizer;

public class FinalSummary
{


    public static void main(String[] args)
    {
        int theta = 0;
        int upsilon = 1;
        int dim1 = 2;

        int o = 0;
        int f = 1;
        int of = 2;
        int dim2 = 3;

        int fm = 0;
        int entropy = 1;
        int dim3 = 2;

        String outputPath = "results"+File.separator;
        String[] methods = {"CO-CE-PCE","MOEA-PCE","EM-PCE","CB-PCE","FCB-PCE"};

        //String[][][] names = new String[dim1][dim2][dim3];
        String[][][] names = new String[][][]{{{"Theta_o (FM) (summary)","Theta_o (ENTROPY) (summary)"},{"Theta_f (FM) (summary)","Theta_f (ENTROPY) (summary)"},{"Theta_{of} (FM) (summary)","Theta_{of} (ENTROPY) (summary)"}},{{"Upsilon_o (FM) (summary)","Upsilon_o (ENTROPY) (summary)"},{"Upsilon_f (FM) (summary)","Upsilon_f (ENTROPY) (summary)"},{"Upsilon_{of} (FM) (summary)","Upsilon_{of} (ENTROPY) (summary)"}}};
        String timeName = "Execution times";
        String[][][] paths = new String[dim1][dim2][dim3];
        String timePath = "";
        long[][][] lastUpdates = new long[dim1][dim2][dim3];
        for (int i=0; i<lastUpdates.length; i++)
        {
            for (int j=0; j<lastUpdates[i].length; j++)
            {
                for (int k=0; k<lastUpdates[i][j].length; k++)
                {
                    lastUpdates[i][j][k] = Long.MIN_VALUE;
                }
            }
        }
        long timeLastUpdate = Long.MIN_VALUE;


        try
        {
            //retrieve filePaths;
            File folder = new File(outputPath);
            String[] files = folder.list();

            for (int i=0; i<files.length; i++)
            {
                boolean stop = false;
                int ix=-1, iy=-1, iz=-1;
                for (int x=0; x<names.length && !stop; x++)
                {
                    for (int y=0; y<names[0].length && !stop; y++)
                    {
                        for (int z=0; z<names[0][0].length && !stop; z++)
                        {
                            if (files[i].startsWith(timeName))
                            {
                                long l = new File(outputPath+files[i]).lastModified();
                                if (l > timeLastUpdate)
                                {
                                    timeLastUpdate = l;
                                    timePath = outputPath+files[i];
                                }
                                
                                x = names.length;
                                y = names[0].length;
                                z = names[0][0].length;
                            }
                            else if (files[i].contains(names[x][y][z]))
                            {
                                stop = true;
                                ix = x;
                                iy = y;
                                iz = z;
                            }
                        }
                    }
                }

                if (stop)
                {
                    File file = new File(outputPath+files[i]);
                    long lastUpdate = file.lastModified();
                    //long lastUpdate = new File(outputPath+files[i]).lastModified();
                    if (new File(outputPath+files[i]).lastModified() > lastUpdates[ix][iy][iz])
                    {
                        lastUpdates[ix][iy][iz] = lastUpdate;
                        paths[ix][iy][iz] = outputPath+files[i];
                    }
                }
            }

            PrintStream out = new PrintStream(new FileOutputStream(File.createTempFile("FinalSummaryHARD---", ".csv", new File(outputPath))));

            //HARD
            //FM
            //theta fm
            printHeadingTable(out, "THETA", "HARD", "FM", methods);
            printTable(out, paths[theta][of][fm], paths[theta][o][fm], paths[theta][f][fm], "HARD", methods.length);
            //upsilon fm
            printHeadingTable(out, "UPSILON", "", "", methods);
            printTable(out, paths[upsilon][of][fm], paths[upsilon][o][fm], paths[upsilon][f][fm], "HARD", methods.length);
            //theta entropy
            printHeadingTable(out, "THETA", "HARD", "ENTROPY", methods);
            printTable(out, paths[theta][of][entropy], paths[theta][o][entropy], paths[theta][f][entropy], "HARD", methods.length);
            //upsilon entropy
            printHeadingTable(out, "UPSILON", "", "", methods);
            printTable(out, paths[upsilon][of][entropy], paths[upsilon][o][entropy], paths[upsilon][f][entropy], "HARD", methods.length);
            //times
            printTimes(out, timePath, methods);

            //FUZZY
            out = new PrintStream(new FileOutputStream(File.createTempFile("FinalSummaryFUZZY---", ".csv", new File(outputPath))));
            //theta fm
            printHeadingTable(out, "THETA", "FUZZY", "FM", methods);
            printTable(out, paths[theta][of][fm], paths[theta][o][fm], paths[theta][f][fm], "FUZZY", methods.length);
            //upsilon fm
            printHeadingTable(out, "UPSILON", "", "", methods);
            printTable(out, paths[upsilon][of][fm], paths[upsilon][o][fm], paths[upsilon][f][fm], "FUZZY", methods.length);
            //theta entropy
            printHeadingTable(out, "THETA", "FUZZY", "ENTROPY", methods);
            printTable(out, paths[theta][of][entropy], paths[theta][o][entropy], paths[theta][f][entropy], "FUZZY", methods.length);
            //upsilon entropy
            printHeadingTable(out, "UPSILON", "", "", methods);
            printTable(out, paths[upsilon][of][entropy], paths[upsilon][o][entropy], paths[upsilon][f][entropy], "FUZZY", methods.length);
            //times
            printTimes(out, timePath, methods);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    protected static void printHeadingTable(PrintStream out, String measure, String hard, String entropy, String[] methods)
    {
        String filler = "";
        for (int i=1; i<=1+methods.length; i++)
        {
            filler += ";";
        }

        //first row
        out.println(hard+";");
        out.println();

        //second row
        out.println(entropy+";");
        out.println();

        //third row
        out.print("DATASET;");
        out.print(measure+"_of;");
        out.print(filler);
        out.print(";");
        out.print(measure+"_o;");
        out.print(filler);
        out.print(";");
        out.print(measure+"_f;");
        out.print(filler);
        out.print(";");
        out.println();

        //fourth row
        out.print(";");
        for (int i=1; i<=3; i++)
        {
            out.print("AVG ensemble;");
            out.print("MAX ensemble;");
            for (int j=0; j<methods.length; j++)
            {
                out.print(methods[j]);
                out.print(";");
            }
            out.print(";");
        }
        out.println();
    }

   protected static void printTable(PrintStream out, String pathof, String patho, String pathf, String hard, int nMethods)
   {
        try
        {
            BufferedReader brof=new BufferedReader(new InputStreamReader(new FileInputStream(pathof)));
            BufferedReader bro=new BufferedReader(new InputStreamReader(new FileInputStream(patho)));
            BufferedReader brf=new BufferedReader(new InputStreamReader(new FileInputStream(pathf)));

            //skip the first three lines
            String lineof="", lineo="", linef="";
            for (int i=1; i<=4; i++)
            {
                lineof = brof.readLine();
                lineo = bro.readLine();
                linef = brf.readLine();
            }

            int size = 1+((2+nMethods+1)*3);
            String[] toWrite = new String[size];
            while (lineof != null)
            {
                int pos = 0;
                StringTokenizer stof = new StringTokenizer(lineof,"; ");
                StringTokenizer sto = new StringTokenizer(lineo,"; ");
                StringTokenizer stf = new StringTokenizer(linef,"; ");
                
                toWrite[pos++] = stof.nextToken();
                sto.nextToken();
                stf.nextToken();
                
                if (hard.equals("HARD"))
                {
                    for (int i=1; i<=2+nMethods; i++)
                    {
                        String sof = stof.nextToken();
                        String so = sto.nextToken();
                        String sf = stf.nextToken();
                        String h = "";
                    }
                }
                
                for (int i=1; i<=2+nMethods; i++)
                {
                    toWrite[pos++] = stof.nextToken();
                }
                toWrite[pos++] = "";
                
                for (int i=1; i<=2+nMethods; i++)
                {
                    toWrite[pos++] = sto.nextToken();
                }
                toWrite[pos++] = "";
                
                for (int i=1; i<=2+nMethods; i++)
                {
                    toWrite[pos++] = stf.nextToken();
                }
                toWrite[pos++] = "";

                for (int h=0; h<toWrite.length; h++)
                {
                    out.print(toWrite[h]+";");
                }
                out.println();
                        
                lineof = brof.readLine();
                lineo = bro.readLine();
                linef = brf.readLine();
            }

            out.println();
            out.println();
            out.println();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
   }

   protected static void printTimes(PrintStream out, String path, String[] methods)
   {
        try
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path)));

            int nMethods = methods.length;

            String filler = "";
            for (int i=1; i<nMethods+1; i++)
            {
                filler += ";";
            }

            //print heading
            out.print(";");
            out.print("TOTAL;");
            out.print(filler);
            out.print("ONLINE;");
            out.print(filler);
            out.print("OFFLINE;");
            out.println(filler);

            out.print("DATASET;");
            for (int i=0; i<3; i++)
            {
                for (int j=0; j<nMethods; j++)
                {
                    out.print(methods[j]);
                    out.print(";");
                }
                out.print(";");
            }
            out.println();


            //print body
            //skip the first three lines
            String line="";
            for (int i=1; i<=4; i++)
            {
                line = br.readLine();
            }

            int size = 1+3*nMethods;
            String[] toWrite = new String[size];
            while (line != null)
            {
                int pos = 0;
                StringTokenizer st = new StringTokenizer(line,"; ");

                for (int i=0; i<toWrite.length; i++)
                {
                    toWrite[i] = st.nextToken();
                }

                out.print(toWrite[0]+";");
                for(int x=0; x<3; x++)//total, online, offline
                {
                    for(int y=0; y<nMethods; y++)
                    {
                        int z = 1+3*y+Math.abs(x-2);
                        out.print(toWrite[z]+";");
                    }
                    out.print(";");
                }
                out.println();

                line = br.readLine();
            }
            out.println();
            out.println();
            out.println();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
   }
}

package dataset;

import dataset.loading.DataLoader;
import evaluation.numericalinstance.MinkowskiNumericalInstanceSim;
import evaluation.numericalinstance.NumericalInstanceSimilarity;
import evaluation.pdf.PDFSimilarity;
import evaluation.Similarity;
import evaluation.cluster.features.ProjectiveClusterFeaturesSimilarity;
import evaluation.cluster.objectsfeatures.ProjectiveClusterObjectsFeaturesSimilarity;
import evaluation.cluster.objects.ProjectiveClusterObjectsSimilarity;
import evaluation.cluster.ProjectiveClusterSimilarity;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;
import objects.centroid.NumericalInstanceCentroidComputationAVG;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import util.Util;
import java.util.HashMap;

public class ProjectiveClusteringDataset extends Dataset
{
    protected double[][] clusterObjectsSimMatrix;
    protected double[][] clusterFeaturesSimMatrix;
    protected double[][] clusterObjectsFeaturesSimMatrix;
    
    protected double[][] instanceSimMatrix;
    
    protected ProjectiveClustering projectiveRefPartition;
    protected ProjectiveClustering projectiveRefPartitionHard;
    
    protected ProjectiveClustering[] dataHard;

    protected double[][] clusterObjectsDistMatrix;
    protected double[][] clusterFeaturesDistMatrix;
    protected double[][] clusterObjectsFeaturesDistMatrix;
    protected long clusterObjectsFeaturesDistMatrixTime;
    
    protected double[][] objectByFeatureMatrix;
    
    protected double[][] coNonOccurrenceObjectMatrix;
    protected long coNonOccurrenceObjectMatrixTime;

    protected double[][] instanceDistMatrix;

    //protected Object[][] clusterDataFeatureMatrix;
    
    protected Double[][] allClusterByObjectRepresentationMatrix;
    protected Double[][] allClusterByFeatureRepresentationMatrix;

    protected Object[][] instanceDataFeatureMatrix;

    //protected double diversity;

    //protected double[][] coOccurrenceMatrix;

    protected ProjectiveClusterObjectsSimilarity clusterObjectsSim;
    protected ProjectiveClusterFeaturesSimilarity clusterFeaturesSim;
    protected ProjectiveClusterObjectsFeaturesSimilarity clusterObjectsFeaturesSim;

    protected Similarity instanceSim;
    protected PDFSimilarity pdfSim;

    //protected ClusteringSimilarity diversityMeasure;

    protected ProjectiveCluster[] allClusters;
    protected int allClustersSize = -1;
    protected long buildAllClustersTime = 0;
    
    //protected WeightingScheme weightingScheme;

    public ProjectiveClusteringDataset (Instance[] data, Clustering refPartition,Double[][] subspaces)
    {
        this.data=data;
        this.refPartition=refPartition;
        //buildClusters();
        if (subspaces == null)
        {
            computeProjectiveRefPartitionLAC();
        }
        else
        {
            computeProjectiveRefPartitionSubspaces(subspaces);
        }
    }

    public ProjectiveClusteringDataset (DataLoader dl)
    {
        Object[] inst = dl.load();
        this.data = (Instance[])inst[0];
        this.refPartition = (Clustering)inst[1];
        Double[][] subspaces = null;
        if (inst.length > 2)
        {
            subspaces = (Double[][])inst[2];
        }
        //buildClusters();
        if (subspaces == null)
        {
            computeProjectiveRefPartitionLAC();
        }
        else
        {
            computeProjectiveRefPartitionSubspaces(subspaces);
        }
    }

    /*
    public Object[][] getClusterDataFeatureMatrix ()
    {
        if (this.clusterDataFeatureMatrix == null)
        {
            this.clusterDataFeatureMatrix = new Object[this.getNumberOfAllClusters()][this.getNumberOfInstances()];

            int indexRow=0;
            for(int i=0; i<data.length; i++)
            {
                Cluster [] CL = ((Clustering)data[i]).getClusters();
                for(int j=0; j<CL.length; j++)
                {
                    clusterDataFeatureMatrix [indexRow]=CL[j].getFeatureVectorRepresentation();
                    indexRow++;
                }
            }
        }
    

        return clusterDataFeatureMatrix;
    }
     */

    public long getCoNonOccurrenceObjectMatrixTime()
    {
        return coNonOccurrenceObjectMatrixTime;
    }
    
    public Double[][] getAllClusterByObjectRepresentationMatrix()
    {
        if (this.allClusterByObjectRepresentationMatrix == null)
        {
            this.allClusterByObjectRepresentationMatrix = new Double[this.getNumberOfAllClusters()][this.getNumberOfInstances()];

            int indexRow=0;
            for(int i=0; i<data.length; i++)
            {
                ProjectiveCluster [] CL = ((ProjectiveClustering)data[i]).getClusters();
                for(int j=0; j<CL.length; j++)
                {
                    allClusterByObjectRepresentationMatrix[indexRow]=CL[j].getFeatureVectorRepresentationDouble();
                    indexRow++;
                }
            }
        }
    

        return allClusterByObjectRepresentationMatrix;        
    }
    
    public Double[][] getAllClusterByfeatureRepresentationMatrix()
    {
        if (this.allClusterByFeatureRepresentationMatrix == null)
        {
            this.allClusterByFeatureRepresentationMatrix = new Double[this.getNumberOfAllClusters()][this.getNumberOfFeaturesInEachCluster()];

            int indexRow=0;
            for(int i=0; i<data.length; i++)
            {
                ProjectiveCluster [] CL = ((ProjectiveClustering)data[i]).getClusters();
                for(int j=0; j<CL.length; j++)
                {
                    allClusterByFeatureRepresentationMatrix[indexRow]=CL[j].getFeatureToClusterAssignments();
                    indexRow++;
                }
            }
        }
    

        return allClusterByFeatureRepresentationMatrix;        
    }
    

    public Object[][] getInstanceDataFeatureMatrix ()
    {
        if (this.instanceDataFeatureMatrix == null)
        {
            Instance[] instArray = ((Clustering)data[0]).getInstances();
            int features = instArray[0].getNumberOfFeatures();

            this.instanceDataFeatureMatrix = new Object[this.getNumberOfInstances()][features];

            for (int i=0; i<instArray.length; i++)
            {
                this.instanceDataFeatureMatrix[instArray[i].getID()] = instArray[i].getFeatureVectorRepresentation();
            }
        }

        return instanceDataFeatureMatrix;
    }

    public double[][] getClusterObjectsDistMatrix (ProjectiveClusterObjectsSimilarity sim)
    {
        if (this.clusterObjectsDistMatrix == null)
        {
            clusterObjectsDistMatrix = new double[this.getNumberOfAllClusters()][this.getNumberOfAllClusters()];
            if (this.clusterObjectsSim == null || !this.clusterObjectsSim.equals(sim))
            {
                this.clusterObjectsSim = sim;
            }

            for (int x=0; x<clusterObjectsDistMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterObjectsDistMatrix[x].length; y++)
                {
                    clusterObjectsDistMatrix[x][y] = sim.getDistance(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterObjectsDistMatrix[x][y]) || Double.isNaN(clusterObjectsDistMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }
        else if (this.clusterObjectsSim == null || !this.clusterObjectsSim.equals(sim))
        {
            this.clusterObjectsSim = sim;

            for (int x=0; x<clusterObjectsDistMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterObjectsDistMatrix[x].length; y++)
                {
                    clusterObjectsDistMatrix[x][y] = sim.getDistance(getClusters()[x], getClusters()[y]);
                }
            }
        }

        return clusterObjectsDistMatrix;
    }

    public double[][] getClusterObjectsSimMatrix (ProjectiveClusterObjectsSimilarity sim)
    {
        if (this.clusterObjectsSimMatrix == null)
        {
            clusterObjectsSimMatrix = new double[this.getNumberOfAllClusters()][this.getNumberOfAllClusters()];
            if (this.clusterObjectsSim == null || !this.clusterObjectsSim.equals(sim))
            {
                this.clusterObjectsSim = sim;
            }

            for (int x=0; x<clusterObjectsSimMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterObjectsSimMatrix[x].length; y++)
                {
                    clusterObjectsSimMatrix[x][y] = sim.getSimilarity(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterObjectsSimMatrix[x][y]) || Double.isNaN(clusterObjectsSimMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }
        else if (this.clusterObjectsSim == null || !this.clusterObjectsSim.equals(sim))
        {
            this.clusterObjectsSim = sim;

            for (int x=0; x<clusterObjectsSimMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterObjectsSimMatrix[x].length; y++)
                {
                    clusterObjectsSimMatrix[x][y] = sim.getSimilarity(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterObjectsSimMatrix[x][y]) || Double.isNaN(clusterObjectsSimMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }

        return clusterObjectsSimMatrix;
    }
    
    
    public double[][] getClusterFeaturesDistMatrix (ProjectiveClusterFeaturesSimilarity sim)
    {
        if (this.clusterFeaturesDistMatrix == null)
        {
            clusterFeaturesDistMatrix = new double[this.getNumberOfAllClusters()][this.getNumberOfAllClusters()];
            if (this.clusterFeaturesSim == null || !this.clusterFeaturesSim.equals(sim))
            {
                this.clusterFeaturesSim = sim;
            }

            for (int x=0; x<clusterFeaturesDistMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterFeaturesDistMatrix[x].length; y++)
                {
                    clusterFeaturesDistMatrix[x][y] = sim.getDistance(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterFeaturesDistMatrix[x][y]) || Double.isNaN(clusterFeaturesDistMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }
        else if (this.clusterFeaturesSim == null || !this.clusterFeaturesSim.equals(sim))
        {
            this.clusterFeaturesSim = sim;

            for (int x=0; x<clusterFeaturesDistMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterFeaturesDistMatrix[x].length; y++)
                {
                    clusterFeaturesDistMatrix[x][y] = sim.getDistance(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterFeaturesDistMatrix[x][y]) || Double.isNaN(clusterFeaturesDistMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }

        return clusterFeaturesDistMatrix;
    }

    public double[][] getClusterFeaturesSimMatrix (ProjectiveClusterFeaturesSimilarity sim)
    {
        if (this.clusterFeaturesSimMatrix == null)
        {
            clusterFeaturesSimMatrix = new double[this.getNumberOfAllClusters()][this.getNumberOfAllClusters()];
            if (this.clusterFeaturesSim == null || !this.clusterFeaturesSim.equals(sim))
            {
                this.clusterFeaturesSim = sim;
            }

            for (int x=0; x<clusterFeaturesSimMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterFeaturesSimMatrix[x].length; y++)
                {
                    clusterFeaturesSimMatrix[x][y] = sim.getSimilarity(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterFeaturesSimMatrix[x][y]) || Double.isNaN(clusterFeaturesSimMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }
        else if (this.clusterFeaturesSim == null || !this.clusterFeaturesSim.equals(sim))
        {
            this.clusterFeaturesSim = sim;

            for (int x=0; x<clusterFeaturesSimMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterFeaturesSimMatrix[x].length; y++)
                {
                    clusterFeaturesSimMatrix[x][y] = sim.getSimilarity(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterFeaturesSimMatrix[x][y]) || Double.isNaN(clusterFeaturesSimMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }

        return clusterFeaturesSimMatrix;
    }
    
    
    
    public double[][] getClusterObjectsFeaturesDistMatrix (ProjectiveClusterObjectsFeaturesSimilarity sim)
    {
        if (this.clusterObjectsFeaturesDistMatrix == null)
        {
            long timeToAdd = (this.allClusters==null)?0:this.buildAllClustersTime;

            long start = System.currentTimeMillis();
            clusterObjectsFeaturesDistMatrix = new double[this.getNumberOfAllClusters()][this.getNumberOfAllClusters()];
            if (this.clusterObjectsFeaturesSim == null || !this.clusterObjectsFeaturesSim.equals(sim))
            {
                this.clusterObjectsFeaturesSim = sim;
            }

            for (int x=0; x<clusterObjectsFeaturesDistMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterObjectsFeaturesDistMatrix[x].length; y++)
                {
                    clusterObjectsFeaturesDistMatrix[x][y] = sim.getDistance(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterObjectsFeaturesDistMatrix[x][y]) || Double.isNaN(clusterObjectsFeaturesDistMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
            this.clusterObjectsFeaturesDistMatrixTime = System.currentTimeMillis()-start+timeToAdd;
        }
        else if (this.clusterObjectsFeaturesSim == null || !this.clusterObjectsFeaturesSim.equals(sim))
        {
            long timeToAdd = (this.allClusters==null)?0:this.buildAllClustersTime;

            long start = System.currentTimeMillis();
            this.clusterObjectsFeaturesSim = sim;

            for (int x=0; x<clusterObjectsFeaturesDistMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterObjectsFeaturesDistMatrix[x].length; y++)
                {
                    clusterObjectsFeaturesDistMatrix[x][y] = sim.getDistance(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterObjectsFeaturesDistMatrix[x][y]) || Double.isNaN(clusterObjectsFeaturesDistMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
            this.clusterObjectsFeaturesDistMatrixTime = System.currentTimeMillis()-start+timeToAdd;
        }

        return clusterObjectsFeaturesDistMatrix;
    }

    public long getClusterObjectsFeaturesDistMatrixTime()
    {
        return clusterObjectsFeaturesDistMatrixTime;
    }

    public double[][] getClusterObjectsFeaturesSimMatrix (ProjectiveClusterObjectsFeaturesSimilarity sim)
    {
        if (this.clusterObjectsFeaturesSimMatrix == null)
        {
            clusterObjectsFeaturesSimMatrix = new double[this.getNumberOfAllClusters()][this.getNumberOfAllClusters()];
            if (this.clusterObjectsFeaturesSim == null || !this.clusterObjectsFeaturesSim.equals(sim))
            {
                this.clusterObjectsFeaturesSim = sim;
            }

            for (int x=0; x<clusterObjectsFeaturesSimMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterObjectsFeaturesSimMatrix[x].length; y++)
                {
                    clusterObjectsFeaturesSimMatrix[x][y] = sim.getSimilarity(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterObjectsFeaturesSimMatrix[x][y]) || Double.isNaN(clusterObjectsFeaturesSimMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }
        else if (this.clusterObjectsFeaturesSim == null || !this.clusterObjectsFeaturesSim.equals(sim))
        {
            this.clusterObjectsFeaturesSim = sim;

            for (int x=0; x<clusterObjectsFeaturesSimMatrix.length-1; x++)
            {
                for (int y=x+1; y<clusterObjectsFeaturesSimMatrix[x].length; y++)
                {
                    clusterObjectsFeaturesSimMatrix[x][y] = sim.getSimilarity(getClusters()[x], getClusters()[y]);
                    if (Double.isInfinite(clusterObjectsFeaturesSimMatrix[x][y]) || Double.isNaN(clusterObjectsFeaturesSimMatrix[x][y]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }

        return clusterObjectsFeaturesSimMatrix;
    }


    public double[][] getInstanceDistMatrix (Similarity sim)
    {
        if (this.instanceDistMatrix == null)
        {
            instanceDistMatrix = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
            if (this.instanceSim == null || !this.instanceSim.equals(sim))
            {
                this.instanceSim = sim;
            }

            Instance[] instances = ((Clustering)data[0]).getInstances();

            for (int i=0; i<instanceDistMatrix.length-1; i++)
            {
                for (int j=i+1; j<instanceDistMatrix[i].length; j++)
                {
                    instanceDistMatrix[i][j] = sim.getDistance(instances[i], instances[j]);
                    if (Double.isInfinite(instanceDistMatrix[i][j]) || Double.isNaN(instanceDistMatrix[i][j]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }
        else if (this.instanceSim == null || !this.instanceSim.equals(sim))
        {
            this.instanceSim = sim;

            Instance[] instances = ((Clustering)data[0]).getInstances();

            for (int i=0; i<instanceDistMatrix.length-1; i++)
            {
                for (int j=i+1; j<instanceDistMatrix[i].length; j++)
                {
                    instanceDistMatrix[i][j] = sim.getDistance(instances[i], instances[j]);
                    if (Double.isInfinite(instanceDistMatrix[i][j]) || Double.isNaN(instanceDistMatrix[i][j]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }

        return instanceDistMatrix;
    }

    public double[][] getInstanceSimMatrix (Similarity sim)
    {
        if (this.instanceSimMatrix == null)
        {
            instanceSimMatrix = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
            if (this.instanceSim == null || !this.instanceSim.equals(sim))
            {
                this.instanceSim = sim;
            }

            Instance[] instances = ((Clustering)data[0]).getInstances();

            for (int i=0; i<instanceSimMatrix.length-1; i++)
            {
                for (int j=i+1; j<instanceSimMatrix[i].length; j++)
                {
                    instanceSimMatrix[i][j] = sim.getSimilarity(instances[i], instances[j]);
                    if (Double.isInfinite(instanceSimMatrix[i][j]) || Double.isNaN(instanceSimMatrix[i][j]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }
        else if (this.instanceSim == null || !this.instanceSim.equals(sim))
        {
            this.instanceSim = sim;

            Instance[] instances = ((Clustering)data[0]).getInstances();

            for (int i=0; i<instanceSimMatrix.length-1; i++)
            {
                for (int j=i+1; j<instanceSimMatrix[i].length; j++)
                {
                    instanceSimMatrix[i][j] = sim.getSimilarity(instances[i], instances[j]);
                    if (Double.isInfinite(instanceSimMatrix[i][j]) || Double.isNaN(instanceSimMatrix[i][j]))
                    {
                        throw new RuntimeException("ERROR: value NAN or INFINITY");
                    }
                }
            }
        }

        return instanceSimMatrix;
    }

    /*
    public double getEnsembleDiversity (ClusteringSimilarity div)
    {
        if (data.length == 0)
        {
            return 0.0;
        }

        double sum = 0.0;
        for (int i=0; i<this.data.length-1; i++)
        {
            for (int j=i+1; j<this.data.length; j++)
            {
                double s = div.getDistance(data[i], data[j]);
                if (s < 0.0)
                {
                    System.out.println("DISTANZA NEGATIVA!!!!!!!!!!!!!!!");
                    double t = div.getDistance(data[i], data[j]);
                }
                sum += div.getDistance(data[i], data[j]);
            }
        }

        double den = data.length*(data.length-1)/2;
        return sum/den;
    }
    */

    public double[][] getCoNonOccurrenceObjectMatrix (PDFSimilarity pdfSim)
    {

        if (this.coNonOccurrenceObjectMatrix == null || this.pdfSim==null ||!pdfSim.equals(this.pdfSim))
        {
            long start = System.currentTimeMillis();
            coNonOccurrenceObjectMatrix = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
            this.pdfSim = pdfSim;
            
            for (int k=0; k<data.length; k++)
            {
                //System.out.println("CO-(NON)OCCURRENCE OBJECT MATRIX: "+(k+1));
                ProjectiveClustering c = (ProjectiveClustering)data[k];
                ProjectiveCluster[] clusters = c.getClusters();

                for (int q1=0; q1<coNonOccurrenceObjectMatrix.length-1; q1++)
                {
                    Double[] vq1 = new Double[clusters.length];
                    for (int w=0; w<vq1.length; w++)
                    {
                        vq1[w] = ((Double)clusters[w].getFeatureVectorRepresentation()[q1]).doubleValue();
                    }

                    for (int q2=q1+1; q2<coNonOccurrenceObjectMatrix.length; q2++)
                    {       
                        Double[] vq2 = new Double[clusters.length];
                        for (int w=0; w<vq2.length; w++)
                        {
                            vq2[w] = ((Double)clusters[w].getFeatureVectorRepresentation()[q2]).doubleValue();
                        }

                        double val = this.pdfSim.getDistance(vq1, vq2);
                        if (clusters[0].isFuzzyObjectsAssignment())
                        {                    
                            Util.throwException(val, 0.0, 1.0);
                        }
                        else
                        {
                            Util.throwException(val, 0.0, Double.POSITIVE_INFINITY);
                        }


                        coNonOccurrenceObjectMatrix[q1][q2] += val/data.length;
                    }                
                }
            }
            this.coNonOccurrenceObjectMatrixTime = System.currentTimeMillis()-start;
        }
        
        return coNonOccurrenceObjectMatrix;
    }
        
    public double[][] getCoNonOccurrenceFeatureMatrix (PDFSimilarity pdfSim)
    {

        double[][] coOccurrenceMatrix2 = new double[this.getNumberOfFeaturesInEachCluster()][this.getNumberOfFeaturesInEachCluster()];        
        
        for (int k=0; k<data.length; k++)
        {
            //System.out.println("CO-(NON)OCCURRENCE FEATURE MATRIX: "+(k+1));
            ProjectiveClustering c = (ProjectiveClustering)data[k];
            ProjectiveCluster[] clusters = c.getClusters();
            
            for (int q1=0; q1<coOccurrenceMatrix2.length-1; q1++)
            {
                Double[] vq1 = new Double[clusters.length];
                for (int w=0; w<vq1.length; w++)
                {
                    vq1[w] = ((Double)clusters[w].getFeatureToClusterAssignments()[q1]).doubleValue();
                }
                
                for (int q2=q1+1; q2<coOccurrenceMatrix2.length; q2++)
                {       
                    Double[] vq2 = new Double[clusters.length];
                    for (int w=0; w<vq2.length; w++)
                    {
                        vq2[w] = ((Double)clusters[w].getFeatureToClusterAssignments()[q2]).doubleValue();
                    }
                    
                    double val = 0.0;
                    if (clusters[0].isFuzzyFeaturesAssignment())
                    {                    
                        val = pdfSim.getDistance(vq1, vq2);
                        if (Double.isInfinite(val) || Double.isNaN(val) || val < -0.0000001 || val > 1.0000001)
                        {
                            throw new RuntimeException("ERROR: val must be within [0,1]-----val="+val);
                        }
                    }
                    else
                    {
                        int count1 = 0;
                        int count2 = 0;
                        for (int ccc=0; ccc<vq1.length; ccc++)
                        {
                            val += vq1[ccc]*vq2[ccc];
                            if (vq1[ccc] == 1.0)
                            {
                                count1++;
                            }
                            if (vq2[ccc] == 1.0)
                            {
                                count2++;
                            }
                            
                            if (vq1[ccc] != 0.0 && vq1[ccc] != 1.0 && vq2[ccc] != 0.0 && vq2[ccc] != 1.0)
                            {
                                throw new RuntimeException("ERROR: vq1[ccc] and vq2[ccc] must be 0 or 1-----vq1[ccc]="+vq1[ccc]+",vq2[ccc]="+vq2[ccc]);
                            }
                        }
                        double den = count1*count1+count2*count2-val;
                        if (den != 0.0)
                        {
                            val /= (count1*count1+count2*count2-val);
                            val = 1-val;
                        }
                        else
                        {
                            val = 0.0;
                        }
                        
                        
                        if (Double.isInfinite(val) || Double.isNaN(val) || val < -0.0000001 || val > 1.0000001)
                        {
                            throw new RuntimeException("ERROR: val must be within [0,1]-----val="+val);
                        }
                    }
                    
                    coOccurrenceMatrix2[q1][q2] += ((new Double(1.0).doubleValue())/data.length)*val;
                }                
            }
        }
        
        return coOccurrenceMatrix2;
    }        
    
    public double[][] getWholeCoNonOccurrenceObjectMatrix (PDFSimilarity sim)
    {
        double [][] M = getCoNonOccurrenceObjectMatrix(sim);
        
        for (int i=0; i<M.length; i++)
        {
            for (int j=0; j<i; j++)
            {
                M[i][j] = M[j][i]; 
            }
        }

        return M;
    }
    
    public double[][] getWholeCoNonOccurrenceFeatureMatrix (PDFSimilarity sim)
    {
        double [][] M = getCoNonOccurrenceFeatureMatrix(sim);
        
        for (int i=0; i<M.length; i++)
        {
            for (int j=0; j<i; j++)
            {
                M[i][j] = M[j][i]; 
            }
        }

        return M;
    }
    
    public double[][] getObjectByFeatureMatrix()
    {
        if (this.objectByFeatureMatrix == null)
        {
            this.objectByFeatureMatrix = new double[this.getNumberOfInstances()][this.getNumberOfFeaturesInEachCluster()];
            
            for (int i=0; i<this.data.length; i++)
            {
                ProjectiveClustering sci = (ProjectiveClustering)this.data[i];
                ProjectiveCluster[] clusters = sci.getClusters();
                
                for (int j=0; j<clusters.length; j++)
                {
                    Double[] objects = (Double[])clusters[j].getFeatureVectorRepresentation();
                    Double[] features = clusters[j].getFeatureToClusterAssignments();
                    
                    for (int k1=0; k1<this.objectByFeatureMatrix.length; k1++)
                    {
                        for (int k2=0; k2<this.objectByFeatureMatrix[k1].length; k2++)
                        {
                            this.objectByFeatureMatrix[k1][k2] += (((double)1.0)/this.data.length)*objects[k1]*features[k2];
                            if (Double.isInfinite(this.objectByFeatureMatrix[k1][k2]) || Double.isNaN(this.objectByFeatureMatrix[k1][k2]))
                            {
                                throw new RuntimeException("ERROR: value NAN or INFINITY");
                            }
                            
                            if (objectByFeatureMatrix[k1][k2] < -0.0000000001 || this.objectByFeatureMatrix[k1][k2] > 1.0000000001)
                            {
                                throw new RuntimeException("ERROR: value must be within [0,1]");
                            }
                        }
                    }
                }
            }
        }
        
        return this.objectByFeatureMatrix;
    }
    
    /*
    public double[][] getWholeWeightedCoOccurrenceMatrix (WeightingScheme ws)
    {
        double [][] M = getWeightedCoOccurrenceMatrix(ws);
        
        for (int i=0; i<M.length; i++)
            for (int j=M.length-1; j>=0; j--)
                if(i != j)
                    M [j][i] = M [i][j];
                else
                    M[i][j] = 1;
        return M;

    }
     */
        
    /*
    public double[][] getWeightedCoOccurrenceMatrix (WeightingScheme ws)
    {

        double[][] coOccurrenceMatrix2 = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
        this.weightingScheme = ws;
        
        double[] weights = this.weightingScheme.weight(this);

        for (int k=0; k<data.length; k++)
        {
            Clustering c = (Clustering)data[k];
            Cluster[] clusters = c.getClusters();
            
            for (int l=0; l<clusters.length; l++)
            {
                Instance[] instances = clusters[l].getInstances();
                
                for (int i=0; i<instances.length-1; i++)
                {
                    for (int j=i+1; j<instances.length; j++)
                    {
                        int ID1 = instances[i].getID();
                        int ID2 = instances[j].getID();
                        
                        if (ID1 < ID2)
                        {
                            coOccurrenceMatrix2[ID1][ID2]+= weights[k]/data.length;
                        }
                        else
                        {
                            coOccurrenceMatrix2[ID2][ID1]+= weights[k]/data.length;
                        }
                    }
                }
            }
        }        
        
            
        return coOccurrenceMatrix2;
    }
    */

    /*
    protected boolean sameCluster(int i, int j, int k)
    {
        Clustering p = (Clustering)data[k];

        boolean bi = false;
        boolean bj = false;

        for (int z=0; z<p.getNumberOfClusters(); z++)
        {
            Cluster cz = p.getClusters()[z];
            for (int w=0; w<cz.getNumberOfInstances(); w++)
            {
                if (cz.getInstances()[w].getID() == i)
                {
                    bi = true;
                }
                if (cz.getInstances()[w].getID() == j)
                {
                    bj = true;
                }
            }

            if (bi && bj)
            {
                return true;
            }

            if ((bi && !bj) || (bj && !bi))
            {
                return false;
            }
        }

        return false;
    }
     */

    public int getNumberOfInstances ()
    {
        return ((ProjectiveClustering)data[0]).getNumberOfInstances();
    }

    public int getNumberOfAllClusters()
    {
        if (this.allClustersSize <= 0)
        {
            this.allClustersSize = 0;
            for (int i=0; i<this.data.length; i++)
            {
                allClustersSize += ((ProjectiveClustering)data[i]).getNumberOfClusters();
            }            
        }

        return this.allClustersSize;
    }
    
    public int getNumberOfClustersInEachClustering()
    {
        return ((ProjectiveClustering)this.getData()[0]).getNumberOfClusters();
    }

    public Instance[] getInstances()
    {
        return ((ProjectiveClustering)data[0]).getInstances();
    }

    public ProjectiveCluster[] getClusters()
    {
        if (this.allClusters == null)
        {
            buildClusters();
        }

        return allClusters;
    }

    public Dataset getInstancesDataset()
    {
        if (this.getInstances()[0] instanceof NumericalInstance)
        {
            return new NumericalInstanceDataset(this.getInstances(),null);
        }

        if (this.getInstances()[0] instanceof Cluster)
        {
            return new ClusterDataset(this.getInstances(),null);
        }

        if (this.getInstances()[0] instanceof Clustering)
        {
            return new ClusteringDataset(this.getInstances(),null);
        }
        
        if (this.getInstances()[0] instanceof ProjectiveCluster)
        {
            return new ProjectiveClusterDataset(this.getInstances(),null);
        }
        
        if (this.getInstances()[0] instanceof ProjectiveClustering)
        {
            return new ProjectiveClusteringDataset(this.getInstances(),null,null);
        }

        return null;
    }
    
    public Dataset getFeaturesDataset()
    {
        //restituisco un dataset fittizio (una feature è rappresentata da una numerical instance vuota)
        Instance[] features = new Instance[this.getNumberOfFeaturesInEachCluster()];
        for (int i=0; i<features.length; i++)
        {
            double[] v = new double[]{0.0};
            features[i] = new NumericalInstance(v, i);
        }
        
        return new NumericalInstanceDataset(features, null);
    }

    protected void buildClusters()
    {
        long start = System.currentTimeMillis();

        this.allClustersSize = 0;
        for (int i=0; i<this.data.length; i++)
        {
            this.allClustersSize += ((ProjectiveClustering)data[i]).getNumberOfClusters();
        }

        allClusters = new ProjectiveCluster[this.allClustersSize];
        int i=0;
        for (int j=0; j<data.length; j++)
        {
            ProjectiveCluster[] clustersJ = ((ProjectiveClustering)data[j]).getClusters();
            for (int k=0; k<clustersJ.length; k++)
            {
                allClusters[i] = clustersJ[k];
                i++;
            }
        }

        this.buildAllClustersTime = System.currentTimeMillis()-start;
    }

    public long getBuildAllClustersTime()
    {
        return buildAllClustersTime;
    }

    public int getNumberOfFeaturesInEachCluster()
    {
        return ((ProjectiveClustering)data[0]).getClusters()[0].getFeatureToClusterAssignments().length;
    }
    
    
    public double[][] getPairwiseFeatureDistancesBasedOnTheirFeatureRepresentations(NumericalInstanceSimilarity numSim)
    {
        double[][] m = this.getObjectByFeatureMatrix();
        
        double[][] dist = new double[this.getNumberOfFeaturesInEachCluster()][this.getNumberOfFeaturesInEachCluster()];
        
        for (int i=0; i<dist.length-1; i++)
        {
            double[] vi = new double[m.length];
            for (int y=0; y<vi.length; y++)
            {
                vi[y] = m[y][i];
            }
            NumericalInstance inst1 = new NumericalInstance(vi);
            
            for (int j=i+1; j<dist[i].length; j++)
            {
                double[] vj = new double[m.length];
                for (int y=0; y<vj.length; y++)
                {
                    vj[y] = m[y][j];
                }
                NumericalInstance inst2 = new NumericalInstance(vj);
                
                dist[i][j] = numSim.getDistance(inst1, inst2);
                dist[j][i] = dist[i][j];
                if (Double.isInfinite(dist[i][j]) || Double.isNaN(dist[i][j]))
                {
                    throw new RuntimeException("ERROR: value NAN or INFINITY");
                }
            }
        }
        
        return dist;
    }
        
    public double[][] getPairwiseObjectDistancesBasedOnTheirFeatureRepresentations(NumericalInstanceSimilarity numSim)
    {
        double[][] m = this.getObjectByFeatureMatrix();
        
        double[][] dist = new double[this.getNumberOfInstances()][this.getNumberOfInstances()];
        
        for (int i=0; i<dist.length-1; i++)
        {
            NumericalInstance inst1 = new NumericalInstance(m[i]);            
            for (int j=i+1; j<dist[i].length; j++)
            {
                NumericalInstance inst2 = new NumericalInstance(m[j]);
                
                dist[i][j] = numSim.getDistance(inst1, inst2);
                dist[j][i] = dist[i][j];
                if (Double.isInfinite(dist[i][j]) || Double.isNaN(dist[i][j]) || dist[i][j] < 0)
                {
                    throw new RuntimeException("ERROR: value NAN or INFINITY or LOWER THAN ZERO---dist[i][j]="+dist[i][j]);
                }
            }
        }
        
        return dist;
    }

    protected void computeProjectiveRefPartitionSubspaces(Double[][] subspaces)
    {
        if (this.refPartition != null && this.getInstances()[0] instanceof NumericalInstance)
        {
            if (subspaces.length != this.refPartition.getNumberOfClusters())
            {
                throw new RuntimeException("ERROR: the number of subspaces must be equal to the number of classes");
            }

            ProjectiveCluster[] projClust = new ProjectiveCluster[this.refPartition.getNumberOfClusters()];

            for (int k=0; k<projClust.length; k++)
            {
                Double[] objRep = new Double[this.getNumberOfInstances()];
                for (int i=0; i<objRep.length; i++)
                {
                    objRep[i] = 0.0;
                }

                Instance[] clusterK = this.refPartition.getClusters()[k].getInstances();
                for (int i=0; i<clusterK.length; i++)
                {
                    objRep[clusterK[i].getID()] = 1.0;
                }
                projClust[k] = new ProjectiveCluster(this.getInstances(),objRep,subspaces[k],k,false,false);
            }

            this.projectiveRefPartition = new ProjectiveClustering(projClust);
            this.projectiveRefPartitionHard = this.projectiveRefPartition;
        }
    }

    
    protected void computeProjectiveRefPartitionLAC()
    {
        if (this.refPartition != null && this.getInstances()[0] instanceof NumericalInstance)
        {
            double h = 0.2;
            
            Double[][] newFeatures = new Double[this.refPartition.getNumberOfClusters()][this.getNumberOfFeaturesInEachCluster()];
            Double[][] newObjects = new Double[this.refPartition.getNumberOfClusters()][this.getNumberOfInstances()];
            Cluster[] refClusters = this.refPartition.getClusters();
            ProjectiveCluster[] newClusters = new ProjectiveCluster[refClusters.length];
            
            for (int j=0; j<refClusters.length; j++)
            {
                Instance[] refInstances = refClusters[j].getInstances();

                Double[] refObjects = refClusters[j].getFeatureVectorRepresentationDouble();
                for (int l=0; l<newObjects[0].length; l++)
                {
                    newObjects[j][l] = new Double(refObjects[l].doubleValue());
                }

                Instance centroid = new NumericalInstanceCentroidComputationAVG().getCentroid(refInstances);
                Double[] dataVectorCentroid = ((NumericalInstance)centroid).getDataVector();
                //Similarity s = new MinkowskiNumericalInstanceSim(2);

                double sum = 0.0;
                double[] tmp = new double[newFeatures[j].length];
                double tmpSum = 0.0;
                for (int i=0; i<newFeatures[j].length; i++)
                {
                    double xji = 0.0;
                    for (int z=0; z<refInstances.length; z++)
                    {
                        Double[] dataVector = ((NumericalInstance)refInstances[z]).getDataVector();
                        xji += (dataVector[i]-dataVectorCentroid[i])*(dataVector[i]-dataVectorCentroid[i]);
                    }

                    
                    double newValue1 = -xji/h;
                    tmp[i] = xji;
                    tmpSum += tmp[i];
                    double newValue2 = Math.exp(newValue1);
                    newFeatures[j][i] = newValue2;
                    sum += newFeatures[j][i];
                }

               if (sum == 0.0)
                {
                   double tmpSum2 = 0.0; 
                   for (int i=0; i<newFeatures[j].length; i++)
                    {
                        newFeatures[j][i] = Math.exp(-tmp[i]/(h*tmpSum));
                        tmpSum2 += newFeatures[j][i];
                    }
                   
                   sum = tmpSum2;
                }
                
                if (sum == 0.0)
                {
                    throw new RuntimeException("COME è POSSIBILE?????????????");
                }
                
                double sumCheck = 0.0;
                for (int i=0; i<newFeatures[j].length; i++)
                {
                    newFeatures[j][i] /= sum;

                    if (Double.isInfinite(newFeatures[j][i]) || Double.isNaN(newFeatures[j][i]) || newFeatures[j][i]<-0.000000001 || newFeatures[j][i]>1.00000001)
                    {
                        throw new RuntimeException("ERROR: newFeatures[j][i] must be within [0,1]---newFeatures[j][i]="+newFeatures[j][i]);
                    }

                    sumCheck += newFeatures[j][i];
                }                    

                if (Double.isInfinite(sumCheck) || Double.isNaN(sumCheck) || sumCheck<0.999999999 || sumCheck>1.0000000001)
                {
                        throw new RuntimeException("ERROR: sumCheck must be equal to 1---sumCheck="+sumCheck);                
                }
            }

            for (int i=0; i<newClusters.length; i++)
            {
                newClusters[i] = new ProjectiveCluster(this.getInstances(), newObjects[i], newFeatures[i], refClusters[i].getID(), false, true);
            }
            
            /*
            for (int i=0; i<refClusters.length; i++)
            {
                Instance[] refInstances = refClusters[i].getInstances();
                
                Double[] refObjects = refClusters[i].getFeatureVectorRepresentationDouble();
                for (int j=0; j<newObjects[0].length; j++)
                {
                    newObjects[i][j] = new Double(refObjects[j].doubleValue());
                }
                
                Instance centroid = new NumericalInstanceCentroidComputationAVG().getCentroid(refInstances);
                Similarity s = new MinkowskiNumericalInstanceSim(2);
                double xij = 0.0;
                for (int k=0; k<refInstances.length; k++)
                {
                    xij += s.getDistance(refInstances[k], centroid);
                }
                xij /= refInstances.length;
                
                double sum = 0.0;
                for (int l=0; l<newFeatures[0].length; l++)
                {
                    newFeatures[i][l] = Math.exp(-xij/h);
                    sum += newFeatures[i][l];
                }
                for (int l=0; l<newFeatures[0].length; l++)
                {
                    newFeatures[i][l] /= sum;
                }
            }
            
            for (int l=0; l<newFeatures[0].length; l++)
            {
                double sum = 0.0;
                for (int i=0; i<newFeatures.length; i++)
                {
                    sum += newFeatures[i][l];
                }
                
                if (sum > 0.0)
                {
                    for (int i=0; i<newFeatures.length; i++)
                    {
                        newFeatures[i][l] /= sum;
                    }
                }
            }
            */
            
            for (int i=0; i<newClusters.length; i++)
            {
                newClusters[i] = new ProjectiveCluster(this.getInstances(), newObjects[i], newFeatures[i], refClusters[i].getID(), false, true);
            }
            
            this.projectiveRefPartition = new ProjectiveClustering(newClusters, this.refPartition.getID());
        }
    }
    
    public ProjectiveClustering getProjectiveRefPartition()
    {
        return this.projectiveRefPartition;
    }

    public ProjectiveClustering getProjectiveRefPartitionHard()
    {
        if (this.projectiveRefPartitionHard == null)
        {
            this.projectiveRefPartitionHard = this.projectiveRefPartition.hardenizeObjectAndFeaturePartitioning();
        }

        return this.projectiveRefPartitionHard;
    }
    
    public int getAvgNUmberOfFeaturesInProjectiveRefPartition()
    {
        ProjectiveClustering rp = this.getProjectiveRefPartitionHard();
        
        double count = 0.0;
        for (ProjectiveCluster pc:rp.getClusters())
        {
            count += pc.getSumOfFeatureAssignments();
        }
        
        count /= rp.getNumberOfClusters();
        
        return Math.min(rp.getNumberOfFeaturesInProjectiveClusters(),(int)Math.round(count));
    }        
            

    /*
    protected boolean same_cluster(Cluster cluster, Instance inst1, Instance inst2){
        boolean present_inst1=false;
        boolean present_inst2=false;
        Instance[] instances = cluster.getInstances();
        for(int i=0; i<instances.length; i++){
            if(instances[i].equals(inst1))
                present_inst1=true;
            if(instances[i].equals(inst2))
                present_inst2=true;
        }
        return (present_inst1 && present_inst2);
    }
    */
    
    public int size()
    {
        return this.data.length;
    }
    
    public ProjectiveClustering[] getDataHard()
    {
        if (this.dataHard == null)
        {
            this.dataHard = new ProjectiveClustering[this.data.length];
            
            for (int i=0; i<this.dataHard.length; i++)
            {
                this.dataHard[i] = ((ProjectiveClustering)this.data[i]).hardenizeObjectAndFeaturePartitioning();
            }
        }
        
        return this.dataHard;
    }
    
    public HashMap<Integer, Integer>[] getMapping()
    {
        HashMap<Integer, Integer>[] mapping = new HashMap[this.getDataLength()];
        int id = 0;
        Instance[] clusterings = this.getData();
        for (int a=0; a<mapping.length; a++)
        {
            ProjectiveClustering C = (ProjectiveClustering)clusterings[a];
            HashMap<Integer,Integer> map = new HashMap<Integer, Integer>(C.getNumberOfClusters());
            for (int b=0; b<C.getClusters().length; b++)
            {
                map.put(b, id);
                id++;
            }
            mapping[a] = map;
        }
        
        int n = this.getNumberOfAllClusters();
        if (id != n)
        {
            throw new RuntimeException("ERROR: these two numbers must coincide---id="+id+", n="+n);
        }
        
        return mapping;
    }
    
    public double[][] allClusterPairwiseDistancesBasedOnMapping(Similarity sim, HashMap<Integer, Integer>[] mapping) 
    {
        int n = this.getNumberOfAllClusters();
        double[][] distMatrix = new double[n][n];
        
        Instance[] clusterings = this.getData();
        for (int a1=0; a1<clusterings.length; a1++)
        {
            ProjectiveClustering C1 = (ProjectiveClustering)clusterings[a1];
            ProjectiveCluster[] clusters1 = C1.getClusters();
            for (int b1=0; b1<C1.getNumberOfClusters(); b1++)
            {
                ProjectiveCluster c1 = clusters1[b1];
                int id1 = mapping[a1].get(b1);
                
                for (int b2=b1+1; b2<clusters1.length; b2++)
                {
                    ProjectiveCluster c2 = clusters1[b2];
                    int id2 = mapping[a1].get(b2);
                    distMatrix[id1][id2] = sim.getDistance(c1, c2);
                    distMatrix[id2][id1] = distMatrix[id1][id2];
                }
                
                for (int a2 = a1+1; a2<clusterings.length; a2++)
                {
                    ProjectiveClustering C2 = (ProjectiveClustering)clusterings[a2];
                    ProjectiveCluster[] clusters2 = C2.getClusters();
                    for (int b2=0; b2<C2.getNumberOfClusters(); b2++)
                    {
                        ProjectiveCluster c2 = clusters2[b2];
                        int id2 = mapping[a2].get(b2);
                        distMatrix[id1][id2] = sim.getDistance(c1, c2);
                        distMatrix[id2][id1] = distMatrix[id1][id2]; 
                    } 
                }
                
            }
        }
        
        return distMatrix;
    }
    
}

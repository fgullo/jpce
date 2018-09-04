/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pce.enhancedsingleobjective;

import clustering.FuzzyKMedoids;
import dataset.ProjectiveClusteringDataset;
import evaluation.numericalinstance.NumericalInstanceSimilarity;
import evaluation.pdf.PDFSimilarity;
import evaluation.numericalinstance.SquaredEuclideanNumericalInstanceSim;
import evaluation.pdf.SquaredEuclideanPDFSim;
import objects.FuzzyCluster;
import objects.FuzzyClustering;
import objects.ProjectiveCluster;
import objects.ProjectiveClustering;
import pce.InstanceBasedPCEMethod;


public class OneObjectiveInstanceBasedPCE_EUCLIDEAN extends InstanceBasedPCEMethod
{
    protected double[][] pairwiseObjectDistances;
    protected double[][] coNonOccurrenceObjectMatrix;

    protected PDFSimilarity pdfSim;
    protected NumericalInstanceSimilarity numSim;

    protected ProjectiveClustering result;

    public OneObjectiveInstanceBasedPCE_EUCLIDEAN (ProjectiveClusteringDataset ensemble)
    {
        this.ensemble = ensemble;
        this.pdfSim = new SquaredEuclideanPDFSim();
        this.numSim = new SquaredEuclideanNumericalInstanceSim();

        long start = System.currentTimeMillis();
        this.coNonOccurrenceObjectMatrix = this.ensemble.getCoNonOccurrenceObjectMatrix(this.pdfSim);
        this.pairwiseObjectDistances = this.ensemble.getPairwiseObjectDistancesBasedOnTheirFeatureRepresentations(numSim);
        long stop = System.currentTimeMillis();

        this.offlineExecutionTime = stop-start;
    }

    public ProjectiveClustering execute (int nClusters)
    {
        System.out.println("####################################################");
        System.out.println("OneObjectiveInstanceBasedSubspaceEnsembles_EUCLIDEAN");
        System.out.println("####################################################");

        long start = System.currentTimeMillis();
        double[][] distMatrix = computeCumulativeDistanceMatrix();

        FuzzyKMedoids fkm = new FuzzyKMedoids(ensemble.getInstancesDataset());
        FuzzyClustering fkmResult = fkm.execute(distMatrix, nClusters);

        this.result = assignFeatures(fkmResult);
        long stop = System.currentTimeMillis();

        this.onlineExecutionTime = stop-start;

        return this.result;
    }

    protected double[][] computeCumulativeDistanceMatrix()
    {
        double[][] m = new double[this.ensemble.getNumberOfInstances()][this.ensemble.getNumberOfInstances()];

        for (int i=0; i<m.length; i++)
        {
            for (int j=i+1; j<m[i].length; j++)
            {
                double norm1 = this.coNonOccurrenceObjectMatrix[i][j]/2;
                if (Double.isInfinite(norm1) || Double.isNaN(norm1) || norm1<-0.0000001 || norm1>1.0000001)
                {
                    throw new RuntimeException("ERROR: norm1 must be within [0,1]---norm1="+norm1);
                }

                double norm2 = this.pairwiseObjectDistances[i][j]/this.ensemble.getNumberOfFeaturesInEachCluster();
                if (Double.isInfinite(norm2) || Double.isNaN(norm2) || norm2<-0.0000001 || norm2>1.0000001)
                {
                    throw new RuntimeException("ERROR: norm2 must be within [0,1]---norm2="+norm2);
                }

                m[i][j] = (norm1+norm2)/2;
                m[j][i] = m[i][j];
            }
        }

        return m;
    }

    protected ProjectiveClustering assignFeatures(FuzzyClustering fkmResult)
    {
        FuzzyCluster[] fuzzyClusters = fkmResult.getClusters();
        ProjectiveCluster[] subspaceClusters = new ProjectiveCluster[fuzzyClusters.length];
        double[][] objectByFeatureMatrix = this.ensemble.getObjectByFeatureMatrix();

        Double[][] featureAssignments = new Double[subspaceClusters.length][this.ensemble.getNumberOfFeaturesInEachCluster()];
        for (int i=0; i<featureAssignments.length; i++)
        {
            for (int j=0; j<featureAssignments[i].length; j++)
            {
                featureAssignments[i][j] = new Double(0.0);
            }
        }

        for (int i=0; i<subspaceClusters.length; i++)
        {
            Double[] objectRep = fuzzyClusters[i].getFeatureVectorRepresentationDouble();
            double den = 0.0;
            for (int j=0; j<objectRep.length; j++)
            {
                den += objectRep[j];
            }

            Double[] featureRep = new Double[this.ensemble.getNumberOfFeaturesInEachCluster()];
            for (int k=0; k<featureRep.length; k++)
            {
                if (den != 0.0)
                {
                    double num = 0.0;
                    for (int j=0; j<objectRep.length; j++)
                    {
                        num += objectRep[j]*objectByFeatureMatrix[j][k];
                    }
                    featureRep[k] = num/den;
                }
                else
                {
                    featureRep[k] = ((double)1.0)/featureRep.length;
                }
            }

            featureAssignments[i] = featureRep;
        }

        for (int i=0; i<featureAssignments.length; i++)
        {
            double sum = 0.0;
            for (int k=0; k<featureAssignments[i].length; k++)
            {
                if (featureAssignments[i][k] < -0.000001 || featureAssignments[i][k] > 1.000001)
                {
                    throw new RuntimeException("ERROR: feature-to-cluster assignment must be within [0,1]---featureAssignments[i][k]="+featureAssignments[i][k]);
                }
                sum += featureAssignments[i][k];
            }

            for (int k=0; k<featureAssignments[i].length; k++)
            {
                if (Double.isInfinite(sum) || Double.isNaN(sum) || sum < 0.999999 || sum > 1.000001)
                {
                    throw new RuntimeException("ERROR: sum must be equal to 1---sum="+sum);
                }
                /*
                if (sum > 0.0)
                {
                    featureAssignments[i][k] /= sum;
                }
                else
                {
                    featureAssignments[i][k] = ((double)1.0)/featureAssignments[i].length;
                }
                */
            }
        }

        for (int i=0; i<subspaceClusters.length; i++)
        {
            Double[] objectRep = fuzzyClusters[i].getFeatureVectorRepresentationDouble();
            Double[] featureRep = featureAssignments[i];

            subspaceClusters[i] = new ProjectiveCluster(this.ensemble.getInstances(), objectRep, featureRep, fuzzyClusters[i].getID(), true, true);
        }

        return new ProjectiveClustering(subspaceClusters);
    }

    public PDFSimilarity getPDFSim()
    {
        return this.pdfSim;
    }

    public void setPDFSim (PDFSimilarity pdfSim)
    {
        this.pdfSim = pdfSim;
    }

    public NumericalInstanceSimilarity getNumSim()
    {
        return this.numSim;
    }

    public void setNumSim (NumericalInstanceSimilarity numSim)
    {
        this.numSim = numSim;
    }

    public ProjectiveClustering[] getAllResults()
    {
        return new ProjectiveClustering[]{this.result};
    }
}
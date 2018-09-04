package clusteringensembles;

import weighting.WeightingScheme;
import dataset.ClusteringDataset;
import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import java.util.Vector;
import util.Matrix;



public class NMF extends NMFBasedCEMethod {
    
    public NMF (ClusteringDataset ensemble){
        this.ensemble = ensemble;
        super.init();
    }
    
    public Clustering execute(int nClusters) {
        
        //MATRICE M_Tilde /W
        double [][] M_Tilde = ensemble.getWholeCoOccurrenceMatrix();
        
        executeNMF(M_Tilde);
        
        //return clustering
        Clustering resultCluatering = buildFinalClusteringByNFM(consensusClustering);
        return resultCluatering;
        
        
        
        ////////////////////////////////////////////////////////////////
        
        /*
        double threshold = 0.0;
        
        double consensusClustering [][] = new double [0][0];
        
        //verificare che ritorni tutti gli elementi e non il numero di cluster
        int nElements = ensemble.getNumberOfInstances();
        
        //MATRICE M_Tilde /W
        double [][] M_Tilde = ensemble.getWholeCoOccurrenceMatrix();
   
        //MATRICE H
        double [][] H = new double [nElements][nClusters];
        //ogni riga ala max un 1
        for(int n=0; n<H.length; n++)
            for(int m=0; m<H[0].length; m++)
                H[n][m]= 0.0;
        
        for(int i=0; i<H.length; i++){
            int x = (int)Math.random() * nClusters;
            H [i][x] = 1;          
        }
        //Inizializzare H_Tilde /Q
        double [][] H_Transpose = Matrix.transposeMatrix(H);
        double [][] temp_Matrix = Matrix.productMatrix(H_Transpose, H);
        //DANGER
        temp_Matrix = Matrix.matrixPow(temp_Matrix, -0.5);
        double [][] H_Tilde = Matrix.productMatrix(H, temp_Matrix);
        
        //Inizializzazione D/S
        double [][] D = Matrix.productMatrix(H_Transpose, H);
        
        boolean continueCondition = true;
        double frobeniusResult = 0.0; 
        
           while(continueCondition){
            continueCondition = false;
            
            //calcolo della prima radice
            //numeratore
            double [][] num1 = Matrix.productMatrix(M_Tilde, H_Tilde);
            num1 = Matrix.productMatrix(num1, D);
            //denominatore
            double [][] H_Tilde_T = Matrix.transposeMatrix(H_Tilde);
            double [][] den1 = Matrix.productMatrix(H_Tilde, H_Tilde_T);
            den1 = Matrix.productMatrix(den1, M_Tilde);
            den1 = Matrix.productMatrix(den1, H_Tilde);
            den1 = Matrix.productMatrix(den1, D);
            
            //BUILD MATRIX Q
            for(int j=0; j<H_Tilde.length; j++){
                for(int k=0 ; k<H_Tilde[0].length; k++){
                    double Q_j_k = Math.sqrt(num1[j][k]/den1[j][k]);

                    if(H_Tilde[j][k] != Q_j_k){
                        H_Tilde[j][k] = Q_j_k;
                    }
                }
            }
            
            //calcolo seconda radice
            //numeratore
            double [][] H_Tilde2_T = Matrix.transposeMatrix(H_Tilde);
            double [][] num2 = Matrix.productMatrix(H_Tilde2_T, M_Tilde);
            num2 = Matrix.productMatrix(num2, H_Tilde);
            //denominatore
            double [][] den2 = Matrix.productMatrix(H_Tilde2_T, H_Tilde);
            den2 = Matrix.productMatrix(den2, D);
            den2 = Matrix.productMatrix(den2, H_Tilde2_T);
            den2 = Matrix.productMatrix(den2, H_Tilde);
           
            //BUILD MATRIX S
            for(int k=0 ; k<D.length; k++){
                for(int l=0; l<D[0].length; l++){
                    double S_k_l = Math.sqrt(num2[k][l]/den2[k][l]);
                    
                    if(H_Tilde[k][l] != S_k_l){
                        D[k][l] = S_k_l;
                    }
                }
            }
            
            //clustering solution
            consensusClustering = Matrix.productMatrix(H_Tilde, D);
            double [][] transposeQ = Matrix.transposeMatrix(H_Tilde);
            consensusClustering = Matrix.productMatrix(consensusClustering, transposeQ);
            
            double [][] normMatrix = Matrix.subtractionMatrix(M_Tilde, consensusClustering);
            
            if(frobeniusResult == 0.0){
                frobeniusResult = Matrix.frobeniusNorm(normMatrix);
            }
            else{
                if(frobeniusResult<Matrix.frobeniusNorm(normMatrix) + threshold)
                    continueCondition = false;
                else
                    frobeniusResult = Matrix.frobeniusNorm(normMatrix);
            }
            
        }//while
         
        //return clustering
        Clustering resultCluatering = buildFinalClusteringByNFM(consensusClustering);
        return resultCluatering;
        */
        
        
        /////////////////////////////////////////////////////////////////////////////////
        
        //versione sequenziale dell'algoritmo
        /*
        while(continueCondition){
            continueCondition = false;
            
            //calcolo della prima radice
            //numeratore
            double [][] num1 = productMatrix(M_Tilde, H_Tilde);
            num1 = productMatrix(num1, S);
            //denominatore
            double [][] QT = transposeMatrix(H_Tilde);
            double [][] den1 = productMatrix(H_Tilde, QT);
            den1 = productMatrix(den1, M_Tilde);
            den1 = productMatrix(den1, H_Tilde);
            den1 = productMatrix(den1, S);
            
            //calcolo seconda radice
            //numeratore
            double [][] num2 = productMatrix(QT, M_Tilde);
            num2 = productMatrix(num2, H_Tilde);
            //denominatore
            double [][] den2 = productMatrix(QT, H_Tilde);
            den2 = productMatrix(den2, S);
            den2 = productMatrix(den2, QT);
            den2 = productMatrix(den2, H_Tilde);
           
            boolean continueCondition1 = false;
            boolean continueCondition2 = false;
            
            for(int k=0 ; k<H_Tilde[0].length; k++){
                
                //BUILD MATRIX Q
                for(int j=0; j<H_Tilde.length; j++){
                    double Q_j_k = Math.sqrt(num1[j][k]/den1[j][k]);
                    
                    if(H_Tilde[j][k] != Q_j_k){
                        H_Tilde[j][k] = Q_j_k;
                        continueCondition1 = true;
                    }
                }
                
                //BUILD MATRIX S
                for(int l=0; l<H_Tilde[0].length; l++){
                    double S_k_l = Math.sqrt(num2[k][l]/den2[k][l]);
                    
                    if(H_Tilde[k][l] != S_k_l){
                        S[k][l] = S_k_l;
                        continueCondition2 = true;
                    }
                }
                
            }
            if(continueCondition1 || continueCondition2)
                continueCondition = true;
  
            
        }//while
       
        
        //clustering solution
        consensusClustering = productMatrix(H_Tilde, S);
        double [][] transposeQ = transposeMatrix(H_Tilde);
        consensusClustering = productMatrix(consensusClustering, transposeQ);
       
        */
    }

    public Clustering weightedExecute(int nClusters, WeightingScheme ws) {        
       throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
    //matrice elevata a potenza
    private double[][] matrixPow(double[][] matrix, double d) {
        //moltiplicazione di una matrice per uno scalare, verificare che gli elementi a 0 della
        //matrice non vengano sottoposti a moltiplicazione
        double [][] returnMatrix = new double [matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[i].length; j++){
                if(matrix[i][j]!=0.0){
                    returnMatrix[i][j] = Math.pow(matrix[i][j],d);
                }
            }
        }
        return returnMatrix;
    }
    
    //Frobenius norm (sqrt(sem(diag(A'*A)))) or :
    private double frobeniusNorm(double[][] matrix){
        double result = 0.0;
        for(int i=0; i<matrix.length; i++){
            for(int j=0; j<matrix[0].length; j++){
                result = result + Math.pow(matrix[i][j], 2);
            }
        }
        return result;
    }
    
    private double[][] transposeMatrix(double[][] matrix) {
	
        double transp[][] = new double[matrix[0].length][matrix.length];

        for (int i = 0; i < matrix.length; i++)
                for (int j = 0; j < matrix[i].length; j++)
                        transp[j][i] = matrix[i][j];
        return transp;
    }
    
    private double[][] productMatrix(double[][] a, double[][] b){
        int aRighe=a.length;
        int aColonne=a[0].length;
        int bRighe=b.length;
        int bColonne=b[0].length;

        if(aColonne != bRighe)
            return null;
        else{
            double[][] c = new double[aRighe][bColonne];

            for(int i=0; i<aRighe; i++){
                for(int j=0; j<bColonne; j++){
                    c[i][j]=0;
                    for(int k=0; k<aColonne; k++)
                        c[i][j] += a[i][k] * b[k][j];
                }
            }
            return c;
        }
    }
    
    private double [][] subtractionMatrix (double[][] aMatrix, double[][] bMatrix){
        double resultMatrix [][] = new double [aMatrix.length] [aMatrix[0].length];
        for(int i=0; i<aMatrix.length; i++){
            for(int j=0; j<aMatrix[0].length; j++){
                resultMatrix[i][j] = aMatrix[i][j] - bMatrix[i][j];
            }
        }
        return resultMatrix;
    }
    */

}

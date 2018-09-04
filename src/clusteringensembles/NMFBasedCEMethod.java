package clusteringensembles;

import objects.Cluster;
import objects.Clustering;
import objects.Instance;
import util.Matrix;
import java.util.ArrayList;


public abstract class NMFBasedCEMethod extends CEMethod{
      
    protected int nElements; //= ensemble.getNumberOfInstances();
    
    protected int nClusters; // = ensemble.getNumberOfClusters();
    
    double [][] consensusClustering; // = new double [nElements][nElements];
    
    double [][] H_Tilde; // = new double [nElements][nClusters];
    
    protected void executeNMF (double [][]M_Tilde){
        
        double threshold = 0.0;
        
        //MATRICE H
        double [][] H = new double [nElements][nClusters];
       
        //inizializzazione random di H
        for(int i=0; i<H.length; i++){
            int x = (int)(Math.random() * nClusters);
            H [i][x] = 1;          
        }
        
        //Inizializzare H_Tilde /Q
        double [][] H_Transpose = Matrix.transposeMatrix(H);
        double [][] temp_Matrix = Matrix.productMatrix(H_Transpose, H);
        //DANGER
        temp_Matrix = Matrix.matrixPow(temp_Matrix, -0.5);
        H_Tilde = Matrix.productMatrix(H, temp_Matrix);
        
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
            
            //BUILD MATRIX Q = H_Tilde
            for(int j=0; j<H_Tilde.length; j++){
                for(int k=0 ; k<H_Tilde[0].length; k++){
                    double Q_j_k = 0.0;
                    if(H_Tilde[j][k]==0 || num1[j][k]==0 || den1[j][k]==0)
                        Q_j_k = 0.0;
                    else
                        Q_j_k = H_Tilde[j][k]*Math.sqrt(num1[j][k]/den1[j][k]);

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
           
            //BUILD MATRIX S = D
            for(int k=0 ; k<D.length; k++){
                for(int l=0; l<D[0].length; l++){
                    double S_k_l =0.0;
                    if(D[k][l]==0 || num2[k][l]==0 || den2[k][l]==0)         
                        S_k_l = 0.0;
                    else
                        S_k_l = D[k][l]*Math.sqrt(num2[k][l]/den2[k][l]);;
                    
                    if(D[k][l] != S_k_l){
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
    }
   
    protected Clustering buildFinalClusteringByNFM(double [][] connectionMatrix){
        
        int idCluster = 0;
        boolean [] verified = new boolean [connectionMatrix.length];
        Instance[] instances = ensemble.getInstances();

        //prendere le instances dall'array precedente tramite gli indici ricavati dalla matrice data in input (indice i)
        //costruire un nuovo array di instance
        ArrayList<Instance> componentOfCluster = new ArrayList<Instance>();
        Cluster [] clusters = new Cluster[nClusters];
        
        for(int j=0; j<connectionMatrix.length; j++){
            if(connectionMatrix[0][j] != 0.0){
                verified[j] = true;
                componentOfCluster.add(instances[j]);
            }
        }
        
        Instance [] newElements = new Instance[componentOfCluster.size()];
        int cont=0;
        for(Instance ins:componentOfCluster){            
            newElements[cont++]=ins;
        }
        
        //newElements = (Instance[]) componentOfCluster.toArray();
        clusters[idCluster++] = new Cluster (newElements, idCluster, nElements);;
       
        componentOfCluster.clear();

        for(int i=1; i<verified.length; i++){
            if(verified[i] != true){      
                for(int j=0; j<connectionMatrix.length; j++){
                    if(connectionMatrix[i][j] != 0.0){
                        
                        if(verified[j]==true)
                            System.out.println("ERRORE DI INCOMPATIBILITA'");
                        
                        componentOfCluster.add(instances[j]);
                        verified[j]=true;
                    }
                }
                //newElements = (Instance[]) componentOfCluster.toArray();
                newElements = new Instance[componentOfCluster.size()];
                int pos=0;
                for(Instance c: componentOfCluster){
                    newElements[pos++]=c;
                }
                clusters[idCluster++] = new Cluster (newElements, idCluster, nElements);;
                
                componentOfCluster.clear();
            }
        }
        return new Clustering(clusters);
    }
    
    protected void init(){
        nElements = ensemble.getNumberOfInstances();
    
        nClusters = ((Clustering)ensemble.getData()[0]).getNumberOfClusters();
    
        consensusClustering = new double [nElements][nElements];
    
        H_Tilde = new double [nElements][nClusters];
    }
}

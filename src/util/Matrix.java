package util;

import javax.sound.midi.SysexMessage;

public class Matrix {
    
    /**
    * Calcola la matrice inversa.
    * @param matrice matrice di double.
    * @return matrice inversa.
    */
    public static double[][] inversa(double matrice[][]){

            double det = determinante(matrice);

            double aggiunta[][] = aggiunta( matrice );

            for(int i=0;i< aggiunta.length; i++)
                    for(int j=0; j< aggiunta[i].length;j++)
                            aggiunta[i][j] /= det;

            return aggiunta;
    }//inversa
    
    /**
    * Calcola l&acute;aggiunta della matrice.
    * @param matrice matrice di double.
    * @return aggiunta.
    */
    public static double[][] aggiunta(double matrice[][]){
            return transposeMatrix( matriceCofattori(matrice) );
    }//aggiunta

    /**
    * Crea la trasposta della matrice.
    * @param matrice matrice di double.
    * @return trasposta.
    */
    public static double[][] transposeMatrix(double matrice[][]){
            double mat[][] = new double[matrice[0].length][matrice.length];

            for(int i=0;i<mat.length;i++)
                    for(int j=0; j<mat[0].length;j++)
                            mat[i][j] = matrice[j][i];
            return mat;
    }//trasposta

    /**
    * Calcola la matrice dei cofattori.
    * @param matrice matrice di double.
    * @return matrice dei cofattori.
    */
    public static double[][] matriceCofattori(double matrice[][]){
            double mat[][] = new double[matrice.length][matrice[0].length];

            for(int i=0; i<mat.length; i++)
                    for(int j=0; j<mat[i].length; j++){
                            mat[i][j] = determinante( estraiSottoMatrice(matrice,i,j) );

                            if( (i+j) % 2 != 0){//i+j è dispari
                                    mat[i][j] = -mat[i][j];
                            }
                    }

            return mat;
    }//matriceCofattori

    /**
    * Estrae la sottomatrice data dall&acute;esclusione della riga e della colonna.
    * @param matrice matrice di double.
    * @param riga riga da non considerare.
    * @param colonna da non considerare.
    * @return sottomatrice.
    */
    public static double[][] estraiSottoMatrice(double matrice[][],int riga,int colonna){
            double mat[][] = new double[ matrice.length-1][ matrice[0].length-1 ];

            for(int i=0; i<mat.length; i++)
                    for(int j=0; j<mat[i].length; j++){
                            int indiceRiga = i<riga?i:i+1;
                            int indiceColonna = j<colonna?j:j+1;

                            mat[i][j] = matrice[indiceRiga][indiceColonna];
                    }

                    return mat;
    }//estraiSottoMatrice

    /**
    * Calcola il determinante di una qualsiasi matrice quadrata.
    * @param mat matrice di double.
    * @return valore determinante.
    */
    /*
    public static double determinante(double[][] mat) {

        double result = 0;

        if(mat.length == 1) {
            result = mat[0][0];
            return result;
        }
        if(mat.length == 2) {
            result = mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
            return result;
        }
        for(int i = 0; i < mat[0].length; i++) {
            double temp[][] = new double[mat.length - 1][mat[0].length - 1];
            for(int j = 1; j < mat.length; j++) {
                for(int k = 0; k < mat[0].length; k++) {
                    if(k < i) {
                        temp[j - 1][k] = mat[j][k];
                     } else if(k > i) {
                        temp[j - 1][k - 1] = mat[j][k];
                     }
                }
            }
            result += mat[0][i] * Math.pow(-1, (double)i) * determinante(temp);
        }
        return result;
    }
    */
     public static  double determinante(double A[][])
  {
         
    int n=A.length;
    double D = 1.0;                 // determinant
    double B[][]=new double[n][n];  // working matrix
    int row[]=new int[n];             // row interchange indicies
    int hold , I_pivot;             // pivot indicies
    double pivot;                   // pivot element value
    double abs_pivot;

    if(A[0].length!=n)
    {
      System.out.println("Error in Matrix.determinant, inconsistent array sizes.");
    }
    // build working matrix
    for(int i=0; i<n; i++)
      for(int j=0; j<n; j++)
        B[i][j]=A[i][j];
    // set up row interchange vectors
    for(int k=0; k<n; k++)
    {
      row[k]= k;
    }
    // begin main reduction loop
    for(int k=0; k<n-1; k++)
    {
      // find largest element for pivot
      pivot = B[row[k]][k];
      abs_pivot = Math.abs(pivot);
      I_pivot = k;
      for(int i=k; i<n; i++)
      {
        if( Math.abs(B[row[i]][k]) > abs_pivot )
        {
          I_pivot = i;
          pivot = B[row[i]][k];
          abs_pivot = Math.abs(pivot);
        }
      }
      // have pivot, interchange row indicies
      if(I_pivot != k)
      {
        hold = row[k];
        row[k] = row[I_pivot];
        row[I_pivot] = hold;
        D = - D;
      }
      // check for near singular
      if(abs_pivot < 1.0E-10)
      {
        return 0.0;
      }
      else
      {
        D = D * pivot;
        // reduce about pivot
        for(int j=k+1; j<n; j++)
        {
          B[row[k]][j] = B[row[k]][j] / B[row[k]][k];
        }
        //  inner reduction loop
        for(int i=0; i<n; i++)
        {
          if(i != k)
          {
            for(int j=k+1; j<n; j++)
            {
              B[row[i]][j] = B[row[i]][j] - B[row[i]][k]* B[row[k]][j];
            }
          }
        }
      }
      //  finished inner reduction
    }
    // end of main reduction loop
    return D * B[row[n-1]][n-1];
  } // end determinant
     
     
    /**
    * Calcola la potenza di matrice.
    * @param matrix matrice di double.
    * @param d esponente della potenza.
    * @return matrice elevata a potenza.
    */
    public static double[][] matrixPow(double[][] matrix, double d) {
        //moltiplicazione di una matrice per uno scalare, verificare che gli elementi a 0 della
        //matrice non vengano sottoposti a moltiplicazione
        double [][] returnMatrix = new double [matrix.length][matrix[0].length];;
        
        if(d >= 0){
            for (int i = 0; i < matrix.length; i++){
                for (int j = 0; j < matrix[i].length; j++){
                    if(matrix[i][j]!=0.0){
                        returnMatrix[i][j] = Math.pow(matrix[i][j],d);
                    }
                }
            }
            return returnMatrix;
            
        }else{
            double pow = d*(-1);
            double [][] inverseMatrix = Matrix.inversa(matrix);
            for (int i = 0; i < inverseMatrix.length; i++){
                for (int j = 0; j < inverseMatrix[i].length; j++){
                    if(inverseMatrix[i][j]!=0.0){
                        returnMatrix[i][j] = Math.pow(inverseMatrix[i][j],pow);
                    }
                }
            }
            return returnMatrix;
        }
            
    }
    
    /**
    * Calcola la norma matriciale di Frobenius norm (sqrt(sem(diag(A'*A)))) .
    * @param matrix matrice di double.
    * @return valore delal norma.
    */
    public static double frobeniusNorm(double[][] matrix){
        //double result = 0.0;
        double [][] matrix1 = Matrix.transposeMatrix(matrix);
        double [][] matrix2 = Matrix.productMatrix(matrix1, matrix);
        double tr = Matrix.tracciaMatrix(matrix2);
        /*
        for(int i=0; i<matrix.length; i++){
            for(int j=0; j<matrix[0].length; j++){
                result = result + Math.pow(matrix[i][j], 2);
            }
        }
        */
        return Math.sqrt(tr);
    }
    
    /**
    * Calcola il prodotto fra due matrici .
    * @param a matrice di double.
    * @param b matrice di double.
    * @return matrice prodotto.
    */
    public static double[][] productMatrix(double[][] a, double[][] b){
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
                    c[i][j]=0.0;
                    for(int k=0; k<aColonne; k++)
                        c[i][j] += a[i][k] * b[k][j];
                }
            }
            return c;
        }
    }
    
    /**
    * Calcola la differenza fra due matrici .
    * @param aMatrix matrice di double.
    * @param bMatrix matrice di double.
    * @return matrice differenza.
    */
    public static double [][] subtractionMatrix (double[][] aMatrix, double[][] bMatrix){
        double resultMatrix [][] = new double [aMatrix.length] [aMatrix[0].length];
        for(int i=0; i<aMatrix.length; i++){
            for(int j=0; j<aMatrix[0].length; j++){
                resultMatrix[i][j] = aMatrix[i][j] - bMatrix[i][j];
            }
        }
        return resultMatrix;
    }
    
    public static double tracciaMatrix(double[][] matrix){
        if(matrix[0].length == matrix.length ){
            double traccia = 0.0;
            for(int i=0; i<matrix.length; i++)
                traccia = traccia+matrix[i][i];
            return traccia;
        }
        else
            System.out.println("Matrice non quadrata");
            return -1;
    }
    
    public static double productMatrixANDTr(double[][] a, double[][] b){
        int aRighe=a.length;
        int aColonne=a[0].length;
        int bRighe=b.length;
        int bColonne=b[0].length;

        if(aColonne != bRighe)
            throw new IllegalArgumentException("Matrix must be square");
        else{
            double traccia = 0.0;
            for(int i=0; i<aRighe; i++){
                for(int k=0; k<aColonne; k++)
                    traccia += a[i][k] * b[k][i];
            }
            return traccia;
        }
    }
    
      
    public static double[][] SumMatrix (double[][] aMatrix, double[][] bMatrix) {
        double [][] C = new double[aMatrix.length][aMatrix[0].length];
        for (int i=0; i<C.length; i++)
            for (int j=0; j<C[0].length; j++)
                C[i][j] = aMatrix[i][j] + bMatrix[i][j];
        return C;
    }
    public static double SumMatrixAndTr (double[][] aMatrix, double[][] bMatrix) {
        double C = 0.0;
        for (int i=0; i<aMatrix.length; i++)
            C += aMatrix[i][i] + bMatrix[i][i];
        return C;
    }
    
    /*
    public static double normalize (double mat[][]){
        double factor = 0.0;
        for(int i=0; i<mat.length; i++){
            for(int j=0; j<mat[0].length; j++){
                if(mat[i][j]>factor)
                    factor=mat[i][j];
            }
        }
        for(int i=0; i<mat.length; i++){
            for(int j=0; j<mat[0].length; j++){
                    mat[i][j]=mat[i][j]/factor;
            }
        }
        return factor;
    }
    
    public static void productForScalar (double mat[][], double scalar){
        for(int i=0; i<mat.length; i++){
            for(int j=0; j<mat[0].length; j++){
                    mat[i][j]*=scalar;
            }
        }
    }
    */
    
    public static void main (String [] args){
        double [][] a = new double [3][3];
        a[0][0] = 1;
        a[0][1] = 2; 
        a[0][2] = 3;
        a[1][0] = 4;
        a[1][1] = 5; 
        a[1][2] = 6;
        a[2][0] = 9;
        a[2][1] = 1; 
        a[2][2] = 3;
        
        
        double d = SumMatrixAndTr(a, a);
        //double mat[][] = SumMatrix(a, a);
        //double c = tracciaMatrix(mat);
        System.out.println(d);
        /*
        //double [][]inversa = inversa(a);
        
        //System.out.println(inversa[0][0]+" "+inversa[0][1]+" "+inversa[0][2]);
        //System.out.println(inversa[1][0]+" "+inversa[1][1]+" "+inversa[1][2]);
        //System.out.println(inversa[2][0]+" "+inversa[2][1]+" "+inversa[2][2]);
        
        //double c = productMatrixANDTr(a, a);
        //System.out.println(c);
        
        double [][] a_inv1 = inversa(a);
        
        
        double det = determinante(a);
        double c = normalize(a);
        System.out.println("Factor = "+c);
        double [][] a_inv = inversa(a);
        
        productForScalar(a_inv, 1.0/c);
        
        double prod1 = a_inv1[0][0] / a_inv[0][0];
        double prod2 = a_inv[0][0] / a_inv1[0][0];
        System.out.println("prod1="+prod1);
        System.out.println("prod2="+prod2);
        System.out.println("c="+c);
        System.out.println("det="+det);    
        System.out.println("c/det="+(c/det));
        System.out.println("det/c="+(det/c));
        System.out.println();
        System.out.println("a_inv1="+a_inv1[0][0]);
        
        System.out.println("a_inv="+a_inv[0][0]);

        
        System.out.println("FINE");
        */
    } 
    
    
}
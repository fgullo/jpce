package evaluation.clustering;

import evaluation.Similarity;
import objects.Instance;

public abstract class ClusteringSimilarity implements Similarity {

    public abstract double getSimilarity (Instance i1, Instance i2);

    public abstract double getDistance (Instance i1, Instance i2);
    
    /**
    * This method return the number of agreements between two clusters
    * 
    * @param c1
    * @param c2
    * @return int
    */
    /*
    public static int getNumberOfAgreement(Instance [] c1, Instance [] c2) {
        int count=0;
        for(int i=0; i<c1.length; i++){
            for ( int j=0; j<c2.length; j++){
                if(c1[i].equals(c2[j]))
                    count++;
            }
        }
        return count;
    }
     */

}


package evaluation.pdf;

public abstract class PDFSimilarity{

    /**
     * Return the similarity between the two instances i1 and i2
     */
    public abstract double getSimilarity (Double[] i1, Double[] i2);

    public abstract double getDistance (Double[] i1, Double[] i2);

}

package evaluation.pdf;

import util.Util;

public class ProductPDFSimHard extends PDFSimilarity
{
    public double getSimilarity (Double[] i1, Double[] i2)
    {
        if (i1.length != i2.length)
        {
            throw new RuntimeException("Vectors of probabilities must be equal length");
        }

        double max1 = Double.NEGATIVE_INFINITY;
        double max2 = Double.NEGATIVE_INFINITY;
        int imax1 = -1;
        int imax2 = -1;
        for (int i=0; i<i1.length; i++)
        {
            if (i1[i] > max1)
            {
                max1 = i1[i];
                imax1 = i;
            }

            if (i2[i] > max2)
            {
                max2 = i2[i];
                imax2 = i;
            }
        }

        Util.throwException(max1, 0.0, 1.0);
        Util.throwException(max2, 0.0, 1.0);

        if (imax1 == imax2)
        {
            return 1.0;
        }

        return 0.0;
    }

    public double getDistance (Double[] i1, Double[] i2)
    {
        return 1.0 - getSimilarity(i1, i2);
    }


    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ProductPDFSimHard))
        {
            return false;
        }

        return true;
    }
}



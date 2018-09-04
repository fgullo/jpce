package pce.baseline;

import objects.ProjectiveClustering;
import pce.PCEMethod;
import util.Util;

public abstract class CEbasedPCE extends PCEMethod
{
    protected void randomFeatures(Double[] features)
    {
            double sum = 0.0;
            for (int d=0; d<features.length; d++)
            {
                features[d] = Math.random();
                sum += features[d];
            }

            double sumCheck = 0.0;
            for (int d=0; d<features.length; d++)
            {
                features[d] /= sum;
                sumCheck += features[d];

                Util.throwException(features[d], 0.0, 1.0);

                if (features[d]>1.0){features[d] = 1.0;}
                if (features[d]<0.0){features[d] = 0.0;}
            }

            Util.throwException(sumCheck, 1.0, 1.0);
    }

    protected void computeRandomFeatureToClusterAssignment(ProjectiveClustering pc)
    {
        for (int k=0; k<pc.getNumberOfClusters(); k++)
        {
            Double[] features = pc.getClusters()[k].getFeatureToClusterAssignments();
            randomFeatures(features);
        }
    }

}

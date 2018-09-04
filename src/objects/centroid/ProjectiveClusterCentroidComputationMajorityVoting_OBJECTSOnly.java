package objects.centroid;

import objects.Instance;
import objects.ProjectiveCluster;

public class ProjectiveClusterCentroidComputationMajorityVoting_OBJECTSOnly implements CentroidComputation
{
    public ProjectiveClusterCentroidComputationMajorityVoting_OBJECTSOnly()
    {

    }

    public Instance getCentroid (Instance [] data)
    {
        int sizeO = data[0].getFeatureVectorRepresentation().length;
        Double[] newObjToClustAssignments = new Double[sizeO];
        for(int i=0; i<newObjToClustAssignments.length; i++)
        {
            double val1=0.0;
            double val0=0.0;

            for(int j=0;j<data.length;j++)
            {
                double feature = (Double)data[j].getFeatureVectorRepresentation()[i];
                val1 += feature;
                val1 += (1.0-feature);
            }

            if(val1>val0)
            {
                newObjToClustAssignments[i] = 1.0;
            }
            else
            {
                newObjToClustAssignments[i] = 0.0;
            }
        }

        //computing pretended feature-based representation vector for the centroid to be returned
        int sizeF = ((ProjectiveCluster)data[0]).getInstances()[0].getNumberOfFeatures();
        Double[] newFeatToClustAssignments = new Double[sizeF];
        newFeatToClustAssignments[0] = 1.0;
        for (int i=1; i<newFeatToClustAssignments.length; i++)
        {
            newFeatToClustAssignments[i] = 0.0;
        }

        return new ProjectiveCluster(((ProjectiveCluster)data[0]).getInstances(),newObjToClustAssignments,newFeatToClustAssignments, -1, false, false);
    }

    private Instance getInstanceFromID(Instance[] data, int ID)
    {
        for (int i=0; i<data.length; i++)
        {
            if (data[i].getID() == ID)
            {
                return data[i];
            }
        }

        return null;
    }

}



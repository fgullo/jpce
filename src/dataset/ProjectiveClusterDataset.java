package dataset;

import dataset.loading.DataLoader;
import objects.Clustering;
import objects.Instance;

public class ProjectiveClusterDataset extends Dataset {

    public ProjectiveClusterDataset (Instance[] data, Clustering refPartition) {
        this.data=data;
        this.refPartition=refPartition;
    }

    public ProjectiveClusterDataset (DataLoader dl) {
        Object[] inst = dl.load();
        this.data = (Instance[])inst[0];
        this.refPartition = (Clustering)inst[1];
    }

}

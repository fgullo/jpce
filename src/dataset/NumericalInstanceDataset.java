package dataset;

import dataset.loading.DataLoader;
import objects.Clustering;
import objects.Instance;
import objects.NumericalInstance;

public class NumericalInstanceDataset extends Dataset 
{
    public NumericalInstanceDataset (Instance[] data, Clustering refPartition) 
    {
        this.data=data;
        for (int i=0; i<data.length; i++)
        {
            if (data[i] == null || !(data[i] instanceof NumericalInstance))
            {
                throw new RuntimeException("The instances must be not null and Numerical Instances");
            }
        }
        this.refPartition=refPartition;
    }

    public NumericalInstanceDataset (DataLoader dl) {
        Object[] inst = dl.load();
        this.data = (Instance[])inst[0];
        this.refPartition = (Clustering)inst[1];
    }
}


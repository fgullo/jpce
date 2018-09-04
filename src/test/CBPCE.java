
package test;

import java.util.Properties;

public class CBPCE 
{
    public static void main(String[] args)
    {
        String datasetPath = args[0];
        String ensemblePath = args[1];
        int alpha = Integer.parseInt(args[2]);
        int beta = Integer.parseInt(args[3]);
        
        run(datasetPath, ensemblePath, alpha, beta);
    }
    
    
    public static void run(String datasetPath, String ensemblePath, int alpha, int beta)
    {
        Properties props = new Properties();
        
        props.put("do_ensemble_generation", "false");
        props.put("do_PCE", "true");
        props.put("datasets_in_PCE", datasetPath);
        props.put("number_of_ensembles_in_PCE", "1");
        props.put("PCE_algorithms", "CB-PCE");
        props.put("number_of_runs", "1");
        props.put("save_consensus_clusterings", "true");

        props.put("only_hard_evaluation","true");
        props.put("fm", "false");
        props.put("entropy", "false");
        props.put("print_complete_CSV", "false");
        props.put("print_summary_CSV", "false");
        props.put("print_summary_CSV_extended", "false");
        
        props.put("single_ensemble",ensemblePath);
        props.put("single_dataset",datasetPath);
        
        props.put("CB-PCE_alpha_parameter",alpha);
        props.put("CB-PCE_beta_parameter",beta);
        
        System.out.println("");
        
        JPCETest.doPCE(props);
    }
}

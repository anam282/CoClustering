package cocluster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by a2shadab on 29/03/17.
 */
public class ClusterMerger {

    // Our expression value map
    Map<String, Vector<Float>> expressions = new HashMap<String, Vector<Float>>();

    public static void main(String argv[]){
        // filename = "/Users/parnell/yadd/school/compstat/proj/data/isa_results1000.filtered.txt";
        if (argv.length < 2){
            System.err.println("Program needs the bicat cluster file and the original microarray\n");
            return;
        }
        ClusterMerger cl = new ClusterMerger(argv[0], argv[1]);
    }

    /** Read in a file, convert a vector of clusters into a microarray form
     */
    public ClusterMerger(String filename, String gene_expression_file){
        BufferedReader br = null;
        // Open Expression File
        try {
            br = new BufferedReader(new FileReader(gene_expression_file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        // Read in expression values
        try {
            while ((line = br.readLine()) != null){
                String[] str = line.split("\t");
                Vector<Float> vals = new Vector<Float>(str.length);
                for (int i = 1; i < str.length; i++){
                    Float f = str[i].length() > 0 ? Float.valueOf(str[i]) : 0;
                    vals.add(f);
                }
                expressions.put(str[0], vals);
            }
        } catch (IOException e1) {
            System.out.println(line);
            e1.printStackTrace();
        }

        // Open up our cluster file
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Create our clusters from the cluster file
        Vector<Cluster> clusters = new Vector<Cluster>();
        System.out.print("clust_name\t");
        int i = 0;
        try {
            while ((line = br.readLine()) != null){
                String[] nums = line.split(" ");
                String[] genes = br.readLine().split(" ");
                String[] conditions = br.readLine().split(" ");
                Cluster cl = new Cluster(i, nums, genes,conditions, expressions);
                clusters.add(cl);
                System.out.print(i++ + "\t");
            }
            System.out.println("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Calculate our distances between each cluster
        i = 0;
        for (Cluster x : clusters){
            System.out.print(i++ + "\t");
            for (Cluster y : clusters){
                System.out.print(x.distanceTo(y) + "\t");
            }
            System.out.println("");
        }
    }
}

package cocluster;

import java.util.*;

/**
 * Created by a2shadab on 29/03/17.
 */

public class Cluster{
    int id;
    Set<String> genes;
    Set<Integer> conditions;
    Map<String, Vector<Float>> expressions;

    public Cluster(int id, String[] nums, String[] genes, String[] conditions, Map<String, Vector<Float>> expressions) {
        this.id = id;
        this.genes = new HashSet<String>();
        this.conditions = new HashSet<Integer>();
        for (String str : genes){ this.genes.add(str);}
        for (String str : conditions){
            this.conditions.add(Integer.valueOf(str) - 1);
        }
        this.expressions = expressions;
    }

    /** Calculate the distance between two clusters*/
    float distanceTo(Cluster x){
        float v = 0;
        Vector<Float> vals;
        for (String g : genes){
            vals = expressions.get(g);

            // System.out.println(" g = " + g + "   " + x.conditions.size() + "    val=" + vals + "  ");
            for (Integer c : x.conditions){
                float f = vals.elementAt(c);
                // System.out.println(c + ":" + f + " ");
                v += f;
            }
        }
        return v / genes.size();
    }
}

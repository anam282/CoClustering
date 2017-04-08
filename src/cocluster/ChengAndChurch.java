package cocluster;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by a2shadab on 03/04/17.
 */
public class ChengAndChurch {

    ArrayList<BitSet> matrix;
    double alpha;
    double delta;
    int numberOfClusters;

    ChengAndChurch(ArrayList<BitSet> matrix, double alpha, double delta, int numberOfClusters) {
        this.matrix  = matrix;
        this.alpha = alpha;
        this.delta = delta;
        this.numberOfClusters = numberOfClusters;
    }

    public void algorithm2() {
        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<String> properties = new ArrayList<>();

    }

    public void runChengAndChurch() {
        for(int i=0; i <= numberOfClusters; i++) {
        }
    }
}

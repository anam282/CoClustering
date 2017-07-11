package biclique;

/**
 * Created by a2shadab on 08/07/17.
 */

import java.util.*;

public class MaximalBicliques {

    List<BiClique> biCliques;
    List<BitSet> graph;
    int numProperties;
    int numSubjects;

    public MaximalBicliques(List<BitSet> graph, int numProperties, int numSubjects) {
        this.graph = graph;
        this.numProperties = numProperties;
        this.numSubjects = numSubjects;
        this.biCliques = new ArrayList<>();
    }

    public void findAllBiCliques() {
        BitSet P = new BitSet(numProperties);
        P.set(0, numProperties);

        BitSet L = new BitSet(numSubjects);
        L.set(0, numSubjects);
        findAllBiCliques(L, new BitSet(),P, new BitSet());
    }

    public void findAllBiCliques(BitSet L, BitSet R, BitSet P, BitSet Q) {
        while(P.cardinality() > 0) {
            int p = P.nextSetBit(0);
            P.clear(p);
            BitSet Rdash = (BitSet)R.clone();
            Rdash.set(p);

            BitSet Ldash = new BitSet(numSubjects);
            for(int l = L.nextSetBit(0); l != -1; l = L.nextSetBit(l+1)) {
                if(graph.get(l).get(p)) {
                    Ldash.set(l);
                }
            }
            if(Ldash.cardinality() == 0) continue;
            BitSet Pdash = new BitSet(numProperties);
            BitSet Qdash = new BitSet(numProperties);

            boolean isMaximal = true;

            for (int v = Q.nextSetBit(0); v!=-1; v=Q.nextSetBit(v+1)) {
                BitSet neighbours = new BitSet();
                for(int subject = Ldash.nextSetBit(0); subject!=-1; subject = Ldash.nextSetBit(subject+1)) {
                    if(graph.get(subject).get(v)) {
                        neighbours.set(subject);
                    }
                }
                if(neighbours.cardinality() == Ldash.cardinality()) {
                    isMaximal = false;
                    break;
                }
                else if(neighbours.cardinality() > 0) {
                    Qdash.set(v);
                }
            }
            if(isMaximal) {
                for(int v=P.nextSetBit(0); v!=-1; v=P.nextSetBit(v+1)) {
                    BitSet neighbours = new BitSet();
                    for (int l = Ldash.nextSetBit(0); l!=-1; l=Ldash.nextSetBit(l+1)) {
                        if (graph.get(l).get(v)) {
                            neighbours.set(l);
                        }
                    }
                    if (neighbours.cardinality() == Ldash.cardinality()) {
                        Rdash.set(v);
                    } else if (neighbours.cardinality() > 0) {
                        Pdash.set(v);
                    }
                }
                System.out.println("biclique");
                //printBicliques(Ldash, Rdash);
                BiClique biClique = new BiClique(Ldash, Rdash);
                System.out.println(biClique.toString());
                biCliques.add(biClique);
                if(Pdash.size()!= 0) {
                    findAllBiCliques(Ldash, Rdash, Pdash, Qdash);
                }
            }
            Q.set(p);
        }
    }

    public void printBicliques(BitSet L, BitSet R) {

        for(int i=L.nextSetBit(0); i!=-1; i= L.nextSetBit(i+1)) {
            for(int j=R.nextSetBit(0); j!=-1; j=R.nextSetBit(j+1)) {
                if(graph.get(i).get(j)) {
                    System.out.print(1 + " ");
                }
                else System.out.print(0 + " ");
            }
            System.out.println();
        }
    }
}

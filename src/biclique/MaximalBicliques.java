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
        List<Integer> P = new ArrayList<>();
        for(int i=0; i<numProperties; i++){
            P.add(i);
        }
        Set<Integer> L = new HashSet<>();
        for(int i=0; i<numSubjects; i++) {
            L.add(i);
        }
        findAllBiCliques(L, new HashSet<Integer>(),P, new HashSet<>());
    }

    public void findAllBiCliques(Set<Integer> L, Set<Integer> R, List<Integer> P, Set<Integer> Q) {
        while(P.size() > 0) {
            int p = P.get(0);
            P.remove(0);
            Set<Integer> Rdash = new HashSet<>();
            for(int r:R){
                Rdash.add(r);
            }
            Rdash.add(p);

            Set<Integer> Ldash = new HashSet<>();
            for(int l:L) {
                if(graph.get(l).get(p)) {
                    Ldash.add(l);
                }
            }
            if(Ldash.size() == 0) continue;
            List<Integer> Pdash = new ArrayList<>();
            Set<Integer> Qdash = new HashSet<>();

            boolean isMaximal = true;

            for (int v:Q) {
                List<Integer> neighbours = new ArrayList<>();
                for(int subject:Ldash) {
                    if(graph.get(subject).get(v)) {
                        neighbours.add(subject);
                    }
                }
                if(neighbours.size() == Ldash.size()) {
                    isMaximal = false;
                    break;
                }
                else if(neighbours.size() > 0) {
                    Qdash.add(v);
                }
            }
            if(isMaximal) {
                for(int v:P) {
                    List<Integer> neighbours = new ArrayList<>();
                    for (int l : Ldash) {
                        if (graph.get(l).get(v)) {
                            neighbours.add(l);
                        }
                    }
                    if (neighbours.size() == Ldash.size()) {
                        Rdash.add(v);
                    } else if (neighbours.size() > 0) {
                        Pdash.add(v);
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
            Q.add(p);
        }
    }

    public void printBicliques(Set<Integer> L, Set<Integer> R) {

        for(int i:L) {
            for(int j:R) {
                if(graph.get(i).get(j)) {
                    System.out.print(1 + " ");
                }
                else System.out.print(0 + " ");
            }
            System.out.println();
        }
    }
}

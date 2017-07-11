package biclique;

/**
 * Created by a2shadab on 09/07/17.
 */

import java.util.HashSet;
import java.util.Set;

public class BiClique {
    Set<Integer> properties;
    Set<Integer> subjects;

    public BiClique(){
        properties = new HashSet<>();
        subjects = new HashSet<>();
    }

    public BiClique(Set<Integer> subjects, Set<Integer> properties) {
        this.subjects = subjects;
        this.properties = properties;
    }

    public String toString() {
        return "p -> " + properties.toString() + " s-> " + subjects.toString() + "\n";
    }
}

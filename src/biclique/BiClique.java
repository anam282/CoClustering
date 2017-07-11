package biclique;

/**
 * Created by a2shadab on 09/07/17.
 */

import java.util.BitSet;

public class BiClique {
    BitSet properties;
    BitSet subjects;

    public BiClique(){
        properties = new BitSet();
        subjects = new BitSet();
    }

    public BiClique(BitSet subjects, BitSet properties) {
        this.subjects = subjects;
        this.properties = properties;
    }

    public String toString() {
        return "p -> " + properties.toString() + " s-> " + subjects.toString() + "\n";
    }
}

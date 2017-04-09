package cocluster;

import java.util.List;
import java.util.BitSet;

/**
 * Created by a2shadab on 08/04/17.
 */
public class BiCluster {

    BitSet schema;
    List<Integer> subjects;

    public BiCluster(BitSet schema, List<Integer> subjects) {
        this.schema = schema;
        this.subjects = subjects;
    }

    public BitSet getSchema(){
        return schema;
    }

    public List<Integer> getSubjects(){
        return subjects;
    }

    public void setSchema(BitSet schema) {
        this.schema = schema;
    }

    public void setSubjects(List<Integer> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Support Size : " + subjects.size() + "\n");
        for (int i = schema.nextSetBit(0); i != -1; i = schema.nextSetBit(i + 1)) {
            str.append(i);
            str.append(' ');
        }
        return str.toString();
    }
}

package cocluster;

/**
 * Created by a2shadab on 06/04/17.
 */

import java.util.*;

public class TestBimax {

    public static void main(String[] args) {
        ArrayList<BitSet> schema = new ArrayList<>();

        for( int i=0; i < 10; i++ ) {
            BitSet bitSet = new BitSet(10);
            bitSet.set(0, 10);
            bitSet.clear(i);
            schema.add(bitSet);
        }
        Bimax bimax = new Bimax(schema, 2, 2, 10);
        bimax.runBimax();
//        for ( int i =0; i < 10; i++) {
//            for( int j =0; j < 10; j++) {
//                System.out.print("1 ");
//            }
//            System.out.println();
//        }
//        System.out.println(schema.toString());


    }
}

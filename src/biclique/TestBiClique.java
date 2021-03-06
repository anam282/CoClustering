package biclique;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;

/**
 * Created by a2shadab on 10/07/17.
 */
public class TestBiClique {
    public static List<BitSet> readFromFile(String filename){
        List<BitSet> SP = new ArrayList<>();
        try {
            Scanner in = new Scanner(new FileReader(filename));
            while(in.hasNext()) {
                String binary = in.nextLine();
                BitSet bitset = new BitSet(binary.length());
                for (int i = 0; i < binary.length(); i++) {
                    if (binary.charAt(i) == '1') {
                        bitset.set(i);
                    }
                }
                SP.add(bitset);
            }
        }
        catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
        }
        return SP;
    }

    public static void main(String[] args) {

        List<BitSet> schema = readFromFile("./dataset/matrix.txt");
        MaximalBicliques maximalBicliques = new MaximalBicliques(schema, 10,10);
        maximalBicliques.findAllBiCliques();
        //System.out.println(maximalBicliques.biCliques.toString());
    }
}

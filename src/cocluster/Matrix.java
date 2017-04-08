package cocluster;

/**
 * Created by a2shadab on 06/04/17.
 */

import java.util.*;

public class Matrix {
    int originalRowNum;
    BitSet row;

    public Matrix(int originalRowNum, BitSet row) {
        this.originalRowNum = originalRowNum;
        this.row = row;
    }

    public int getOriginalRowNum() {
        return originalRowNum;
    }

    public BitSet getRow() {
        return row;
    }
}

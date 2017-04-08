package cocluster;

/**
 * Created by a2shadab on 04/04/17.
 */

import java.util.*;

public class Bimax {

    List<BitSet> schemas;
    int noProperties;
    int minProperties;
    int minSubjects;
    int noSubjects;
    List<Matrix> matrix;
    List<BitSet> mandatoryColumns;
    List<BitSet> consideredColumns;
    BitSet columnIntersection;
    int overlapping;


    public Bimax(List<BitSet> schemas, int minProperties, int minSubjects, int noProperties) {
        this.noProperties = noProperties;
        this.schemas = schemas;
        this.minProperties = minProperties;
        this.minSubjects = minSubjects;
        this.columnIntersection = new BitSet(noProperties);
        this.noSubjects = schemas.size();
        this.overlapping = 0;
        initialize();
    }

    private void initialize() {

        matrix = new ArrayList<>();
        for (int i = 0; i < noSubjects; i++) {

            Matrix matrix = new Matrix(i, schemas.get(i));
            this.matrix.add(matrix);
        }
        consideredColumns = new ArrayList<>(Collections.nCopies(noSubjects + 2, null));
        mandatoryColumns = new ArrayList<>(Collections.nCopies(noSubjects + 2, null));
        consideredColumns.set(0, new BitSet());
        for (int i = 0; i < noProperties; i++) {
            setColumn(consideredColumns.get(0), i);
        }
    }

    private Boolean isSet(BitSet row, int column) {
        return row.get(column);
    }

    private void setColumn(BitSet row, int column) {
        row.set(column);
    }

    private void unsetColumn(BitSet row, int column) {
        row.clear(column);
    }

    private int columnCount(BitSet row) {
        return row.cardinality();
    }

    private int compareColumns(BitSet row1, BitSet row2, BitSet mask) {

        int contained = 1;
        int disjoint = 1;

        BitSet sharedColumns = (BitSet) row1.clone();
        sharedColumns.and(row2);
        sharedColumns.and(mask);
        BitSet scr2 = (BitSet) sharedColumns.clone();
        scr2.or(row2);
        if (!scr2.equals(sharedColumns)) {
            contained = 0;
        }

        if (sharedColumns.cardinality() != 0)
            disjoint = 0;
        if (contained > 0 && disjoint > 0)
            return -2;
        if (contained > 0)
            return -1;
        if (disjoint > 0)
            return 1;
        return 0;
    }

    private BitSet copyColumnSet(BitSet row1, Boolean copyMode) {
        BitSet row2 = new BitSet();
        if (copyMode) {
            row2 = (BitSet) row1.clone();
        } else {
            row1.flip(0, noProperties);
            row2 = (BitSet) row1.clone();
        }
        return row2;
    }

    private BitSet intersectColumnSets(BitSet row1, BitSet row2) {
        BitSet dest = (BitSet) row1.clone();
        dest.and(row2);
        return dest;
    }

    private void determineColumnsInCommon(int firstRow, int lastRow) {
        if (firstRow >= 0 && lastRow >= firstRow && lastRow < noSubjects) {
            columnIntersection.set(0, noProperties);
            for (int i = firstRow; i <= lastRow; i++) {
                columnIntersection.and(matrix.get(i).getRow());
            }
        }
    }

    private boolean containsMandatoryColumns(BitSet row, int noMandatorySets) {

        for (int i = 0; i < noMandatorySets; i++) {
            BitSet mandatory = (BitSet) row.clone();
            mandatory.and(matrix.get(i).getRow());
            if (mandatory.cardinality() == 0) return false;
        }

        return true;

    }

    private void swapRows(int a, int b) {
        if (a >= 0 && a < noSubjects && b >= 0 && b < noSubjects) {
            Matrix temp = matrix.get(a);
            matrix.set(a, matrix.get(b));
            matrix.set(b, temp);
        }
    }

    private int chooseSplitRow(int firstRow, int lastRow, int level) {

        int i = firstRow;
        for (; i <= lastRow &&
                compareColumns(matrix.get(i).getRow(), consideredColumns.get(level), consideredColumns.get(0)) < 0;
             i++)
            ;
        if (i <= lastRow)
            return i;
        return firstRow;
    }

    private int selectRows(int firstRow, int lastRow, int level) {

        int selected = 0;
        overlapping = 0;
        while (firstRow <= lastRow) {
            switch (compareColumns(consideredColumns.get(level), matrix.get(firstRow).getRow(), consideredColumns.get(level - 1))) {
                case -2:
                case 1:
                    swapRows(lastRow, firstRow);
                    lastRow--;
                    break;
                case 0:
                    overlapping = 1;
                default:
                    selected++;
                    firstRow++;
                    break;
            }
        }
        return selected;
    }

    private void writeBicluster(int firstRow, int lastRow, BitSet columnSet) {

        System.out.println("This works");
        int biclusterCounter = 0;
        biclusterCounter++;
        System.out.println("cluster number =" + biclusterCounter);
        for (int i = firstRow; i <= lastRow; i++)
            System.out.print(matrix.get(i).getOriginalRowNum() + 1 + " ");
        System.out.println();
        for (int i = 0; i < noProperties; i++) {
            //System.out.println(columnSet.toString());
            if (isSet(columnSet, i)) {
                System.out.print(i + 1 + " ");
            }
        }
        System.out.println();
    }

    private void conquer(int firstRow, int lastRow, int level, int noMandatorySets) {

        int noSelectedRows = 0;
        determineColumnsInCommon(firstRow, lastRow);
        System.out.println(compareColumns(columnIntersection, consideredColumns.get(level), consideredColumns.get(level)));
        if (compareColumns(columnIntersection, consideredColumns.get(level), consideredColumns.get(level)) == -1) {
            writeBicluster(firstRow, lastRow, columnIntersection);
        } else {
            int splitRow = chooseSplitRow(firstRow, lastRow, level);
            consideredColumns.set(level + 1, intersectColumnSets(consideredColumns.get(level), matrix.get(splitRow).getRow()));

            if (columnCount(consideredColumns.get(level + 1)) >= 2 && containsMandatoryColumns(consideredColumns.get(level + 1), noMandatorySets)) {
                noSelectedRows = selectRows(firstRow, lastRow, level + 1);
                if (noSelectedRows >= 2) {
                    conquer(firstRow, firstRow + noSelectedRows - 1, level + 1, noMandatorySets);
                }
            }

            consideredColumns.set(level + 1, copyColumnSet(consideredColumns.get(level + 1), false));
            consideredColumns.set(level + 1, intersectColumnSets(consideredColumns.get(level), consideredColumns.get(level + 1)));

            if (overlapping > 0) {
                mandatoryColumns.set(noMandatorySets, copyColumnSet(consideredColumns.get(level + 1), true));
                noMandatorySets++;
            }
            noSelectedRows = selectRows(firstRow, lastRow, level + 1);
            consideredColumns.set(level + 1, copyColumnSet(consideredColumns.get(level), true));
            if (noSelectedRows >= 2)
                conquer(firstRow, firstRow + noSelectedRows - 1, level + 1, noMandatorySets);
        }
    }

    public void runBimax() {
        conquer(0, 10 - 1, 0, 0);

    }
}
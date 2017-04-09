package cocluster;

/**
 * Created by a2shadab on 08/04/17.
 */
import java.util.*;

public class BimaxCluster {
    List<BitSet> schemas;
    int noProperties;
    int minProperties;
    int minSubjects;
    int noSubjects;
    List<Matrix> matrix;
    List<BitSet> mandatoryColumns;
    List<BitSet> consideredColumns;
    int biclusterCounter;
    List<BiCluster> biClusters;
    Set<BitSet> allSchemas;


    public BimaxCluster(List<BitSet> schemas, int minProperties, int minSubjects, int noProperties) {
        this.noProperties = noProperties;
        this.schemas = schemas;
        this.minProperties = minProperties;
        this.minSubjects = minSubjects;
        this.noSubjects = schemas.size();
        this.biclusterCounter = 0;
        this.biClusters = new ArrayList<>();
        this.allSchemas = new HashSet<>();
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
        consideredColumns.get(0).set(0, noProperties);
    }

    private int columnCount(BitSet row) {
//        System.out.println("column count");
//        System.out.println(row.toString());
//        System.out.println(row.cardinality());
        return row.cardinality();
    }

    private int compareColumns(BitSet row1, BitSet row2, BitSet mask) {

        int contained = 1;
        int disjoint = 1;
        int output = 0;
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
//        System.out.println("compare columns");
//        System.out.println("\nrow1: " + row1.toString());
//        System.out.println("row2: " + row2.toString());
//        System.out.println("mask: " + mask.toString());
        if (contained > 0 && disjoint > 0)
            output = -2;
        if (contained > 0)
            output = -1;
        if (disjoint > 0)
            output = 1;
//        System.out.println("output :" + output);
        return output;
    }

    private BitSet copyColumnSet(BitSet row1, Boolean copyMode) {
        BitSet row2 = (BitSet) row1.clone();
        if (!copyMode) {
            row2.flip(0, noProperties);
        }
        return row2;
    }

    private BitSet intersectColumnSets(BitSet row1, BitSet row2) {
//        System.out.println("intersect");
//        System.out.println(row1.toString());
//        System.out.println(row2.toString());
        BitSet dest = (BitSet) row1.clone();
        dest.and(row2);
//        System.out.println(dest.toString());
        return dest;
    }

    private BitSet determineColumnsInCommon(int firstRow, int lastRow) {

        BitSet columnIntersection = new BitSet(noProperties);
        if (firstRow >= 0 && lastRow >= firstRow && lastRow < noSubjects) {
            columnIntersection.set(0, noProperties);
            for (int i = firstRow; i <= lastRow; i++) {
                columnIntersection.and(matrix.get(i).getRow());
            }
        }
        return columnIntersection;
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
//        System.out.println("split start");
        for (; i <= lastRow &&
                compareColumns(matrix.get(i).getRow(), consideredColumns.get(level), consideredColumns.get(0)) < 0;
             i++)
            ;
//        System.out.println("split end");
        if (i <= lastRow)
            return i;
        return firstRow;
    }

    private Map<Integer, Integer> selectRows(int firstRow, int lastRow, int level) {

        Map<Integer, Integer> values = new HashMap<>();
        int selected = 0;
        int overlapping = 0;
        while (firstRow <= lastRow) {
//            System.out.println("switch : " + compareColumns(consideredColumns.get(level), matrix.get(firstRow).getRow(), consideredColumns.get(level - 1)));
//            System.out.println("firstrow : " + matrix.get(firstRow).getRow().toString());
//            System.out.println("cc level : " + consideredColumns.get(level).toString());
//            System.out.println("cc level-1 : " + consideredColumns.get(level-1).toString());
//            System.out.println("select Rows");
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
        values.put(1, selected);
        values.put(2, overlapping);
        return values;
    }

    private void writeBicluster(int firstRow, int lastRow, BitSet columnSet) {

//        if(biclusterCounter == 20) {
//            System.exit(0);
//        }
        System.out.println("cluster number =" + ++biclusterCounter);
        List<Integer> subjects = new ArrayList<>();
        for (int i = firstRow; i <= lastRow; i++) {
            subjects.add(matrix.get(i).getOriginalRowNum());
        }
        BiCluster biCluster = new BiCluster(columnSet, subjects);
        System.out.println(biCluster.toString());
        biClusters.add(biCluster);
        allSchemas.add(columnSet);
    }

    private void conquer(int firstRow, int lastRow, int level, int noMandatorySets) {

//        System.out.println("mandatory sets =" + noMandatorySets);
        System.out.println("first= " + firstRow + " last= " + lastRow);
        System.out.println("level =" + level);
//        System.out.println();
        int noSelectedRows;
        int overlapping = 0;
        Map<Integer, Integer> values;
        BitSet columnIntersection = determineColumnsInCommon(firstRow, lastRow);
//        System.out.println("considered columns");
//        System.out.println(consideredColumns.toString());
//        System.out.println("column intersection");
        if (compareColumns(columnIntersection,
                consideredColumns.get(level),
                consideredColumns.get(level)) == -1) {

            writeBicluster(firstRow, lastRow, columnIntersection);

        } else {

            int splitRow = chooseSplitRow(firstRow, lastRow, level);
//            System.out.println("Check value" + intersectColumnSets(consideredColumns.get(level),
//                    matrix.get(splitRow).getRow()).toString() + " level: " + level);
//            System.out.println("matrix splitrow= " + matrix.get(splitRow).getRow().toString());
//            System.out.println("ccl: " + consideredColumns.get(level));
            consideredColumns.set(level + 1,
                    intersectColumnSets(consideredColumns.get(level),
                            matrix.get(splitRow).getRow()));

            if (columnCount(consideredColumns.get(level + 1)) >= minProperties
                    && containsMandatoryColumns(consideredColumns.get(level + 1), noMandatorySets)) {

                values = selectRows(firstRow, lastRow, level + 1);
                noSelectedRows = values.get(1);
                overlapping = values.get(2);
                if (noSelectedRows >= minSubjects) {
                    //int l = firstRow + noSelectedRows - 1;
                    //System.out.println("1. first= " + firstRow + " firstRow + noslected -1 = " + l);
                    conquer(firstRow, firstRow + noSelectedRows - 1, level + 1, noMandatorySets);
                }
            }

            consideredColumns.set(level + 1, copyColumnSet(consideredColumns.get(level + 1), false));
            consideredColumns.set(level + 1, intersectColumnSets(consideredColumns.get(level), consideredColumns.get(level + 1)));

            if (overlapping > 0) {
                mandatoryColumns.set(noMandatorySets, copyColumnSet(consideredColumns.get(level + 1), true));
                noMandatorySets++;
            }
            values = selectRows(firstRow, lastRow, level + 1);
            noSelectedRows = values.get(1);
            consideredColumns.set(level + 1, copyColumnSet(consideredColumns.get(level), true));
            if (noSelectedRows >= minSubjects) {
                //int l = firstRow + noSelectedRows - 1;
                //System.out.println("2. first= " + firstRow + " firstrow + noslected -1 = " + l);
                conquer(firstRow, firstRow + noSelectedRows - 1, level + 1, noMandatorySets);
            }
        }
    }

    public void runBimax() {
        conquer(0, noSubjects - 1, 0, 0);
//        System.out.println(consideredColumns.toString());

    }

    public Set<BitSet> getAllSchemas(){

        return this.allSchemas;
    }

}


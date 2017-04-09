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
        if (contained > 0 && disjoint > 0)
            output = -2;
        if (contained > 0)
            output = -1;
        if (disjoint > 0)
            output = 1;
        return output;
    }

    private BitSet copyColumnSet(BitSet row1, Boolean copyMode) {
        BitSet row2 = (BitSet) row1.clone();
        if (copyMode) {
            row2.flip(0, noProperties);
        }
        return row2;
    }

    private BitSet intersectColumnSets(BitSet row1, BitSet row2) {

        BitSet dest = (BitSet) row1.clone();
        dest.and(row2);
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
            BitSet mandatoryColumn = (BitSet) row.clone();
            mandatoryColumn.and(mandatoryColumns.get(i));
            if (mandatoryColumn.cardinality() == 0) return false;
        }

        return true;

    }

    private void swapRows(int a, int b) {
        if (a!= b && a >= 0 && a < noSubjects && b >= 0 && b < noSubjects) {
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

    private Map<Integer, Integer> selectRows(int firstRow, int lastRow, int level) {

        Map<Integer, Integer> values = new HashMap<>();
        int selected = 0;
        int overlapping = 0;
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
        values.put(1, selected);
        values.put(2, overlapping);
        return values;
    }

    private void writeBicluster(int firstRow, int lastRow, BitSet columnSet) {

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

        int noSelectedRows;
        int overlapping = 0;
        Map<Integer, Integer> values;
        BitSet columnIntersection = determineColumnsInCommon(firstRow, lastRow);

        if (compareColumns(columnIntersection,
                consideredColumns.get(level),
                consideredColumns.get(level)) == -1) {

            writeBicluster(firstRow, lastRow, columnIntersection);

        } else {

            int splitRow = chooseSplitRow(firstRow, lastRow, level);
            consideredColumns.set(level + 1,
                    intersectColumnSets(consideredColumns.get(level),
                            matrix.get(splitRow).getRow()));

            if (columnCount(consideredColumns.get(level + 1)) >= minProperties
                    && containsMandatoryColumns(consideredColumns.get(level + 1), noMandatorySets)) {

                values = selectRows(firstRow, lastRow, level + 1);
                noSelectedRows = values.get(1);
                overlapping = values.get(2);
                if (noSelectedRows >= minSubjects) {
                    conquer(firstRow, firstRow + noSelectedRows - 1, level + 1, noMandatorySets);
                }
            }

            consideredColumns.set(level + 1, copyColumnSet(consideredColumns.get(level + 1), true));
            consideredColumns.set(level + 1, intersectColumnSets(consideredColumns.get(level), consideredColumns.get(level + 1)));

            if (overlapping > 0) {
                mandatoryColumns.set(noMandatorySets, copyColumnSet(consideredColumns.get(level + 1), false));
                noMandatorySets++;
            }
            values = selectRows(firstRow, lastRow, level + 1);
            noSelectedRows = values.get(1);
            consideredColumns.set(level + 1, copyColumnSet(consideredColumns.get(level), false));
            if (noSelectedRows >= minSubjects) {
                conquer(firstRow, firstRow + noSelectedRows - 1, level + 1, noMandatorySets);
            }
        }
    }

    public void runBimax() {
        conquer(0, noSubjects - 1, 0, 0);
    }

    public Set<BitSet> getAllSchemas(){

        return this.allSchemas;
    }

}


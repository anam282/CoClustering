package GoodmanKruskalCoCluster;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

/**
 * Created by a2shadab on 13/06/17.
 */
public class TCoClust {
    List<BitSet> dataMatrix;
    int numOfIter;
    List<BitSet> rowClusters;
    List<BitSet> columnClusters;
    int numRows;
    int numColumns;
    List<List<Integer>> contingency;
    List<Integer> contingencyRows;
    List<Integer> contingencyColumns;
    int contingencyTotal;
    double minError;

    public TCoClust(List<BitSet> dataMatrix, int numOfIter, int numRows, int numColumns) {
        this.dataMatrix = dataMatrix;
        this.numOfIter = numOfIter;
        this.rowClusters = new ArrayList<>();
        this.columnClusters = new ArrayList<>();
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.contingency = new ArrayList<>();
        this.contingencyRows = new ArrayList<>();
        this.contingencyColumns = new ArrayList<>();
        this.contingencyTotal = 0;
        initializeClusters();
        this.contingency = calculateContingencyTable(rowClusters, columnClusters);
        this.contingencyRows = calculateContingencyRows();
        this.contingencyColumns = calculateContingencyCols();
        calculateContingencyTotal();
        this.minError = goodmanKruskal(contingencyRows, contingencyColumns, contingency);
    }

    public void initializeClusters() {
        BitSet row = dataMatrix.get(0);
        rowClusters.add(row);
        BitSet cloneRow = (BitSet) row.clone();
        cloneRow.flip(0, numColumns);
        rowClusters.add(cloneRow);

        BitSet column = new BitSet();
        for(int i=0 ; i < numRows; i++) {
            if(dataMatrix.get(0).get(0)) {
                column.set(i);
            }
        }

        BitSet cloneColumn = (BitSet) column.clone();
        cloneColumn.flip(0, numRows);
        columnClusters.add(column);
        columnClusters.add(cloneColumn);
    }

    public List<List<Integer>> calculateContingencyTable(List<BitSet> rowClusters, List<BitSet> colClusters) {

        List<List<Integer>> newContingency = new ArrayList<>();
        for(int i=0; i< rowClusters.size(); i++) {
            newContingency.add(new ArrayList<Integer>());
        }

        int r = 0;
        //int c = 0;
        for(BitSet rowCluster : rowClusters) {
            for(BitSet colCluster : colClusters) {
                int sum = 0;
                for(int i=rowCluster.nextSetBit(0); i!= -1; i = rowCluster.nextSetBit(i+1)) {
                    for(int j = colCluster.nextSetBit(0); j != -1; j = colCluster.nextSetBit(j+1)) {
                        sum++;
                    }
                }
                newContingency.get(r).add(sum);
            }
            r++;
        }
        return newContingency;
    }

    public List<Integer> calculateContingencyRows() {

        List<Integer> newContingencyRows = new ArrayList<>();
        for(int i=0; i < contingency.size(); i++) {
            int rowSum = 0;
            for(int j=0; j < contingency.get(i).size(); j++) {
                rowSum += contingency.get(i).get(j);
            }
            newContingencyRows.add(rowSum);
        }
        return newContingencyRows;
    }

    public void calculateContingencyTotal() {
         for(BitSet row: dataMatrix) {
             contingencyTotal += row.cardinality();
         }
    }

    public List<Integer> calculateContingencyCols() {

        List<Integer> newContingencyCols = new ArrayList<Integer>();
        for(int i=0; i < contingency.size(); i++) {
            for(int j=0; j < contingency.get(i).size(); j++) {
                if(newContingencyCols.size() < j+1) {
                    newContingencyCols.add(0);
                }
                newContingencyCols.set(j, newContingencyCols.get(j)+contingency.get(i).get(j));
            }
        }

        return newContingencyCols;
    }

    public void runCoClust() {
        int t = 0;

        while(t <= numOfIter) {
            optimizePartition(rowClusters, columnClusters, true);
            optimizePartition(columnClusters, rowClusters, false);
            t++;
        }
    }

    public void optimizePartition(List<BitSet> U, List<BitSet> V, boolean isRowPartition) {
        Random rand = new Random();
        int len = U.size();

        int cluster = rand.nextInt(len);
        int object = U.get(cluster).nextSetBit(0);
        if (object < 0) return;
        for(int i=0; i<len; i++) {
            if( i != cluster) {
                U.get(i).set(object);
                U.get(cluster).clear(object);
                List<List<Integer>> newContingency = null;
                double newError = 0.0;
                if( isRowPartition ) {
                    newContingency = calculateContingencyTable(U, V);
                    List<Integer> newContingencyCols = calculateContingencyCols();
                    newError = goodmanKruskal(contingencyRows, newContingencyCols, newContingency);
                    if(newError < minError) {
                        minError = newError;
                        contingencyColumns = newContingencyCols;
                        contingency = newContingency;
                    }
                    else {
                        U.get(i).clear(object);
                        U.get(cluster).set(object);
                    }
                }
                else {
                    newContingency = calculateContingencyTable(V, U);
                    List<Integer> newContingencyRows = calculateContingencyRows();
                    newError = goodmanKruskal(newContingencyRows, contingencyColumns, newContingency);
                    if(newError < minError) {
                        minError = newError;
                        contingencyRows = newContingencyRows;
                        contingency = newContingency;
                    }
                    else {
                        U.get(i).clear(object);
                        U.get(cluster).set(object);
                    }
                }
            }
        }
    }

    public double goodmanKruskal(List<Integer> U, List<Integer> V, List<List<Integer>> contingency) {

        double eu = 0.0;
        double euv = 0.0;
        for(int i=0; i < U.size(); i++) {
            eu += (double) (contingencyTotal - U.get(i)) * U.get(i) / (double) contingencyTotal ;
            for(int j=0; j < V.size(); j++) {
                if(V.get(j) != 0) {
                    euv += (V.get(j) - contingency.get(i).get(j)) * contingency.get(i).get(j) / V.get(j);
                }
            }
        }

        return (eu - euv)/eu;
    }

    public List<BitSet> getRowClusters() {
        return rowClusters;
    }

    public List<BitSet> getColumnClusters() {
        return columnClusters;
    }

    public void printCoClusters() {
//        for(BitSet rowCl: getRowClusters()) {
//            if(rowCl.cardinality() == 0) continue;
//            for(BitSet colCl : getColumnClusters()) {
//
//                System.out.println("RowCluster");
//                for(int i=rowCl.nextSetBit(0); i!= -1; i = rowCl.nextSetBit(i+1)) {
//                    System.out.print(i + " ");
//                }
//                System.out.println();
//
//                System.out.println("Support:");
//                for(int i=colCl.nextSetBit(0); i!= -1; i = colCl.nextSetBit(i+1)) {
//                    System.out.print(i + " ");
//                }
//                System.out.println();
//            }
//        }
        System.out.println(getColumnClusters().toString());
        System.out.println(getRowClusters().toString());
    }
}
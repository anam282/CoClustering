package GoodmanKruskalCoCluster;

import javax.swing.plaf.synth.SynthEditorPaneUI;
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
    double minErrorRow;
    double minErrorColumn;

    public TCoClust(List<BitSet> dataMatrix, int numOfIter, int numRows, int numColumns) {
        //System.out.println(dataMatrix.toString());
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
        this.contingencyRows = calculateContingencyRows(contingency);
        this.contingencyColumns = calculateContingencyCols(contingency);
        calculateContingencyTotal();
        this.minErrorRow = goodmanKruskalRow();
        this.minErrorColumn = goodmanKruskalColumn();
    }

    public void initializeClusters() {
        BitSet col = (BitSet) dataMatrix.get(0).clone();
        columnClusters.add(col);
        BitSet cloneCol = (BitSet) col.clone();
        cloneCol.flip(0, numColumns);
        if(cloneCol.cardinality() > 0){
            columnClusters.add(cloneCol);
        }

        BitSet row = new BitSet();
        for (int i = 0; i < numRows; i++) {
            if (dataMatrix.get(i).get(0)) {
                row.set(i);
            }
        }

        rowClusters.add(row);
        BitSet cloneRow = (BitSet) row.clone();
        cloneRow.flip(0, numRows);
        if(cloneRow.cardinality() > 0) {
            rowClusters.add(cloneRow);
        }


        //System.out.println("initial row cluster: " + rowClusters.toString());
        //System.out.println("initial col cluster: " + columnClusters.toString());
    }

    public List<List<Integer>> calculateContingencyTable(List<BitSet> rowClusters, List<BitSet> colClusters) {

        List<List<Integer>> newContingency = new ArrayList<>();
        for (int i = 0; i < rowClusters.size(); i++) {
            newContingency.add(new ArrayList<Integer>());
        }

        int r = 0;
        //int c = 0;
        for (BitSet rowCluster : rowClusters) {
            for (BitSet colCluster : colClusters) {
                int sum = 0;
//                boolean and = true;
//                boolean or = false;
                for (int i = rowCluster.nextSetBit(0); i != -1; i = rowCluster.nextSetBit(i + 1)) {
                    for (int j = colCluster.nextSetBit(0); j != -1; j = colCluster.nextSetBit(j + 1)) {
//                        and &= dataMatrix.get(i).get(j);
//                        or |= dataMatrix.get(i).get(j);
                        if(dataMatrix.get(i).get(j)) {
                            sum++;
                        }
                    }
                }
                //if(and && or || !and && !or ) sum = 1;
                newContingency.get(r).add(sum);
            }
            r++;
        }
        //System.out.println(newContingency.toString());
        return newContingency;
    }

    public List<Integer> calculateContingencyRows(List<List<Integer>> contingency) {

        List<Integer> newContingencyRows = new ArrayList<>();
        for (int i = 0; i < contingency.size(); i++) {
            int rowSum = 0;
            for (int j = 0; j < contingency.get(i).size(); j++) {
                rowSum += contingency.get(i).get(j);
            }
            newContingencyRows.add(rowSum);
        }
        System.out.println(newContingencyRows.toString());
        return newContingencyRows;
    }

    public void updatePartition(List<Integer> partitions, BitSet object, int fromPartitionNum, int toPartitionNum) {
        if(fromPartitionNum < partitions.size() && toPartitionNum < partitions.size()) {
            partitions.set(toPartitionNum, partitions.get(toPartitionNum) + object.cardinality());
            partitions.set(fromPartitionNum, partitions.get(fromPartitionNum) - object.cardinality());
        }
        //System.out.println(partitions.toString());
    }

    public int addNewRowCluster(){
        rowClusters.add(new BitSet());
        contingencyRows.add(0);
        List<Integer> list = new ArrayList<>();
        for(int i=0; i<contingencyColumns.size(); i++) {
            list.add(0);
        }
        contingency.add(list);
        return rowClusters.size()-1;
    }

    public int addNewColCluster(){
        columnClusters.add(new BitSet());
        contingencyColumns.add(0);
        for(int i=0; i<contingencyRows.size(); i++){
            contingency.get(i).add(0);
        }

        return columnClusters.size()-1;
    }

    public void removeEmptyRowCluster(int clNum) {
        rowClusters.remove(clNum);
        contingencyRows.remove(clNum);
        contingency.remove(clNum);
    }

    public void removeEmptyColumnCluster(int clNum) {
        columnClusters.remove(clNum);
        contingencyColumns.remove(clNum);
        for(int i=0; i<contingencyRows.size(); i++) {
            contingency.get(i).remove(clNum);
        }
    }

    public void updateContingencyTable(BitSet object, List<BitSet> partitions,int fromPartitionNum, int toPartitionNum, boolean isRowPartition) {
//        if(isRowPartition)
//            System.out.println("Row");
//        else System.out.println("column");
//        System.out.println("before update");
//        System.out.println(contingency.toString());
//        System.out.println(object.toString());
//        System.out.println(fromPartitionNum);
//        System.out.println(toPartitionNum);
//        System.out.println(rowClusters);
//        System.out.println(columnClusters);
        int i = 0;
        for (BitSet partition : partitions) {
            BitSet objectClone = (BitSet) object.clone();
            objectClone.and(partition);
            int num = objectClone.cardinality();
            if (isRowPartition) {
                contingency.get(fromPartitionNum).set(i, contingency.get(fromPartitionNum).get(i) - num);
                contingency.get(toPartitionNum).set(i, contingency.get(toPartitionNum).get(i) + num);
            } else {
                contingency.get(i).set(fromPartitionNum, contingency.get(i).get(fromPartitionNum) - num);
                contingency.get(i).set(toPartitionNum, contingency.get(i).get(toPartitionNum) + num);
            }
            i++;
        }
        //System.out.println("after update");
        //System.out.println(contingency.toString());
    }

    public void calculateContingencyTotal() {
        for (BitSet row : dataMatrix) {
            contingencyTotal += row.cardinality();
        }
    }

    public List<Integer> calculateContingencyCols(List<List<Integer>> contingency) {

        List<Integer> newContingencyCols = new ArrayList<Integer>();
        for (int i = 0; i < contingency.size(); i++) {
            for (int j = 0; j < contingency.get(i).size(); j++) {
                if (newContingencyCols.size() < j + 1) {
                    newContingencyCols.add(0);
                }
                newContingencyCols.set(j, newContingencyCols.get(j) + contingency.get(i).get(j));
            }
        }
        System.out.println(newContingencyCols.toString());
        return newContingencyCols;
    }

    public void runCoClust() {
        int t = 0;

        while (t <= numOfIter) {
            optimizePartition(rowClusters, true);
            optimizePartition(columnClusters, false);
            t++;
        }
    }

    public int optimizeRowPartition(int i, int cluster, int object, int optimalCluster) {

        int opt = optimalCluster;
        updateContingencyTable(dataMatrix.get(object), columnClusters, cluster, i, true);
        updatePartition(contingencyRows, dataMatrix.get(object), cluster, i);
        double newError = goodmanKruskal(contingencyRows, contingencyColumns, contingency);
        if (newError < minErrorRow) {
            minErrorRow = newError;
            opt = i;
        }

        rowClusters.get(i).clear(object);
        updateContingencyTable(dataMatrix.get(object), columnClusters, i, cluster, true);
        updatePartition(contingencyRows, dataMatrix.get(object), i, cluster);

        return opt;
    }

    public int optimizeColumnPartition(int i, int cluster, int object, int optimalCLuster) {

        int opt = optimalCLuster;
        BitSet objectCol = new BitSet();
        //System.out.println("datamatrix");
        //System.out.println(dataMatrix.toString());
        for(int j=0; j < dataMatrix.size(); j++) {
            if(dataMatrix.get(j).get(object)) {
                objectCol.set(j);
            }
        }
        updateContingencyTable(objectCol, rowClusters,cluster, i, false);
        updatePartition(contingencyColumns, objectCol, cluster, i);
        double newError = goodmanKruskal(contingencyRows, contingencyColumns, contingency);
        if (newError < minErrorColumn) {
            minErrorColumn = newError;
            opt = i;
        }
        columnClusters.get(i).clear(object);
        updateContingencyTable(objectCol, rowClusters, i, cluster, false);
        updatePartition(contingencyColumns, objectCol, i, cluster);

        return opt;
    }


    public void optimizePartition(List<BitSet> modifiablePartitions, boolean isRowPartition) {
        Random rand = new Random();
        // Randomly select a partition
        int len = modifiablePartitions.size();
        int cluster = rand.nextInt(len);

        // Randomly select an object
        int num = 1;
        int object = -1;
        for (int i = modifiablePartitions.get(cluster).nextSetBit(0); i != -1; i = modifiablePartitions.get(cluster).nextSetBit(i + 1)) {
            if (rand.nextInt(num) == 0) {
                object = i;
            }
            num++;
        }
        //System.out.println(object);
        //int object = U.get(cluster).nextSetBit(0);
        // Do nothing if the cluster is empty
        if (object < 0) return;
        //System.out.println(object);
        int optimalCluster = cluster;
        modifiablePartitions.get(cluster).clear(object);
        for (int i = 0; i < len; i++) {
            if (i != cluster) {
                modifiablePartitions.get(i).set(object);
                if (isRowPartition) {
                    optimalCluster = optimizeRowPartition(i, cluster, object, optimalCluster);
                } else {
                    optimalCluster = optimizeColumnPartition(i, cluster, object, optimalCluster);
                }
                modifiablePartitions.get(i).clear(object);
            }
        }

        // Check if error is minimum in empty cluster
        int newCluster = 0;
        if(isRowPartition) {
            newCluster = addNewRowCluster();
            optimalCluster = optimizeRowPartition(newCluster, cluster, object, optimalCluster);
        }
        else {
            newCluster = addNewColCluster();
            optimalCluster = optimizeColumnPartition(newCluster, cluster, object, optimalCluster);
        }

        //System.out.println("before delete: " + modifiablePartitions.toString());
        if(optimalCluster != newCluster) {
            if(isRowPartition) {
                removeEmptyRowCluster(rowClusters.size()-1);
            }
            else removeEmptyColumnCluster(columnClusters.size()-1);
        }

        //System.out.println("after delete" + modifiablePartitions.toString());


        if (optimalCluster != cluster) {
            modifiablePartitions.get(optimalCluster).set(object);
            if (isRowPartition) {
                updateContingencyTable(dataMatrix.get(object), columnClusters, cluster, optimalCluster, true);
                updatePartition(contingencyRows, dataMatrix.get(object), cluster, optimalCluster);
            } else {
                BitSet objcol = new BitSet();
                for(int j=0; j < dataMatrix.size(); j++) {
                    if(dataMatrix.get(j).get(object)) {
                        objcol.set(j);                    }
                }
                updateContingencyTable(objcol, rowClusters, cluster, optimalCluster, false);
                updatePartition(contingencyColumns, objcol, cluster, optimalCluster);
            }
            if(modifiablePartitions.get(cluster).cardinality() == 0) {
                if(isRowPartition) {
                    //System.out.println(cluster);
                    removeEmptyRowCluster(cluster);
                } else {
                    removeEmptyColumnCluster(cluster);
                }
            }
        }
        else {
            modifiablePartitions.get(cluster).set(object);
        }
        System.out.println(minErrorRow);
        System.out.println(minErrorColumn);
        //System.out.println(contingencyColumns.toString());
        //System.out.println(contingencyRows.toString());
        //System.out.println(contingency.toString());
    }

    public double goodmanKruskal(List<Integer> U, List<Integer> V, List<List<Integer>> contingency) {

//        if(U.size()!= contingency.size() || V.size()!= contingency.get(0).size()) {
//            System.out.println("size mismatch");
//            System.exit(1);
//        }
        double eu = 0.0;
        double euv = 0.0;
        for (int i = 0; i < U.size(); i++) {
            eu += (double) (contingencyTotal - U.get(i)) * U.get(i) / (double) contingencyTotal;
            for (int j = 0; j < V.size(); j++) {
                if (V.get(j) != 0) {
                    euv += (V.get(j) - contingency.get(i).get(j)) * contingency.get(i).get(j) / V.get(j);
                }
            }
        }

        return (eu - euv) / eu;
    }

    public double goodmanKruskalRow() {
        double eu = 0.0;
        double euv = 0.0;
        for(int i =0; i < contingencyRows.size(); i++) {
            eu += (double) (contingencyTotal - contingencyRows.get(i)) * contingencyRows.get(i) / (double) contingencyTotal;
            for (int j = 0; j < contingencyColumns.size(); j++) {
                if (contingencyColumns.get(j) != 0) {
                    euv += (contingencyColumns.get(j) - contingency.get(i).get(j)) * contingency.get(i).get(j) / contingencyColumns.get(j);
                }
            }
        }
        return  (eu - euv) / eu;
    }

    public double goodmanKruskalColumn() {
        double eu = 0.0;
        double euv = 0.0;
        for(int i=0 ; i<contingencyColumns.size(); i++) {
            eu += (double) (contingencyTotal - contingencyColumns.get(i)) * contingencyColumns.get(i) / (double) contingencyTotal;
            for(int j=0; j<contingencyRows.size(); j++) {
                if(contingencyRows.get(j) != 0) {
                    euv += (contingencyRows.get(j) - contingency.get(j).get(i)) * contingency.get(j).get(i) / contingencyRows.get(j);
                }
            }
        }
        return  (eu - euv) / eu;
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

        for(BitSet rowClust: rowClusters) {
            for(BitSet colClust:columnClusters) {
                for(int i=rowClust.nextSetBit(0); i!= -1; i = rowClust.nextSetBit(i+1)) {
                    for(int j=colClust.nextSetBit(0); j!=-1; j= colClust.nextSetBit(j+1)) {
                        if(dataMatrix.get(i).get(j))
                            System.out.print(1 + " ");
                        else System.out.print(0 + " ");
                    }
                    System.out.println();
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
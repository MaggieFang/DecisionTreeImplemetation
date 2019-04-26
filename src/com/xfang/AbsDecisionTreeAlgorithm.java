package com.xfang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author by Maggie Fang. Email maggie2fang@gmail.com. Date on 2019-04-25
 * Talk is cheap,show me the Code.
 **/
public abstract class AbsDecisionTreeAlgorithm {
    String className;// class name
    PrintNode root;// root of decision tree
    int TP; // true positive.predict 1,real 1
    int TN; // true negative. predict 0, real 0
    int FP; //false positive.  predict 1,real 0
    int FN; // false negative. predict 0, real 1

    /**
     * the abstract method for building decision tree.
     *
     * @param records row records
     * @return root of the decision tree
     */
    public abstract PrintNode build(ArrayList<RowRecord> records);

    /**
     * the abstract method for finding the best attribute for every step.
     *
     * @param records row records
     * @return the feature selected.
     */
    abstract String findBestAttribute(ArrayList<RowRecord> records);

    public AbsDecisionTreeAlgorithm(String className) {
        this.className = className;
    }

    /**
     * entry  of decision tree, includling actions of build trees and printTrees
     *
     * @param records
     */
    public void start(ArrayList<RowRecord> records) {
        root = build(records);
        printTree();
    }

    /**
     * print decision tree in pretty json format.
     */
    public void printTree() {
        System.out.println("print the result structure of decision tree: ");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(root);
        System.out.println(jsonOutput);
    }

    /**
     * test the decision tree. and compute the confusion matrix.
     * @param testRecords test row records data.
     * @return the accuracy of the algorithm
     */
    public float testPerformance(ArrayList<RowRecord> testRecords) {
        for (RowRecord rowRecord : testRecords) {
            String label = travelTree(rowRecord);
            if (rowRecord.isPositive() && RowRecord.POSITIVE_LABEL.equals(label)) {// True Positive
                TP++;
            } else if (rowRecord.isPositive() && RowRecord.NEGATIVE_LABEL.equals(label)) { // False Negative
                FN++;
            } else if (!rowRecord.isPositive() && RowRecord.NEGATIVE_LABEL.equals(label)) {// True Negative.
                TN++;
            } else if (!rowRecord.isPositive() && RowRecord.POSITIVE_LABEL.equals(label)) { // False Positive
                FP++;
            }

        }
        printConfusionMatrix();
        return (float) (TP + TN) / (TP + TN + FN + FP); // accuracy
    }

    /**
     * print the confusion matrix
     */
    public void printConfusionMatrix() {
        System.out.println("the confusion matrix is:");
        String actual = String.format("%20s", "actual ");
        System.out.println(actual);

        String s1 = String.format("%-9s|%-10s|%-10s|", "Predict", TP + " (TP)", FP + " (FP)");
        String s2 = String.format("%-9s|%-10s|%-10s|", "", FN + " (FN)", TN + " (TN)");
        System.out.println(s1);
        System.out.println(s2);
    }

    /**
     * travel the tree for a record to get its predict value.
     * @param record record
     * @return the predicted value
     */
    private String travelTree(RowRecord record) {
        if (root == null) {
            System.out.println("Build tree first");
            return null;
        }
        PrintNode tmp = root;
        while (tmp.getBranches() != null) {
            String selectFeature = tmp.getVertex();
            String value = record.getFeatureValue(selectFeature);
            tmp = tmp.getBranches().get(value);
            if (tmp == null) {
                System.out.println("No branch for value '"+value+"' for feature ["+selectFeature +"] here!. Skip it");
                return null;
            }
        }
        return tmp.getVertex();
    }

    /***
     * compute the positive class of records
     * @param records records.
     * @return the number of positive of records
     */
    int computePositiveCnt(ArrayList<RowRecord> records) {
        int cnt = 0;
        for (RowRecord record : records) {
            cnt += record.isPositive() ? 1 : 0;
        }
        return cnt;
    }

    /**
     * remove feature after it was selected.
     * @param records records
     * @param bestAttri the feature selected
     * @return the categories sub data based on the feature selected.
     */
    HashMap<String, ArrayList<RowRecord>> removeFeatures(ArrayList<RowRecord> records, String bestAttri) {
        HashMap<String, ArrayList<RowRecord>> category = new HashMap<>();
        for (RowRecord record : records) {
            String newKey = record.getFeatureValue(bestAttri);
            record.removeFeature(bestAttri);
            category.computeIfAbsent(newKey, v -> new ArrayList<>()).add(record);
        }
        return category;
    }


}

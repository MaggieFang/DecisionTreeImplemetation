package com.xfang.nbclassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Author by Maggie Fang <maggie2fang@gmail.com>. Date on 2019-05-14
 **/
public class NB {
    /**
     * the map contains information of each feature values for train data
     * key is the feature name, the value is statistics information for each value of this feature.
     * e.g in feature2Positive, {"F1"={"a"= StatResult1, "b"=StatResult2}},similar to feature2Negative
     */
    private static HashMap<String, HashMap<String, StatResult>> feature2Positive = new HashMap<>();
    private static HashMap<String, HashMap<String, StatResult>> feature2Negative = new HashMap<>();

    private static int positiveCount = 0; // positive label count of the train data
    private static int negativeCount = 0;// negative  label count of the train data

    private static float positivePriorProb;
    private static float negativePriorProb;

    private static int TP; // true positive.
    private static int TN; // true negative.
    private static int FP; //false positive.
    private static int FN; // false negative.


    public static void main(String[] args) {
        ArrayList<RowRecord> trainData = readFile("SpectHeart_train.csv");
        train(trainData);
        ArrayList<RowRecord> testData = readFile("SpectHeart_test.csv");
        test(testData);
    }

    /**
     * read csv file and return the Row Records
     *
     * @param fileName fileName
     * @return List of Row record.
     */
    private static ArrayList<RowRecord> readFile(String fileName) {
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            String[] names = scanner.nextLine().split(",");
            ArrayList<RowRecord> records = new ArrayList<>();

            while (scanner.hasNextLine()) { // read each row data
                String[] sp = scanner.nextLine().split(",");
                RowRecord record = new RowRecord();
                HashMap<String, String> map = new HashMap<>();
                record.setClassValue(sp[sp.length - 1]); // class label
                record.setFeatures(map);  // feature map
                for (int i = 0; i < sp.length - 1; i++) {
                    map.put(names[i], sp[i]);
                }
                records.add(record);
            }
            return records;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * train the naive bayes classifier model.
     * include works : compute the class probabilities and calculate conditional probabilities for each feature value
     * that belong to each class
     *
     * @param records the row records
     */
    private static void train(ArrayList<RowRecord> records) {
        for (RowRecord record : records) {
            if (record.isPositive()) {
                positiveCount++;  // count positive
            } else {
                negativeCount++;  // count negative
            }

            for (Map.Entry<String, String> entry : record.getFeatures().entrySet()) {//for each data row features.
                String featureName = entry.getKey(); // get the feature name.e.g "F1" .
                String featureValue = entry.getValue(); // get the feature value. e.g 'x'
                
                if (record.isPositive()) { // the label is positive 
                    HashMap<String, StatResult> map = feature2Positive.computeIfAbsent(featureName, v -> new HashMap<>());
                    StatResult result = map.getOrDefault(featureValue, new StatResult());
                    result.increaseCount(); // increase the count for this feature value.
                    map.put(featureValue, result);
                } else {
                    HashMap<String, StatResult> map = feature2Negative.computeIfAbsent(featureName, v -> new HashMap<>());
                    StatResult result = map.getOrDefault(featureValue, new StatResult());
                    result.increaseCount();
                    map.put(featureValue, result);
                }
            }
        }

        // compute class probabilities
        positivePriorProb = (float) positiveCount / (positiveCount + negativeCount);
        negativePriorProb = (float) negativeCount / (positiveCount + negativeCount);

        // compute conditional probabilities for each class
        computeConditionalProb(feature2Positive, positiveCount);
        computeConditionalProb(feature2Negative, negativeCount);
    }


    /**
     * compute the conditional probabilities of each feature value and update it in the StatResult.
     *
     * @param entries the positive or negative entries
     * @param total   the total number of positive or negative label
     */
    private static void computeConditionalProb(HashMap<String, HashMap<String, StatResult>> entries, int total) {
        for (Map.Entry<String, HashMap<String, StatResult>> entry : entries.entrySet()) {
            HashMap<String, StatResult> map = entry.getValue();//the feature value map
            for (Map.Entry<String, StatResult> innerEntry : map.entrySet()) {
                StatResult result = innerEntry.getValue();
                result.setConditionalProb((float) result.getCount() / total);
            }
        }
    }

    /**
     * make predictions for the test data. select max{P(Y)*productP(Xi|Y)} and
     * compare it to true class label to compute the confusion matrix.
     *
     * @param records test data rows
     */
    private static void test(ArrayList<RowRecord> records) {
        for (RowRecord record : records) {
            float pPositive = positivePriorProb;
            float pNegative = negativePriorProb;
            String predict = null;
            for (Map.Entry<String, String> entry : record.getFeatures().entrySet()) {//loop  each feature
                String key = entry.getKey(); // feature name
                String val = entry.getValue(); // feature value
                if (feature2Positive.get(key).get(val) != null) {
                    pPositive *= feature2Positive.get(key).get(val).getConditionalProb();
                }

                if (feature2Negative.get(key).get(val) != null) {
                    pNegative *= feature2Negative.get(key).get(val).getConditionalProb();
                }
            }


            if (pPositive > pNegative) {
                predict = RowRecord.POSITIVE_LABEL;
            } else if (pPositive < pNegative) {
                predict = RowRecord.NEGATIVE_LABEL;
            } else {
                System.out.println("equal probability to be POSITIVE/NEGATIVE!!!");
            }

            if (record.getClassValue().equals(predict)) { // true class label same with predict label
                if (record.isPositive()) {
                    TP++;
                } else {
                    TN++;
                }
            } else { // true class label differ from predict label
                if (record.isPositive()) {
                    FN++;
                } else {
                    FP++;
                }
            }
        }

        System.out.println("accuracy is " + (float) (TP + TN) / (TP + TN + FN + FP));
        printConfusionMatrix();

    }

    /**
     * print the confusion matrix
     */
    private static void printConfusionMatrix() {
        System.out.println("the confusion matrix is:");
        String actual = String.format("%20s", "actual ");
        System.out.println(actual);

        String s1 = String.format("%-9s|%-10s|%-10s|", "Predict", TP + " (TP)", FP + " (FP)");
        String s2 = String.format("%-9s|%-10s|%-10s|", "", FN + " (FN)", TN + " (TN)");
        System.out.println(s1);
        System.out.println(s2);
    }


}

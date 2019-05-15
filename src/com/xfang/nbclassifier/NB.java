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
     * data structure to store the frequency of each feature for the positive and negative class.
     * key is the feature name,the value is another map the
     */
    private static HashMap<String, HashMap<String, StatResult>> feature2Positive = new HashMap<>();
    private static HashMap<String, HashMap<String, StatResult>> feature2Negative = new HashMap<>();
    private static int positiveCount = 0;
    private static int negativeCount = 0;

    private static float positivePriorProb;
    private static float negativePriorProb;

    private static int TP; // true positive.predict 1,real 1
    private static int TN; // true negative. predict 0, real 0
    private static int FP; //false positive.  predict 1,real 0
    private static int FN; // false negative. predict 0, real 1


    public static void main(String[] args) {
        ArrayList<RowRecord> trainData = readFile("SpectHeart_train.csv");
        train(trainData);
        ArrayList<RowRecord> testData = readFile("SpectHeart_test.csv");
        test(testData);
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


    private static void test(ArrayList<RowRecord> records) {
        for (RowRecord record : records) {
            float pPositve = 0f;
            float pNegative = 0f;
            String predict = null;
            for (Map.Entry<String, String> entry : record.getFeatures().entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                if(feature2Positive.get(key).get(val) != null){
                    pPositve += feature2Positive.get(key).get(val).getConditionalProb();
                }

                if(feature2Negative.get(key).get(val) != null){
                    pNegative += feature2Negative.get(key).get(val).getConditionalProb();
                }

            }

            if(pPositve > pNegative){
                predict = RowRecord.POSITIVE_LABEL;
            }else if(pPositve < pNegative) {
                predict = RowRecord.NEGATIVE_LABEL;
            }else {
                System.out.println("equal probability to be POSITIVE/NEGATIVE!!!");
            }

           if(record.getClassValue().equals(predict)){
               if(record.isPositive()){
                   TP++;
               }else {
                   TN++;
               }
           }else {
               if(record.isPositive()){
                   FN++;
               }else {
                   FP++;
               }
           }
        }

        System.out.println("accuracy is "+(float)(TP+TN)/(TP+TN+FN+FP));
        printConfusionMatrix();

    }

    private static void train(ArrayList<RowRecord> records) {
        for (RowRecord record : records) {
            if (record.isPositive()) {
                positiveCount++;
            } else {
                negativeCount++;
            }

            for (Map.Entry<String, String> entry : record.getFeatures().entrySet()) {
                HashMap<String, StatResult> map = null;
                String featureName = entry.getKey();
                String featureValue = entry.getValue();

                if (record.isPositive()) {
                    map = feature2Positive.computeIfAbsent(featureName, v -> new HashMap<>());
                } else {
                    map = feature2Negative.computeIfAbsent(featureName, v -> new HashMap<>());
                }

                StatResult result = map.getOrDefault(featureValue, new StatResult());
                result.increaseCount();
                map.put(featureValue, result);

            }
        }

        positivePriorProb = (float) positiveCount / (positiveCount + negativeCount);
        negativePriorProb = (float) negativeCount / (positiveCount + negativeCount);

        computeConditionalProb(feature2Positive, positiveCount);
        computeConditionalProb(feature2Negative, negativeCount);
    }


    /**
     * compute the conditional probabilities of each attribute value and update it in the StatResult.
     *
     * @param entries the positive or neagtive entries
     * @param total   the total number of positive or negative label
     */
    private static void computeConditionalProb(HashMap<String, HashMap<String, StatResult>> entries, int total) {
        for (Map.Entry<String, HashMap<String, StatResult>> entry : entries.entrySet()) {
            HashMap<String, StatResult> map = entry.getValue();
            for (Map.Entry<String, StatResult> innerEntry : map.entrySet()) {
                StatResult result = innerEntry.getValue();
                result.setConditionalProb((float) result.getCount() / total);
            }
        }
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
            int classIndex = 0;
            Scanner scanner = new Scanner(file);
            ArrayList<RowRecord> records = new ArrayList<>();
            String[] names = scanner.nextLine().split(",");

            while (scanner.hasNextLine()) { // read each row data
                String[] sp = scanner.nextLine().split(",");
                RowRecord record = new RowRecord();
                HashMap<String, String> map = new HashMap<>();
                record.setClassValue(sp[sp.length - 1]);
                record.setFeatures(map);
                for (int i = 0; i < sp.length; i++) {
                    if (i != classIndex) {
                        map.put(names[i], sp[i]);
                    }
                }
                records.add(record);
            }
            return records;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }


}

package com.xfang;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Author by Maggie Fang. Email maggie2fang@gmail.com. Date on 2019-04-24
 * Talk is cheap,show me the Code.
 **/
public class DecisionTree {
    public static void main(String[] args) {
        // java -jar dt.jar <Train> <Test> <Class> <Metric>
        // Where <metric> is 0 for the Information Gain or 1 for your alternative.
        if (args.length != 4) {
            System.out.println("java <Train file> <Test file> <class variable> <Metric>, metric : 0 for IG and 1 for yours");
            return;
        }

        String trainFile = args[0];
        String testFile = args[1];
        String className = args[2];
        String metric = args[3];

        ArrayList<RowRecord> records = readFile(trainFile, className);
        ArrayList<RowRecord> testRecords = readFile(testFile, className);

        AbsDecisionTreeAlgorithm al = null;
        if ("0".equals(metric)) { // use Information Gain ID3 algorithm
            al = new ID3Algorithm(className);
        } else {  //use my own algorithm, select features with most branch
            al = new MostBranchAlgorithm(className);
        }
        // excute the decision tree algorithm
        al.start(records);
        // test the performance. print the confusion matrix, compute the accuracy
        float acc = al.testPerformance(testRecords);
        System.out.println("accuracy : " + acc);
    }

    /**
     * read csv file and return the Row Records
     *
     * @param fileName fileName
     * @param className class variable
     * @return Row records.
     */
    private static ArrayList<RowRecord> readFile(String fileName, String className) {
        try {
            File file = new File(fileName);
            int classIndex = 0;
            Scanner scanner = new Scanner(file);
            ArrayList<RowRecord> records = new ArrayList<>();
            String[] names = scanner.nextLine().split(",");
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(className)) {
                    classIndex = i;
                }
            }
            while (scanner.hasNextLine()) {
                String[] sp = scanner.nextLine().split(",");
                RowRecord record = new RowRecord();
                HashMap<String, String> map = new HashMap<>();
                record.setClassValue(sp[classIndex]);
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

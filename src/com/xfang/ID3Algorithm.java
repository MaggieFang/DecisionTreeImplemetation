package com.xfang;

import java.util.*;

/**
 * Author by Maggie Fang. Email maggie2fang@gmail.com. Date on 2019-04-24
 * Talk is cheap,show me the Code.
 **/
public class ID3Algorithm extends AbsDecisionTreeAlgorithm {
    ID3Algorithm(String className) {
        super(className);
    }

    /**
     * build decision tree with  ID3 algorithm
     * @param records row records
     * @return root of tree
     */
    @Override
    public PrintNode build(ArrayList<RowRecord> records) {
        int posCnt = computePositiveCnt(records);//number of positive class
        int negCnt = records.size() - posCnt;
        if (posCnt == records.size() || negCnt == records.size()) {//class value are same, no need to split
            String name = posCnt == records.size() ? "1" : "0";
            PrintNode node = new PrintNode(name);
            return node;
        }

        double entropy = calEntropy(records, posCnt, negCnt);
        String bestAttri = findBestAttribute(records, entropy);

        if (bestAttri == null) {// information gain is 0,select the maximum class as the leaf node.
            String name = posCnt > negCnt ? "1" : "0";
            PrintNode node = new PrintNode(name);
//            System.out.println("********************here bestAttri null,parent="+parentBest+",pos="+posCnt+",neg="+negCnt);
            return node;
        }

        PrintNode node = new PrintNode(bestAttri);
        HashMap<String, ArrayList<RowRecord>> categories = removeFeatures(records, bestAttri);
        for (Map.Entry<String, ArrayList<RowRecord>> entry : categories.entrySet()) {// recur to expand its branch
            node.addBranch(entry.getKey(), build(entry.getValue()));
        }
        return node;
    }

    @Override
    String findBestAttribute(ArrayList<RowRecord> records) {
        return null;
    }

    /**
     * use ID3 to find the best attribute
     * @param records row records
     * @param entropy H(D)
     * @return the best attribute to select for records
     */
    private String findBestAttribute(ArrayList<RowRecord> records, double entropy) {
        if (records == null || records.size() == 0 || records.get(0).getFeatures().size() == 0) {
            return null;
        }
        double maxIG = 0; //store the maximum  information gain
        String result = null; // store the attribute to select
        Set<String> attibutes = records.get(0).getFeatures().keySet(); //get the attribute set.

        for (String attr : attibutes) { // compute every attribute's information gain
            //key is the attribute value.
             //group records by the attribute value and compute the positive class and negative class for it
            HashMap<String, int[]> mapCnt = new HashMap<>();
            for (RowRecord row : records) {
                String key = row.getFeatureValue(attr);
                int[] cnts = mapCnt.computeIfAbsent(key, v -> new int[]{0, 0});
                if (row.isPositive()) {
                    cnts[0]++;
                } else {
                    cnts[1]++;
                }
            }

            double curEntropy = 0;
            //compute the entropy for current attribute  H(D|A)
            for (Map.Entry<String, int[]> entry : mapCnt.entrySet()) {
                curEntropy += calCurrentEntropy(records.size(), entry.getValue()[0], entry.getValue()[1]);
            }
            double curIG = entropy - curEntropy;
            if (maxIG < curIG) { // update the maxIG and the feature with maxIG
                maxIG = curIG;
                result = attr;
            }
        }
        return result;
    }

    /**calculate H(D|Ai)
     * @param sampleSize the total row record size
     * @param pos the number of positive class for Ai
     * @param neg the number of negative class for Ai
     * @return H(D|Ai)
     */
    private double calCurrentEntropy(int sampleSize, int pos, int neg) {
        int total = pos + neg;
        double pos_prob = (double) pos / total;
        double neg_prob = (double) neg / total;
        double pre = (double) total / sampleSize;
        return pre * (-plogp(pos_prob) - plogp(neg_prob));
    }

    /**
     * calcuate Entropy  H(D)
     * @param records row records
     * @param posCnt number of positive class
     * @param negCnt number of negative class
     * @return entropy
     */
    private double calEntropy(ArrayList<RowRecord> records, int posCnt, int negCnt) {
        if (records.size() == 0) return -1;

        double pos_prob = (double) posCnt / records.size();
        double neg_prob = (double) negCnt / records.size();
        return -plogp(pos_prob) - plogp(neg_prob);
    }

    /**
     * calculate X*log2{X}
     * @param value value
     * @return result
     */
    private double plogp(double value) {
        if (value == 0) {
            return 0;
        }
        double a = value * (Math.log(value) / Math.log(2));
        if (!Double.isNaN(a)) {
            return a;
        } else {
            return 0;
        }
    }

}

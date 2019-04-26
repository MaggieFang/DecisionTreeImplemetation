package com.xfang;

import java.util.*;

/**
 * Author by Maggie Fang. Email maggie2fang@gmail.com. Date on 2019-04-25
 * Talk is cheap,show me the Code.
 **/

/**
 * my own selection metrics. select the feature with max branches to expand.
 */
public class MostBranchAlgorithm extends AbsDecisionTreeAlgorithm {

    public MostBranchAlgorithm(String className) {
        super(className);
    }

    /**
     * build decision tree witrh max branch algorithm
     * @param records row records
     * @return root of tree
     */
    @Override
    public PrintNode build(ArrayList<RowRecord> records) {
        int posCnt = computePositiveCnt(records); //number of positive class
        int negCnt = records.size() - posCnt; ////number of negative class
        if (posCnt == records.size() || negCnt == records.size()) {//class value are same, no need to split
            String name = posCnt == records.size() ? "1" : "0";
            PrintNode node = new PrintNode(name);
            return node;
        }

        String bestAttri = findBestAttribute(records);
        if (bestAttri == null) {
            String name = posCnt > negCnt ? "1" : "0";
            PrintNode node = new PrintNode(name);
            return node;
        }

        PrintNode node = new PrintNode(bestAttri);
        HashMap<String, ArrayList<RowRecord>> categories = removeFeatures(records, bestAttri);
        for (Map.Entry<String, ArrayList<RowRecord>> entry : categories.entrySet()) { // recur to expand its branch
            node.addBranch(entry.getKey(), build(entry.getValue()));
        }
        return node;
    }

    /**
     * the implementation of attribute selection
     * @param records row records
     * @return attribute name
     */
    @Override
    String findBestAttribute(ArrayList<RowRecord> records) {
        if (records == null || records.size() == 0 || records.get(0).getFeatures().size() == 0) {
            return null;
        }

        double maxBranch = 0; // store the max branch
        String result = null; // store the attribute with max branch
        Set<String> attibutes = records.get(0).getFeatures().keySet(); // attribute set
        for(String attr: attibutes){ // iterate every attribute. the find their unique values(the branch it can expand)
            HashSet<String> values = new HashSet<>();
            for(RowRecord rowRecord:records){
                String v = rowRecord.getFeatureValue(attr);
                values.add(v);
            }
            if(maxBranch < values.size()){ // uodate the max branch value and its attribute name
                maxBranch = values.size();
                result = attr;
            }
        }
        return result;
    }


}

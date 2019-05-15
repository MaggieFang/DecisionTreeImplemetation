package com.xfang.nbclassifier;

import java.util.HashMap;

/**
 * Author by Maggie Fang <maggie2fang@gmail.com>. Date on 2019-05-14
 **/

/**
 * record for each row , including fields of class label value and a map for features.(key is the feature name,and value is the feature value)
 */
public class RowRecord {
    public static final String POSITIVE_LABEL = "1";
    public static final String NEGATIVE_LABEL = "0";

    private String classValue;  // class label. positive/negative
    private HashMap<String, String> features; // the features map

    public String getClassValue() {
        return classValue;
    }

    public boolean isPositive() {
        return POSITIVE_LABEL.equals(classValue);
    }

    public void setClassValue(String classValue) {
        this.classValue = classValue;
    }

    public HashMap<String, String> getFeatures() {
        return features;
    }

    public void setFeatures(HashMap<String, String> features) {
        this.features = features;
    }
}

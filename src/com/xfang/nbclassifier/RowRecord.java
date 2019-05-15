package com.xfang.nbclassifier;

import java.util.HashMap;

/**
 * Author by Maggie Fang <maggie2fang@gmail.com>. Date on 2019-05-14
 **/
public class RowRecord {
    public static final String POSITIVE_LABEL ="1";
    public static final String NEGATIVE_LABEL ="0";

    private String classValue;  // class label. positive/negative
    private HashMap<String,String> features; // the features map

    public String getClassValue() {
        return classValue;
    }

    public boolean isPositive(){
        return POSITIVE_LABEL.equals(classValue);
    }

    public void setClassValue(String classValue) {
        this.classValue = classValue;
    }

    public HashMap<String,String> getFeatures() {
        return features;
    }

    /**
     * return the value of a feature
     * @param key feature
     * @return value of the feature
     */
    public String getFeatureValue(String key){
        return features.get(key);
    }

    /**
     * remove a feature from a row record
     * @param key the feature to remove
     */
    public void removeFeature(String key){
        features.remove(key);
    }

    public void setFeatures(HashMap<String,String> features) {
        this.features = features;
    }
}

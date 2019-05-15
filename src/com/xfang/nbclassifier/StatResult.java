package com.xfang.nbclassifier;

/**
 * Author by Maggie Fang <maggie2fang@gmail.com>. Date on 2019-05-14
 **/

/**
 * statistics result for each feature value for each label. including information frequency and conditional probability.
 */
public class StatResult {
    private int count = 0;
    private float conditionalProb = 0f;

    public int getCount() {
        return count;
    }


    public void increaseCount() {
        this.count++;
    }

    public float getConditionalProb() {
        return conditionalProb;
    }

    public void setConditionalProb(float conditionalProb) {
        this.conditionalProb = conditionalProb;
    }
}

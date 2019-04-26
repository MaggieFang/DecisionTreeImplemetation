package com.xfang;

import java.util.HashMap;

/**
 * Author by Maggie Fang. Email maggie2fang@gmail.com. Date on 2019-04-24
 * Talk is cheap,show me the Code.
 **/

public class PrintNode {
    private String vertex; // vertex in the decision tree. when it is leaf, it is the label value.Otherwise it is the feature name
    private HashMap<String, PrintNode> branches; // the branch to expand. it will be null if the node is leaf

    public PrintNode(String featureName) {
        this.vertex = featureName;
    }

    public String getVertex() {
        return vertex;
    }

    public void addBranch(String key, PrintNode node) {
        if (branches == null) branches = new HashMap<>();
        branches.put(key, node);
    }

    public HashMap<String, PrintNode> getBranches() {
        return branches;
    }
}

### 1. Overall implementation 

- In my implementation. I define a class `RowRecord` with the below field to represent a row record data.

```java
 public static final String POSITIVE_LABEL ="1";
 public static final String NEGATIVE_LABEL ="0";
 private String classValue;  // class value. positive/negative
 private HashMap<String,String> features; // the features map
....... 
```

- define the `PrintNode` to represent the relation of decision tree structure. 

```java
public class PrintNode {
    private String vertex; // vertex in the decision tree. when it is leaf, it is the label value.Otherwise it is the feature name
    private HashMap<String, PrintNode> branches; // the branch to expand. it will be null if the node is leaf  
   .......
}
```

- Define an abstract class `AbsDecisionTreeAlgorithm` to define the framework for decision  tree algorithm. It inlcude some common logic for decision tree alogorithm. e.g compute the number of positive/negative label,print decision tree, test traversal logic, etc. 

  It also include two abstract methods. One is `public abstract PrintNode build(List<RowRecord> records);` to build the  decision tree and return the print node of tree structure. The other one is `abstract String findBestAttribute(ArrayList<RowRecord> records);` the selection algorithm  to select the best attribute on every step. They both  implemented by the subclass of it to implement the specifically.

- in application entry.I parse the command line to extract every parameter. then read file data and based on the metrics parameter to instance DecisionTreeAlgorithm class. it includes `ID3Algorithm` and `MostBranchAlgorithm` ,which will be discussed latter. Then excute the decision tree algorithm and then test the performance to compute the confusion matrix and the accuracy.

  ```java
   public static void main(String[] args) {
           ........
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
  ```

### 2. ID3 algorithm to build decision tree

#### 2.1 algorithm description:

uses Entropy function and Information gain as metrics. on every step select the attribute with maximum information gain. Specifically: 

> 1. compute the entropy for data-set H(D)
> 2. for every attribute/feature:
>         1.calculate entropy for all categorical values Entropy($A_i$)
>         2.take average information entropy for the current attribute H(D|A) 
>         3.calculate gain information for the current attribute IG = H(D) - H(D|A)
> 3. pick the highest gain attribute.
> 4. Repeat until we get the tree we desired. the border cases:
>    - If all positive or all negative class remain, label that node “1” or “0” accordingly
>    - if the information gain is zero. Select the class label with a majority.

Please seen the well-commented source code `ID3Algorithm.java` for its specific implemtation.

#### 2.2 run and test

> use the command java -jar DecisionTreeImplementation.jar <trainfile> <testfile> <class varialbe> <metrics>.  ,metrics is 0

### 3. New selection algorithm 

#### 3.1 algorithm description 

In my own selection algorithm. Every  time  I select the attribute with the maximum unique values.  such that it can expand branch as many as possible at the same level. In this way, I can try to divide the data sets as many groups as possible. With smaller data sets, it is more likely to  reach the leaf node. the implementation is shown as follows:

```java
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
```

#### 3.2 run and test

>  use the command java -jar DecisionTreeImplementation.jar <trainfile> <testfile> <class varialbe> <metrics> , metric value is 1
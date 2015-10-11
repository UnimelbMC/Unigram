package co.example.junjen.mobileinstagram.suggestion;

import android.util.Log;
import java.util.ArrayList;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by Tou on 10/6/2015.
 */
public class Classification {

    //Suggestion class to retrieve needed info to build classifier
    private static Suggestion suggestion;

    //Attributes of suggested users class
    private static ArrayList<Attribute> suggAttributeList;
    //Attributes of not suggested users class
    private static ArrayList<Attribute> notSuggAttributeList;
    //Attributes names of suggested users class
    private static ArrayList<String> suggAttributeNames;
    //Attributes names of not suggested users class
    private static ArrayList<String> notSuggAttributeNames;

    //Dataset to be filled and used to build classifier
    private static Instances dataset;

    // Class attribute
    private Attribute cls;
    // Class attribute names
    private final String suggestedCls = "suggested";
    private final String notSuggestedCls = "notSuggested";

    //Updateable Naive Bayes classifier
    private static NaiveBayesUpdateable nb;
    //Dataset to be predicted
    private static Instances toPredDataset;

    //Possible users to be classified
    private static ArrayList<String> possibleUsers;

    // Classification constructor for userId
    // that builds an updateable Naive Bayes classifier
    public Classification(String userId){
        suggestion = new Suggestion(userId);
        suggAttributeList = new ArrayList<Attribute>();
        notSuggAttributeList = new ArrayList<Attribute>();
        suggAttributeNames = suggestion.getSuggestedUsersIdList();
        notSuggAttributeNames = suggestion.getNotSuggestedUsersIdList();
        possibleUsers = suggestion.getPossibleUsersId();

      // Creating the NB classifier
        createClsLabels();
        loadDataset();
        loadClassifiedData();
        buildNB();
//        classifyPossibleUsers();
    }

    /*Method
    *   Create the attributes for the dataset
    * @params
    * @returns
    * */
    public void createAttributes(){
        FastVector featureLabels = new FastVector();
        featureLabels.addElement("0");
        featureLabels.addElement("1");

        // Suggested users attributes
        for(String attName: suggAttributeNames){
            suggAttributeList.add(new Attribute(attName, featureLabels));
        }

        // Not suggested users attributes
        for(String attName: notSuggAttributeNames){
            notSuggAttributeList.add(new Attribute(attName, featureLabels));
        }
    }

    /*Method
    *   Create class labels attribute
    * @params
    * @returns
    * */
    public void createClsLabels(){
        FastVector labels = new FastVector();
        labels.addElement(notSuggestedCls);
        labels.addElement(suggestedCls);
        cls = new Attribute("class", labels);
    }

    /*Method
    *   Load the dataset with the attributes and classes to be used.
    * @params
    * @returns
    * */
    public void loadDataset() {
        createAttributes();
        FastVector attributes = new FastVector();

        // Add the sugg and not sugg attributes
        for (Attribute a : this.suggAttributeList) {
            attributes.addElement(a);
        }
        for (Attribute a : this.notSuggAttributeList) {
            attributes.addElement(a);
        }
        //Add the class attribute
        attributes.addElement(cls);

        this.dataset = new Instances("dataset", attributes, 20);
        this.toPredDataset = new Instances("dataset", attributes, 20);
        // Setting the class attribute for the datasets
        dataset.setClassIndex(dataset.numAttributes() - 1);
        toPredDataset.setClassIndex(dataset.numAttributes() - 1);
    }

    /*Method
    *   Create an instance for an unclassified userId
    * @params
    *   userId: user ID on which an instance is going to be created
    * @returns
    *   Instance of the user with userId
    * */
    public Instance createUnclassifiedInstance(String userId){
        double[] values = new double[dataset.numAttributes()];
        ArrayList<String> list = new Suggestion(userId).getSuggestedUsersIdList();

        int suggIndex;
        int suggAttNamesSize = suggAttributeNames.size();
        int notSuggIndex;

        if(list.size()!=0){
            for(String usr : list) {
                suggIndex= suggAttributeNames.indexOf(usr);
                notSuggIndex= notSuggAttributeNames.indexOf(usr);
                if(suggIndex != -1){
                    values[suggIndex] = dataset.attribute(suggIndex).indexOfValue("1");
                }else if(notSuggIndex != -1){
                    values[suggAttNamesSize+notSuggIndex] =
                                dataset.attribute(suggAttNamesSize+notSuggIndex).indexOfValue("1");
                }
            }
            for(int i2=0; i2< values.length; i2++){
                if(values[i2]==0){
                    values[i2] = dataset.attribute(i2).indexOfValue("0");
                }
            }
        }else{
            for(int i2=0; i2< values.length; i2++){
                values[i2] = dataset.attribute(i2).indexOfValue("0");
            }
        }
        Instance inst = new Instance(1.0, values);
        return inst;
    }


    /*Method
    *   Create an instance for a classified instance
    * @params
    *   userId: user on which a classified instances is created
    *   cls: label (class) the user is classified under either sugg or not sugg
    * @returns
    *   Labeled instance of user with userId
    * */
    public Instance createClassifiedInstance(String userId, String cls){
        double[] values = new double[dataset.numAttributes()];
        ArrayList<String> list = new Suggestion(userId).getSuggestedUsersIdList();
        int suggIndex;
        int suggAttNamesSize = suggAttributeNames.size();
        int notSuggIndex;
        if(list.size()!=0) {
            for (String usr : list) {
                suggIndex = suggAttributeNames.indexOf(usr);
                notSuggIndex = notSuggAttributeNames.indexOf(usr);
                if (suggIndex != -1) {
                    values[suggIndex] = dataset.attribute(suggIndex).indexOfValue("1");
                } else if (notSuggIndex != -1) {
                    values[suggAttNamesSize + notSuggIndex] =
                            dataset.attribute(suggAttNamesSize + notSuggIndex).indexOfValue("1");
                }
            }
        }
        values[dataset.numAttributes()-1]= dataset.attribute(dataset.numAttributes()-1).indexOfValue(cls);
        Instance inst = new Instance(1.0, values);
        return inst;
    }

    /*Method
    *   Load the classified(labeled) data
    * @params
    * @returns
    * */
    public void loadClassifiedData(){
        // load not suggested class instances
        for (String usr : notSuggAttributeNames) {
            dataset.add(createClassifiedInstance(usr, notSuggestedCls));
        }
        // load suggested class instances
        for (String usr : suggAttributeNames){
            dataset.add(createClassifiedInstance(usr, suggestedCls));
        }
    }

    /*Method
    *   Build NB classifier
    * @params
    * @returns
    * */
    public void buildNB(){
        nb = new NaiveBayesUpdateable();
        int i = dataset.numAttributes() -1;
        try {
            nb.buildClassifier(dataset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Method
    *   Classify an unclassified(unlabeled) user with userId
    * @params
    *   userId: user with userId to be classified
    * @returns
    * */
    public void classify(String userId){
        Instance unlabeled = createUnclassifiedInstance(userId);
        toPredDataset.add(unlabeled);
        int lastInstanceIndex = toPredDataset.numInstances()-1;
        double pred=0;
        try {
            pred = nb.classifyInstance(toPredDataset.instance(lastInstanceIndex));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String cls = toPredDataset.classAttribute().value((int) pred);
        Log.d("pred_forUserId", cls);
    }

    /*Method
    *   Classify possible unlabeled(unclassified) users
    * @params
    * @returns
    * */
    public void classifyPossibleUsers(){
//        Log.d("clasPosUsr", "startClasPosUsr");
        for(String usr : possibleUsers){
            classify(usr);
        }
    }


}

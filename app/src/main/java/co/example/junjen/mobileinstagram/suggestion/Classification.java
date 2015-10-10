package co.example.junjen.mobileinstagram.suggestion;


import android.util.Log;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by Tou on 10/6/2015.
 */
public class Classification {

    private static Suggestion suggestion;
    private static String selfUserId;
    private static ArrayList<Attribute> suggAttributeList;
    private static ArrayList<Attribute> notSuggAttributeList;
    private FastVector featureLabels;
    private Attribute cls;
    private static ArrayList<String> suggAttributeNames;
    private static ArrayList<String> notSuggAttributeNames;
//    private ArrayList<String> userIdList;
    private static Instances dataset;
    private static NaiveBayes nb;


    private final String suggestedCls = "suggested";
    private final String notSuggestedCls = "notSuggested";

    public Classification(String userId){
        this.selfUserId = userId;
        suggAttributeList = new ArrayList<Attribute>();
        notSuggAttributeList = new ArrayList<Attribute>();
        featureLabels = new FastVector();
        suggAttributeNames = new ArrayList<String>();
        notSuggAttributeNames = new ArrayList<String>();
        Log.d("classification", "this is classificati");

        createInstances();
        loadData();
        buildNB();


    }

    public void createAttributes(){
        String feature_name;
        suggestion = new Suggestion(selfUserId);
        featureLabels.addElement("0");
        featureLabels.addElement("1");

        String attName;
        // Suggested users attributes
        for(int i=0; i< suggestion.getSuggestedUsersIdList().size(); i++){
            attName = suggestion.getSuggestedUsersIdList().get(i);
            suggAttributeList.add(new Attribute(attName, featureLabels));
            suggAttributeNames.add(attName);
//            featureLabels.addElement(suggestion.getSuggestedUsersIdList().get(i));
        }

        // Not suggested users attributes
        for(int i=0; i< suggestion.getNotSuggestedUsersIdList().size(); i++){
            attName = suggestion.getNotSuggestedUsersIdList().get(i);
            notSuggAttributeList.add(new Attribute(attName, featureLabels));
            notSuggAttributeNames.add(attName);
//            featureLabels.addElement(suggestion.getNotSuggestedUsersIdList().get(i));
        }
        Log.d("attributeList", suggAttributeList.toString());
        Log.d("attributeList", notSuggAttributeList.toString());
//        Log.d("featureLabels", featureLabels.toString());
    }

    public void createClsLabels(){
        FastVector labels = new FastVector();
        labels.addElement(suggestedCls);
        labels.addElement(notSuggestedCls);
        cls = new Attribute("class", labels);
    }

    public void createInstances(){
        createAttributes();
        createClsLabels();
        FastVector attributes = new FastVector();
        for(Attribute a : this.suggAttributeList){
            attributes.addElement(a);
        }
        for(Attribute a : this.notSuggAttributeList){
            attributes.addElement(a);
        }
        attributes.addElement(cls);
        this.dataset = new Instances("user-dataset",attributes,20);
//        Log.d("dataset",dataset.attribute(attributeNames.size()).toString());
    }

    public Instance createUnknownInstance(String userId){
//        get suggested users and find their attributes same for not suggested
        double[] values = new double[dataset.numAttributes()];
        ArrayList<String> list = new Suggestion(userId).getSuggestedUsersIdList();

        int suggIndex;
        int suggAttNamesSize = suggAttributeNames.size();
        int notSuggIndex;

        if(list.size()!=0){
//      if feature is present then 1 else 0
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
        Log.d("list", list.toString());
        Log.d("values",values.toString());
        return inst;
    }
//Create an instance for a classified instance
    public Instance createClassifiedInstance(String userId, String cls){
//        get suggested users and find their attributes same for not suggested
        double[] values = new double[dataset.numAttributes()];
        ArrayList<String> list = new Suggestion(userId).getSuggestedUsersIdList();

        int suggIndex;
        int suggAttNamesSize = suggAttributeNames.size();
        int notSuggIndex;

        if(list.size()!=0){
//      if feature is present then 1 else 0
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
        values[dataset.numAttributes()]= dataset.attribute(dataset.numAttributes()).indexOfValue(cls);
        Instance inst = new Instance(1.0, values);
        Log.d("list",list.toString());
        Log.d("values",values.toString());
        return inst;
    }

    public void loadData(){
        for (String usr : suggAttributeNames){
            dataset.add(createClassifiedInstance(usr, suggestedCls));
        }

        for (String usr : notSuggAttributeNames) {
            dataset.add(createClassifiedInstance(usr, notSuggestedCls));
        }
    }


    public void buildNB(){
        nb = new NaiveBayes();
        int i = dataset.numAttributes() -1;
//        Instances trainSet = ;

        try {
            nb.buildClassifier(dataset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void classify(String userId){
        Instance unlabeled = createUnknownInstance(userId);
//        dataset.numAttributes()-1;
        double pred=-1;
        try {
            pred = nb.classifyInstance(unlabeled);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("pred_forUserId", userId+ (Double.toString(pred)));
    }

}

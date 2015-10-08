package co.example.junjen.mobileinstagram.suggestion;


import android.util.Log;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * Created by Tou on 10/6/2015.
 */
public class Classification {

    private static Suggestion suggestion;
    private static String selfUserId;
    private ArrayList<Attribute> attributeList;
    private FastVector featureLabels;
    private Attribute cls;
//    private ArrayList<String> userIdList;
    private Instances dataset;

    public Classification(){
        this.selfUserId = "self";
        attributeList = new ArrayList<Attribute>();
        featureLabels = new FastVector();
        Log.d("classification","this is classificati");
        createInstances();
    }


    public void createAttributes(){
        String feature_name;
        suggestion = new Suggestion(selfUserId);
        featureLabels.addElement("0");
        featureLabels.addElement("1");
        // Suggested users attributes
        for(int i=0; i< suggestion.getSuggestedUsersIdList().size(); i++){
            attributeList.add(new Attribute(suggestion.getSuggestedUsersIdList().get(i),
                                                                                    featureLabels));
//            featureLabels.addElement(suggestion.getSuggestedUsersIdList().get(i));
        }
        // Not suggested users attributes
        for(int i=0; i< suggestion.getNotSuggestedUsersIdList().size(); i++){
            attributeList.add(new Attribute(suggestion.getNotSuggestedUsersIdList().get(i),
                                                                                    featureLabels));
//            featureLabels.addElement(suggestion.getNotSuggestedUsersIdList().get(i));
        }

        Log.d("attributeList", attributeList.toString());
//        Log.d("featureLabels", featureLabels.toString());
    }

    public void createClsLabels(){
        FastVector labels = new FastVector();
        labels.addElement("suggest");
        labels.addElement("notSuggest");
        cls = new Attribute("class", labels);
    }

    public void createInstances(){
        createAttributes();
        createClsLabels();
        FastVector attributes = new FastVector();
        for(Attribute a : this.attributeList){
            attributes.addElement(a);
        }
        attributes.addElement(cls);

        this.dataset = new Instances("user-dataset",attributes,20);

        Log.d("dataset",dataset.toString());
    }


}

package co.example.junjen.mobileinstagram.suggestion;


import android.util.Log;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;
import java.util.List;

import co.example.junjen.mobileinstagram.network.NetParams;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * Created by Tou on 10/6/2015.
 */
public class Classification {

    public static ArrayList<Attribute> attributeList;
    Suggestion suggestion;
    public static String currUserId;
    private static final int ATTR_PER_SELF = 10;



    public Classification(){
        attributeList = new ArrayList<Attribute>();
        this.currUserId = "self";
        suggestion= new Suggestion(currUserId);

    }

//    public ArrayList getAttributes(String userId, int numberofUsers){
//        ArrayList<String> followsList = suggestion.follows(userId, numberofUsers);
//        ArrayList<Attribute> featuresList = new ArrayList<Attribute>();
//        for(int i = 0; i<10; i++){
//            featuresList.add(new Attribute(followsList.get(i)));
//        };
//
//        return featuresList;
//    }

    public void createAttributes(){
        String feature_name;
        for(int i=0; i< suggestion.getUsersIdList().size(); i++){
            if(i<10){
                feature_name = "follows_user_";
            }else{
                feature_name = "not_following_user_";
            }
            attributeList.add(new Attribute(feature_name+suggestion.getUsersIdList().get(i)));
        }
        Log.d("attributeList",attributeList.toString());
    }

    public Attribute createLabels(){
        FastVector labels = new FastVector();
        labels.addElement("suggest");
        labels.addElement("notSuggest");
        Attribute cls = new Attribute("class", labels);
        return cls;
    }

    public void createInstance(String userId){


    }


}

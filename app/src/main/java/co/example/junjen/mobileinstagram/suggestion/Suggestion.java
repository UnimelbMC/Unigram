package co.example.junjen.mobileinstagram.suggestion;

import android.util.Log;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;
import java.util.List;

import co.example.junjen.mobileinstagram.network.NetParams;
import weka.core.Attribute;

/**
 * Created by Tou on 10/8/2015.
 */
public class Suggestion {

    Instagram instagram;
    private ArrayList<String> usersIdList;
    private String userId;

    public Suggestion(String userId){
        this.instagram = new Instagram(NetParams.ACCESS_TOKEN);
        this.usersIdList = new ArrayList<String>();
        filterFinalAttributeList(userId);
//        filterFinalAttributeList("self");
    }

//    first 10 people self is following later 10 people not interested in following
    public ArrayList<String> getUsersIdList(){return this.usersIdList;}



    //the people user with userId follows, with an option to limit the amount of users returned
    private ArrayList<String> getFollowsList(String userId, int numberOfUsers, boolean limit){
        ArrayList<String> followsUsersId = new ArrayList<String>();


        try {
            List<UserFeedData> list =  instagram.getUserFollowList(userId).getUserList();
            Log.d("getuserFList", Integer.toString(list.size()));
            int i2;
            if(!limit || numberOfUsers > list.size()){
                i2  = list.size();
            }else{
                i2 = numberOfUsers;
            }

            for(int i=0; i<i2; i++){
                followsUsersId.add(list.get(i).getId());

            }
        } catch (InstagramException e) {

            e.printStackTrace();
        }
//        Log.d("followsUsersId", followsUsersId.toString());
        return followsUsersId;
    }

    //check whether user(self) is related to target user with userId
//    public boolean followedBy(String userId){
//        String outgoing;
//        String incoming;
//        boolean related = false;
//        try{
//            outgoing = instagram.getUserRelationship(userId).getData().getOutgoingStatus();
//            incoming = instagram.getUserRelationship(userId).getData().getIncomingStatus();
//            if (outgoing.equals("none")&& incoming.equals("none")){
//                related = true;
//            }
//        }catch (InstagramException e){
//            e.printStackTrace();
//        }
//        return related;
//    }

    //    Find followed_by users that userId does not follow (Not interested in follow)
    public ArrayList<String> notInterestingUsers(String userId){
        ArrayList<String> notInterestingUsers = new ArrayList<String>();
        try{
            ArrayList<String> followList = getFollowsList(userId, 50, true);
//            List<UserFeedData> followList =  instagram.getUserFollowList(userId).getUserList();
            List<UserFeedData> followedByList =  instagram.getUserFollowedByList(userId).getUserList();
            int counter = 0;
            while(notInterestingUsers.size() < 5
                    || followedByList.size() == 0 || followList.size() == 0){
                if(!followList.contains(followedByList.get(counter))){
                    notInterestingUsers.add(followedByList.get(counter).getId());
                    counter++;
                };
            }
        }catch (InstagramException e){
            e.printStackTrace();
        }

        return notInterestingUsers;
    }

////    Create a list of attributes for the suggested and not suggeste attributes
    public void filterFinalAttributeList(String userId){
        ArrayList<String> selfList = getFollowsList(userId, 10, true);
        ArrayList<String> notInterestingUsersList = notInterestingUsers(userId);
        System.out.println(selfList.toString());
        Log.d("selfList", selfList.toString());
        Log.d("notIntList",notInterestingUsersList.toString());

        for(String elem : selfList){
            usersIdList.add(elem);
        }
        String userId1;

        while(usersIdList.size()<20 && !(notInterestingUsersList.size()==0)){
            for(String userId2: notInterestingUsersList){
                int i = 0;
                ArrayList<String> userIdList= getFollowsList(userId2, 0, false);
                if(userIdList.size()!=0){

                    while(i<2 && !(userIdList.size()==0) && !(usersIdList.size()>=20)){
                        userId1 = userIdList.get(i);
                        if(!(selfList.contains(userId1))){
                            usersIdList.add(userId1);
                            i++;
                        };
                    }
                }else {
                    usersIdList.add(userId2);
                }
                if(usersIdList.size()>=20){
                    break;
                }
            }
            Log.d("userIdList",Integer.toString(usersIdList.size()));
            Log.d("userIdList",usersIdList.toString());
        }
    }
}

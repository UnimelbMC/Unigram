package co.example.junjen.mobileinstagram.suggestion;

import android.util.Log;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import co.example.junjen.mobileinstagram.network.NetParams;
import weka.core.Attribute;

/**
 * Created by Tou on 10/8/2015.
 * This class will analyze two lists:
 *      - one suggested users (users that self user is following)
 *      - the other not suggested users (users that self is followed by but not following)
 *
 */

public class Suggestion {

    Instagram instagram;
    private ArrayList<String> suggestedUsersIdList;
    private ArrayList<String> notSuggestedUsersIdList;

    private ArrayList<String> possibleUsers;
    private String userId;

    public Suggestion(String userId){
        this.instagram = new Instagram(NetParams.ACCESS_TOKEN);
        this.suggestedUsersIdList = new ArrayList<String>();
        this.notSuggestedUsersIdList = new ArrayList<String>();
        this.possibleUsers = new ArrayList<String>();
        this.userId = userId;
//        Log.d("suggest","suggestion");
    }

    public ArrayList<String> getSuggestedUsersIdList() {
        suggestedUsersIdList = fetchFollowsList(this.userId, 10, true);
        return suggestedUsersIdList;
    }

    public ArrayList<String> getNotSuggestedUsersIdList() {
        filterNotSuggetedUsersList();
//        Log.d("notsuggest",notSuggestedUsersIdList.toString());
        return notSuggestedUsersIdList;
    }

    public ArrayList<String> getPossibleUsers() {
        fetchPossibleUsers();
        return possibleUsers;
    }

    //the people user with userId follows, with an option to limit the amount of users returned
    private ArrayList<String> fetchFollowsList(String userId, int numberOfUsers, boolean limit){
        ArrayList<String> followsUsersId = new ArrayList<String>();
        try {
            List<UserFeedData> list =  instagram.getUserFollowList(userId).getUserList();
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
        Collections.sort(followsUsersId);
        return followsUsersId;
    }

    //  People that follow self but self does not follow (not interesting users)
    public ArrayList<String> fetchNotInterestingUsers(){
        ArrayList<String> notInterestingUsers = new ArrayList<String>();
        try{
            ArrayList<String> followList = fetchFollowsList(userId, 50, true);
            List<UserFeedData> followedByList =  instagram.getUserFollowedByList(userId).getUserList();
            int counter = 0;
            while(notInterestingUsers.size() < 10 && !(followedByList.size() == 0) ){
                if(!followList.contains(followedByList.get(counter))){
                    notInterestingUsers.add(followedByList.get(counter).getId());
                    counter++;
                };
            }
        }catch (InstagramException e){
            e.printStackTrace();
        }
        Collections.sort(notInterestingUsers);
//        Log.d("notInteresting", notInterestingUsers.toString());
        return notInterestingUsers;
    }


//  filtering (max 2) users that self does not follow but not interesting users follow
    public void filterNotSuggetedUsersList(){
        //  suggested self list
        ArrayList<String> selfList = getSuggestedUsersIdList();
        // not interesting users list for userId
        ArrayList<String> notInterestingUsersList = fetchNotInterestingUsers();

        String userId1;
        Log.d("filterWHile", "here");
        while(notSuggestedUsersIdList.size()<10 && !(notInterestingUsersList.size()==0)){
            for(String userId2: notInterestingUsersList){
                int i = 0;
                ArrayList<String> userIdList= fetchFollowsList(userId2, 0, false);
                if(userIdList.size()!=0){
                    while(i<2 && !(userIdList.size()==0)){
                        userId1 = userIdList.get(i);
                        // if userId1 is not an user self follows add it to not suggested
                        if(!(selfList.contains(userId1))){
                            notSuggestedUsersIdList.add(userId1);
                            i++;
                        };
                    }
                }else {
                    notSuggestedUsersIdList.add(userId2);
                }

                if(notSuggestedUsersIdList.size()>=10){
                    break;
                }
            }
//            Log.d("userIdList",Integer.toString(notSuggestedUsersIdList.size()));
//            Log.d("userIdList",notSuggestedUsersIdList.toString());
        }
        Collections.sort(notSuggestedUsersIdList);
    }


    //check whether user(self) is related to target user with userId
    public void fetchPossibleUsers(){
        for(String user : suggestedUsersIdList){
            possibleUsers.add(fetchFollowsList(user,1,true).get(0));
        }
    }




}

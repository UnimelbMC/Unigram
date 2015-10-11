package co.example.junjen.mobileinstagram.suggestion;


import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.example.junjen.mobileinstagram.network.NetParams;

/**
 * Created by Tou on 10/11/2015.
 *  ClassificationHelper helps the classification class to retrieve possible suggested users and
 *  define which users are suggested or not, so that classification can build a model and label
 *  unlabeled possible users
 *
 *  This class will analyze two lists:
 *      - Suggested users are defined as users that self user is following
 *      - The other Not Suggested users which are users that self is followed by but not following
 *
 */
public class ClassificationHelper {

    // Instagram object to realize
    Instagram instagram;

    // Suggested users ID list
    private ArrayList<String> suggestedUsersIdList;
    // Not suggested users ID list
    private ArrayList<String> notSuggestedUsersIdList;

    // Possible users to be classified ID list
    private ArrayList<String> possibleUsersId;
    //User to be perform a ClassificationHelper
    private String userId;

    // Fetch a max amount of class suggested users
    private static final int MAX_SUGG_USR_TO_FETCH = 14;
    // Fetch a max amount of class not sugg users
    private static final int MAX_NOT_SUGG_USR_TO_FETCH = 9;
    // Number of possible users to be suggested per suggested user
    private static final int NUM_POSS_USR_PER_SUGG_USR = 2;

    // ClassificationHelper for a user with userId
    public ClassificationHelper(String userId){
        this.userId = userId;
        this.instagram =  new Instagram(NetParams.ACCESS_TOKEN); //NetParams.NETWORK.getInstagram();//
        this.suggestedUsersIdList = fetchFollowsList(this.userId, MAX_SUGG_USR_TO_FETCH);;
        this.notSuggestedUsersIdList = new ArrayList<String>();
        this.possibleUsersId = new ArrayList<String>();

    }


    public ArrayList<String> getSuggestedUsersIdList() {
        return suggestedUsersIdList;
    }

    public ArrayList<String> getNotSuggestedUsersIdList() {
        // filter the not suggested list using filterNotSuggestedUsersList method
        filterNotSuggestedUsersList();
        return notSuggestedUsersIdList;
    }

    public ArrayList<String> getPossibleUsersId() {
        // fetch possible users with fetchPossible users method
        fetchPossibleUsers();
        return possibleUsersId;
    }

    /*Method to fetch people, user with userId follows,
    * with an option to limit the amount of users returned
    * @params
    *   userId: ID of the user whom follow list is fetch
    *   numberOfUsers: number of users to be fetch
    * @return
    *   String Arraylist with the users userId follows*/
    private ArrayList<String> fetchFollowsList(String userId, int numberOfUsers){
        ArrayList<String> followsUsersId = new ArrayList<String>();
        try {
            List<UserFeedData> list =  instagram.getUserFollowList(userId).getUserList();
            int i2;
            if(numberOfUsers > list.size()){
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


    /* Method to fetch people that follow this userId but this userId
    * does not follow (not interesting users to follow)
    * @params
    * @return String Arraylist of not interesting users
    * */
    public ArrayList<String> fetchNotInterestingUsers(){
        ArrayList<String> notInterestingUsers = new ArrayList<String>();
        try{
            ArrayList<String> followList = fetchFollowsList(userId, 50);
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
        return notInterestingUsers;
    }


    //
    /*Method
    *   filtering users that this userId does not follow but not interesting users follow
    * @params
    * @returns
    * */
    public void filterNotSuggestedUsersList(){
        //  suggested self list
        ArrayList<String> selfList = getSuggestedUsersIdList();
        // not interesting users list for userId
        ArrayList<String> notInterestingUsersList = fetchNotInterestingUsers();
        String userId1;
        while(notSuggestedUsersIdList.size()< MAX_NOT_SUGG_USR_TO_FETCH
                && !(notInterestingUsersList.size()==0)){
            for(String userId2: notInterestingUsersList){
                int i = 0;
                ArrayList<String> userIdList= fetchFollowsList(userId2, 10);
                if(userIdList.size()!=0){
                    while(i<2 && !(userIdList.size()==0) && notSuggestedUsersIdList.size()<10){
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

                if(notSuggestedUsersIdList.size()>=MAX_NOT_SUGG_USR_TO_FETCH){
                    break;
                }
            }
        }
        Collections.sort(notSuggestedUsersIdList);
    }


    // for people self follows, fetch who they are following. Possible users to suggest
    /*Method
    *   Users that this userId follows which might be possible candidates to suggest
    * @params
    * @returns
    * */
    public void fetchPossibleUsers(){
        ArrayList<String> list;
        for(String user : suggestedUsersIdList){
            list = fetchFollowsList(user,NUM_POSS_USR_PER_SUGG_USR);
            if(list.size()!=0){
                possibleUsersId.add(list.get(0));
            }
        }
    }


}

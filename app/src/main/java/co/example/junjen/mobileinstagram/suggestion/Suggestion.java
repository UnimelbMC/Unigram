package co.example.junjen.mobileinstagram.suggestion;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tou on 10/11/2015.
 *  Suggestion class is in charge to suggest possible users classified according to a classifier
 *  for a user with userID.
 *
 */
public class Suggestion {

    // Classification for a suggestion
    private Classification classification;

    // Classified possible users with ID and class (Suggested or Not Suggested)
    private Map<String,String> classifiedPossibleSuggestions;

    // Constructor
    public Suggestion(String userId){

        this.classification = new Classification(userId);
        this.classifiedPossibleSuggestions = classification.getClassifiedPossUsers();
        Log.d("classifiedPossSugg",this.classifiedPossibleSuggestions.toString());
    }

    /*Method
    *   Get the classified by NB model from classification, possible user suggestions
    * @params
    * @returns
    * */
    public Map<String,String> getClassifiedPossibleUserSuggestion() {
        return classifiedPossibleSuggestions;
    }




}

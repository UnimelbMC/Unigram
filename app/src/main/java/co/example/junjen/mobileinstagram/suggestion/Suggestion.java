package co.example.junjen.mobileinstagram.suggestion;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tou on 10/11/2015.
 */
public class Suggestion {

    private Classification classification;
    private ArrayList<String> suggestedUsers;
    private HashMap classifiedPossibleSuggestions;

    public Suggestion(String userId){

        this.classification = new Classification(userId);
        this.classifiedPossibleSuggestions = classification.getClassifiedPossUsers();
        Log.d("classifiedPossSugg",this.classifiedPossibleSuggestions.toString());
    }


    public HashMap getClassifiedPossibleSuggestion() {
        return classifiedPossibleSuggestions;
    }
}

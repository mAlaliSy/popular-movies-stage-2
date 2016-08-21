package Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mohammad on 29/07/16.
 */

/*
    This class is for making the access to the settings easier .
*/

public class MyPreferencesManager {


    public static final String SORTING_BY_KEY = "sort_by"; // The key of the "sort by" option stored in the SharedPreferences
    public static final String POPULAR_VALUE = "popular"; // The value for sorting by the popularity
    public static final String TOP_RATED_VALUE = "top_rated"; // The value for sorting by the ratings
    public static final String FAVORITE_VALUE = "favorite" ; // The value for showing favorite onlyy
    private static SharedPreferences sharedPreferences ;
    private static SharedPreferences.Editor editor ;


    public static SharedPreferences getPreferences (Context context){ // Getting the DefaultSharedPreferences
        if (sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences;
    }

    public static SharedPreferences.Editor getEditor (Context context){
        return getPreferences(context).edit();
    }

    public static String getSortingSetting(Context context){ // Getting the order by option stored in the SharedPreferences
        return getPreferences(context).getString(SORTING_BY_KEY,POPULAR_VALUE);
    }


    public static void setSortingSetting(Context context , String newValue){ // Store the passed sorting method in the SharedPreferences
        getEditor(context).putString(SORTING_BY_KEY , newValue).apply();
    }


}

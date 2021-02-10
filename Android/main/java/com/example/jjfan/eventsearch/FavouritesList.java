package com.example.jjfan.eventsearch;

import android.util.Log;
import java.util.HashSet;

public class FavouritesList {

    private static FavouritesList instance = null;
    private HashSet<String> set;
    private static final String TAG = FavouritesFragment.class.getSimpleName();

    private FavouritesList() {
        set = new HashSet<>();
    }
    public static FavouritesList getInstance() {
        if(instance == null) {
            instance = new FavouritesList();
        }
        return instance;
    }

    public void addEvent(String event){
        set.add(event);
        Log.e(TAG, String.valueOf(set.size()));
    }
    public void removeEvent(String event){
        set.remove(event);
        Log.e(TAG, String.valueOf(set.size()));
    }
    public boolean isFav(String event){
        return set.contains(event);
    }

}
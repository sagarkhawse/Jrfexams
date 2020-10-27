package com.skteam.jrfexams.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skteam.jrfexams.models.User;


public class Helper {

    public static void saveProfileMe(SharedPreferenceUtil sharedPreferenceUtil, User userMe) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_USER_PROFILE, new Gson().toJson(userMe, new TypeToken<User>() {
        }.getType()));
        setLoggedInUser(sharedPreferenceUtil, userMe);
    }

    public static User getProfileMe(SharedPreferenceUtil sharedPreferenceUtil) {
        User toReturn = null;
        String savedUserString = sharedPreferenceUtil.getStringPreference(Constants.KEY_USER_PROFILE, null);
        if (savedUserString != null)
            toReturn = new Gson().fromJson(savedUserString, new TypeToken<User>() {
            }.getType());
        return toReturn;
    }

    public static void setLoggedInUser(SharedPreferenceUtil sharedPreferenceUtil, User user) {
        sharedPreferenceUtil.setStringPreference(Constants.USER, new Gson().toJson(user, new TypeToken<User>() {
        }.getType()));
    }

    public static User getLoggedInUser(SharedPreferenceUtil sharedPreferenceUtil) {
        String savedUserPref = sharedPreferenceUtil.getStringPreference(Constants.USER, null);
        if (savedUserPref != null)
            return new Gson().fromJson(savedUserPref, new TypeToken<User>() {
            }.getType());
        else return null;
    }
}

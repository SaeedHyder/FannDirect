package com.app.fandirect.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.fandirect.R;
import com.app.fandirect.activities.MainActivity;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.retrofit.GsonFactory;


public class BasePreferenceHelper extends PreferenceHelper {

    private Context context;
    protected static final String KEY_LOGIN_STATUS = "islogin";
    private static final String FILENAME = "preferences";
    protected static final String Firebase_TOKEN = "Firebasetoken";
    protected static final String NotificationCount = "NotificationCount";
    protected static final String USER = "user";
    protected static final String TOKEN = "TOKEN";
    protected static final String KEY_USER = "KEY_USER";


    public BasePreferenceHelper(Context c) {
        this.context = c;
    }

    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(FILENAME, Activity.MODE_PRIVATE);
    }

    public void setLoginStatus( boolean isLogin ) {
        putBooleanPreference( context, FILENAME, KEY_LOGIN_STATUS, isLogin );
    }

    public boolean isLogin() {
        return getBooleanPreference(context, FILENAME, KEY_LOGIN_STATUS);
    }


    public String getFirebase_TOKEN() {
        return getStringPreference(context, FILENAME, Firebase_TOKEN);
    }

    public void setFirebase_TOKEN(String _token) {
        putStringPreference(context, FILENAME, Firebase_TOKEN, _token);
    }
    public int getNotificationCount() {
        return getIntegerPreference(context, FILENAME, NotificationCount);
    }

    public void setNotificationCount(int count) {
        putIntegerPreference(context, FILENAME, NotificationCount, count);
    }

    public void setUserType(String user){
        putStringPreference(context, FILENAME, USER, user);

    }


    public String getUserType() {
        return getStringPreference(context, FILENAME, USER);
    }

    public boolean isUserSelected() {
        return getUserType().equalsIgnoreCase(context.getString(R.string.user));
    }


    public String get_TOKEN() {
        return getStringPreference(context, FILENAME, TOKEN);
    }

    public void set_TOKEN(String token) {
        putStringPreference(context, FILENAME, TOKEN, token);
    }

    public UserEnt getUser() {
        return GsonFactory.getConfiguredGson().fromJson(
                getStringPreference(context, FILENAME, KEY_USER), UserEnt.class);
    }

    public void putUser(UserEnt user) {
        putStringPreference(context, FILENAME, KEY_USER, GsonFactory
                .getConfiguredGson().toJson(user));
    }



}

package com.app.fandirect.helpers;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.fragments.LoginFragment;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.facebook.AccessToken;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class FacebookLoginHelper implements FacebookCallback<LoginResult> {
  //  private List<String> permissionNeeds = Arrays.asList("public_profile", "email", "user_birthday");
    private List<String> permissionNeeds = Arrays.asList("public_profile", "email");
    private Context mContext;
    private BaseFragment baseFragment;
    private FacebookLoginListener facebookLoginListener;
    private final static String TAG = "ICA";
    public FacebookLoginHelper(Context context, BaseFragment baseFragment, FacebookLoginListener facebookLoginListener) {
        this.mContext = context;
        this.baseFragment = baseFragment;
        this.facebookLoginListener = facebookLoginListener;
    }

    public List<String> getPermissionNeeds() {
        return permissionNeeds;
    }

    @Override
    public void onSuccess(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                // Get facebook data from login
                getFacebookData(object, loginResult.getAccessToken());

            }


        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, first_name, last_name, email,gender,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {
        baseFragment.loadingFinished();
        Toast.makeText(mContext, "Login Cancelled", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onError(FacebookException e) {
        baseFragment.loadingFinished();
        Toast.makeText(mContext, "Please try again..", Toast.LENGTH_SHORT).show();
        e.printStackTrace();

        Log.i(TAG,  "facebooklog: " +e.toString());
        if (e instanceof FacebookAuthorizationException) {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
        }


    }

    private void getFacebookData(JSONObject object, AccessToken accessToken) {

        try {
            FacebookLoginEnt loginEnt = new FacebookLoginEnt();
            Bundle bundle = new Bundle();
            Log.e("facebook", object.toString());
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                bundle.putString("profile_pic", profile_pic.toString());
                loginEnt.setFacebookUProfilePicture(profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                facebookLoginListener.onSuccessfulFacebookLogin(null);
            }

            bundle.putString("idFacebook", id);
            loginEnt.setFacebookUID(id);
            bundle.putString("tokenFacebook", accessToken.getToken());
            loginEnt.setFacebookToken(accessToken.getToken());
            if (object.has("name")) {
                loginEnt.setFacebookFullName(object.getString("name"));
                bundle.putString("name", object.getString("name"));
            }
            if (object.has("first_name")) {
                loginEnt.setFacebookFirstName(object.getString("first_name"));
                bundle.putString("first_name", object.getString("first_name"));
            }
            if (object.has("last_name")) {
                loginEnt.setFacebookLastName(object.getString("last_name"));
                bundle.putString("last_name", object.getString("last_name"));
            }
            if (object.has("email")) {
                loginEnt.setFacebookEmail(object.getString("email"));
                bundle.putString("email", object.getString("email"));
            }
            if (object.has("gender")) {
                loginEnt.setFacebookGender(object.getString("gender"));
                bundle.putString("gender", object.getString("gender"));
            }
            if (object.has("birthday")) {
                loginEnt.setFacebookBirthday(object.getString("birthday"));
                bundle.putString("birthday", object.getString("birthday"));
            }
            if (object.has("location")) {
                loginEnt.setFacebookLocation(object.getJSONObject("location").getString("name"));
                bundle.putString("location", object.getJSONObject("location").getString("name"));
            }
            if (object.has("link")) {
                loginEnt.setFacebookLink(object.getString("link"));
                bundle.putString("link", object.getString("link"));
            }

            facebookLoginListener.onSuccessfulFacebookLogin(loginEnt);

        } catch (Exception e) {
            e.printStackTrace();
            facebookLoginListener.onSuccessfulFacebookLogin(null);

        }


    }

    public interface FacebookLoginListener {
        public void onSuccessfulFacebookLogin(FacebookLoginEnt LoginEnt);
    }

    public class FacebookLoginEnt {
        private String FacebookUID;
        private String FacebookUProfilePicture;
        private String FacebookToken;
        private String FacebookFirstName;
        private String FacebookLastName;
        private String FacebookEmail;
        private String FacebookGender;
        private String FacebookBirthday;
        private String FacebookLocation;
        private String FacebookLink;
        private String FacebookFullName;

        public String getFacebookUID() {
            return FacebookUID;
        }

        public void setFacebookUID(String facebookUID) {
            FacebookUID = facebookUID;
        }

        public String getFacebookUProfilePicture() {
            return FacebookUProfilePicture;
        }

        public void setFacebookUProfilePicture(String facebookUProfilePicture) {
            FacebookUProfilePicture = facebookUProfilePicture;
        }

        public String getFacebookToken() {
            return FacebookToken;
        }

        public void setFacebookToken(String facebookToken) {
            FacebookToken = facebookToken;
        }

        public String getFacebookFirstName() {
            return FacebookFirstName;
        }

        public void setFacebookFirstName(String facebookFirstName) {
            FacebookFirstName = facebookFirstName;
        }

        public String getFacebookLastName() {
            return FacebookLastName;
        }

        public void setFacebookLastName(String facebookLastName) {
            FacebookLastName = facebookLastName;
        }

        public String getFacebookEmail() {
            return FacebookEmail;
        }

        public void setFacebookEmail(String facebookEmail) {
            FacebookEmail = facebookEmail;
        }

        public String getFacebookGender() {
            return FacebookGender;
        }

        public void setFacebookGender(String facebookGender) {
            FacebookGender = facebookGender;
        }

        public String getFacebookBirthday() {
            return FacebookBirthday == null ? "" : FacebookBirthday;
        }

        public void setFacebookBirthday(String facebookBirthday) {
            FacebookBirthday = facebookBirthday;
        }

        public String getFacebookLocation() {
            return FacebookLocation;
        }

        public void setFacebookLocation(String facebookLocation) {
            FacebookLocation = facebookLocation;
        }

        public String getFacebookLink() {
            return FacebookLink;
        }

        public void setFacebookLink(String facebookLink) {
            FacebookLink = facebookLink;
        }

        public String getFacebookFullName() {
            return FacebookFullName;
        }

        public void setFacebookFullName(String facebookFullName) {
            FacebookFullName = facebookFullName;
        }
    }

}

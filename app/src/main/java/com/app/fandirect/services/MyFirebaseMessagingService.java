package com.app.fandirect.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.fandirect.R;
import com.app.fandirect.activities.MainActivity;
import com.app.fandirect.entities.NotificationCount;
import com.app.fandirect.entities.ResponseWrapper;
import com.app.fandirect.entities.TilesCountEnt;
import com.app.fandirect.fragments.LoginFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.global.WebServiceConstants;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.NotificationHelper;
import com.app.fandirect.retrofit.WebService;
import com.app.fandirect.retrofit.WebServiceFactory;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fandirect.global.AppConstants.block_user;
import static com.app.fandirect.global.AppConstants.cancel_request;
import static com.app.fandirect.global.AppConstants.declined_request;
import static com.app.fandirect.global.AppConstants.delete_user;
import static com.app.fandirect.global.AppConstants.messagePush;
import static com.app.fandirect.global.AppConstants.unfriend;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private WebService webservice;
    private BasePreferenceHelper preferenceHelper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        preferenceHelper = new BasePreferenceHelper(getApplicationContext());
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());


            if (!(remoteMessage.getData().get("type").equals(cancel_request)
                    || remoteMessage.getData().get("type").equals(declined_request)
                    || remoteMessage.getData().get("type").equals(unfriend)
                    || remoteMessage.getData().get("type").equals(block_user)
                    || remoteMessage.getData().get("type").equals(delete_user))
                    && isChatNotification(remoteMessage)) {
                buildNotification(remoteMessage);
            } else if (remoteMessage.getData().get("type").equals(block_user) || remoteMessage.getData().get("type").equals(delete_user)) {

                preferenceHelper.setLoginStatus(false);

                String message = remoteMessage.getData().get("message");
                String Type = remoteMessage.getData().get("type");
                String sender_Id = remoteMessage.getData().get("sender_id");
                String receiver_Id = remoteMessage.getData().get("receiver_id");
                String request_id = remoteMessage.getData().get("action_id");
                Intent pushNotification = new Intent(AppConstants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                pushNotification.putExtra("pushtype", Type);
                pushNotification.putExtra("sender_id", sender_Id);
                pushNotification.putExtra("receiver_id", receiver_Id);
                pushNotification.putExtra("request_id", request_id);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);


            } else {

                //buildWithoutNoitications(remoteMessage);
                String message = remoteMessage.getData().get("message");
                String Type = remoteMessage.getData().get("type");
                String sender_Id = remoteMessage.getData().get("sender_id");
                String receiver_Id = remoteMessage.getData().get("receiver_id");
                String request_id = remoteMessage.getData().get("action_id");
                Intent pushNotification = new Intent(AppConstants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                pushNotification.putExtra("pushtype", Type);
                pushNotification.putExtra("sender_id", sender_Id);
                pushNotification.putExtra("receiver_id", receiver_Id);
                pushNotification.putExtra("request_id", request_id);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
            }

        }

    }


    private void buildNotification(RemoteMessage messageBody) {

       // getNotificaitonCount(messageBody);

        if(preferenceHelper.getUser().getIsNotify()!=null && preferenceHelper.getUser().getIsNotify().equals("0")){

            String title = getString(R.string.app_name);
            String message = messageBody.getData().get("message");
            String Type = messageBody.getData().get("type");
            String sender_Id = messageBody.getData().get("sender_id");
            String receiver_Id = messageBody.getData().get("receiver_id");
            String request_id = messageBody.getData().get("action_id");

            Log.e(TAG, "message: " + message);

            Intent pushNotification = new Intent(AppConstants.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            pushNotification.putExtra("pushtype", Type);
            pushNotification.putExtra("sender_id", sender_Id);
            pushNotification.putExtra("receiver_id", receiver_Id);
            pushNotification.putExtra("request_id", request_id);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);


        }else {

            String title = getString(R.string.app_name);
            String message = messageBody.getData().get("message");
            String Type = messageBody.getData().get("type");
            String sender_Id = messageBody.getData().get("sender_id");
            String receiver_Id = messageBody.getData().get("receiver_id");
            String request_id = messageBody.getData().get("action_id");

            Log.e(TAG, "message: " + message);

            Intent resultIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
            //    resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            resultIntent.putExtra("message", message);
            resultIntent.putExtra("tapped", true);
            resultIntent.putExtra("pushtype", Type);
            resultIntent.putExtra("sender_id", sender_Id);
            resultIntent.putExtra("receiver_id", receiver_Id);
            resultIntent.putExtra("request_id", request_id);

            Intent pushNotification = new Intent(AppConstants.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            pushNotification.putExtra("pushtype", Type);
            pushNotification.putExtra("sender_id", sender_Id);
            pushNotification.putExtra("receiver_id", receiver_Id);
            pushNotification.putExtra("request_id", request_id);


            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);

            showNotificationMessage(MyFirebaseMessagingService.this, title, message, "", resultIntent);

        }
    }

    private void getNotificaitonCount(final RemoteMessage messageBody) {
        webservice = WebServiceFactory.getWebServiceInstanceWithCustomInterceptorandheader(this, WebServiceConstants.Local_SERVICE_URL);
        preferenceHelper = new BasePreferenceHelper(this);
        Call<ResponseWrapper<TilesCountEnt>> call = webservice.getHomeCount();
        call.enqueue(new Callback<ResponseWrapper<TilesCountEnt>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<TilesCountEnt>> call, Response<ResponseWrapper<TilesCountEnt>> response) {
                if (response != null && response.body() != null && response.body().getResult() != null) {
                    Integer count = response.body().getResult().getNotification_count();
                    preferenceHelper.setNotificationCount(count);

                    if (count != null && !count.equals("") && !count.equals("0")) {
                        ShortcutBadger.applyCount(getApplicationContext(), count);

                    }

                }

                String title = getString(R.string.app_name);
                String message = messageBody.getData().get("message");
                String Type = messageBody.getData().get("type");
                String sender_Id = messageBody.getData().get("sender_id");
                String receiver_Id = messageBody.getData().get("receiver_id");
                String request_id = messageBody.getData().get("action_id");

                Intent resultIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                //    resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("tapped", true);
                resultIntent.putExtra("pushtype", Type);
                resultIntent.putExtra("sender_id", sender_Id);
                resultIntent.putExtra("receiver_id", receiver_Id);
                resultIntent.putExtra("request_id", request_id);

                Intent pushNotification = new Intent(AppConstants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                pushNotification.putExtra("pushtype", Type);
                pushNotification.putExtra("sender_id", sender_Id);
                pushNotification.putExtra("receiver_id", receiver_Id);
                pushNotification.putExtra("request_id", request_id);


                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);

                showNotificationMessage(MyFirebaseMessagingService.this, title, message, "", resultIntent);
            }

            @Override
            public void onFailure(Call<ResponseWrapper<TilesCountEnt>> call, Throwable t) {

            }
        });
    }

    private boolean isChatNotification(RemoteMessage remoteMessage) {

        String sender_Id = remoteMessage.getData().get("sender_id");
        String receiver_Id = remoteMessage.getData().get("receiver_id");
        String userId = getUserId(sender_Id, receiver_Id, preferenceHelper.getUser().getId() + "");
        String type = remoteMessage.getData().get("type");

        if (preferenceHelper.isChatScreen() && preferenceHelper.getChatReceiver_id().equals(userId) && type.equals(messagePush)) {
            return false;
        } else {
            return true;
        }
    }

    protected String getUserId(String senderId, String receiverId, String myId) {
        if (myId.equals(senderId)) {
            return receiverId;
        } else {
            return senderId;
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        NotificationHelper.getInstance().showNotification(context,
                R.drawable.app_icon,
                title,
                message,
                timeStamp,
                intent);
    }


}

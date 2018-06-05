package com.app.fandirect.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.fandirect.R;
import com.app.fandirect.activities.MainActivity;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.NotificationHelper;
import com.app.fandirect.retrofit.WebService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import static com.app.fandirect.global.AppConstants.cancel_request;
import static com.app.fandirect.global.AppConstants.declined_request;
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


            if (!(remoteMessage.getData().get("type").equals(cancel_request) || remoteMessage.getData().get("type").equals(declined_request) || remoteMessage.getData().get("type").equals(unfriend))) {
                buildNotification(remoteMessage);
            } else {
                String message = remoteMessage.getData().get("message");
                String Type = remoteMessage.getData().get("type");
                String sender_Id = remoteMessage.getData().get("sender_id");
                String receiver_Id = remoteMessage.getData().get("receiver_id");
                String request_id = remoteMessage.getData().get("request_id");
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
        //getNotificaitonCount();
        String title = getString(R.string.app_name);
        String message = messageBody.getData().get("message");
        String Type = messageBody.getData().get("type");
        String sender_Id = messageBody.getData().get("sender_id");
        String receiver_Id = messageBody.getData().get("receiver_id");
        String request_id = messageBody.getData().get("request_id");
        Log.e(TAG, "message: " + message);
        Intent resultIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    /*private void getNotificaitonCount() {
        webservice = WebServiceFactory.getWebServiceInstanceWithCustomInterceptor(this, WebServiceConstants.Local_SERVICE_URL);
        preferenceHelper = new BasePreferenceHelper(this);
        Call<ResponseWrapper<countEnt>> call = webservice.getNotificationCount(preferenceHelper.getMerchantId());
        call.enqueue(new Callback<ResponseWrapper<countEnt>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<countEnt>> call, Response<ResponseWrapper<countEnt>> response) {
                preferenceHelper.setNotificationCount(response.body().getResult().getCount());
            }

            @Override
            public void onFailure(Call<ResponseWrapper<countEnt>> call, Throwable t) {

            }
        });
    }*/

    private void SendNotification(int count, JSONObject json) {

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

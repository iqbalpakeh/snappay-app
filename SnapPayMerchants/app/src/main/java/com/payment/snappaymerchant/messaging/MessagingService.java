package com.payment.snappaymerchant.messaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.payment.snappaymerchant.MainActivity;
import com.payment.snappaymerchant.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {

    private static final String LOG_TAG = MessagingService.class.getSimpleName();

    private NotificationCompat.Builder mNotificationBuilder;

    private NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(LOG_TAG, "Message data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage.getData());
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageData FCM message body received.
     */
    private void sendNotification(Map<String, String> messageData) {

        JSONObject data = new JSONObject(messageData);

        String messageTitle = "";
        String messageBody = "";

        try {
            messageTitle = data.getString("title");
            messageBody = data.getString("body");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "data = " + data.toString());
        Log.d(LOG_TAG, "title = " + messageTitle);
        Log.d(LOG_TAG, "body = " + messageBody);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (mNotificationBuilder == null) {
            Log.d(LOG_TAG, "new builder");
            mNotificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_receipt_24dp)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
        }

        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mNotificationBuilder.build());
        }

    }
}

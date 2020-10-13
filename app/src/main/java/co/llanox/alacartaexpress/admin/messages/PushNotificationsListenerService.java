package co.llanox.alacartaexpress.admin.messages;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import co.llanox.alacartaexpress.admin.ACEApplication;
import co.llanox.alacartaexpress.admin.R;
import co.llanox.alacartaexpress.admin.UICommands;
import co.llanox.alacartaexpress.mobile.ApplicationSessionHelper;
import co.llanox.alacartaexpress.mobile.Constants;


public class PushNotificationsListenerService extends FirebaseMessagingService {

    private static final int NOTIFICATION_ID = 2333;
    private static final String TAG = PushNotificationsListenerService.class.getCanonicalName();
    public PushNotificationsListenerService() {
        super();
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "NEW_TOKEN: " + token);
        ApplicationSessionHelper applicationSessionHelper = ((ACEApplication)this.getApplication()).getApplicationSession();
        applicationSessionHelper.putString(Constants.TOKEN_DEVICE_KEY,token);
    }


        @Override
    public void onMessageReceived(RemoteMessage message){
        Log.d("PushNotifications","Message "+message.getNotification().getBody());
        sendNotification(message.getNotification().getBody());
    }



    public void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, UICommands.ENTRY_POINT), 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.lacoro);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, getString(R.string.default_channel_id))
                        .setLargeIcon(icon)
                        .setContentTitle(getString(R.string.txt_push_notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setVibrate(new long[] { 1000, 1000, 0, 1000, 1000 });
        mBuilder.setSmallIcon(R.drawable.lacoro);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_channel_id),
                    getString(R.string.txt_push_notification_title),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


    }




}





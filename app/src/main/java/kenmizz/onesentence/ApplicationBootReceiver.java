package kenmizz.onesentence;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

import kenmizz.onesentence.utils.Constants;

public class ApplicationBootReceiver extends BroadcastReceiver {

    private static final String TAG = "OnesentenceBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i(TAG, "BootReceiver received");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Log.i(TAG, "Device's SDK version: " + Build.VERSION.SDK_INT);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //create new channel
                NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, context.getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(context.getString(R.string.channel_description));
                notificationManager.createNotificationChannel(channel);
            }
            SharedPreferences NotificationPrefs = context.getSharedPreferences(Constants.NOTIFICATION_PREFS, Context.MODE_PRIVATE);
            Map<String, ?> allPrefs = NotificationPrefs.getAll();
            if(allPrefs.size() > 0) {
                for(Map.Entry<String, ?> entry : allPrefs.entrySet()) {
                    int NotificationId = Integer.parseInt(entry.getKey());
                    String sentence = entry.getValue().toString();
                    Intent NotificationIntent = new Intent(context, NotificationActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .putExtra("id", NotificationId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, NotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_IMMUTABLE);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                            .setSmallIcon(R.drawable.app_notification_icon_small)
                            .setContentTitle(sentence)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setOngoing(true)
                            .addAction(R.drawable.app_notification_icon_small, context.getString(R.string.remove), pendingIntent);
                    notificationManager.notify(NotificationId, notificationBuilder.build());
                }
            }
        }
    }
}

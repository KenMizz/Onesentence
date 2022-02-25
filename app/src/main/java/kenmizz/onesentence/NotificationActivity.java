package kenmizz.onesentence;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import kenmizz.onesentence.utils.Constants;

public class NotificationActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //cancel the notification
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        int notification_id = extras.getInt("id");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notification_id);
        SharedPreferences NotificationPrefs = getSharedPreferences(Constants.NOTIFICATION_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor notificationPrefsEditor = NotificationPrefs.edit();
        notificationPrefsEditor.remove(String.valueOf(notification_id));
        notificationPrefsEditor.apply();
        finish();
    }
}

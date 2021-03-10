package kenmizz.onesentence;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static kenmizz.onesentence.MainActivity.CHANNEL_ID;
import static kenmizz.onesentence.MainActivity.NOTIFICATION_PREFS;

public class SentenceItemAdapter extends RecyclerView.Adapter<SentenceItemAdapter.SentenceViewHolder> {
    private static final String TAG = "SentenceItemAdapter";

    private ArrayList<String> mSentenceList;
    private TextView mEmptyView;
    private Context mSentenceContext;

    private boolean mIsItemClickable = false;
    private int mWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Context mActivityContext;
    private SentenceWidgetConfiguration mActivity;


    static class SentenceViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public SentenceViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.SentenceView);
        }
    }

    public SentenceItemAdapter(ArrayList<String> sentenceList, TextView emptyView, Context sentenceContext) {
        mSentenceList = sentenceList;
        mEmptyView = emptyView;
        mSentenceContext = sentenceContext;
    }

    public SentenceItemAdapter(ArrayList<String> sentenceList, int widgetId, boolean isItemClickable, Context activityContext, SentenceWidgetConfiguration activity) {
        mSentenceList = sentenceList;
        mWidgetId = widgetId;
        mIsItemClickable = isItemClickable;
        mActivityContext = activityContext;
        mActivity = activity;
    }

    @NonNull
    @Override
    public SentenceItemAdapter.SentenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_item, parent, false);
        return new SentenceViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceItemAdapter.SentenceViewHolder holder, final int position) {
        final String currentSentence = mSentenceList.get(position);
        holder.mTextView.setText(currentSentence);
        if(mIsItemClickable) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpWidget(position);
                }
            });
        } else {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setNotificationDialog(currentSentence);
                    return true;
                }
            });
            if(mSentenceList.size() <= 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.INVISIBLE);
            }
        }
    }



    @Override
    public int getItemCount() {
        return mSentenceList.size();
    }

    public String getSentence(int position) {
        return mSentenceList.get(position);
    }

    public void removeSentence(int position) {
        notifyItemRemoved(position);
        mSentenceList.remove(position);
        if(mSentenceList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public void setNotificationDialog(final String sentence) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(mSentenceContext)
                .setTitle(R.string.set_long_time_notification)
                .setMessage(mSentenceContext.getResources().getString(R.string.set_setence_notification).replace("sentence", sentence))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createLongTimeNotification(sentence);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        dialogBuilder.show();
    }

    public void setUpWidget(int position) {
        Log.d(TAG, "Click " + mSentenceList.get(position));
        SharedPreferences sharedPreferences = mActivityContext.getSharedPreferences(SentenceWidgetConfiguration.WIDGET_PREFS, MODE_PRIVATE);
        SharedPreferences sentencesAttrPreferences = mActivityContext.getSharedPreferences(MainActivity.SENATTR_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences.Editor sentencesAttributesEditor = sentencesAttrPreferences.edit(); //Sentences Attr for initialize
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mActivityContext);
        String sentence = mSentenceList.get(position);
        RemoteViews views = new RemoteViews(mActivityContext.getPackageName(), R.layout.sentence_widget);
        views.setTextViewText(R.id.SentenceTextView, sentence);
        editor.putString(mWidgetId + SentenceWidgetConfiguration.SENTENCE_TEXT, sentence);
        sentencesAttributesEditor.putFloat(mWidgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textSize", 25);
        sentencesAttributesEditor.putInt(mWidgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textColor", mActivityContext.getColor(R.color.white));
        editor.apply();
        sentencesAttributesEditor.apply();
        Intent attributeDialog = new Intent(mActivityContext, SentenceAttributeDialog.class);
        attributeDialog.putExtra("widgetId", mWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(mActivityContext, mWidgetId, attributeDialog, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.SentenceTextView, pendingIntent);
        appWidgetManager.updateAppWidget(mWidgetId, views);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
        mActivity.setResult(Activity.RESULT_OK, resultValue);
        mActivity.finish();
    }

    public void createLongTimeNotification(String sentence) {
        NotificationManager notificationManager = (NotificationManager)mSentenceContext.getSystemService(Context.NOTIFICATION_SERVICE);
        int NotificationId = new Random().nextInt();
        Intent intent = new Intent(mSentenceContext, NotificationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra("id", NotificationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(mSentenceContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mSentenceContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon_around)
                .setContentTitle(sentence)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true)
                .addAction(R.drawable.app_icon_around, mSentenceContext.getString(R.string.remove), pendingIntent);
        notificationManager.notify(NotificationId, notificationBuilder.build());
        SharedPreferences NotificationPrefs = mSentenceContext.getSharedPreferences(NOTIFICATION_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor notificationPrefsEditor = NotificationPrefs.edit();
        notificationPrefsEditor.putString(String.valueOf(NotificationId), sentence);
        notificationPrefsEditor.apply();
    }

}

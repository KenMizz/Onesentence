package kenmizz.onesentence.adapter;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kenmizz.onesentence.MainActivity;
import kenmizz.onesentence.R;
import kenmizz.onesentence.SentenceAttributeDialog;
import kenmizz.onesentence.ui.main.SentenceFragment;
import kenmizz.onesentence.utils.Constants;
import kenmizz.onesentence.widget.SentenceWidgetConfiguration;

import static android.content.Context.MODE_PRIVATE;

/**
 * This adapter is for sentence
 */

public class SentenceAdapter extends RecyclerView.Adapter<SentenceAdapter.SentenceViewHolder> {
    private static final String TAG = "SentenceAdapter";

    private ArrayList<String> mSentenceList;
    private TextView mEmptyView;
    private SentenceFragment mSentenceFragment;

    private boolean mIsItemClickable = false;
    private int mWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Context mActivityContext;
    private SentenceWidgetConfiguration mActivity;


    static class SentenceViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        CardView mCardView;

        public SentenceViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.SentenceView);
            mCardView = itemView.findViewById(R.id.SentenceCardView);
        }
    }

    public SentenceAdapter(ArrayList<String> sentenceList, TextView emptyView, SentenceFragment sentenceFragment) {
        mSentenceList = sentenceList;
        mEmptyView = emptyView;
        mSentenceFragment = sentenceFragment;
    }

    public SentenceAdapter(ArrayList<String> sentenceList, int widgetId, boolean isItemClickable, Context activityContext, SentenceWidgetConfiguration activity) {
        mSentenceList = sentenceList;
        mWidgetId = widgetId;
        mIsItemClickable = isItemClickable;
        mActivityContext = activityContext;
        mActivity = activity;
    }

    @NonNull
    @Override
    public SentenceAdapter.SentenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_item, parent, false);
        return new SentenceViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceAdapter.SentenceViewHolder holder, int position) {
        final String currentSentence = mSentenceList.get(holder.getAdapterPosition());
        holder.mTextView.setText(currentSentence);
        if(mIsItemClickable) {
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpWidget(holder.getAdapterPosition());
                }
            });
        } else {
            holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mSentenceFragment.setNotificationDialog(currentSentence);
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

    public void removeSentence(int position) {
        notifyItemRemoved(position);
        mSentenceList.remove(position);
        if(mSentenceList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public String getSentence(int position) {
        return mSentenceList.get(position);
    }

    public void setUpWidget(int position) {
        Log.d(TAG, "Click " + mSentenceList.get(position));
        SharedPreferences sharedPreferences = mActivityContext.getSharedPreferences(SentenceWidgetConfiguration.WIDGET_PREFS, MODE_PRIVATE);
        SharedPreferences sentencesAttrPreferences = mActivityContext.getSharedPreferences(Constants.SENATTR_PREFS, MODE_PRIVATE);
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
        PendingIntent pendingIntent = PendingIntent.getActivity(mActivityContext, mWidgetId, attributeDialog, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.SentenceTextView, pendingIntent);
        appWidgetManager.updateAppWidget(mWidgetId, views);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
        mActivity.setResult(Activity.RESULT_OK, resultValue);
        mActivity.finish();
    }



}

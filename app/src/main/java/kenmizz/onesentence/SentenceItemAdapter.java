package kenmizz.onesentence;

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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SentenceItemAdapter extends RecyclerView.Adapter<SentenceItemAdapter.SentenceViewHolder> {
    private static final String TAG = "SentenceItemAdapter";

    private ArrayList<String> sentencesArrayList;
    private TextView emptyView;
    private boolean ItemClickable = false, inMainActivity = false;
    private Context activityContext;
    private Activity mActivity;
    private MainActivity mainActivity;

    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;


    static class SentenceViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        SentenceViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.SentenceView);
        }
    }

    SentenceItemAdapter(ArrayList<String> sentenceList, TextView emptyView, boolean inMainActivity, MainActivity mainActivity) {
        sentencesArrayList = sentenceList;
        this.emptyView = emptyView;
        this.inMainActivity = inMainActivity;
        this.mainActivity = mainActivity;
    }

    SentenceItemAdapter(ArrayList<String> sentenceList, boolean isItemClickable, Context activityContext, int widgetId, Activity mActivity) {
        sentencesArrayList = sentenceList;
        this.ItemClickable = isItemClickable;
        this.activityContext = activityContext;
        this.widgetId = widgetId;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public SentenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_item, parent, false);
        return new SentenceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceViewHolder holder, final int position) {
        String currentSentence = sentencesArrayList.get(position);
        holder.mTextView.setText(currentSentence);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(ItemClickable) {
                //Only runs when new appWidget on initialize
                Log.d(TAG, "Click " + sentencesArrayList.get(position).toString());
                SharedPreferences sharedPreferences = activityContext.getSharedPreferences(SentenceWidgetConfiguration.WIDGET_PREFS, Context.MODE_PRIVATE);
                SharedPreferences sentencesAttrPreferences = activityContext.getSharedPreferences(MainActivity.SENATTR_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                SharedPreferences.Editor sentencesAttributesEditor = sentencesAttrPreferences.edit(); //Sentences Attr for initialize
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activityContext);
                String sentence = sentencesArrayList.get(position).toString();
                RemoteViews views = new RemoteViews(activityContext.getPackageName(), R.layout.sentence_widget);
                views.setTextViewText(R.id.SentenceTextView, sentence);
                editor.putString(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT, sentence);
                sentencesAttributesEditor.putFloat(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textSize", 25);
                sentencesAttributesEditor.putInt(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textColor", activityContext.getColor(R.color.white));
                editor.apply();
                sentencesAttributesEditor.apply();
                Intent attributeDialog = new Intent(activityContext, SentenceAttributeDialog.class);
                attributeDialog.putExtra("widgetId", widgetId);
                PendingIntent pendingIntent = PendingIntent.getActivity(activityContext, widgetId, attributeDialog, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.SentenceTextView, pendingIntent);
                appWidgetManager.updateAppWidget(widgetId, views);
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                mActivity.setResult(Activity.RESULT_OK, resultValue);
                mActivity.finish();
            }
        }
    });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mainActivity.setNotificationDialog(sentencesArrayList.get(position).toString());
                return true;
            }
        });
}

    @Override
    public int getItemCount() {
        return sentencesArrayList.size();
    }

    public ArrayList<String> getSentencesArrayList() {
        return sentencesArrayList;
    }

    public void deleteSentence(int position) {
        sentencesArrayList.remove(position);
        notifyItemRemoved(position);
        if(getItemCount() == 0) {
            if(emptyView.getVisibility() == View.INVISIBLE) {
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void addSentence(String sentence) {
        sentencesArrayList.add(sentencesArrayList.size(), sentence);
        notifyItemInserted(sentencesArrayList.size() - 1);
    }
}

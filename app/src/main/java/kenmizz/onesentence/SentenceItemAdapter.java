package kenmizz.onesentence;

import android.app.Activity;
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
    private ArrayList<SentenceItem> sentenceItemArrayList;
    private boolean isItemClickable = false;
    private Context activityContext;
    private Activity mActivity;

    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;


    static class SentenceViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        SentenceViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.SentenceView);
        }
    }

    SentenceItemAdapter(ArrayList<SentenceItem> sentenceList) {
        sentenceItemArrayList = sentenceList;
    }

    SentenceItemAdapter(ArrayList<SentenceItem> sentenceList, boolean isItemClickable, Context activityContext, int widgetId, Activity mActivity) {
        sentenceItemArrayList = sentenceList;
        this.isItemClickable = isItemClickable;
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
        SentenceItem currentItem = sentenceItemArrayList.get(position);
        holder.mTextView.setText(currentItem.getSentence());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isItemClickable) {
                    Log.d("Adapter", "Click " + sentenceItemArrayList.get(position).getSentence());
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activityContext);
                    String sentence = sentenceItemArrayList.get(position).getSentence();
                    RemoteViews views = new RemoteViews(activityContext.getPackageName(), R.layout.sentence_widget);
                    views.setCharSequence(R.id.SentenceTextView, "setText", sentence);
                    appWidgetManager.updateAppWidget(widgetId, views);
                    SharedPreferences sharedPreferences = activityContext.getSharedPreferences(SentenceWidgetConfiguration.WIDGET_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT, sentence);
                    editor.apply();
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                    mActivity.setResult(Activity.RESULT_OK, resultValue);
                    mActivity.finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sentenceItemArrayList.size();
    }

    public ArrayList<SentenceItem> getSentenceItemArrayList() {
        return sentenceItemArrayList;
    }

    public void deleteSentence(int position) {
        sentenceItemArrayList.remove(position);
        notifyItemRemoved(position);
    }
}

package kenmizz.onesentence.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import kenmizz.onesentence.MainActivity;
import kenmizz.onesentence.R;
import kenmizz.onesentence.SentenceAttributeDialog;
import kenmizz.onesentence.utils.Constants;
import kenmizz.onesentence.widget.SentenceWidgetConfiguration;

public class SentenceAppWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "SentenceWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for(int widgetId : appWidgetIds) {
            removeWidget(context, widgetId);
            Log.d(TAG, "Deleted widgetId: " + widgetId + " attributes");
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SentenceWidgetConfiguration.WIDGET_PREFS, Context.MODE_PRIVATE);
        SharedPreferences sentencesAttrPreferences = context.getSharedPreferences(Constants.SENATTR_PREFS, Context.MODE_PRIVATE);
        String sentence = sharedPreferences.getString(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT, SentenceWidgetConfiguration.SENTENCE_TEXT);
        float sentenceTextSize = sentencesAttrPreferences.getFloat(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textSize", 25);
        int sentenceTextColor = sentencesAttrPreferences.getInt(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textColor", context.getColor(R.color.white));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sentence_widget);
        Intent attributeDialog = new Intent(context, SentenceAttributeDialog.class);
        attributeDialog.putExtra("widgetId", widgetId);
        attributeDialog.putExtra("sentence", sentence);
        PendingIntent attributeDialogPendingIntent = PendingIntent.getActivity(context, widgetId, attributeDialog, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.SentenceTextView, attributeDialogPendingIntent);
        views.setTextViewText(R.id.SentenceTextView, sentence);
        views.setTextViewTextSize(R.id.SentenceTextView, TypedValue.COMPLEX_UNIT_SP, sentenceTextSize);
        views.setTextColor(R.id.SentenceTextView, sentenceTextColor);
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private void removeWidget(Context context, int widgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SentenceWidgetConfiguration.WIDGET_PREFS, Context.MODE_PRIVATE);
        SharedPreferences sentencesAttrPreferences = context.getSharedPreferences(Constants.SENATTR_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences.Editor sentenceAttrEditor = sentencesAttrPreferences.edit();
        editor.remove(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT);
        sentenceAttrEditor.remove(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textSize");
        sentenceAttrEditor.remove(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT + "textColor");
        editor.apply();
        sentenceAttrEditor.apply();
    }
}
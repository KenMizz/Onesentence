package kenmizz.onesentence;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class SentenceAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SentenceWidgetConfiguration.WIDGET_PREFS, Context.MODE_PRIVATE);
            String sentence = sharedPreferences.getString(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT, "等待初始化。。。");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sentence_widget);
            views.setCharSequence(R.id.SentenceTextView, "setText", sentence);
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for(int widgetId : appWidgetIds) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SentenceWidgetConfiguration.WIDGET_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(widgetId + SentenceWidgetConfiguration.SENTENCE_TEXT);
            editor.apply();
        }
    }
}

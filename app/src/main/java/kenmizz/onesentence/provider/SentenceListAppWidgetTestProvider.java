package kenmizz.onesentence.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import kenmizz.onesentence.R;

public class SentenceListAppWidgetTestProvider extends AppWidgetProvider {

    private static final String TAG = "SentenceListAppWidgetTestProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int widgetId: appWidgetIds) {
            RemoteViews sentenceRemoteViews = new RemoteViews(context.getPackageName(), R.layout.sentence_widget);
            sentenceRemoteViews.setTextViewText(R.id.SentenceTextView, "Test");
            sentenceRemoteViews.setTextViewTextSize(R.id.SentenceTextView, TypedValue.COMPLEX_UNIT_SP, 25);
            appWidgetManager.updateAppWidget(widgetId, sentenceRemoteViews);
            Log.i(TAG, "update widget: " + widgetId); //this doesn't get call again
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for(int widgetId: appWidgetIds) {
            Log.i(TAG, "remove widget: " + widgetId);
        }
    }
}

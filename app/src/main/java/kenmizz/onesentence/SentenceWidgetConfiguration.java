package kenmizz.onesentence;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class SentenceWidgetConfiguration extends AppCompatActivity {

    private final ArrayList<String> sentencesList = new ArrayList<>();

    public static final String SHARED_PREFS = "sentencesPref";
    public static final String WIDGET_PREFS = "widgetsPref";
    public static final String SENTENCE_TEXT = "句";

    private int themeOptions = MainActivity.NIGHTMODE.DEFAULT.ordinal();
    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences config = getSharedPreferences(MainActivity.CONFIG_PREFS, MODE_PRIVATE);
        if(!config.contains("themeOptions")) {
            SharedPreferences.Editor configedit = config.edit();
            configedit.putInt("themeOptions", themeOptions);
            configedit.apply();
        }
        themeOptions = config.getInt("themeOptions", MainActivity.NIGHTMODE.DEFAULT.ordinal());
        switch(themeOptions) {
            case 0:
                switch(getUiMode()) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        setTheme(R.style.AppConfigurationTheme_Grey);
                        break;

                    case Configuration.UI_MODE_NIGHT_NO:
                        setTheme(R.style.AppConfigurationTheme);
                }
                break;
            case 1:
                setTheme(R.style.AppConfigurationTheme);
                break;

            case 2:
                setTheme(R.style.AppConfigurationTheme_Grey);
                break;

            case 3:
                setTheme(R.style.AppConfigurationTheme_Dark);
        }
        setContentView(R.layout.activity_sentence_widget_configuration);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_CANCELED, resultValue);

        if(widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        loadSentencesList();
        setUpSentencesView();
    }

    public int getUiMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public void loadSentencesList() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Map<String, ?> Sentences = sharedPreferences.getAll();
        if(!Sentences.isEmpty()) {
            for (Map.Entry<String, ?> Sentence : Sentences.entrySet()) {
                sentencesList.add(Sentence.getValue().toString());
            }
            TextView emptyView = findViewById(R.id.configure_emptyView);
            if(emptyView.getVisibility() == View.VISIBLE) {
                emptyView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setUpSentencesView() {
        RecyclerView mRecylerView = findViewById(R.id.RecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        SentenceItemAdapter sentenceItemAdapter = new SentenceItemAdapter(sentencesList, widgetId, true, getApplicationContext(), this);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mLayoutManager);
        mRecylerView.setAdapter(sentenceItemAdapter);
    }
}
package kenmizz.onesentence.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import kenmizz.onesentence.MainActivity;
import kenmizz.onesentence.R;
import kenmizz.onesentence.adapter.SentenceListAdapter;

public class SentenceListWidgetConfiguration extends AppCompatActivity {

    private HashMap<String, ArrayList<String>> sentenceCollection = new HashMap<>();

    private int themeOptions = MainActivity.ThemeMode.DEFAULT.ordinal();
    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences config = getSharedPreferences(MainActivity.CONFIG_PREFS, MODE_PRIVATE);
        if(!config.contains("themeOptions")) {
            SharedPreferences.Editor configEdit = config.edit();
            configEdit.putInt("themeOptions", themeOptions);
            configEdit.apply();
        }
        themeOptions = config.getInt("themeOptions", MainActivity.ThemeMode.DEFAULT.ordinal());
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
        setContentView(R.layout.activity_sentence_list_widget_configuration);
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
        setUpRecyclerView();
    }

    public int getUiMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public void loadSentenceCollection() {
        //TODO: finish this
    }

    public void setUpRecyclerView() {
        RecyclerView mRecylerView = findViewById(R.id.sentenceWidgetConfigurationRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        SentenceListAdapter mAdapter = new SentenceListAdapter(sentenceCollection);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mLayoutManager);
        mRecylerView.setAdapter(mAdapter);
    }
}
package kenmizz.onesentence.widget;

import static kenmizz.onesentence.MainActivity.SENTENCE_LIST_FILE;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kenmizz.onesentence.MainActivity;
import kenmizz.onesentence.R;
import kenmizz.onesentence.adapter.SentenceListAdapter;

public class SentenceListWidgetConfiguration extends AppCompatActivity {

    private HashMap<String, ArrayList<String>> sentenceCollection = new HashMap<>();

    private int themeOptions = MainActivity.ThemeMode.DEFAULT.ordinal();
    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public static String SENLIST_PREFS = "sentenceCollectionPrefs";

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
                        setTheme(R.style.AppConfigurationThemeGrey);
                        break;

                    case Configuration.UI_MODE_NIGHT_NO:
                        setTheme(R.style.AppConfigurationTheme);
                }
                break;
            case 1:
                setTheme(R.style.AppConfigurationTheme);
                break;

            case 2:
                setTheme(R.style.AppConfigurationThemeGrey);
                break;

            case 3:
                setTheme(R.style.AppConfigurationThemeDark);
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
        loadSentenceCollection();
        setUpRecyclerView();
    }

    public int getUiMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public void loadSentenceCollection() {
        File SentenceCollectionJsonFile = new File(getFilesDir().getAbsolutePath() + File.separator + SENTENCE_LIST_FILE);
        try {
            FileInputStream fileInputStream = new FileInputStream(SentenceCollectionJsonFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            fileInputStream.close();
            reader.close();
            JSONObject SentenceCollectionJsonObject = new JSONObject(contentBuilder.toString());
            Iterator<String> stringIterator = SentenceCollectionJsonObject.keys();
            while(stringIterator.hasNext()) {
                String key = stringIterator.next();
                JSONArray valueArray = SentenceCollectionJsonObject.getJSONArray(key);
                ArrayList<String> value = new ArrayList<>();
                for(int i = 0; i < valueArray.length(); i++) {
                    value.add(valueArray.getString(i));
                }
                sentenceCollection.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!sentenceCollection.isEmpty()) {
            TextView emptyView = findViewById(R.id.ListConfigureEmptyView);
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    public void setUpRecyclerView() {
        RecyclerView mRecylerView = findViewById(R.id.sentenceListWidgetConfigurationRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        SentenceListAdapter mAdapter = new SentenceListAdapter(sentenceCollection, true, this);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mLayoutManager);
        mRecylerView.setAdapter(mAdapter);
    }
}
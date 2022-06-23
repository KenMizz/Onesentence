package kenmizz.onesentence.widget;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.DynamicColors;

import java.util.ArrayList;
import java.util.Map;

import kenmizz.onesentence.MainActivity;
import kenmizz.onesentence.R;
import kenmizz.onesentence.adapter.SentenceAdapter;
import kenmizz.onesentence.utils.Constants;

public class SentenceWidgetConfiguration extends AppCompatActivity {

    private final ArrayList<String> sentencesList = new ArrayList<>();

    public static final String SHARED_PREFS = "sentencesPref";
    public static final String WIDGET_PREFS = "widgetsPref";
    public static final String SENTENCE_TEXT = "句";

    private int themeOptions = MainActivity.ThemeMode.DEFAULT.ordinal();
    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences config = getSharedPreferences(Constants.CONFIG_PREFS, MODE_PRIVATE);
        if(!config.contains("themeOptions")) {
            SharedPreferences.Editor configEdit = config.edit();
            configEdit.putInt("themeOptions", themeOptions);
            configEdit.apply();
        }
        themeOptions = config.getInt("themeOptions", MainActivity.ThemeMode.DEFAULT.ordinal());
        configureTheme(themeOptions);
        DynamicColors.applyToActivityIfAvailable(this);
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

    /**
     * Configure AppTheme
     * Android S+ Device uses Material3 Theme as default
     * @param themeOptions themeOptions
     */
    public void configureTheme(int themeOptions) {
        boolean MaterialYou = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
        switch(themeOptions) {
            case 0: //Default Day/Night Mode
                switch(getUiMode()) {

                    case Configuration.UI_MODE_NIGHT_YES:
                        if(MaterialYou) {
                            setTheme(R.style.AppConfigurationTheme_md3_Grey);
                            return;
                        }
                        setTheme(R.style.AppConfigurationTheme_md2_Grey);
                        break;

                    case Configuration.UI_MODE_NIGHT_NO:
                        if(MaterialYou) {
                            setTheme(R.style.AppConfigurationTheme_md3_Light);
                            return;
                        }
                        setTheme(R.style.AppConfigurationTheme_md2_Light);
                }
                break;

            case 1: //Light Mode
                if(MaterialYou) {
                    setTheme(R.style.AppConfigurationTheme_md3_Light);
                    return;
                }
                setTheme(R.style.AppConfigurationTheme_md2_Light);
                break;

            case 2: //Grey Mode
                if(MaterialYou) {
                    setTheme(R.style.AppConfigurationTheme_md3_Grey);
                    return;
                }
                setTheme(R.style.AppConfigurationTheme_md2_Grey);
                break;

            case 3: //AMOLED Dark Mode
                if(MaterialYou) {
                    setTheme(R.style.AppConfigurationTheme_md3_Dark);
                    return;
                }
                setTheme(R.style.AppConfigurationTheme_md2_Dark);
        }
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
        RecyclerView mRecyclerView = findViewById(R.id.RecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        SentenceAdapter sentenceAdapter = new SentenceAdapter(sentencesList, widgetId, true, getApplicationContext(), this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(sentenceAdapter);
    }
}
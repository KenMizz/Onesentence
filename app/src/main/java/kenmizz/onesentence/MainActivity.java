package kenmizz.onesentence;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private SentenceItemAdapter mAdapter;

    ArrayList<SentenceItem> sentencesList = new ArrayList<>();
    public static final String SENTENCES_PREFS = "sentencesPref";
    public static final String CONFIG_PREFS = "configsPref";
    public static final String SENATTR_PREFS = "SentencesAttributePref";
    private static final String TAG = "MainActivity";
    private int themeOptions = NIGHTMODE.DEFAULT.ordinal();
    private int newThemeOptions = 0;

    public enum NIGHTMODE {
        DEFAULT, LIGHT, GREY, DARK
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpConfigurations(CONFIG_PREFS);
        switch(themeOptions) {
            case 0:
                switch(getUiMode()) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        setTheme(R.style.AppThemeGrey);
                        break;

                    case Configuration.UI_MODE_NIGHT_NO:
                        setTheme(R.style.AppTheme);
                }
                break;
            case 1:
                setTheme(R.style.AppTheme);
                break;

            case 2:
                setTheme(R.style.AppThemeGrey);
                break;

            case 3:
                setTheme(R.style.AppThemeDark);
        }
        setContentView(R.layout.activity_main);
        setUpConfigurations(SENTENCES_PREFS);
        setUpSentencesView();
        FloatingActionButton addButton = findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSentenceDialog();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        syncAllSharedPrefs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        syncAllSharedPrefs();
    }

    public int getUiMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public void syncAllSharedPrefs() {
        Log.d(TAG, "syncing with all SharedPrefs");
        sentencesList = mAdapter.getSentenceItemArrayList();
        SharedPreferences configsPrefs = getSharedPreferences(CONFIG_PREFS, MODE_PRIVATE);
        SharedPreferences sentencesPrefs = getSharedPreferences(SENTENCES_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor configEditor = configsPrefs.edit();
        SharedPreferences.Editor sentencesEditor = sentencesPrefs.edit();
        configEditor.putInt("themeOptions", themeOptions);
        configEditor.apply();
        sentencesEditor.clear();
        for(SentenceItem sentenceItem: sentencesList) {
            sentencesEditor.putString(sentenceItem.getSentence(), sentenceItem.getSentence());
        }
        sentencesEditor.apply();
    }

    public void addSentenceDialog() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.sentence_edittext, null);
        final TextInputEditText editText = view.findViewById(R.id.sentenceAddEditText);
        dialog.setView(view);
        dialog.setTitle(R.string.newsentence)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = Objects.requireNonNull(editText.getText()).toString();
                        if(!text.isEmpty()) {
                            for(SentenceItem item : sentencesList) {
                                if(item.getSentence().equals(text)) {
                                    Snackbar.make(getWindow().getDecorView().getRootView(), text + getResources().getString(R.string.KeyExists), Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            addSentence(editText.getText().toString());
                            Snackbar.make(getWindow().getDecorView().getRootView(), getResources().getString(R.string.AlreadyAdd) + text, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    @SuppressLint({"InflateParams", "NonConstantResourceId", "SetTextI18n"})
    public void showAppDialog(int layoutId) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        final View view = getLayoutInflater().inflate(layoutId, null);
        switch(layoutId) {

            case R.layout.about:
                dialog.setTitle(R.string.about)
                        .setView(view)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                TextView textView = view.findViewById(R.id.versionView);
                textView.setText("v" + BuildConfig.VERSION_NAME);
                break;


                case R.layout.themes:
                    dialog.setTitle(R.string.theme)
                            .setView(view)
                            .setPositiveButton(R.string.confirmButton, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(newThemeOptions != themeOptions) {
                                        themeOptions = newThemeOptions;
                                        syncAllSharedPrefs();
                                        recreate();
                                    }
                                }
                            })
                            .show();
                    final RadioGroup group = view.findViewById(R.id.ThemeRadioGroup);
                    group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            switch (radioGroup.getCheckedRadioButtonId()) {
                                case R.id.follow_system:
                                    newThemeOptions = NIGHTMODE.DEFAULT.ordinal();
                                    break;

                                case R.id.light_mode:
                                    newThemeOptions = NIGHTMODE.LIGHT.ordinal();
                                    break;

                                case R.id.grey_mode:
                                    newThemeOptions = NIGHTMODE.GREY.ordinal();
                                    break;

                                case R.id.dark_mode:
                                    newThemeOptions = NIGHTMODE.DARK.ordinal();
                            }
                        }
                    });
                    switch(themeOptions) {
                        case 0:
                            group.check(R.id.follow_system);
                            break;

                        case 1:
                            group.check(R.id.light_mode);
                            break;

                        case 2:
                            group.check(R.id.grey_mode);
                            break;

                        case 3:
                            group.check(R.id.dark_mode);
                    }
            }
        }


        public void setUpConfigurations(String PreferencesName) {
            switch (PreferencesName) {
                case CONFIG_PREFS:
                    SharedPreferences config = getSharedPreferences(PreferencesName, MODE_PRIVATE);
                    if(!config.contains("themeOptions")) {
                        SharedPreferences.Editor configedit = config.edit();
                        configedit.putInt("themeOptions", themeOptions);
                        configedit.apply();
                    }
                    themeOptions = config.getInt("themeOptions", NIGHTMODE.DEFAULT.ordinal());
                    break;

                case SENTENCES_PREFS:
                    SharedPreferences sentences = getSharedPreferences(PreferencesName, MODE_PRIVATE);
                    Map<String, ?>Sentences = sentences.getAll();
                    if(!Sentences.isEmpty()) {
                        for (Map.Entry<String, ?> Sentence : Sentences.entrySet()) {
                            sentencesList.add(new SentenceItem(Sentence.getValue().toString()));
                        }
                        TextView emptyView = findViewById(R.id.emptyView);
                        if(emptyView.getVisibility() == View.VISIBLE) {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                    }
            }
    }

    public void setUpSentencesView() {
        RecyclerView mRecylerView = findViewById(R.id.RecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new SentenceItemAdapter(sentencesList, (TextView) findViewById(R.id.emptyView));
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mLayoutManager);
        mRecylerView.setAdapter(mAdapter);
        SwipeController swipeController = new SwipeController(mAdapter, getWindow().getDecorView().getRootView());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(mRecylerView);
    }

    public void addSentence(String sentence) {
        mAdapter.addSentence(sentence);
        TextView emptyView = findViewById(R.id.emptyView);
        if(emptyView.getVisibility() == View.VISIBLE) {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            default:
                return true;

            case R.id.about:
                showAppDialog(R.layout.about);
                break;

            case R.id.themes:
                showAppDialog(R.layout.themes);
        }
        return super.onOptionsItemSelected(item);
    }
}

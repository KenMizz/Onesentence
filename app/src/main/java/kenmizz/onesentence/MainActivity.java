package kenmizz.onesentence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private SentenceItemAdapter mAdapter;

    ArrayList<SentenceItem> sentencesList = new ArrayList<SentenceItem>();
    public static final String SENTENCES_PREFS = "sentencesPref";
    public static final String CONFIG_PREFS = "configsPref";
    private static final String TAG = "MainActivity";
    private int themeOptions = NIGHTMODE.DEFAULT.ordinal();

    public enum NIGHTMODE {
        DEFAULT, LIGHT, DARK, BLACK
    }

    //private final String COOLAPK_URL = "http://www.coolapk.com/u/618459";
    //private final String GITHUB_URL = "https://github.com/KenMizz/Onesentence";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpConfigurations(CONFIG_PREFS);
        switch(themeOptions) {
            case 0:
                switch(getUiMode()) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        setTheme(R.style.AppThemeDark);
                        break;

                    case Configuration.UI_MODE_NIGHT_NO:
                        setTheme(R.style.AppTheme);
                }
                break;
            case 1:
                //TODO: Finish this

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
        syncWithSharedPrefs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        syncWithSharedPrefs();
    }

    public int getUiMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public void syncWithSharedPrefs() {
        Log.d(TAG, "sync with SharedPrefs..");
        SharedPreferences sharedPreferences = getSharedPreferences(SENTENCES_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); //in order to fully sync with local ArrayList
        for(SentenceItem item : mAdapter.getSentenceItemArrayList()) {
            editor.putString(item.getSentence(), item.getSentence());
        }
        editor.apply();
    }

    public void addSentenceDialog() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        /*if(getDarkMode() == Configuration.UI_MODE_NIGHT_YES) {
            dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogDark);
        } else {
            dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogLight);
        }*/
        final TextInputEditText editText = new TextInputEditText(this);
        editText.setHint(R.string.sentence);
        dialog.setView(editText);
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

    @SuppressLint({"InflateParams", "NonConstantResourceId"})
    public void showAppDialog(int layoutId) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        View view = getLayoutInflater().inflate(layoutId, null);
        switch(layoutId) {
            case R.layout.about:
                dialog.setTitle(R.string.about)
                        .setView(view)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                TextView textView = view.findViewById(R.id.versionName);
                textView.setText(BuildConfig.VERSION_NAME);
                break;


                case R.layout.themes:
                    dialog.setTitle(R.string.theme)
                            .setView(view)
                            .show();
                    RadioGroup group = view.findViewById(R.id.ThemeRadioGroup);
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
        mAdapter = new SentenceItemAdapter(sentencesList);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mLayoutManager);
        mRecylerView.setAdapter(mAdapter);
        SwipeController swipeController = new SwipeController(mAdapter, getWindow().getDecorView().getRootView());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(mRecylerView);
    }

    public void addSentence(String sentence) {
        sentencesList.add(new SentenceItem(sentence));
        mAdapter.notifyItemInserted(sentencesList.size() - 1);
        TextView emptyView = findViewById(R.id.emptyView);
        if(emptyView.getVisibility() == View.VISIBLE) {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
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

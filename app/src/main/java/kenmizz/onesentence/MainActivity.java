package kenmizz.onesentence;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import kenmizz.onesentence.ui.main.SectionsPagerAdapter;
import kenmizz.onesentence.utils.Constants;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> sentencesList = new ArrayList<>();
    HashMap<String, ArrayList<String>> sentenceCollection = new HashMap<>();

    private static final String TAG = "MainActivity";

    private int themeOptions = ThemeMode.DEFAULT.ordinal();
    private int newThemeOptions = 0;

    public enum ThemeMode {
        DEFAULT, LIGHT, GREY, DARK
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setUpConfigurations(Constants.CONFIG_PREFS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationManager.getNotificationChannel(Constants.CHANNEL_ID) == null) {
                //create new channel if channel not exists
                NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(getString(R.string.channel_description));
                notificationManager.createNotificationChannel(channel);
            }
        }
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
        try {
            setUpConfigurations(Constants.SENTENCES_PREFS);
            setUpConfigurations(Constants.SENTENCE_LIST_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.mainTabs);
        tabs.setupWithViewPager(viewPager);
        MaterialToolbar mainToolbar = findViewById(R.id.mainToolbar);
        final FloatingActionButton addButton = findViewById(R.id.addFloatingButton);
        mainToolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            switch (itemId) {

                default:
                    return true;

                case R.id.about:
                    showAppDialog(R.layout.about);
                    break;

                case R.id.themes:
                    showAppDialog(R.layout.themes);
            }
            return true;
        });
        addButton.setOnClickListener(view -> {
            if(viewPager.getCurrentItem() == 1) {
                addSentenceListDialog();
            } else {
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

    public void syncAllSharedPrefs() {
        Log.d(TAG, "syncing with all SharedPrefs");
        SharedPreferences configsPrefs = getSharedPreferences(Constants.CONFIG_PREFS, MODE_PRIVATE);
        SharedPreferences sentencesPrefs = getSharedPreferences(Constants.SENTENCES_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor configEditor = configsPrefs.edit();
        SharedPreferences.Editor sentencesEditor = sentencesPrefs.edit();
        configEditor.putInt("themeOptions", themeOptions);
        configEditor.apply();
        sentencesEditor.clear();
        for( String sentence: sentencesList) {
            sentencesEditor.putString(sentence, sentence);
        }
        sentencesEditor.apply();
        JSONObject SentenceListToJson = new JSONObject(sentenceCollection);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(Constants.SENTENCE_LIST_FILE, MODE_PRIVATE));
            outputStreamWriter.write(SentenceListToJson.toString());
            outputStreamWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File jsonFile = new File(getFilesDir().getAbsolutePath() + File.separator + Constants.SENTENCE_LIST_FILE);
        if(jsonFile.exists()) {
            Log.d(TAG, Constants.SENTENCE_LIST_FILE + " created");
        }
        Log.d(TAG, "jsonFile path:" + jsonFile.getAbsolutePath());
    }

    public void setUpConfigurations(String PreferencesName) throws IOException {
        switch (PreferencesName) {
            case Constants.CONFIG_PREFS:
                SharedPreferences config = getSharedPreferences(PreferencesName, MODE_PRIVATE);
                if(!config.contains("themeOptions")) {
                    SharedPreferences.Editor configEdit = config.edit();
                    configEdit.putInt("themeOptions", themeOptions);
                    configEdit.apply();
                }
                themeOptions = config.getInt("themeOptions", ThemeMode.DEFAULT.ordinal());
                break;

            case Constants.SENTENCES_PREFS:
                SharedPreferences sentences = getSharedPreferences(PreferencesName, MODE_PRIVATE);
                Map<String, ?> Sentences = sentences.getAll();
                if(!Sentences.isEmpty()) {
                    for (Map.Entry<String, ?> Sentence : Sentences.entrySet()) {
                        sentencesList.add(Sentence.getValue().toString());
                    }
                }
            break;

            case Constants.SENTENCE_LIST_FILE:
                File SentenceCollectionJsonFile = new File(getFilesDir().getAbsolutePath() + File.separator + Constants.SENTENCE_LIST_FILE);
                if(!SentenceCollectionJsonFile.exists()) {
                    boolean createStatus = SentenceCollectionJsonFile.createNewFile();
                    if(!createStatus) {
                        Log.e(TAG, "sentenceList.json create failed");
                    }
                }
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
            break;
        }
    }

    public void addSentenceDialog() {
        final MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.sentence_edittext, null);
        final TextInputEditText editText = view.findViewById(R.id.sentenceAddEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(sentencesList.contains(charSequence.toString())) {
                    editText.setError(charSequence.toString() + getString(R.string.sentenceExists));
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.setView(view);
        dialog.setTitle(R.string.newsentence)
                .setPositiveButton(R.string.add, (dialog1, which) -> {
                    String text = Objects.requireNonNull(editText.getText()).toString();
                    if(!text.isEmpty()) {
                        if(!sentencesList.contains(text)) {
                            sentencesList.add(sentencesList.size(), text);
                            Snackbar.make(findViewById(R.id.addFloatingButton), getString(R.string.AlreadyAdd) + text, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(R.id.addFloatingButton), text + getString(R.string.KeyExists), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    public void addSentenceListDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.sentence_list_edittext, null);
        final TextInputEditText editText = view.findViewById(R.id.sentenceListAddEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if(sentenceCollection.containsKey(text)) {
                    editText.setError(text + getString(R.string.sentenceExists));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.new_sentence_list)
                .setView(view)
                .setPositiveButton(R.string.add, (dialogInterface, i) -> {
                    if(!Objects.requireNonNull(editText.getText()).toString().isEmpty()) {
                        if(!sentenceCollection.containsKey(Objects.requireNonNull(editText.getText()).toString())) {
                            sentenceCollection.put(editText.getText().toString(), new ArrayList<>());
                            Snackbar.make(findViewById(R.id.addFloatingButton), getString(R.string.successfully_add_sentence_list).replace("_key_", editText.getText().toString()), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(R.id.addFloatingButton), getString(R.string.fail_add_sentence_list).replace("_key_", editText.getText().toString()), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    if(!sentenceCollection.isEmpty()) {
                        findViewById(R.id.emptyListView).setVisibility(View.INVISIBLE);
                    }
                });
        dialogBuilder.show();
    }

    @SuppressLint({"InflateParams", "NonConstantResourceId", "SetTextI18n"})
    public void showAppDialog(int layoutId) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        final View view = getLayoutInflater().inflate(layoutId, null);
        switch(layoutId) {

            case R.layout.about:
                TextView coolApkLinkView = view.findViewById(R.id.coolApkLinkView);
                TextView githubLinkView = view.findViewById(R.id.githubLinkView);
                coolApkLinkView.setMovementMethod(LinkMovementMethod.getInstance());
                githubLinkView.setMovementMethod(LinkMovementMethod.getInstance());
                coolApkLinkView.setLinkTextColor(Color.BLUE);
                githubLinkView.setLinkTextColor(Color.BLUE);
                dialog.setTitle(R.string.about)
                        .setView(view)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                TextView textView = view.findViewById(R.id.versionView);
                textView.setText("v" + BuildConfig.VERSION_NAME); //comment this when first compile
                break;


            case R.layout.themes:
                dialog.setTitle(R.string.theme)
                        .setView(view)
                        .setPositiveButton(R.string.confirmButton, (dialogInterface, i) -> {
                            if(newThemeOptions != themeOptions) {
                                themeOptions = newThemeOptions;
                                syncAllSharedPrefs();
                                recreate();
                            }
                        })
                        .show();
                final RadioGroup group = view.findViewById(R.id.ThemeRadioGroup);
                group.setOnCheckedChangeListener((radioGroup, i) -> {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.follow_system:
                            newThemeOptions = ThemeMode.DEFAULT.ordinal();
                            break;

                        case R.id.light_mode:
                            newThemeOptions = ThemeMode.LIGHT.ordinal();
                            break;

                        case R.id.grey_mode:
                            newThemeOptions = ThemeMode.GREY.ordinal();
                            break;

                        case R.id.dark_mode:
                            newThemeOptions = ThemeMode.DARK.ordinal();
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

    public int getUiMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public ArrayList<String> getSentencesList() {
        return sentencesList;
    }

    public HashMap<String, ArrayList<String>> getSentenceCollection() { return sentenceCollection; }

}
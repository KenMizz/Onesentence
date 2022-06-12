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
import com.google.android.material.color.DynamicColors;
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

public class MainActivity extends AppCompatActivity {

    ArrayList<String> sentencesList = new ArrayList<>();
    HashMap<String, ArrayList<String>> sentenceCollection = new HashMap<>();

    public static final String SENTENCES_PREFS = "sentencesPref";
    public static final String CONFIG_PREFS = "configsPref";
    public static final String SENATTR_PREFS = "SentencesAttributePref";
    public static final String NOTIFICATION_PREFS = "NotificationsPref";
    public static final String SENTENCE_LIST_FILE = "sentenceList.json";
    private static final String TAG = "MainActivity";
    public static final String CHANNEL_ID = "OneSentence_NotificationChannel";
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
            setUpConfigurations(CONFIG_PREFS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                //create new channel if channel not exists
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(getString(R.string.channel_description));
                notificationManager.createNotificationChannel(channel);
            }
        }
        configureTheme(themeOptions);
        DynamicColors.applyToActivityIfAvailable(this);
        setContentView(R.layout.activity_main);
        try {
            setUpConfigurations(SENTENCES_PREFS);
            setUpConfigurations(SENTENCE_LIST_FILE);
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
                    showAppDialog(R.layout.theme_options);
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

    /**
     * sync up all configs when app is on the background or close
     */
    public void syncAllSharedPrefs() {
        Log.d(TAG, "syncing with all SharedPrefs");
        SharedPreferences configsPrefs = getSharedPreferences(CONFIG_PREFS, MODE_PRIVATE);
        SharedPreferences sentencesPrefs = getSharedPreferences(SENTENCES_PREFS, MODE_PRIVATE);
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
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(SENTENCE_LIST_FILE, MODE_PRIVATE));
            outputStreamWriter.write(SentenceListToJson.toString());
            outputStreamWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File jsonFile = new File(getFilesDir().getAbsolutePath() + File.separator + SENTENCE_LIST_FILE);
        if(jsonFile.exists()) {
            Log.d(TAG, SENTENCE_LIST_FILE + " created");
        }
        Log.d(TAG, "jsonFile path:" + jsonFile.getAbsolutePath());
    }

    /**
     * set up configurations
     * @param PreferencesName fileName
     * @throws IOException fileNotFound
     */
    public void setUpConfigurations(String PreferencesName) throws IOException {
        switch (PreferencesName) {
            case CONFIG_PREFS:
                SharedPreferences config = getSharedPreferences(PreferencesName, MODE_PRIVATE);
                if(!config.contains("themeOptions")) {
                    SharedPreferences.Editor configEdit = config.edit();
                    configEdit.putInt("themeOptions", themeOptions);
                    configEdit.apply();
                }
                themeOptions = config.getInt("themeOptions", ThemeMode.DEFAULT.ordinal());
                break;

            case SENTENCES_PREFS:
                SharedPreferences sentences = getSharedPreferences(PreferencesName, MODE_PRIVATE);
                Map<String, ?> Sentences = sentences.getAll();
                if(!Sentences.isEmpty()) {
                    for (Map.Entry<String, ?> Sentence : Sentences.entrySet()) {
                        sentencesList.add(Sentence.getValue().toString());
                    }
                }
            break;

            case SENTENCE_LIST_FILE:
                File SentenceCollectionJsonFile = new File(getFilesDir().getAbsolutePath() + File.separator + SENTENCE_LIST_FILE);
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
                            setTheme(R.style.AppTheme_md3_Grey);
                            return;
                        }
                        setTheme(R.style.AppTheme_md2_Grey);
                    break;

                    case Configuration.UI_MODE_NIGHT_NO:
                        if(MaterialYou) {
                            setTheme(R.style.AppTheme_md3_Light);
                            return;
                        }
                        setTheme(R.style.AppTheme_md2_Light);
                }
                break;

            case 1: //Light Mode
                if(MaterialYou) {
                    setTheme(R.style.AppTheme_md3_Light);
                    return;
                }
                setTheme(R.style.AppTheme_md2_Light);
            break;

            case 2: //Grey Mode
                if(MaterialYou) {
                    setTheme(R.style.AppTheme_md3_Grey);
                    return;
                }
                setTheme(R.style.AppTheme_md2_Grey);
            break;

            case 3: //AMOLED Dark Mode
                if(MaterialYou) {
                    setTheme(R.style.AppTheme_md3_Dark);
                    return;
                }
                setTheme(R.style.AppTheme_md2_Dark);
        }
    }

    /**
     * shows addSentenceDialog
     */
    @SuppressLint("InflateParams")
    public void addSentenceDialog() {
        final MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        View editTextView;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            editTextView = LayoutInflater.from(this).inflate(R.layout.sentence_edittext_md3, null);
        } else {
            editTextView = LayoutInflater.from(this).inflate(R.layout.sentence_edittext_md2, null);
        }
        final TextInputEditText editText = editTextView.findViewById(R.id.sentenceAddEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(sentencesList.contains(charSequence.toString())) {
                    editText.setError(charSequence + getString(R.string.sentenceExists));
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.setView(editTextView);
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

    /**
     * shows addSentenceListDialog
     */
    @SuppressLint("InflateParams")
    public void addSentenceListDialog() {
        View editTextView;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            editTextView = LayoutInflater.from(this).inflate(R.layout.sentence_list_edittext_md3, null);
        } else {
            editTextView = LayoutInflater.from(this).inflate(R.layout.sentence_list_edittext_md2, null);
        }
        final TextInputEditText editText = editTextView.findViewById(R.id.sentenceListAddEditText);
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
                .setView(editTextView)
                .setPositiveButton(R.string.add, (dialogInterface, i) -> {
                    if(!sentenceCollection.containsKey(Objects.requireNonNull(editText.getText()).toString())) {
                        sentenceCollection.put(editText.getText().toString(), new ArrayList<>());
                        Snackbar.make(findViewById(R.id.addFloatingButton), getString(R.string.successfully_add_sentence_list).replace("_key_", editText.getText().toString()), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(findViewById(R.id.addFloatingButton), getString(R.string.fail_add_sentence_list).replace("_key_", editText.getText().toString()), Snackbar.LENGTH_SHORT).show();
                    }
                    if(!sentenceCollection.isEmpty()) {
                        findViewById(R.id.emptyListView).setVisibility(View.INVISIBLE);
                    }
                });
        dialogBuilder.show();
    }

    /**
     * show app dialogs
     * @param layoutId dialogLayout
     */
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
                coolApkLinkView.setLinkTextColor(getColor(R.color.dark_blue));
                githubLinkView.setLinkTextColor(getColor(R.color.dark_blue));
                dialog.setTitle(R.string.about)
                        .setView(view)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                TextView textView = view.findViewById(R.id.versionView);
                textView.setText("v" + BuildConfig.VERSION_NAME); //comment this when first compile
                break;


            case R.layout.theme_options:
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

    /**
     * this indicates that night mode is on or off
     * @return int
     */
    public int getUiMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public ArrayList<String> getSentencesList() {
        return sentencesList;
    }

    public HashMap<String, ArrayList<String>> getSentenceCollection() { return sentenceCollection; }

}
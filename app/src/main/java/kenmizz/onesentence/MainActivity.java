package kenmizz.onesentence;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Map;

import kenmizz.onesentence.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> sentencesList = new ArrayList<String>();

    public static final String SENTENCES_PREFS = "sentencesPref";
    public static final String CONFIG_PREFS = "configsPref";
    public static final String SENATTR_PREFS = "SentencesAttributePref";
    public static final String NOTIFICATION_PREFS = "NotificationsPref";
    private static final String TAG = "MainActivity";
    public static final String CHANNEL_ID = "OneSentence_NotificationChannel";
    private int themeOptions = NIGHTMODE.DEFAULT.ordinal();
    private int newThemeOptions = 0;

    public enum NIGHTMODE {
        DEFAULT, LIGHT, GREY, DARK
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpConfigurations(CONFIG_PREFS);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                //create new channel if channel not exists
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT);
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
        setUpConfigurations(SENTENCES_PREFS);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.mainTabs);
        tabs.setupWithViewPager(viewPager);
        MaterialToolbar mainToolbar = findViewById(R.id.mainToolbar);
        final FloatingActionButton addButton = findViewById(R.id.addFloatingButton);
        mainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSentenceDialog();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1) { //SentenceList Page
                    addButton.hide();
                } else {
                    addButton.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
    }

    public void setUpConfigurations(String PreferencesName) {
        switch (PreferencesName) {
            case CONFIG_PREFS:
                SharedPreferences config = getSharedPreferences(PreferencesName, MODE_PRIVATE);
                if(!config.contains("themeOptions")) {
                    SharedPreferences.Editor configEdit = config.edit();
                    configEdit.putInt("themeOptions", themeOptions);
                    configEdit.apply();
                }
                themeOptions = config.getInt("themeOptions", NIGHTMODE.DEFAULT.ordinal());
                break;

            case SENTENCES_PREFS:
                SharedPreferences sentences = getSharedPreferences(PreferencesName, MODE_PRIVATE);
                Map<String, ?> Sentences = sentences.getAll();
                if(!Sentences.isEmpty()) {
                    for (Map.Entry<String, ?> Sentence : Sentences.entrySet()) {
                        sentencesList.add(Sentence.getValue().toString());
                    }
                }
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
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if(!text.isEmpty()) {
                            if(!sentencesList.contains(text)) {
                                sentencesList.add(text);
                                Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.AlreadyAdd) + text, Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(getWindow().getDecorView().getRootView(), text + getString(R.string.KeyExists), Snackbar.LENGTH_SHORT).show();
                            }
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
                        .setNeutralButton(R.string.coolapk, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String coolApkUrl = "http://www.coolapk.com/u/618459";
                                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                                urlIntent.setData(Uri.parse(coolApkUrl));
                                startActivity(urlIntent);
                            }
                        })
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

    public int getUiMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public ArrayList<String> getSentencesList() {
        return sentencesList;
    }


}
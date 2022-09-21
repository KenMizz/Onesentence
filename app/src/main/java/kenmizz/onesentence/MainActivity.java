package kenmizz.onesentence;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import kenmizz.onesentence.adapter.SentenceAdapter;
import kenmizz.onesentence.controller.SwipeController;
import kenmizz.onesentence.utils.Constants;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> sentencesList = new ArrayList<>();

    private static final String TAG = "MainActivity";

    private int themeOptions = ThemeMode.DEFAULT.ordinal();
    private int newThemeOptions = 0;

    public enum ThemeMode {
        DEFAULT, LIGHT, GREY, DARK
    }

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
        configureTheme(themeOptions);
        DynamicColors.applyToActivityIfAvailable(this);
        setContentView(R.layout.activity_main);
        try {
            setUpConfigurations(Constants.SENTENCES_PREFS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setUpSentencesView();
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
                    break;
            }
            return true;
        });
        addButton.setOnClickListener(view -> {
                addSentenceDialog();
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
    }

    /**
     * set up configurations
     * @param PreferencesName fileName
     * @throws IOException fileNotFound
     */
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
                        sentencesList.add(sentencesList.size(), Sentence.getValue().toString());
                    }
                }
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
                            //sentencesList.add(sentencesList.size(), text);
                            sentencesList.add(text);
                            Snackbar.make(findViewById(R.id.addFloatingButton), getString(R.string.AlreadyAdd) + text, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(R.id.addFloatingButton), text + getString(R.string.KeyExists), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
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
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                List<Fragment> fragments = fragmentManager.getFragments();
                                for(Fragment fragment: fragments) {
                                    fragmentManager.beginTransaction()
                                            .remove(fragment)
                                            .commit();
                                }
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

    public void setUpSentencesView() {
        RecyclerView mRecyclerView = findViewById(R.id.RecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        SentenceAdapter mAdapter = new SentenceAdapter(sentencesList, findViewById(R.id.emptyView), this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        SwipeController swipeController = new SwipeController(mAdapter, getWindow().getDecorView());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        if(!sentencesList.isEmpty()) {
            findViewById(R.id.emptyView).setVisibility(View.INVISIBLE);
        }
    }

    public void setNotificationDialog(final String sentence) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.set_long_time_notification)
                .setMessage(getString(R.string.set_setence_notification).replace("sentence", sentence))
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> createLongTimeNotification(sentence))
                .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
        dialogBuilder.show();
    }

    public void createLongTimeNotification(String sentence) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int NotificationId = new Random().nextInt();
        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra("id", NotificationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.app_notification_icon_small)
                .setContentTitle(sentence)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setShowWhen(false)
                .setOngoing(true)
                .addAction(R.drawable.app_notification_icon_small, getString(R.string.remove), pendingIntent);
        notificationManager.notify(NotificationId, notificationBuilder.build());
        SharedPreferences NotificationPrefs = getSharedPreferences(Constants.NOTIFICATION_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor notificationPrefsEditor = NotificationPrefs.edit();
        notificationPrefsEditor.putString(String.valueOf(NotificationId), sentence);
        notificationPrefsEditor.apply();
    }

    /**
     * this indicates that night mode is on or off
     * @return int
     */
    public int getUiMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }
}
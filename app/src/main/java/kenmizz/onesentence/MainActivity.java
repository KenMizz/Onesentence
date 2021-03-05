package kenmizz.onesentence;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import kenmizz.onesentence.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private NotificationManager notificationManager;
    private FloatingActionButton addFloatingButton;

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
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.mainTabs);
        tabs.setupWithViewPager(viewPager);
        addFloatingButton = findViewById(R.id.addButton);
        addFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSentenceDialog();
            }
        });
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
                    }
                })
                .show();
    }
}
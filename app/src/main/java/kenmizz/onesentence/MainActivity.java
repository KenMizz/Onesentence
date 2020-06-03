package kenmizz.onesentence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecylerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<SentenceItem> sentencesList = new ArrayList<SentenceItem>();


    public static final String SHARED_PREFS = "sentencesPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch(getDarkMode()) {
            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.AppThemeDark);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.AppTheme);

        }
        setContentView(R.layout.activity_main);
        loadSentencesList();
        setUpSentencesView();
        FloatingActionButton addButton = findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public int getDarkMode() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public void addMessage() {
        MaterialAlertDialogBuilder dialog;
        if(getDarkMode() == Configuration.UI_MODE_NIGHT_YES) {
            dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogDark);
        } else {
            dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogLight);
        }
        final TextInputEditText editText = new TextInputEditText(this);
        editText.setHint(R.string.sentence);
        dialog.setView(editText);
        dialog.setTitle(R.string.newsentence)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = Objects.requireNonNull(editText.getText()).toString();
                        if(!text.isEmpty()) {
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            if(sharedPreferences.contains(text)) {
                                Snackbar.make(getWindow().getDecorView().getRootView(), text + getResources().getString(R.string.KeyExists), Snackbar.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(text, text);
                                editor.apply();
                                Snackbar.make(getWindow().getDecorView().getRootView(), getResources().getString(R.string.AlreadyAdd) + text, Snackbar.LENGTH_SHORT).show();
                                //recreate(); //TODO: implement a better way instead of recreate;
                            }
                        }
                    }
                })
                .show();
    }

    public void showAboutDialog() {
        MaterialAlertDialogBuilder dialog;
        if(getDarkMode() == Configuration.UI_MODE_NIGHT_YES) {
            dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogDark);
        } else {
            dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogLight);
        }
        dialog.setTitle(R.string.about)
                .setView(getLayoutInflater().inflate(R.layout.about, null))
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public void loadSentencesList() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Map<String, ?>Sentences = sharedPreferences.getAll();
        if(!Sentences.isEmpty()) {
            TextView emptyView = findViewById(R.id.emptyView);
            ((ViewGroup) emptyView.getParent()).removeView(emptyView);
            for (Map.Entry<String, ?> Sentence : Sentences.entrySet()) {
                sentencesList.add(new SentenceItem(Sentence.getValue().toString()));
            }
        }
    }
    public void setUpSentencesView() {
            mRecylerView = findViewById(R.id.RecyclerView);
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mAdapter = new SentenceItemAdapter(sentencesList);
            mRecylerView.setHasFixedSize(true);
            mRecylerView.setLayoutManager(mLayoutManager);
            mRecylerView.setAdapter(mAdapter);
            SwipeController swipeController = new SwipeController();
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
            itemTouchHelper.attachToRecyclerView(mRecylerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.option) {
            //Toast.makeText(this, R.string.about, Toast.LENGTH_SHORT).show();
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

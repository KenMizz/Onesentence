package kenmizz.onesentence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class SentenceWidgetConfiguration extends AppCompatActivity {

    private ArrayList<SentenceItem> sentencesList = new ArrayList<>();

    public static final String SHARED_PREFS = "sentencesPref";
    public static final String WIDGETS_PREFS = "widgetsPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_widget_configuration);
        loadSentencesList();
        setUpSentencesView();
    }

    public void loadSentencesList() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Map<String, ?> Sentences = sharedPreferences.getAll();
        if(!Sentences.isEmpty()) {
            for (Map.Entry<String, ?> Sentence : Sentences.entrySet()) {
                sentencesList.add(new SentenceItem(Sentence.getValue().toString()));
            }
        }
    }

    public void setUpSentencesView() {
        RecyclerView mRecylerView = findViewById(R.id.RecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        SentenceItemAdapter mAdapter = new SentenceItemAdapter(sentencesList, true);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mLayoutManager);
        mRecylerView.setAdapter(mAdapter);
    }
}
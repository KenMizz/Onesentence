package kenmizz.onesentence.ui.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import kenmizz.onesentence.R;
import kenmizz.onesentence.adapter.DialogSentenceListAdapter;
import kenmizz.onesentence.adapter.SentenceListAdapter;

public class SentenceListFragment extends Fragment {
    private static final String TAG = "SentenceListFragment";

    private HashMap<String, ArrayList<String>> mSentenceCollection = new HashMap<>();
    private ArrayList<String> mSentencesList = new ArrayList<>();

    public static SentenceListFragment newInstance(HashMap<String, ArrayList<String>> sentenceCollection, ArrayList<String> sentencesList) {
        return new SentenceListFragment(sentenceCollection, sentencesList);
    }

    public SentenceListFragment(HashMap<String, ArrayList<String>> sentenceCollection, ArrayList<String> sentencesList) {
        mSentenceCollection = sentenceCollection;
        mSentencesList = sentencesList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sentence_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpSentenceListView();
        super.onViewCreated(view, savedInstanceState);
    }

    public void setUpSentenceListView() {
        RecyclerView mRecylerView = getView().findViewById(R.id.SentenceListRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SentenceListAdapter mAdapter = new SentenceListAdapter(this);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mLayoutManager);
        mRecylerView.setAdapter(mAdapter);
    }

    public HashMap<String, ArrayList<String>> getSentenceCollection() {
        return mSentenceCollection;
    }

    public ArrayList<String> getSentencesList() {
        return mSentencesList;
    }
}
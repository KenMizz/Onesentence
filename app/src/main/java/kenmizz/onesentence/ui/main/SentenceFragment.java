package kenmizz.onesentence.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kenmizz.onesentence.R;
import kenmizz.onesentence.SentenceItemAdapter;
import kenmizz.onesentence.SwipeController;

public class SentenceFragment extends Fragment {

    private ArrayList<String> mSentenceList;

    public static SentenceFragment newInstance(ArrayList<String> sentenceList) {
        SentenceFragment fragment = new SentenceFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("SentenceList", sentenceList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSentenceList = getArguments().getStringArrayList("SentenceList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sentence, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpSentencesView();
        if(!mSentenceList.isEmpty()) {
            ((TextView)getView().findViewById(R.id.emptyView)).setVisibility(View.INVISIBLE);
        }
    }

    public void setUpSentencesView() {
        RecyclerView mRecylerView = getView().findViewById(R.id.RecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SentenceItemAdapter mAdapter = new SentenceItemAdapter(mSentenceList, (TextView)getView().findViewById(R.id.emptyView), getContext());
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mLayoutManager);
        mRecylerView.setAdapter(mAdapter);
        SwipeController swipeController = new SwipeController(mAdapter, getView().getRootView());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(mRecylerView);
    }
}
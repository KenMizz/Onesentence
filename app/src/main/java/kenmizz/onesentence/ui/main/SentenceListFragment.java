package kenmizz.onesentence.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import kenmizz.onesentence.R;

public class SentenceListFragment extends Fragment {

    private HashMap<String, ArrayList<String>> mSentenceCollection = new HashMap<>();
    public static SentenceListFragment newInstance(HashMap<String, ArrayList<String>> sentenceCollection) {
        return new SentenceListFragment(sentenceCollection);
    }

    public SentenceListFragment(HashMap<String, ArrayList<String>> sentenceCollection) {
        mSentenceCollection = sentenceCollection;
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
        super.onViewCreated(view, savedInstanceState);
    }
}
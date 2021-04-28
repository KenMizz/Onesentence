package kenmizz.onesentence.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SentenceListItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SentenceListItemAdapter";

    private ArrayList<String> mSentenceCollectionList = new ArrayList<>();

    static class SentenceListItemViewHolder extends RecyclerView.ViewHolder {

        public SentenceListItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public SentenceListItemAdapter(ArrayList<String> sentenceCollectionList) {
        mSentenceCollectionList = sentenceCollectionList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        return new SentenceListItemViewHolder(new TextView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        ((TextView)holder.itemView).setText(mSentenceCollectionList.get(position));
    }

    @Override
    public int getItemCount() {
        return mSentenceCollectionList.size();
    }
}

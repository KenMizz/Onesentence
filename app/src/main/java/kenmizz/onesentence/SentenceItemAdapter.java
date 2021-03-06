package kenmizz.onesentence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SentenceItemAdapter extends RecyclerView.Adapter<SentenceItemAdapter.SentenceViewHolder> {
    private static final String TAG = "SentenceItemAdapter";

    private ArrayList<String> mSentenceList;


    static class SentenceViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public SentenceViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.SentenceView);
        }
    }

    public SentenceItemAdapter(ArrayList<String> sentenceList) {
        mSentenceList = sentenceList;
    }

    @NonNull
    @Override
    public SentenceItemAdapter.SentenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_item, parent, false);
        return new SentenceViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceItemAdapter.SentenceViewHolder holder, int position) {
        String currentSentence = mSentenceList.get(position);
        holder.mTextView.setText(currentSentence);
    }

    @Override
    public int getItemCount() {
        return mSentenceList.size();
    }
}

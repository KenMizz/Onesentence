package kenmizz.onesentence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SentenceItemAdapter extends RecyclerView.Adapter<SentenceItemAdapter.SentenceViewHolder> {
    private ArrayList<SentenceItem> sentenceItemArrayList;

    static class SentenceViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        SentenceViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.SentenceView);
        }
    }

    SentenceItemAdapter(ArrayList<SentenceItem> sentenceList) {
        sentenceItemArrayList = sentenceList;
    }

    @NonNull
    @Override
    public SentenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_item, parent, false);
        return new SentenceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceViewHolder holder, int position) {
        SentenceItem currentItem = sentenceItemArrayList.get(position);
        holder.mTextView.setText(currentItem.getSentence());
    }

    @Override
    public int getItemCount() {
        return sentenceItemArrayList.size();
    }
}

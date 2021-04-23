package kenmizz.onesentence;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SentenceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> mSentenceList = new ArrayList<>();

    static class SentenceListViewHolder extends RecyclerView.ViewHolder {

        public SentenceListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public SentenceListAdapter(ArrayList<String> sentenceList) {
        mSentenceList = sentenceList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        return new SentenceListViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TextView)holder.itemView).setText(mSentenceList.get(position));
    }

    @Override
    public int getItemCount() {
        return mSentenceList.size();
    }
}

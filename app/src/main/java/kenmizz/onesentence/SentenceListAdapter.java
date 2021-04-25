package kenmizz.onesentence;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SentenceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> mSentenceList = new ArrayList<>();
    private ArrayList<String> mSentenceCollection = new ArrayList<>();

    static class SentenceListViewHolder extends RecyclerView.ViewHolder {

        public SentenceListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public SentenceListAdapter(ArrayList<String> sentenceList, ArrayList<String> sentenceCollection) {
        mSentenceList = sentenceList;
        mSentenceCollection = sentenceCollection;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CheckBox itemView = new CheckBox(parent.getContext());
        return new SentenceListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CheckBox)holder.itemView).setText(mSentenceList.get(position));
        if(mSentenceCollection.contains(((CheckBox) holder.itemView).getText().toString())) {
            ((CheckBox) holder.itemView).setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return mSentenceList.size();
    }
}

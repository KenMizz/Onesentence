package kenmizz.onesentence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import kenmizz.onesentence.ui.main.SentenceListFragment;

public class SentenceListItemAdapter extends RecyclerView.Adapter<SentenceListItemAdapter.SentenceListViewHolder> {
    private static final String TAG = "SentenceListItemAdapter";

    private HashMap<String, ArrayList<String>> mSentenceList = new HashMap<>();
    private SentenceListFragment mSentenceListFragment;

    static class SentenceListViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public SentenceListViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.sentenceListNameTextView);
        }
    }

    public SentenceListItemAdapter(HashMap<String, ArrayList<String>> sentenceList, SentenceListFragment sentenceListFragment) {
        mSentenceList = sentenceList;
        mSentenceListFragment = sentenceListFragment;
    }

    @NonNull
    @Override
    public SentenceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View expansionPanelView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_list_item, parent, false);
        return new SentenceListViewHolder(expansionPanelView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SentenceListViewHolder holder, int position) {
        final String sentenceListName = (String) mSentenceList.keySet().toArray()[position];
        holder.mTextView.setText(sentenceListName);
        Button sentenceListItemEditButton = holder.itemView.findViewById(R.id.sentenceListItemEditButton);
        sentenceListItemEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSentenceListFragment.createSentenceListEditDialog(sentenceListName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSentenceList.size();
    }
}

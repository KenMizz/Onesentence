package kenmizz.onesentence.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import kenmizz.onesentence.R;
import kenmizz.onesentence.ui.main.SentenceListFragment;

/**
 * This adapter is for sentence list
 */
public class SentenceListAdapter extends RecyclerView.Adapter<SentenceListAdapter.SentenceListViewHolder> {
    private static final String TAG = "SentenceListAdapter";

    private HashMap<String, ArrayList<String>> mSentenceCollection = new HashMap<>();
    private ArrayList<String> mSentencesList = new ArrayList<>();

    private SentenceListFragment mSentenceListFragment;

    static class SentenceListViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        RecyclerView mRecyclerView;

        public SentenceListViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.sentenceListNameTextView);
            mRecyclerView = itemView.findViewById(R.id.sentenceListItemRecyclerView);
        }
    }

    public SentenceListAdapter(HashMap<String, ArrayList<String>> sentenceCollection, ArrayList<String> sentencesList, SentenceListFragment sentenceListFragment) {
        mSentenceCollection = sentenceCollection;
        mSentencesList = sentencesList;
        mSentenceListFragment = sentenceListFragment;
    }

    @NonNull
    @Override
    public SentenceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View expansionPanelView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_list_item, parent, false);
        return new SentenceListViewHolder(expansionPanelView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SentenceListViewHolder holder, final int position) {
        final String sentenceListName = (String) mSentenceCollection.keySet().toArray()[position];
        holder.mTextView.setText(sentenceListName);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mSentenceListFragment.getContext(), LinearLayoutManager.VERTICAL, false);
        SentenceListItemAdapter mAdapter = new SentenceListItemAdapter(mSentenceCollection.get(sentenceListName));
        holder.mRecyclerView.setAdapter(mAdapter);
        holder.mRecyclerView.setLayoutManager(mLayoutManager);
        Button sentenceListItemEditButton = holder.itemView.findViewById(R.id.sentenceListItemEditButton);
        sentenceListItemEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View sentenceListEditDialogView = mSentenceListFragment.getLayoutInflater().inflate(R.layout.sentence_list_edit_dialog, null);
                RecyclerView sentenceListEditDialogRecyclerView = sentenceListEditDialogView.findViewById(R.id.sentenceListEditDialogRecyclerView);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mSentenceListFragment.getContext(), LinearLayoutManager.VERTICAL, false);
                final DialogSentenceListAdapter mAdapter = new DialogSentenceListAdapter(mSentenceCollection.get(sentenceListName), mSentencesList);
                sentenceListEditDialogRecyclerView.setHasFixedSize(true);
                sentenceListEditDialogRecyclerView.setLayoutManager(mLayoutManager);
                sentenceListEditDialogRecyclerView.setAdapter(mAdapter);
                new MaterialAlertDialogBuilder(mSentenceListFragment.getContext())
                        .setTitle(R.string.edit)
                        .setView(sentenceListEditDialogView)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSentenceCollection.size();
    }
}

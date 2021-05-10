package kenmizz.onesentence.adapter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.w3c.dom.Text;

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
        LinearLayout mLinearLayout;

        public SentenceListViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.sentenceListNameTextView);
            mLinearLayout= itemView.findViewById(R.id.sentenceListLinearLayout);
        }
    }

    public SentenceListAdapter(SentenceListFragment sentenceListFragment) {
        mSentenceListFragment = sentenceListFragment;
        mSentenceCollection = mSentenceListFragment.getSentenceCollection();
        mSentencesList = mSentenceListFragment.getSentencesList();
    }

    @NonNull
    @Override
    public SentenceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View expansionPanelView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_list_item, parent, false);
        return new SentenceListViewHolder(expansionPanelView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final SentenceListViewHolder holder, final int position) {
        final String sentenceListName = (String) mSentenceCollection.keySet().toArray()[position];
        holder.mTextView.setText(sentenceListName);
        Button sentenceListItemEditButton = holder.itemView.findViewById(R.id.sentenceListItemEditButton);
        for(String sentenceListString : mSentenceCollection.get(sentenceListName)) {
            TextView stringView = new TextView(mSentenceListFragment.getContext());
            stringView.setText(sentenceListString);
            holder.mLinearLayout.addView(stringView);
        }
        sentenceListItemEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View sentenceListEditDialogView = mSentenceListFragment.getLayoutInflater().inflate(R.layout.sentence_list_edit_dialog, null);
                RecyclerView sentenceListEditDialogRecyclerView = sentenceListEditDialogView.findViewById(R.id.sentenceListEditDialogRecyclerView);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mSentenceListFragment.getContext(), LinearLayoutManager.VERTICAL, false);
                final DialogSentenceListAdapter mAdapter = new DialogSentenceListAdapter(sentenceListName, mSentenceListFragment);
                sentenceListEditDialogRecyclerView.setHasFixedSize(true);
                sentenceListEditDialogRecyclerView.setLayoutManager(mLayoutManager);
                sentenceListEditDialogRecyclerView.setAdapter(mAdapter);
                ArrayList<String> mSentenceCollectionList = mSentenceCollection.get(sentenceListName);
                new MaterialAlertDialogBuilder(mSentenceListFragment.getContext())
                        .setTitle(R.string.edit)
                        .setView(sentenceListEditDialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                holder.mLinearLayout.removeAllViews();
                                if(!mSentenceCollectionList.isEmpty()) {
                                    for(String sentence : mSentencesList) {
                                        if(!mSentenceCollectionList.contains(sentence)) {
                                            mSentenceCollectionList.remove(sentence);
                                            Log.d(TAG, "remove " + sentence);
                                        }
                                    }
                                    for(String sentenceListString : mSentenceCollectionList) {
                                        TextView stringView = new TextView(mSentenceListFragment.getContext());
                                        stringView.setText(sentenceListString);
                                        holder.mLinearLayout.addView(stringView);
                                    }
                                }
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSentenceCollection.size();
    }
}

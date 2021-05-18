package kenmizz.onesentence.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kenmizz.onesentence.ui.main.SentenceListFragment;

/**
 * This adapter is for sentence_list_edit dialog
 */

public class DialogSentenceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "DialogSentenceAdapter";

    private ArrayList<String> mSentenceCollectionList = new ArrayList<>();
    private ArrayList<String> mSentencesList = new ArrayList<>();
    private View mDialogView;

    static class SentenceListViewHolder extends RecyclerView.ViewHolder {

        public SentenceListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public DialogSentenceListAdapter(String sentenceListName, SentenceListFragment sentenceListFragment, View dialogView) {
        mSentenceCollectionList = sentenceListFragment.getSentenceCollection().get(sentenceListName);
        mSentencesList = sentenceListFragment.getSentencesList();
        mDialogView = dialogView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CheckBox itemView = new CheckBox(parent.getContext());
        return new SentenceListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CheckBox)holder.itemView).setText(mSentencesList.get(position));
        if(mSentenceCollectionList.contains(((CheckBox) holder.itemView).getText().toString())) {
            ((CheckBox) holder.itemView).setChecked(true);
        }
        ((CheckBox)holder.itemView).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()) {
                    mSentenceCollectionList.add(buttonView.getText().toString());
                } else {
                    mSentenceCollectionList.remove(buttonView.getText().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSentencesList.size();
    }
}

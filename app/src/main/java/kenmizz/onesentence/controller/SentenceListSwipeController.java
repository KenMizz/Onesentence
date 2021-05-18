package kenmizz.onesentence.controller;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import kenmizz.onesentence.R;
import kenmizz.onesentence.adapter.SentenceListAdapter;
import kenmizz.onesentence.ui.main.SentenceListFragment;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

public class SentenceListSwipeController extends Callback {

    private SentenceListFragment mSentenceListFragment;
    private SentenceListAdapter mSentenceListAdapter;

    public SentenceListSwipeController(SentenceListFragment sentenceListFragment, SentenceListAdapter sentenceListAdapter) {
        mSentenceListFragment = sentenceListFragment;
        mSentenceListAdapter = sentenceListAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT | RIGHT);
    }

    @Override
    public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
        String key = ((TextView)viewHolder.itemView.findViewById(R.id.sentenceListNameTextView)).getText().toString();
        mSentenceListFragment.removeSentenceCollectionList(key);
        mSentenceListAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        if(mSentenceListFragment.getSentenceCollection().isEmpty()) {
            mSentenceListFragment.getView().findViewById(R.id.emptyListView).setVisibility(View.VISIBLE);
        }
    }
}

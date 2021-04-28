package kenmizz.onesentence;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import kenmizz.onesentence.adapter.SentenceAdapter;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

public class SwipeController extends Callback {

    private final SentenceAdapter sentenceAdapter;
    private final View activityView;

    public SwipeController(SentenceAdapter mAdapter, View view) {
        sentenceAdapter = mAdapter;
        this.activityView = view;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT | RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        showDeleteSnackBar(position);
        sentenceAdapter.removeSentence(position);
    }

    public void showDeleteSnackBar(int position) {
        Snackbar.make(activityView.getRootView(), activityView.getResources().getText(R.string.remove) + " " + sentenceAdapter.getSentence(position), Snackbar.LENGTH_SHORT).show();
    }
}
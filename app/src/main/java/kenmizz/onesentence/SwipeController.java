package kenmizz.onesentence;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

class SwipeController extends Callback {

    private SentenceItemAdapter sentenceItemAdapter;
    private View activityView;

    public SwipeController(SentenceItemAdapter mAdapter, View view) {
        sentenceItemAdapter = mAdapter;
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
        sentenceItemAdapter.deleteSentence(position);
    }

    public void showDeleteSnackBar(int position) {
        Snackbar.make(activityView, activityView.getResources().getText(R.string.remove) + " " + sentenceItemAdapter.getSentencesArrayList().get(position).toString(), Snackbar.LENGTH_SHORT).show();
    }
}
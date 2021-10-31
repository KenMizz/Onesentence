package kenmizz.onesentence.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import kenmizz.onesentence.MainActivity;
import kenmizz.onesentence.R;
import kenmizz.onesentence.ui.main.SentenceListFragment;
import kenmizz.onesentence.widget.SentenceListWidgetConfiguration;

/**
 * This adapter is for sentence list
 */
public class SentenceListAdapter extends RecyclerView.Adapter<SentenceListAdapter.SentenceListViewHolder> {
    private static final String TAG = "SentenceListAdapter";

    private HashMap<String, ArrayList<String>> mSentenceCollection;
    private ArrayList<String> mSentencesList = new ArrayList<>();

    private SentenceListFragment mSentenceListFragment;

    private boolean itemOnConfiguration = false;
    private Context mActivityContext;

    private int mWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private SentenceListWidgetConfiguration mSentenceListWidgetConfiguration;

    static class SentenceListViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        LinearLayout mLinearLayout;

        public SentenceListViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.sentenceListNameTextView);
            mLinearLayout = itemView.findViewById(R.id.sentenceListLinearLayout);
        }
    }

    public SentenceListAdapter(SentenceListFragment sentenceListFragment) {
        mSentenceListFragment = sentenceListFragment;
        mSentenceCollection = mSentenceListFragment.getSentenceCollection();
        mSentencesList = mSentenceListFragment.getSentencesList();
    }

    public SentenceListAdapter(HashMap<String, ArrayList<String>> configurationSentenceCollection, boolean configuration, SentenceListWidgetConfiguration sentenceListWidgetConfiguration) {
        mSentenceCollection = configurationSentenceCollection;
        itemOnConfiguration = configuration;
        mSentenceListWidgetConfiguration = sentenceListWidgetConfiguration;
        mActivityContext = sentenceListWidgetConfiguration.getApplicationContext();
    }

    @NonNull
    @Override
    public SentenceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View expansionPanelView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_list_item, parent, false);
        return new SentenceListViewHolder(expansionPanelView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final SentenceListViewHolder holder, int position) {
        final String sentenceListName = (String) mSentenceCollection.keySet().toArray()[position];
        holder.mTextView.setText(sentenceListName);
        Button sentenceListItemEditButton = holder.itemView.findViewById(R.id.sentenceListItemEditButton);
        if(!itemOnConfiguration) {
            for(String sentenceListString : mSentenceCollection.get(sentenceListName)) {
                TextView stringView = new TextView(mSentenceListFragment.getContext());
                stringView.setText(sentenceListString);
                holder.mLinearLayout.addView(stringView);
            }
            sentenceListItemEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View sentenceListEditDialogView = mSentenceListFragment.getLayoutInflater().inflate(R.layout.sentence_list_edit_dialog, null);
                    if(!mSentencesList.isEmpty()) {
                        sentenceListEditDialogView.findViewById(R.id.dialogEmptyView).setVisibility(View.INVISIBLE);
                    }
                    RecyclerView sentenceListEditDialogRecyclerView = sentenceListEditDialogView.findViewById(R.id.sentenceListEditDialogRecyclerView);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mSentenceListFragment.getContext(), LinearLayoutManager.VERTICAL, false);
                    final DialogSentenceListAdapter mAdapter = new DialogSentenceListAdapter(sentenceListName, mSentenceListFragment, sentenceListEditDialogView);
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
                                    ArrayList<String> notExistsList = new ArrayList<>();
                                    holder.mLinearLayout.removeAllViews();
                                    if(!mSentenceCollectionList.isEmpty()) {
                                        for (String sentenceListString : mSentenceCollectionList) {
                                            if (mSentencesList.contains(sentenceListString)) {
                                                TextView stringView = new TextView(mSentenceListFragment.getContext());
                                                stringView.setText(sentenceListString);
                                                holder.mLinearLayout.addView(stringView);
                                            } else {
                                                notExistsList.add(sentenceListString);
                                            }
                                        }
                                    }
                                    if(!notExistsList.isEmpty()) {
                                        for(String notExistsString : notExistsList) {
                                            mSentenceCollectionList.remove(notExistsString);
                                        }
                                    }
                                }
                            })
                            .show();
                }
            });
        } else {
            for(String sentenceListString : mSentenceCollection.get(sentenceListName)) {
                TextView stringView = new TextView(mActivityContext);
                stringView.setText(sentenceListString);
                holder.mLinearLayout.addView(stringView);
            }
            sentenceListItemEditButton.setText(R.string.use);
            sentenceListItemEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setUpWidget(position);
                }
            });
        }
    }

    public void removeSentenceList(String key, int position) {
       notifyItemRemoved(position);
       mSentenceCollection.remove(key);
       if(mSentenceCollection.size() <= 0) {
           mSentenceListFragment.getView().findViewById(R.id.emptyListView).setVisibility(View.VISIBLE);
       }
    }

    @Override
    public int getItemCount() {
        return mSentenceCollection.size();
    }

    public void setUpWidget(int position) {
        Log.d(TAG, "Setting up widget for " + position);
        Random random = new Random();
        ArrayList<String> sentenceCollectionList = mSentenceCollection.get(position);
        Set<String> sentenceCollectionSet = new HashSet<String>(sentenceCollectionList); //convert to StringSet
        SharedPreferences sentenceListPref = mActivityContext.getSharedPreferences(SentenceListWidgetConfiguration.SENLIST_PREFS, Context.MODE_PRIVATE);
        SharedPreferences sentenceAttributePref = mActivityContext.getSharedPreferences(MainActivity.SENATTR_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor sentenceListPrefEditer = sentenceListPref.edit();
        SharedPreferences.Editor sentenceAttributePrefEditer = sentenceAttributePref.edit();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mActivityContext);
        RemoteViews remoteViews = new RemoteViews(mActivityContext.getPackageName(), R.layout.sentence_widget);
        remoteViews.setTextViewText(R.id.SentenceTextView, sentenceCollectionList.get(random.nextInt(sentenceCollectionList.size() + 1)));
        sentenceListPrefEditer.putStringSet(String.valueOf(mWidgetId), sentenceCollectionSet);
        sentenceAttributePrefEditer.putFloat(mWidgetId + "textSize", 25);
        sentenceAttributePrefEditer.putInt(mWidgetId + "textColor", mActivityContext.getColor(R.color.white));
        sentenceListPrefEditer.apply();
        sentenceAttributePrefEditer.apply();
        appWidgetManager.updateAppWidget(mWidgetId, remoteViews);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
        mSentenceListWidgetConfiguration.setResult(Activity.RESULT_OK, resultValue);
        mSentenceListWidgetConfiguration.finish();
    }
}

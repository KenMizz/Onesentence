package kenmizz.onesentence.ui.main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Random;

import kenmizz.onesentence.NotificationActivity;
import kenmizz.onesentence.R;
import kenmizz.onesentence.adapter.SentenceAdapter;
import kenmizz.onesentence.SwipeController;

import static android.content.Context.MODE_PRIVATE;
import static kenmizz.onesentence.MainActivity.CHANNEL_ID;
import static kenmizz.onesentence.MainActivity.NOTIFICATION_PREFS;

public class SentenceFragment extends Fragment {

    private ArrayList<String> mSentenceList;

    public static SentenceFragment newInstance(ArrayList<String> sentenceList) {
        SentenceFragment fragment = new SentenceFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("SentenceList", sentenceList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSentenceList = getArguments().getStringArrayList("SentenceList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sentence, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpSentencesView();
        if(!mSentenceList.isEmpty()) {
            ((TextView)getView().findViewById(R.id.emptyView)).setVisibility(View.INVISIBLE);
        }
    }

    public void setUpSentencesView() {
        RecyclerView mRecylerView = getView().findViewById(R.id.RecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SentenceAdapter mAdapter = new SentenceAdapter(mSentenceList, (TextView)getView().findViewById(R.id.emptyView), this);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mLayoutManager);
        mRecylerView.setAdapter(mAdapter);
        SwipeController swipeController = new SwipeController(mAdapter, getView().getRootView());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(mRecylerView);
    }

    public void setNotificationDialog(final String sentence) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.set_long_time_notification)
                .setMessage(getContext().getString(R.string.set_setence_notification).replace("sentence", sentence))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createLongTimeNotification(sentence);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        dialogBuilder.show();
    }

    public void createLongTimeNotification(String sentence) {
        NotificationManager notificationManager = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int NotificationId = new Random().nextInt();
        Intent intent = new Intent(getContext(), NotificationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra("id", NotificationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.app_notification_icon_small)
                .setContentTitle(sentence)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true)
                .addAction(R.drawable.app_icon_round, getContext().getString(R.string.remove), pendingIntent);
        notificationManager.notify(NotificationId, notificationBuilder.build());
        SharedPreferences NotificationPrefs = getContext().getSharedPreferences(NOTIFICATION_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor notificationPrefsEditor = NotificationPrefs.edit();
        notificationPrefsEditor.putString(String.valueOf(NotificationId), sentence);
        notificationPrefsEditor.apply();
    }
}
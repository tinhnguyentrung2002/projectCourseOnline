package com.example.courseonline.Activity.Learner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.TopicAdapter;
import com.example.courseonline.Dialog.Learner.SendTopicDialog;
import com.example.courseonline.Domain.TopicClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TopicActivity extends AppCompatActivity {
    private Toast toast;
    private LinearLayout btnMute,searchBox;
    private RecyclerView recyclerViewTopics;
    private TopicAdapter topicAdapter;
    private FloatingActionButton fabScrollToBottom;
    private SearchView editTextSearch;
    private TextView btnCloseSearch;
    private ImageView btnBackTopic, btnMoreOptions;
    private ArrayList<String> dateHeaders = new ArrayList<>();
    private ArrayList<TopicClass> topics = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView textViewEmpty, textViewHeader, textViewMembersCount, txtNotification;
    private Date lastLoadDate;
    private SendTopicDialog sendTopicDialog;
    private String discussion_id, discussion_title;
    private boolean allowNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_discussion_topic);
        Handler handler = new Handler(Looper.getMainLooper());
        discussion_id = getIntent().getStringExtra("discussion_id");
        discussion_title = getIntent().getStringExtra("discussion_title");
        mapping();
        topicAdapter = new TopicAdapter(TopicActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewTopics.setLayoutManager(layoutManager);
        recyclerViewTopics.setAdapter(topicAdapter);

        db = FirebaseFirestore.getInstance();
        sendTopicDialog = new SendTopicDialog(TopicActivity.this, discussion_id);
        loadTopics();

        fabScrollToBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewTopics.smoothScrollToPosition(topics.size() + dateHeaders.size());
            }
        });

        btnBackTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearch();
            }
        });
        btnMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(TopicActivity.this, v);
                getMenuInflater().inflate(R.menu.menu_topic_box, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.option_leave) {
                            db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isComplete()){
                                        if(task.getResult().getString("user_permission").equals("1"))
                                        {
                                            leave();
                                        }else{
                                            toastMes("Bạn là chủ nhóm, không thể rời nhóm");
                                        }
                                    }
                                }
                            });

                            return true;
                        } else if (item.getItemId() == R.id.option_add_new) {
                            sendTopicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(TopicActivity.this, android.R.color.transparent)));
                            sendTopicDialog.setCancelable(false);
                            sendTopicDialog.show();
                            return true;
                        } else if (item.getItemId() == R.id.option_search) {
                            showSearch();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });
        btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(TopicActivity.this,android.Manifest.permission.POST_NOTIFICATIONS) ==
                            PackageManager.PERMISSION_GRANTED) {
                        db.collection("Discussions").document(discussion_id).collection("DiscussionParticipants")
                                .document(mAuth.getCurrentUser().getUid())
                                .update("allowNotification", !allowNotification)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

                    } else {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                    }
                }

            }
        });

        recyclerViewTopics.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    fabScrollToBottom.show();
                } else   if (dy >= 0)  {
                    fabScrollToBottom.hide();
                }
                if (recyclerView.canScrollVertically(1)) {
                    fabScrollToBottom.show();
                }
            }
        });
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                } else {
                    return;
                }
            });
    private void showSearch() {
        searchBox.setVisibility(View.VISIBLE);

        Animation slideDown = AnimationUtils.loadAnimation(TopicActivity.this, R.anim.slide_down);
        searchBox.startAnimation(slideDown);
        editTextSearch.requestFocus();
        editTextSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                topicAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                topicAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void hideSearch() {
        Animation slideUp = AnimationUtils.loadAnimation(TopicActivity.this, R.anim.slide_up);
        searchBox.startAnimation(slideUp);

        slideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                searchBox.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    private void mapping() {
        recyclerViewTopics = findViewById(R.id.recyclerViewMessages);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        textViewHeader = findViewById(R.id.textViewSubject);
        fabScrollToBottom = findViewById(R.id.fabScrollToBottom);
        btnBackTopic = findViewById(R.id.btnBackTopic);
        editTextSearch = findViewById(R.id.editTextSearchTopic);
        btnCloseSearch = findViewById(R.id.btnCloseSearchTopic);
        searchBox = findViewById(R.id.searchBox);
        btnMoreOptions = findViewById(R.id.btnMoreOptions);
        textViewMembersCount = findViewById(R.id.textViewMembers);
        txtNotification = findViewById(R.id.txtNotification);
        btnMute = findViewById(R.id.btnMute);
    }
    public void updateUserState(String state) {
        db.collection("Discussions").document(discussion_id).collection("DiscussionParticipants")
                .document(mAuth.getCurrentUser().getUid())
                .update("userState", state)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    private void updateToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM Token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        db.collection("Discussions")
                                .document(discussion_id)
                                .collection("DiscussionParticipants")
                                .document(mAuth.getCurrentUser().getUid())
                                .update(new HashMap<String, Object>() {{
                                    put("userMessagingToken", token);
                                }});
                    }
                });
    }
    private void leave(){
        new AlertDialog.Builder(TopicActivity.this)
                .setTitle("Rời nhóm thảo luận")
                .setMessage("Bạn có chắc chắn muốn rời khỏi nhóm thảo luận này không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("Discussions")
                                .document(discussion_id)
                                .collection("DiscussionParticipants")
                                .document(mAuth.getCurrentUser().getUid())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            db.collection("Users")
                                                    .document(mAuth.getCurrentUser().getUid())
                                                    .collection("DiscussionGroups")
                                                    .document(discussion_id)
                                                    .delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                toastMes("Bạn đã rời nhóm thảo luận");
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            toastMes("Có lỗi xảy ra, không thể rời nhóm thảo luận");
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
    private void loadTopics() {
//        lastLoadDate = new Date();
        textViewHeader.setText(discussion_title);
        db.collection("Discussions").document(discussion_id).collection("DiscussionParticipants")
                .document(mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            allowNotification = value.getBoolean("allowNotification");
                            if (allowNotification) {
                                txtNotification.setText("Tắt thông báo");
                                txtNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mute, 0, 0, 0);
                            } else {
                                txtNotification.setText("Bật thông báo");
                                txtNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unmute, 0, 0, 0);
                            }
                        }
                    }
                });
        db.collection("Discussions")
                .document(discussion_id)
                .collection("DiscussionParticipants")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null || snapshot == null) {
                            return;
                        }

                        int totalMembers = snapshot.size();
                        textViewMembersCount.setText("Thành viên: " + totalMembers);
                    }
                });
        db.collection("Discussions")
                .document(discussion_id)
                .collection("Topics")
                .whereEqualTo("topic_state", true)
//    .whereGreaterThan("topic_upload", getDateNDaysAgo(days))
                .orderBy("topic_upload", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            db.collection("Discussions")
                                    .document(discussion_id)
                                    .collection("Topics")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) return;

                                            if (value.size() != 0) {
                                                for (QueryDocumentSnapshot topicSnapshot : value) {
                                                    List<String> topicSeen = (List<String>) topicSnapshot.get("topic_seen");
                                                    if (topicSeen != null && !topicSeen.contains(mAuth.getCurrentUser().getUid())) {
                                                        topicSeen.add(mAuth.getCurrentUser().getUid());
                                                    } else if (topicSeen == null) {
                                                        topicSeen = new ArrayList<>();
                                                        topicSeen.add(mAuth.getCurrentUser().getUid());
                                                    }
                                                    topicSnapshot.getReference().update("topic_seen", topicSeen);
                                                }
                                            }
                                        }
                                    });

                            textViewEmpty.setVisibility(View.GONE);
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                TopicClass topic = dc.getDocument().toObject(TopicClass.class);
                                if (topic.getTopic_upload() != null) {
                                    Date timestamp = topic.getTopic_upload();
                                    String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp);

                                    if (!dateHeaders.contains(dateStr)) {
                                        dateHeaders.add(dateStr);
                                    }
                                    if (!topics.contains(topic)) {
                                        insertTopicInOrder(topic);
                                        topicAdapter.notifyItemRangeChanged(0, topics.size());
                                        recyclerViewTopics.smoothScrollToPosition(topics.size() + dateHeaders.size());
                                    }
                                }
                                switch (dc.getType()) {
                                    case ADDED:

                                        break;

                                    case REMOVED:
                                        TopicClass removedTopic = dc.getDocument().toObject(TopicClass.class);
                                        Date timestamp1 = removedTopic.getTopic_upload();
                                        String dateStr1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp1);
                                        topics.remove(removedTopic);

                                        boolean stillHasTopicsForDate = false;
                                        for (TopicClass t : topics) {
                                            String topicDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(t.getTopic_upload());
                                            if (topicDate.equals(dateStr1)) {
                                                stillHasTopicsForDate = true;
                                                break;
                                            }
                                        }
                                        if (!stillHasTopicsForDate) {
                                            dateHeaders.remove(dateStr1);
                                        }

                                        topicAdapter.notifyDataSetChanged();
                                        break;

                                    case MODIFIED:
                                        break;
                                }
                            }
                        } else {
                            textViewEmpty.setVisibility(View.VISIBLE);
                            fabScrollToBottom.hide();
                            topics.clear();
                            dateHeaders.clear();
                            topicAdapter.notifyDataSetChanged();
                        }

                        topicAdapter.updateData(dateHeaders, topics);
                    }
                });

    }
    private void insertTopicInOrder(TopicClass newTopic) {
        int index = 0;
        while (index < topics.size() && topics.get(index).getTopic_upload().before(newTopic.getTopic_upload())) {
            index++;
        }
        topics.add(index, newTopic);
    }
//    private void loadMoreTopics(Handler handler) {
//        if (lastLoadDate == null) return;
////        progressBar.setVisibility(View.VISIBLE);
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                progressBar.setVisibility(View.GONE);
//                db.collection("Discussions")
//                        .document(discussion_id)
//                        .collection("Topics")
//                        .whereEqualTo("topic_state", true)
//                        .whereLessThan("topic_upload", lastLoadDate)
//                        .orderBy("topic_upload", Query.Direction.ASCENDING)
//                        .limit(100)
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//
//                                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
//                                    boolean newDateHeader = false;
//                                    ArrayList<TopicClass> newTopics = new ArrayList<>();
//
//                                    for (DocumentChange dc : task.getResult().getDocumentChanges()) {
//                                        TopicClass topic = dc.getDocument().toObject(TopicClass.class);
//                                        Date timestamp = topic.getTopic_upload();
//                                        String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp);
//
//                                        if (!dateHeaders.contains(dateStr)) {
//                                            dateHeaders.add(0, dateStr);
//                                            newDateHeader = true;
//                                        }
//
//                                        if (!topics.contains(topic)) {
//                                            newTopics.add(0, topic);
//                                            topicAdapter.notifyItemRangeChanged(0, topics.size());
//                                            recyclerViewTopics.smoothScrollToPosition(0);
//                                        }
//                                        lastLoadDate = timestamp;
//                                    }
//
//                                    if (!newTopics.isEmpty()) {
//                                        topics.addAll(0, newTopics);
//                                        topicAdapter.updateData(dateHeaders, topics);
////                                        recyclerViewTopics.smoothScrollToPosition(0);
//                                    }
//
//                                }
//                            }
//                        });
//            }
//        }, 500);
//    }

    private Date getDateNDaysAgo(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -n);
        return calendar.getTime();
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(TopicActivity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (sendTopicDialog != null) {
            sendTopicDialog.handleActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateToken();
        updateUserState("in-topics");
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserState("out-topics");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserState("in-topics");
        loadTopics();
        topicAdapter.notifyDataSetChanged();
    }

}

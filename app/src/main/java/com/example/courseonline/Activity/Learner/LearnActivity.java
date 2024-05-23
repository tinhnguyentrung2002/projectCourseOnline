package com.example.courseonline.Activity.Learner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.DocumentAdapter;
import com.example.courseonline.Adapter.Learner.VideoAdapter;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Domain.DocumentClass;
import com.example.courseonline.Domain.VideoClass;
import com.example.courseonline.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LearnActivity extends AppCompatActivity {

    RecyclerView recyclerPlaylist, recyclerDocument, recyclerQuiz;
    private VideoAdapter videoAdapter;
    private DocumentAdapter documentAdapter;
    private ArrayList<VideoClass> arrayVideo = new ArrayList<>();
    private ArrayList<DocumentClass> arrayDocument = new ArrayList<>();
    ImageView imgBack;
    NestedScrollView scrollLayout;
    TextView textHeading;
    private FirebaseAuth mAuth;
    private String headingKey, courseKey;
    private FirebaseFirestore db;
    TextView expand;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        courseKey = getIntent().getStringExtra("course_key");
        headingKey = getIntent().getStringExtra("heading_key");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mapping();
        LoadingAlert alert = new LoadingAlert(LearnActivity.this);
        alert.startLoading();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        loadData();
        executor.execute(new Runnable() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollLayout.setVisibility(View.GONE);
                        recyclerPlaylist.setLayoutManager(new LinearLayoutManager(LearnActivity.this,LinearLayoutManager.VERTICAL,false));
                        recyclerPlaylist.setHasFixedSize(true);
                        recyclerPlaylist.setItemViewCacheSize(20);
                        recyclerDocument.setLayoutManager(new LinearLayoutManager(LearnActivity.this,LinearLayoutManager.VERTICAL,false));
                        recyclerDocument.setHasFixedSize(true);
                        recyclerDocument.setItemViewCacheSize(20);
                        recyclerPlaylist.setAdapter(videoAdapter);
                        recyclerDocument.setAdapter(documentAdapter);

                        imgBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollLayout.setVisibility(View.VISIBLE);
                        alert.closeLoading();
                    }
                }, 300);

            }
        });
    }
    private void loadData(){

        documentAdapter = new DocumentAdapter(arrayDocument);
        videoAdapter = new VideoAdapter(arrayVideo, courseKey);

        if(mAuth.getCurrentUser() != null)
        {
            if(headingKey != null && courseKey!=null)
            {
                db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null)
                        {
                            return;
                        }
                        textHeading.setText(value.getString("heading_title"));
                        expand.setText(value.getString("heading_description"));
                    }
                });
                db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("video").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            return;
                        }
                        if(value.size() != 0 ){
                            arrayVideo.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.toObject(VideoClass.class) != null) {
                                    arrayVideo.add(doc.toObject(VideoClass.class));
                                }
                                videoAdapter.notifyDataSetChanged();
                            }
                        }


                    }
                });
                db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("document").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            return;
                        }
                        if(value.size() != 0 ){
                            arrayDocument.clear();
                            for (QueryDocumentSnapshot doc : value) {
                               arrayDocument.add(doc.toObject(DocumentClass.class));
                            }
                            documentAdapter.notifyDataSetChanged();}
                    }
                });
            }

        }
    }
    private void mapping(){
        scrollLayout = (NestedScrollView) findViewById(R.id.scrollLayout);
        recyclerPlaylist = (RecyclerView) findViewById(R.id.recyclerPlaylist);
        recyclerDocument = (RecyclerView) findViewById(R.id.recyclerDocument);
        recyclerQuiz = (RecyclerView) findViewById(R.id.recyclerQuiz);
        textHeading = (TextView) findViewById(R.id.textHeading);
        expand = (TextView) findViewById(R.id.expandDescription);
        imgBack = (ImageView) findViewById(R.id.imgBack);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
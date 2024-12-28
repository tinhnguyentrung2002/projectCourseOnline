package com.example.courseonline.Activity.Learner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.DocumentAdapter;
import com.example.courseonline.Adapter.Learner.QuizAdapter;
import com.example.courseonline.Adapter.Learner.VideoAdapter;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Domain.DocumentClass;
import com.example.courseonline.Domain.QuizClass;
import com.example.courseonline.Domain.VideoClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LearnActivity extends AppCompatActivity {

    RecyclerView recyclerPlaylist, recyclerDocument, recyclerQuiz;
    private VideoAdapter videoAdapter;
    private DocumentAdapter documentAdapter;
    private QuizAdapter quizAdapter;
    private ArrayList<VideoClass> arrayVideo = new ArrayList<>();
    private ArrayList<DocumentClass> arrayDocument = new ArrayList<>();
    private ArrayList<QuizClass> arrayQuiz = new ArrayList<>();
    ImageView imgBack;
    NestedScrollView scrollLayout;
    TextView textHeading, txtHeading, txtHeading1, txtHeading2;
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
                        recyclerQuiz.setLayoutManager(new LinearLayoutManager(LearnActivity.this,LinearLayoutManager.HORIZONTAL,false));
                        recyclerQuiz.setHasFixedSize(true);
                        recyclerQuiz.setItemViewCacheSize(20);
                        recyclerQuiz.setAdapter(quizAdapter);
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

        documentAdapter = new DocumentAdapter(arrayDocument, this);
        videoAdapter = new VideoAdapter(arrayVideo, courseKey, this);
        quizAdapter = new QuizAdapter(arrayQuiz, this);

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
                db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("video").orderBy("video_upload_date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            return;
                        }
                        arrayVideo.clear();
                        if(value.size() != 0 ){
                            txtHeading.setVisibility(View.VISIBLE);
                            recyclerPlaylist.setVisibility(View.VISIBLE);
                            Query query = db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("video");
                            AggregateQuery aggregateQuery = query.count();
                            aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                    AggregateQuerySnapshot snapshot = task.getResult();
                                    txtHeading.setText("Danh sách bài giảng ("+snapshot.getCount()+")");
                                }
                            });
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.toObject(VideoClass.class) != null) {
                                    arrayVideo.add(doc.toObject(VideoClass.class));
                                }
                            }

                        }else{
                            txtHeading.setVisibility(View.GONE);
                            recyclerPlaylist.setVisibility(View.GONE);
                        }
                        videoAdapter.notifyDataSetChanged();


                    }
                });
                db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("document").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            return;
                        }
                        arrayDocument.clear();
                        if(value.size() != 0 ){
                            txtHeading1.setVisibility(View.VISIBLE);
                            recyclerDocument.setVisibility(View.VISIBLE);
                            Query query = db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("document");
                            AggregateQuery aggregateQuery = query.count();
                            aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                    AggregateQuerySnapshot snapshot = task.getResult();
                                    txtHeading1.setText("Tài liệu bài giảng ("+snapshot.getCount()+")");
                                }
                            });
                            for (QueryDocumentSnapshot doc : value) {
                               arrayDocument.add(doc.toObject(DocumentClass.class));
                            }

                        }else {
                            txtHeading1.setVisibility(View.GONE);
                            recyclerDocument.setVisibility(View.GONE);
                        }
                        documentAdapter.notifyDataSetChanged();
                    }
                });
                db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("quiz").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            return;
                        }
                        arrayQuiz.clear();
                        if(value.size() != 0){
                            txtHeading2.setVisibility(View.VISIBLE);
                            recyclerQuiz.setVisibility(View.VISIBLE);
                            Query query = db.collection("Courses").document(courseKey).collection("Heading").document(headingKey).collection("quiz");
                            AggregateQuery aggregateQuery = query.count();
                            aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                    AggregateQuerySnapshot snapshot = task.getResult();
                                    txtHeading2.setText("Trắc nghiệm củng cố ("+snapshot.getCount()+")");
                                }
                            });
                            for(QueryDocumentSnapshot doc : value)
                            {
                                arrayQuiz.add(doc.toObject(QuizClass.class));
                            }

                        }else  {
                            txtHeading2.setVisibility(View.GONE);
                            recyclerQuiz.setVisibility(View.GONE);
                        }
                        quizAdapter.notifyDataSetChanged();
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
        txtHeading = (TextView) findViewById(R.id.textPlaylist);
        txtHeading1 = (TextView) findViewById(R.id.textHeading1);
        txtHeading2 = (TextView) findViewById(R.id.textHeading2);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(documentAdapter != null) documentAdapter.release();
        if(videoAdapter != null) videoAdapter.release();
        if(quizAdapter != null) quizAdapter.release();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }
}
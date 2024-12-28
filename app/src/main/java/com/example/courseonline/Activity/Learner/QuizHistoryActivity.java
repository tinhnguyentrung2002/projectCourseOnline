package com.example.courseonline.Activity.Learner;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Domain.HistoryQuizClass;
import com.example.courseonline.Adapter.Learner.HistoryQuizAdapter;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppCompatButton btnBack;
    private HistoryQuizAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<HistoryQuizClass> historyQuizList;
    private TextView tvTrong, tvQuizName, tvQuizRequire;
    private String courseId;
    private String headingId;
    private String quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_history);

        mapping();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        courseId = getIntent().getStringExtra("course_id");
        headingId = getIntent().getStringExtra("heading_id");
        quizId = getIntent().getStringExtra("quiz_id");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryQuizAdapter(QuizHistoryActivity.this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        loadHistoryData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void mapping() {
        recyclerView = findViewById(R.id.recycler_view_history);
        tvTrong = findViewById(R.id.tv_trong);
        btnBack = findViewById(R.id.btn_back_history_quiz);
        tvQuizName = findViewById(R.id.quizName);
        tvQuizRequire = findViewById(R.id.quizRequire);
    }

    private void loadHistoryData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("Courses")
                .document(courseId)
                .collection("Heading")
                .document(headingId)
                .collection("quiz")
                .document(quizId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isComplete()){
                            tvQuizName.setText("Bài: " + task.getResult().getString("quiz_title"));
                            tvQuizRequire.setText("Yêu cầu: " + task.getResult().getString("quiz_require") +"%");
                        }
                    }
                });
        db.collection("Courses")
                .document(courseId)
                .collection("Heading")
                .document(headingId)
                .collection("quiz")
                .document(quizId)
                .collection("history")
                .whereEqualTo("history_quiz_user_id", userId)
                .orderBy("history_quiz_upload", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                historyQuizList = new ArrayList<>();
                                for (DocumentSnapshot document : querySnapshot) {
                                    HistoryQuizClass historyQuiz = document.toObject(HistoryQuizClass.class);
                                    historyQuizList.add(historyQuiz);
                                }
                                adapter.updateData(historyQuizList);
                                tvTrong.setVisibility(View.GONE);
                            } else {
                                tvTrong.setVisibility(View.VISIBLE);
                            }
                        } else {
                            tvTrong.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}
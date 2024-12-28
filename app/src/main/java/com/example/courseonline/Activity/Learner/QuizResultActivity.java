package com.example.courseonline.Activity.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.courseonline.Domain.QuestionClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class QuizResultActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private ArrayList<QuestionClass> arrayQuestion = new ArrayList<>();
    private ArrayList<String> arrayAnswer = new ArrayList<>();
    private String quizID, courseID, headingID;
    private TextView score_progress, score_subtitle, time_subtitle, best_score, correct, wrong, skip;
    private CircularProgressIndicator circular_progress;
    private AppCompatButton btnShowAnswer, btnFinishAnswer;
    private LinearLayout btnHistoryQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);
        mapping();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        quizID = getIntent().getStringExtra("quiz_id");
        courseID = getIntent().getStringExtra("course_id");
        headingID = getIntent().getStringExtra("heading_id");
        arrayQuestion = getIntent().getParcelableArrayListExtra("questionList");
        arrayAnswer = getIntent().getStringArrayListExtra("quiz_array_answer");

        loadResult();

        btnFinishAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizResultActivity.this, QuizActivity.class);
                intent.putParcelableArrayListExtra("questionList", arrayQuestion);
                intent.putStringArrayListExtra("quiz_array_answer", arrayAnswer);
                intent.putExtra("quiz_id", quizID);
                intent.putExtra("quiz_state", "show");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
        btnHistoryQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizResultActivity.this, QuizHistoryActivity.class);

                intent.putExtra("course_id", courseID);
                intent.putExtra("heading_id", headingID);
                intent.putExtra("quiz_id", quizID);

                startActivity(intent);
            }
        });
    }
    private void loadResult(){
        if(mAuth.getCurrentUser() != null){
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").document(quizID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isComplete()){
                        double totalCorrect = task.getResult().getDouble("quiz_total_correct");
                        double totalQuestion = task.getResult().getDouble("quiz_total_question");
                        int progress = (int) ((totalCorrect / totalQuestion) * 100);
                        score_progress.setText(progress + "%");
                        circular_progress.setProgress(progress);
                        score_subtitle.setText("Tổng: " + task.getResult().getLong("quiz_total_question"));
                        time_subtitle.setText("Thời gian: "+task.getResult().getString("quiz_time_taken"));
                        correct.setText(String.valueOf(task.getResult().getLong("quiz_total_correct")));
                        wrong.setText(String.valueOf(task.getResult().getLong("quiz_total_wrong")));
                        skip.setText(String.valueOf(task.getResult().getLong("quiz_total_skip")));
                        best_score.setText("Điểm cao nhất (%): " + task.getResult().getString("quiz_best_score"));
//                        int best = (int) Double.parseDouble(task.getResult().getString("quiz_best_score"));
//                        if(progress > best)
//                        {
//                            best_score.setText("Điểm cao nhất (%): " + progress);
//                            DocumentReference ref = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").document(task.getResult().getId());
//                            Map hashMap = new HashMap<>();
//                            hashMap.put(QUIZ_BEST_SCORE, String.valueOf(progress));
//                            ref.update(hashMap).addOnSuccessListener(new OnSuccessListener() {
//                                @Override
//                                public void onSuccess(Object o) {
//
//                                }
//                            });
//                        }

                    }
                }
            });
        }
    }
    private void mapping(){
        score_progress = (TextView) findViewById(R.id.score_progress_text);
        score_subtitle = (TextView) findViewById(R.id.score_subtitle);
        time_subtitle = (TextView) findViewById(R.id.time_subtitle);
        best_score = (TextView) findViewById(R.id.best_score);
        correct = (TextView) findViewById(R.id.correct_answer_amount);
        wrong = (TextView) findViewById(R.id.wrong_answer_amount);
        skip = (TextView) findViewById(R.id.skip_answer_amount);
        circular_progress = (CircularProgressIndicator) findViewById(R.id.score_progress_indicator);
        btnShowAnswer = (AppCompatButton) findViewById(R.id.btn_showAnswer);
        btnFinishAnswer = (AppCompatButton) findViewById(R.id.btn_finishAnswer);
        btnHistoryQuiz = (LinearLayout) findViewById(R.id.historyQuizBtn);
    }
}
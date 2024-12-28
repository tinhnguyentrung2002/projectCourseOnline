package com.example.courseonline.Activity.Learner;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.courseonline.BuildConfig;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Class.TypewriterTextView;
import com.example.courseonline.Domain.QuestionClass;
import com.example.courseonline.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView txtQuestionCount, txtQuizTime, txtQuestion, txtAnswer, txtExplain, txtToolbar;
    TypewriterTextView txtExplainWithBot;
    AppCompatButton option1, option2, option3, option4, next, previous;
    TextInputEditText editAnswer;
    TextInputLayout layoutEditAnswer;
    LinearLayout linearAnswer, botBtn;
    LinearProgressIndicator progressIndicator;
    ImageView imgBackQuiz, icon_timer, icon_bot, imageExplain;
    private String  quizState, quizTime, quizID, courseID, headingID;
    private String selectedAnswer = "";
    private int totalQuiz = 0;
    private int checkedQuiz = 0;
    private ArrayList<QuestionClass> arrayQuestion = new ArrayList<>();
    private ArrayList<String> arrayAnswer = new ArrayList<>();
    private LoadingAlert alert;
    private int currentQuestionIndex = 0;
    private int totalCorrect = 0;
    private int totalWrong = 0;
    private int totalSkip = 0;
    private static final String KEY_PROGRESS_EX = "own_course_ex_progress";
    private static final String KEY_COMPLETE_COURSE = "own_course_complete";
    private static final String KEY_PROGRESS_COURSE = "own_course_progress";
    private static final String QUIZ_KEY = "quiz_id";
    private static final String QUIZ_KEY_COURSE_ID = "course_id";
    private static final String QUIZ_TOTAL_QUESTION = "quiz_total_question";
    private static final String QUIZ_BEST_SCORE = "quiz_best_score";
    private static final String QUIZ_TOTAL_CORRECT = "quiz_total_correct";
    private static final String QUIZ_TOTAL_WRONG = "quiz_total_wrong";
    private static final String QUIZ_TOTAL_SKIP = "quiz_total_skip";
    private static final String QUIZ_TIME_TAKEN = "quiz_time_taken";
    private static final String QUIZ_STATE = "quiz_state";
    private static final String QUIZ_UPLOAD_TIME = "quiz_time_upload";
    private static final String KEY_USER_POINTS = "user_points";
    private static final String HISTORY_QUIZ_ID = "history_quiz_id";
    private static final String HISTORY_QUIZ_STATE = "history_quiz_state";
    private static final String HISTORY_QUIZ_TIME_TAKEN = "history_quiz_time_taken";
    private static final String HISTORY_QUIZ_USER_ID = "history_quiz_user_id";
    private static final String HISTORY_QUIZ_SCORE= "history_quiz_score";
    private static final String HISTORY_QUIZ_UPLOAD = "history_quiz_upload";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        alert = new LoadingAlert(QuizActivity.this);

        ColorStateList colorStateList;
        int color2 = ContextCompat.getColor(this, R.color.orange);
        int color3 = ContextCompat.getColor(this, R.color.grey);
        mapping();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        arrayQuestion = getIntent().getParcelableArrayListExtra("questionList");
        if(getIntent().getStringArrayListExtra("quiz_array_answer") != null)
        {
            arrayAnswer = getIntent().getStringArrayListExtra("quiz_array_answer");
        }
       if(getIntent().getStringExtra("quiz_time") != null)
       {
           quizTime = getIntent().getStringExtra("quiz_time");
       }
        quizState = getIntent().getStringExtra("quiz_state");
        quizID = getIntent().getStringExtra("quiz_id");
        courseID = getIntent().getStringExtra("course_id");
        headingID = getIntent().getStringExtra("heading_id");

        txtToolbar.setText(getIntent().getStringExtra("quiz_title")) ;
        imgBackQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        botBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(arrayQuestion.get(currentQuestionIndex).getQuestion_type().equals("choice"))
                    callBot("Giải thích chi tiết câu hỏi sau: " + arrayQuestion.get(currentQuestionIndex).getQuestion_content() +
                        "\nHãy đối chiếu và phân tích các đáp án: " +
                        arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(0) + ", " +
                        arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(1) + ", " +
                        arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(2) + ", " +
                        arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(3) + ", " +
                        "\nĐưa ra dẫn chứng cụ thể để chứng minh đáp án đúng là: " +
                        arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer()+
                            "\nVà tại sao các đáp án còn lại sai.");
                else
                    callBot("Giải thích chi tiết câu hỏi sau: " + arrayQuestion.get(currentQuestionIndex).getQuestion_content() +
                        "\nĐưa ra dẫn chứng cụ thể để chứng minh đáp án đúng là: " +
                        arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer());

                icon_bot.setVisibility(View.VISIBLE);
                txtExplainWithBot.setVisibility(View.VISIBLE);
            }
        });

        if(quizState.compareTo("new") == 0 || quizState.compareTo("exist") == 0){
            initArrayAnswer();
            txtQuizTime.setVisibility(View.VISIBLE);
            linearAnswer.setVisibility(View.GONE);
            txtAnswer.setVisibility(View.GONE);
            imageExplain.setVisibility(View.GONE);
            icon_bot.setVisibility(View.GONE);
            icon_timer.setVisibility(View.VISIBLE);

            option1.setOnClickListener(this::onClick);
            option2.setOnClickListener(this::onClick);
            option3.setOnClickListener(this::onClick);
            option4.setOnClickListener(this::onClick);
            next.setOnClickListener(this::onClick);
            previous.setOnClickListener(this::onClick);

            if(currentQuestionIndex == 0){
                colorStateList = ColorStateList.valueOf(color3);
                previous.setBackgroundTintList(colorStateList);
                previous.setEnabled(false);
            }
            else{
                colorStateList = ColorStateList.valueOf(color2);
                previous.setBackgroundTintList(colorStateList);
                previous.setEnabled(true);
            }
            loadDiscussions();
            startTimer();
        }else if(quizState.compareTo("show") == 0){
            txtQuizTime.setVisibility(View.GONE);
            linearAnswer.setVisibility(View.VISIBLE);
            txtAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(linearAnswer.getVisibility() == View.GONE)
                    {
                        AutoTransition autoTransition = new AutoTransition();
                        autoTransition.setDuration(30);
                        TransitionManager.beginDelayedTransition(linearAnswer, autoTransition);
                        linearAnswer.setVisibility(View.VISIBLE);
                        txtAnswer.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.baseline_arrow_drop_down_24,0);
                    }

                    else{
                        AutoTransition autoTransition = new AutoTransition();
                        autoTransition.setDuration(30);
                        TransitionManager.beginDelayedTransition(linearAnswer, autoTransition);
                        linearAnswer.setVisibility(View.GONE);
                        txtAnswer.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.baseline_arrow_drop_up_24,0);
                    }
                }
            });
            txtAnswer.setVisibility(View.VISIBLE);
            imageExplain.setVisibility(View.VISIBLE);
            icon_timer.setVisibility(View.GONE);
            next.setOnClickListener(this::onClick);
            previous.setOnClickListener(this::onClick);
            if(currentQuestionIndex == 0){
                colorStateList = ColorStateList.valueOf(color3);
                previous.setBackgroundTintList(colorStateList);
                previous.setEnabled(false);
            }
            else{
                colorStateList = ColorStateList.valueOf(color2);
                previous.setBackgroundTintList(colorStateList);
                previous.setEnabled(true);
            }
            loadDiscussions();
        }
    }
    private void loadDiscussions(){
        icon_bot.setVisibility(View.GONE);
        txtExplainWithBot.setVisibility(View.GONE);
        txtExplainWithBot.setText("");
        option1.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        option2.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        option3.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        option4.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        editAnswer.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        if(quizState.equals("new") || quizState.equals("exist"))
        {
            if (currentQuestionIndex == arrayQuestion.size()) {
                finishQuiz();
            }else{
                int color2 = ContextCompat.getColor(this, R.color.orange);
                int color3 = ContextCompat.getColor(this, R.color.grey);
                ColorStateList colorStateList;
                selectedAnswer = "";

                if(currentQuestionIndex == arrayQuestion.size() -1 ){
                    next.setText("Kết thúc");
                }else{
                    next.setText("Tiếp theo");
                }
                if(currentQuestionIndex == 0){
                    colorStateList = ColorStateList.valueOf(color3);
                    previous.setBackgroundTintList(colorStateList);
                    previous.setEnabled(false);
                }
                else{
                    colorStateList = ColorStateList.valueOf(color2);
                    previous.setBackgroundTintList(colorStateList);
                    previous.setEnabled(true);
                }
                txtQuestionCount.setText("Câu hỏi " + (currentQuestionIndex + 1) + "/" + arrayQuestion.size());
                progressIndicator.setProgress((int) ((currentQuestionIndex / (float) arrayQuestion.size()) * 100));
                txtQuestion.setText(arrayQuestion.get(currentQuestionIndex).getQuestion_content());
                if(arrayQuestion.get(currentQuestionIndex).getQuestion_type().equals("choice"))
                {
                    show();

                    option1.setText(arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(0));
                    option2.setText(arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(1));
                    option3.setText(arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(2));
                    option4.setText(arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(3));

                    zoomInView(txtQuestion);
                    zoomInView(option1);
                    zoomInView(option2);
                    zoomInView(option3);
                    zoomInView(option4);

                    if(arrayAnswer.get(currentQuestionIndex).trim().toString().equals(option1.getText().toString())){
                        colorStateList = ColorStateList.valueOf(color2);
                        option1.setBackgroundTintList(colorStateList);
                        selectedAnswer = arrayAnswer.get(currentQuestionIndex);
                    }
                    if(arrayAnswer.get(currentQuestionIndex).trim().toString().equals(option2.getText().toString())){
                        colorStateList = ColorStateList.valueOf(color2);
                        option2.setBackgroundTintList(colorStateList);
                        selectedAnswer = arrayAnswer.get(currentQuestionIndex);
                    }
                    if(arrayAnswer.get(currentQuestionIndex).trim().toString().equals(option3.getText().toString())){
                        colorStateList = ColorStateList.valueOf(color2);
                        option3.setBackgroundTintList(colorStateList);
                        selectedAnswer = arrayAnswer.get(currentQuestionIndex);
                    }
                    if(arrayAnswer.get(currentQuestionIndex).trim().toString().equals(option4.getText().toString())){
                        colorStateList = ColorStateList.valueOf(color2);
                        option4.setBackgroundTintList(colorStateList);
                        selectedAnswer = arrayAnswer.get(currentQuestionIndex);
                    }
                }else{
                    editAnswer.setEnabled(true);
                    hide();

                    if(arrayAnswer.get(currentQuestionIndex).trim().equals("null")) editAnswer.setText("");
                    else editAnswer.setText(arrayAnswer.get(currentQuestionIndex));
                    selectedAnswer = arrayAnswer.get(currentQuestionIndex);

                    zoomInView(txtQuestion);
                    zoomInView(layoutEditAnswer);

                }
            }

        }else{
            if (currentQuestionIndex == arrayQuestion.size()) {
                finishShow();
            }else{
                int color1 = ContextCompat.getColor(this, R.color.orange);
                int color2 = ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_error);
                int color3 = ContextCompat.getColor(this, R.color.grey);
                int color4 = ContextCompat.getColor(this, android.R.color.holo_green_light);
                ColorStateList colorStateList;


                if(currentQuestionIndex == arrayQuestion.size() -1 ){
                    next.setText("Kết thúc");
                }else{
                    next.setText("Tiếp theo");
                }
                if(currentQuestionIndex == 0){
                    colorStateList = ColorStateList.valueOf(color3);
                    previous.setBackgroundTintList(colorStateList);
                    previous.setEnabled(false);
                }
                else{
                    colorStateList = ColorStateList.valueOf(color1);
                    previous.setBackgroundTintList(colorStateList);
                    previous.setEnabled(true);
                }
                txtQuestionCount.setText("Câu hỏi " + (currentQuestionIndex + 1) + "/" + arrayQuestion.size());
                progressIndicator.setProgress((int) ((currentQuestionIndex / (float) arrayQuestion.size()) * 100));
                txtQuestion.setText(arrayQuestion.get(currentQuestionIndex).getQuestion_content().replace("\\n", "\n"));
                if(!arrayQuestion.get(currentQuestionIndex).getQuestion_explain().trim().equals(""))
                {
                    txtExplain.setText("Đáp án đúng: "+ arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer().replace("\\n", "\n") + ". Giải thích: " +arrayQuestion.get(currentQuestionIndex).getQuestion_explain().replace("\\n", "\n"));
                }else{
                    txtExplain.setText("Không có");
                }

                txtExplainWithBot.setText("");
                if(arrayQuestion.get(currentQuestionIndex).getQuestion_type().trim().equals("choice"))
                {
                    show();
                    option1.setText(arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(0));
                    option2.setText(arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(1));
                    option3.setText(arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(2));
                    option4.setText(arrayQuestion.get(currentQuestionIndex).getQuestion_answer_option().get(3));

                    zoomInView(txtQuestion);
                    zoomInView(option1);
                    zoomInView(option2);
                    zoomInView(option3);
                    zoomInView(option4);


                    if(arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer().trim().equals(option1.getText().toString().trim())){

                        colorStateList = ColorStateList.valueOf(color4);
                        option1.setBackgroundTintList(colorStateList);
                    }
                    if(arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer().trim().equals(option2.getText().toString().trim())){
                        colorStateList = ColorStateList.valueOf(color4);
                        option2.setBackgroundTintList(colorStateList);
                    }
                    if(arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer().trim().equals(option3.getText().toString().trim())){
                    colorStateList = ColorStateList.valueOf(color4);
                    option3.setBackgroundTintList(colorStateList);
                    }
                    if(arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer().trim().equals(option4.getText().toString().trim())){
                        colorStateList = ColorStateList.valueOf(color4);
                        option4.setBackgroundTintList(colorStateList);
                    }
                    if(arrayAnswer.get(currentQuestionIndex).equals(option1.getText().toString())){
                        if(arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer().trim().equals(option1.getText().toString().trim())){
                            colorStateList = ColorStateList.valueOf(color4);
                            option1.setBackgroundTintList(colorStateList);
                            option1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_answer, 0);
                        }else{
                            colorStateList = ColorStateList.valueOf(color2);
                            option1.setBackgroundTintList(colorStateList);
                        }

                    }
                    if(arrayAnswer.get(currentQuestionIndex).trim().equals(option2.getText().toString().trim())){
                        if(arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer().trim().equals(option2.getText().toString().trim())){
                            colorStateList = ColorStateList.valueOf(color4);
                            option2.setBackgroundTintList(colorStateList);
                            option2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_answer, 0);
                        }else{
                            colorStateList = ColorStateList.valueOf(color2);
                            option2.setBackgroundTintList(colorStateList);
                        }

                    }
                    if(arrayAnswer.get(currentQuestionIndex).trim().equals(option3.getText().toString().trim())){
                        if(arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer().trim().equals(option3.getText().toString().trim())){
                            colorStateList = ColorStateList.valueOf(color4);
                            option3.setBackgroundTintList(colorStateList);
                            option3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_answer, 0);
                        }else{
                            colorStateList = ColorStateList.valueOf(color2);
                            option3.setBackgroundTintList(colorStateList);
                        }

                    }
                    if(arrayAnswer.get(currentQuestionIndex).trim().equals(option4.getText().toString().trim())){
                        if(arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer().trim().equals(option4.getText().toString().trim())){
                            colorStateList = ColorStateList.valueOf(color4);
                            option4.setBackgroundTintList(colorStateList);
                            option4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_answer, 0);
                        }else{
                            colorStateList = ColorStateList.valueOf(color2);
                            option4.setBackgroundTintList(colorStateList);
                        }

                    }
                }else{
                    editAnswer.setEnabled(false);
                    hide();
                    String[] splitText = arrayQuestion.get(currentQuestionIndex).getQuestion_correct_answer().split(",");
                    for(String str : splitText)
                    {
                        if(arrayAnswer.get(currentQuestionIndex).trim().equals("null"))
                        {
                            editAnswer.setText("Chưa nhập đáp án");
                            layoutEditAnswer.setBoxBackgroundColor(color2);
                            break;
                        }else if (str.trim().equalsIgnoreCase(arrayAnswer.get(currentQuestionIndex).trim()) == false)
                        {
                            editAnswer.setText(arrayAnswer.get(currentQuestionIndex));
                            layoutEditAnswer.setBoxBackgroundColor(color2);
                        }
                        else
                        {
                            editAnswer.setText(arrayAnswer.get(currentQuestionIndex));
                            layoutEditAnswer.setBoxBackgroundColor(color4);
                            editAnswer.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_answer, 0);
                            break;
                        }
                    }


                    zoomInView(txtQuestion);
                    zoomInView(layoutEditAnswer);

                }
            }

        }

    }
    private void finishShow(){
        Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
        intent.putExtra("quiz_id", quizID);
        intent.putExtra("course_id", courseID);
        intent.putExtra("heading_id", headingID);
        intent.putParcelableArrayListExtra("questionList", arrayQuestion);
        intent.putStringArrayListExtra("quiz_array_answer", arrayAnswer);
        startActivity(intent);
        finish();
    }
    private void finishQuiz(){
        alert.startLoading();
        Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
        intent.putExtra("quiz_id", quizID);
        intent.putExtra("course_id", courseID);
        intent.putExtra("heading_id", headingID);
        intent.putParcelableArrayListExtra("questionList", arrayQuestion);
        intent.putStringArrayListExtra("quiz_array_answer", arrayAnswer);
        for(int i = 0; i < arrayQuestion.size(); i++)
        {
            if(arrayQuestion.get(i).getQuestion_type().equals("choice"))
            {
                if(arrayAnswer.get(i).trim().equals("null"))
                {
                    totalSkip++;
                }
                else if(arrayAnswer.get(i).trim().equalsIgnoreCase(arrayQuestion.get(i).getQuestion_correct_answer().trim())){
                    totalCorrect++;
                }
                else{
                    totalWrong++;
                }
            }else{
                boolean isSkip = false;
                boolean isCor = false;
                String[] splitText = arrayQuestion.get(i).getQuestion_correct_answer().split(",");
                for(String str : splitText)
                {
                    if(arrayAnswer.get(i).trim().equals("null"))
                    {
                        isSkip = true;
                        break;
                    }else if (str.trim().equalsIgnoreCase(arrayAnswer.get(i).trim()))
                    {
                        isCor = true;
                        break;
                    }

                }
                if(isCor)
                {
                    totalCorrect++;
                }else if(isSkip) {
                    totalSkip++;
                }
                else{
                    totalWrong++;
                }

            }

        }
        double progress = ((double) totalCorrect / (double) arrayQuestion.size()) * 100;
        DocumentReference reference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").document(quizID);
        Map hashMap = new HashMap<>();
        db.collection("Courses").document(courseID).collection("Heading").document(headingID).collection("quiz").document(quizID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){
                    DocumentReference reference1 = db.collection("Courses").document(courseID).collection("Heading").document(headingID).collection("quiz").document(quizID).collection("history").document();
                    Map hashMap1 = new HashMap<>();
                    hashMap1.put(HISTORY_QUIZ_ID, reference1.getId());
                    if(progress>= Double.parseDouble(task.getResult().getString("quiz_require"))) {
                        hashMap1.put(HISTORY_QUIZ_STATE, true);
                    }else{
                        hashMap1.put(HISTORY_QUIZ_STATE, false);
                    }

                    hashMap1.put(HISTORY_QUIZ_TIME_TAKEN, txtQuizTime.getText().toString());
                    hashMap1.put(HISTORY_QUIZ_SCORE, String.valueOf(progress));
                    hashMap1.put(HISTORY_QUIZ_USER_ID, mAuth.getCurrentUser().getUid());
                    hashMap1.put(HISTORY_QUIZ_UPLOAD, FieldValue.serverTimestamp());
                    reference1.set(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete())
                            {
                            }
                        }
                    });
                }
            }
        });


        if(quizState.compareTo("new") == 0)
        {
            hashMap.put(QUIZ_KEY, quizID);
            hashMap.put(QUIZ_KEY_COURSE_ID, courseID);
            hashMap.put(QUIZ_TOTAL_QUESTION, arrayQuestion.size());
            hashMap.put(QUIZ_BEST_SCORE, String.valueOf(progress));
            hashMap.put(QUIZ_TOTAL_CORRECT, totalCorrect);
            hashMap.put(QUIZ_TOTAL_WRONG, totalWrong);
            hashMap.put(QUIZ_TOTAL_SKIP, totalSkip);
            hashMap.put(QUIZ_STATE, false);
            hashMap.put(QUIZ_TIME_TAKEN, txtQuizTime.getText().toString());
            hashMap.put(QUIZ_UPLOAD_TIME, FieldValue.serverTimestamp());
            reference.set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isComplete())
                    {
                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").document(quizID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isComplete()){
                                    db.collection("Courses").document(courseID).collection("Heading").document(headingID).collection("quiz").document(quizID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                if(progress>= Double.parseDouble(task.getResult().getString("quiz_require"))){
                                                    DocumentReference ref = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").document(task.getResult().getId());
                                                    Map hashMap = new HashMap<>();
                                                    hashMap.put(QUIZ_STATE, true);
                                                    ref.update(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            Toast.makeText(QuizActivity.this, "Kết thúc", Toast.LENGTH_SHORT).show();
//                                                            updateProgressCourse();

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                    alert.closeLoading();
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }
            });
        }else{
            hashMap.put(QUIZ_TOTAL_QUESTION, arrayQuestion.size());
            hashMap.put(QUIZ_TOTAL_CORRECT, totalCorrect);
            hashMap.put(QUIZ_TOTAL_WRONG, totalWrong);
            hashMap.put(QUIZ_TOTAL_SKIP, totalSkip);
            hashMap.put(QUIZ_TIME_TAKEN, txtQuizTime.getText().toString());
            hashMap.put(QUIZ_UPLOAD_TIME, FieldValue.serverTimestamp());
            reference.update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isComplete())
                    {
                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").document(quizID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isComplete()){
                                    DocumentReference ref = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").document(task.getResult().getId());
                                    Map hashMap = new HashMap<>();
                                    double best = Double.parseDouble(task.getResult().getString("quiz_best_score"));
                                    if(progress > best)
                                    {

                                        hashMap.put(QUIZ_BEST_SCORE, String.valueOf(progress));
                                        ref.update(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                alert.closeLoading();
                                                startActivity(intent);
                                                finish();

                                            }
                                        });
                                    }else{
                                        alert.closeLoading();
                                        startActivity(intent);
                                        finish();
                                    }
                                    if(task.getResult().getBoolean("quiz_state") == false)
                                    {
                                        db.collection("Courses").document(courseID).collection("Heading").document(headingID).collection("quiz").document(quizID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    if( progress >= Double.parseDouble(task.getResult().getString("quiz_require"))){
                                                        hashMap.put(QUIZ_STATE, true);
                                                        ref.update(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                Toast.makeText(QuizActivity.this, "Kết thúc", Toast.LENGTH_SHORT).show();
//                                                                updateProgressCourse();
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });

                    }
                }
            });
        }

    }
    private void initArrayAnswer(){
        for(int i = 0;i < arrayQuestion.size(); i++)
        {
            arrayAnswer.add(i,"null");
        }
    }
    private void hide(){
        option1.setVisibility(View.GONE);
        option2.setVisibility(View.GONE);
        option3.setVisibility(View.GONE);
        option4.setVisibility(View.GONE);
        layoutEditAnswer.setVisibility(View.VISIBLE);
    }
    private void show(){
        option1.setVisibility(View.VISIBLE);
        option2.setVisibility(View.VISIBLE);
        option3.setVisibility(View.VISIBLE);
        option4.setVisibility(View.VISIBLE);
        layoutEditAnswer.setVisibility(View.GONE);
    }
    private void zoomInView(View view) {
        view.setScaleX(0);
        view.setScaleY(0);

        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }
    private void updateProgressCourse(){
        Query query = db.collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("CheckQuiz")
                .whereEqualTo("course_id", courseID).whereEqualTo("quiz_state", true);
        AggregateQuery aggregateQuery = query.count();
        aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    totalQuiz = 0;
                    checkedQuiz = 0;
                    AggregateQuerySnapshot snapshot = task.getResult();
                    checkedQuiz = (int) snapshot.getCount();

                    db.collection("Courses").document(courseID)
                            .collection("Heading")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        return;
                                    }
                                    if (value != null && value.size() != 0) {
                                        final int[] i = {0};
                                        for (QueryDocumentSnapshot doc : value) {
                                            Query query1 = db.collection("Courses")
                                                    .document(courseID)
                                                    .collection("Heading")
                                                    .document(doc.getString("heading_id"))
                                                    .collection("quiz");
                                            AggregateQuery aggregateQuery1 = query1.count();
                                            Query query2 = db.collection("Courses").document(courseID).collection("Heading").document(doc.getString("heading_id").toString()).collection("video");
                                            AggregateQuery aggregateQuery2 = query2.count();
                                            aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        AggregateQuerySnapshot snapshot = task.getResult();
                                                        totalQuiz += (int) snapshot.getCount();
                                                        i[0]++;

                                                        if (i[0] == value.size()) {
                                                            if (totalQuiz != 0) {
                                                                db.collection("Users")
                                                                        .document(mAuth.getCurrentUser().getUid())
                                                                        .collection("OwnCourses")
                                                                        .whereEqualTo("own_course_item_id", courseID)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isComplete()) {
                                                                                    for (QueryDocumentSnapshot documentt : task.getResult()) {
                                                                                        aggregateQuery2.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                                                                DocumentReference documentReference = db.collection("Users")
                                                                                                        .document(mAuth.getCurrentUser().getUid())
                                                                                                        .collection("OwnCourses")
                                                                                                        .document(documentt.getString("own_course_id"));

                                                                                                Map<String, Object> map = new HashMap<>();
                                                                                                float temp = ((float) checkedQuiz / totalQuiz) * 100;
                                                                                                String videoProgressStr = documentt.getString("own_course_video_progress");
                                                                                                double videoProgress = (videoProgressStr != null) ? Double.parseDouble(videoProgressStr) : 0.0;
                                                                                                double temp1 = (temp / 2) + (videoProgress / 2);
                                                                                                map.put(KEY_PROGRESS_EX, String.format("%.2f", temp).replace(",", "."));
                                                                                                if(task.getResult().getCount() > 0 ) {
                                                                                                    map.put(KEY_PROGRESS_COURSE, String.format("%.2f", temp1).replace(",", "."));
                                                                                                }else{
                                                                                                    map.put(KEY_PROGRESS_COURSE, String.format("%.2f", temp1*2).replace(",", "."));
                                                                                                }
                                                                                                documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        db.collection("Users")
                                                                                                                .document(mAuth.getCurrentUser().getUid())
                                                                                                                .collection("OwnCourses")
                                                                                                                .document(documentt.getString("own_course_id")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                        if(task.isComplete()){
                                                                                                                            if(temp1 == 100)
                                                                                                                            {
                                                                                                                                DocumentReference documentReference = db.collection("Users")
                                                                                                                                        .document(mAuth.getCurrentUser().getUid())
                                                                                                                                        .collection("OwnCourses")
                                                                                                                                        .document(task.getResult().getId());
                                                                                                                                Map map = new HashMap();
                                                                                                                                map.put(KEY_COMPLETE_COURSE, true);
                                                                                                                                documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                    }
                                                                                                                                });
                                                                                                                            }
                                                                                                                            else{
                                                                                                                                DocumentReference documentReference = db.collection("Users")
                                                                                                                                        .document(mAuth.getCurrentUser().getUid())
                                                                                                                                        .collection("OwnCourses")
                                                                                                                                        .document(task.getResult().getId());
                                                                                                                                Map map = new HashMap();
                                                                                                                                map.put(KEY_COMPLETE_COURSE, false);
                                                                                                                                documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                    }
                                                                                                                                });
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                    }
                                                                                                });
//                                                                                                db.collection("Users")
//                                                                                                        .document(mAuth.getCurrentUser().getUid())
//                                                                                                        .collection("OwnCourses")
//                                                                                                        .document(documentt.getString("own_course_id")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                                                                                            @Override
//                                                                                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                                                                                                                if(error != null) return;
//                                                                                                                if(value.exists() && value != null){
//                                                                                                                    if(value.getString("own_course_progress").equals("100.00"))
//                                                                                                                    {
//                                                                                                                        DocumentReference documentReference = db.collection("Users")
//                                                                                                                                .document(mAuth.getCurrentUser().getUid())
//                                                                                                                                .collection("OwnCourses")
//                                                                                                                                .document(value.getId());
//                                                                                                                        Map map = new HashMap();
//                                                                                                                        map.put(KEY_COMPLETE_COURSE, true);
//                                                                                                                        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                            @Override
//                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                                                                                            }
//                                                                                                                        });
////                                                                                                                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////                                                                                                                            @Override
////                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                                                                                                                                if(task.isComplete()){
////                                                                                                                                    long currentPoints = task.getResult().getLong("user_points");
////                                                                                                                                    long newPoints = currentPoints + 20;
////                                                                                                                                    DocumentReference documentReference1 = db.collection("Users")
////                                                                                                                                            .document(mAuth.getCurrentUser().getUid());
////                                                                                                                                    Map map = new HashMap();
////                                                                                                                                    map.put(KEY_USER_POINTS, newPoints);
////                                                                                                                                    documentReference1.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                                                                                                        @Override
////                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
////                                                                                                                                        }
////                                                                                                                                    });
////                                                                                                                                }
////                                                                                                                            }
////                                                                                                                        });
//                                                                                                                    }
//                                                                                                                }
//                                                                                                            }
//                                                                                                        });


                                                                                            }
                                                                                        });

                                                                                    }
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
    public void callBot(String text){
        previous.setEnabled(false);
        next.setEnabled(false);
        imgBackQuiz.setEnabled(false);
        txtExplainWithBot.animateText("Bot đang suy nghĩ....");
        GenerativeModel gm =
                new GenerativeModel("gemini-1.5-flash-8b-exp-0924", BuildConfig.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content =
                new Content.Builder().addText(text + "Không dùng các thẻ HTML khi trả lời, ghi bình thường là được rồi").build();
            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
            Futures.addCallback(
                    response,
                    new FutureCallback<GenerateContentResponse>() {
                        @Override
                        public void onSuccess(GenerateContentResponse result) {
                            txtExplainWithBot.setText("");
                            String resultText = result.getText();
                            txtExplainWithBot.animateText(resultText);
                            previous.setEnabled(true);
                            next.setEnabled(true);
                            imgBackQuiz.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                            txtExplainWithBot.animateText("Lượt sử dụng AI Bot có giới hạn, cảm ơn bạn, hẹn gặp lại !");
                        }
                    },
                    this.getMainExecutor());

    }
    public void onClick(View view) {
        AppCompatButton clickedBtn = (AppCompatButton) view;
        int color = ContextCompat.getColor(this, R.color.gray);
        int color2 = ContextCompat.getColor(this, R.color.orange);
        ColorStateList colorStateList;
        colorStateList= ColorStateList.valueOf(color);
        option1.setBackgroundTintList(colorStateList);
        option2.setBackgroundTintList(colorStateList);
        option3.setBackgroundTintList(colorStateList);
        option4.setBackgroundTintList(colorStateList);
        if(quizState.compareTo("new") ==0 || quizState.compareTo("exist") ==0)
        {
            if (clickedBtn.getId() == R.id.btn6) {
                if(arrayQuestion.get(currentQuestionIndex).getQuestion_type().equals("text"))
                {
                    selectedAnswer = editAnswer.getText().toString().trim();
                }
                if (selectedAnswer.isEmpty()) {
                    arrayAnswer.remove(currentQuestionIndex);
                    arrayAnswer.add(currentQuestionIndex, "null");
                }else{
                    arrayAnswer.remove(currentQuestionIndex);
                    arrayAnswer.add(currentQuestionIndex, selectedAnswer);
                }
                currentQuestionIndex++;
                loadDiscussions();
            }else if (clickedBtn.getId() == R.id.btn5) {
                if (selectedAnswer.isEmpty()) {
                    arrayAnswer.remove(currentQuestionIndex);
                    arrayAnswer.add(currentQuestionIndex, "null");
                }else{
                    arrayAnswer.remove(currentQuestionIndex);
                    arrayAnswer.add(currentQuestionIndex, selectedAnswer);
                }
                currentQuestionIndex--;
                loadDiscussions();

            }
            else {
                selectedAnswer = clickedBtn.getText().toString();
                colorStateList = ColorStateList.valueOf(color2);
                clickedBtn.setBackgroundTintList(colorStateList);

            }
        }
        else{

            if (clickedBtn.getId() == R.id.btn6) {
                currentQuestionIndex++;
                loadDiscussions();
            }else if (clickedBtn.getId() == R.id.btn5) {
                currentQuestionIndex--;
                loadDiscussions();

            }
        }

    }
    private void startTimer() {
        long totalTimeInMillis = Integer.parseInt(quizTime) * 60 * 1000L;

        new CountDownTimer(totalTimeInMillis, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long remainingSeconds = seconds % 60;
                txtQuizTime.setText(String.format("%02d:%02d", minutes, remainingSeconds));
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
    private void mapping(){
        txtToolbar = (TextView) findViewById(R.id.txtToolbarQuizTitle);
        txtQuestionCount = (TextView) findViewById(R.id.question_indicator_textview);
        txtQuizTime = (TextView) findViewById(R.id.timer_indicator_textview);
        txtQuestion = (TextView) findViewById(R.id.question_textview);
        txtAnswer = (TextView) findViewById(R.id.txtAnswer);
        txtExplain = (TextView) findViewById(R.id.txtExplain);
        txtExplainWithBot = (TypewriterTextView) findViewById(R.id.txtExplainWithBot);

        option1 = (AppCompatButton) findViewById(R.id.btn1);
        option2 = (AppCompatButton) findViewById(R.id.btn2);
        option3 = (AppCompatButton) findViewById(R.id.btn3);
        option4 = (AppCompatButton) findViewById(R.id.btn4);
        previous = (AppCompatButton) findViewById(R.id.btn5);
        next = (AppCompatButton) findViewById(R.id.btn6);

        editAnswer = (TextInputEditText) findViewById(R.id.editTextInputAnswer);
        layoutEditAnswer = (TextInputLayout) findViewById(R.id.textInputLayoutAnswer);

        linearAnswer = (LinearLayout) findViewById(R.id.linearAnswer);
        botBtn= (LinearLayout) findViewById(R.id.btn_bot);

        progressIndicator = (LinearProgressIndicator) findViewById(R.id.question_progress_indicator);

        imgBackQuiz = (ImageView) findViewById(R.id.imgBackQuiz);
        icon_timer = (ImageView) findViewById(R.id.icon_timer);
        icon_bot = (ImageView) findViewById(R.id.icon_bot);
        imageExplain = (ImageView) findViewById(R.id.imageExplain);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
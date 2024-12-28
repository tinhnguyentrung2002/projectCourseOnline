package com.example.courseonline.Activity.Learner;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterGradeActivity extends AppCompatActivity {

    Button btnNextGrade;
    Toast toast;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int clickedID;
    private boolean isClicked = false;
    LinearLayout optionContainer;
    ConstraintLayout constraintLayout_1, constraintLayout_2, constraintLayout_3;
//    RadioButton radioButton_1, radioButton_2, radioButton_3;
//    TextView textView_1, textView_2, textView_3, textView_4, textView_5, textView_6;
    private String state;
    private static final String KEY_GRADE = "user_grade";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_grade);
        mapping();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null)
        {
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().getString("user_grade").contains("Lớp 10"))
                        {
//                            radioButton_1.callOnClick();
                            constraintLayout_1.callOnClick();
                            isClicked = true;
                            clickedID = constraintLayout_1.getId();
                        }
                        else if(task.getResult().getString("user_grade").contains("Lớp 11")){
                            constraintLayout_2.callOnClick();
                            isClicked = true;
                            clickedID = constraintLayout_2.getId();
                        }
                        else if(task.getResult().getString("user_grade").contains("Lớp 12")){
                            constraintLayout_3.callOnClick();
                            isClicked = true;
                            clickedID = constraintLayout_3.getId();
                        }
                    }
                }
            });
        }
//        radioButton_1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.orange));
//                textView_1.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_2.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_3.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_4.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_5.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_6.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                radioButton_1.setChecked(true);
//                radioButton_2.setChecked(false);
//                radioButton_3.setChecked(false);
//            }
//        });
//        radioButton_2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.orange));
//                textView_3.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_4.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_1.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_2.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_5.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_6.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                radioButton_1.setChecked(false);
//                radioButton_2.setChecked(true);
//                radioButton_3.setChecked(false);
//            }
//        });
//        radioButton_3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.orange));
//                textView_5.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_6.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_1.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_2.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_3.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_4.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                radioButton_1.setChecked(false);
//                radioButton_2.setChecked(false);
//                radioButton_3.setChecked(true);
//            }
//        });
        constraintLayout_1.setOnClickListener(this::onClickConstraint);
        constraintLayout_2.setOnClickListener(this::onClickConstraint);
        constraintLayout_3.setOnClickListener(this::onClickConstraint);
//        radioButton_1.setOnClickListener(this::onClickRadio);
//        radioButton_2.setOnClickListener(this::onClickRadio);
//        radioButton_3.setOnClickListener(this::onClickRadio);
//        constraintLayout_1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.orange));
//                textView_1.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_2.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_3.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_4.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_5.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_6.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                radioButton_1.setChecked(true);
//                radioButton_2.setChecked(false);
//                radioButton_3.setChecked(false);
//            }
//        });
//        constraintLayout_2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.orange));
//                textView_3.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_4.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_1.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_2.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_5.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_6.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                radioButton_1.setChecked(false);
//                radioButton_2.setChecked(true);
//                radioButton_3.setChecked(false);
//            }
//        });
//        constraintLayout_3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.orange));
//                textView_5.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_6.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_1.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_2.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//                textView_3.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                textView_4.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//                radioButton_1.setChecked(false);
//                radioButton_2.setChecked(false);
//                radioButton_3.setChecked(true);
//            }
//        });
        btnNextGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isClicked == false)
                {
                    toastMes("Vui lòng chọn một đối tượng");
                }
                else{
                    if(constraintLayout_1.getId() == clickedID)
                    {
                        state = "1";
                        updateGrade();
                        finish();
                    }
                    else if(constraintLayout_2.getId() == clickedID)
                    {
                        state = "2";
                        updateGrade();
                        finish();
                    }
                    else{
                        state = "3";
                        updateGrade();
                        finish();
                    }
                }
            }
        });
    }
//    public void onClickRadio(View view) {
//        radioButton_1.setChecked(false);
//        radioButton_2.setChecked(false);
//        radioButton_3.setChecked(false);
//
//
//        RadioButton clickedRadio = (RadioButton) view;
//        clickedRadio.setChecked(true);
//
//    }
//    public void onClickConstraint(View view) {
//        constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//        constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//        constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//        radioButton_1.setChecked(false);
//        radioButton_2.setChecked(false);
//        radioButton_3.setChecked(false);
//        textView_1.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//        textView_2.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//        textView_3.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//        textView_4.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//        textView_5.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//        textView_6.setTextColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.black));
//
//        ConstraintLayout clickedBtn = (ConstraintLayout) view;
//        TextView clickedText = (TextView) view;
//        RadioButton clickedRadio = (RadioButton) view;
//        clickedBtn.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.orange));
//        clickedText.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
//        clickedRadio.setChecked(true);
//
//    }
    public void onClickConstraint(View view) {
        constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
        constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));
        constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.white));

        ConstraintLayout clickedBtn = (ConstraintLayout) view;
        clickedBtn.setBackgroundColor(ContextCompat.getColor(RegisterGradeActivity.this, R.color.light_pink));
        clickedID = clickedBtn.getId();
        isClicked = true;


    }
    private void updateGrade(){
        if(mAuth.getCurrentUser() != null)
        {
            DocumentReference ref= db.collection("Users").document(mAuth.getCurrentUser().getUid());
            Map map = new HashMap<>();
            if(state == "1")
            {
                map.put(KEY_GRADE, "Lớp 10");
            }else if(state == "2")
            {
                map.put(KEY_GRADE, "Lớp 11");
            }else{
                map.put(KEY_GRADE, "Lớp 12");
            }
            ref.update(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    toastMes("Đã cập nhật lớp");
                }
            });
        }
    }
    private void mapping(){
        btnNextGrade = (Button) findViewById(R.id.buttonNext_grade);
        constraintLayout_1 = (ConstraintLayout) findViewById(R.id.constraintLayout_1);
        constraintLayout_2 = (ConstraintLayout) findViewById(R.id.constraintLayout_2);
        constraintLayout_3 = (ConstraintLayout) findViewById(R.id.constraintLayout_3);
        optionContainer = (LinearLayout) findViewById(R.id.optionContainer);
//        radioButton_1 = (RadioButton) findViewById(R.id.radioButton);
//        radioButton_2 = (RadioButton) findViewById(R.id.radioButton1);
//        radioButton_3 = (RadioButton) findViewById(R.id.radioButton2);
//        textView_1 = (TextView) findViewById(R.id.textView);
//        textView_2 = (TextView) findViewById(R.id.textView2);
//        textView_3 = (TextView) findViewById(R.id.textView3);
//        textView_4 = (TextView) findViewById(R.id.textView4);
//        textView_5 = (TextView) findViewById(R.id.textView5);
//        textView_6 = (TextView) findViewById(R.id.textView6);
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(RegisterGradeActivity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
}
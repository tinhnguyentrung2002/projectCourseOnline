package com.example.courseonline.Activity.Learner;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
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

public class RegisterJobActivity extends AppCompatActivity {

    Button btnNextJob;
    Toast toast;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    ConstraintLayout constraintLayout_1, constraintLayout_2, constraintLayout_3;
    RadioButton radioButton_1, radioButton_2, radioButton_3;
    TextView textView_1, textView_2, textView_3, textView_4, textView_5, textView_6;
    private String state_job;
    private static final String KEY_JOB = "user_job";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_job);
        mapping();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null)
        {
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().getString("user_job").contains("Học sinh"))
                        {
                            radioButton_1.callOnClick();
                        }
                        else if(task.getResult().getString("user_job").contains("Sinh viên")){
                            radioButton_2.callOnClick();
                        }
                        else if(task.getResult().getString("user_job").contains("Đi làm")){
                            radioButton_3.callOnClick();
                        }else{

                        }
                    }
                }
            });
        }
        radioButton_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.orange));
                textView_1.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_2.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_3.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_4.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_5.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_6.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                radioButton_1.setChecked(true);
                radioButton_2.setChecked(false);
                radioButton_3.setChecked(false);
            }
        });
        radioButton_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.orange));
                textView_3.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_4.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_1.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_2.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_5.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_6.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                radioButton_1.setChecked(false);
                radioButton_2.setChecked(true);
                radioButton_3.setChecked(false);
            }
        });
        radioButton_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.orange));
                textView_5.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_6.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_1.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_2.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_3.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_4.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                radioButton_1.setChecked(false);
                radioButton_2.setChecked(false);
                radioButton_3.setChecked(true);
            }
        });
        constraintLayout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.orange));
                textView_1.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_2.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_3.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_4.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_5.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_6.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                radioButton_1.setChecked(true);
                radioButton_2.setChecked(false);
                radioButton_3.setChecked(false);
            }
        });
        constraintLayout_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.orange));
                textView_3.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_4.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_1.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_2.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_5.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_6.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                radioButton_1.setChecked(false);
                radioButton_2.setChecked(true);
                radioButton_3.setChecked(false);
            }
        });
        constraintLayout_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintLayout_3.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.orange));
                textView_5.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_6.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                constraintLayout_1.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_1.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_2.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                constraintLayout_2.setBackgroundColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.white));
                textView_3.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                textView_4.setTextColor(ContextCompat.getColor(RegisterJobActivity.this, R.color.black));
                radioButton_1.setChecked(false);
                radioButton_2.setChecked(false);
                radioButton_3.setChecked(true);
            }
        });
        btnNextJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!radioButton_1.isChecked() && !radioButton_2.isChecked() && !radioButton_3.isChecked())
                {
                    toastMes("Vui lòng chọn một đối tượng");
                }
                else{
                    if(radioButton_1.isChecked())
                    {
                        state_job = "1";
                        updateJob();
                        finish();
                    }
                    else if(radioButton_2.isChecked())
                    {
                        state_job = "2";
                        updateJob();
                        finish();
                    }
                    else{
                        state_job = "3";
                        updateJob();
                        finish();
                    }
                }
            }
        });
    }
    private void updateJob(){
        if(mAuth.getCurrentUser() != null)
        {
            DocumentReference ref= db.collection("Users").document(mAuth.getCurrentUser().getUid());
            Map map = new HashMap<>();
            if(state_job == "1")
            {
                map.put(KEY_JOB, "Học sinh");
            }else if(state_job == "2")
            {
                map.put(KEY_JOB, "Sinh viên");
            }else{
                map.put(KEY_JOB, "Đi làm");
            }
            ref.update(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    toastMes("Đã cập nhật thể loại");
                }
            });
        }
    }
    private void mapping(){
        btnNextJob = (Button) findViewById(R.id.buttonNext_job);
        constraintLayout_1 = (ConstraintLayout) findViewById(R.id.constraintLayout_1);
        constraintLayout_2 = (ConstraintLayout) findViewById(R.id.constraintLayout_2);
        constraintLayout_3 = (ConstraintLayout) findViewById(R.id.constraintLayout_3);
        radioButton_1 = (RadioButton) findViewById(R.id.radioButton);
        radioButton_2 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton_3 = (RadioButton) findViewById(R.id.radioButton2);
        textView_1 = (TextView) findViewById(R.id.textView);
        textView_2 = (TextView) findViewById(R.id.textView2);
        textView_3 = (TextView) findViewById(R.id.textView3);
        textView_4 = (TextView) findViewById(R.id.textView4);
        textView_5 = (TextView) findViewById(R.id.textView5);
        textView_6 = (TextView) findViewById(R.id.textView6);
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(RegisterJobActivity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
}
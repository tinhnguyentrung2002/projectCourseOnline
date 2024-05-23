package com.example.courseonline.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.courseonline.Activity.Learner.RegisterMainActivity;
import com.example.courseonline.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class IntroActivity extends AppCompatActivity {

    Button btnNext;
    TextView txtRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mapping();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroActivity.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroActivity.this, RegisterMainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
    private void mapping(){
        btnNext = (Button) findViewById(R.id.buttonNext);
        txtRegister = (TextView) findViewById(R.id.txtRegister);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
//        if(mAuth.getCurrentUser() != null)
//        {
//            if(mAuth.getCurrentUser() != null) {
//                db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//
//                            if (task.getResult().getString("user_permission").contains("2")) {
//                                Intent intent = new Intent(IntroActivity.this, DashBoardTeacherActivity.class);
//                                startActivity(intent);
//                            }
//                            else{
//                                Intent intent = new Intent(IntroActivity.this, DashboardActivity.class);
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                            }
//                        }
//                    }
//                });
//            }
//        }
    }
}
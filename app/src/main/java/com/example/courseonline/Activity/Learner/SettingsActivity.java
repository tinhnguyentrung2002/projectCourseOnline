package com.example.courseonline.Activity.Learner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.courseonline.Activity.IntroActivity;
import com.example.courseonline.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {

    ConstraintLayout constraintLogout, constraintDeleteAcc, constraintAlarm, constraintUpdateGrade, constraintPaymentHistory;
    AppCompatImageButton btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mapping();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        constraintAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this,AlarmManager.class));
            }
        });
        constraintPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, PaymentHistoryActivity.class);
                startActivity(intent);
            }
        });
        constraintLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              showPopup();
            }
        });
        constraintDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup2();
            }
        });
        constraintUpdateGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, RegisterGradeActivity.class);
                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void mapping(){
        constraintLogout = (ConstraintLayout) findViewById(R.id.constraintLogout);
        constraintDeleteAcc = (ConstraintLayout) findViewById(R.id.constraintDeleteAcc);
        constraintUpdateGrade = (ConstraintLayout) findViewById(R.id.constraintUpdateGrade);
        constraintAlarm = (ConstraintLayout) findViewById(R.id.constraintAlarm);
        constraintPaymentHistory = (ConstraintLayout) findViewById(R.id.constraintPaymentHistory);
        btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);

    }
    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
        alert.setMessage("Bạn có chắc chắn muốn đăng xuất ?")
                .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener()                 {

                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        finishAffinity();
                        Intent intent = new Intent(SettingsActivity.this, IntroActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Huỷ", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }
    private void showPopup2() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        String uid = mAuth.getCurrentUser().getUid();
//        AuthCredential credential = EmailAuthProvider
//                .getCredential("user@example.com", "password1234");
        AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
        alert.setMessage("Tất cả thông tin liên quan đến tài khoản này sẽ bị xoá toàn bộ, bạn có chắc chắn xoá ?")
                .setPositiveButton("Xoá", new DialogInterface.OnClickListener()                 {
                    public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SettingsActivity.this, "Chức năng này tạm thời không hoạt động", Toast.LENGTH_SHORT).show();
//                        mAuth.signOut();
//                        db.collection("Users").document(uid).delete();
//                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            mAuth.signOut();
//                                            Toast.makeText(SettingsActivity.this, "Xoá thành công",
//                                                    Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent(SettingsActivity.this, IntroActivity.class);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                            startActivity(intent);
//
//
//                                        }
//                                    }
//                                });

                    }
                }).setNegativeButton("Huỷ", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }
    private void userStatus(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){

        }
        else{

        }
    }

    @Override
    protected void onStart() {
        userStatus();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
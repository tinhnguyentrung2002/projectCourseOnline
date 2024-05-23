package com.example.courseonline.Activity.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.courseonline.Activity.LoginActivity;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterMainActivity extends AppCompatActivity {

    private static final String KEY_NAME = "user_name";
    private static final String KEY_UID = "user_uid";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_AVATAR = "user_avatar";
    private static final String KEY_PERMISSION = "user_permission";
    //private static final String KEY_USER_JOB = "user_job";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private Toast toast;
    private ArrayList<String> arrayList = new ArrayList<>();
    Button btnSignup;
    EditText editText1;
    TextInputEditText editText2, editText3;
    TextView txtLogin;
    FloatingActionButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_main);
        mapping();


        //state_job = getIntent().getStringExtra("state_job");
        mAuth = FirebaseAuth.getInstance();

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterMainActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editText1.getText().toString().trim();
                String password = editText2.getText().toString().trim();
                String rePassword = editText3.getText().toString().trim();
                //Toast.makeText(RegisterMainActivity.this, editText.getText().toString() + editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString(),Toast.LENGTH_SHORT).show();
                if(email.isEmpty() || password.isEmpty() || rePassword.isEmpty())
                {
                    toastMes("Vui lòng nhập đầy đủ thông tin");
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    editText1.setError("Email không phù hợp");
                }
                else if(password.length() < 8)
                {
                    editText2.setError("Mật khẩu phải từ 8 kí tự trở lên");
                }
                else if(!password.equals(rePassword))
                {
                    editText3.setError("Mật khẩu nhập lại không khớp");
                }else{
                    dangKy(email,password);
                }
            }
        });
    }
    private void mapping()
    {
        btnBack = (FloatingActionButton) findViewById(R.id.actionBack);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        editText1 = (EditText) findViewById(R.id.editTextEmail);
        editText2 = (TextInputEditText) findViewById(R.id.editTextPassword);
        editText3 = (TextInputEditText) findViewById(R.id.editTextPassword2);
        txtLogin = (TextView) findViewById(R.id.textLogin);
    }
    private void dangKy(String email, String password){
        LoadingAlert loadingAlert = new LoadingAlert(RegisterMainActivity.this);
        loadingAlert.startLoading();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String uid = user.getUid();


                            Map<Object, String> hashMap = new HashMap<>();
                            hashMap.put(KEY_EMAIL, email);
                            hashMap.put(KEY_UID, uid);
                            hashMap.put(KEY_NAME, email);
                            hashMap.put(KEY_AVATAR, "");
                            hashMap.put(KEY_PERMISSION,"1");
                           // hashMap.put(KEY_USER_JOB, "");
                            DocumentReference reference = db.collection("Users").document(uid);
                            reference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loadingAlert.closeLoading();
                                    startActivity(new Intent(RegisterMainActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });

                        } else {
                            loadingAlert.closeLoading();
                            toastMes("Tài khoản này đã được sử dụng !");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingAlert.closeLoading();
                    }
                });

    }
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(RegisterMainActivity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
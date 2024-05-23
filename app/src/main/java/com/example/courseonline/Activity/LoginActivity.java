package com.example.courseonline.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.courseonline.Activity.Learner.DashboardActivity;
import com.example.courseonline.Activity.Learner.RegisterMainActivity;
import com.example.courseonline.Activity.Teacher.DashBoardTeacherActivity;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toast toast;
    private static final String KEY_NAME = "user_name";
    private static final String KEY_UID = "user_uid";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_AVATAR = "user_avatar";
    private static final String KEY_PERMISSION = "user_permission";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> arrayList = new ArrayList<>();
    Button btnSignin;
    SignInButton btnGoogle;
    TextInputEditText editEmail, editPass;
    FloatingActionButton  btnBack;
    GoogleSignInClient mgoogleSignInClient;
    TextView txtRegister, txtForgotPassword, txtError;
    private int countLoginFail = 0;

    private static final int RC_SIGN_IN = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mapping();
        txtError.setText("");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("143441589699-4bddsl7e09vclkchd7033ptd0a09ehu5.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mgoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this,gso);
        mgoogleSignInClient.signOut();
        mAuth = FirebaseAuth.getInstance();
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterMainActivity.class));
            }
        });
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPasswordDialog();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString().trim();
                String password = editPass.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    editEmail.setError("Email không phù hợp");
                    editEmail.setFocusable(true);
                }
                else{
                    if(!editPass.getText().toString().trim().isEmpty())
                    {
                        dangNhap(email,password);
                    }
                    else{
                        txtError.setText("Nhập thiếu thông tin");
                    }

                }
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }
    private void mapping(){
        btnSignin = (Button) findViewById(R.id.btnSignin);
        btnBack = (FloatingActionButton) findViewById(R.id.actionBack_signin);
        txtRegister = (TextView) findViewById(R.id.txtRegister_login);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgot);
        txtError = (TextView) findViewById(R.id.textError);
        editEmail = (TextInputEditText) findViewById(R.id.editTextUserName);
        editPass = (TextInputEditText) findViewById(R.id.editTextPassword);
        btnGoogle = (SignInButton) findViewById(R.id.signinGoogle);
    }
    private void googleSignIn(){
        Intent intent = mgoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == RC_SIGN_IN)
            {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuth(account.getIdToken());
                }catch (ApiException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

    }
    private void dangNhap(String email, String password){
        LoadingAlert loadingAlert = new LoadingAlert(LoginActivity.this);
        loadingAlert.startLoading();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(mAuth.getCurrentUser().isEmailVerified())
                            {
                                if(mAuth.getCurrentUser() != null) {
                                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                if (task.getResult().getString("user_permission").contains("2")) {
                                                    Intent intent = new Intent(LoginActivity.this, DashBoardTeacherActivity.class);
                                                    startActivity(intent);
                                                    loadingAlert.closeLoading();
                                                    finish();
                                                }
                                                else{
                                                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                    loadingAlert.closeLoading();
                                                    finish();
                                                }
                                            }
                                        }
                                    });}
                            }
                            else{
                                loadingAlert.closeLoading();
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loadingAlert.closeLoading();
                                        toastMes("Đã gửi link xác thực tới email, hãy xác thực để đăng nhập !");
                                    }
                                });
                            }


                        } else {
                            loadingAlert.closeLoading();
                            countLoginFail++;
                            txtError.setText("Sai tài khoản hoặc mật khẩu! Sai " + countLoginFail +" lần");
                            editPass.getText().clear();

                            if (countLoginFail >= 5) {
                                toastMes("Vui lòng đăng nhập lại sau 1 phút !");
                                btnSignin.setEnabled(false);
                                editPass.getText().clear();
                                editEmail.getText().clear();
                                editEmail.setFocusable(true);
                                new CountDownTimer(60000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        btnSignin.setText("Thử lại sau " + millisUntilFinished / 1000 + " giây");
                                    }

                                    @Override
                                    public void onFinish() {
                                        btnSignin.setEnabled(true);
                                        btnSignin.setText("Đăng nhập");
                                        countLoginFail = 0;
                                    }
                                }.start();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingAlert.closeLoading();
                    }
                });
    }
    private void firebaseAuth(String idToken){
        LoadingAlert loadingAlert = new LoadingAlert(LoginActivity.this);
        loadingAlert.startLoading();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    if (task.getResult().getAdditionalUserInfo().isNewUser())
                    {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String email = user.getEmail();
                        String uid = user.getUid();
                        String name = user.getDisplayName();
                        Uri avatar = user.getPhotoUrl();
                        Map<Object, String> hashMap = new HashMap<>();
                        hashMap.put(KEY_EMAIL, email);
                        hashMap.put(KEY_UID, uid);
                        hashMap.put(KEY_NAME, name);
                        hashMap.put(KEY_AVATAR, avatar.toString());
                        hashMap.put(KEY_PERMISSION,"1");
                       // hashMap.put(KEY_USER_JOB, "");
                        DocumentReference reference = db.collection("Users").document(uid);
                        reference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                                loadingAlert.closeLoading();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingAlert.closeLoading();
                                txtError.setText("Lỗi!");
                            }
                        });
                    }
                    else{
                        if(mAuth.getCurrentUser() != null) {
                            db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        if (task.getResult().getString("user_permission").contains("2")) {
                                            Intent intent = new Intent(LoginActivity.this, DashBoardTeacherActivity.class);
                                            startActivity(intent);
                                            loadingAlert.closeLoading();
                                            finish();
                                        }
                                        else{
                                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            loadingAlert.closeLoading();
                                            finish();
                                        }
                                    }
                                }
                            });}
                    }
                }
                else{
                    loadingAlert.closeLoading();
                    txtError.setText("Lỗi!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingAlert.closeLoading();
                txtError.setText("Lỗi!");
            }
        });
    }
    private void resetPasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Đặt lại mật khẩu");
        LinearLayout layout = new LinearLayout(LoginActivity.this);
        EditText editTextEmailReset = new EditText(LoginActivity.this);
        editTextEmailReset.setHint("Email của bạn");
        editTextEmailReset.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editTextEmailReset.setBackgroundResource(R.drawable.edit_login);
        editTextEmailReset.setMinEms(16);
        editTextEmailReset.setPadding(25,20,20,20);
        layout.addView(editTextEmailReset);
        layout.setPadding(15,15,15,15);
        builder.setView(layout);
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = editTextEmailReset.getText().toString().trim();
                startReset(email);
            }
        });
        builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
    private void startReset(String email)
    {
        LoadingAlert loadingAlert = new LoadingAlert(LoginActivity.this);
        loadingAlert.startLoading();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingAlert.closeLoading();
                if(task.isSuccessful())
                {
                    toastMes("Đã gửi liên kết đến email");
                }
                else{
                    toastMes("Gửi thất bại");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingAlert.closeLoading();
                Toast.makeText(LoginActivity.this,"" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(LoginActivity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }

}
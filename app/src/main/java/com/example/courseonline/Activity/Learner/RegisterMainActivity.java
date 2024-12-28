package com.example.courseonline.Activity.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterMainActivity extends AppCompatActivity {

    private static final String KEY_NAME = "user_name";
    private static final String KEY_UID = "user_uid";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_AVATAR = "user_avatar";
    private static final String KEY_LEVEL = "user_level";
    private static final String KEY_POINTS = "user_points";
    private static final String KEY_BEST_POINTS = "user_best_points";
    private static final String KEY_PERMISSION = "user_permission";
    private static final String KEY_USER_GRADE = "user_grade";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private Toast toast;
    private boolean  emailCheck, passCheck, repassCheck = false;
    Button btnSignup;
    EditText editText1;
    TextInputLayout textInputLayout1, textInputLayout2, textInputLayout3;
    TextInputEditText editText2, editText3;
    TextView txtLogin;
    ImageButton btnBack;
    List<String> timeList = new ArrayList<>();
    private static final  String KEY_ALARM_ACTIVE = "alarm_active";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_main);
        mapping();

        //state_job = getIntent().getStringExtra("state_job");
        mAuth = FirebaseAuth.getInstance();
        textInputLayout1.setErrorEnabled(false);
        textInputLayout2.setErrorEnabled(false);
        textInputLayout3.setErrorEnabled(false);
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
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = editText1.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailCheck = false;
//                    editText1.setError("Email không phù hợp");
                    textInputLayout1.setErrorEnabled(true);
                    textInputLayout1.setError("Email không phù hợp");

                }else {
                    textInputLayout1.setError(null);
                    textInputLayout1.setErrorEnabled(false);
                    emailCheck  = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = editText2.getText().toString().trim();
                if(password.length() < 8)
                {
                    passCheck = false;
//                  editText2.setError("Mật khẩu phải từ 8 kí tự trở lên");
                    textInputLayout2.setErrorEnabled(true);
                    textInputLayout2.setError("Mật khẩu phải từ 8 kí tự trở lên");
                }else {
                    textInputLayout2.setError(null);
                    textInputLayout2.setErrorEnabled(false);
                    passCheck = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String rePassword = editText3.getText().toString().trim();
                String password = editText2.getText().toString().trim();
                if(!password.equals(rePassword))
                {
                    repassCheck = false;
//                  editText3.setError("Mật khẩu nhập lại không khớp");
                    textInputLayout3.setErrorEnabled(true);
                    textInputLayout3.setError("Mật khẩu nhập lại không khớp");
                }else {
                    textInputLayout3.setError(null);
                    textInputLayout3.setErrorEnabled(false);
                    repassCheck = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editText1.getText().toString().trim();
                String password = editText2.getText().toString().trim();
                String rePassword = editText3.getText().toString().trim();
                if(email.isEmpty()) textInputLayout1.setError("Không được bỏ trống");
                if(password.isEmpty()) textInputLayout2.setError("Không được bỏ trống");
                if (rePassword.isEmpty()) textInputLayout3.setError("Không được bỏ trống");
//              Toast.makeText(RegisterMainActivity.this, editText.getText().toString() + editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString(),Toast.LENGTH_SHORT).show();
//              if(email.isEmpty() || password.isEmpty() || rePassword.isEmpty())
//              {
//
//              }
                if(emailCheck && passCheck && repassCheck)
                {
//                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
//                a{
//                    editText1.setError("Email không phù hợp");
//                    textInputLayout1.setError("Email không phù hợp");
//                }
//                else if(password.length() < 8)
//                {
//                    editText2.setError("Mật khẩu phải từ 8 kí tự trở lên");
//                    textInputLayout2.setError("Mật khẩu phải từ 8 kí tự trở lên");
//                }
//                else if(!password.equals(rePassword))
//                {
//                    editText3.setError("Mật khẩu nhập lại không khớp");
//                    textInputLayout3.setError("Mật khẩu nhập lại không khớp");
//                }
                        dangKy(email,password);
                }
                else{
                    toastMes("Thông tin không hợp lệ");
                }
            }
        });
    }
    private void mapping()
    {
        btnBack = (ImageButton) findViewById(R.id.actionBack);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        textInputLayout1 = (TextInputLayout) findViewById(R.id.editTextText1);
        textInputLayout2 = (TextInputLayout) findViewById(R.id.editTextText2);
        textInputLayout3 = (TextInputLayout) findViewById(R.id.editTextText3);
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


                            Map hashMap = new HashMap<>();
                            hashMap.put(KEY_EMAIL, email);
                            hashMap.put(KEY_UID, uid);
                            hashMap.put(KEY_NAME, email);
                            hashMap.put(KEY_AVATAR, "");
                            hashMap.put(KEY_LEVEL, "1");
                            hashMap.put(KEY_POINTS, 0);
                            hashMap.put(KEY_BEST_POINTS, 0);
                            hashMap.put(KEY_PERMISSION,"1");
                            hashMap.put(KEY_USER_GRADE, "");
                            DocumentReference reference = db.collection("Users").document(uid);

                            reference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    addAlarm(uid);
//                                    Intent intent = new Intent(RegisterMainActivity.this, LoginActivity.class);
                                    mAuth.signOut();
                                    toastMes("Đăng ký thành công !");
                                    loadingAlert.closeLoading();
//                                    startActivity(intent);
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
    public void addAlarm(String id){
        timeList.add("07:00");
        timeList.add("08:00");
        timeList.add("09:00");
        timeList.add("10:00");
        timeList.add("11:00");
        timeList.add("12:00");
        timeList.add("13:00");
        timeList.add("14:00");
        timeList.add("15:00");
        timeList.add("16:00");
        timeList.add("17:00");
        timeList.add("18:00");
        timeList.add("19:00");
        timeList.add("20:00");
        timeList.add("21:00");
        timeList.add("22:00");
        timeList.add("23:00");
        for (String time : timeList) {
            DocumentReference reference1 = db.collection("Users").document(id).collection("Alarm").document(time);
            Map map = new HashMap();
            map.put(KEY_ALARM_ACTIVE, false);
            reference1.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
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
        finish();
        super.onBackPressed();
    }
}
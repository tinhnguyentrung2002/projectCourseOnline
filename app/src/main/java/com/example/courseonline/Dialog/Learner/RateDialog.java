package com.example.courseonline.Dialog.Learner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateField;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RateDialog extends Dialog {
    private String comment_id;
    private String currentContent;
    private float currentRate;
    private AppCompatButton laterBtn, submitBtn;
    private TextInputEditText editComment;
    private ImageView rateImage;
    private TextView txtTitleRate;
    private RatingBar ratingBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String course_id;
    private Toast toast;
    private static final String KEY_COMMENT_ID = "comment_id";
    private static final String KEY_COURSE_RATE = "course_rate";
    private static final String KEY_COURSE_ID ="course_id";
    private static final String KEY_COMMENT_CONTENT = "comment_content";
    private static final String KEY_COMMENT_LIKE = "comment_like";
    private static final String KEY_COMMENT_RATE = "comment_rate";
    private static final String KEY_UID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_AVATAR = "user_avatar";
    private static final String KEY_COMMENT_UPLOAD= "comment_upload_time";

    public RateDialog(@NonNull Context context, String comment_id) {
        super(context);
        setOwnerActivity((Activity) context);
        this.comment_id = comment_id;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rate);
        course_id =  getOwnerActivity().getIntent().getStringExtra("course_key");
        LoadingAlert loadingAlert = new LoadingAlert(getOwnerActivity());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mapping();
        if(comment_id != null)
        {
            updateData();
        }
        else{
            submitBtn.setText("Đánh giá ngay");
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment_id != null){
                    DocumentReference documentReference = db.collection("Courses").document(course_id).collection("Comment").document(comment_id);
                    Map map = new HashMap();
                    map.put(KEY_COMMENT_CONTENT, editComment.getText().toString());
                    map.put(KEY_COMMENT_RATE, ratingBar.getRating());
                    documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Query query = db.collection("Courses").document(course_id).collection("Comment");
                            AggregateQuery aggregateQuery = query.aggregate(AggregateField.average("comment_rate"));
                            aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        AggregateQuerySnapshot snapshot = task.getResult();
                                        DocumentReference reference3 = db.collection("Courses").document(course_id);
                                        String a = String.format("%.1f",snapshot.get(AggregateField.average("comment_rate"))).replace(',','.');
                                        Map map = new HashMap();
                                        map.put(KEY_COURSE_RATE, Double.parseDouble(a));
                                        reference3.update(map).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {

                                            }
                                        });
                                    }
                                }
                            });
                            toastMes("Sửa thành công");
                            dismiss();
                        }
                    });
                }
                else{
                    db.collection("Courses").document(course_id).collection("Comment").whereEqualTo("user_id", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isComplete()){
                                if(task.getResult().size() !=0){
                                    toastMes("Bạn chỉ có thể đánh giá 1 lần");
                                }
                                else{
                                    if(user != null)
                                    {
                                        loadingAlert.startLoading();
                                        String uid = user.getUid();

                                        DocumentReference reference = db.collection("Users").document(uid);
                                        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                if(error!=null) {
                                                    return;
                                                }
                                                DocumentReference reference1 = db.collection("Courses").document(course_id).collection("Comment").document();
                                                String user_name = value.getString("user_name");
                                                String user_avatar = value.getString("user_avatar");
                                                Map hashMap = new HashMap<>();
                                                hashMap.put(KEY_COURSE_ID, course_id);
                                                hashMap.put(KEY_COMMENT_ID, reference1.getId());
                                                hashMap.put(KEY_COMMENT_UPLOAD, FieldValue.serverTimestamp());
                                                hashMap.put(KEY_UID, uid);
                                                hashMap.put(KEY_USER_AVATAR, user_avatar);
                                                hashMap.put(KEY_USER_NAME, user_name);
                                                hashMap.put(KEY_COMMENT_LIKE, 0);
                                                hashMap.put(KEY_COMMENT_RATE, ratingBar.getRating());
                                                hashMap.put(KEY_COMMENT_CONTENT,editComment.getText().toString());

                                                reference1.set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Query query = db.collection("Courses").document(course_id).collection("Comment");
                                                        AggregateQuery aggregateQuery = query.aggregate(AggregateField.average("comment_rate"));
                                                        aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    AggregateQuerySnapshot snapshot = task.getResult();
                                                                    DocumentReference reference3 = db.collection("Courses").document(course_id);
                                                                    String a = String.format("%.1f",snapshot.get(AggregateField.average("comment_rate"))).replace(',','.');
                                                                    Map map = new HashMap();
                                                                    map.put(KEY_COURSE_RATE, Double.parseDouble(a));
                                                                    reference3.update(map).addOnCompleteListener(new OnCompleteListener() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task task) {

                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });

                                                        toastMes("Đánh giá thành công");
                                                        loadingAlert.closeLoading();
                                                        dismiss();
                                                    }
                                                });
                                            }
                                        });


                                    }else{
                                        loadingAlert.closeLoading();
                                        toastMes("Có lỗi xảy ra !!!");
                                    }
                                }
                            }
                        }
                    });
                }

            }
        });
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(v <= 1)
                {
                    rateImage.setImageResource(R.drawable._1s);
                }else if (v <= 2)
                {
                    rateImage.setImageResource(R.drawable._2s);
                }else if (v <= 3)
                {
                    rateImage.setImageResource(R.drawable._3s);
                }else if (v <= 4)
                {
                    rateImage.setImageResource(R.drawable._4s);
                }else if (v <= 5)
                {
                    rateImage.setImageResource(R.drawable._5s);
                }
                imageAnimation(rateImage);
            }
        });


    }
    private void updateData(){
        submitBtn.setText("Xác nhận");
        txtTitleRate.setText("Chỉnh sửa đánh giá");
        db.collection("Courses").document(course_id).collection("Comment").document(comment_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete())
                {
                    currentContent = task.getResult().getString("comment_content");
                    currentRate =  Float.parseFloat(String.valueOf(task.getResult().getDouble("comment_rate")));
                    ratingBar.setRating(Float.parseFloat(String.valueOf(currentRate)+"f"));
                    //ratingBar.setRating((float)Float.parseFloat(String.valueOf(task.getResult().getDouble("comment_rate"))));
                    editComment.setText(task.getResult().getString("comment_content"));
                }
            }
        });
    }
    private void imageAnimation(ImageView imageView){
        ScaleAnimation anim = new ScaleAnimation(0,1f,0,1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        anim.setFillAfter(true);
        anim.setDuration(200);
        imageView.startAnimation(anim);

    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(getOwnerActivity(), mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void mapping(){
        laterBtn = (AppCompatButton) findViewById(R.id.laterBtn);
        submitBtn = (AppCompatButton) findViewById(R.id.submitBtn);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        rateImage = (ImageView) findViewById(R.id.ratingImage);
        editComment = (TextInputEditText) findViewById(R.id.editTextComment);
        txtTitleRate = (TextView) findViewById(R.id.txtTitleRate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getWindow() != null) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getWindow().setAttributes(params);
            getWindow().getAttributes().windowAnimations = vn.zalopay.sdk.R.style.Animation_AppCompat_Dialog;
        }
    }
    @Override
    public void show() {
        super.show();
        View view = getWindow().getDecorView();
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(fadeInAnimation);
    }
}

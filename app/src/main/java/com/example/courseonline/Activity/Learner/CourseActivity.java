package com.example.courseonline.Activity.Learner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.CommentAdapter;
import com.example.courseonline.Adapter.Learner.HeadingAdapter;
import com.example.courseonline.Adapter.Learner.TypeAdapter;
import com.example.courseonline.Api.CreateOrder;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Dialog.Learner.CommentDialog;
import com.example.courseonline.Dialog.Learner.RateDialog;
import com.example.courseonline.Dialog.Learner.TeacherDialog;
import com.example.courseonline.Domain.CommentClass;
import com.example.courseonline.Domain.HeadingClass;
import com.example.courseonline.Domain.TypeClass;
import com.example.courseonline.Domain.VideoClass;
import com.example.courseonline.R;
import com.example.courseonline.util.PaymentsUtil;
import com.example.courseonline.viewmodel.CheckoutViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.button.ButtonConstants;
import com.google.android.gms.wallet.button.ButtonOptions;
import com.google.android.gms.wallet.button.PayButton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class CourseActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;
    BottomSheetDialog bottomSheetDialog;
    CheckoutViewModel model;
    ConstraintLayout constraintDescription;
    public PayButton googlePayButton;
    AppCompatButton btnJoin1, btnCancel;
    LinearLayout joinByPoints, joinByZaloPay;
    CoordinatorLayout courseCoor;
    ProgressBar progressBar;
    CircularProgressIndicator videoProgress, exProgress;
    TextView textMember, textTime, textRate, textUpload, textTeacher, textTitle, txtProgress, txtSeeAll, txtNone3, txtVideoProgress, txtExProgress, points;
    ShapeableImageView avatarTeacher;
    ConstraintLayout constraintHeading6,constraintHeading2, constraintHeading3, constraintHeading4;
    LinearLayout linearHeading6, linearHeading2, linearHeading3, linearHeading4, linearHeading6Child0, linearHeading6Child1, linearHeading6Child2, linearPayMethod;
    ImageView  imgArrow1, imgArrow3 ,imgBack, imgArrow4, imgArrow6;
    TextView expandableTextView,txtState;
    public TextView txtPrice;
    RecyclerView recyclerLearn, recyclerReview, recyclerCourseType;
    private FirebaseFirestore db;
    private HeadingAdapter headingAdapter;
    private CommentAdapter commentAdapter3;
    private TypeAdapter adapterCategory;
    private ArrayList<HeadingClass> arrayHeading = new ArrayList<>();
    private ArrayList<CommentClass> arrayTopComment = new ArrayList<>();
    ArrayList<TypeClass> arrayCategory = new ArrayList<>();
    private FirebaseAuth mAuth;
    private String key, uid;
    private long member;
    public boolean isOwn = false;
    private long price;
    private String teacherID;
    private String userID;
    private String name;
    private boolean isParticipating = false;
    public int shortAnimationDuration;
    private int totalQuiz = 0;
    private int checkedQuiz = 0;
    public  int totalVideo = 0;

    private int checkedVideo = 0;
    int totalTasks = 0;
    int completedTasks = 0;
    boolean[] video = {false};
    boolean[] quiz = {false};
    private static final String KEY_UID = "user_id";
    private static final String KEY_CID = "course_id";
    private static final String KEY_OWN_COURSE_DATE = "own_course_date";
    private static final String KEY_OWN_COURSE_ID = "own_course_id";
    private static final String KEY_OWN_COURSE_ITEM_TYPE = "own_course_item_type";
    private static final String KEY_OWN_COURSE_ITEM_ID = "own_course_item_id";
    private static final String KEY_OWN_COURSE_ITEM_COMPLETE = "own_course_complete";
    private static final String KEY_OWN_COURSE_ITEM_PROGRESS= "own_course_progress";
    private static final String KEY_OWN_COURSE_ITEM_VIDEO_PROGRESS= "own_course_video_progress";
    private static final String KEY_OWN_COURSE_ITEM_EX_PROGRESS= "own_course_ex_progress";
    private static final String KEY_ORDER_DATE = "order_date";
    private static final String KEY_ORDER_PAYER_ID = "order_payer_id";
    private static final String KEY_ORDER_PAY_METHOD = "order_pay_method";
    private static final String KEY_ORDER_ID = "order_id";
    private static final String KEY_ORDER_ITEM_ID = "order_item_id";
    private static final String KEY_ORDER_AMOUNT = "order_amount";
    private static final String KEY_ORDER_STATUS= "order_status";
    private static final String KEY_USER_BEST_POINTS = "user_best_points";
    private static final String KEY_USER_POINTS = "user_points";
//    private static final String KEY_PROGRESS_EX = "own_course_ex_progress";
//    private static final String KEY_COMPLETE_COURSE = "own_course_complete";
//    private static final String KEY_PROGRESS_COURSE = "own_course_progress";
//    private static final String KEY_PROGRESS_VIDEO = "own_course_video_progress";
    Toast toast;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    ActivityResultLauncher<IntentSenderRequest> resolvePaymentForResult = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case Activity.RESULT_OK:
                        Intent resultData = result.getData();
                        if (resultData != null) {
                            PaymentData paymentData = PaymentData.getFromIntent(result.getData());
                            if (paymentData != null) {
                                handlePaymentSuccess(paymentData);
                            }
                        }
                        break;

                    case Activity.RESULT_CANCELED:
                        break;
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        key = getIntent().getStringExtra("course_key");
        mapping();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid =  mAuth.getCurrentUser().getUid();
        initGoogle();
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        LoadingAlert alert = new LoadingAlert(CourseActivity.this);

        alert.startLoading();
        courseCoor.setVisibility(View.GONE);
            executor.execute(new Runnable() {
                @Override
                public void run() {

                    recyclerCourseType.setLayoutManager(new LinearLayoutManager(CourseActivity.this,LinearLayoutManager.HORIZONTAL,false));
                    recyclerCourseType.setHasFixedSize(true);
                    recyclerCourseType.setItemViewCacheSize(20);
                    //adapterCategory.setHasStableIds(true);

                    recyclerLearn.setLayoutManager(new LinearLayoutManager(CourseActivity.this,LinearLayoutManager.VERTICAL,false));
                    recyclerLearn.setHasFixedSize(true);
                    recyclerLearn.setItemViewCacheSize(20);
                    //headingAdapter.setHasStableIds(true);

                    recyclerReview.setLayoutManager(new LinearLayoutManager(CourseActivity.this,LinearLayoutManager.VERTICAL,false));
                    recyclerReview.setHasFixedSize(true);
                    recyclerReview.setItemViewCacheSize(20);
                    //  commentAdapter3.setHasStableIds(true);
                    txtSeeAll.setVisibility(View.GONE);


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadData();
                            setSupportActionBar(toolbar);
                            imgBack.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view){
                                    finish();
                                }
                            });
                            shortAnimationDuration = getResources().getInteger(
                                    android.R.integer.config_shortAnimTime);
                            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout2);
                            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                                boolean isShow = true;
                                int scrollRange = -1;
                                @Override
                                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                    if (scrollRange == -1) {
                                        scrollRange = appBarLayout.getTotalScrollRange();
                                    }
                                    if (scrollRange + verticalOffset == 0) {
                                        // textTitle.animate().alpha(0.0f).setDuration(100);
                                        constraintDescription.animate().setDuration(80);
                                        displayPayment(isOwn, isShow);
                                        isShow = true;
                                    } else if(isShow) {
                                        //textTitle.animate().alpha(1.0f).setDuration(100);
                                        constraintDescription.animate().setDuration(80);
                                        isShow = false;
                                    }
                                }
                            });
                            txtSeeAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CommentDialog commentDialog = new CommentDialog(key);
                                    commentDialog.show(getSupportFragmentManager(), commentDialog.getTag());
                                }
                            });
//                        constraintHeading6.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if(linearHeading6.getVisibility() == View.VISIBLE)
//                                {
//                                    AutoTransition autoTransition = new AutoTransition();
//                                    autoTransition.setDuration(30);
//                                    TransitionManager.beginDelayedTransition(constraintHeading6, autoTransition);
//                                    linearHeading6.setVisibility(View.GONE);
//                                    imgArrow6.setImageResource(R.drawable.arrow_down_blue);
//                                }else{
//                                    AutoTransition autoTransition = new AutoTransition();
//                                    autoTransition.setDuration(30);
//                                    TransitionManager.beginDelayedTransition(constraintHeading6, autoTransition);
//                                    linearHeading6.setVisibility(View.VISIBLE);
//                                    imgArrow6.setImageResource(R.drawable.arrow_up);
//                                }
//                                db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            if(task.getResult().size() != 0)
//                                            {
//                                                linearHeading6Child0.setVisibility(linearHeading6.getVisibility());
//                                            }
//                                        }
//                                    }
//                                });
//
//                            }
//                        });
                            linearHeading2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TeacherDialog teacherDialog = new TeacherDialog(CourseActivity.this, teacherID);
                                    teacherDialog.show();
                                }
                            });
                            constraintHeading4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(linearHeading4.getVisibility() == View.VISIBLE)
                                    {
                                        AutoTransition autoTransition = new AutoTransition();
                                        autoTransition.setDuration(30);
                                        TransitionManager.beginDelayedTransition(constraintHeading4, autoTransition);
                                        linearHeading4.setVisibility(View.GONE);
                                        imgArrow4.setImageResource(R.drawable.arrow_down_blue);
                                    }else{
                                        AutoTransition autoTransition = new AutoTransition();
                                        autoTransition.setDuration(30);
                                        TransitionManager.beginDelayedTransition(constraintHeading4, autoTransition);
                                        linearHeading4.setVisibility(View.VISIBLE);
                                        imgArrow4.setImageResource(R.drawable.arrow_up);
                                    }

                                }
                            });
//                        constraintHeading2.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if(linearHeading2.getVisibility() == View.VISIBLE)
//                                {
//                                    AutoTransition autoTransition = new AutoTransition();
//                                    autoTransition.setDuration(30);
//                                    TransitionManager.beginDelayedTransition(constraintHeading2, autoTransition);
//                                    linearHeading2.setVisibility(View.GONE);
//                                    imgArrow1.setImageResource(R.drawable.arrow_down_blue);
//                                }else{
//                                    AutoTransition autoTransition = new AutoTransition();
//                                    autoTransition.setDuration(30);
//                                    TransitionManager.beginDelayedTransition(constraintHeading2, autoTransition);
//                                    linearHeading2.setVisibility(View.VISIBLE);
//                                    imgArrow1.setImageResource(R.drawable.arrow_up);
//                                }
//                            }
//                        });
                            constraintHeading3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(linearHeading3.getVisibility() == View.VISIBLE)
                                    {
                                        AutoTransition autoTransition = new AutoTransition();
                                        autoTransition.setDuration(30);
                                        TransitionManager.beginDelayedTransition(constraintHeading3, autoTransition);
                                        linearHeading3.setVisibility(View.GONE);
                                        imgArrow3.setImageResource(R.drawable.arrow_down_blue);
                                    }else{
                                        AutoTransition autoTransition = new AutoTransition();
                                        autoTransition.setDuration(30);
                                        TransitionManager.beginDelayedTransition(constraintHeading3, autoTransition);
                                        linearHeading3.setVisibility(View.VISIBLE);
                                        imgArrow3.setImageResource(R.drawable.arrow_up);
                                    }
                                }
                            });
                        }
                    });

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            courseCoor.setVisibility(View.VISIBLE);
                            alert.closeLoading();
                        }
                    }, 600);

                }
            });
        }
    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(CourseActivity.this);
        alert.setMessage("Bạn có chắc chắn đăng kí tham gia khoá học ?")
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        if(mAuth.getCurrentUser() != null) {
                            bottomSheetDialog.dismiss();
                            DocumentReference reference = db.collection("Users").document(uid).collection("OwnCourses").document();
                            Map hashMap = new HashMap<>();
                            hashMap.put(KEY_OWN_COURSE_ITEM_ID, key);
                            hashMap.put(KEY_OWN_COURSE_ID, reference.getId());
                            hashMap.put(KEY_OWN_COURSE_ITEM_TYPE, "course");
                            hashMap.put(KEY_OWN_COURSE_ITEM_COMPLETE, false);
                            hashMap.put(KEY_OWN_COURSE_ITEM_PROGRESS, "0.00");
                            hashMap.put(KEY_OWN_COURSE_ITEM_VIDEO_PROGRESS, "0.00");
                            hashMap.put(KEY_OWN_COURSE_ITEM_EX_PROGRESS, "0.00");
                            hashMap.put(KEY_OWN_COURSE_DATE, FieldValue.serverTimestamp());
                            reference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    member += 1;
                                    DocumentReference Ref = db.collection("Courses").document(key);
                                    Ref.update("course_member",member)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    recreate();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            });
                                    toastMes("Đăng kí thành công! Chúc bạn học thật tốt <3");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    toastMes("Có lỗi xảy ra !");
                                }
                            });


                        }
                    }
                }).setNegativeButton("Huỷ", null);
        AlertDialog alert1 = alert.create();
        alert1.show();
    }
    private String changeCurrency(double value){
        Locale locale = new Locale("vi", "VN");
        Currency currency = Currency.getInstance("VND");
        DecimalFormatSymbols df = DecimalFormatSymbols.getInstance(locale);
        df.setCurrency(currency);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);
        return numberFormat.format(value);
    }
    private void updateProgressCourse() {
        linearHeading6.setVisibility(View.GONE);
        linearHeading6Child0.setVisibility(View.GONE);
        if (mAuth.getCurrentUser() != null) {
            // Biến đếm số lượng Heading đã xử lý
            final int[] totalHeading = {0}; // Biến lưu tổng số Heading
            final int[] headingCount = {0};
            final int[] headingCount1 = {0};
            final ArrayList<String> videoId = new ArrayList<>();
            final ArrayList<String> quizId = new ArrayList<>();
            db.collection("Courses").document(key).collection("Heading").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isComplete()) {
                        totalHeading[0] = task.getResult().size();

                        // Duyệt qua tất cả các Heading
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            // Truy vấn video trong Heading
                            db.collection("Courses").document(key).collection("Heading").document(doc.getId()).collection("video").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                    if (task1.isComplete()) {
                                        headingCount[0]++;
                                        totalVideo += task1.getResult().size();
                                        for (QueryDocumentSnapshot doc : task1.getResult()) {
                                            videoId.add(doc.getId());
                                        }

                                        checkIfHeadingComplete(headingCount, totalHeading[0], videoId, null);

                                    }
                                }
                            });

                            // Truy vấn quiz trong Heading
                            db.collection("Courses").document(key).collection("Heading").document(doc.getId()).collection("quiz").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                    if (task1.isComplete()) {
                                        headingCount1[0]++;
                                        totalQuiz += task1.getResult().size(); // Cộng dồn số quiz
                                        for (QueryDocumentSnapshot doc : task1.getResult()) {
                                            quizId.add(doc.getId());
                                        }
                                        checkIfHeadingComplete(headingCount1, totalHeading[0], null, quizId);
//                                        if(headingCount1[0] == task.getResult().size())
//                                        {
//
//                                            headingCount1[0]=0;
//                                        }
                                        // Kiểm tra nếu cả video và quiz của Heading đã hoàn tất
//                                        checkIfHeadingComplete(headingCount, totalHeading[0], videoId, quizId);
                                    }
                                }
                            });

                            // Tăng số lượng heading đã xử lý

                        }
                    }
                }
            });
        }
    }

    // Kiểm tra nếu tất cả các Heading đã xử lý xong
    private void checkIfHeadingComplete(int[] headingCount, int totalHeading, ArrayList<String> videoId, ArrayList<String> quizId) {
        if(headingCount[0] == totalHeading)
        {
            finalUpdate(videoId, quizId);
        }
    }
    //    private void updateAllProgress(){
//        Query query = db.collection("Users")
//                .document(mAuth.getCurrentUser().getUid())
//                .collection("CheckQuiz")
//                .whereEqualTo("course_id", key).whereEqualTo("quiz_state", true);
//        AggregateQuery aggregateQuery = query.count();
//        aggregateQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    totalQuiz = 0;
//                    checkedQuiz = 0;
//                    AggregateQuerySnapshot snapshot = task.getResult();
//                    checkedQuiz = (int) snapshot.getCount();
//
//                    db.collection("Courses").document(key)
//                            .collection("Heading")
//                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                    if (error != null) {
//                                        return;
//                                    }
//                                    if (value != null && value.size() != 0) {
//                                        final int[] i = {0};
//                                        for (QueryDocumentSnapshot doc : value) {
//                                            Query query1 = db.collection("Courses")
//                                                    .document(key)
//                                                    .collection("Heading")
//                                                    .document(doc.getString("heading_id"))
//                                                    .collection("quiz");
//                                            AggregateQuery aggregateQuery1 = query1.count();
//                                            aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                                    if (task.isSuccessful()) {
//                                                        AggregateQuerySnapshot snapshot = task.getResult();
//                                                        totalQuiz += (int) snapshot.getCount();
//                                                        i[0]++;
//
//                                                        if (i[0] == value.size()) {
//                                                            if (totalQuiz != 0) {
//                                                                db.collection("Users")
//                                                                        .document(mAuth.getCurrentUser().getUid())
//                                                                        .collection("OwnCourses")
//                                                                        .whereEqualTo("own_course_item_id", key)
//                                                                        .get()
//                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                if (task.isComplete()) {
//                                                                                    for (QueryDocumentSnapshot documentt : task.getResult()) {
//                                                                                        DocumentReference documentReference = db.collection("Users")
//                                                                                                .document(mAuth.getCurrentUser().getUid())
//                                                                                                .collection("OwnCourses")
//                                                                                                .document(documentt.getString("own_course_id"));
//
//                                                                                        Map<String, Object> map = new HashMap<>();
//                                                                                        float temp = ((float) checkedQuiz / totalQuiz) * 100;
//                                                                                        String videoProgressStr = documentt.getString("own_course_video_progress");
//                                                                                        double videoProgress = (videoProgressStr != null) ? Double.parseDouble(videoProgressStr) : 0.0;
////                                                                                                double temp1 = (temp / 2) + (videoProgress / 2);
////                                                                                                Log.d("test", String.valueOf(temp1));
//                                                                                        map.put(KEY_PROGRESS_EX, String.format("%.2f", temp).replace(",", "."));
////                                                                                                if(task.getResult().getCount() > 0 ) {
////
////                                                                                                    map.put(KEY_PROGRESS_COURSE, String.format("%.2f", temp1).replace(",", "."));
////                                                                                                }else{
////
////                                                                                                    map.put(KEY_PROGRESS_COURSE, String.format("%.2f", temp1*2).replace(",", "."));
////                                                                                                }
//                                                                                        double temp1 = temp + videoProgress;
//                                                                                        map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp1).replaceAll(",","."));
//                                                                                        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                            @Override
//                                                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                                                updateVideoProgress();
////                                                                                                        db.collection("Users")
////                                                                                                                .document(mAuth.getCurrentUser().getUid())
////                                                                                                                .collection("OwnCourses")
////                                                                                                                .document(documentt.getString("own_course_id")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////                                                                                                                    @Override
////                                                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////
////                                                                                                                        if(task.isComplete()){
////                                                                                                                            if(task.getResult().getString("own_course_progress").equals("100.00"))
////                                                                                                                            {
////                                                                                                                                DocumentReference documentReference = db.collection("Users")
////                                                                                                                                        .document(mAuth.getCurrentUser().getUid())
////                                                                                                                                        .collection("OwnCourses")
////                                                                                                                                        .document(task.getResult().getId());
////                                                                                                                                Map map = new HashMap();
////                                                                                                                                map.put(KEY_COMPLETE_COURSE, true);
////                                                                                                                                documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                                                                                                    @Override
////                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
////                                                                                                                                        updateVideoProgress();
////                                                                                                                                    }
////                                                                                                                                });
////                                                                                                                            }
////                                                                                                                            else{
////                                                                                                                                DocumentReference documentReference = db.collection("Users")
////                                                                                                                                        .document(mAuth.getCurrentUser().getUid())
////                                                                                                                                        .collection("OwnCourses")
////                                                                                                                                        .document(task.getResult().getId());
////                                                                                                                                Map map = new HashMap();
////                                                                                                                                map.put(KEY_COMPLETE_COURSE, false);
////                                                                                                                                documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                                                                                                    @Override
////                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
////                                                                                                                                        updateVideoProgress();
////                                                                                                                                    }
////                                                                                                                                });
////                                                                                                                            }
////                                                                                                                        }
////                                                                                                                    }
////                                                                                                                });
//
//                                                                                            }
//                                                                                        });
//
//                                                                                    }
//                                                                                }
//                                                                            }
//                                                                        });
//                                                            }else{
//                                                                db.collection("Users")
//                                                                        .document(mAuth.getCurrentUser().getUid())
//                                                                        .collection("OwnCourses")
//                                                                        .whereEqualTo("own_course_item_id", key)
//                                                                        .get()
//                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                if (task.isComplete()) {
//                                                                                    for (QueryDocumentSnapshot documentt : task.getResult()) {
//                                                                                        DocumentReference documentReference = db.collection("Users")
//                                                                                                .document(mAuth.getCurrentUser().getUid())
//                                                                                                .collection("OwnCourses")
//                                                                                                .document(documentt.getString("own_course_id"));
//
//                                                                                        Map<String, Object> map = new HashMap<>();
//                                                                                        float temp = 0;
//                                                                                        String videoProgressStr = documentt.getString("own_course_video_progress");
//                                                                                        double videoProgress = (videoProgressStr != null) ? Double.parseDouble(videoProgressStr) : 0.0;
////                                                                                                double temp1 = (temp / 2) + (videoProgress / 2);
////                                                                                                Log.d("test", String.valueOf(temp1));
//                                                                                        map.put(KEY_PROGRESS_EX, "0");
////                                                                                                if(task.getResult().getCount() > 0 ) {
////
////                                                                                                    map.put(KEY_PROGRESS_COURSE, String.format("%.2f", temp1).replace(",", "."));
////                                                                                                }else{
////
////                                                                                                    map.put(KEY_PROGRESS_COURSE, String.format("%.2f", temp1*2).replace(",", "."));
////                                                                                                }
//                                                                                        double temp1 = temp + videoProgress;
//                                                                                        map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp1).replaceAll(",","."));
//                                                                                        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                            @Override
//                                                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                                                updateVideoProgress();
//                                                                                            }
//                                                                                        });
//
//                                                                                    }
//                                                                                }
//                                                                            }
//                                                                        });
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            });
//                                        }
//                                    }
//                                }
//                            });
//                }
//            }
//        });
//    }
//    private void updateVideoProgress(){
//        Query query3 = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereEqualTo("course_id", key);
//        AggregateQuery aggregateQuery3 = query3.count();
//        aggregateQuery3.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    totalVideo = 0;
//                    checkedVideo = 0;
//                    AggregateQuerySnapshot snapshot = task.getResult();
//                    checkedVideo = (int)snapshot.getCount();
//
//                    db.collection("Courses").document(key).collection("Heading").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                            if(error != null)
//                            {
//                                return;
//                            }
//                            if(value.size() != 0)
//                            {
//                                final int[] i = {0};
//                                for(QueryDocumentSnapshot doc : value)
//                                {
//                                    String hid = doc.getString("heading_id").toString();
//
//                                    Query query1 = db.collection("Courses").document(key).collection("Heading").document(hid).collection("video");
//                                    AggregateQuery aggregateQuery1 = query1.count();
//                                    aggregateQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                                            if (task.isSuccessful()) {
//                                                AggregateQuerySnapshot snapshot = task.getResult();
//                                                totalVideo = totalVideo + (int)snapshot.getCount();
//                                                i[0]++;
//                                                if(i[0] == value.size())
//                                                {
//                                                    if(totalVideo != 0)
//                                                    {
//                                                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                if(task.isComplete())
//                                                                {
//                                                                    for (QueryDocumentSnapshot documentt : task.getResult()) {
//                                                                        DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(documentt.getString("own_course_id"));
//                                                                        Map map = new HashMap();
//                                                                        float temp = (((float)checkedVideo/totalVideo)*100);
//                                                                        String exProgressStr = documentt.getString("own_course_ex_progress");
//                                                                        double exeProgress = (exProgressStr != null) ? Double.parseDouble(exProgressStr) : 0.0;
//                                                                        map.put(KEY_PROGRESS_VIDEO, String.format("%.2f",temp).replaceAll(",","."));
//                                                                        double temp1 = temp + exeProgress;
//                                                                        map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp1).replaceAll(",","."));
//                                                                        documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task task) {
//                                                                                displayProgress();
//                                                                            }
//                                                                        });
//
//                                                                    }
//
//                                                                }
//                                                            }
//                                                        });
//                                                    }else{
//                                                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                if(task.isComplete())
//                                                                {
//                                                                    for (QueryDocumentSnapshot documentt : task.getResult()) {
//                                                                        DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(documentt.getString("own_course_id"));
//                                                                        Map map = new HashMap();
//                                                                        float temp = 0;
//                                                                        String exProgressStr = documentt.getString("own_course_ex_progress");
//                                                                        double exProgress = (exProgressStr != null) ? Double.parseDouble(exProgressStr) : 0.0;
//                                                                        map.put(KEY_PROGRESS_VIDEO, "0");
//                                                                        double temp1 = temp + exProgress;
//                                                                        map.put(KEY_PROGRESS_COURSE, String.format("%.2f",temp1).replaceAll(",","."));
//                                                                        documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task task) {
//                                                                                displayProgress();
//                                                                            }
//                                                                        });
//
//                                                                    }
//
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    });
//                                }
//
//                            }
//                        }
//                    });
//                }
//            }
//        });
//    }
    private void finalUpdate( List<String> videoId, List<String> quizId){
            if(videoId != null && videoId.size() != 0)
            {
                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckVideo").whereIn("video_id", videoId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()){
                            if(task.getResult() != null)
                            {
                                checkedVideo = task.getResult().size();
                                float videoProgress = ((float) checkedVideo/totalVideo) * 100;
                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                        if(task1.isComplete()){
                                            for(QueryDocumentSnapshot doc:task1.getResult())
                                            {
                                                DocumentReference ref = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                                HashMap map = new HashMap();

                                                map.put(KEY_OWN_COURSE_ITEM_VIDEO_PROGRESS,String.format("%.2f",videoProgress).replaceAll(",","."));
                                                ref.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isComplete()){
                                                                    if(task.getResult().size() != 0)
                                                                    {
                                                                        for(QueryDocumentSnapshot doc : task.getResult()){
                                                                            DocumentReference reference =  db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                                                            Map map = new HashMap<>();
                                                                            float videoProgress = Float.parseFloat(doc.getString("own_course_video_progress"));
                                                                            float quizProgress = Float.parseFloat(doc.getString("own_course_ex_progress"));
                                                                            float total = videoProgress + quizProgress;
                                                                            map.put(KEY_OWN_COURSE_ITEM_PROGRESS, String.format("%.2f",total).replaceAll(",","."));
                                                                            reference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isComplete()){
                                                                                        displayProgress();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }

                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }

                                        }
                                    }
                                });;
                            }else{
                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                        if(task1.isComplete()){
                                            for(QueryDocumentSnapshot doc:task1.getResult())
                                            {
                                                DocumentReference ref = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                                HashMap map = new HashMap();

                                                map.put(KEY_OWN_COURSE_ITEM_VIDEO_PROGRESS,"0");
                                                ref.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isComplete()){
                                                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                                                        DocumentReference reference =  db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                                                        Map map = new HashMap<>();
                                                                        float videoProgress = Float.parseFloat(doc.getString("own_course_video_progress"));
                                                                        float quizProgress = Float.parseFloat(doc.getString("own_course_ex_progress"));
                                                                        float total = videoProgress + quizProgress;
                                                                        map.put(KEY_OWN_COURSE_ITEM_PROGRESS, String.format("%.2f",total).replaceAll(",","."));
                                                                        reference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isComplete()){
                                                                                    displayProgress();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }

                                        }
                                    }
                                });;
                            }

                        }
                    }
                });
            }else{
                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                        if(task1.isComplete()){
                            for(QueryDocumentSnapshot doc:task1.getResult())
                            {
                                DocumentReference ref = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                HashMap map = new HashMap();

                                map.put(KEY_OWN_COURSE_ITEM_VIDEO_PROGRESS,"0");
                                ref.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isComplete()){
                                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                                        DocumentReference reference =  db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                                        Map map = new HashMap<>();
                                                        float videoProgress = Float.parseFloat(doc.getString("own_course_video_progress"));
                                                        float quizProgress = Float.parseFloat(doc.getString("own_course_ex_progress"));
                                                        float total = videoProgress + quizProgress;
                                                        map.put(KEY_OWN_COURSE_ITEM_PROGRESS, String.format("%.2f",total).replaceAll(",","."));
                                                        reference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isComplete()){
                                                                    displayProgress();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }

                        }
                    }
                });;
            }
            if(quizId != null && quizId.size() != 0)
            {
                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("CheckQuiz").whereIn("quiz_id", quizId).whereEqualTo("quiz_state", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()){
                            if(task.getResult().size() != 0)
                            {
                                checkedQuiz = task.getResult().size();
                                float quizProgress = ((float) checkedQuiz/totalQuiz) * 100;
                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                        if(task1.isComplete()){
                                            for(QueryDocumentSnapshot doc:task1.getResult())
                                            {
                                                DocumentReference ref = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                                HashMap map = new HashMap();

                                                map.put(KEY_OWN_COURSE_ITEM_EX_PROGRESS,String.format("%.2f",quizProgress).replaceAll(",","."));
                                                ref.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isComplete()){
                                                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                                                        DocumentReference reference =  db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                                                        Map map = new HashMap<>();
                                                                        float videoProgress = Float.parseFloat(doc.getString("own_course_video_progress"));
                                                                        float quizProgress = Float.parseFloat(doc.getString("own_course_ex_progress"));
                                                                        float total = videoProgress + quizProgress;
                                                                        map.put(KEY_OWN_COURSE_ITEM_PROGRESS, String.format("%.2f",total).replaceAll(",","."));
                                                                        reference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isComplete()){
                                                                                    displayProgress();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }

                                        }
                                    }
                                });;
                            }else{
                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                        if(task1.isComplete()){
                                            if(task.getResult().size()!=0)
                                            {
                                                for(QueryDocumentSnapshot doc:task1.getResult())
                                                {
                                                    DocumentReference ref = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                                    HashMap map = new HashMap();

                                                    map.put(KEY_OWN_COURSE_ITEM_EX_PROGRESS,"0");
                                                    ref.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if(task.isComplete()){
                                                                        for(QueryDocumentSnapshot doc : task.getResult()){
                                                                            DocumentReference reference =  db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                                                            Map map = new HashMap<>();
                                                                            float videoProgress = Float.parseFloat(doc.getString("own_course_video_progress"));
                                                                            float quizProgress = Float.parseFloat(doc.getString("own_course_ex_progress"));
                                                                            float total = videoProgress + quizProgress;
                                                                            map.put(KEY_OWN_COURSE_ITEM_PROGRESS, String.format("%.2f",total).replaceAll(",","."));
                                                                            reference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isComplete()){
                                                                                        displayProgress();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            }


                                        }
                                    }
                                });;
                            }



                        }
                    }
                });
            }else{
                db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                        if(task1.isComplete()){
                            if(task1.getResult().size()!=0)
                            {
                                for(QueryDocumentSnapshot doc:task1.getResult())
                                {
                                    DocumentReference ref = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                    HashMap map = new HashMap();

                                    map.put(KEY_OWN_COURSE_ITEM_EX_PROGRESS,"0");
                                    ref.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isComplete()){
                                                        for(QueryDocumentSnapshot doc : task.getResult()){
                                                            DocumentReference reference =  db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").document(doc.getId());
                                                            Map map = new HashMap<>();
                                                            float videoProgress = Float.parseFloat(doc.getString("own_course_video_progress"));
                                                            float quizProgress = Float.parseFloat(doc.getString("own_course_ex_progress"));
                                                            float total = videoProgress + quizProgress;
                                                            map.put(KEY_OWN_COURSE_ITEM_PROGRESS, String.format("%.2f",total).replaceAll(",","."));
                                                            reference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isComplete()){
                                                                        displayProgress();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }


                        }
                    }
                });;
            }

    }
    private void displayProgress(){
        db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> value) {
                if(value.isComplete()){
                    if(value.getResult().size() != 0){
                        for (QueryDocumentSnapshot document : value.getResult()) {
                            db.collection("Courses").document(key).collection("Heading").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isComplete()) {
                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                    String hid = doc.getId();

                                                    totalTasks += 2;

                                                    db.collection("Courses").document(key).collection("Heading").document(hid).collection("quiz")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                                    if (task1.isComplete() && task1.getResult().size() > 0) {
                                                                        quiz[0] = true;
                                                                    }
                                                                    checkAndProceed((int)Float.parseFloat(document.getString("own_course_progress")));
                                                                }
                                                            });

                                                    db.collection("Courses").document(key).collection("Heading").document(hid).collection("video")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                                    if (task2.isComplete() && task2.getResult().size() > 0) {
                                                                        video[0] = true;
                                                                    }
                                                                    checkAndProceed((int)Float.parseFloat(document.getString("own_course_progress")));
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });


                            txtExProgress.setText(document.getString("own_course_ex_progress") + "%");
                            txtVideoProgress.setText(document.getString("own_course_video_progress") + "%");


                            videoProgress.setProgress((int)Float.parseFloat(document.getString("own_course_video_progress")));
                            exProgress.setProgress((int)Float.parseFloat(document.getString("own_course_ex_progress")));
                        }
                    }
                }
            }
        });
    }
    private void updateLearningState(int progress){
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete())
                {
                    for(DocumentSnapshot documentt: task.getResult()){
                        db.collection("Users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("OwnCourses")
                                .document(documentt.getString("own_course_id")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> value) {
                                        if(value.isComplete() && value.getResult().exists()){
                                            if(progress == 100)
                                            {
                                                DocumentReference documentReference = db.collection("Users")
                                                        .document(mAuth.getCurrentUser().getUid())
                                                        .collection("OwnCourses")
                                                        .document(value.getResult().getId());
                                                Map map = new HashMap();
                                                map.put(KEY_OWN_COURSE_ITEM_COMPLETE, true);
                                                documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        txtState.setText("Đã hoàn thành");
                                                        txtState.setTextColor(getResources().getColor(R.color.dark_green, null));
                                                        linearHeading6.setVisibility(View.VISIBLE);
                                                        linearHeading6Child0.setVisibility(View.VISIBLE);
                                                    }
                                                });
                                            }else{
                                                DocumentReference documentReference = db.collection("Users")
                                                        .document(mAuth.getCurrentUser().getUid())
                                                        .collection("OwnCourses")
                                                        .document(value.getResult().getId());
                                                Map map = new HashMap();
                                                map.put(KEY_OWN_COURSE_ITEM_COMPLETE, false);
                                                documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        txtState.setText("Đang học");
                                                        txtState.setTextColor(getResources().getColor(R.color.blue, null));
                                                        linearHeading6.setVisibility(View.VISIBLE);
                                                        linearHeading6Child0.setVisibility(View.VISIBLE);
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
    private void loadData(){
        adapterCategory = new TypeAdapter(arrayCategory, CourseActivity.this);
        headingAdapter = new HeadingAdapter(arrayHeading, CourseActivity.this);
        commentAdapter3 = new CommentAdapter(arrayTopComment,CourseActivity.this);
        recyclerLearn.setAdapter(headingAdapter);
        recyclerCourseType.setAdapter(adapterCategory);

        if(!key.isEmpty()){
            if(mAuth.getCurrentUser() != null) {
                db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() != 0)
                            {
//                                linearHeading6Child0.setVisibility(View.VISIBLE);
                                updateProgressCourse();
                            }else{
                                linearHeading6Child0.setVisibility(View.GONE);
                            }
                        }
                    }
                });
                db.collection("Courses").document(key).collection("Type").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            return;
                        }
                        arrayCategory.clear();
                        if(value.size() != 0)
                        {
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.toObject(TypeClass.class) != null) {
                                    arrayCategory.add(doc.toObject(TypeClass.class));
                                }
                            }
                        }
                        adapterCategory.notifyDataSetChanged();


                    }
                });
                db.collection("Courses").document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        member = value.getLong("course_member");
                        if(value.getLong("course_member") != null)
                        textMember.setText(String.valueOf(changeValue(value.getLong("course_member"))));
                        toolbar.setTitle(value.getString("course_title"));
                        textTitle.setText(value.getString("course_title"));
                        if(value.getLong("course_rate") != null)
                        textRate.setText(String.valueOf(value.getDouble("course_rate")));
                        if(value.getLong("course_total_time") != null)
                        textTime.setText(String.valueOf(value.getDouble("course_total_time")));
                        price = value.getLong("course_price");
                        teacherID = value.getString("course_owner_id");

                        try {
                            URL url = new URL(value.getString("course_img"));
                            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            BitmapDrawable image = new BitmapDrawable(getResources(), bitmap);
                            toolbarLayout.setBackground(image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        textUpload.setText("Ngày đăng: " + sdf.format(value.getDate("course_upload")));
                        expandableTextView.setText(value.getString("course_description").replace("\\n", "\n"));
                        if (teacherID != "") {
                         db.collection("Users").document(teacherID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                             @Override
                             public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                 if(error != null)
                                 {
                                     return;
                                 }
                                 if(value.exists())
                                 {
                                     textTeacher.setText(value.getString("user_name"));
                                     String url = value.getString("user_avatar");
//                                     if (!isFinishing() && !isDestroyed()) {
//                                         RequestOptions options = new RequestOptions()
//                                                 .placeholder(R.drawable.user)
//                                                 .error(R.drawable.user)
//                                                 .centerInside();
//
//                                         Glide.with(CourseActivity.this)
//                                                 .load(url)
//                                                 .apply(options)
//                                                 .into(avatarTeacher);
//                                     }
                                     Picasso.get().load(url).resize(100, 100).centerInside().into(avatarTeacher);
                                 }




                             }
                         });
                        }else{
//                            if (!isFinishing() && !isDestroyed()) {
//                            Glide.with(CourseActivity.this)
//                                    .load(R.drawable.user)
//                                    .into(avatarTeacher);}
                            Picasso.get().load(R.drawable.profile).resize(100, 100).centerInside().into(avatarTeacher);
                        }
                    }
                });

                db.collection("Courses").document(key).collection("Heading").orderBy("heading_order", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        arrayHeading.clear();
                        if (value.size() != 0) {
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.toObject(VideoClass.class) != null) {
                                    arrayHeading.add(doc.toObject(HeadingClass.class));
                                }

                            }
                        }
                        headingAdapter.notifyDataSetChanged();
                    }
                });
                db.collection("Courses").document(key).collection("Comment").orderBy("comment_like", Query.Direction.DESCENDING).orderBy("comment_rate", Query.Direction.DESCENDING).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if(value.size() != 0)
                        {
                            txtSeeAll.setVisibility(View.VISIBLE);
                            txtNone3.setVisibility(View.GONE);
                        }else{
                            txtSeeAll.setVisibility(View.GONE);
                            txtNone3.setVisibility(View.VISIBLE);
                        }

                        arrayTopComment.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            if(doc.toObject(CommentClass.class) != null)
                            {
                                arrayTopComment.add(doc.toObject(CommentClass.class));
                            }

                        }
                        commentAdapter3.notifyDataSetChanged();
                    }
                });

            }
        }
        recyclerReview.setAdapter(commentAdapter3);
    };
    private void checkAndProceed(int progress) {
        completedTasks++;
        if (completedTasks == totalTasks) {
            if (video[0] && quiz[0]) {
                int temp = progress/2;
                txtProgress.setText("Tiến độ: " + temp + "%");
                progressBar.setProgress(temp);
                updateLearningState(temp);
            } else if (video[0] || quiz[0]) {

                int temp = progress;
                txtProgress.setText("Tiến độ: " + temp + "%");
                progressBar.setProgress(temp);
                updateLearningState(temp);
            } else {
                txtProgress.setText("Tiến độ: " + 0+ "%");
                progressBar.setProgress(0);
                updateLearningState(progress);
            }
        }
    }
    public String changeValue(Number number) {
        char[] suffix = {' ', 'K', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }
    private void initGoogle(){
        model = new ViewModelProvider(CourseActivity.this).get(CheckoutViewModel.class);
        model.canUseGooglePay.observe(CourseActivity.this, CourseActivity.this::setGooglePayAvailable);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course, menu);
        if(mAuth.getCurrentUser() != null)
        {
            db.collection("Users").document(uid).collection("Favourites").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().getData() != null){
                        menu.findItem(R.id.course_favourite).setIcon(R.drawable.favourite_fill);
                    }else{
                        menu.findItem(R.id.course_favourite).setIcon(R.drawable.favourite_border);
                    }
                }
            });
        }
//        int nightModeFlags =
//                getApplicationContext().getResources().getConfiguration().uiMode &
//                        Configuration.UI_MODE_NIGHT_MASK;
//        if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
//        {
//            MenuItem item = menu.findItem(R.id.course_rate);
//            SpannableString s = new SpannableString("Đánh giá khoá học");
//            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
//            item.setTitle(s);
//        }

        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.course_favourite) {
            View view = findViewById(R.id.course_favourite);
            if (view != null) {
                Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce_effect);
                view.startAnimation(bounce);
            }
            if (mAuth.getCurrentUser() != null) {
                db.collection("Users").document(uid).collection("Favourites").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().getData() != null) {
                            db.collection("Users").document(uid).collection("Favourites").document(key)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            item.setIcon(R.drawable.favourite_border);
                                            toastMes("Đã xoá khoá học khỏi mục yêu thích!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            toastMes("Có lỗi xảy ra !");
                                        }
                                    });
                        } else {
                            item.setIcon(R.drawable.favourite_fill);
                            Map<Object, String> hashMap = new HashMap<>();
                            hashMap.put(KEY_UID, uid);
                            hashMap.put(KEY_CID, key);
                            DocumentReference reference = db.collection("Users").document(uid).collection("Favourites").document(key);
                            reference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    toastMes("Đã lưu khoá học vào mục yêu thích!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    toastMes("Có lỗi xảy ra !");
                                }
                            });
                        }
                    }
                });
            }
        } else if (item.getItemId() == R.id.course_rate) {
            db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().size() == 0) {
                        toastMes("Bạn phải tham gia khoá học trước");
                    } else {
                        db.collection("Courses").document(key).collection("Comment").whereEqualTo("user_id", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isComplete()) {
                                    if (task.getResult().size() != 0) {
                                        toastMes("Bạn chỉ có thể đánh giá 1 lần");
                                    } else {
                                        RateDialog rateDialog = new RateDialog(CourseActivity.this, null);
                                        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                                        rateDialog.setCancelable(false);
                                        rateDialog.show();
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }else{
//            db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.getResult().size() == 0) {
//                        toastMes("Bạn phải tham gia khoá học trước");
//                    } else {
//                        db.collection("Discussions")
//                                .whereEqualTo("course_id", key)
//                                .get()
//                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                                            for (DocumentSnapshot discussion : task.getResult()) {
//                                                db.collection("Discussions")
//                                                        .document(discussion.getId())
//                                                        .collection("DiscussionParticipants")
//                                                        .document(uid)
//                                                        .get()
//                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                if (task.isSuccessful() && task.getResult().exists()) {
//                                                                    isParticipating = true;
//                                                                }
//                                                                    if (isParticipating) {
//                                                                        Intent intent = new Intent(CourseActivity.this, DiscussionBoxActivity.class);
//                                                                        startActivity(intent);
//                                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                                                                    } else {
//                                                                        new AlertDialog.Builder(CourseActivity.this)
//                                                                                .setTitle("Tham gia thảo luận")
//                                                                                .setMessage("Bạn có muốn tham gia nhóm thảo luận của khóa học này không?")
//                                                                                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
//                                                                                    @Override
//                                                                                    public void onClick(DialogInterface dialog, int which) {
//                                                                                        FirebaseMessaging.getInstance().getToken()
//                                                                                                .addOnCompleteListener(new OnCompleteListener<String>() {
//                                                                                                    @Override
//                                                                                                    public void onComplete(@NonNull Task<String> task) {
//                                                                                                        if (!task.isSuccessful()) {
//                                                                                                            Log.w("FCM Token", "Fetching FCM registration token failed", task.getException());
//                                                                                                            return;
//                                                                                                        }
//
//                                                                                                        String token = task.getResult();
//                                                                                                        db.collection("Discussions")
//                                                                                                                .document(discussion.getId())
//                                                                                                                .collection("DiscussionParticipants")
//                                                                                                                .document(uid)
//                                                                                                                .set(new HashMap<String, Object>() {{
//                                                                                                                    put("userId", uid);
//                                                                                                                    put("userState", "out-topics");
//                                                                                                                    put("userMessagingToken", token);
//                                                                                                                    put("allowNotification", false);
//                                                                                                                }});
//                                                                                                    }
//                                                                                                });
//
//                                                                                        db.collection("Users")
//                                                                                                .document(uid)
//                                                                                                .collection("DiscussionGroups")
//                                                                                                .document(discussion.getId())
//                                                                                                .set(new HashMap<String, Object>() {{
//                                                                                                    put("discussion_id", discussion.getId());
//                                                                                                    put("join_time", FieldValue.serverTimestamp());
//                                                                                                }});
//                                                                                        toastMes("Bạn đã tham gia nhóm thảo luận");
//                                                                                        finish();
//                                                                                        Intent intent = new Intent(CourseActivity.this, DiscussionBoxActivity.class);
//                                                                                        startActivity(intent);
//                                                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                                                                                    }
//                                                                                })
//                                                                                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
//                                                                                    @Override
//                                                                                    public void onClick(DialogInterface dialog, int which) {
//                                                                                        dialog.dismiss();
//                                                                                    }
//                                                                                })
//                                                                                .show();
//                                                                    }
//
//                                                            }
//                                                        });
//                                            }
//                                        } else {
//                                            toastMes("Không tìm thấy thảo luận cho khóa học này");
//                                        }
//                                    }
//                                });
//                    }
//                }
//            });


        }
        return super.onOptionsItemSelected(item);
    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(CourseActivity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void mapping(){
        recyclerLearn = (RecyclerView) findViewById(R.id.recyclerLearn);
        recyclerReview = (RecyclerView) findViewById(R.id.recyclerReview);
        recyclerCourseType = (RecyclerView) findViewById(R.id.recyclerCourseType);
        courseCoor = (CoordinatorLayout) findViewById(R.id.courseCoor);

        constraintHeading6 = (ConstraintLayout) findViewById(R.id.heading6);
        constraintHeading2 = (ConstraintLayout) findViewById(R.id.heading2);
        constraintHeading3= (ConstraintLayout) findViewById(R.id.heading3);
        constraintHeading4 = (ConstraintLayout) findViewById(R.id.heading4);
        constraintDescription = (ConstraintLayout) findViewById(R.id.constraintDescription);

        linearHeading6 = (LinearLayout) findViewById(R.id.heading6_child);
        linearHeading6Child0 = (LinearLayout) findViewById(R.id.heading_6_child_0);
        linearHeading6Child1 = (LinearLayout) findViewById(R.id.heading6_child_1);
        linearHeading6Child2 = (LinearLayout) findViewById(R.id.heading6_child_2);
        linearHeading2 = (LinearLayout) findViewById(R.id.heading2_child);
        linearHeading3 = (LinearLayout) findViewById(R.id.heading3_child);
        linearHeading4 = (LinearLayout) findViewById(R.id.heading4_child);

//        imgArrow6 = (ImageView) findViewById(R.id.arrow6);
//        imgArrow1 = (ImageView) findViewById(R.id.arrow1);
        imgArrow4 = (ImageView) findViewById(R.id.arrow4);
        imgArrow3 = (ImageView) findViewById(R.id.arrow3);
        imgBack = (ImageView) findViewById(R.id.backAction);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbarLayout);

        textMember = (TextView) findViewById(R.id.textMember);
        textTime = (TextView) findViewById(R.id.textTime);
        textRate = (TextView) findViewById(R.id.textRate);
        textUpload = (TextView) findViewById(R.id.textUpload);
        textTeacher = (TextView) findViewById(R.id.textTeacher);
        txtSeeAll = (TextView) findViewById(R.id.btnSeeAll);
        txtState = (TextView) findViewById(R.id.textState);
        textTitle = (TextView) findViewById(R.id.titleCourse);
        txtProgress = (TextView) findViewById(R.id.textProgress);
        txtNone3 = (TextView) findViewById(R.id.txtNone3);
        txtExProgress = (TextView) findViewById(R.id.ex_progress_text);
        txtVideoProgress = (TextView) findViewById(R.id.video_progress_text);
        expandableTextView = (TextView) findViewById(R.id.expand);

        progressBar = (ProgressBar) findViewById(R.id.progressComplete);
        exProgress = (CircularProgressIndicator) findViewById(R.id.ex_progress);
        videoProgress = (CircularProgressIndicator) findViewById(R.id.video_progress);
        avatarTeacher = (ShapeableImageView) findViewById(R.id.avatarTeacher);

    }
    private void setGooglePayAvailable(boolean available) {
        db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (available) {
                        if(task.getResult().size() != 0)
                        {
//                            linearHeading6Child0.setVisibility(View.VISIBLE);
                            isOwn = true;
                        }
                        else{
                            txtState.setText("Chưa đăng ký");
                            txtState.setTextColor(getResources().getColor(R.color.red, null));
                            progressBar.setVisibility(View.GONE);
                            linearHeading6Child0.setVisibility(View.GONE);
                            txtProgress.setText("Bạn chưa đăng ký khoá học!");
                            isOwn = false;

                        }
                    } else {
                        googlePayButton.setVisibility(View.GONE);
//                        joinByPoints.setVisibility(View.GONE);
//                        linearPayMethod.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
    public void requestPayment(View view) {

        googlePayButton.setClickable(false);

        long dummyPriceCents = price;
        long shippingCostCents = 0;
        long totalPriceCents = dummyPriceCents + shippingCostCents;
        final Task<PaymentData> task = model.getLoadPaymentDataTask(totalPriceCents);

        task.addOnCompleteListener(completedTask -> {
            if (completedTask.isSuccessful()) {
                handlePaymentSuccess(completedTask.getResult());
            } else {
                Exception exception = completedTask.getException();
                if (exception instanceof ResolvableApiException) {
                    PendingIntent resolution = ((ResolvableApiException) exception).getResolution();
                    resolvePaymentForResult.launch(new IntentSenderRequest.Builder(resolution).build());

                } else if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    handleError(apiException.getStatusCode(), apiException.getMessage());

                } else {
                    handleError(CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                            " exception when trying to deliver the task result to an activity!");
                }
            }

            googlePayButton.setClickable(true);
        });
    }
    public void displayPayment(boolean isOwn, boolean isDisplay)
    {
        View dialog = getLayoutInflater().inflate(R.layout.dialog_payment, null);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(dialog);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        googlePayButton = dialog.findViewById(R.id.btnJoin);
        linearPayMethod = dialog.findViewById(R.id.payMethod);
        joinByPoints = dialog.findViewById(R.id.btnJoinByPoints);
        joinByZaloPay = dialog.findViewById(R.id.btnJoinZaloPay);
        btnJoin1 = dialog.findViewById(R.id.btnJoin1);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        txtPrice = dialog.findViewById(R.id.textViewPrice);
        points = dialog.findViewById(R.id.pointsPrice);
        try {
            googlePayButton.initialize(
                    ButtonOptions.newBuilder()
                            .setButtonTheme(ButtonConstants.ButtonTheme.LIGHT)
                            .setButtonType(ButtonConstants.ButtonType.CHECKOUT)
                            .setCornerRadius(8)
                            .setAllowedPaymentMethods(PaymentsUtil.getAllowedPaymentMethods().toString()).build()
            );
            googlePayButton.setOnClickListener(this::requestPayment);
        } catch (JSONException e) {
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        btnJoin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(price == 0)
                showPopup(); else toastMes("Lỗi do bug UI, đừng hãy thử lại");
            }
        });
        joinByZaloPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = db.collection("Courses").document(key);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isComplete())
                        try {
                            CreateOrder orderApi = new CreateOrder();
                            JSONObject data = orderApi.createOrder(String.valueOf(price), task.getResult().getString("course_title"));
                            String code = data.getString("return_code");
                            if (code.equals("1")) {
                                String token = data.getString("zp_trans_token");
                                ZaloPaySDK.getInstance().payOrder(CourseActivity.this, token, "demozpdk://app", new PayOrderListener() {
                                    @Override
                                    public void onPaymentSucceeded(String s, String s1, String s2) {
                                        if(mAuth.getCurrentUser() != null) {
                                            bottomSheetDialog.dismiss();
                                            toastMes("Thanh toán thành công");
                                            price = task.getResult().getLong("course_price");
                                            if(price != 0){
                                                addPoints(price/10000);
                                            }else{
                                                addPoints(0);
                                            }
                                            handleOrderSaving(0, "ZaloPay");
                                            handleOwnCourseSaving();
                                            DocumentReference documentReference = db.collection("Courses").document(key);
                                        }
                                    }

                                    @Override
                                    public void onPaymentCanceled(String s, String s1) {
                                        toastMes("Hủy thanh toán");
                                        handleOrderSaving(1, "ZaloPay");
                                        addPoints(0);
                                    }

                                    @Override
                                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                        toastMes("Thanh toán thất bại");
                                        handleOrderSaving(1, "ZaloPay");
                                        addPoints(0);
                                    }
                                });
                            }} catch (Exception e) {
                            toastMes("Có lỗi xảy ra");
                            handleOrderSaving(1, "ZaloPay");
                            addPoints(0);
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        joinByPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Thông báo thanh toán")
                        .setMessage("Bạn có chắc chắn muốn thanh toán bằng điểm không?")
                        .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which){
                                if(mAuth.getCurrentUser() != null) {
                                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isComplete()){
                                                if(task.getResult().getLong("user_points") >= Long.parseLong(String.valueOf(points.getText()))){
                                                    long balances = task.getResult().getLong("user_points") - Long.parseLong(String.valueOf(points.getText()));
                                                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).update(KEY_USER_POINTS, balances).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            bottomSheetDialog.dismiss();
                                                            handleOrderSaving(0, "Points");
                                                            handleOwnCourseSaving();
                                                            toastMes("Thanh toán thành công");
                                                        }
                                                    });
                                                }else{
                                                    toastMes("Bạn cần thêm " + (Long.parseLong(String.valueOf(points.getText())) - task.getResult().getLong("user_points")) + " điểm để sở hữu khóa học này" );
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        if(!key.isEmpty()){
            if(mAuth.getCurrentUser() != null) {
                DocumentReference documentReference = db.collection("Courses").document(key);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        price = value.getLong("course_price");
                        if(price != 0){
                            txtPrice.setText(changeCurrency(price));
                            linearPayMethod.setVisibility(View.VISIBLE);
                            points.setText(String.valueOf((int)(price * 2)/1000));
//                            googlePayButton.setVisibility(View.VISIBLE);
//                            joinByPoints.setVisibility(View.VISIBLE);
                            btnJoin1.setVisibility(View.GONE);
                        }else{
                            txtPrice.setText("Miễn phí");
//                            googlePayButton.setVisibility(View.GONE);
//                            joinByPoints.setVisibility(View.GONE);
                            linearPayMethod.setVisibility(View.GONE);
                            btnJoin1.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        }
        if(isOwn)
        {
            bottomSheetDialog.dismiss();
        }else{
            if(isDisplay == false)
            {
                bottomSheetDialog.show();
            }
            else{
                bottomSheetDialog.dismiss();
            }
        }
    }
    private void handlePaymentSuccess(PaymentData paymentData) {
        final String paymentInfo = paymentData.toJson();

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");

            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");

            Log.d("Google Pay token", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token"));
            toastMes("Cảm ơn " +billingName+" đã mua khoá học !");
            bottomSheetDialog.dismiss();
            if(mAuth.getCurrentUser() != null) {
                handleOrderSaving(0, "GooglePay");
                handleOwnCourseSaving();
                DocumentReference documentReference = db.collection("Courses").document(key);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isComplete()){
                            price = task.getResult().getLong("course_price");
                            if(price != 0){
                                addPoints(price/10000);
                            }else{
                                addPoints(0);
                            }
                        }
                    }
                });

            }
        } catch (JSONException e) {
            toastMes("Có lỗi xảy ra");
            handleOrderSaving(1, "GooglePay");
            addPoints(0);
            Log.e("handlePaymentSuccess", "Error: " + e);
        }
    }
    private void handleError(int statusCode, @Nullable String message) {
        handleOrderSaving(1, "GooglePay");
        toastMes(String.format(Locale.getDefault(), "Mã lỗi: %d, Lỗi44: %s", statusCode, message));
        Log.e("loadPaymentData failed",
                String.format(Locale.getDefault(), "Error code: %d, Message: %s", statusCode, message));
    }
    private void addPoints(long points){
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete())
                {
                    long currentPoints = task.getResult().getLong("user_points");
                    long newPoints = currentPoints + points;
                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).update(KEY_USER_POINTS, newPoints).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
            }
        });
    }
    private void handleOwnCourseSaving(){
        DocumentReference reference = db.collection("Users").document(uid).collection("OwnCourses").document();
        Map hashMap = new HashMap<>();
        hashMap.put(KEY_OWN_COURSE_ITEM_ID, key);
        hashMap.put(KEY_OWN_COURSE_ITEM_TYPE, "course");
        hashMap.put(KEY_OWN_COURSE_ID, reference.getId());
        hashMap.put(KEY_OWN_COURSE_ITEM_COMPLETE, false);
        hashMap.put(KEY_OWN_COURSE_ITEM_PROGRESS, "0.00");
        hashMap.put(KEY_OWN_COURSE_ITEM_VIDEO_PROGRESS, "0.00");
        hashMap.put(KEY_OWN_COURSE_ITEM_EX_PROGRESS, "0.00");
        hashMap.put(KEY_OWN_COURSE_DATE, FieldValue.serverTimestamp());
        reference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                member+=1;
                DocumentReference Ref = db.collection("Courses").document(key);
                Ref.update("course_member",member)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                recreate();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMes("Có lỗi xảy ra !");
            }
        });
    }

    private void handleOrderSaving(int status, String payMethod){
        DocumentReference refOrder = db.collection("Orders").document();
        Map hashMap = new HashMap<>();
        hashMap.put(KEY_ORDER_ID, refOrder.getId());
        hashMap.put(KEY_ORDER_ITEM_ID, key);
        hashMap.put(KEY_ORDER_DATE, FieldValue.serverTimestamp());
        hashMap.put(KEY_ORDER_PAY_METHOD, payMethod);
        hashMap.put(KEY_ORDER_AMOUNT, price);
        hashMap.put(KEY_ORDER_PAYER_ID, uid);
        hashMap.put(KEY_ORDER_STATUS, status);
        refOrder.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(CourseActivity.this, CheckoutSuccessActivity.class);
                intent.putExtra("order_id",refOrder.getId());
//                                intent.putExtra("user_name", payerName);
//                                intent.putExtra("c_id", key);
//                                intent.putExtra("c_status", true);
//                                intent.putExtra("c_title", toolbar.getTitle());
//                                intent.putExtra("c_owner", textTeacher.getText());
//                                String date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(new Date());
//                                intent.putExtra("c_date", date);
//                                intent.putExtra("bill_price",  String.valueOf(price));
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DocumentReference refOrder1 = db.collection("Orders").document(refOrder.getId());
                refOrder.update(KEY_ORDER_STATUS, 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        toastMes("Có lỗi xảy ra !");
                    }
                });
            }
        });

    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//
//        finish();
//        overridePendingTransition(0, 0);
//        startActivity(getIntent());
//        overridePendingTransition(0, 0);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(commentAdapter3 != null) commentAdapter3.release();
        if(headingAdapter != null) headingAdapter.release();
        if(adapterCategory != null) adapterCategory.release();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
package com.example.courseonline.Activity.Learner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Dialog.Learner.CommentDialog;
import com.example.courseonline.Dialog.Learner.RateDialog;
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
import com.google.android.gms.wallet.button.ButtonOptions;
import com.google.android.gms.wallet.button.PayButton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourseActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;
    public BottomSheetDialog bottomSheetDialog;
    CheckoutViewModel model;
    ConstraintLayout constraintDescription;
    public PayButton googlePayButton;
    public AppCompatButton btnJoin1;
    CoordinatorLayout courseCoor;
    ProgressBar progressBar;
    TextView textMember, textTime, textRate, textUpload, textTeacher, textTitle, txtProgress, txtSeeAll, txtNone3;
    ShapeableImageView avatarTeacher;
    ConstraintLayout constraintHeading6,constraintHeading2, constraintHeading3, constraintHeading4;
    ImageView  imgArrow1, imgArrow3 ,imgBack, imgArrow4, imgArrow6;
    LinearLayout linearHeading6, linearHeading2, linearHeading3, linearHeading4;
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
    public int shortAnimationDuration;
    private static final String KEY_UID = "user_id";
    private static final String KEY_CID = "course_id";
    private static final String KEY_CART_DATE = "cart_date";
    private static final String KEY_CART_ID = "cart_id";
    private static final String KEY_CART_ITEM_ID = "cart_item_id";
    private static final String KEY_CART_ITEM_COMPLETE = "cart_complete";
    private static final String KEY_CART_ITEM_PROGRESS= "cart_progress";
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
        initGoogle();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        LoadingAlert alert = new LoadingAlert(CourseActivity.this);
        alert.startLoading();
        executor.execute(new Runnable() {
            @Override
            public void run() {

                courseCoor.setVisibility(View.GONE);
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                uid =  mAuth.getCurrentUser().getUid();

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
                loadData();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
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
                                    textTitle.animate().alpha(0.0f).setDuration(100);
                                    constraintDescription.animate().setDuration(80);
                                    displayPayment(isOwn, isShow);
                                    isShow = true;
                                } else if(isShow) {
                                    textTitle.animate().alpha(1.0f).setDuration(100);
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
                        constraintHeading6.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(linearHeading6.getVisibility() == View.VISIBLE)
                                {
                                    AutoTransition autoTransition = new AutoTransition();
                                    autoTransition.setDuration(30);
                                    TransitionManager.beginDelayedTransition(constraintHeading6, autoTransition);
                                    linearHeading6.setVisibility(View.GONE);
                                    imgArrow6.setImageResource(R.drawable.arrow_down_blue);
                                }else{
                                    AutoTransition autoTransition = new AutoTransition();
                                    autoTransition.setDuration(30);
                                    TransitionManager.beginDelayedTransition(constraintHeading6, autoTransition);
                                    linearHeading6.setVisibility(View.VISIBLE);
                                    imgArrow6.setImageResource(R.drawable.arrow_up);
                                }

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
                        constraintHeading2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(linearHeading2.getVisibility() == View.VISIBLE)
                                {
                                    AutoTransition autoTransition = new AutoTransition();
                                    autoTransition.setDuration(30);
                                    TransitionManager.beginDelayedTransition(constraintHeading2, autoTransition);
                                    linearHeading2.setVisibility(View.GONE);
                                    imgArrow1.setImageResource(R.drawable.arrow_down_blue);
                                }else{
                                    AutoTransition autoTransition = new AutoTransition();
                                    autoTransition.setDuration(30);
                                    TransitionManager.beginDelayedTransition(constraintHeading2, autoTransition);
                                    linearHeading2.setVisibility(View.VISIBLE);
                                    imgArrow1.setImageResource(R.drawable.arrow_up);
                                }
                            }
                        });
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
                }, 1000);

            }
        });
    }
    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(CourseActivity.this);
        alert.setMessage("Bạn có chắc chắn đăng kí tham gia khoá học ?")
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        if(mAuth.getCurrentUser() != null) {
                            DocumentReference reference = db.collection("Users").document(uid).collection("cart").document();
                            Map<Object, String> hashMap = new HashMap<>();
                            hashMap.put(KEY_CART_ITEM_ID, key);
                            hashMap.put(KEY_CART_ID, reference.getId());
                            reference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    member += 1;
                                    DocumentReference Ref = db.collection("Courses").document(key);
                                    Ref.update("course_member",member)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
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
                            DocumentReference ref= db.collection("Users").document(uid).collection("cart").document(reference.getId());
                            Map map = new HashMap<>();
                            map.put(KEY_CART_ITEM_COMPLETE, false);
                            map.put(KEY_CART_ITEM_PROGRESS, "0");
                            map.put(KEY_CART_DATE, FieldValue.serverTimestamp());
                            ref.update(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    onRestart();
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
    private void loadData(){
        adapterCategory = new TypeAdapter(arrayCategory, 2);
        headingAdapter = new HeadingAdapter(arrayHeading);
        commentAdapter3 = new CommentAdapter(arrayTopComment);
        recyclerLearn.setAdapter(headingAdapter);
        recyclerCourseType.setAdapter(adapterCategory);

        if(!key.isEmpty()){
            if(mAuth.getCurrentUser() != null) {
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
                        textTeacher.setText(value.getString("course_owner"));
                        price = value.getLong("course_price");

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
                        String owner = value.getString("course_owner_id");
                        if (owner != "") {
                         db.collection("Users").document(owner).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                             @Override
                             public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                 if(error != null)
                                 {
                                     return;
                                 }
                                 String url = value.getString("user_avatar");
                                 try {
                                     Picasso.get().load(url).resize(100, 100).centerCrop().into(avatarTeacher);
                                 } catch (Exception e) {
                                     Picasso.get().load(R.drawable.profile).resize(100, 100).centerCrop().into(avatarTeacher);
                                 }
                             }
                         });
                        } else {
                            Picasso.get().load(R.drawable.profile).resize(100, 100).centerCrop().into(avatarTeacher);
                        }

                    }
                });
                db.collection("Users").document(uid).collection("cart").whereEqualTo("cart_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getBoolean("cart_complete") == false) {
                                    txtState.setText("Đang học");
                                    txtState.setTextColor(getResources().getColor(R.color.blue, null));
                                } else {
                                    txtState.setText("Đã hoàn thành");
                                    txtState.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
                                }
                                txtProgress.setText("Đã hoàn thành: " + document.getString("cart_progress") + "%");
                                progressBar.setProgress((int)Float.parseFloat(document.getString("cart_progress")));
                            }
                        } else {
                            toastMes("Lỗi");
                        }
                    }
                });
                db.collection("Courses").document(key).collection("Heading").orderBy("heading_order", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                           // Log.d("aaaa", error.toString());
                            return;
                        }
                        if (value.size() != 0) {
                            arrayHeading.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.toObject(VideoClass.class) != null) {
                                    arrayHeading.add(doc.toObject(HeadingClass.class));
                                }
                                headingAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
                db.collection("Courses").document(key).collection("Comment").orderBy("comment_like", Query.Direction.DESCENDING).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            commentAdapter3.notifyDataSetChanged();
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
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.toObject(TypeClass.class) != null) {
                                arrayCategory.add(doc.toObject(TypeClass.class));
                            }
                            adapterCategory.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
        recyclerReview.setAdapter(commentAdapter3);
    };
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
        int nightModeFlags =
                getApplicationContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
        {
            MenuItem item = menu.findItem(R.id.course_rate);
            SpannableString s = new SpannableString("Đánh giá khoá học");
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            item.setTitle(s);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.course_favourite)
        {
            if(mAuth.getCurrentUser() != null)
            {
                db.collection("Users").document(uid).collection("Favourites").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().getData() != null){
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
                        }else{
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
        }
        else if(item.getItemId() == R.id.course_rate)
        {
            db.collection("Users").document(uid).collection("cart").whereEqualTo("cart_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().size() == 0)
                    {
                        toastMes("Bạn phải tham gia khoá học trước");
                    }else{
                        db.collection("Courses").document(key).collection("Comment").whereEqualTo("user_id", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isComplete()){
                                    if(task.getResult().size() !=0){
                                        toastMes("Bạn chỉ có thể đánh giá 1 lần");
                                    }
                                    else{
                                        RateDialog rateDialog = new RateDialog(CourseActivity.this,null);
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
        linearHeading2 = (LinearLayout) findViewById(R.id.heading2_child);
        linearHeading3 = (LinearLayout) findViewById(R.id.heading3_child);
        linearHeading4 = (LinearLayout) findViewById(R.id.heading4_child);
        expandableTextView = (TextView) findViewById(R.id.expand);

        imgArrow6 = (ImageView) findViewById(R.id.arrow6);
        imgArrow4 = (ImageView) findViewById(R.id.arrow4);
        imgArrow1 = (ImageView) findViewById(R.id.arrow1);
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

        progressBar = (ProgressBar) findViewById(R.id.progressComplete);
        avatarTeacher = (ShapeableImageView) findViewById(R.id.avatarTeacher);

    }
    private void setGooglePayAvailable(boolean available) {
        db.collection("Users").document(uid).collection("cart").whereEqualTo("cart_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (available) {
                        if(task.getResult().size() != 0)
                        {
                            isOwn = true;
                        }
                        else{
                            txtState.setText("Chưa sở hữu");
                            txtState.setTextColor(getResources().getColor(R.color.red, null));
                            progressBar.setVisibility(View.GONE);
                            txtProgress.setText("Bạn chưa mua khoá học!");
                            isOwn = false;

                        }
                    } else {
                        googlePayButton.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
    public void requestPayment(View view) {

        googlePayButton.setClickable(false);

        long dummyPriceCents = price;
        long shippingCostCents = 30000;
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
        btnJoin1 = dialog.findViewById(R.id.btnJoin1);
        txtPrice = dialog.findViewById(R.id.txtPrice);
        try {
            googlePayButton.initialize(
                    ButtonOptions.newBuilder()
                            .setAllowedPaymentMethods(PaymentsUtil.getAllowedPaymentMethods().toString()).build()
            );
            googlePayButton.setOnClickListener(this::requestPayment);
        } catch (JSONException e) {
        }
        btnJoin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
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
                            googlePayButton.setVisibility(View.VISIBLE);
                            btnJoin1.setVisibility(View.GONE);
                        }else{
                            txtPrice.setText("Miễn phí");
                            googlePayButton.setVisibility(View.GONE);
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
            toastMes("Cảm ơn bạn " +billingName+" đã mua khoá học !");
            if(mAuth.getCurrentUser() != null) {
                DocumentReference reference = db.collection("Users").document(uid).collection("cart").document();
                Map<Object, String> hashMap = new HashMap<>();
                hashMap.put(KEY_CART_ITEM_ID, key);
                hashMap.put(KEY_CART_ID, reference.getId());
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
                                        Intent intent = new Intent(CourseActivity.this, CheckoutSuccessActivity.class);
                                        intent.putExtra("bill_id",reference.getId());
                                        intent.putExtra("user_name", billingName);
                                        intent.putExtra("c_id", key);
                                        intent.putExtra("c_title", toolbar.getTitle());
                                        intent.putExtra("c_owner", textTeacher.getText());
                                        String date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(new Date());
                                        intent.putExtra("c_date", date);
                                        intent.putExtra("bill_price",  txtPrice.getText());
                                        startActivity(intent);
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
                DocumentReference ref= db.collection("Users").document(uid).collection("cart").document(reference.getId());
                Map map = new HashMap<>();
                map.put(KEY_CART_ITEM_COMPLETE, false);
                map.put(KEY_CART_ITEM_PROGRESS, "0");
                map.put(KEY_CART_DATE, FieldValue.serverTimestamp());
                ref.update(map).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {}
                });
            }
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e);
        }
    }
    private void handleError(int statusCode, @Nullable String message) {
        Log.e("loadPaymentData failed",
                String.format(Locale.getDefault(), "Error code: %d, Message: %s", statusCode, message));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
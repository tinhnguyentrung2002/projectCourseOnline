package com.example.courseonline.Activity.Learner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.Adapter.Learner.ClassResultAdapter;
import com.example.courseonline.Adapter.Learner.CourseDisplayAdapter;
import com.example.courseonline.Adapter.Learner.HeadingAdapter;
import com.example.courseonline.Adapter.Learner.TypeAdapter;
import com.example.courseonline.Adapter.Learner.UserListClassAdapter;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Dialog.Learner.TeacherDialog;
import com.example.courseonline.Domain.CourseDisplayClass;
import com.example.courseonline.Domain.HeadingClass;
import com.example.courseonline.Domain.TypeClass;
import com.example.courseonline.Domain.UserClass;
import com.example.courseonline.Domain.VideoClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;
    ConstraintLayout constraintDescription;
    CoordinatorLayout courseCoor;
    AlertDialog alertDialog;
    ProgressBar progressBar;
    CircularProgressIndicator videoProgress, exProgress;
    TextView textMember,textUpload, textTeacher, textTitle, txtProgress, txtNone3, txtVideoProgress, txtExProgress, txtNoneUserList;
    ShapeableImageView avatarTeacher;
    ConstraintLayout constraintHeading6,constraintHeading2, constraintHeading3, constraintHeading4;
    LinearLayout linearHeading6, linearHeading2, linearHeading3, linearHeading4, linearHeading6Child0, linearHeading6Child1, linearHeading6Child2;
    ImageView imgArrow3 ,imgBack, imgArrow4;
    TextView expandableTextView,txtState;
    RecyclerView recyclerLearn, recyclerRecommend, recyclerViewUserList, recyclerClassType;
    private UserListClassAdapter userAdapter;
    private ArrayList<UserClass> userList = new ArrayList<>();
    ArrayList<TypeClass> arrayCategory = new ArrayList<>();
    private FirebaseFirestore db;
    private TypeAdapter adapterCategory;
    private HeadingAdapter headingAdapter;
    private CourseDisplayAdapter recommendAdapter3;
    private ArrayList<HeadingClass> arrayHeading = new ArrayList<>();
    private ArrayList<CourseDisplayClass> arrayRecommend = new ArrayList<>();
    private FirebaseAuth mAuth;
    private String key, uid;
    private String teacherID;
    private boolean isParticipating = false;
    public int shortAnimationDuration;
    Toast toast;
    private int totalQuiz = 0;
    private int checkedQuiz = 0;
    public  int totalVideo = 0;
    private int checkedVideo = 0;
    int totalTasks = 0;
    int completedTasks = 0;
    boolean[] video = {false};
    boolean[] quiz = {false};
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private static final String KEY_OWN_COURSE_ITEM_PROGRESS= "own_course_progress";
    private static final String KEY_OWN_COURSE_ITEM_VIDEO_PROGRESS= "own_course_video_progress";
    private static final String KEY_OWN_COURSE_ITEM_EX_PROGRESS= "own_course_ex_progress";
    private static final String KEY_COMPLETE_COURSE = "own_course_complete";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        key = getIntent().getStringExtra("course_key");
        mapping();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid =  mAuth.getCurrentUser().getUid();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        LoadingAlert alert = new LoadingAlert(ClassActivity.this);

        alert.startLoading();
        courseCoor.setVisibility(View.GONE);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                recyclerClassType.setLayoutManager(new LinearLayoutManager(ClassActivity.this,LinearLayoutManager.HORIZONTAL,false));
                recyclerClassType.setHasFixedSize(true);
                recyclerClassType.setItemViewCacheSize(20);

                recyclerLearn.setLayoutManager(new LinearLayoutManager(ClassActivity.this,LinearLayoutManager.VERTICAL,false));
                recyclerLearn.setHasFixedSize(true);
                recyclerLearn.setItemViewCacheSize(20);

                recyclerRecommend.setLayoutManager(new LinearLayoutManager(ClassActivity.this,LinearLayoutManager.HORIZONTAL,false));
                recyclerRecommend.setHasFixedSize(true);
                recyclerRecommend.setItemViewCacheSize(20);

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
                        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayoutClass);
                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = true;
                            int scrollRange = -1;
                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                if (scrollRange == -1) {
                                    scrollRange = appBarLayout.getTotalScrollRange();
                                }
                                if (scrollRange + verticalOffset == 0) {
                                    constraintDescription.animate().setDuration(80);
                                    isShow = true;
                                } else if(isShow) {
                                    constraintDescription.animate().setDuration(80);
                                    isShow = false;
                                }
                            }
                        });
                        linearHeading2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TeacherDialog teacherDialog = new TeacherDialog(ClassActivity.this, teacherID);
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
                }, 800);

            }
        });
    }
    private void loadData(){
        adapterCategory = new TypeAdapter(arrayCategory, ClassActivity.this);
        headingAdapter = new HeadingAdapter(arrayHeading, ClassActivity.this);
        recommendAdapter3 = new CourseDisplayAdapter(arrayRecommend,ClassActivity.this);
        recyclerLearn.setAdapter(headingAdapter);
        recyclerRecommend.setAdapter(recommendAdapter3);
        recyclerClassType.setAdapter(adapterCategory);
        if(key != null){
            if(mAuth.getCurrentUser() != null) {
                updateProgressCourse();
                db.collection("Courses").document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        List<String> courseMemberList = (List<String>) value.get("course_member");
                        if (courseMemberList != null) {
                            int memberCount = courseMemberList.size();

                            textMember.setText("Thành viên: " + changeValue(memberCount));
                        } else {
                            textMember.setText("0");
                        }
                        toolbar.setTitle(value.getString("course_title"));
                        textTitle.setText(value.getString("course_title"));

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
                        if (teacherID != null) {
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
                                        Picasso.get()
                                                .load(url)
                                                .placeholder(R.drawable.default_image)
                                                .error(R.drawable.default_image)
                                                .fit()
                                                .centerInside()
                                                .into(avatarTeacher);
                                    }
                                }
                            });
                        }else{
                            Picasso.get().load(R.drawable.profile).resize(100, 100).centerInside().into(avatarTeacher);
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
                db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getBoolean("own_course_complete") == false) {
                                    txtState.setText("Đang học");
                                    txtState.setTextColor(getResources().getColor(R.color.blue, null));
                                } else {
                                    txtState.setText("Đã hoàn thành");
                                    txtState.setTextColor(getResources().getColor(R.color.dark_green, null));
                                }

                                txtProgress.setText("Tiến độ:" + document.getString("own_course_progress") + "%");
                                txtExProgress.setText(document.getString("own_course_ex_progress") + "%");
                                txtVideoProgress.setText(document.getString("own_course_video_progress") + "%");

                                progressBar.setProgress((int)Float.parseFloat(document.getString("own_course_progress")));
                                videoProgress.setProgress((int)Float.parseFloat(document.getString("own_course_video_progress")));
                                exProgress.setProgress((int)Float.parseFloat(document.getString("own_course_ex_progress")));
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
                db.collection("Courses").document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        arrayRecommend.clear();
                        if (value.exists()) {
                            txtNone3.setVisibility(View.GONE);
                            List<String> arrayCourseID = (List<String>) value.get("course_recommend");
                            if(arrayCourseID.size() != 0)
                            {
                                db.collection("Courses").whereIn("course_id", arrayCourseID).whereEqualTo("course_type","course").whereEqualTo("course_state", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(error != null) {
                                            return;
                                        }
                                        arrayRecommend.clear();
                                        for(QueryDocumentSnapshot doc : value){
                                            arrayRecommend.add(doc.toObject(CourseDisplayClass.class));
                                        }
                                    }
                                });
                            }
                        }else{
                            txtNone3.setVisibility(View.VISIBLE);
                        }
                        recommendAdapter3.notifyDataSetChanged();
                    }
                });

            }
        }

    };
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
                                                map.put(KEY_COMPLETE_COURSE, true);
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
                                                map.put(KEY_COMPLETE_COURSE, false);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class, menu);
        int nightModeFlags =
                getApplicationContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
        {
            MenuItem item = menu.findItem(R.id.course_result);
            SpannableString s = new SpannableString("Kết quả học tập");
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            item.setTitle(s);

            MenuItem item2 = menu.findItem(R.id.course_list_member);
            SpannableString s2 = new SpannableString("Danh sách thành viên");
            s2.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s2.length(), 0);
            item2.setTitle(s2);
        }
        MenuItem item3 = menu.findItem(R.id.course_leave);
        SpannableString s3 = new SpannableString("Rời khỏi lớp");
        s3.setSpan(new ForegroundColorSpan(Color.RED), 0, s3.length(), 0);
        item3.setTitle(s3);
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.course_discussion) {
            db.collection("Discussions")
                    .whereEqualTo("course_id", key)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (DocumentSnapshot discussion : task.getResult()) {
                                    db.collection("Discussions")
                                            .document(discussion.getId())
                                            .collection("DiscussionParticipants")
                                            .document(uid)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful() && task.getResult().exists()) {
                                                        isParticipating = true;
                                                    }
                                                    if (isParticipating) {
                                                        Intent intent = new Intent(ClassActivity.this, DiscussionBoxActivity.class);
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                    } else {
                                                        new AlertDialog.Builder(ClassActivity.this)
                                                                .setTitle("Tham gia thảo luận")
                                                                .setMessage("Bạn có muốn tham gia nhóm thảo luận của khóa học này không?")
                                                                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        FirebaseMessaging.getInstance().getToken()
                                                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<String> task) {
                                                                                        if (!task.isSuccessful()) {
                                                                                            Log.w("FCM Token", "Fetching FCM registration token failed", task.getException());
                                                                                            return;
                                                                                        }

                                                                                        String token = task.getResult();
                                                                                        db.collection("Discussions")
                                                                                                .document(discussion.getId())
                                                                                                .collection("DiscussionParticipants")
                                                                                                .document(uid)
                                                                                                .set(new HashMap<String, Object>() {{
                                                                                                    put("userId", uid);
                                                                                                    put("userState", "out-topics");
                                                                                                    put("userMessagingToken", token);
                                                                                                    put("allowNotification", false);
                                                                                                }});
                                                                                    }
                                                                                });

                                                                        db.collection("Users")
                                                                                .document(uid)
                                                                                .collection("DiscussionGroups")
                                                                                .document(discussion.getId())
                                                                                .set(new HashMap<String, Object>() {{
                                                                                    put("discussion_id", discussion.getId());
                                                                                    put("join_time", FieldValue.serverTimestamp());
                                                                                }});
                                                                        toastMes("Bạn đã tham gia nhóm thảo luận");
                                                                        finish();
                                                                        Intent intent = new Intent(ClassActivity.this, DiscussionBoxActivity.class);
                                                                        startActivity(intent);
                                                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                                    }
                                                                })
                                                                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                })
                                                                .show();
                                                    }

                                                }
                                            });
                                }
                            } else {
                                toastMes("Không tìm thấy nhóm thảo luận cho khóa học này");
                            }
                        }
                    });

        }else if(item.getItemId() == R.id.course_list_member){
            showUserDialog();
        }else if(item.getItemId() == R.id.course_result){
            showResultDialog();
        }else{
            leaveClass(mAuth.getCurrentUser().getUid());
        }
        return super.onOptionsItemSelected(item);
    }
    private void leaveClass(String uid){
        AlertDialog.Builder alert = new AlertDialog.Builder(ClassActivity.this);
        alert.setMessage("Bạn có chắc chắn muốn rời khỏi lớp học này?")
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        if(mAuth.getCurrentUser() != null){
                            DocumentReference classRef = db.collection("Courses").document(key);
                            classRef.update("course_member", FieldValue.arrayRemove(uid))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection("Users").document(uid).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isComplete()){
                                                        for(QueryDocumentSnapshot doc : task.getResult()){
                                                            DocumentReference reference =   db.collection("Users").document(uid).collection("OwnCourses").document(doc.getId());
                                                            reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    toastMes("Bạn đã rời khỏi lớp học");
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });


                        }
                    }
                }).setNegativeButton("Không", null);

        AlertDialog alert1 = alert.create();
        alert1.show();

    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(ClassActivity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void showUserDialog() {
        userList = new ArrayList<>();


        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_list, null);

        TextView totalMembersTextView = dialogView.findViewById(R.id.txtTotalMembers);


        txtNoneUserList = dialogView.findViewById(R.id.textNoneUserList);
        recyclerViewUserList = dialogView.findViewById(R.id.recyclerViewUserList);
        recyclerViewUserList .setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserListClassAdapter(this, userList, key);
        recyclerViewUserList.setAdapter(userAdapter);
        if(mAuth.getCurrentUser().getUid() != null){
            db.collection("Courses").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isComplete())
                    {
                        List<String> courseMemberList = (List<String>) task.getResult().get("course_member");
                        totalMembersTextView.setText("Tổng số thành viên: " + courseMemberList.size());
                        if(courseMemberList.size() != 0)
                        {
                            db.collection("Users").whereIn("user_uid", courseMemberList).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isComplete()){
                                        for(QueryDocumentSnapshot doc : task.getResult()){
                                            userList.add(doc.toObject(UserClass.class));
                                        }
                                        userAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            txtNoneUserList.setVisibility(View.GONE);
                        }else{
                            txtNoneUserList.setVisibility(View.VISIBLE);
                        }

                    }
                }
            });
        }
        ImageView closeButton = dialogView.findViewById(R.id.btnCloseDialog);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setAttributes(params);


    }
    private void showResultDialog() {
        List<String> headerID= new ArrayList<>();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_learning_result, null);
        ClassResultAdapter resultAdapter;
        ShapeableImageView avatar = dialogView.findViewById(R.id.imgAvatar_result);
        ImageView btnCloseResultDialog = dialogView.findViewById(R.id.btnCloseResultDialog);
        TextView txtProfileNameResult = dialogView.findViewById(R.id.txtProfileName_result);
        TextView txtProfileEmailResult = dialogView.findViewById(R.id.txtProfileEmail_result);
        TextView txtProfileUIDResult = dialogView.findViewById(R.id.txtProfileUID_result);
        TextView generalProgressResult = dialogView.findViewById(R.id.generalProgress_result);
        TextView videoProgressResult = dialogView.findViewById(R.id.videoProgress_result);
        TextView quizProgressResult = dialogView.findViewById(R.id.quizProgress_result);
        TextView classTitleResult = dialogView.findViewById(R.id.classTitle_result);
        RecyclerView recyclerDetailsResult = dialogView.findViewById(R.id.recyclerDetails_result);

        recyclerDetailsResult.setLayoutManager(new LinearLayoutManager(ClassActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerDetailsResult.setHasFixedSize(true);
        recyclerDetailsResult.setItemViewCacheSize(20);
        classTitleResult.setText(textTitle.getText().toString());
        if(mAuth.getCurrentUser() != null){
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isComplete()){
                        Picasso.get()
                                .load(task.getResult().getString("user_avatar"))
                                .placeholder(R.drawable.default_image)
                                .error(R.drawable.default_image)
                                .fit()
                                .centerInside()
                                .into(avatar);
                        txtProfileNameResult.setText(task.getResult().getString("user_name"));
                        txtProfileEmailResult.setText(task.getResult().getString("user_email"));
                        txtProfileUIDResult.setText(task.getResult().getString("user_uid"));
                    }
                }
            });
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("OwnCourses").whereEqualTo("own_course_item_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isComplete()){
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            if(doc.getString("own_course_progress") != "")
                            {
                                float general = Float.parseFloat(doc.getString("own_course_progress"))/2;
                                generalProgressResult.setText(general + "%");
                            }
                            else{
                                generalProgressResult.setText(doc.getString("own_course_progress") + "%");
                            }
                            generalProgressResult.setText(doc.getString("own_course_progress") + "%");
                            videoProgressResult.setText(doc.getString("own_course_video_progress")+ "%");
                            quizProgressResult.setText(doc.getString("own_course_ex_progress")+ "%");
                        }
                    }
                }
            });
            db.collection("Courses").document(key).collection("Heading").orderBy("heading_order", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("SuspiciousIndentation")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isComplete()){
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            if(!headerID.contains(doc.getString("heading_id")))
                            headerID.add(doc.getString("heading_id"));
                        }
                        if(headerID.size() != 0){
                            ClassResultAdapter resultAdapter = new ClassResultAdapter(headerID, key, ClassActivity.this);
                            recyclerDetailsResult.setAdapter(resultAdapter);
                            resultAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

        btnCloseResultDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setAttributes(params);


    }

    private void mapping(){
        recyclerLearn = (RecyclerView) findViewById(R.id.recyclerClassLearn);
        recyclerRecommend = (RecyclerView) findViewById(R.id.recyclerCourseRecommend);
        courseCoor = (CoordinatorLayout) findViewById(R.id.classCoor);
        recyclerClassType = (RecyclerView) findViewById(R.id.recyclerClassType);

        constraintHeading6 = (ConstraintLayout) findViewById(R.id.heading6Class);
        constraintHeading2 = (ConstraintLayout) findViewById(R.id.heading2Class);
        constraintHeading3= (ConstraintLayout) findViewById(R.id.heading3Class);
        constraintHeading4 = (ConstraintLayout) findViewById(R.id.heading4Class);
        constraintDescription = (ConstraintLayout) findViewById(R.id.constraintDescriptionClass);

        linearHeading6 = (LinearLayout) findViewById(R.id.heading6_child_class);
        linearHeading6Child0 = (LinearLayout) findViewById(R.id.heading_6_child_0_class);
        linearHeading6Child1 = (LinearLayout) findViewById(R.id.heading6_child_1_class);
        linearHeading6Child2 = (LinearLayout) findViewById(R.id.heading6_child_2_class);
        linearHeading2 = (LinearLayout) findViewById(R.id.heading2_child_class);
        linearHeading3 = (LinearLayout) findViewById(R.id.heading3_child_class);
        linearHeading4 = (LinearLayout) findViewById(R.id.heading4_child_class);

        imgArrow4 = (ImageView) findViewById(R.id.arrow4Class);
        imgArrow3 = (ImageView) findViewById(R.id.arrow3Class);
        imgBack = (ImageView) findViewById(R.id.backActionClass);

        toolbar = (Toolbar)findViewById(R.id.toolbarClass);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbarLayoutClass1);

        textMember = (TextView) findViewById(R.id.textMemberClass);
        textUpload = (TextView) findViewById(R.id.textUploadClass);
        textTeacher = (TextView) findViewById(R.id.textClassTeacher);
        txtState = (TextView) findViewById(R.id.textLearningState);
        textTitle = (TextView) findViewById(R.id.titleClass);
        txtProgress = (TextView) findViewById(R.id.textClassProgress);
        txtNone3 = (TextView) findViewById(R.id.txtNone3Class);
        txtExProgress = (TextView) findViewById(R.id.ex_progress_text_class);
        txtVideoProgress = (TextView) findViewById(R.id.video_progress_text_class);
        expandableTextView = (TextView) findViewById(R.id.expandClass);

        progressBar = (ProgressBar) findViewById(R.id.progressCompleteClass);
        exProgress = (CircularProgressIndicator) findViewById(R.id.ex_progress_class);
        videoProgress = (CircularProgressIndicator) findViewById(R.id.video_progress_class);
        avatarTeacher = (ShapeableImageView) findViewById(R.id.avatarClassTeacher);

    }
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
    }
}
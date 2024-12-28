package com.example.courseonline.Activity.Teacher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.courseonline.Activity.Learner.DiscussionBoxActivity;
import com.example.courseonline.Activity.Learner.SettingsActivity;
import com.example.courseonline.Activity.LoginActivity;
import com.example.courseonline.Adapter.Teacher.ViewPage2TeacherAdapter;
import com.example.courseonline.Domain.TopicClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashBoardTeacherActivity extends AppCompatActivity {
        private FirebaseAuth mAuth;
        private FirebaseFirestore db;
        ViewPager2 viewPager2;
        ShapeableImageView imgTeacher;
        BottomNavigationView bottomBar;
        AppCompatImageButton  btnQuestionBox;
        TextView txtToolBar, txtUnreadCount;
        AppBarLayout appBarLayout;
        FloatingActionButton fab;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dash_board_teacher);
            db = FirebaseFirestore.getInstance();
            mapping();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    mAuth = FirebaseAuth.getInstance();
                    appBarLayout.setVisibility(View.VISIBLE);
                    txtToolBar.setText("Tổng quan");
                    imgTeacher.setVisibility(View.VISIBLE);
                    btnQuestionBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DashBoardTeacherActivity.this, DiscussionBoxActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    });
//                    getUnreadDiscussions();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ViewPage2TeacherAdapter viewPage2Adapter = new ViewPage2TeacherAdapter(DashBoardTeacherActivity.this);
                            viewPager2.setAdapter(viewPage2Adapter);
                            viewPager2.setOffscreenPageLimit(2);
                            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                @Override
                                public void onPageSelected(int position) {
                                    super.onPageSelected(position);
                                    switch (position){
                                        case 0 :
                                            bottomBar.getMenu().findItem(R.id.dashboard_teacher).setChecked(true);
                                            //appBarLayout.setVisibility(View.VISIBLE);
                                            txtToolBar.setText("Tổng quan");
                                            if(mAuth.getCurrentUser() != null)
                                            {
                                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        if(error != null){
                                                            return;
                                                        }
                                                        RequestOptions options = new RequestOptions()
                                                                .placeholder(R.drawable.user)
                                                                .error(R.drawable.user)
                                                                .centerInside();

                                                        Glide.with(DashBoardTeacherActivity.this)
                                                                .load(value.getString("user_avatar"))
                                                                .apply(options)
                                                                .into(imgTeacher);
//                                                        Picasso.get().load(value.getString("user_avatar")).resize(45,45).centerCrop().into(imgTeacher);
                                                    }
                                                });
                                            }
                                            break;
                                        case 1 :
                                            bottomBar.getMenu().findItem(R.id.search_teacher).setChecked(true);
                                            txtToolBar.setText("Tìm kiếm");
                                            if(mAuth.getCurrentUser() != null)
                                            {
                                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        if(error != null){
                                                            return;
                                                        }
                                                        RequestOptions options = new RequestOptions()
                                                                .placeholder(R.drawable.user)
                                                                .error(R.drawable.user)
                                                                .centerInside();

                                                        Glide.with(DashBoardTeacherActivity.this)
                                                                .load(value.getString("user_avatar"))
                                                                .apply(options)
                                                                .into(imgTeacher);
                                                    }
                                                });
                                            }
                                            break;
//                                        case 2 :
//                                            bottomBar.getMenu().findItem(R.id.analytics_teacher).setChecked(true);
//                                            //appBarLayout.setVisibility(View.VISIBLE);
//                                            txtToolBar.setText("Khoá học đang học");
//                                            if(mAuth.getCurrentUser() != null)
//                                            {
//                                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                                    @Override
//                                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                                                        if(error != null){
//                                                            return;
//                                                        }
//                                                        RequestOptions options = new RequestOptions()
//                                                                .placeholder(R.drawable.user)
//                                                                .error(R.drawable.user)
//                                                                .centerInside();
//
//                                                        Glide.with(DashBoardTeacherActivity.this)
//                                                                .load(value.getString("user_avatar"))
//                                                                .apply(options)
//                                                                .into(imgTeacher);
//                                                    }
//                                                });
//                                            }
//                                            break;
                                        case 3:
                                            bottomBar.getMenu().findItem(R.id.profile_teacher).setChecked(true);
                                            //appBarLayout.setVisibility(View.VISIBLE);
                                            txtToolBar.setText("Hồ sơ cá nhân");
                                            imgTeacher.setImageDrawable(getResources().getDrawable(R.drawable.baseline_settings_24,null));
                                            break;
                                    }
                                }
                            });
                            if(mAuth.getCurrentUser() != null)
                            {
                                db.collection("Users").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(error != null){
                                            return;
                                        }
                                        RequestOptions options = new RequestOptions()
                                                .placeholder(R.drawable.user)
                                                .error(R.drawable.user)
                                                .centerInside();

                                        Glide.with(DashBoardTeacherActivity.this)
                                                .load(value.getString("user_avatar"))
                                                .apply(options)
                                                .into(imgTeacher);
                                    }
                                });
                            }
                            imgTeacher.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(bottomBar.getSelectedItemId() == R.id.profile_teacher){
                                        Intent intent =new Intent(DashBoardTeacherActivity.this, SettingsActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    }
                                    else{
                                        bottomBar.setSelectedItemId(R.id.profile_teacher);
                                    }
                                }
                            });
                           bottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                                @Override
                                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                    if(item.getItemId() == R.id.dashboard_teacher)
                                    {
                                        viewPager2.setCurrentItem(0);
                                        txtToolBar.setText("Khám phá");
                                    }
                                    else if(item.getItemId() == R.id.search_teacher)
                                    {
                                        viewPager2.setCurrentItem(1);
                                        txtToolBar.setText("Tìm kiếm");
                                    }
//                                    else if(item.getItemId() == R.id.analytics_teacher)
//                                    {
//                                        viewPager2.setCurrentItem(2);
//                                        txtToolBar.setText("Khoá học đang học");
//                                    }
                                    else{
                                        viewPager2.setCurrentItem(3);
                                        txtToolBar.setText("Hồ sơ cá nhân");
                                    }
                                    return false;
                                }
                            });
//                           fab.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                   Intent intent = new Intent(DashBoardTeacherActivity.this, UploadStep1Activity.class);
//                                   intent.putExtra("key_type","create");
//                                   overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//                                   startActivity(intent);
//                                }
//                            });
                        }

                    });
                }
            });

        }
    private void getUnreadDiscussions(){
        if(mAuth.getCurrentUser() != null)
        {
            db.collection("Discussions")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot discussionsValue, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                return;
                            }

                            if (discussionsValue != null) {
                                final int[] totalUnreadCount = {0};

                                for (DocumentSnapshot discussionSnapshot : discussionsValue.getDocuments()) {
                                    String discussionId = discussionSnapshot.getId();

                                    db.collection("Discussions")
                                            .document(discussionId)
                                            .collection("Topics").whereEqualTo("topic_state", true)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful() && task.getResult() != null) {
                                                        int unreadCount = 0;

                                                        for (DocumentSnapshot topicSnapshot : task.getResult().getDocuments()) {
                                                            TopicClass topic = topicSnapshot.toObject(TopicClass.class);
                                                            List<String> topicSeen = topic.getTopic_seen();
                                                            if (topicSeen == null || !topicSeen.contains(mAuth.getCurrentUser().getUid())) {
                                                                unreadCount++;
                                                            }
                                                        }

                                                        totalUnreadCount[0] += unreadCount;

                                                        if (discussionSnapshot.equals(discussionsValue.getDocuments().get(discussionsValue.size() - 1))) {
                                                            if (totalUnreadCount[0] > 0) {
                                                                txtUnreadCount.setVisibility(View.VISIBLE);
                                                                txtUnreadCount.setText(String.valueOf(totalUnreadCount[0]));
                                                            } else {
                                                                txtUnreadCount.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
        }
    }
        private void mapping(){
            viewPager2 = (ViewPager2) findViewById(R.id.viewpager2);
            bottomBar = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
            txtToolBar = (TextView) findViewById(R.id.txtToolbar);
            appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
//            fab = (FloatingActionButton) findViewById(R.id.fab);
            imgTeacher = (ShapeableImageView) findViewById(R.id.imgTeacher);
            btnQuestionBox = (AppCompatImageButton) findViewById(R.id.btnQuestionBoxTeacher);
            txtUnreadCount = (TextView) findViewById(R.id.unreadCountDashboardTeacher);

        }
        private void userStatus(){
            FirebaseUser user = mAuth.getCurrentUser();
            if(user != null){

            }
            else{
                Intent intent = new Intent(DashBoardTeacherActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected void onStart() {
            //userStatus();
            super.onStart();
        }
        boolean doubleBackToExitPressedOnce = false;

        @Override
        public void onBackPressed() {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finishAffinity();
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Back thêm 1 lần để thoát !", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 3000);
        }

}
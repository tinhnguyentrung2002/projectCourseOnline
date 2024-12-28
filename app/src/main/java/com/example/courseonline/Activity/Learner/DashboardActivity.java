package com.example.courseonline.Activity.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.courseonline.Activity.IntroActivity;
import com.example.courseonline.Activity.LoginActivity;
import com.example.courseonline.Activity.Teacher.DashBoardTeacherActivity;
import com.example.courseonline.Adapter.Learner.ViewPage2Adapter;
import com.example.courseonline.Domain.TopicClass;
import com.example.courseonline.Domain.UserClass;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    BottomNavigationView bottomBar;
    TextView txtToolBar, txtUnreadCount;
    AppBarLayout appBarLayout;
    ViewPager2 viewPager2;
    AppCompatImageButton btnSearch, btnQuestionBox;
    ViewPage2Adapter viewPage2Adapter;
    List<UserClass> userList = new ArrayList<>();
    private final static String KEY_RANK_OLD_POSITION= "rank_old_position";
//    private final static String uniqueWorkName = "updateLeaderboardWork";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mapping();
        if(mAuth.getCurrentUser() != null) updateBestPoints();
        appBarLayout.setVisibility(View.VISIBLE);
        txtToolBar.setText("Khám phá");
        btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
        viewPage2Adapter = new ViewPage2Adapter(DashboardActivity.this);
        viewPager2.setAdapter(viewPage2Adapter);
        viewPager2.setOffscreenPageLimit(5);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    switch (position){
                        case 0 :
                            bottomBar.getMenu().findItem(R.id.tab_home).setChecked(true);
                            //appBarLayout.setVisibility(View.VISIBLE);
                            txtToolBar.setText("Khám phá");
                            btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                            btnSearch.setTooltipText("Tìm kiếm");
                            break;
                        case 1 :
                            bottomBar.getMenu().findItem(R.id.tab_discover).setChecked(true);
                            btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                            txtToolBar.setText("Tìm kiếm");
                            break;
                        case 2 :
                            bottomBar.getMenu().findItem(R.id.tab_learning).setChecked(true);
                            //appBarLayout.setVisibility(View.VISIBLE);
                            txtToolBar.setText("Đang học");
                            btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                            btnSearch.setTooltipText("Tìm kiếm");
                            break;
                        case 3 :
                            bottomBar.getMenu().findItem(R.id.tab_leaderboard).setChecked(true);
                            //appBarLayout.setVisibility(View.VISIBLE);
                            txtToolBar.setText("Xếp hạng");
                            btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                            btnSearch.setTooltipText("Tìm kiếm");
                            break;
                        case 4 :
                            bottomBar.getMenu().findItem(R.id.tab_person).setChecked(true);
                            //appBarLayout.setVisibility(View.VISIBLE);
                            txtToolBar.setText("Hồ sơ cá nhân");
                            btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_settings_24, null));
                            btnSearch.setTooltipText("Thiết lập");
                            break;
                    }
                }
            });
            bottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId() == R.id.tab_home)
                    {
                        viewPager2.setCurrentItem(0);
                        txtToolBar.setText("Khám phá");
                        btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                    }
                    else if(item.getItemId() == R.id.tab_discover)
                    {
                        viewPager2.setCurrentItem(1);
                        txtToolBar.setText("Tìm kiếm");
                        btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                    }else if(item.getItemId() == R.id.tab_learning)
                    {
                        viewPager2.setCurrentItem(2);
                        txtToolBar.setText("Đang học");
                        btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                    }else if(item.getItemId() == R.id.tab_leaderboard){
                        viewPager2.setCurrentItem(3);
                        txtToolBar.setText("Xếp hạng");
                        btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                    }else{
                        viewPager2.setCurrentItem(4);
                        txtToolBar.setText("Cá nhân");
                        btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_settings_24, null));
                    }
                    return false;
                }

            });
            btnQuestionBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, DiscussionBoxActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
            getUnreadDiscussions();
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bottomBar.getSelectedItemId() == R.id.tab_person){
                        Intent intent =new Intent(DashboardActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    else{
                        bottomBar.setSelectedItemId(R.id.tab_discover);
                        //setFragment(fragment2);
                        //appBarLayout.setVisibility(View.GONE);
                    }

                }
            });
//      }


    }

    private void mapping(){
        viewPager2 = (ViewPager2) findViewById(R.id.viewpager2);
        bottomBar = (BottomNavigationView) findViewById(R.id.bottomBar);
        txtToolBar = (TextView) findViewById(R.id.txtToolbar);
        btnSearch = (AppCompatImageButton) findViewById(R.id.btnActionSearch);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        btnQuestionBox = (AppCompatImageButton) findViewById(R.id.btnQuestionBox);
        txtUnreadCount = (TextView) findViewById(R.id.unreadCountDashboard);

    }
//    private void scheduleLeaderboardUpdate() {
//        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
//        long initialDelay = calculateInitialDelay();
//        PeriodicWorkRequest leaderboardUpdateRequest =
//                new PeriodicWorkRequest.Builder(UpdateLeaderboardWorker.class, 10, TimeUnit.DAYS)
//                        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
//                        .addTag("leaderboardUpdateWork")
//                        .build();
//
//        workManager.enqueueUniquePeriodicWork(
//                "leaderboardUpdateWork",
//                ExistingPeriodicWorkPolicy.REPLACE,
//                leaderboardUpdateRequest
//        );
//
//        // Kiểm tra công việc hiện tại
//    }
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
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
    private void getRegisterGrade(){
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getString("user_grade") == "") {
                        Intent intent = new Intent(DashboardActivity.this, RegisterGradeActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
    private void getUserPermission(){
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().getString("user_account_state") != null)
                    {
                        if (task.getResult().getString("user_account_state").equals("ban")) {
                            Toast.makeText(DashboardActivity.this,"Tài khoản của bạn đã bị cấm. liên hệ admin@gmail.com",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                            if(mAuth.getCurrentUser() != null)   mAuth.signOut();
                            startActivity(intent);
                            finishAffinity();
                            return;
                        }
                    }
                    if (task.getResult().getString("user_permission").contains("2")) {
                        Intent intent = new Intent(DashboardActivity.this, DashBoardTeacherActivity.class);
                        startActivity(intent);
                    }
                    else{
                        if(mAuth.getCurrentUser() != null)
                        {
                            getRegisterGrade();
                            getLoginStreak();
                        }

                    }

                }
            }
        });
    }
    private void updateBestPoints() {
        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) return;

                        if (value != null && value.exists()) {
                            Long userPoints = value.getLong("user_points");
                            Long userBestPoints = value.getLong("user_best_points");
                            if (userPoints != null && userBestPoints != null && userPoints > userBestPoints) {
                                db.collection("Users").document(mAuth.getCurrentUser().getUid())
                                        .update("user_best_points", userPoints)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Cập nhật thành công
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Xử lý lỗi
                                            }
                                        });
                            }
                        }
                    }
                });
    }

//    private long calculateInitialDelay() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//
//        // Đặt giờ, phút, giây và mili giây cho 0 giờ
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//
//        // Nếu hiện tại đã qua 0 giờ, thêm một ngày
//        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
//            calendar.add(Calendar.DAY_OF_YEAR, 1);
//        }
//
//        return 960000;
//    }
    private void reloadRank(){
        db.collection("Users").whereEqualTo("user_permission", "1").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    UserClass user = document.toObject(UserClass.class);
                    userList.add(user);
                }

                Collections.sort(userList, new Comparator<UserClass>() {
                    @Override
                    public int compare(UserClass u1, UserClass u2) {
                        return Long.compare(u2.getUser_best_points(), u1.getUser_best_points());
                    }
                });

                // Cập nhật bảng xếp hạng
                for (int i = 0; i < userList.size(); i++) {
                    UserClass user = userList.get(i);
                    if(mAuth.getCurrentUser().getUid().equals(user.getUser_uid()))
                    {
//                        int rank = i + 1;
                        DocumentReference rankingRef = db.collection("Rank").document(user.getUser_uid());
                        Map map = new HashMap();

                        map.put(KEY_RANK_OLD_POSITION, i+1);
                        rankingRef.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("RankingsUpdate", "Updated ranking for user: " + user.getUser_uid());

                            }
                        });
                        break;
                    }

                }
            }
        }).addOnFailureListener(e -> {
            // Xử lý lỗi
            e.printStackTrace();
        });
    }
    private void getLoginStreak() {
        DocumentReference userRef = db.collection("Users").document(mAuth.getUid());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Timestamp now = Timestamp.now();

                long streak = 0;
                long score = 0;
                long addScore = 0;

                if (document.exists()) {
                    Timestamp lastLogin = document.getTimestamp("user_lastLogin");
                    streak = document.getLong("user_streakLogin") != null ? document.getLong("user_streakLogin") : 0;
                    score = document.getLong("user_points") != null ? document.getLong("user_points") : 0;

                    LocalDate lastLoginDate = lastLogin != null
                            ? lastLogin.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            : null;
                    LocalDate today = now.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    if (lastLoginDate == null || !today.isEqual(lastLoginDate)) {
                        reloadRank();
                        if (lastLoginDate != null && today.minusDays(1).isEqual(lastLoginDate)) {
                            streak++;
                        } else {
                            streak = 1;
                        }

                        // Calculate score based on streak
                        if (streak <= 10) {
                            score += 1;
                            addScore += 1;
                        } else if (streak <= 20) {
                            score += 2;
                            addScore += 2;
                        } else if (streak <= 30) {
                            score += 3;
                            addScore += 3;
                        } else {
                            score += 5;
                            addScore += 5;
                        }

                        updateUserStats(userRef, now, streak, score);
                        startDailyLoginActivity(streak, addScore);
                    }
                } else {
                    streak = 1;
                    score = 1;
                    addScore = 1;
                    updateUserStats(userRef, now, streak, score);
                    startDailyLoginActivity(streak, addScore);
                }
            }
        });
    }

    private void updateUserStats(DocumentReference userRef, Timestamp now, long streak, long score) {
        userRef.update("user_lastLogin", now);
        userRef.update("user_streakLogin", streak);
        userRef.update("user_points", score);
    }

    private void startDailyLoginActivity(long streak, long addScore) {
        Intent intent = new Intent(DashboardActivity.this, DailyLoginActivity.class);
        intent.putExtra("streak_login", streak);
        intent.putExtra("score_login", addScore);
        startActivity(intent);
    }
    private void userStatus(){
        if(mAuth.getCurrentUser() != null) {
            getUserPermission();
        }
        else{
            Intent intent = new Intent(DashboardActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onStart() {
        userStatus();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        getUnreadDiscussions();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        getUnreadDiscussions();
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
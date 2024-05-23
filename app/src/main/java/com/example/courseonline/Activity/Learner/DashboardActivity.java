package com.example.courseonline.Activity.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.courseonline.Activity.LoginActivity;
import com.example.courseonline.Activity.Teacher.DashBoardTeacherActivity;
import com.example.courseonline.Adapter.Learner.ViewPage2Adapter;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    BottomNavigationView bottomBar;
    TextView txtToolBar;
    AppBarLayout appBarLayout;
    ViewPager2 viewPager2;
    AppCompatImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        db = FirebaseFirestore.getInstance();
        mapping();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mAuth = FirebaseAuth.getInstance();
                appBarLayout.setVisibility(View.VISIBLE);
                txtToolBar.setText("Khám phá");
                btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewPage2Adapter viewPage2Adapter = new ViewPage2Adapter(DashboardActivity.this);
                        viewPager2.setAdapter(viewPage2Adapter);
                        viewPager2.setOffscreenPageLimit(1);
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
                                        txtToolBar.setText("Tìm kiếm");
                                        break;
                                    case 2 :
                                        bottomBar.getMenu().findItem(R.id.tab_learning).setChecked(true);
                                        //appBarLayout.setVisibility(View.VISIBLE);
                                        txtToolBar.setText("Khoá học đang học");
                                        btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                                        btnSearch.setTooltipText("Tìm kiếm");
                                        break;
                                    case 3 :
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
                                    btnSearch.setVisibility(View.VISIBLE);
                                    btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                                }
                                else if(item.getItemId() == R.id.tab_discover)
                                {
                                    viewPager2.setCurrentItem(1);
                                    txtToolBar.setText("Tìm kiếm");
                                    btnSearch.setVisibility(View.GONE);
                                }else if(item.getItemId() == R.id.tab_learning)
                                {
                                    viewPager2.setCurrentItem(2);
                                    txtToolBar.setText("Khoá học đang học");
                                    btnSearch.setVisibility(View.VISIBLE);
                                    btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_search_24, null));
                                }else{
                                    viewPager2.setCurrentItem(3);
                                    txtToolBar.setText("Hồ sơ cá nhân");
                                    btnSearch.setVisibility(View.VISIBLE);
                                    btnSearch.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_settings_24, null));
                                }
                                return false;
                            }
                        });
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
                    }
                });
            }
        });

    }
    private void mapping(){
        viewPager2 = (ViewPager2) findViewById(R.id.viewpager2);
        bottomBar = (BottomNavigationView) findViewById(R.id.bottomBar);
        txtToolBar = (TextView) findViewById(R.id.txtToolbar);
        btnSearch = (AppCompatImageButton) findViewById(R.id.btnActionSearch);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

    }
    private void userStatus(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            db.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getString("user_permission").contains("2")) {
                            Intent intent = new Intent(DashboardActivity.this, DashBoardTeacherActivity.class);
                            startActivity(intent);
                        }
                        else{
                        }


                    }
                }
            });
        }
        else{
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onStart() {
        userStatus();
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
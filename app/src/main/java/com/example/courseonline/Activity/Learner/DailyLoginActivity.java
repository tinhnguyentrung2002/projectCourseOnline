package com.example.courseonline.Activity.Learner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.courseonline.R;

public class DailyLoginActivity extends AppCompatActivity {

    private TextView txtStreak, txtScore;
    private ImageView icon_points;
    private AppCompatButton btnStreakContinue;

    private long streak, score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_login);

        // Gọi hàm ánh xạ các view
        mapping();

        // Lấy giá trị từ Intent
        streak = getIntent().getLongExtra("streak_login", 1);
        score = getIntent().getLongExtra("score_login", 1);
        // Bắt đầu hoạt ảnh cho streak
        animateStreak(streak - 1, streak);

        // Đặt sự kiện click cho nút
        btnStreakContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void animateStreak(long start, long end) {
        ValueAnimator animator = ValueAnimator.ofInt((int) start, (int) end);
        animator.setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                txtStreak.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                txtScore.setText("Bạn đã nhận được " + score +" ");
                fadeInTextView(txtScore, icon_points);
            }
        });
        animator.start();
    }

    private void fadeInTextView(final TextView textView, ImageView imageView) {
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        textView.setAlpha(0f);
        imageView.setAlpha(0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.start();
        ObjectAnimator fadeIn1 = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
        fadeIn1.setDuration(1000);
        fadeIn1.start();
    }

    private void mapping() {
        txtScore = findViewById(R.id.textViewScore);
        txtStreak = findViewById(R.id.textViewStreak);
        icon_points = findViewById(R.id.icon_points);
        btnStreakContinue = findViewById(R.id.buttonContinueStreak);
    }
}

package com.example.courseonline.Activity.Learner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.courseonline.Adapter.Learner.TimeSwitchAdapter;
import com.example.courseonline.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmManager extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TimeSwitchAdapter adapter;
    private List<String> timeList;
    private AppCompatImageButton btnBack;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mapping();

        timeList = new ArrayList<>();
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

        checkNotificationPermission();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void mapping() {
        recyclerView = findViewById(R.id.recycler_Alarm);
        btnBack = findViewById(R.id.btnBackAlarm);
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
            } else {
                initAdapter();
            }
        } else {
            initAdapter();
        }
    }

    private void initAdapter() {
        adapter = new TimeSwitchAdapter(timeList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initAdapter();
            } else {
                Toast.makeText(this, "Quyền thông báo bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

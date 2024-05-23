package com.example.courseonline.Activity.Learner;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;

import com.example.courseonline.Class.Alarm;
import com.example.courseonline.R;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
public class AlarmActivity extends AppCompatActivity {
    private MaterialTimePicker timePicker;
    private Calendar calendar;
    AppCompatButton  btn1, btn2, btn3;
    AppCompatImageButton btnBack;
    private TextView txtAlarm;
    private  Toast toast;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private  SharedPreferences sharedPref;
    private boolean selected= false;
    private int check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        createNotificationChannel();
        mapping();
        sharedPref = AlarmActivity.this.getPreferences(Context.MODE_PRIVATE);
        String time = sharedPref.getString("alarm","Chọn thời gian");
        check = sharedPref.getInt("check",0);
        txtAlarm.setText(time);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Đặt thời gian")
                        .build();
                timePicker.show(getSupportFragmentManager(), "courseonline");
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(check != 0 )
                        {
                            toastMes("Vui lòng xoá lời nhắc cũ, chỉ được đặt một lời nhắc");
                        }
                        else{
                            if (timePicker.getHour() > 12){
                                txtAlarm.setText(
                                        String.format("%02d",(timePicker.getHour()-12)) +":"+ String.format("%02d", timePicker.getMinute())+" PM"
                                );
                            } else {
                                txtAlarm.setText(timePicker.getHour()+":" + timePicker.getMinute()+ " AM");
                            }
                            selected = true;
                            calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                            calendar.set(Calendar.MINUTE, timePicker.getMinute());
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                        }
                        }

                });
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check != 0 )
                {
                    toastMes("Vui lòng xoá lời nhắc cũ, chỉ được đặt một lời nhắc");
                }
                else{
                    if(selected)
                    {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ActivityCompat.checkSelfPermission(AlarmActivity.this,android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                                requestPermissions(new String[] { android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                            }
                            else {

                            }
                            sharedPref = AlarmActivity.this.getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("alarm", String.valueOf(txtAlarm.getText()));
                            editor.putInt("check", 1);
                            editor.apply();
                            check = 1;
                            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(AlarmActivity.this, Alarm.class);

                            pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR,pendingIntent);
                            toastMes("Đã đặt lời nhắc");
                        }
                    }else{
                        toastMes("Vui lòng chọn thời gian");
                    }
                }

            }

        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    sharedPref = AlarmActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("alarm", "Chọn thời gian");
                    editor.putInt("check", 0);
                    editor.apply();
                    txtAlarm.setText("Chọn thời gian");
                    check = 0;
                    Intent intent = new Intent(AlarmActivity.this, Alarm.class);
                    pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                    if (alarmManager == null) {
                        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    }
                    alarmManager.cancel(pendingIntent);
                    toastMes("Đã xoá lời nhắc");

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Nhắc nhở";
            String desc = "Tới giờ học";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("courseonline", name, imp);
            channel.setDescription(desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private  void  mapping(){
        btn1 = (AppCompatButton) findViewById(R.id.button);
        btn2 = (AppCompatButton) findViewById(R.id.button2);
        btn3 = (AppCompatButton) findViewById(R.id.button3);
        btnBack = (AppCompatImageButton) findViewById(R.id.backAlarm);

        txtAlarm = (TextView) findViewById(R.id.textAlarm);

    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(AlarmActivity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
}
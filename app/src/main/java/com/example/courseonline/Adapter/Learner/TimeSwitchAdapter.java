package com.example.courseonline.Adapter.Learner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseonline.R;
import com.example.courseonline.util.AlarmReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSwitchAdapter extends RecyclerView.Adapter<TimeSwitchAdapter.TimeViewHolder> {
    private List<String> timeList;
    private Context context;
    private static FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static String uid;
    private static final String KEY_ALARM_ACTIVE = "alarm_active";

    public TimeSwitchAdapter(List<String> timeList, Context context) {
        this.timeList = timeList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_item_time_switch, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        String time = timeList.get(position);
        holder.timeText.setText(time);

        db.collection("Users").document(uid).collection("Alarm").document(time).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (value != null && value.exists()) {
                    Boolean isActive = value.getBoolean(KEY_ALARM_ACTIVE);
                    holder.alarmSwitch.setChecked(isActive != null && isActive);
                }
            }
        });

        holder.alarmSwitch.setOnCheckedChangeListener(null);
        holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setAlarm(context, time);
                } else {
                    cancelAlarm(time);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeList.size();
    }

    class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        SwitchCompat alarmSwitch;

        TimeViewHolder(View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.time_text);
            alarmSwitch = itemView.findViewById(R.id.alarm_switch);
        }
    }

    public static void setAlarm(Context context, String time) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = time.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        String[] splitTime = time.split(":");
        int hour = Integer.parseInt(splitTime[0]);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
        DocumentReference documentReference = db.collection("Users").document(uid).collection("Alarm").document(time);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_ALARM_ACTIVE, true);
        documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                }
            }
        });
    }

    private void cancelAlarm(String time) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = time.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        DocumentReference documentReference = db.collection("Users").document(uid).collection("Alarm").document(time);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_ALARM_ACTIVE, false);
        documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                }
            }
        });
    }
}

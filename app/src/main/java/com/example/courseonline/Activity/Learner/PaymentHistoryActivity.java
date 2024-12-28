package com.example.courseonline.Activity.Learner;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.example.courseonline.Adapter.Learner.PaymentHistoryAdapter;
import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.Domain.PaymentHistoryClass;
import com.example.courseonline.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentHistoryActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private Toast toast;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<PaymentHistoryClass> historyArray = new ArrayList<>();
    private PaymentHistoryAdapter historyAdapter;
    private AppCompatImageButton btnBack;
    private AppCompatButton btnAll, btnDate;
    private RecyclerView recyclerViewPH;
    private TextView txtNone3;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mapping();
        btnAll.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.button_filter_selected));
        btnAll.setTextColor(ContextCompat.getColor(this, R.color.white));
        LoadingAlert alert = new LoadingAlert(PaymentHistoryActivity.this);
        alert.startLoading();

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                PaymentHistoryActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setAccentColor(R.color.blue);
        dpd.setThemeDark(true);
        dpd.setStartTitle("Từ");
        dpd.setEndTitle("Đến");
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
                int startMonth = monthOfYear + 1 ;
                int endMonth = monthOfYearEnd + 1 ;
                String startDate = dayOfMonth+"/"+ startMonth +"/"+year;
                String endDate = dayOfMonthEnd+"/"+ endMonth +"/"+yearEnd;

                try {
                    Date strDate = sdf.parse(startDate);
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(yearEnd, monthOfYearEnd, dayOfMonthEnd, 0, 0, 0);
                    endCalendar.set(Calendar.MILLISECOND, 0);

                    // Thêm một ngày và trừ 1 giây
                    endCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    endCalendar.add(Calendar.SECOND, -1);
                    Date eDate = endCalendar.getTime();
                    if (eDate.getTime() > strDate.getTime()) {
                        btnDate.setText(startDate + " -> " + endDate);
                        btnDate.setBackgroundDrawable(ContextCompat.getDrawable(PaymentHistoryActivity.this,R.drawable.button_filter_selected));
                        btnDate.setTextColor(ContextCompat.getColor(PaymentHistoryActivity.this,R.color.white));
                        btnAll.setBackgroundDrawable(ContextCompat.getDrawable(PaymentHistoryActivity.this,R.drawable.button_filter));
                        btnAll.setTextColor(ContextCompat.getColor(PaymentHistoryActivity.this,R.color.black));
                        loadByFilter(strDate, eDate);
                    }else if(eDate.getTime() == strDate.getTime() || eDate.getTime() < strDate.getTime()){
                        toastMes("Lựa chọn không hợp lệ, hãy chọn lại!");
                    }

                } catch (DateTimeParseException e) {

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if(mAuth.getCurrentUser() != null)
                {
                    loadData();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        historyAdapter = new PaymentHistoryAdapter(historyArray, getApplicationContext());
                        recyclerViewPH.setLayoutManager(new LinearLayoutManager(PaymentHistoryActivity.this,LinearLayoutManager.VERTICAL,false));
                        recyclerViewPH.setHasFixedSize(true);
                        recyclerViewPH.setItemViewCacheSize(20);
                        recyclerViewPH.setAdapter(historyAdapter);
                        btnBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                        btnDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dpd.show(getFragmentManager(), "Datepickerdialog");
                            }
                        });
                        btnAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toastMes("Hiện tất cả");
                                loadData();
                                btnDate.setText("Lọc theo ngày");
                                btnAll.setBackgroundDrawable(ContextCompat.getDrawable(PaymentHistoryActivity.this,R.drawable.button_filter_selected));
                                btnAll.setTextColor(ContextCompat.getColor(PaymentHistoryActivity.this,R.color.white));
                                btnDate.setBackgroundDrawable(ContextCompat.getDrawable(PaymentHistoryActivity.this,R.drawable.button_filter));
                                btnDate.setTextColor(ContextCompat.getColor(PaymentHistoryActivity.this,R.color.black));
                            }
                        });
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alert.closeLoading();
                    }
                }, 300);

            }
        });
    }
    private void loadData(){
        db.collection("Orders").whereEqualTo("order_payer_id", mAuth.getCurrentUser().getUid()).orderBy("order_date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }
                historyArray.clear();
                if(value.size() != 0)
                {
                    txtNone3.setVisibility(View.GONE);
                    for(QueryDocumentSnapshot doc : value)
                    {
                        historyArray.add(doc.toObject(PaymentHistoryClass.class));
                    }
                }else{
                    txtNone3.setVisibility(View.VISIBLE);
                }
                historyAdapter.notifyDataSetChanged();
            }
        });
    }
    private void loadByFilter(Date startDate, Date endDate){
        if (startDate != null && endDate != null) {
            Timestamp timestamp = new Timestamp(startDate);
            Timestamp timestamp2 = new Timestamp(endDate);
            Log.d("dateeCk", startDate + " - " + endDate);

            db.collection("Orders").whereEqualTo("order_payer_id", mAuth.getCurrentUser().getUid()).orderBy("order_date", Query.Direction.DESCENDING).whereGreaterThanOrEqualTo("order_date", timestamp)
                    .whereLessThanOrEqualTo("order_date", timestamp2).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null)
                    {
                        return;
                    }
                    historyArray.clear();
                    if(value.size() != 0)
                    {
                        txtNone3.setVisibility(View.GONE);
                        for(QueryDocumentSnapshot doc : value)
                        {
                            historyArray.add(doc.toObject(PaymentHistoryClass.class));
                        }
                    }else{
                        txtNone3.setVisibility(View.VISIBLE);
                    }
                    historyAdapter.notifyDataSetChanged();
                }
            });
        }

    }
    private void toastMes(String mes){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(PaymentHistoryActivity.this, mes,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void mapping(){
        btnBack = (AppCompatImageButton) findViewById(R.id.btnPHBack);
        recyclerViewPH = (RecyclerView) findViewById(R.id.RecyclerPH);
        txtNone3 = (TextView) findViewById(R.id.txtNone3);
        btnAll = (AppCompatButton) findViewById(R.id.button_all);
        btnDate = (AppCompatButton) findViewById(R.id.button_date_range);
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
    }
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(historyAdapter != null) historyAdapter.release();
    }
}
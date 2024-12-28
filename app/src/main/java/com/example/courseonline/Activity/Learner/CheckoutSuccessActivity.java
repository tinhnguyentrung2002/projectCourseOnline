package com.example.courseonline.Activity.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Locale;

public class CheckoutSuccessActivity extends AppCompatActivity {

    AppCompatButton btnFinish;
    FirebaseFirestore db;
    ImageView imgSuccess, imgPayMethod;
    TextView txtBillId, txtCTitle, txtDate, txtPrice, txtStatus, txtPayMethod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_success);
        db = FirebaseFirestore.getInstance();
        LoadingAlert alert = new LoadingAlert(CheckoutSuccessActivity.this);
        alert.startLoading();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                alert.closeLoading();
            }
        }, 400);
        mapping();
        loadBill(getIntent());
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void loadBill(Intent intent){
        String orderId = intent.getStringExtra("order_id");
//      String name = intent.getStringExtra("user_name");
//      String cid = intent.getStringExtra("c_id");
//      String cTitle = intent.getStringExtra("c_title");
//      String cOwner = intent.getStringExtra("c_owner");
//      String bill_date = intent.getStringExtra("c_date");
//      String price = intent.getStringExtra("bill_price");
        db.collection("Orders").document(orderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task != null)
                {
                    if(task.getResult().getDouble("order_status") == 0){
                        imgSuccess.setImageDrawable(ContextCompat.getDrawable(CheckoutSuccessActivity.this, R.drawable.checked_large));
                        txtStatus.setText("Thanh toán thành công");
                    }else{
                        imgSuccess.setImageDrawable(ContextCompat.getDrawable(CheckoutSuccessActivity.this, R.drawable.error));
                        txtStatus.setText("Thanh toán thất bại");
                    }
                    if(task.getResult().getString("order_pay_method").equals("GooglePay")){
                        txtPayMethod.setText("Phương thức thanh toán: Google Pay");
                        imgPayMethod.setImageResource(R.drawable.google_pay_icon);
                        if(task.getResult().getLong("order_status") == 0)
                        {
                            txtPrice.setText("Số tiền thanh toán: " + changeCurrency(task.getResult().getDouble("order_amount")) + " (+ " + task.getResult().getDouble("order_amount")/10000 + " ⭐)" );
                        }else{
                            txtPrice.setText("Số tiền thanh toán: " + changeCurrency(task.getResult().getDouble("order_amount")) + " (+ " + 0 + " ⭐)");
                        }

                    }else if(task.getResult().getString("order_pay_method").equals("ZaloPay")){
                        txtPayMethod.setText("Phương thức thanh toán: Zalo Pay");
                        imgPayMethod.setImageResource(R.drawable.icon_zalo_square);
                        if(task.getResult().getLong("order_status") == 0)
                        {
                            txtPrice.setText("Số tiền thanh toán: " + changeCurrency(task.getResult().getDouble("order_amount")) + " (+ " + task.getResult().getDouble("order_amount")/10000 + " ⭐)" );
                        }else{
                            txtPrice.setText("Số tiền thanh toán: " + changeCurrency(task.getResult().getDouble("order_amount")) + " (+ " + 0 + " ⭐)");
                        }
                    }
                    else {
                        txtPayMethod.setText("Phương thức thanh toán: Điểm (⭐)");

                        txtPrice.setText("Số tiền thanh toán: " +(int)(task.getResult().getDouble("order_amount")*2)/1000 + " ⭐"  + "~" +changeCurrency(task.getResult().getDouble("order_amount")));
                    }

                    txtBillId.setText("Order ID: #" + task.getResult().getId());
                    db.collection("Courses").document(task.getResult().getString("order_item_id")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task != null){
                                txtCTitle.setText("Tên khoá học: " + task.getResult().getString("course_title") + " - "+ task.getResult().getString("course_owner") );
                            }
                        }
                    });
                    String date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(task.getResult().getDate("order_date"));
                    txtDate.setText(date);
                }
            }
        });


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
    private void mapping(){
        btnFinish = (AppCompatButton) findViewById(R.id.buttonFinish);
        txtBillId = (TextView) findViewById(R.id.txtBillID);
        txtCTitle = (TextView) findViewById(R.id.txtCourseTitle);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtPayMethod = (TextView) findViewById(R.id.txtPayMethod);
        imgSuccess = (ImageView) findViewById(R.id.success_image);
        imgPayMethod = (ImageView) findViewById(R.id.icon_payMethod);
        txtStatus = (TextView) findViewById(R.id.payment_success_description);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
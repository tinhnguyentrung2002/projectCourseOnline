package com.example.courseonline.Activity.Learner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.R;

public class CheckoutSuccessActivity extends AppCompatActivity {

    AppCompatButton btnFinish;
    TextView txtBillId, txtName, txtCID, txtCTitle,txtDate, txtPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_success);
        LoadingAlert alert = new LoadingAlert(CheckoutSuccessActivity.this);
        alert.startLoading();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                alert.closeLoading();
            }
        }, 1000);
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
        String billId = intent.getStringExtra("bill_id");
        String name = intent.getStringExtra("user_name");
        String cid = intent.getStringExtra("c_id");
        String cTitle = intent.getStringExtra("c_title");
        String cOwner = intent.getStringExtra("c_owner");
        String bill_date = intent.getStringExtra("c_date");
        String price = intent.getStringExtra("bill_price");

        txtBillId.setText("Bill ID: " + billId);
        txtName.setText("Họ và tên: " + name);
        txtCID.setText("Mã khoá học: " + cid);
        txtCTitle.setText("Tên khoá học: " + cTitle + "("+ cOwner + ")");
        txtPrice.setText("Số tiền đã thanh toán: " + price);
        txtDate.setText(bill_date);
    }
    private void mapping(){
        btnFinish = (AppCompatButton) findViewById(R.id.buttonFinish);
        txtBillId = (TextView) findViewById(R.id.txtBillID);
        txtName = (TextView) findViewById(R.id.txtName);
        txtCID = (TextView) findViewById(R.id.txtCID);
        txtCTitle = (TextView) findViewById(R.id.txtCourseTitle);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
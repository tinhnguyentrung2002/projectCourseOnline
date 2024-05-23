package com.example.courseonline.Activity.Learner;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.courseonline.Class.LoadingAlert;
import com.example.courseonline.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WebActivity extends AppCompatActivity {

    private WebView webView;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        LoadingAlert alert = new LoadingAlert(WebActivity.this);
        alert.startLoading();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert.closeLoading();
            }
        }, 5000);
        String document_url = getIntent().getStringExtra("document_url");

        webView = findViewById(R.id.webView);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWebPrintJob(webView);
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                fab.setVisibility(View.VISIBLE);
            }
        });
        if(document_url != null)
        {
            webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+ document_url);
        }else{
            Toast.makeText(WebActivity.this,"Không thể mở tài liệu này",Toast.LENGTH_SHORT).show();
        }

    }
    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
        String jobName = getString(R.string.app_name) + "đã được lưu về máy";
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }

}
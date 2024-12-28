package com.example.courseonline.Class;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.os.Handler;

import com.example.courseonline.R;


public class LoadingAlert {
    private Activity activity;
    private AlertDialog alertDialog;
    private Handler handler;
    private Runnable dismissRunnable;
    public LoadingAlert(Activity myActivity)
    {
        activity = myActivity;
        handler = new Handler();
    }
    public void startLoading(){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.loading_layout,null));
            builder.setCancelable(false);

            alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dismissRunnable = new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        };
        handler.postDelayed(dismissRunnable, 4000);

    }
    public void closeLoading(){
        alertDialog.dismiss(); handler.removeCallbacks(dismissRunnable);
    }
}

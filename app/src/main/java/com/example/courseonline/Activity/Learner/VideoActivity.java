package com.example.courseonline.Activity.Learner;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.courseonline.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class VideoActivity extends AppCompatActivity {

    ImageView imgPip;
    YouTubePlayerView playerView;
    private String url_video;
    private PictureInPictureParams.Builder piBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        playerView = (YouTubePlayerView) findViewById(R.id.frameVideo);
        imgPip = (ImageView) findViewById(R.id.imgPip);
        url_video = getIntent().getStringExtra("url_id");
        getLifecycle().addObserver(playerView);
        playerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
            @Override
            public void onYouTubePlayer(@androidx.annotation.NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(url_video, 0);
                youTubePlayer.play();
            }
        });
        playerView.addOnLayoutChangeListener((v, left, top, right, bottom,
                                              oldLeft, oldTop, oldRight, oldBottom) -> {
            if (left != oldLeft || right != oldRight || top != oldTop
                    || bottom != oldBottom) {
                final Rect sourceRectHint = new Rect();
                playerView.getGlobalVisibleRect(sourceRectHint);
                setPictureInPictureParams(
                        new PictureInPictureParams.Builder()
                                .setSourceRectHint(sourceRectHint)
                                .build());
            }
        });

        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(playerView, youTubePlayer);
                defaultPlayerUiController.showYouTubeButton(false);
                defaultPlayerUiController.showVideoTitle(false);
                defaultPlayerUiController.showFullscreenButton(false);
                defaultPlayerUiController.setFullscreenButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                playerView.setCustomPlayerUi(defaultPlayerUiController.getRootView());
            }
        };

        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
        playerView.initialize(listener, options);
        imgPip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               pictureInPicture();
            }
        });
        //View customUiView = playerView.inflateCustomPlayerUi(R.layout.layout_frame);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            piBuilder = new PictureInPictureParams.Builder();
        }

    }

    private void pictureInPicture(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Rational rational = new Rational(playerView.getWidth()+100,playerView.getHeight()+300);
            piBuilder.setAspectRatio(rational).build();
            enterPictureInPictureMode(piBuilder.build());
        }else{

        }
    }
    @Override
    public void onBackPressed() {
        playerView.release();
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        url_video = intent.getStringExtra("url_id");
        playerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
            @Override
            public void onYouTubePlayer(@androidx.annotation.NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(url_video, 0);
                youTubePlayer.play();
            }
        });
    }
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!isInPictureInPictureMode()){
                pictureInPicture();
            }else{
            }
        }
    }
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if(isInPictureInPictureMode()){
            imgPip.setVisibility(View.GONE);
        }else{
            imgPip.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        playerView.release();
        finish();
    }
}
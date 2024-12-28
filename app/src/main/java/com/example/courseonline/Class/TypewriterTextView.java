package com.example.courseonline.Class;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;

import com.example.courseonline.R;

public class TypewriterTextView extends androidx.appcompat.widget.AppCompatTextView {
    private String text = "";
    private int index = 0;
    private long delay = 1; // Thời gian giữa mỗi ký tự
    private MediaPlayer mediaPlayer;

    public TypewriterTextView(Context context) {
        super(context);
        init(context);
    }

    public TypewriterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TypewriterTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.typewritter_sound);
        mediaPlayer.setVolume(0.15f, 0.15f); // Giảm âm lượng
    }

    public void animateText(final String text) {
        this.text = text;
        index = 0;
        setText("");
        setCharacter();
    }

    private void setCharacter() {
        if (index < text.length()) {
            final String currentText = text.substring(0, index++);
            setText(currentText);

            if (mediaPlayer != null) {
                mediaPlayer.start();
            }

            // Thiết lập delay cho ký tự tiếp theo
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCharacter();
                }
            }, delay);
        } else {
            // Ngừng phát âm thanh khi đã hoàn thành gõ
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0); // Đưa con trỏ về đầu
            }
        }
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

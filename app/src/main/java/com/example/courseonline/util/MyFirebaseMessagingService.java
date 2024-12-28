package com.example.courseonline.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.example.courseonline.Activity.Learner.DiscussionBoxActivity;
import com.example.courseonline.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.concurrent.ExecutionException;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "DailyNotification";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String notificationBody = remoteMessage.getNotification().getBody();
            String notificationTitle = remoteMessage.getNotification().getTitle();
            Uri notificationImages = remoteMessage.getNotification().getImageUrl();
            try {
                sendNotification(notificationTitle, notificationBody, notificationImages);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (remoteMessage.getData().size() > 0) {
            String topicTitle = remoteMessage.getData().get("discussionTitle");
            String topicContent = remoteMessage.getData().get("topicContent");
            String topicFirstImage = remoteMessage.getData().get("image");

            Uri imageUri = (topicFirstImage != null) ? Uri.parse(topicFirstImage) : null;
            try {
                sendCustomNotification(topicTitle, topicContent, imageUri);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void sendNotification(String title, String body, Uri image) throws ExecutionException, InterruptedException {
        Intent intent = new Intent(this, DiscussionBoxActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_IMMUTABLE);

        int channelId = (int) System.currentTimeMillis();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, String.valueOf(channelId))
                        .setSmallIcon(R.mipmap.logo2_new)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        if (image != null) {
            Bitmap imageUrl = Glide.with(this)
                    .asBitmap()
                    .load(image)
                    .submit()
                    .get();
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                    .bigPicture(imageUrl) ;
            notificationBuilder.setStyle(bigPictureStyle);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(channelId),
                    "FCM",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 , notificationBuilder.build());
    }
    private void sendCustomNotification(String discussionTitle, String topicContent, Uri imageUri) throws ExecutionException, InterruptedException {
        Intent intent = new Intent(this, DiscussionBoxActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        int channelId = (int) System.currentTimeMillis();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, String.valueOf(channelId))
                        .setSmallIcon(R.mipmap.logo2_new)
                        .setContentTitle("Chủ đề mới từ: " + discussionTitle)
                        .setContentText(topicContent)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        if (imageUri != null) {
            Bitmap imageUrl = Glide.with(this)
                    .asBitmap()
                    .load(imageUri)
                    .submit()
                    .get();
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                    .bigPicture(imageUrl);

            notificationBuilder.setStyle(bigPictureStyle);
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(channelId),
                    "FCM",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(channelId, notificationBuilder.build());
    }


}

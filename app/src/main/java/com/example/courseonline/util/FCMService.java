package com.example.courseonline.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMService {

    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/courseonline-6050b/messages:send";
    public static void sendNotification(Context context, String fcmToken, String title, String body, String imageUrl) {
        try {
            JSONObject message = new JSONObject();
            JSONObject messageContent = new JSONObject();
            JSONObject notification = new JSONObject();

            notification.put("title", title);
            notification.put("body", "Chủ đề mới: " +  body);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                notification.put("image", imageUrl);
            }

            messageContent.put("notification", notification);
            messageContent.put("token", fcmToken);

            message.put("message", messageContent);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FCM_URL, message,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("FCM", "Notification sent successfully: " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(com.android.volley.VolleyError error) {
                            Log.e("FCM", "Error sending notification: " + error.toString());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    AccessToken accessToken = new AccessToken();
                    headers.put("Authorization", "Bearer " + accessToken.getAccessToken());
                    headers.put("Content-Type", "application/json");

                    return headers;
                }
            };

            Volley.newRequestQueue(context).add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

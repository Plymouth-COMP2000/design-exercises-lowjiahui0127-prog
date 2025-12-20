package com.example.mal2017restaurantmanagementapplication;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationHelper {
    private Context context;
    private static final String CHANNEL_ID = "restaurant_channel";
    private static final String NOTIFICATIONS_KEY = "user_notifications";

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Restaurant Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for restaurant reservations");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void sendReservationNotification(String title, String message, String reservationNumber) {
        String userEmail = UserSessionManager.getUserEmail(context);
        sendNotificationToUser(title, message, reservationNumber, userEmail);
    }

    public void sendNotificationToUser(String title, String message, String reservationNumber, String targetUserEmail) {
        Notification notification = new Notification(title, message, getTypeFromTitle(title),
                reservationNumber, targetUserEmail);

        saveNotification(notification);

        String currentUserEmail = UserSessionManager.getUserEmail(context);
        if (targetUserEmail.equals(currentUserEmail) && hasNotificationPermission()) {
            sendSystemNotification(title, message, notification.getId());

            Toast.makeText(context, title + ": " + message, Toast.LENGTH_LONG).show();
        }

        updateNotificationBadge(targetUserEmail);
    }

    private String getTypeFromTitle(String title) {
        title = title.toLowerCase();
        if (title.contains("confirmed")) return "reservation_confirmed";
        if (title.contains("cancelled") || title.contains("rejected")) return "reservation_cancelled";
        if (title.contains("updated")) return "reservation_updated";
        if (title.contains("submitted") || title.contains("new")) return "reservation_created";
        return "general";
    }

    private void sendSystemNotification(String title, String message, String notificationId) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_bell)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{100, 200, 100, 200});

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                // 使用notificationId的hashCode作为通知ID
                int id = Math.abs(notificationId.hashCode());
                notificationManager.notify(id, builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveNotification(Notification notification) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE);
            String userEmail = notification.getUserEmail();
            String key = NOTIFICATIONS_KEY + "_" + userEmail;

            JSONArray notificationsArray = new JSONArray();
            String savedNotifications = prefs.getString(key, "[]");

            if (!savedNotifications.equals("[]")) {
                notificationsArray = new JSONArray(savedNotifications);
            }

            JSONObject notificationObj = new JSONObject();
            notificationObj.put("id", notification.getId());
            notificationObj.put("title", notification.getTitle());
            notificationObj.put("message", notification.getMessage());
            notificationObj.put("type", notification.getType());
            notificationObj.put("isRead", notification.isRead());
            notificationObj.put("timestamp", notification.getTimestamp());
            notificationObj.put("reservationNumber", notification.getReservationNumber());
            notificationObj.put("userEmail", notification.getUserEmail());

            notificationsArray.put(notificationObj);

            prefs.edit().putString(key, notificationsArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getUserNotifications(String userEmail) {
        List<Notification> notifications = new ArrayList<>();
        try {
            SharedPreferences prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE);
            String key = NOTIFICATIONS_KEY + "_" + userEmail;
            String savedNotifications = prefs.getString(key, "[]");

            JSONArray notificationsArray = new JSONArray(savedNotifications);

            for (int i = 0; i < notificationsArray.length(); i++) {
                JSONObject obj = notificationsArray.getJSONObject(i);
                Notification notification = new Notification();
                notification.setId(obj.getString("id"));
                notification.setTitle(obj.getString("title"));
                notification.setMessage(obj.getString("message"));
                notification.setType(obj.getString("type"));
                notification.setRead(obj.getBoolean("isRead"));
                notification.setTimestamp(obj.getLong("timestamp"));
                notification.setReservationNumber(obj.getString("reservationNumber"));
                notification.setUserEmail(obj.getString("userEmail"));

                notifications.add(notification);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    public int getUnreadNotificationCount(String userEmail) {
        List<Notification> notifications = getUserNotifications(userEmail);
        int count = 0;
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                count++;
            }
        }
        return count;
    }

    public void markAllAsRead(String userEmail) {
        List<Notification> notifications = getUserNotifications(userEmail);
        for (Notification notification : notifications) {
            markAsRead(notification.getId(), userEmail);
        }
    }

    public void markAsRead(String notificationId, String userEmail) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE);
            String key = NOTIFICATIONS_KEY + "_" + userEmail;
            String savedNotifications = prefs.getString(key, "[]");

            JSONArray notificationsArray = new JSONArray(savedNotifications);

            for (int i = 0; i < notificationsArray.length(); i++) {
                JSONObject obj = notificationsArray.getJSONObject(i);
                if (obj.getString("id").equals(notificationId)) {
                    obj.put("isRead", true);
                    break;
                }
            }

            prefs.edit().putString(key, notificationsArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteNotification(String notificationId, String userEmail) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE);
            String key = NOTIFICATIONS_KEY + "_" + userEmail;
            String savedNotifications = prefs.getString(key, "[]");

            JSONArray notificationsArray = new JSONArray(savedNotifications);
            JSONArray newArray = new JSONArray();

            for (int i = 0; i < notificationsArray.length(); i++) {
                JSONObject obj = notificationsArray.getJSONObject(i);
                if (!obj.getString("id").equals(notificationId)) {
                    newArray.put(obj);
                }
            }

            prefs.edit().putString(key, newArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clearAllNotifications(String userEmail) {
        SharedPreferences prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE);
        String key = NOTIFICATIONS_KEY + "_" + userEmail;
        prefs.edit().putString(key, "[]").apply();
    }

    private void updateNotificationBadge(String userEmail) {
        int unreadCount = getUnreadNotificationCount(userEmail);
    }

    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void requestNotificationPermission(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    requestCode);
        }
    }
}
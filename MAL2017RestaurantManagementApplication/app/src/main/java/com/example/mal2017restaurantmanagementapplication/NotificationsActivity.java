package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationsActivity extends BaseGuestActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private NotificationHelper notificationHelper;
    private TextView tvNoNotifications;
    private TextView tvClearAll;

    @Override
    protected int getCurrentNavId() {
        return R.id.navProfile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        tvNoNotifications = findViewById(R.id.tv_no_notifications);
        rvNotifications = findViewById(R.id.rv_notifications);
        tvClearAll = findViewById(R.id.tv_clear_all);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        tvClearAll.setOnClickListener(v -> clearAllNotifications());

        notificationHelper = new NotificationHelper(this);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        loadNotifications();
    }

    private void loadNotifications() {
        String userEmail = UserSessionManager.getUserEmail(this);
        String userRole = UserSessionManager.getUserRole(this);

        List<Notification> notifications = notificationHelper.getUserNotifications(userEmail);

        Collections.sort(notifications, (n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp()));

        if (notifications.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            tvNoNotifications.setVisibility(View.VISIBLE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            tvNoNotifications.setVisibility(View.GONE);

            adapter = new NotificationAdapter(notifications, new NotificationAdapter.OnNotificationClickListener() {
                @Override
                public void onNotificationClick(Notification notification) {
                    notificationHelper.markAsRead(notification.getId(), notification.getUserEmail());
                    loadNotifications();
                }

                @Override
                public void onNotificationLongClick(Notification notification) {
                    showDeleteDialog(notification);
                }
            });
            rvNotifications.setAdapter(adapter);
            notificationHelper.markAllAsRead(userEmail);
        }
    }

    private void clearAllNotifications() {
        String userEmail = UserSessionManager.getUserEmail(this);
        notificationHelper.clearAllNotifications(userEmail);
        loadNotifications();
        updateBadgeCount();
    }

    private void showDeleteDialog(Notification notification) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Notification")
                .setMessage("Are you sure you want to delete this notification?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    notificationHelper.deleteNotification(notification.getId(), notification.getUserEmail());
                    loadNotifications();
                    updateBadgeCount();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateBadgeCount() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}
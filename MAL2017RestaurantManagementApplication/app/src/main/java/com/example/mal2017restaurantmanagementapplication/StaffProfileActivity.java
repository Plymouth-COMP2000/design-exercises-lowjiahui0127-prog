package com.example.mal2017restaurantmanagementapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.button.MaterialButton;

public class StaffProfileActivity extends BaseStaffActivity {

    private TextView tvStaffName, tvStaffEmail;
    private MaterialButton btnLogout;
    private SwitchCompat switchReservationAlerts, switchUpdates, switchCancellations;
    private LinearLayout layoutEditProfile, layoutChangePassword, layoutAboutUs;
    private SharedPreferences notificationPrefs;
    private static final String PREFS_NAME = "staff_notification_prefs";
    private static final String KEY_RESERVATION_ALERTS = "staff_reservation_alerts";
    private static final String KEY_RESERVATION_UPDATES = "staff_updates";
    private static final String KEY_CANCELLATION_ALERTS = "staff_cancellations";

    @Override
    protected int getCurrentNavId() {
        return R.id.navStaffProfile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);

        setupStaffBottomNav();
        initViews();
        displayStaffProfile();
        loadNotificationPreferences();
        setupNotificationSwitches();
        setupMenuClickListeners();
        setupNotificationIcon();
        setupLogoutButton();
    }

    private void initViews() {
        tvStaffName = findViewById(R.id.tvStaffName);
        tvStaffEmail = findViewById(R.id.tvStaffEmail);
        btnLogout = findViewById(R.id.btnStaffLogout);
        switchReservationAlerts = findViewById(R.id.switch1);
        switchUpdates = findViewById(R.id.switch2);
        switchCancellations = findViewById(R.id.switch3);

        layoutEditProfile = findViewById(R.id.layout_edit_profile);
        layoutChangePassword = findViewById(R.id.layout_change_password);
        layoutAboutUs = findViewById(R.id.layout_about_us);

        notificationPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void displayStaffProfile() {
        String fullName = UserSessionManager.getUserName(this);
        String email = UserSessionManager.getUserEmail(this);

        if (tvStaffName != null) tvStaffName.setText(fullName.toUpperCase());
        if (tvStaffEmail != null) tvStaffEmail.setText(email);
    }

    private void loadNotificationPreferences() {
        boolean alerts = notificationPrefs.getBoolean(KEY_RESERVATION_ALERTS, true);
        boolean updates = notificationPrefs.getBoolean(KEY_RESERVATION_UPDATES, true);
        boolean cancellations = notificationPrefs.getBoolean(KEY_CANCELLATION_ALERTS, true);

        switchReservationAlerts.setChecked(alerts);
        switchUpdates.setChecked(updates);
        switchCancellations.setChecked(cancellations);
    }

    private void setupNotificationSwitches() {
        switchReservationAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveNotificationPreference(KEY_RESERVATION_ALERTS, isChecked);
            String status = isChecked ? "ON" : "OFF";
            Toast.makeText(this, "New reservation alerts: " + status, Toast.LENGTH_SHORT).show();
        });

        switchUpdates.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveNotificationPreference(KEY_RESERVATION_UPDATES, isChecked);
            String status = isChecked ? "ON" : "OFF";
            Toast.makeText(this, "Reservation updates: " + status, Toast.LENGTH_SHORT).show();
        });

        switchCancellations.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveNotificationPreference(KEY_CANCELLATION_ALERTS, isChecked);
            String status = isChecked ? "ON" : "OFF";
            Toast.makeText(this, "Cancellation alerts: " + status, Toast.LENGTH_SHORT).show();
        });
    }

    private void saveNotificationPreference(String key, boolean value) {
        SharedPreferences.Editor editor = notificationPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void setupMenuClickListeners() {
        // Edit Profile
        if (layoutEditProfile != null) {
            layoutEditProfile.setOnClickListener(v -> {
                showComingSoonDialog("Edit Profile",
                        "This feature is coming soon!");
            });
        }

        // Change Password
        if (layoutChangePassword != null) {
            layoutChangePassword.setOnClickListener(v -> {
                showContactSupportDialog();
            });
        }

        // About Us
        if (layoutAboutUs != null) {
            layoutAboutUs.setOnClickListener(v -> {
                showAboutUsDialog();
            });
        }
    }

    private void showComingSoonDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showContactSupportDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Contact Support")
                .setMessage("For password changes, please contact our Restaurant Support Center:\n\n" +
                        "Phone: 012-3456789\n" +
                        "Email: support@restaurant.com")
                .setPositiveButton("OK", null)
                .setNeutralButton("Copy Email", (dialog, which) -> {
                    Toast.makeText(this, "Email copied to clipboard", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showAboutUsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About Restaurant Management App")
                .setMessage("DineShip is a modern casual-premium restaurant dedicated to serving exceptional flavours crafted with care and quality. At DineShip, every dish is prepared using freshly sourced ingredients and perfected through our signature thin-crust baking technique, delivering a light, crisp, and flavourful experience in every bite.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        UserSessionManager.logout(StaffProfileActivity.this);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupNotificationIcon() {
        FrameLayout notificationIcon = findViewById(R.id.notification_icon_container);
        if (notificationIcon != null) {
            notificationIcon.setOnClickListener(v -> {
                Intent intent = new Intent(StaffProfileActivity.this, NotificationsActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void updateNotificationBadge() {
        FrameLayout notificationIcon = findViewById(R.id.notification_icon_container);
        if (notificationIcon != null) {
            TextView badge = notificationIcon.findViewById(R.id.badge_count);
            if (badge != null) {
                NotificationHelper notificationHelper = new NotificationHelper(this);

                String staffEmail = UserSessionManager.getUserEmail(this);
                int unreadCount = notificationHelper.getUnreadNotificationCount(staffEmail);

                if (unreadCount > 0) {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(unreadCount > 99 ? "99+" : unreadCount));
                } else {
                    badge.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayStaffProfile();
        updateNotificationBadge();
    }

    public static boolean shouldSendNotificationToStaff(Context context, String notificationType) {
        SharedPreferences prefs = context.getSharedPreferences("staff_notification_prefs", MODE_PRIVATE);

        switch (notificationType) {
            case "new_reservation":
                return prefs.getBoolean("staff_reservation_alerts", true);
            case "reservation_update":
                return prefs.getBoolean("staff_updates", true);
            case "cancellation":
                return prefs.getBoolean("staff_cancellations", true);
            default:
                return true;
        }
    }
}
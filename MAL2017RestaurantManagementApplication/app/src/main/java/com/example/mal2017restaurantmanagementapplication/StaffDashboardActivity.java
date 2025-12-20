package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class StaffDashboardActivity extends BaseStaffActivity {

    private DatabaseHelper dbHelper;
    private TextView tvMenuItemsCount, tvReservationsCount;
    private LinearLayout btnManageMenu, btnViewReservations;
    private TextView badgeCount;

    @Override
    protected int getCurrentNavId() {
        return R.id.navDashboard;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        setupStaffBottomNav();
        dbHelper = new DatabaseHelper(this);

        initializeUI();
        loadDashboardData();
        setupNotificationBadge();
    }

    private void initializeUI() {
        tvMenuItemsCount = findViewById(R.id.tv_menu_items_count);
        tvReservationsCount = findViewById(R.id.tv_reservations_count);
        btnManageMenu = findViewById(R.id.btn_manage_menu);
        btnViewReservations = findViewById(R.id.btn_view_reservations);
        badgeCount = findViewById(R.id.badge_count);

        // Set up notification bell click
        View icBell = findViewById(R.id.ic_bell);
        icBell.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Set up quick action buttons
        btnManageMenu.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, StaffMenuActivity.class);
            startActivity(intent);
        });

        btnViewReservations.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, StaffReservationsActivity.class);
            startActivity(intent);
        });
    }

    private void loadDashboardData() {
        // Get menu items count
        int menuItemsCount = dbHelper.getMenuItemsCount();
        tvMenuItemsCount.setText(String.valueOf(menuItemsCount));

        // Get total reservations count
        int reservationsCount = dbHelper.getTotalReservationsCount();
        tvReservationsCount.setText(String.valueOf(reservationsCount));
    }

    private void setupNotificationBadge() {
        int userId = UserSessionManager.getUserIdInt(this);
        if (userId != -1) {
            int unreadCount = dbHelper.getUnreadNotificationCount(userId);
            if (unreadCount > 0) {
                badgeCount.setText(String.valueOf(unreadCount));
                badgeCount.setVisibility(View.VISIBLE);
            } else {
                badgeCount.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
        setupNotificationBadge();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
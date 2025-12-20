package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseStaffActivity extends AppCompatActivity {

    protected abstract int getCurrentNavId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in as staff
        if (!UserSessionManager.isLoggedIn(this) ||
                !"staff".equals(UserSessionManager.getUserRole(this))) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    protected void setupStaffBottomNav() {
        View bottomBar = findViewById(R.id.bottom_bar);
        if (bottomBar == null) {
            return;
        }

        resetAllNavItems();
        highlightCurrentNavItem(getCurrentNavId());
        setupNavClickListeners();
    }

    private void resetAllNavItems() {
        ImageView dashboardIcon = findViewById(R.id.navDashboardIcon);
        TextView dashboardText = findViewById(R.id.navDashboardText);
        if (dashboardIcon != null && dashboardText != null) {
            dashboardIcon.setImageResource(R.drawable.ic_dashboard);
            dashboardText.setTextColor(getResources().getColor(R.color.light_text));
        }

        ImageView menuIcon = findViewById(R.id.navStaffMenuIcon);
        TextView menuText = findViewById(R.id.navStaffMenuText);
        if (menuIcon != null && menuText != null) {
            menuIcon.setImageResource(R.drawable.ic_menu);
            menuText.setTextColor(getResources().getColor(R.color.light_text));
        }

        ImageView bookingIcon = findViewById(R.id.navStaffReservationsIcon);
        TextView bookingText = findViewById(R.id.navStaffReservationsText);
        if (bookingIcon != null && bookingText != null) {
            bookingIcon.setImageResource(R.drawable.ic_bookings);
            bookingText.setTextColor(getResources().getColor(R.color.light_text));
        }

        ImageView profileIcon = findViewById(R.id.navStaffProfileIcon);
        TextView profileText = findViewById(R.id.navStaffProfileText);
        if (profileIcon != null && profileText != null) {
            profileIcon.setImageResource(R.drawable.ic_profile);
            profileText.setTextColor(getResources().getColor(R.color.light_text));
        }
    }

    private void highlightCurrentNavItem(int navId) {
        int orange = getResources().getColor(R.color.main_orange);

        if (navId == R.id.navDashboard) {
            ImageView icon = findViewById(R.id.navDashboardIcon);
            TextView text = findViewById(R.id.navDashboardText);
            if (icon != null && text != null) {
                icon.setImageResource(R.drawable.ic_dashboardorange);
                text.setTextColor(orange);
            }
        } else if (navId == R.id.navStaffMenu) {
            ImageView icon = findViewById(R.id.navStaffMenuIcon);
            TextView text = findViewById(R.id.navStaffMenuText);
            if (icon != null && text != null) {
                icon.setImageResource(R.drawable.ic_menuorange);
                text.setTextColor(orange);
            }
        } else if (navId == R.id.navStaffReservations) {
            ImageView icon = findViewById(R.id.navStaffReservationsIcon);
            TextView text = findViewById(R.id.navStaffReservationsText);
            if (icon != null && text != null) {
                icon.setImageResource(R.drawable.ic_bookingsorange);
                text.setTextColor(orange);
            }
        } else if (navId == R.id.navStaffProfile) {
            ImageView icon = findViewById(R.id.navStaffProfileIcon);
            TextView text = findViewById(R.id.navStaffProfileText);
            if (icon != null && text != null) {
                icon.setImageResource(R.drawable.ic_profileorange);
                text.setTextColor(orange);
            }
        }
    }

    private void setupNavClickListeners() {
        View dashboardNav = findViewById(R.id.navDashboard);
        View menuNav = findViewById(R.id.navStaffMenu);
        View reservationsNav = findViewById(R.id.navStaffReservations);
        View profileNav = findViewById(R.id.navStaffProfile);

        if (dashboardNav != null) {
            dashboardNav.setOnClickListener(v -> navigateToDashboard());
        }
        if (menuNav != null) {
            menuNav.setOnClickListener(v -> navigateToMenu());
        }
        if (reservationsNav != null) {
            reservationsNav.setOnClickListener(v -> navigateToReservations());
        }
        if (profileNav != null) {
            profileNav.setOnClickListener(v -> navigateToProfile());
        }
    }

    private void navigateToDashboard() {
        if (!(this instanceof StaffDashboardActivity)) {
            startActivity(new Intent(this, StaffDashboardActivity.class));
            finish();
        }
    }

    private void navigateToMenu() {
        if (!(this instanceof StaffMenuActivity)) {
            startActivity(new Intent(this, StaffMenuActivity.class));
            finish();
        }
    }

    private void navigateToReservations() {
        if (!(this instanceof StaffReservationsActivity)) {
            startActivity(new Intent(this, StaffReservationsActivity.class));
            finish();
        }
    }

    private void navigateToProfile() {
        if (!(this instanceof StaffProfileActivity)) {
            startActivity(new Intent(this, StaffProfileActivity.class));
            finish();
        }
    }

    protected void updateNotificationBadge() {
    }
}
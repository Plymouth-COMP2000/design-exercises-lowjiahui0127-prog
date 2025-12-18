package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseStaffActivity extends AppCompatActivity {

    protected abstract int getCurrentNavId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 👉 如果之后你要做 Staff login 验证，可以在这里加
        // 目前 UI-only，可不写
    }

    protected void setupStaffBottomNav() {
        resetAllNavItems();
        highlightCurrentNavItem(getCurrentNavId());

        findViewById(R.id.navDashboard).setOnClickListener(v -> navigateToDashboard());
        findViewById(R.id.navStaffMenu).setOnClickListener(v -> navigateToMenu());
        findViewById(R.id.navStaffReservations).setOnClickListener(v -> navigateToReservations());
        findViewById(R.id.navStaffProfile).setOnClickListener(v -> navigateToProfile());
    }

    // =====================
    // Reset states
    // =====================
    private void resetAllNavItems() {

        ImageView dashboardIcon = findViewById(R.id.navDashboardIcon);
        TextView dashboardText = findViewById(R.id.navDashboardText);
        dashboardIcon.setImageResource(R.drawable.ic_dashboard);
        dashboardText.setTextColor(getResources().getColor(R.color.light_text));

        ImageView menuIcon = findViewById(R.id.navStaffMenuIcon);
        TextView menuText = findViewById(R.id.navStaffMenuText);
        menuIcon.setImageResource(R.drawable.ic_menu);
        menuText.setTextColor(getResources().getColor(R.color.light_text));

        ImageView bookingIcon = findViewById(R.id.navStaffReservationsIcon);
        TextView bookingText = findViewById(R.id.navStaffReservationsText);
        bookingIcon.setImageResource(R.drawable.ic_bookings);
        bookingText.setTextColor(getResources().getColor(R.color.light_text));

        ImageView profileIcon = findViewById(R.id.navStaffProfileIcon);
        TextView profileText = findViewById(R.id.navStaffProfileText);
        profileIcon.setImageResource(R.drawable.ic_profile);
        profileText.setTextColor(getResources().getColor(R.color.light_text));
    }


    // =====================
    // Highlight current tab
    // =====================
    private void highlightCurrentNavItem(int navId) {

        int orange = getResources().getColor(R.color.main_orange);

        if (navId == R.id.navDashboard) {
            ImageView icon = findViewById(R.id.navDashboardIcon);
            TextView text = findViewById(R.id.navDashboardText);
            icon.setImageResource(R.drawable.ic_dashboardorange);
            text.setTextColor(orange);

        } else if (navId == R.id.navStaffMenu) {
            ImageView icon = findViewById(R.id.navStaffMenuIcon);
            TextView text = findViewById(R.id.navStaffMenuText);
            icon.setImageResource(R.drawable.ic_menuorange);
            text.setTextColor(orange);

        } else if (navId == R.id.navStaffReservations) {
            ImageView icon = findViewById(R.id.navStaffReservationsIcon);
            TextView text = findViewById(R.id.navStaffReservationsText);
            icon.setImageResource(R.drawable.ic_bookingsorange);
            text.setTextColor(orange);

        } else if (navId == R.id.navStaffProfile) {
            ImageView icon = findViewById(R.id.navStaffProfileIcon);
            TextView text = findViewById(R.id.navStaffProfileText);
            icon.setImageResource(R.drawable.ic_profileorange);
            text.setTextColor(orange);
        }
    }


    // =====================
    // Navigation
    // =====================
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
}

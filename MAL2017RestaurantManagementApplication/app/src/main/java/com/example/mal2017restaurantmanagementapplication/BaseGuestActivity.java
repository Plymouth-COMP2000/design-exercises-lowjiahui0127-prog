package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseGuestActivity extends AppCompatActivity {

    protected abstract int getCurrentNavId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatActivity currentActivity = this;

        if (!(currentActivity instanceof LoginActivity) &&
                !(currentActivity instanceof GuestCreateAccountActivity) &&
                !UserSessionManager.isLoggedIn(this)) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    protected void setupBottomNav() {
        // Reset all nav items to inactive state
        resetAllNavItems();

        // Highlight current nav item
        highlightCurrentNavItem(getCurrentNavId());

        // Set click listeners
        findViewById(R.id.navMenu).setOnClickListener(v -> navigateToMenu());

        findViewById(R.id.navBookTable).setOnClickListener(v -> navigateToBookTable());

        findViewById(R.id.navBookings).setOnClickListener(v -> navigateToMyBookings());

        findViewById(R.id.navProfile).setOnClickListener(v -> navigateToProfile());
    }

    private void resetAllNavItems() {
        // Reset Menu
        ImageView menuIcon = findViewById(R.id.navMenuIcon);
        TextView menuText = findViewById(R.id.navMenuText);
        menuIcon.setImageResource(R.drawable.ic_menu);
        menuText.setTextColor(getResources().getColor(R.color.light_text));

        // Reset Book Table
        ImageView bookTableIcon = findViewById(R.id.navBookTableIcon);
        TextView bookTableText = findViewById(R.id.navBookTableText);
        bookTableIcon.setImageResource(R.drawable.ic_table);
        bookTableText.setTextColor(getResources().getColor(R.color.light_text));

        // Reset My Bookings
        ImageView bookingsIcon = findViewById(R.id.navBookingsIcon);
        TextView bookingsText = findViewById(R.id.navBookingsText);
        bookingsIcon.setImageResource(R.drawable.ic_bookings);
        bookingsText.setTextColor(getResources().getColor(R.color.light_text));

        // Reset Profile
        ImageView profileIcon = findViewById(R.id.navProfileIcon);
        TextView profileText = findViewById(R.id.navProfileText);
        profileIcon.setImageResource(R.drawable.ic_profile);
        profileText.setTextColor(getResources().getColor(R.color.light_text));
    }

    private void highlightCurrentNavItem(int navItemId) {
        int orangeColor = getResources().getColor(R.color.main_orange);

        if (navItemId == R.id.navMenu) {
            ImageView menuIcon = findViewById(R.id.navMenuIcon);
            TextView menuText = findViewById(R.id.navMenuText);
            menuIcon.setImageResource(R.drawable.ic_menuorange);
            menuText.setTextColor(orangeColor);

        } else if (navItemId == R.id.navBookTable) {
            ImageView bookTableIcon = findViewById(R.id.navBookTableIcon);
            TextView bookTableText = findViewById(R.id.navBookTableText);
            bookTableIcon.setImageResource(R.drawable.ic_tableorange);
            bookTableText.setTextColor(orangeColor);

        } else if (navItemId == R.id.navBookings) {
            ImageView bookingsIcon = findViewById(R.id.navBookingsIcon);
            TextView bookingsText = findViewById(R.id.navBookingsText);
            bookingsIcon.setImageResource(R.drawable.ic_bookingsorange);
            bookingsText.setTextColor(orangeColor);

        } else if (navItemId == R.id.navProfile) {
            ImageView profileIcon = findViewById(R.id.navProfileIcon);
            TextView profileText = findViewById(R.id.navProfileText);
            profileIcon.setImageResource(R.drawable.ic_profileorange);
            profileText.setTextColor(orangeColor);
        }
    }

    private void navigateToMenu() {
        if (!(this instanceof GuestMenuActivity)) {
            startActivity(new Intent(this, GuestMenuActivity.class));
            finish();
        }
    }

    private void navigateToBookTable() {
        if (!(this instanceof GuestBookTableActivity)) {
            startActivity(new Intent(this, GuestBookTableActivity.class));
            finish();
        }
    }

    private void navigateToMyBookings() {
        if (!(this instanceof GuestMyBookingsActivity)) {
            startActivity(new Intent(this, GuestMyBookingsActivity.class));
            finish();
        }
    }

    private void navigateToProfile() {
        if (!(this instanceof GuestProfileActivity)) {
            startActivity(new Intent(this, GuestProfileActivity.class));
            finish();
        }
    }

    protected void updateNotificationBadge() {
    }
}
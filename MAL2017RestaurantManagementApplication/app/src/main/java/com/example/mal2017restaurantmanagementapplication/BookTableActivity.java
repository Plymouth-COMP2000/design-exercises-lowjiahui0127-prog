package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookTableActivity extends AppCompatActivity {

    private void setupBottomNav() {
        findViewById(R.id.navMenu).setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class)));

        findViewById(R.id.navBookTable).setOnClickListener(v -> {
            if (!(this instanceof BookTableActivity)) {
                startActivity(new Intent(this, BookTableActivity.class));
            }
        });

        findViewById(R.id.navBookings).setOnClickListener(v ->
                startActivity(new Intent(this, MyBookingsActivity.class)));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_table);

        setupBottomNav();

        findViewById(R.id.btnConfirmBooking).setOnClickListener(v -> {
            // 现在先不存 SQLite
            Toast.makeText(this, "Booking confirmed", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, MyBookingsActivity.class));
        });

    }
}
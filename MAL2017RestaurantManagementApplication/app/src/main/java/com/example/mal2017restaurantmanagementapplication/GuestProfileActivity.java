package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;

public class GuestProfileActivity extends BaseGuestActivity {

    @Override
    protected int getCurrentNavId() {
        return R.id.navProfile; // Highlight Profile as active
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupBottomNav();

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Intent intent = new Intent(GuestProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
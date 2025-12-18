package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StaffDashboardActivity extends BaseStaffActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        setupStaffBottomNav();
    }

    @Override
    protected int getCurrentNavId() {
        return R.id.navDashboard;
    }
}

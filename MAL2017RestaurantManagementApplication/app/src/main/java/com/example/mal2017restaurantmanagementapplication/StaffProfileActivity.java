package com.example.mal2017restaurantmanagementapplication;

import android.os.Bundle;

public class StaffProfileActivity extends BaseStaffActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);

        setupStaffBottomNav();
    }

    @Override
    protected int getCurrentNavId() {
        return R.id.navStaffProfile;
    }
}

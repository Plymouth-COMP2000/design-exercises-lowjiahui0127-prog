package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffReservationsActivity extends BaseStaffActivity {

    private RecyclerView rvReservations;
    private StaffReservationAdapter reservationAdapter;
    private DatabaseHelper dbHelper;
    private List<Reservation> allReservations = new ArrayList<>();
    private List<Reservation> filteredReservations = new ArrayList<>();
    private TextView tvNoReservations, tvFilterTitle, tvResetFilter, badgeCount;
    private EditText etSearch;
    private ImageView ivSearchButton;
    private LinearLayout filterButtonsContainer;
    private HorizontalScrollView filterScrollView;

    private String currentFilter = "pending";
    private Map<String, Integer> statusCountMap = new HashMap<>();

    @Override
    protected int getCurrentNavId() {
        return R.id.navStaffReservations;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_reservations);

        setupStaffBottomNav();
        dbHelper = new DatabaseHelper(this);

        initializeUI();
        loadAllReservations();
        setupFilterButtons();
        checkPendingReservations();
    }

    private void initializeUI() {
        rvReservations = findViewById(R.id.rv_reservations);
        tvNoReservations = findViewById(R.id.tv_no_reservations);
        tvFilterTitle = findViewById(R.id.tv_filter_title);
        tvResetFilter = findViewById(R.id.tv_reset_filter);
        etSearch = findViewById(R.id.et_search);
        ivSearchButton = findViewById(R.id.iv_search_button);
        badgeCount = findViewById(R.id.badge_count);
        filterButtonsContainer = findViewById(R.id.filter_buttons_container);
        filterScrollView = findViewById(R.id.filter_scroll_view);

        // Set up notification bell click
        ImageView icBell = findViewById(R.id.ic_bell);
        icBell.setOnClickListener(v -> {
            Intent intent = new Intent(StaffReservationsActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        tvResetFilter.setOnClickListener(v -> {
            currentFilter = "all";
            applyFilter();
            updateFilterButtons();
        });

        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        reservationAdapter = new StaffReservationAdapter(this, filteredReservations);
        rvReservations.setAdapter(reservationAdapter);

        ivSearchButton.setOnClickListener(v -> performSearch());

        reservationAdapter.setOnReservationClickListener(new StaffReservationAdapter.OnReservationClickListener() {
            @Override
            public void onConfirmClick(Reservation reservation) {
                confirmReservation(reservation);
            }

            @Override
            public void onRejectClick(Reservation reservation) {
                rejectReservation(reservation);
            }
        });
    }

    private void loadAllReservations() {
        allReservations.clear();
        statusCountMap.clear();

        List<Reservation> reservationsFromDb = dbHelper.getAllReservations();
        allReservations.addAll(reservationsFromDb);

        for (Reservation r : reservationsFromDb) {
            String status = r.getStatus().toLowerCase();
            statusCountMap.put(status, statusCountMap.getOrDefault(status, 0) + 1);
        }

        applyFilter();
    }

    private void setupFilterButtons() {
        filterButtonsContainer.removeAllViews();

        String[] statuses = {"all", "pending", "confirmed", "cancelled"};

        for (String status : statuses) {
            addFilterButton(status);
        }

        updateFilterButtons();
    }

    private void addFilterButton(String status) {
        LayoutInflater inflater = LayoutInflater.from(this);
        MaterialButton button = (MaterialButton) inflater.inflate(
                R.layout.layout_filter_button, filterButtonsContainer, false);

        String buttonText = getButtonTextForStatus(status);
        int count = getCountForStatus(status);

        button.setText(buttonText + (count > 0 ? " (" + count + ")" : ""));
        button.setTag(status);

        int strokeColorResId = getStrokeColorForStatus(status);
        button.setStrokeColorResource(strokeColorResId);

        button.setOnClickListener(v -> {
            currentFilter = status;
            applyFilter();
            updateFilterButtons();
            scrollToSelectedButton();
        });

        filterButtonsContainer.addView(button);
    }

    private String getButtonTextForStatus(String status) {
        switch (status) {
            case "all": return "All";
            case "pending": return "Pending";
            case "confirmed": return "Confirmed";
            case "cancelled": return "Cancelled";
            default: return status;
        }
    }

    private int getCountForStatus(String status) {
        if ("all".equals(status)) {
            return allReservations.size();
        }
        return statusCountMap.getOrDefault(status, 0);
    }

    private int getStrokeColorForStatus(String status) {
        switch (status) {
            case "all": return R.color.main_orange;
            case "pending": return R.color.status_pending;
            case "confirmed": return R.color.status_confirmed;
            case "cancelled": return R.color.status_cancelled;
            default: return R.color.muted_text;
        }
    }

    private void updateFilterButtons() {
        String title;
        int count = filteredReservations.size();
        switch (currentFilter) {
            case "all":
                title = "All Reservations (" + count + ")";
                break;
            case "pending":
                title = "Pending Reservations (" + count + ")";
                break;
            case "confirmed":
                title = "Confirmed Reservations (" + count + ")";
                break;
            case "cancelled":
                title = "Cancelled Reservations (" + count + ")";
                break;
            default:
                title = "Reservations (" + count + ")";
        }
        tvFilterTitle.setText(title);

        tvResetFilter.setVisibility(currentFilter.equals("all") ? View.GONE : View.VISIBLE);

        for (int i = 0; i < filterButtonsContainer.getChildCount(); i++) {
            View child = filterButtonsContainer.getChildAt(i);
            if (child instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) child;
                String buttonStatus = (String) button.getTag();

                boolean isSelected = buttonStatus.equals(currentFilter);
                updateButtonAppearance(button, buttonStatus, isSelected);
            }
        }
    }

    private void updateButtonAppearance(MaterialButton button, String status, boolean isSelected) {
        if (isSelected) {
            int bgColorResId = getBackgroundColorForStatus(status);
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(bgColorResId)));
            button.setTextColor(getResources().getColor(android.R.color.white));
            button.setStrokeColorResource(android.R.color.transparent);
        } else {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(android.R.color.transparent)));
            button.setTextColor(getResources().getColor(android.R.color.black));
            int strokeColorResId = getStrokeColorForStatus(status);
            button.setStrokeColorResource(strokeColorResId);
        }
    }

    private int getBackgroundColorForStatus(String status) {
        switch (status) {
            case "all": return R.color.main_orange;
            case "pending": return R.color.status_pending;
            case "confirmed": return R.color.status_confirmed;
            case "cancelled": return R.color.status_cancelled;
            default: return R.color.muted_text;
        }
    }

    private void applyFilter() {
        filteredReservations.clear();

        for (Reservation reservation : allReservations) {
            String status = reservation.getStatus().toLowerCase();

            boolean shouldInclude = false;
            if ("all".equals(currentFilter)) {
                shouldInclude = true;
            } else {
                shouldInclude = status.equals(currentFilter);
            }

            if (shouldInclude) {
                filteredReservations.add(reservation);
            }
        }

        reservationAdapter.updateList(filteredReservations);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredReservations.isEmpty()) {
            String message;
            if (allReservations.isEmpty()) {
                message = "No reservations in system";
            } else {
                message = "No " + currentFilter + " reservations";
            }

            tvNoReservations.setText(message);
            tvNoReservations.setVisibility(View.VISIBLE);
            rvReservations.setVisibility(View.GONE);
        } else {
            tvNoReservations.setVisibility(View.GONE);
            rvReservations.setVisibility(View.VISIBLE);
        }
    }

    private void scrollToSelectedButton() {
        for (int i = 0; i < filterButtonsContainer.getChildCount(); i++) {
            View child = filterButtonsContainer.getChildAt(i);
            if (child instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) child;
                String buttonStatus = (String) button.getTag();

                if (buttonStatus.equals(currentFilter)) {
                    int scrollX = button.getLeft() - (filterScrollView.getWidth() - button.getWidth()) / 2;
                    filterScrollView.smoothScrollTo(Math.max(0, scrollX), 0);
                    break;
                }
            }
        }
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            List<Reservation> searchResults = new ArrayList<>();
            for (Reservation r : filteredReservations) {
                if (r.getGuestName().toLowerCase().contains(query.toLowerCase()) ||
                        r.getGuestEmail().toLowerCase().contains(query.toLowerCase()) ||
                        r.getReservationNumber().toLowerCase().contains(query.toLowerCase())) {
                    searchResults.add(r);
                }
            }
            reservationAdapter.updateList(searchResults);
            updateEmptyStateForList(searchResults);
        } else {
            applyFilter();
        }
    }

    private void updateEmptyStateForList(List<Reservation> list) {
        if (list.isEmpty()) {
            tvNoReservations.setText("No results found for your search");
            tvNoReservations.setVisibility(View.VISIBLE);
            rvReservations.setVisibility(View.GONE);
        } else {
            tvNoReservations.setVisibility(View.GONE);
            rvReservations.setVisibility(View.VISIBLE);
        }
    }

    private void refreshData() {
        loadAllReservations();
        setupFilterButtons();
    }

    private void checkPendingReservations() {
        int pendingCount = statusCountMap.getOrDefault("pending", 0);
        if (pendingCount > 0) {
            Toast.makeText(this, "You have " + pendingCount + " pending reservations to review",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }

    private void confirmReservation(Reservation reservation) {
        if (!"pending".equals(reservation.getStatus())) {
            Toast.makeText(this, "Only pending reservations can be confirmed", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Reservation")
                .setMessage("Confirm reservation #" + reservation.getReservationNumber() +
                        " for " + reservation.getGuestName() + "?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    reservation.setStatus("confirmed");
                    int result = dbHelper.updateReservation(reservation);

                    if (result > 0) {
                        sendGuestConfirmationNotification(reservation);

                        sendStaffSelfNotification(reservation, "confirmed");

                        Toast.makeText(this, "Reservation confirmed", Toast.LENGTH_SHORT).show();
                        refreshData();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void rejectReservation(Reservation reservation) {
        if (!"pending".equals(reservation.getStatus())) {
            Toast.makeText(this, "Only pending reservations can be rejected", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Reject Reservation")
                .setMessage("Are you sure you want to reject reservation #" +
                        reservation.getReservationNumber() + " for " +
                        reservation.getGuestName() + "?")
                .setPositiveButton("Reject", (dialog, which) -> {
                    reservation.setStatus("cancelled");
                    int result = dbHelper.updateReservation(reservation);

                    if (result > 0) {
                        sendGuestRejectionNotification(reservation);

                        sendStaffSelfNotification(reservation, "rejected");

                        Toast.makeText(this, "Reservation rejected", Toast.LENGTH_SHORT).show();
                        refreshData();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendGuestConfirmationNotification(Reservation reservation) {
        if (GuestProfileActivity.shouldSendNotification(this.getApplicationContext(), "booking_confirmation")) {
            NotificationHelper notificationHelper = new NotificationHelper(this);
            String title = "Reservation Confirmed!";
            String message = "Great news! Your reservation #" + reservation.getReservationNumber() +
                    " has been confirmed for " + reservation.getDate() + " at " + reservation.getTime() +
                    ". We look forward to serving you!";

            notificationHelper.sendNotificationToUser(title, message,
                    reservation.getReservationNumber(), reservation.getGuestEmail());
        }
    }

    private void sendGuestRejectionNotification(Reservation reservation) {
        if (GuestProfileActivity.shouldSendNotification(this.getApplicationContext(), "cancellation")) {
            NotificationHelper notificationHelper = new NotificationHelper(this);
            String title = "Reservation Not Available";
            String message = "Sorry, your reservation #" + reservation.getReservationNumber() +
                    " could not be confirmed at this time. Please contact us or make a new reservation.";

            notificationHelper.sendNotificationToUser(title, message,
                    reservation.getReservationNumber(), reservation.getGuestEmail());
        }
    }

    private void sendStaffSelfNotification(Reservation reservation, String action) {
        if (StaffProfileActivity.shouldSendNotificationToStaff(this,
                action.equals("confirmed") ? "reservation_update" : "cancellation")) {
            NotificationHelper notificationHelper = new NotificationHelper(this);
            String title = action.equals("confirmed") ? "Reservation Confirmed" : "Reservation Rejected";
            String message = "You " + action + " reservation #" + reservation.getReservationNumber() +
                    " for " + reservation.getGuestName();

            notificationHelper.sendNotificationToUser(title, message,
                    reservation.getReservationNumber(), "staff@gmail.com");
        }
    }
}
package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuestMyBookingsActivity extends BaseGuestActivity {

    private RecyclerView rvBookings;
    private GuestReservationAdapter reservationAdapter;
    private DatabaseHelper dbHelper;
    private List<Reservation> allUserReservations = new ArrayList<>();
    private List<Reservation> filteredReservations = new ArrayList<>();
    private TextView tvNoBookings, tvBookingCount, tvResetFilter, badgeCount;
    private LinearLayout filterButtonsContainer;
    private String currentFilter = "all"; // "all", "upcoming", "pending", "confirmed", "cancelled"
    private Map<String, Integer> statusCountMap = new HashMap<>();

    @Override
    protected int getCurrentNavId() {
        return R.id.navBookings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        setupBottomNav();
        dbHelper = new DatabaseHelper(this);

        initializeUI();
        loadUserReservations();
        setupFilterButtons();
    }

    private void initializeUI() {
        rvBookings = findViewById(R.id.rv_bookings);
        tvNoBookings = findViewById(R.id.tv_no_bookings);
        tvBookingCount = findViewById(R.id.tv_booking_count);
        tvResetFilter = findViewById(R.id.tv_reset_filter);
        badgeCount = findViewById(R.id.badge_count);
        filterButtonsContainer = findViewById(R.id.filter_buttons_container);

        // Set up notification bell click
        ImageView icBell = findViewById(R.id.ic_bell);
        icBell.setOnClickListener(v -> {
            Intent intent = new Intent(GuestMyBookingsActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        if (badgeCount != null) {
            badgeCount.setVisibility(View.GONE);
        }

        tvResetFilter.setOnClickListener(v -> {
            currentFilter = "all";
            applyFilter();
            updateFilterButtons();
        });

        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        reservationAdapter = new GuestReservationAdapter(this, filteredReservations);
        rvBookings.setAdapter(reservationAdapter);

        reservationAdapter.setOnReservationClickListener(new GuestReservationAdapter.OnReservationClickListener() {
            @Override
            public void onEditClick(Reservation reservation) {
                editReservation(reservation);
            }

            @Override
            public void onCancelClick(Reservation reservation) {
                cancelReservation(reservation);
            }
        });
    }

    private void loadUserReservations() {
        String userEmail = UserSessionManager.getUserEmail(this);
        if (!userEmail.isEmpty()) {
            allUserReservations.clear();
            statusCountMap.clear();

            List<Reservation> allReservations = dbHelper.getAllReservations();
            for (Reservation reservation : allReservations) {
                if (reservation.getGuestEmail().equals(userEmail)) {
                    allUserReservations.add(reservation);

                    String status = reservation.getStatus().toLowerCase();
                    statusCountMap.put(status, statusCountMap.getOrDefault(status, 0) + 1);
                }
            }

            updateBookingCountText();
            applyFilter();
        }
    }

    private void updateBookingCountText() {
        int total = allUserReservations.size();
        int upcoming = getUpcomingCount();

        String countText;
        if (total == 0) {
            countText = "No bookings yet";
        } else if (currentFilter.equals("all")) {
            countText = total + " booking" + (total > 1 ? "s" : "") + " total";
        } else if (currentFilter.equals("upcoming")) {
            countText = upcoming + " upcoming booking" + (upcoming > 1 ? "s" : "");
        } else {
            String status = currentFilter;
            int count = statusCountMap.getOrDefault(status, 0);
            countText = count + " " + status + " booking" + (count > 1 ? "s" : "");
        }

        tvBookingCount.setText(countText);
    }

    private int getUpcomingCount() {
        int count = 0;
        for (Reservation r : allUserReservations) {
            String status = r.getStatus().toLowerCase();
            if (status.equals("pending") || status.equals("confirmed")) {
                count++;
            }
        }
        return count;
    }

    private void setupFilterButtons() {
        filterButtonsContainer.removeAllViews();

        Set<String> statusesToShow = new HashSet<>();
        statusesToShow.add("all");

        for (String status : statusCountMap.keySet()) {
            if (statusCountMap.get(status) > 0) {
                statusesToShow.add(status);
            }
        }

        boolean hasPending = statusCountMap.getOrDefault("pending", 0) > 0;
        boolean hasConfirmed = statusCountMap.getOrDefault("confirmed", 0) > 0;
        if (hasPending || hasConfirmed) {
            statusesToShow.add("upcoming");
        }

        String[] buttonOrder = {"all", "upcoming", "pending", "confirmed", "cancelled"};

        for (String status : buttonOrder) {
            if (statusesToShow.contains(status)) {
                addFilterButton(status);
            }
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
        });

        filterButtonsContainer.addView(button);
    }

    private String getButtonTextForStatus(String status) {
        switch (status) {
            case "all": return "All";
            case "upcoming": return "Upcoming";
            case "pending": return "Pending";
            case "confirmed": return "Confirmed";
            case "cancelled": return "Cancelled";
            default: return status;
        }
    }

    private int getCountForStatus(String status) {
        switch (status) {
            case "all": return allUserReservations.size();
            case "upcoming": return getUpcomingCount();
            default: return statusCountMap.getOrDefault(status, 0);
        }
    }

    private int getStrokeColorForStatus(String status) {
        switch (status) {
            case "all": return R.color.main_orange;
            case "upcoming": return R.color.main_orange;
            case "pending": return R.color.status_pending;
            case "confirmed": return R.color.status_confirmed;
            case "cancelled": return R.color.status_cancelled;
            default: return R.color.muted_text;
        }
    }

    private void updateFilterButtons() {
        tvResetFilter.setVisibility(currentFilter.equals("all") ? View.GONE : View.VISIBLE);

        for (int i = 0; i < filterButtonsContainer.getChildCount(); i++) {
            View child = filterButtonsContainer.getChildAt(i);
            if (child instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) child;
                String buttonStatus = (String) button.getTag();

                boolean isSelected = buttonStatus.equals(currentFilter);

                if (isSelected) {
                    int bgColorResId = getBackgroundColorForStatus(buttonStatus);
                    button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                            getResources().getColor(bgColorResId)));
                    button.setTextColor(getResources().getColor(android.R.color.white));
                    button.setStrokeColorResource(android.R.color.transparent);
                } else {
                    button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                            getResources().getColor(android.R.color.transparent)));
                    button.setTextColor(getResources().getColor(android.R.color.black));
                    int strokeColorResId = getStrokeColorForStatus(buttonStatus);
                    button.setStrokeColorResource(strokeColorResId);
                }
            }
        }

        updateBookingCountText();
        scrollToSelectedButton();
    }

    private void scrollToSelectedButton() {
        HorizontalScrollView scrollView = findViewById(R.id.filter_scroll_view);
        for (int i = 0; i < filterButtonsContainer.getChildCount(); i++) {
            View child = filterButtonsContainer.getChildAt(i);
            if (child instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) child;
                String buttonStatus = (String) button.getTag();

                if (buttonStatus.equals(currentFilter)) {
                    int scrollX = button.getLeft() - (scrollView.getWidth() - button.getWidth()) / 2;
                    scrollView.smoothScrollTo(Math.max(0, scrollX), 0);
                    break;
                }
            }
        }
    }

    private int getBackgroundColorForStatus(String status) {
        switch (status) {
            case "all": return R.color.main_orange;
            case "upcoming": return R.color.main_orange;
            case "pending": return R.color.status_pending;
            case "confirmed": return R.color.status_confirmed;
            case "cancelled": return R.color.status_cancelled;
            default: return R.color.muted_text;
        }
    }

    private void applyFilter() {
        filteredReservations.clear();

        for (Reservation reservation : allUserReservations) {
            String status = reservation.getStatus().toLowerCase();

            boolean shouldInclude = false;
            switch (currentFilter) {
                case "all":
                    shouldInclude = true;
                    break;
                case "upcoming":
                    shouldInclude = status.equals("pending") || status.equals("confirmed");
                    break;
                default:
                    shouldInclude = status.equals(currentFilter);
                    break;
            }

            if (shouldInclude) {
                filteredReservations.add(reservation);
            }
        }

        reservationAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredReservations.isEmpty()) {
            String message;
            if (allUserReservations.isEmpty()) {
                message = "You don't have any bookings yet";
            } else if (currentFilter.equals("all")) {
                message = "No bookings found";
            } else {
                message = "No " + currentFilter + " bookings";
            }

            tvNoBookings.setText(message);
            tvNoBookings.setVisibility(View.VISIBLE);
            rvBookings.setVisibility(View.GONE);
        } else {
            tvNoBookings.setVisibility(View.GONE);
            rvBookings.setVisibility(View.VISIBLE);
        }
    }

    private void editReservation(Reservation reservation) {
        if ("cancelled".equalsIgnoreCase(reservation.getStatus())) {
            Toast.makeText(this, "Cannot edit a cancelled reservation", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GuestEditBookingActivity.class);
        intent.putExtra("RESERVATION_ID", reservation.getId());
        startActivity(intent);
    }

    private void cancelReservation(Reservation reservation) {
        if ("cancelled".equalsIgnoreCase(reservation.getStatus())) {
            Toast.makeText(this, "Reservation is already cancelled", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel reservation " +
                        reservation.getReservationNumber() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    reservation.setStatus("cancelled");
                    int result = dbHelper.updateReservation(reservation);
                    if (result > 0) {
                        sendGuestCancellationNotification(reservation);

                        sendStaffCancellationNotification(reservation);

                        Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                        loadUserReservations();
                        setupFilterButtons();
                    } else {
                        Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void sendGuestCancellationNotification(Reservation reservation) {
        if (GuestProfileActivity.shouldSendNotification(this, "cancellation")) {
            NotificationHelper notificationHelper = new NotificationHelper(this);
            String title = "Reservation Cancelled";
            String message = "Your reservation #" + reservation.getReservationNumber() +
                    " has been cancelled successfully";

            notificationHelper.sendReservationNotification(title, message, reservation.getReservationNumber());
        }
    }

    private void sendStaffCancellationNotification(Reservation reservation) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        String title = "Reservation Cancelled by Guest";
        String message = "Guest " + reservation.getGuestName() + " cancelled reservation #" +
                reservation.getReservationNumber();

        String staffEmail = "staff@gmail.com";
        notificationHelper.sendNotificationToUser(title, message,
                reservation.getReservationNumber(), staffEmail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserReservations();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
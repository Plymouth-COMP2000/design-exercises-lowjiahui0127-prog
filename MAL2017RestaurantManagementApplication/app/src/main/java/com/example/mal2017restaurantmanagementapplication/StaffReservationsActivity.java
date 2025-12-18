package com.example.mal2017restaurantmanagementapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StaffReservationsActivity extends BaseStaffActivity {

    private RecyclerView rvReservations;
    private ReservationAdapter reservationAdapter;
    private DatabaseHelper dbHelper;
    private List<Reservation> reservations = new ArrayList<>();
    private TextView tvNoReservations;

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
        loadReservations();

        // Check for pending reservations notification
        checkPendingReservations();
    }

    private void initializeUI() {
        rvReservations = findViewById(R.id.rv_reservations);
        tvNoReservations = findViewById(R.id.tv_no_reservations);

        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        reservationAdapter = new ReservationAdapter(this, reservations, true);
        rvReservations.setAdapter(reservationAdapter);

        reservationAdapter.setOnReservationClickListener(new ReservationAdapter.OnReservationClickListener() {
            @Override
            public void onEditClick(Reservation reservation) {
                // Not used in staff view
            }

            @Override
            public void onCancelClick(Reservation reservation) {
                // Not used in staff view
            }

            @Override
            public void onDeleteClick(Reservation reservation) {
                deleteReservation(reservation);
            }

            @Override
            public void onStatusChangeClick(Reservation reservation, String newStatus) {
                updateReservationStatus(reservation, newStatus);
            }
        });
    }

    private void loadReservations() {
        reservations.clear();
        reservations.addAll(dbHelper.getAllReservations());
        reservationAdapter.notifyDataSetChanged();

        updateEmptyState();
    }

    private void updateEmptyState() {
        if (reservations.isEmpty()) {
            tvNoReservations.setVisibility(View.VISIBLE);
            rvReservations.setVisibility(View.GONE);
        } else {
            tvNoReservations.setVisibility(View.GONE);
            rvReservations.setVisibility(View.VISIBLE);
        }
    }

    private void checkPendingReservations() {
        int pendingCount = dbHelper.getPendingReservationsCount();
        if (pendingCount > 0) {
            Toast.makeText(this, "You have " + pendingCount + " pending reservations",
                    Toast.LENGTH_LONG).show();

            // Send notification
            sendNotification("New Reservations",
                    "You have " + pendingCount + " new reservation(s) pending approval.");
        }
    }

    private void updateReservationStatus(Reservation reservation, String newStatus) {
        reservation.setStatus(newStatus);
        int result = dbHelper.updateReservation(reservation);

        if (result > 0) {
            String action = "confirmed".equals(newStatus) ? "approved" : "rejected";
            Toast.makeText(this, "Reservation " + action + " successfully", Toast.LENGTH_SHORT).show();
            loadReservations();

            // Send notification to staff
            sendNotification("Reservation " + action,
                    "Reservation " + reservation.getReservationNumber() + " has been " + action + ".");
        } else {
            Toast.makeText(this, "Failed to update reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteReservation(Reservation reservation) {
        int result = dbHelper.deleteReservation(reservation.getId());
        if (result > 0) {
            Toast.makeText(this, "Reservation deleted", Toast.LENGTH_SHORT).show();
            loadReservations();
        } else {
            Toast.makeText(this, "Failed to delete reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String title, String message) {
        Toast.makeText(this, title + ": " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
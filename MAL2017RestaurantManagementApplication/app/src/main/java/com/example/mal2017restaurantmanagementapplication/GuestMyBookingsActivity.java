package com.example.mal2017restaurantmanagementapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GuestMyBookingsActivity extends BaseGuestActivity {

    private RecyclerView rvBookings;
    private ReservationAdapter reservationAdapter;
    private DatabaseHelper dbHelper;
    private List<Reservation> reservations = new ArrayList<>();
    private TextView tvNoBookings;

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
        loadReservations();
    }

    private void initializeUI() {
        rvBookings = findViewById(R.id.rv_bookings);
        tvNoBookings = findViewById(R.id.tv_no_bookings);

        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        reservationAdapter = new ReservationAdapter(this, reservations, false);
        rvBookings.setAdapter(reservationAdapter);

        reservationAdapter.setOnReservationClickListener(new ReservationAdapter.OnReservationClickListener() {
            @Override
            public void onEditClick(Reservation reservation) {
                editReservation(reservation);
            }

            @Override
            public void onCancelClick(Reservation reservation) {
                cancelReservation(reservation);
            }

            @Override
            public void onDeleteClick(Reservation reservation) {
                deleteReservation(reservation);
            }

            @Override
            public void onStatusChangeClick(Reservation reservation, String newStatus) {
                // Not used in guest view
            }
        });
    }

    private void loadReservations() {
        reservations.clear();
        reservations.addAll(dbHelper.getUpcomingReservations());
        reservationAdapter.notifyDataSetChanged();

        updateEmptyState();
    }

    private void updateEmptyState() {
        if (reservations.isEmpty()) {
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
                    int result = dbHelper.cancelReservation(reservation.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                        loadReservations();

                        // Send notification
                        sendNotification("Reservation Cancelled",
                                "Your reservation " + reservation.getReservationNumber() + " has been cancelled.");
                    } else {
                        Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteReservation(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Reservation")
                .setMessage("Are you sure you want to permanently delete reservation " +
                        reservation.getReservationNumber() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = dbHelper.deleteReservation(reservation.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Reservation deleted", Toast.LENGTH_SHORT).show();
                        loadReservations();
                    } else {
                        Toast.makeText(this, "Failed to delete reservation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
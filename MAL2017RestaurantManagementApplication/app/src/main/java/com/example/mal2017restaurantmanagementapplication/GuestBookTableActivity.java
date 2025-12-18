package com.example.mal2017restaurantmanagementapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GuestBookTableActivity extends BaseGuestActivity {

    private int guestCount = 1;
    private MaterialButton selectedTimeButton = null;
    private TextView tvDateValue;
    private EditText etSpecialRequests;
    private DatabaseHelper dbHelper;
    private String selectedTime = "";

    @Override
    protected int getCurrentNavId() {
        return R.id.navBookTable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_table);

        setupBottomNav();
        dbHelper = new DatabaseHelper(this);

        initializeUI();
        setupDatePicker();
        setupGuestCounter();
        setupTimeSlots();
        setupConfirmButton();
    }

    private void initializeUI() {
        View dateContainer = findViewById(R.id.dateContainer);
        tvDateValue = findViewById(R.id.tvDateValue);
        etSpecialRequests = findViewById(R.id.et_special_requests);

        // Set default date to tomorrow
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String tomorrow = dateFormat.format(calendar.getTime());
        tvDateValue.setText(tomorrow);
        tvDateValue.setTextColor(Color.BLACK);
    }

    private void setupDatePicker() {
        View dateContainer = findViewById(R.id.dateContainer);
        dateContainer.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    tvDateValue.setText(selectedDate);
                    tvDateValue.setTextColor(Color.BLACK);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    private void setupGuestCounter() {
        TextView tvCount = findViewById(R.id.tvCount);
        MaterialButton btnPlus = findViewById(R.id.btnPlus);
        MaterialButton btnMinus = findViewById(R.id.btnMinus);

        btnPlus.setOnClickListener(v -> {
            if (guestCount < 12) {
                guestCount++;
                tvCount.setText(String.valueOf(guestCount));
                btnPlus.setAlpha(1.0f);
            } else {
                btnPlus.setAlpha(0.5f);
                Toast.makeText(this, "Maximum 12 guests", Toast.LENGTH_SHORT).show();
            }
        });

        btnMinus.setOnClickListener(v -> {
            if (guestCount > 1) {
                guestCount--;
                tvCount.setText(String.valueOf(guestCount));
            }
        });
    }

    private void setupTimeSlots() {
        GridLayout timeGrid = findViewById(R.id.timeGrid);
        for (int i = 0; i < timeGrid.getChildCount(); i++) {
            View child = timeGrid.getChildAt(i);
            if (child instanceof MaterialButton) {
                MaterialButton btn = (MaterialButton) child;
                btn.setOnClickListener(v -> {
                    // Reset previous button
                    if (selectedTimeButton != null) {
                        selectedTimeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F5F5F5")));
                        selectedTimeButton.setStrokeWidth(0);
                        selectedTimeButton.setTextColor(Color.parseColor("#333333"));
                    }

                    // Highlight selected button
                    selectedTimeButton = btn;
                    selectedTimeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF8F2")));
                    selectedTimeButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FF6900")));
                    selectedTimeButton.setStrokeWidth(4);
                    selectedTimeButton.setTextColor(Color.parseColor("#FF6900"));

                    // Store selected time
                    selectedTime = btn.getText().toString();
                });
            }
        }
    }

    private void setupConfirmButton() {
        MaterialButton btnConfirm = findViewById(R.id.btnConfirmBooking);
        btnConfirm.setOnClickListener(v -> createReservation());
    }

    private void createReservation() {
        // Validate inputs
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = tvDateValue.getText().toString();
        String specialRequests = etSpecialRequests.getText().toString().trim();

        // Check for date format
        if (date.equals("dd/mm/yyyy")) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for time conflicts
        if (dbHelper.hasConflictingReservation(date, selectedTime, 0)) {
            Toast.makeText(this, "This time slot is already booked. Please choose another time.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Create reservation object
        Reservation reservation = new Reservation();
        reservation.setDate(date);
        reservation.setTime(selectedTime);
        reservation.setGuestCount(guestCount);
        reservation.setSpecialRequests(specialRequests);
        reservation.setStatus("pending");
        reservation.setReservationNumber(dbHelper.getNextReservationNumber());

        // Save to database
        long reservationId = dbHelper.addReservation(reservation);

        if (reservationId != -1) {
            // Show success message with reservation number
            String message = "Reservation submitted! Your reservation " +
                    reservation.getReservationNumber() + " is pending approval.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Send notification
            sendNotification("Reservation Submitted",
                    "Your reservation " + reservation.getReservationNumber() + " is pending approval.");

            // Navigate to My Bookings page
            Intent intent = new Intent(this, GuestMyBookingsActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to create reservation. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String title, String message) {
        // This is a simplified notification
        // In a real app, you would use NotificationCompat.Builder
        Toast.makeText(this, title + ": " + message, Toast.LENGTH_LONG).show();

        // TODO: Implement actual notification system
        // NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // if (notificationManager != null) {
        //     NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
        //         .setSmallIcon(R.drawable.ic_notification)
        //         .setContentTitle(title)
        //         .setContentText(message)
        //         .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //
        //     notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        // }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
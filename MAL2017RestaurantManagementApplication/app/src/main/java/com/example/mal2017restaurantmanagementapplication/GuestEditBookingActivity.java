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

public class GuestEditBookingActivity extends BaseGuestActivity {

    private DatabaseHelper dbHelper;
    private Reservation reservation;

    private TextView tvDateValue;
    private EditText etSpecialRequests;
    private MaterialButton selectedTimeButton = null;
    private int guestCount = 1;
    private String selectedTime = "";
    private int reservationId;

    @Override
    protected int getCurrentNavId() {
        return R.id.navBookings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        setupBottomNav();
        dbHelper = new DatabaseHelper(this);

        // Get reservation ID from intent
        reservationId = getIntent().getIntExtra("RESERVATION_ID", -1);
        if (reservationId == -1) {
            Toast.makeText(this, "Invalid reservation", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeUI();
        loadReservationData();
        setupDatePicker();
        setupGuestCounter();
        setupTimeSlots();
        setupButtons();
    }

    private void initializeUI() {
        tvDateValue = findViewById(R.id.tvDateValue);
        etSpecialRequests = findViewById(R.id.et_special_requests);

        // Set up back button
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadReservationData() {
        reservation = dbHelper.getReservationById(reservationId);
        if (reservation != null) {
            tvDateValue.setText(reservation.getDate());
            tvDateValue.setTextColor(Color.BLACK);

            guestCount = reservation.getGuestCount();
            TextView tvCount = findViewById(R.id.tvCount);
            tvCount.setText(String.valueOf(guestCount));

            if (reservation.getSpecialRequests() != null) {
                etSpecialRequests.setText(reservation.getSpecialRequests());
            }

            selectedTime = reservation.getTime();
            highlightSelectedTime();
        } else {
            Toast.makeText(this, "Failed to load reservation data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void highlightSelectedTime() {
        GridLayout timeGrid = findViewById(R.id.timeGrid);
        for (int i = 0; i < timeGrid.getChildCount(); i++) {
            View child = timeGrid.getChildAt(i);
            if (child instanceof MaterialButton) {
                MaterialButton btn = (MaterialButton) child;
                if (btn.getText().toString().equals(selectedTime)) {
                    selectedTimeButton = btn;
                    selectedTimeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF8F2")));
                    selectedTimeButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FF6900")));
                    selectedTimeButton.setStrokeWidth(4);
                    selectedTimeButton.setTextColor(Color.parseColor("#FF6900"));
                    break;
                }
            }
        }
    }

    private void setupDatePicker() {
        View dateContainer = findViewById(R.id.dateContainer);
        dateContainer.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(reservation.getDate()));

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        tvDateValue.setText(selectedDate);
                        tvDateValue.setTextColor(Color.BLACK);
                    }, year, month, day);

            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.DAY_OF_YEAR, -1); // Allow same day edits
            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
            datePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void setupButtons() {
        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        MaterialButton btnSave = findViewById(R.id.btnSaveChanges);

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        // Validate inputs
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = tvDateValue.getText().toString();
        String specialRequests = etSpecialRequests.getText().toString().trim();

        // Check if date changed to "dd/mm/yyyy"
        if (date.equals("dd/mm/yyyy")) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for time conflicts (excluding current reservation)
        boolean dateChanged = !date.equals(reservation.getDate());
        boolean timeChanged = !selectedTime.equals(reservation.getTime());

        if (dateChanged || timeChanged) {
            if (dbHelper.hasConflictingReservation(date, selectedTime, reservationId)) {
                Toast.makeText(this, "This time slot is already booked. Please choose another time.",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Update reservation object
        reservation.setDate(date);
        reservation.setTime(selectedTime);
        reservation.setGuestCount(guestCount);
        reservation.setSpecialRequests(specialRequests);

        // Save changes to database
        int result = dbHelper.updateReservation(reservation);

        if (result > 0) {
            Toast.makeText(this, "Reservation updated successfully", Toast.LENGTH_SHORT).show();

            // Send notification
            sendNotification("Reservation Updated",
                    "Your reservation " + reservation.getReservationNumber() + " has been updated.");

            // Navigate back to My Bookings
            Intent intent = new Intent(this, GuestMyBookingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to update reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String title, String message) {
        Toast.makeText(this, title + ": " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
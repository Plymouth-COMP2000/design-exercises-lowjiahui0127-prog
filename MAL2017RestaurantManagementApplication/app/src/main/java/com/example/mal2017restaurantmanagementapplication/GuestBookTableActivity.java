package com.example.mal2017restaurantmanagementapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GuestBookTableActivity extends BaseGuestActivity {

    private int guestCount = 1;
    private MaterialButton selectedTimeButton = null;
    private TextView tvDateValue;
    private EditText etSpecialRequests;
    private DatabaseHelper dbHelper;
    private String selectedTime = "";
    private String guestName;
    private String guestEmail;

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
        guestName = UserSessionManager.getUserName(this);
        guestEmail = UserSessionManager.getUserEmail(this);

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

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String tomorrow = dateFormat.format(calendar.getTime());
        tvDateValue.setText(tomorrow);
        tvDateValue.setTextColor(Color.BLACK);

        // Set up notification bell click
        ImageView icBell = findViewById(R.id.ic_bell);
        icBell.setOnClickListener(v -> {
            Intent intent = new Intent(GuestBookTableActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });
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
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F5F5F5")));
                btn.setStrokeWidth(0);
                btn.setTextColor(Color.parseColor("#333333"));

                btn.setOnClickListener(v -> {
                    if (selectedTimeButton != null) {
                        selectedTimeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F5F5F5")));
                        selectedTimeButton.setStrokeWidth(0);
                        selectedTimeButton.setTextColor(Color.parseColor("#333333"));
                    }

                    selectedTimeButton = btn;
                    selectedTimeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF8F2")));
                    selectedTimeButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FF6900")));
                    selectedTimeButton.setStrokeWidth(4);
                    selectedTimeButton.setTextColor(Color.parseColor("#FF6900"));

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
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = tvDateValue.getText().toString();
        String specialRequests = etSpecialRequests.getText().toString().trim();

        if (date.equals("dd/mm/yyyy")) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.hasConflictingReservation(date, selectedTime, 0)) {
            Toast.makeText(this, "This time slot is already booked. Please choose another time.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String guestName = UserSessionManager.getUserName(this);
        String guestEmail = UserSessionManager.getUserEmail(this);

        if (guestEmail.isEmpty()) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            UserSessionManager.logout(this);
            return;
        }

        Reservation reservation = new Reservation();
        reservation.setGuestName(guestName);
        reservation.setGuestEmail(guestEmail);
        reservation.setDate(date);
        reservation.setTime(selectedTime);
        reservation.setGuestCount(guestCount);
        reservation.setSpecialRequests(specialRequests);
        reservation.setStatus("pending");
        reservation.setReservationNumber(dbHelper.getNextReservationNumber());

        long reservationId = dbHelper.addReservation(reservation);

        if (reservationId != -1) {
            sendGuestNotification(reservation, "created");
            sendStaffNotification(reservation, "new");

            String message = "Reservation " + reservation.getReservationNumber() +
                    " submitted successfully!";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, GuestMyBookingsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void sendGuestNotification(Reservation reservation, String action) {
        if (GuestProfileActivity.shouldSendNotification(this,
                action.equals("created") ? "booking_confirmation" : "general")) {
            NotificationHelper notificationHelper = new NotificationHelper(this);
            String title = "Reservation Submitted!";
            String message = "Your reservation #" + reservation.getReservationNumber() +
                    " is pending approval for " + reservation.getDate() + " at " + reservation.getTime();

            notificationHelper.sendReservationNotification(title, message, reservation.getReservationNumber());
        }
    }

    private void sendStaffNotification(Reservation reservation, String action) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        String title = "New Reservation Request";
        String message = "Guest " + reservation.getGuestName() + " created a new reservation #" +
                reservation.getReservationNumber() + " for " + reservation.getDate() + " at " +
                reservation.getTime() + " (" + reservation.getGuestCount() + " guests)";

        notificationHelper.sendNotificationToUser(title, message,
                reservation.getReservationNumber(), "staff@gmail.com");
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
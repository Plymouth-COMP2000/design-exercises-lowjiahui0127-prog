package com.example.mal2017restaurantmanagementapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String type; // "reservation_created", "reservation_confirmed", "reservation_cancelled", "reservation_updated"
    private boolean isRead;
    private long timestamp;
    private String reservationNumber;
    private String userEmail;

    public Notification() {
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    public Notification(String title, String message, String type, String reservationNumber, String userEmail) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.reservationNumber = reservationNumber;
        this.userEmail = userEmail;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.id = generateId();
    }

    private String generateId() {
        return userEmail + "_" + timestamp + "_" + reservationNumber;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    // Helper methods
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public int getIconResource() {
        return R.drawable.ic_bell;
    }
}
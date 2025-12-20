package com.example.mal2017restaurantmanagementapplication;

public class Reservation {
    private int id;
    private String guestName;
    private String guestEmail;
    private String date; // Format: "dd/MM/yyyy"
    private String time; // Format: "HH:mm"
    private int guestCount;
    private String specialRequests;
    private String status; // "pending", "confirmed", "cancelled"
    private long createdAt;
    private String reservationNumber; // Format: "B001"

    public Reservation() {
        this.createdAt = System.currentTimeMillis();
        this.status = "pending";
    }

    public Reservation(int id, String guestName, String guestEmail, String date,
                       String time, int guestCount, String specialRequests,
                       String status, String reservationNumber) {
        this.id = id;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.date = date;
        this.time = time;
        this.guestCount = guestCount;
        this.specialRequests = specialRequests;
        this.status = status;
        this.reservationNumber = reservationNumber;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }
}
package com.example.mal2017restaurantmanagementapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    private static final String DATABASE_NAME = "restaurant.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_MENU = "menu";
    private static final String TABLE_RESERVATIONS = "reservations";

    // Menu Table Columns
    private static final String KEY_MENU_ID = "id";
    private static final String KEY_MENU_NAME = "name";
    private static final String KEY_MENU_PRICE = "price";
    private static final String KEY_MENU_DESCRIPTION = "description";
    private static final String KEY_MENU_CATEGORY = "category";
    private static final String KEY_MENU_IMAGE_PATH = "image_path";

    // Reservations Table Columns
    private static final String KEY_RESERVATION_ID = "id";
    private static final String KEY_RESERVATION_DATE = "date";
    private static final String KEY_RESERVATION_TIME = "time";
    private static final String KEY_GUEST_COUNT = "guest_count";
    private static final String KEY_SPECIAL_REQUESTS = "special_requests";
    private static final String KEY_STATUS = "status";
    private static final String KEY_RESERVATION_NUMBER = "reservation_number";
    private static final String KEY_CREATED_AT = "created_at";

    // Table Creation SQL Statements
    private static final String CREATE_TABLE_MENU = "CREATE TABLE " + TABLE_MENU + "("
            + KEY_MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_MENU_NAME + " TEXT NOT NULL,"
            + KEY_MENU_PRICE + " TEXT NOT NULL,"
            + KEY_MENU_DESCRIPTION + " TEXT,"
            + KEY_MENU_CATEGORY + " TEXT,"
            + KEY_MENU_IMAGE_PATH + " TEXT"
            + ")";

    private static final String CREATE_TABLE_RESERVATIONS = "CREATE TABLE " + TABLE_RESERVATIONS + "("
            + KEY_RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_RESERVATION_DATE + " TEXT NOT NULL,"
            + KEY_RESERVATION_TIME + " TEXT NOT NULL,"
            + KEY_GUEST_COUNT + " INTEGER NOT NULL,"
            + KEY_SPECIAL_REQUESTS + " TEXT,"
            + KEY_STATUS + " TEXT DEFAULT 'pending',"
            + KEY_RESERVATION_NUMBER + " TEXT UNIQUE,"
            + KEY_CREATED_AT + " INTEGER NOT NULL"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_MENU);
        db.execSQL(CREATE_TABLE_RESERVATIONS);

        // Insert sample menu items
        insertSampleMenuItems(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);

        // Create tables again
        onCreate(db);
    }

    // ==================== MENU ITEMS OPERATIONS ====================

    // Insert sample menu items (for initial setup)
    private void insertSampleMenuItems(SQLiteDatabase db) {
        List<MenuItem> sampleItems = new ArrayList<>();

        sampleItems.add(new MenuItem(0, "Hawaiian Thin Crust", "RM18.99",
                "10 Thin crust pizza topped with mozzarella cheese, red sauce, chicken toast slices, pineapple, yellow onion.",
                "Main course", "pizza1"));
        sampleItems.add(new MenuItem(0, "Pepperoni Thin Crust", "RM18.99",
                "10 Thin crust pizza topped with mozzarella cheese, red sauce, beef pepperoni.",
                "Main course", "pizza2"));
        sampleItems.add(new MenuItem(0, "Mushroom Thin Crust", "RM18.99",
                "Thin crust pizza topped with mozzarella cheese, red sauce, sauté mushroom.",
                "Main course", "pizza3"));
        sampleItems.add(new MenuItem(0, "Shrimp Thin Crust", "RM19.99",
                "Thin crust pizza topped with mozzarella cheese, grilled shrimp & cherry tomatoes.",
                "Main course", "pizza4"));
        sampleItems.add(new MenuItem(0, "Caesar Salad", "RM8.99",
                "Crispy romaine lettuce with parmesan, croutons and caesar dressing.",
                "Appetizer", "salad"));
        sampleItems.add(new MenuItem(0, "Chocolate Lava Cake", "RM12.99",
                "Warm chocolate cake with molten center.",
                "Dessert", "lava_cake"));

        for (MenuItem item : sampleItems) {
            ContentValues values = new ContentValues();
            values.put(KEY_MENU_NAME, item.getName());
            values.put(KEY_MENU_PRICE, item.getPrice());
            values.put(KEY_MENU_DESCRIPTION, item.getDescription());
            values.put(KEY_MENU_CATEGORY, item.getCategory());
            values.put(KEY_MENU_IMAGE_PATH, item.getImagePath());

            db.insert(TABLE_MENU, null, values);
        }
    }

    // Add new menu item
    public long addMenuItem(MenuItem menuItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MENU_NAME, menuItem.getName());
        values.put(KEY_MENU_PRICE, menuItem.getPrice());
        values.put(KEY_MENU_DESCRIPTION, menuItem.getDescription());
        values.put(KEY_MENU_CATEGORY, menuItem.getCategory());
        values.put(KEY_MENU_IMAGE_PATH, menuItem.getImagePath());

        long id = db.insert(TABLE_MENU, null, values);
        db.close();

        return id;
    }

    // Get all menu items
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MENU + " ORDER BY " + KEY_MENU_ID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MENU_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_NAME)));
                item.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_PRICE)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_DESCRIPTION)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_CATEGORY)));
                item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_IMAGE_PATH)));

                menuItems.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return menuItems;
    }

    // Get menu item by ID
    public MenuItem getMenuItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_MENU +
                " WHERE " + KEY_MENU_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

        MenuItem menuItem = null;
        if (cursor.moveToFirst()) {
            menuItem = new MenuItem();
            menuItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MENU_ID)));
            menuItem.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_NAME)));
            menuItem.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_PRICE)));
            menuItem.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_DESCRIPTION)));
            menuItem.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_CATEGORY)));
            menuItem.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_IMAGE_PATH)));
        }

        cursor.close();
        db.close();

        return menuItem;
    }

    // Update menu item
    public int updateMenuItem(MenuItem menuItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MENU_NAME, menuItem.getName());
        values.put(KEY_MENU_PRICE, menuItem.getPrice());
        values.put(KEY_MENU_DESCRIPTION, menuItem.getDescription());
        values.put(KEY_MENU_CATEGORY, menuItem.getCategory());
        values.put(KEY_MENU_IMAGE_PATH, menuItem.getImagePath());

        int rowsAffected = db.update(TABLE_MENU, values,
                KEY_MENU_ID + " = ?",
                new String[]{String.valueOf(menuItem.getId())});

        db.close();

        return rowsAffected;
    }

    // Delete menu item
    public int deleteMenuItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_MENU,
                KEY_MENU_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();

        return rowsAffected;
    }

    // Search menu items by name or description
    public List<MenuItem> searchMenuItems(String query) {
        List<MenuItem> menuItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MENU +
                " WHERE " + KEY_MENU_NAME + " LIKE ? OR " +
                KEY_MENU_DESCRIPTION + " LIKE ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,
                new String[]{"%" + query + "%", "%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MENU_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_NAME)));
                item.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_PRICE)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_DESCRIPTION)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_CATEGORY)));
                item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_IMAGE_PATH)));

                menuItems.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return menuItems;
    }

    // Get menu items by category
    public List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> menuItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MENU +
                " WHERE " + KEY_MENU_CATEGORY + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MENU_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_NAME)));
                item.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_PRICE)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_DESCRIPTION)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_CATEGORY)));
                item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MENU_IMAGE_PATH)));

                menuItems.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return menuItems;
    }

    // Get distinct categories
    public List<String> getDistinctCategories() {
        List<String> categories = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_MENU_CATEGORY +
                " FROM " + TABLE_MENU +
                " ORDER BY " + KEY_MENU_CATEGORY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                if (category != null && !category.isEmpty()) {
                    categories.add(category);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Add "All Categories" option
        categories.add(0, "All Categories");

        return categories;
    }

    // ==================== RESERVATION OPERATIONS ====================

    // Add a new reservation
    public long addReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RESERVATION_DATE, reservation.getDate());
        values.put(KEY_RESERVATION_TIME, reservation.getTime());
        values.put(KEY_GUEST_COUNT, reservation.getGuestCount());
        values.put(KEY_SPECIAL_REQUESTS, reservation.getSpecialRequests());
        values.put(KEY_STATUS, reservation.getStatus());
        values.put(KEY_RESERVATION_NUMBER, reservation.getReservationNumber());
        values.put(KEY_CREATED_AT, reservation.getCreatedAt());

        long id = db.insert(TABLE_RESERVATIONS, null, values);
        db.close();

        return id;
    }

    // Get all reservations
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RESERVATIONS +
                " ORDER BY " + KEY_RESERVATION_DATE + " DESC, " +
                KEY_RESERVATION_TIME + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation();
                reservation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESERVATION_ID)));
                reservation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_DATE)));
                reservation.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_TIME)));
                reservation.setGuestCount(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GUEST_COUNT)));
                reservation.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SPECIAL_REQUESTS)));
                reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS)));
                reservation.setReservationNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_NUMBER)));
                reservation.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));

                reservations.add(reservation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return reservations;
    }

    // Get upcoming reservations (not cancelled)
    public List<Reservation> getUpcomingReservations() {
        List<Reservation> reservations = new ArrayList<>();

        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        String selectQuery = "SELECT * FROM " + TABLE_RESERVATIONS +
                " WHERE (" + KEY_RESERVATION_DATE + " > ? OR " +
                "(" + KEY_RESERVATION_DATE + " = ? AND " + KEY_RESERVATION_TIME + " >= ?))" +
                " AND " + KEY_STATUS + " != 'cancelled'" +
                " ORDER BY " + KEY_RESERVATION_DATE + " ASC, " +
                KEY_RESERVATION_TIME + " ASC";

        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{currentDate, currentDate, currentTime});

        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation();
                reservation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESERVATION_ID)));
                reservation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_DATE)));
                reservation.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_TIME)));
                reservation.setGuestCount(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GUEST_COUNT)));
                reservation.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SPECIAL_REQUESTS)));
                reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS)));
                reservation.setReservationNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_NUMBER)));
                reservation.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));

                reservations.add(reservation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return reservations;
    }

    // Get reservation by ID
    public Reservation getReservationById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_RESERVATIONS +
                " WHERE " + KEY_RESERVATION_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

        Reservation reservation = null;
        if (cursor.moveToFirst()) {
            reservation = new Reservation();
            reservation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESERVATION_ID)));
            reservation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_DATE)));
            reservation.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_TIME)));
            reservation.setGuestCount(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GUEST_COUNT)));
            reservation.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SPECIAL_REQUESTS)));
            reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS)));
            reservation.setReservationNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_NUMBER)));
            reservation.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));
        }

        cursor.close();
        db.close();

        return reservation;
    }

    // Update reservation
    public int updateReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RESERVATION_DATE, reservation.getDate());
        values.put(KEY_RESERVATION_TIME, reservation.getTime());
        values.put(KEY_GUEST_COUNT, reservation.getGuestCount());
        values.put(KEY_SPECIAL_REQUESTS, reservation.getSpecialRequests());
        values.put(KEY_STATUS, reservation.getStatus());

        int rowsAffected = db.update(TABLE_RESERVATIONS, values,
                KEY_RESERVATION_ID + " = ?",
                new String[]{String.valueOf(reservation.getId())});

        db.close();

        return rowsAffected;
    }

    // Cancel reservation (change status to cancelled)
    public int cancelReservation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, "cancelled");

        int rowsAffected = db.update(TABLE_RESERVATIONS, values,
                KEY_RESERVATION_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();

        return rowsAffected;
    }

    // Delete reservation permanently
    public int deleteReservation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_RESERVATIONS,
                KEY_RESERVATION_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();

        return rowsAffected;
    }

    // Check for conflicting reservations (same date and time, not cancelled)
    public boolean hasConflictingReservation(String date, String time, int excludeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        String[] args;

        if (excludeId > 0) {
            query = "SELECT COUNT(*) FROM " + TABLE_RESERVATIONS +
                    " WHERE " + KEY_RESERVATION_DATE + " = ? AND " +
                    KEY_RESERVATION_TIME + " = ? AND " +
                    KEY_STATUS + " != 'cancelled' AND " +
                    KEY_RESERVATION_ID + " != ?";
            args = new String[]{date, time, String.valueOf(excludeId)};
        } else {
            query = "SELECT COUNT(*) FROM " + TABLE_RESERVATIONS +
                    " WHERE " + KEY_RESERVATION_DATE + " = ? AND " +
                    KEY_RESERVATION_TIME + " = ? AND " +
                    KEY_STATUS + " != 'cancelled'";
            args = new String[]{date, time};
        }

        Cursor cursor = db.rawQuery(query, args);
        cursor.moveToFirst();
        int count = cursor.getInt(0);

        cursor.close();
        db.close();

        return count > 0;
    }

    // Get next reservation number
    public String getNextReservationNumber() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + KEY_RESERVATION_ID + ") FROM " + TABLE_RESERVATIONS;

        Cursor cursor = db.rawQuery(query, null);
        int maxId = 0;
        if (cursor.moveToFirst()) {
            maxId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return "B" + String.format("%03d", maxId + 1);
    }

    // Get reservations by status
    public List<Reservation> getReservationsByStatus(String status) {
        List<Reservation> reservations = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RESERVATIONS +
                " WHERE " + KEY_STATUS + " = ?" +
                " ORDER BY " + KEY_RESERVATION_DATE + " ASC, " +
                KEY_RESERVATION_TIME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{status});

        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation();
                reservation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESERVATION_ID)));
                reservation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_DATE)));
                reservation.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_TIME)));
                reservation.setGuestCount(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GUEST_COUNT)));
                reservation.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SPECIAL_REQUESTS)));
                reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS)));
                reservation.setReservationNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RESERVATION_NUMBER)));
                reservation.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));

                reservations.add(reservation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return reservations;
    }

    // Get pending reservations count (for staff notifications)
    public int getPendingReservationsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_RESERVATIONS +
                " WHERE " + KEY_STATUS + " = 'pending'";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);

        cursor.close();
        db.close();

        return count;
    }
}
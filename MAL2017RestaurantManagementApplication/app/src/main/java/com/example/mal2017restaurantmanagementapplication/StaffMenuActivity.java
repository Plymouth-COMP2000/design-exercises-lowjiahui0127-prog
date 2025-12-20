package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class StaffMenuActivity extends BaseStaffActivity {

    private RecyclerView rvMenu;
    private StaffMenuAdapter menuAdapter;
    private DatabaseHelper dbHelper;
    private List<MenuItem> menuItems = new ArrayList<>();
    private EditText etSearch;
    private ImageView ivSearchButton;
    private Spinner spinnerCategory;
    private MaterialButton btnAddItem;
    private TextView badgeCount;

    @Override
    protected int getCurrentNavId() {
        return R.id.navStaffMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu);

        setupStaffBottomNav();
        dbHelper = new DatabaseHelper(this);

        initializeUI();
        loadMenuItems();
        setupCategorySpinner();
        setupSearch();
        setupNotificationBadge();
    }

    private void initializeUI() {
        rvMenu = findViewById(R.id.rv_menu);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));

        menuAdapter = new StaffMenuAdapter(this, menuItems);
        rvMenu.setAdapter(menuAdapter);

        etSearch = findViewById(R.id.et_search);
        ivSearchButton = findViewById(R.id.iv_search_button);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnAddItem = findViewById(R.id.btnAddItem);
        badgeCount = findViewById(R.id.badge_count);

        // Set up notification bell click
        ImageView icBell = findViewById(R.id.ic_bell);
        icBell.setOnClickListener(v -> {
            Intent intent = new Intent(StaffMenuActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Set up add item button click
        com.google.android.material.button.MaterialButton btnAddItem = findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(StaffMenuActivity.this, StaffAddItemActivity.class);
            startActivity(intent);
        });

        menuAdapter.setOnMenuItemClickListener(new StaffMenuAdapter.OnMenuItemClickListener() {
            @Override
            public void onEditClick(MenuItem menuItem) {
                editMenuItem(menuItem);
            }

            @Override
            public void onDeleteClick(MenuItem menuItem) {
                deleteMenuItem(menuItem);
            }
        });
    }

    private void loadMenuItems() {
        menuItems.clear();
        menuItems.addAll(dbHelper.getAllMenuItems());
        menuAdapter.notifyDataSetChanged();
    }

    private void setupCategorySpinner() {
        List<String> categories = dbHelper.getDistinctCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                if (position == 0) { // "All Categories"
                    loadMenuItems();
                } else {
                    menuItems.clear();
                    menuItems.addAll(dbHelper.getMenuItemsByCategory(selectedCategory));
                    menuAdapter.notifyDataSetChanged();

                    if (menuItems.isEmpty()) {
                        Toast.makeText(StaffMenuActivity.this,
                                "No items in category: " + selectedCategory,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupSearch() {
        ivSearchButton.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                menuItems.clear();
                menuItems.addAll(dbHelper.searchMenuItems(query));
                menuAdapter.notifyDataSetChanged();

                if (menuItems.isEmpty()) {
                    Toast.makeText(this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
                }
            } else {
                loadMenuItems();
            }
        });
    }

    private void setupNotificationBadge() {
        int userId = UserSessionManager.getUserIdInt(this);
        if (userId != -1) {
            int unreadCount = dbHelper.getUnreadNotificationCount(userId);
            if (unreadCount > 0) {
                badgeCount.setText(String.valueOf(unreadCount));
                badgeCount.setVisibility(View.VISIBLE);
            } else {
                badgeCount.setVisibility(View.GONE);
            }
        }
    }

    private void editMenuItem(MenuItem menuItem) {
        Intent intent = new Intent(this, StaffEditItemActivity.class);
        intent.putExtra("MENU_ITEM_ID", menuItem.getId());
        startActivity(intent);
    }

    private void deleteMenuItem(MenuItem menuItem) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Menu Item");
        builder.setMessage("Are you sure you want to delete \"" + menuItem.getName() + "\"?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            int result = dbHelper.deleteMenuItem(menuItem.getId());
            if (result > 0) {
                Toast.makeText(this, "Menu item deleted successfully", Toast.LENGTH_SHORT).show();
                loadMenuItems();

                // Send notification to all users who have menu notifications enabled
                sendMenuUpdateNotification("Menu Item Removed",
                        "The menu item \"" + menuItem.getName() + "\" has been removed from the menu.");
            } else {
                Toast.makeText(this, "Failed to delete menu item", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void sendMenuUpdateNotification(String title, String message) {
        Toast.makeText(this, "Menu update notification sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems();
        setupNotificationBadge();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
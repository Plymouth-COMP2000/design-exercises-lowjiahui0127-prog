package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GuestMenuActivity extends BaseGuestActivity {

    private RecyclerView rvMenu;
    private GuestMenuAdapter menuAdapter;
    private DatabaseHelper dbHelper;
    private List<MenuItem> menuItems = new ArrayList<>();
    private EditText etSearch;
    private ImageView ivSearchButton;
    private Spinner spinnerCategory;

    @Override
    protected int getCurrentNavId() {
        return R.id.navMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setupBottomNav();
        dbHelper = new DatabaseHelper(this);

        initializeUI();
        loadMenuItems();
        setupCategorySpinner();
        setupSearch();
    }

    private void initializeUI() {
        TextView btnBookTable = findViewById(R.id.btnBookTable);
        btnBookTable.setOnClickListener(v -> {
            Intent intent = new Intent(GuestMenuActivity.this, GuestBookTableActivity.class);
            startActivity(intent);
        });

        TextView btnMyBookings = findViewById(R.id.btnMyBookings);
        btnMyBookings.setOnClickListener(v -> {
            Intent intent = new Intent(GuestMenuActivity.this, GuestMyBookingsActivity.class);
            startActivity(intent);
        });

        rvMenu = findViewById(R.id.rv_menu);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));

        menuAdapter = new GuestMenuAdapter(this, menuItems);
        rvMenu.setAdapter(menuAdapter);

        etSearch = findViewById(R.id.et_search);
        ivSearchButton = findViewById(R.id.iv_search_button);
        spinnerCategory = findViewById(R.id.spinner_category);

        // Set up notification bell click
        ImageView icBell = findViewById(R.id.ic_bell);
        icBell.setOnClickListener(v -> {
            Intent intent = new Intent(GuestMenuActivity.this, NotificationsActivity.class);
            startActivity(intent);
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
                        Toast.makeText(GuestMenuActivity.this,
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

    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
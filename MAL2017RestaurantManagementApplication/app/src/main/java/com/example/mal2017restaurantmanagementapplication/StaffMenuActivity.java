package com.example.mal2017restaurantmanagementapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    }

    private void initializeUI() {
        rvMenu = findViewById(R.id.rv_menu);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));

        menuAdapter = new StaffMenuAdapter(this, menuItems);
        rvMenu.setAdapter(menuAdapter);

        MaterialButton btnAddItem = findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(v -> showAddMenuItemDialog());

        menuAdapter.setOnMenuItemClickListener(new StaffMenuAdapter.OnMenuItemClickListener() {
            @Override
            public void onEditClick(MenuItem menuItem) {
                showEditMenuItemDialog(menuItem);
            }

            @Override
            public void onDeleteClick(MenuItem menuItem) {
                showDeleteConfirmationDialog(menuItem);
            }
        });
    }

    private void loadMenuItems() {
        menuItems.clear();
        menuItems.addAll(dbHelper.getAllMenuItems());
        menuAdapter.notifyDataSetChanged();
    }

    private void showAddMenuItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Menu Item");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_menu_item, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etPrice = dialogView.findViewById(R.id.et_price);
        EditText etDescription = dialogView.findViewById(R.id.et_description);
        EditText etCategory = dialogView.findViewById(R.id.et_category);
        EditText etImagePath = dialogView.findViewById(R.id.et_image_path);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String price = etPrice.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String imagePath = etImagePath.getText().toString().trim();

            if (name.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Name and price are required", Toast.LENGTH_SHORT).show();
                return;
            }

            MenuItem menuItem = new MenuItem();
            menuItem.setName(name);
            menuItem.setPrice(price);
            menuItem.setDescription(description);
            menuItem.setCategory(category.isEmpty() ? "Main course" : category);
            menuItem.setImagePath(imagePath.isEmpty() ? "default_food" : imagePath);

            long id = dbHelper.addMenuItem(menuItem);
            if (id != -1) {
                Toast.makeText(this, "Menu item added successfully", Toast.LENGTH_SHORT).show();
                loadMenuItems();
            } else {
                Toast.makeText(this, "Failed to add menu item", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditMenuItemDialog(MenuItem menuItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Menu Item");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_menu_item, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etPrice = dialogView.findViewById(R.id.et_price);
        EditText etDescription = dialogView.findViewById(R.id.et_description);
        EditText etCategory = dialogView.findViewById(R.id.et_category);
        EditText etImagePath = dialogView.findViewById(R.id.et_image_path);

        etName.setText(menuItem.getName());
        etPrice.setText(menuItem.getPrice());
        etDescription.setText(menuItem.getDescription());
        etCategory.setText(menuItem.getCategory());
        etImagePath.setText(menuItem.getImagePath());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String price = etPrice.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String imagePath = etImagePath.getText().toString().trim();

            if (name.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Name and price are required", Toast.LENGTH_SHORT).show();
                return;
            }

            menuItem.setName(name);
            menuItem.setPrice(price);
            menuItem.setDescription(description);
            menuItem.setCategory(category.isEmpty() ? "Main course" : category);
            menuItem.setImagePath(imagePath.isEmpty() ? "default_food" : imagePath);

            int result = dbHelper.updateMenuItem(menuItem);
            if (result > 0) {
                Toast.makeText(this, "Menu item updated successfully", Toast.LENGTH_SHORT).show();
                loadMenuItems();
            } else {
                Toast.makeText(this, "Failed to update menu item", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteConfirmationDialog(MenuItem menuItem) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Menu Item")
                .setMessage("Are you sure you want to delete \"" + menuItem.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = dbHelper.deleteMenuItem(menuItem.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show();
                        loadMenuItems();
                    } else {
                        Toast.makeText(this, "Failed to delete menu item", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
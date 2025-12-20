package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class StaffEditItemActivity extends BaseStaffActivity {

    private DatabaseHelper dbHelper;
    private Spinner spinnerCategory;
    private EditText etItemName, etPrice, etDescription;
    private MaterialButton btnSaveChanges, btnCancel;
    private ImageView ivFoodImage;
    private MenuItem menuItem;
    private int menuItemId;
    private String selectedCategory;
    private String selectedImagePath;

    @Override
    protected int getCurrentNavId() {
        return R.id.navStaffMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_edit_item);

        dbHelper = new DatabaseHelper(this);

        // Get menu item ID from intent
        menuItemId = getIntent().getIntExtra("MENU_ITEM_ID", -1);
        if (menuItemId == -1) {
            Toast.makeText(this, "Invalid menu item", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load menu item data
        menuItem = dbHelper.getMenuItemById(menuItemId);
        if (menuItem == null) {
            Toast.makeText(this, "Menu item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeUI();
        setupCategorySpinner();
        setupImageSelector();
        setupButtons();
        populateForm();
    }

    private void initializeUI() {
        spinnerCategory = findViewById(R.id.spinner_category);
        etItemName = findViewById(R.id.et_item_name);
        etPrice = findViewById(R.id.et_price);
        etDescription = findViewById(R.id.et_description);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
        ivFoodImage = findViewById(R.id.iv_food_image);

        // Set up back button
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("Main course");
        categories.add("Appetizer");
        categories.add("Dessert");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set initial selection
        int position = categories.indexOf(menuItem.getCategory());
        if (position >= 0) {
            spinnerCategory.setSelection(position);
        }

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupImageSelector() {
        ivFoodImage.setOnClickListener(v -> {
            cycleImageSelection();
        });

        // Set initial image
        selectedImagePath = menuItem.getImagePath();
        setImageByPath(selectedImagePath);
    }

    private void cycleImageSelection() {
        String[] imagePaths = {"pizza1", "pizza2", "pizza3", "pizza4", "pizza5", "pasta1", "pasta2", "pasta3", "salad", "lava_cake", "default_food"};

        for (int i = 0; i < imagePaths.length; i++) {
            if (imagePaths[i].equals(selectedImagePath)) {
                selectedImagePath = imagePaths[(i + 1) % imagePaths.length];
                setImageByPath(selectedImagePath);
                break;
            }
        }
    }

    private void setImageByPath(String imagePath) {
        int imageResId = getResources().getIdentifier(
                imagePath,
                "drawable",
                getPackageName()
        );

        if (imageResId != 0) {
            ivFoodImage.setImageResource(imageResId);
        } else {
            ivFoodImage.setImageResource(R.drawable.pizza1);
        }
    }

    private void setupButtons() {
        btnSaveChanges.setOnClickListener(v -> saveChanges());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void populateForm() {
        etItemName.setText(menuItem.getName());

        // Remove "RM" prefix if present for editing
        String price = menuItem.getPrice();
        if (price.startsWith("RM")) {
            price = price.substring(2);
        }
        etPrice.setText(price);

        etDescription.setText(menuItem.getDescription());
        selectedCategory = menuItem.getCategory();
    }

    private void saveChanges() {
        String itemName = etItemName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validation
        if (itemName.isEmpty()) {
            Toast.makeText(this, "Please enter item name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (price.isEmpty()) {
            Toast.makeText(this, "Please enter price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format price
        if (!price.startsWith("RM")) {
            price = "RM" + price;
        }

        // Update menu item
        menuItem.setName(itemName);
        menuItem.setPrice(price);
        menuItem.setDescription(description);
        menuItem.setCategory(selectedCategory);
        menuItem.setImagePath(selectedImagePath);

        // Save to database
        int result = dbHelper.updateMenuItem(menuItem);
        if (result > 0) {
            Toast.makeText(this, "Menu item updated successfully", Toast.LENGTH_SHORT).show();

            // 直接返回Menu页面
            Intent intent = new Intent(this, StaffMenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 清除返回栈
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update menu item", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mal2017restaurantmanagementapplication.api.VolleyApiService;

import org.json.JSONException;
import org.json.JSONObject;

public class GuestCreateAccountActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private ImageView ivPasswordToggle, ivConfirmPasswordToggle;
    private TextView btnCreateAccount, tvSignIn;
    private VolleyApiService apiService;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        apiService = VolleyApiService.getInstance(this);

        initViews();
        setupPasswordToggle();
        setupCreateAccountButton();
        setupSignInLink();
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        ivPasswordToggle = findViewById(R.id.ivPasswordToggle);
        ivConfirmPasswordToggle = findViewById(R.id.ivConfirmPasswordToggle);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private void setupPasswordToggle() {
        ivPasswordToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(null);
                ivPasswordToggle.setImageResource(R.drawable.ic_visibility_on);
            } else {
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                ivPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        ivConfirmPasswordToggle.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setTransformationMethod(null);
                ivConfirmPasswordToggle.setImageResource(R.drawable.ic_visibility_on);
            } else {
                etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                ivConfirmPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });
    }

    private void setupCreateAccountButton() {
        btnCreateAccount.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validateAllFields(fullName, email, phone, password, confirmPassword)) {
                registerAccount(fullName, email, phone, password);
            }
        });
    }

    private boolean validateAllFields(String fullName, String email, String phone,
                                      String password, String confirmPassword) {

        if (TextUtils.isEmpty(fullName)) {
            showErrorMessage(etFullName, "Please enter your full name");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            showErrorMessage(etEmail, "Please enter your email address");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorMessage(etEmail, "Please enter a valid email address");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            showErrorMessage(etPhone, "Please enter your phone number");
            return false;
        }

        if (phone.length() < 10) {
            showErrorMessage(etPhone, "Phone number must be at least 10 digits");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            showErrorMessage(etPassword, "Please enter a password");
            return false;
        }

        if (password.length() < 6) {
            showErrorMessage(etPassword, "Password must be at least 6 characters");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            showErrorMessage(etConfirmPassword, "Please confirm your password");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showErrorMessage(etConfirmPassword, "Passwords do not match");
            return false;
        }

        return true;
    }

    private void showErrorMessage(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void registerAccount(String fullName, String email, String phone, String password) {
        btnCreateAccount.setText("Creating Account...");
        btnCreateAccount.setEnabled(false);

        JSONObject userData = new JSONObject();
        try {
            String username = email.split("@")[0] + "_" + System.currentTimeMillis();
            String[] nameParts = fullName.split(" ");
            String firstName = nameParts.length > 0 ? nameParts[0] : fullName;
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            userData.put("username", username);
            userData.put("password", password);
            userData.put("firstname", firstName);
            userData.put("lastname", lastName);
            userData.put("email", email);
            userData.put("contact", phone);
            userData.put("usertype", "guest");

        } catch (JSONException e) {
            btnCreateAccount.setText("Register");
            btnCreateAccount.setEnabled(true);
            Toast.makeText(this, "Error creating user data", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.createUser(userData, new VolleyApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    btnCreateAccount.setText("Register");
                    btnCreateAccount.setEnabled(true);

                    Toast.makeText(GuestCreateAccountActivity.this,
                            "Account created successfully! Please login with your credentials.",
                            Toast.LENGTH_LONG).show();

                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnCreateAccount.setText("Register");
                    btnCreateAccount.setEnabled(true);

                    Toast.makeText(GuestCreateAccountActivity.this,
                            "Failed to create account: " + error,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupSignInLink() {
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
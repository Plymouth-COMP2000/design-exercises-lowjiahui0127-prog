package com.example.mal2017restaurantmanagementapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mal2017restaurantmanagementapplication.api.VolleyApiService;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout tabGuest, tabStaff, layoutCreateAccount;
    private TextView tvGuest, tvStaff, tvCreateAccount, btnLogin, tvForgotPassword;
    private View underlineGuest, underlineStaff;
    private EditText etEmail, etPassword;
    private CheckBox rememberMeCheck;
    private ImageView ivPasswordToggle;

    private boolean isPasswordVisible = false;
    private String currentRole = "guest";
    private VolleyApiService apiService;

    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkAndRequestNotificationPermission();

        if (UserSessionManager.isLoggedIn(this)) {
            redirectToDashboard();
            return;
        }

        apiService = VolleyApiService.getInstance(this);
        apiService.createStudentDatabase(new VolleyApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("LoginActivity", "Student database checked/created");
            }

            @Override
            public void onError(String error) {
                Log.e("LoginActivity", "Database check failed: " + error);
            }
        });

        initViews();
        setupTabListeners();
        setupPasswordToggle();
        setupLoginButton();
        setupCreateAccount();
        setupForgotPassword();

        showGuestUI();
        autoFillRememberedEmail();
    }

    private void initViews() {
        tabGuest = findViewById(R.id.tabGuest);
        tabStaff = findViewById(R.id.tabStaff);
        layoutCreateAccount = findViewById(R.id.layoutCreateAccount);

        tvGuest = findViewById(R.id.tvGuest);
        tvStaff = findViewById(R.id.tvStaff);

        underlineGuest = findViewById(R.id.underlineGuest);
        underlineStaff = findViewById(R.id.underlineStaff);

        etEmail = findViewById(R.id.login_email);
        etPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        rememberMeCheck = findViewById(R.id.rememberMeCheck);
        ivPasswordToggle = findViewById(R.id.ivPasswordToggle);
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.POST_NOTIFICATIONS)) {

                    new AlertDialog.Builder(this)
                            .setTitle("Notification Permission")
                            .setMessage("This app needs notification permission to send you updates about your reservations, such as booking confirmations and changes.")
                            .setPositiveButton("Allow", (dialog, which) -> {
                                requestNotificationPermission();
                            })
                            .setNegativeButton("Not Now", null)
                            .show();
                } else {
                    requestNotificationPermission();
                }
            } else {
                Log.d("LoginActivity", "Notification permission already granted");
            }
        }
    }

    private void requestNotificationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                Log.d("LoginActivity", "Notification permission granted by user");
            } else {
                Toast.makeText(this, "You can enable notifications in app settings", Toast.LENGTH_LONG).show();
                Log.d("LoginActivity", "Notification permission denied by user");
            }
        }
    }

    private void setupTabListeners() {
        tabGuest.setOnClickListener(v -> {
            currentRole = "guest";
            showGuestUI();
        });

        tabStaff.setOnClickListener(v -> {
            currentRole = "staff";
            showStaffUI();
        });
    }

    private void showGuestUI() {
        tvGuest.setTextColor(getResources().getColor(R.color.main_orange));
        tvStaff.setTextColor(getResources().getColor(R.color.light_text));
        underlineGuest.setBackgroundColor(getResources().getColor(R.color.main_orange));
        underlineStaff.setBackgroundColor(getResources().getColor(R.color.light_text));

        layoutCreateAccount.setVisibility(View.VISIBLE);
    }

    private void showStaffUI() {
        tvStaff.setTextColor(getResources().getColor(R.color.main_orange));
        tvGuest.setTextColor(getResources().getColor(R.color.light_text));
        underlineStaff.setBackgroundColor(getResources().getColor(R.color.main_orange));
        underlineGuest.setBackgroundColor(getResources().getColor(R.color.light_text));

        layoutCreateAccount.setVisibility(View.GONE);
    }

    private void autoFillRememberedEmail() {
        String lastEmail = UserSessionManager.getLastEmail(this);

        if (!lastEmail.isEmpty()) {
            etEmail.setText(lastEmail);
            rememberMeCheck.setChecked(true);
        }
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
    }

    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!validateLoginFields(email, password)) {
                return;
            }

            if (rememberMeCheck.isChecked()) {
                UserSessionManager.saveLastEmail(this, email);
            } else {
                UserSessionManager.saveLastEmail(this, "");
            }

            showLoading(true);
            performApiLogin(email, password);
        });
    }

    private void showLoading(boolean show) {
        btnLogin.setText(show ? "Logging in..." : "Login");
        btnLogin.setEnabled(!show);
    }

    private boolean validateLoginFields(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            showErrorMessage(etEmail, "Please enter your email address");
            return false;
        }

        if (!isValidEmail(email)) {
            showErrorMessage(etEmail, "Please enter a valid email address");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            showErrorMessage(etPassword, "Please enter your password");
            return false;
        }

        if (password.length() < 6) {
            showErrorMessage(etPassword, "Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }

    private void showErrorMessage(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void performApiLogin(String email, String password) {
        apiService.login(email, password, new VolleyApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    showLoading(false);

                    try {
                        String role = response.getString("role");
                        String userId = response.getString("user_id");
                        String name = response.getString("name");
                        String userEmail = response.getString("email");

                        if (!role.equalsIgnoreCase(currentRole)) {
                            String message;
                            if (role.equals("staff")) {
                                message = "This is a staff account. Please switch to Staff tab.";
                            } else {
                                message = "This is a guest account. Please switch to Guest tab.";
                            }

                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            return;
                        }

                        UserSessionManager.createLoginSession(
                                LoginActivity.this,
                                userId,
                                name,
                                userEmail,
                                role
                        );

                        redirectToDashboard();

                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this,
                                "Login response error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this,
                            "Login failed: " + error,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void redirectToDashboard() {
        String role = UserSessionManager.getUserRole(this);

        Intent intent;
        if (role.equalsIgnoreCase("staff")) {
            intent = new Intent(this, StaffDashboardActivity.class);
        } else {
            intent = new Intent(this, GuestMenuActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupCreateAccount() {
        tvCreateAccount.setOnClickListener(v -> {
            if (currentRole.equals("guest")) {
                startActivity(new Intent(this, GuestCreateAccountActivity.class));
            } else {
                Toast.makeText(this, "Staff accounts can only be created by administrators", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupForgotPassword() {
        tvForgotPassword.setOnClickListener(v -> {
            showForgotPasswordDialog();
        });
    }

    private void showForgotPasswordDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Forgot Password")
                .setMessage("Please contact restaurant staff at:\n\nPhone: 012-3456789\nEmail: support@restaurant.com")
                .setPositiveButton("OK", null)
                .show();
    }
}
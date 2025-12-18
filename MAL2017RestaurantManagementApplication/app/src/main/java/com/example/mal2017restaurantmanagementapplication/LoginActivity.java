package com.example.mal2017restaurantmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout tabGuest, tabStaff, layoutCreateAccount;
    private TextView tvGuest, tvStaff, tvCreateAccount, btnLogin, tvForgotPassword;
    private View underlineGuest, underlineStaff;
    private EditText etEmail, etPassword;
    private CheckBox rememberMeCheck;
    private ImageView ivPasswordToggle;

    private boolean isPasswordVisible = false;

    private String currentRole = "GUEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

    private void setupTabListeners() {
        tabGuest.setOnClickListener(v -> {
            currentRole = "GUEST";
            showGuestUI();
        });

        tabStaff.setOnClickListener(v -> {
            currentRole = "STAFF";
            showStaffUI();
        });
    }

    private void showGuestUI() {
        tvGuest.setTextColor(getColor(R.color.main_orange));
        tvStaff.setTextColor(getColor(R.color.light_text));
        underlineGuest.setBackgroundColor(getColor(R.color.main_orange));
        underlineStaff.setBackgroundColor(getColor(R.color.light_text));

        layoutCreateAccount.setVisibility(View.VISIBLE);
    }

    private void showStaffUI() {
        tvStaff.setTextColor(getColor(R.color.main_orange));
        tvGuest.setTextColor(getColor(R.color.light_text));
        underlineStaff.setBackgroundColor(getColor(R.color.main_orange));
        underlineGuest.setBackgroundColor(getColor(R.color.light_text));

        layoutCreateAccount.setVisibility(View.GONE);
    }

    private void autoFillRememberedEmail() {
        String lastEmail = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                .getString("LAST_EMAIL", "");

        if (!lastEmail.isEmpty()) {
            etEmail.setText(lastEmail);
            rememberMeCheck.setChecked(true);
        }
    }

    private void setupPasswordToggle() {
        ivPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    etPassword.setTransformationMethod(null);
                    ivPasswordToggle.setImageResource(R.drawable.ic_visibility_on);
                } else {
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ivPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
                }
                etPassword.setSelection(etPassword.getText().length());
            }
        });
    }

    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateLoginFields(email, password)) {
                Toast.makeText(this, "Login successful as " + currentRole, Toast.LENGTH_SHORT).show();

                UserSessionManager.saveUserRole(this, currentRole);
                UserSessionManager.saveUserEmail(this, email);
                UserSessionManager.setLoggedIn(this, true);
                UserSessionManager.setRememberMe(this, rememberMeCheck.isChecked());

                // TODO: 这里添加API登录，获取userId后保存
                // String userId = apiLogin(email, password);
                // UserSessionManager.saveUserId(this, userId);

                if (rememberMeCheck.isChecked()) {
                    getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("LAST_EMAIL", email)
                            .apply();
                } else {
                    getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                            .edit()
                            .remove("LAST_EMAIL")
                            .apply();
                }

                System.out.println("=== 保存到SharedPreferences ===");
                System.out.println("Role: " + currentRole);
                System.out.println("Email: " + email);
                System.out.println("Logged In: true");
                System.out.println("Remember Me: " + rememberMeCheck.isChecked());

                if (currentRole.equals("GUEST")) {
                    startActivity(new Intent(this, GuestMenuActivity.class));
                } else {
                    startActivity(new Intent(this, StaffDashboardActivity.class));
                }

                finish();
            }
        });
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

    private void setupCreateAccount() {
        tvCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, GuestCreateAccountActivity.class));
        });
    }

    private void setupForgotPassword() {
        tvForgotPassword.setOnClickListener(v -> {
            // 简单的忘记密码提示
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
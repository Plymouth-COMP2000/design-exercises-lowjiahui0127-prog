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

public class GuestCreateAccountActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private ImageView ivPasswordToggle, ivConfirmPasswordToggle;
    private TextView btnCreateAccount, tvSignIn;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

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

        ivConfirmPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                if (isConfirmPasswordVisible) {
                    etConfirmPassword.setTransformationMethod(null);
                    ivConfirmPasswordToggle.setImageResource(R.drawable.ic_visibility_on);
                } else {
                    etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ivConfirmPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
                }
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
            }
        });
    }

    private void setupCreateAccountButton() {
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (validateAllFields(fullName, email, phone, password, confirmPassword)) {
                    registerAccount(fullName, email, phone, password);
                }
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
        // TODO: 这里添加实际的注册逻辑，比如调用API

        // 显示成功消息
        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

        // 返回登录页面，并传递新注册的邮箱
        Intent intent = new Intent(GuestCreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupSignInLink() {
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回登录页面
                Intent intent = new Intent(GuestCreateAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
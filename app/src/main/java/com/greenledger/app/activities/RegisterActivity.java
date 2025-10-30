package com.greenledger.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.greenledger.app.R;
import com.greenledger.app.models.User;
import com.greenledger.app.utils.FirebaseHelper;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText nameEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private AutoCompleteTextView userTypeAutoComplete;
    private MaterialButton registerButton;
    private TextView loginLink;
    private ProgressBar progressBar;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseHelper = FirebaseHelper.getInstance();

        initializeViews();
        setupUserTypeDropdown();
        setupListeners();
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        userTypeAutoComplete = findViewById(R.id.userTypeAutoComplete);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupUserTypeDropdown() {
        String[] userTypes = getResources().getStringArray(R.array.user_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, userTypes);
        userTypeAutoComplete.setAdapter(adapter);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> attemptRegistration());
        loginLink.setOnClickListener(v -> finish());
    }

    private void attemptRegistration() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String userType = userTypeAutoComplete.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!validateInput(name, phone, userType, password, confirmPassword)) {
            return;
        }

        showLoading(true);

        // Create email from phone number for Firebase Auth
        String email = phone + "@greenledger.app";

        firebaseHelper.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseHelper.getAuth().getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserToDatabase(firebaseUser.getUid(), name, phone, userType);
                        }
                    } else {
                        showLoading(false);
                        Toast.makeText(RegisterActivity.this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToDatabase(String userId, String name, String phone, String userType) {
        User user = new User(userId, name, phone, userType);

        firebaseHelper.getUsersRef().child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Registration successful!", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String name, String phone, String userType,
                                  String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(getString(R.string.error_empty_field));
            nameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError(getString(R.string.error_empty_field));
            phoneEditText.requestFocus();
            return false;
        }

        if (phone.length() < 10) {
            phoneEditText.setError(getString(R.string.error_invalid_phone));
            phoneEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(userType)) {
            userTypeAutoComplete.setError(getString(R.string.error_empty_field));
            userTypeAutoComplete.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_empty_field));
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.error_password_mismatch));
            confirmPasswordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        registerButton.setEnabled(!show);
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

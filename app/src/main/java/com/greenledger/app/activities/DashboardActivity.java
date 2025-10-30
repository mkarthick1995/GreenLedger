package com.greenledger.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.greenledger.app.R;
import com.greenledger.app.models.User;
import com.greenledger.app.utils.FirebaseHelper;

public class DashboardActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextView userNameText;
    private MaterialCardView expenseCard, rawMaterialCard, labourCard;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firebaseHelper = FirebaseHelper.getInstance();

        if (!firebaseHelper.isUserLoggedIn()) {
            navigateToLogin();
            return;
        }

        initializeViews();
        setupToolbar();
        loadUserData();
        setupCardListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        userNameText = findViewById(R.id.userNameText);
        expenseCard = findViewById(R.id.expenseCard);
        rawMaterialCard = findViewById(R.id.rawMaterialCard);
        labourCard = findViewById(R.id.labourCard);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                logout();
                return true;
            }
            return false;
        });
    }

    private void loadUserData() {
        String userId = firebaseHelper.getCurrentUserId();
        if (userId != null) {
            firebaseHelper.getUsersRef().child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                userNameText.setText(user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(DashboardActivity.this,
                                    "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setupCardListeners() {
        expenseCard.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, ExpenseActivity.class));
        });

        rawMaterialCard.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, RawMaterialActivity.class));
        });

        labourCard.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, LabourActivity.class));
        });
    }

    private void logout() {
        firebaseHelper.logout();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

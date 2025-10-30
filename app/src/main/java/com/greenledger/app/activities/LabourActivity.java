package com.greenledger.app.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.greenledger.app.R;
import com.greenledger.app.adapters.LabourAdapter;
import com.greenledger.app.models.Labour;
import com.greenledger.app.utils.FirebaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LabourActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton addLabourFab;
    private LabourAdapter adapter;
    private FirebaseHelper firebaseHelper;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labour);

        firebaseHelper = FirebaseHelper.getInstance();

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadLabourEntries();

        addLabourFab.setOnClickListener(v -> showAddLabourDialog());
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.labourRecyclerView);
        addLabourFab = findViewById(R.id.addLabourFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new LabourAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadLabourEntries() {
        String userId = firebaseHelper.getCurrentUserId();
        if (userId == null) return;

        firebaseHelper.getLabourRef().orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Labour> labourList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Labour labour = dataSnapshot.getValue(Labour.class);
                            if (labour != null) {
                                labourList.add(labour);
                            }
                        }
                        adapter.setLabourList(labourList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LabourActivity.this,
                                "Failed to load labour entries", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddLabourDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_labour, null);

        TextInputEditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        TextInputEditText phoneEditText = dialogView.findViewById(R.id.phoneEditText);
        TextInputEditText hoursEditText = dialogView.findViewById(R.id.hoursEditText);
        TextInputEditText rateEditText = dialogView.findViewById(R.id.rateEditText);
        TextInputEditText dateEditText = dialogView.findViewById(R.id.dateEditText);
        TextInputEditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);

        // Set current date
        dateEditText.setText(dateFormatter.format(calendar.getTime()));

        // Date picker
        dateEditText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    LabourActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        dateEditText.setText(dateFormatter.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        MaterialButton saveButton = dialogView.findViewById(R.id.saveButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String hoursStr = hoursEditText.getText().toString().trim();
            String rateStr = rateEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (validateLabourInput(name, phone, hoursStr, rateStr, description)) {
                double hours = Double.parseDouble(hoursStr);
                double rate = Double.parseDouble(rateStr);
                saveLabourEntry(name, phone, hours, rate, date, description);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean validateLabourInput(String name, String phone, String hours,
                                       String rate, String description) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter labour name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(hours)) {
            Toast.makeText(this, "Please enter hours worked", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(rate)) {
            Toast.makeText(this, "Please enter hourly rate", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please enter work description", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveLabourEntry(String name, String phone, double hours, double rate,
                                 String date, String description) {
        String userId = firebaseHelper.getCurrentUserId();
        if (userId == null) return;

        String labourId = firebaseHelper.getLabourRef().push().getKey();
        if (labourId == null) return;

        Labour labour = new Labour(labourId, userId, name, phone, hours, rate, date, description);

        firebaseHelper.getLabourRef().child(labourId).setValue(labour)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LabourActivity.this,
                                "Labour entry added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LabourActivity.this,
                                "Failed to add labour entry", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

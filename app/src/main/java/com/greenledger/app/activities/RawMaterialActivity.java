package com.greenledger.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.greenledger.app.adapters.RawMaterialAdapter;
import com.greenledger.app.models.RawMaterial;
import com.greenledger.app.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class RawMaterialActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton addMaterialFab;
    private RawMaterialAdapter adapter;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_material);

        firebaseHelper = FirebaseHelper.getInstance();

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadMaterials();

        addMaterialFab.setOnClickListener(v -> showAddMaterialDialog());
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.materialRecyclerView);
        addMaterialFab = findViewById(R.id.addMaterialFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new RawMaterialAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadMaterials() {
        String userId = firebaseHelper.getCurrentUserId();
        if (userId == null) return;

        firebaseHelper.getRawMaterialsRef().orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<RawMaterial> materials = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            RawMaterial material = dataSnapshot.getValue(RawMaterial.class);
                            if (material != null) {
                                materials.add(material);
                            }
                        }
                        adapter.setMaterials(materials);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RawMaterialActivity.this,
                                "Failed to load materials", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddMaterialDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_material, null);

        TextInputEditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        TextInputEditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        AutoCompleteTextView unitAutoComplete = dialogView.findViewById(R.id.unitAutoComplete);
        TextInputEditText costEditText = dialogView.findViewById(R.id.costEditText);

        // Setup unit dropdown
        String[] units = getResources().getStringArray(R.array.material_units);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, units);
        unitAutoComplete.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        MaterialButton saveButton = dialogView.findViewById(R.id.saveButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String quantityStr = quantityEditText.getText().toString().trim();
            String unit = unitAutoComplete.getText().toString().trim();
            String costStr = costEditText.getText().toString().trim();

            if (validateMaterialInput(name, quantityStr, unit, costStr)) {
                double quantity = Double.parseDouble(quantityStr);
                double cost = Double.parseDouble(costStr);
                saveMaterial(name, quantity, unit, cost);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean validateMaterialInput(String name, String quantity, String unit, String cost) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter material name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(quantity)) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(unit)) {
            Toast.makeText(this, "Please select unit", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(cost)) {
            Toast.makeText(this, "Please enter cost per unit", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveMaterial(String name, double quantity, String unit, double cost) {
        String userId = firebaseHelper.getCurrentUserId();
        if (userId == null) return;

        String materialId = firebaseHelper.getRawMaterialsRef().push().getKey();
        if (materialId == null) return;

        RawMaterial material = new RawMaterial(materialId, userId, name, quantity, unit, cost);

        firebaseHelper.getRawMaterialsRef().child(materialId).setValue(material)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RawMaterialActivity.this,
                                "Material added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RawMaterialActivity.this,
                                "Failed to add material", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

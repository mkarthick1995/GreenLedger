package com.greenledger.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.greenledger.app.R;
import com.greenledger.app.data.local.entities.BusinessPartnerEntity;
import com.greenledger.app.data.repository.BusinessPartnerRepository;

public class AddPartnerActivity extends AppCompatActivity {
    private TextInputEditText nameInput;
    private TextInputEditText phoneInput;
    private TextInputEditText emailInput;
    private TextInputEditText addressInput;
    private AutoCompleteTextView typeInput;
    private BusinessPartnerRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partner);

        repository = new BusinessPartnerRepository(this);

        nameInput = findViewById(R.id.et_name);
        phoneInput = findViewById(R.id.et_phone);
        emailInput = findViewById(R.id.et_email);
        addressInput = findViewById(R.id.et_address);
        typeInput = findViewById(R.id.act_type);
        Button saveButton = findViewById(R.id.btn_save);

        // Setup business type dropdown
        String[] types = new String[]{"Buyer", "Supplier", "Transporter", "Storage Provider"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, types);
        typeInput.setAdapter(adapter);

        saveButton.setOnClickListener(v -> validateAndSavePartner());
    }

    private void validateAndSavePartner() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Business name is required");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneInput.setError("Phone number is required");
            return;
        }

        if (TextUtils.isEmpty(type)) {
            typeInput.setError("Business type is required");
            return;
        }

        BusinessPartnerEntity partner = new BusinessPartnerEntity();
        partner.name = name;
        partner.phone = phone;
        partner.email = email;
        partner.address = address;
        partner.businessType = type;
        partner.createdAt = System.currentTimeMillis();

        repository.addPartner(partner, new BusinessPartnerRepository.DatabaseCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(AddPartnerActivity.this,
                        "Partner added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AddPartnerActivity.this,
                        "Error adding partner: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}

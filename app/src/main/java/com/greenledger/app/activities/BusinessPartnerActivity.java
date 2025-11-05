package com.greenledger.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.greenledger.app.R;
import com.greenledger.app.adapters.BusinessPartnerAdapter;
import com.greenledger.app.data.local.entities.BusinessPartnerEntity;
import com.greenledger.app.data.repository.BusinessPartnerRepository;
import java.util.List;

public class BusinessPartnerActivity extends AppCompatActivity implements BusinessPartnerAdapter.OnPartnerClickListener {
    private RecyclerView recyclerView;
    private BusinessPartnerAdapter adapter;
    private BusinessPartnerRepository repository;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_partners);

        repository = new BusinessPartnerRepository(this);
        adapter = new BusinessPartnerAdapter(this);

        setupViews();
        startSync();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recycler_partners);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_partner);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddPartnerActivity.class)));
    }

    private void startSync() {
        // First load from local database
        repository.getLocalPartners(partners -> {
            updatePartnersList(partners);

            // Then sync with Firebase
            repository.syncPartners(new BusinessPartnerRepository.SyncCallback() {
                @Override
                public void onSyncComplete(List<BusinessPartnerEntity> partners) {
                    runOnUiThread(() -> updatePartnersList(partners));
                }

                @Override
                public void onSyncFailed(String error) {
                    runOnUiThread(() -> Toast.makeText(BusinessPartnerActivity.this,
                        "Sync failed: " + error, Toast.LENGTH_LONG).show());
                }
            });
        });
    }

    private void updatePartnersList(List<BusinessPartnerEntity> partners) {
        adapter.setPartners(partners);
        if (partners.isEmpty()) {
            // Show empty state if needed
            Toast.makeText(this, "No business partners found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPartnerClick(BusinessPartnerEntity partner) {
        // Open partner details/transactions activity
        Intent intent = new Intent(this, PartnerTransactionsActivity.class);
        intent.putExtra("partnerId", partner.partnerId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh local data when returning to screen
        repository.getLocalPartners(this::updatePartnersList);
    }
}

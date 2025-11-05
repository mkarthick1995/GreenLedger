package com.greenledger.app.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.greenledger.app.R;
import com.greenledger.app.data.local.entities.BusinessPartnerEntity;
import com.greenledger.app.data.local.entities.OrderEntity;
import com.greenledger.app.data.repository.BusinessPartnerRepository;
import com.greenledger.app.fragments.PartnerOrdersFragment;
import com.greenledger.app.fragments.PartnerTransactionsFragment;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class PartnerTransactionsActivity extends AppCompatActivity {
    private String partnerId;
    private BusinessPartnerRepository repository;
    private TextView partnerNameText;
    private TextView partnerDetailsText;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_transactions);

        partnerId = getIntent().getStringExtra("partnerId");
        if (partnerId == null) {
            finish();
            return;
        }

        repository = new BusinessPartnerRepository(this);
        setupViews();
        loadPartnerDetails();
    }

    private void setupViews() {
        partnerNameText = findViewById(R.id.txt_partner_name);
        partnerDetailsText = findViewById(R.id.txt_partner_details);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        fab = findViewById(R.id.fab_add);

        viewPager.setAdapter(new PartnerPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Orders" : "Transactions");
        }).attach();

        // FAB behavior changes based on selected tab
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                fab.setContentDescription(position == 0 ? "Add new order" : "Add new transaction");
            }
        });

        fab.setOnClickListener(v -> {
            int currentTab = viewPager.getCurrentItem();
            if (currentTab == 0) {
                // Launch add order dialog/activity
                showAddOrderDialog();
            } else {
                // Launch add transaction dialog/activity
                showAddTransactionDialog();
            }
        });
    }

    private void loadPartnerDetails() {
        repository.getLocalPartners(partners -> {
            for (BusinessPartnerEntity partner : partners) {
                if (partner.partnerId.equals(partnerId)) {
                    runOnUiThread(() -> {
                        partnerNameText.setText(partner.name);
                        String details = String.format("%s\n%s\n%s",
                            partner.businessType,
                            partner.phone,
                            partner.email);
                        partnerDetailsText.setText(details);
                    });
                    break;
                }
            }
        });
    }

    private void showAddOrderDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_order, null);
        AutoCompleteTextView cropInput = dialogView.findViewById(R.id.act_crop);
        TextInputEditText quantityInput = dialogView.findViewById(R.id.et_quantity);
        TextInputEditText priceInput = dialogView.findViewById(R.id.et_price);
        TextInputEditText notesInput = dialogView.findViewById(R.id.et_notes);

        // Load available crops for the dropdown
        List<String> crops = new ArrayList<>(); // TODO: Load from database
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, crops);
        cropInput.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("New Order")
            .setView(dialogView)
            .setPositiveButton("Create", null) // Set in onShow to prevent auto-dismiss
            .setNegativeButton("Cancel", null)
            .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String crop = cropInput.getText().toString().trim();
                String quantityStr = quantityInput.getText().toString().trim();
                String priceStr = priceInput.getText().toString().trim();
                String notes = notesInput.getText().toString().trim();

                if (crop.isEmpty()) {
                    cropInput.setError("Please select a crop");
                    return;
                }

                if (quantityStr.isEmpty()) {
                    quantityInput.setError("Please enter quantity");
                    return;
                }

                if (priceStr.isEmpty()) {
                    priceInput.setError("Please enter price per unit");
                    return;
                }

                double quantity = Double.parseDouble(quantityStr);
                double price = Double.parseDouble(priceStr);

                OrderEntity order = new OrderEntity(UUID.randomUUID().toString());
                order.partnerId = partnerId;
                order.cropId = crop; // TODO: Store actual crop ID
                order.quantity = quantity;
                order.pricePerUnit = price;
                order.status = "PENDING";
                order.notes = notes;
                order.createdAt = System.currentTimeMillis();
                order.updatedAt = System.currentTimeMillis();

                Executors.newSingleThreadExecutor().execute(() -> {
                    database.orderDao().insert(order);
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        // Refresh the orders list
                        viewPager.setCurrentItem(0); // Switch to Orders tab
                        PartnerOrdersFragment fragment =
                            (PartnerOrdersFragment) getSupportFragmentManager()
                                .findFragmentByTag("f0");
                        if (fragment != null) {
                            fragment.loadOrders();
                        }
                    });
                });
            });
        });

        dialog.show();
    }

    private void showAddTransactionDialog() {
        // TODO: Implement dialog to add new transaction
    }

    private class PartnerPagerAdapter extends FragmentStateAdapter {
        public PartnerPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return PartnerOrdersFragment.newInstance(partnerId);
            } else {
                return PartnerTransactionsFragment.newInstance(partnerId);
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}

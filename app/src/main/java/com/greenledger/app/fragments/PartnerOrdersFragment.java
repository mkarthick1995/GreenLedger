package com.greenledger.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.greenledger.app.R;
import com.greenledger.app.data.local.GreenLedgerDatabase;
import com.greenledger.app.data.repository.OrderRepository;
import java.util.List;
import java.util.concurrent.Executors;

public class PartnerOrdersFragment extends Fragment {
    private static final String TAG = "PartnerOrdersFragment";
    private String partnerId;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private GreenLedgerDatabase database;
    private OrderAdapter adapter;
    private OrderRepository orderRepository;

    public static PartnerOrdersFragment newInstance(String partnerId) {
        PartnerOrdersFragment fragment = new PartnerOrdersFragment();
        Bundle args = new Bundle();
        args.putString("partnerId", partnerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            partnerId = getArguments().getString("partnerId");
        }
        database = GreenLedgerDatabase.getInstance(requireContext());
        orderRepository = new OrderRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partner_orders, container, false);

        recyclerView = view.findViewById(R.id.recycler_orders);
        emptyView = view.findViewById(R.id.txt_empty);

        adapter = new OrderAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadOrders();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startSync();
    }

    private void startSync() {
        // First load local data
        loadOrders();

        // Then sync with Firebase
        orderRepository.syncPartnerOrders(partnerId, new OrderRepository.SyncCallback() {
            @Override
            public void onSyncComplete(List<OrderEntity> orders) {
                requireActivity().runOnUiThread(() -> {
                    updateOrdersList(orders);
                });
            }

            @Override
            public void onSyncFailed(String error) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(),
                        "Failed to sync orders: " + error,
                        Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void loadOrders() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<OrderEntity> orders = database.orderDao().getOrdersForPartner(partnerId);
            requireActivity().runOnUiThread(() -> updateOrdersList(orders));
        });
    }

    private void updateOrdersList(List<OrderEntity> orders) {
        if (orders.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.setOrders(orders);
        }
    }
}

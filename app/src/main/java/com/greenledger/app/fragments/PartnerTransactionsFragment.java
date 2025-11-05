package com.greenledger.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.greenledger.app.R;

public class PartnerTransactionsFragment extends Fragment {
    private String partnerId;
    private RecyclerView recyclerView;
    private TextView emptyView;

    public static PartnerTransactionsFragment newInstance(String partnerId) {
        PartnerTransactionsFragment fragment = new PartnerTransactionsFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partner_transactions, container, false);

        recyclerView = view.findViewById(R.id.recycler_transactions);
        emptyView = view.findViewById(R.id.txt_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTransactions();

        return view;
    }

    private void loadTransactions() {
        // Will be implemented in next phase
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }
}

package com.greenledger.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.greenledger.app.R;
import com.greenledger.app.data.local.entities.BusinessPartnerEntity;
import java.util.ArrayList;
import java.util.List;

public class BusinessPartnerAdapter extends RecyclerView.Adapter<BusinessPartnerAdapter.ViewHolder> {
    private List<BusinessPartnerEntity> partners = new ArrayList<>();
    private OnPartnerClickListener listener;

    public interface OnPartnerClickListener {
        void onPartnerClick(BusinessPartnerEntity partner);
    }

    public BusinessPartnerAdapter(OnPartnerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_business_partner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusinessPartnerEntity partner = partners.get(position);
        holder.bind(partner, listener);
    }

    @Override
    public int getItemCount() {
        return partners.size();
    }

    public void setPartners(List<BusinessPartnerEntity> partners) {
        this.partners = partners;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView typeText;
        private final TextView contactText;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.txt_partner_name);
            typeText = itemView.findViewById(R.id.txt_partner_type);
            contactText = itemView.findViewById(R.id.txt_partner_contact);
        }

        void bind(BusinessPartnerEntity partner, OnPartnerClickListener listener) {
            nameText.setText(partner.name);
            typeText.setText(partner.businessType);
            String contact = String.format("%s\n%s", partner.phone, partner.email);
            contactText.setText(contact);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPartnerClick(partner);
                }
            });
        }
    }
}

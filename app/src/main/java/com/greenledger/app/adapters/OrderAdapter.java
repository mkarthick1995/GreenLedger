package com.greenledger.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.greenledger.app.R;
import com.greenledger.app.data.local.entities.OrderEntity;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<OrderEntity> orders = new ArrayList<>();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderEntity order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cropText;
        private final TextView detailsText;
        private final TextView statusText;
        private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        ViewHolder(View itemView) {
            super(itemView);
            cropText = itemView.findViewById(R.id.txt_crop);
            detailsText = itemView.findViewById(R.id.txt_details);
            statusText = itemView.findViewById(R.id.txt_status);
        }

        void bind(OrderEntity order) {
            cropText.setText(order.cropId);
            String details = String.format("%s%.2f units @ %s per unit\nTotal: %s",
                    order.notes != null && !order.notes.isEmpty() ? order.notes + "\n" : "",
                    order.quantity,
                    currencyFormat.format(order.pricePerUnit),
                    currencyFormat.format(order.quantity * order.pricePerUnit));
            detailsText.setText(details);
            statusText.setText(order.status);
        }
    }
}

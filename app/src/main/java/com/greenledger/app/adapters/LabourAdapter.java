package com.greenledger.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenledger.app.R;
import com.greenledger.app.models.Labour;

import java.util.ArrayList;
import java.util.List;

public class LabourAdapter extends RecyclerView.Adapter<LabourAdapter.LabourViewHolder> {
    private List<Labour> labourList = new ArrayList<>();

    @NonNull
    @Override
    public LabourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_labour, parent, false);
        return new LabourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabourViewHolder holder, int position) {
        Labour labour = labourList.get(position);
        holder.bind(labour);
    }

    @Override
    public int getItemCount() {
        return labourList.size();
    }

    public void setLabourList(List<Labour> labourList) {
        this.labourList = labourList;
        notifyDataSetChanged();
    }

    static class LabourViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView phoneText;
        private final TextView workDetailsText;
        private final TextView descriptionText;
        private final TextView dateText;
        private final TextView totalPayText;

        public LabourViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            phoneText = itemView.findViewById(R.id.phoneText);
            workDetailsText = itemView.findViewById(R.id.workDetailsText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dateText = itemView.findViewById(R.id.dateText);
            totalPayText = itemView.findViewById(R.id.totalPayText);
        }

        public void bind(Labour labour) {
            nameText.setText(labour.getName());
            phoneText.setText("Phone: " + labour.getPhone());
            workDetailsText.setText(String.format("Hours: %.2f | Rate: ₹%.2f/hr",
                    labour.getHoursWorked(), labour.getHourlyRate()));
            descriptionText.setText(labour.getWorkDescription());
            dateText.setText(labour.getWorkDate());
            totalPayText.setText(String.format("₹%.2f", labour.getTotalPay()));
        }
    }
}

package com.greenledger.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenledger.app.R;
import com.greenledger.app.models.RawMaterial;

import java.util.ArrayList;
import java.util.List;

public class RawMaterialAdapter extends RecyclerView.Adapter<RawMaterialAdapter.MaterialViewHolder> {
    private List<RawMaterial> materials = new ArrayList<>();

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        RawMaterial material = materials.get(position);
        holder.bind(material);
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    public void setMaterials(List<RawMaterial> materials) {
        this.materials = materials;
        notifyDataSetChanged();
    }

    static class MaterialViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView quantityText;
        private final TextView costText;
        private final TextView totalText;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            quantityText = itemView.findViewById(R.id.quantityText);
            costText = itemView.findViewById(R.id.costText);
            totalText = itemView.findViewById(R.id.totalText);
        }

        public void bind(RawMaterial material) {
            nameText.setText(material.getName());
            quantityText.setText(String.format("Quantity: %.2f %s",
                    material.getQuantity(), material.getUnit()));
            costText.setText(String.format("₹%.2f/%s",
                    material.getCostPerUnit(), material.getUnit()));
            totalText.setText(String.format("Total: ₹%.2f", material.getTotalCost()));
        }
    }
}

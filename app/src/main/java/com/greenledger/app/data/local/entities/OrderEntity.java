package com.greenledger.app.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(
    tableName = "orders",
    foreignKeys = @ForeignKey(
        entity = BusinessPartnerEntity.class,
        parentColumns = "partnerId",
        childColumns = "partnerId",
        onDelete = ForeignKey.CASCADE
    )
)
public class OrderEntity {
    @PrimaryKey
    @NonNull
    public String orderId;

    public String partnerId;
    public String cropId;
    public double quantity;
    public String unit;
    public double pricePerUnit;
    public String status; // PENDING, CONFIRMED, DELIVERED, CANCELLED
    public long createdAt;
    public long updatedAt;
    public String notes;

    public OrderEntity(@NonNull String orderId) {
        this.orderId = orderId;
    }
}

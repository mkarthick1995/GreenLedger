package com.greenledger.app.models;

public class RawMaterial {
    private String materialId;
    private String userId;
    private String name;
    private double quantity;
    private String unit;
    private double costPerUnit;
    private long timestamp;

    public RawMaterial() {
        // Required empty constructor for Firebase
    }

    public RawMaterial(String materialId, String userId, String name, double quantity, String unit, double costPerUnit) {
        this.materialId = materialId;
        this.userId = userId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.costPerUnit = costPerUnit;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(double costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getTotalCost() {
        return quantity * costPerUnit;
    }
}

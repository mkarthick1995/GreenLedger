package com.greenledger.app.models;

public class Labour {
    private String labourId;
    private String userId;
    private String name;
    private String phone;
    private double hoursWorked;
    private double hourlyRate;
    private String workDate;
    private String workDescription;
    private long timestamp;

    public Labour() {
        // Required empty constructor for Firebase
    }

    public Labour(String labourId, String userId, String name, String phone, double hoursWorked,
                 double hourlyRate, String workDate, String workDescription) {
        this.labourId = labourId;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
        this.workDate = workDate;
        this.workDescription = workDescription;
        this.timestamp = System.currentTimeMillis();
    }

    public String getLabourId() {
        return labourId;
    }

    public void setLabourId(String labourId) {
        this.labourId = labourId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getTotalPay() {
        return hoursWorked * hourlyRate;
    }
}

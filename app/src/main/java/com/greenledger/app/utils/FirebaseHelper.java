package com.greenledger.app.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greenledger.app.models.Farm;
import com.greenledger.app.models.Crop;
import com.greenledger.app.models.CropStage;
import com.greenledger.app.models.Storage;
import com.greenledger.app.models.User;
import com.greenledger.app.models.UserV2;

public class FirebaseHelper {
    private static FirebaseHelper instance;
    private final FirebaseAuth auth;
    private final DatabaseReference database;

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getUsersRef() {
        return database.child("users");
    }

    public DatabaseReference getExpensesRef() {
        return database.child("expenses");
    }

    public DatabaseReference getRawMaterialsRef() {
        return database.child("rawMaterials");
    }

    public DatabaseReference getLabourRef() {
        return database.child("labour");
    }

    // V2.0 collection references
    public DatabaseReference getUsersV2Ref() {
        return database.child("usersV2");
    }

    public DatabaseReference getCropsRef() {
        return database.child("crops");
    }

    public DatabaseReference getFarmOperationsRef() {
        return database.child("farmOperations");
    }

    public DatabaseReference getStorageRef() {
        return database.child("storage");
    }

    public DatabaseReference getLabourRecordsRef() {
        return database.child("labourRecords");
    }

    public DatabaseReference getFinancialRecordsRef() {
        return database.child("financialRecords");
    }

    public DatabaseReference getTransactionsRef() {
        return database.child("transactions");
    }

    public DatabaseReference getReportsRef() {
        return database.child("reports");
    }

    public DatabaseReference getNotificationsRef() {
        return database.child("notifications");
    }

    public DatabaseReference getAnalyticsRef() {
        return database.child("analytics");
    }

    // Query methods for user-specific data
    public DatabaseReference getUserExpensesRef(String userId) {
        return database.child("expenses").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserMaterialsRef(String userId) {
        return database.child("rawMaterials").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserLabourRef(String userId) {
        return database.child("labour").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserCropsRef(String userId) {
        return database.child("crops").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserFarmOperationsRef(String userId) {
        return database.child("farmOperations").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserStorageRef(String userId) {
        return database.child("storage").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserLabourRecordsRef(String userId) {
        return database.child("labourRecords").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserFinancialRecordsRef(String userId) {
        return database.child("financialRecords").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserTransactionsRef(String userId) {
        return database.child("transactions").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserNotificationsRef(String userId) {
        return database.child("notifications").orderByChild("userId").equalTo(userId).getRef();
    }

    // Farm Management References
    public DatabaseReference getFarmsRef() {
        return database.child("farms");
    }

    public DatabaseReference getFarmsByUserRef(String userId) {
        return database.child("farms").orderByChild("farmerId").equalTo(userId).getRef();
    }

    public DatabaseReference getFarmCropsRef(String farmId) {
        return database.child("crops").orderByChild("farmId").equalTo(farmId).getRef();
    }

    public DatabaseReference getFarmStorageRef(String farmId) {
        return database.child("storage").orderByChild("farmId").equalTo(farmId).getRef();
    }

    public DatabaseReference getFarmExpensesRef(String farmId) {
        return database.child("expenses").orderByChild("farmId").equalTo(farmId).getRef();
    }

    public DatabaseReference getFarmLabourRef(String farmId) {
        return database.child("labour").orderByChild("farmId").equalTo(farmId).getRef();
    }

    // Storage Management References
    public DatabaseReference getStorageByUserRef(String userId) {
        return database.child("storage").orderByChild("farmerId").equalTo(userId).getRef();
    }

    public DatabaseReference getStorageInventoryRef(String storageId) {
        return database.child("storage").child(storageId).child("inventory");
    }

    // Crops Management References
    public DatabaseReference getCropsByUserRef(String userId) {
        return database.child("crops").orderByChild("farmerId").equalTo(userId).getRef();
    }

    public DatabaseReference getCropStagesRef(String cropId) {
        return database.child("crops").child(cropId).child("lifecycle").child("stages");
    }

    // Orders Management References
    public DatabaseReference getOrdersRef() {
        return database.child("orders");
    }

    public DatabaseReference getPartnerOrdersRef(String partnerId) {
        return database.child("orders").orderByChild("partnerId").equalTo(partnerId).getRef();
    }

    // Helper methods
    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void logout() {
        auth.signOut();
    }

    public void addFarm(Farm farm, DatabaseReference.CompletionListener listener) {
        String farmId = getFarmsRef().push().getKey();
        if (farmId != null) {
            farm.setFarmId(farmId);
            farm.setFarmerId(getCurrentUserId());
            farm.getMetadata().setCreatedAt(System.currentTimeMillis());
            farm.getMetadata().setUpdatedAt(System.currentTimeMillis());
            getFarmsRef().child(farmId).setValue(farm, listener);
        }
    }

    public void addCrop(Crop crop, DatabaseReference.CompletionListener listener) {
        String cropId = getCropsRef().push().getKey();
        if (cropId != null) {
            crop.setCropId(cropId);
            crop.setFarmerId(getCurrentUserId());
            crop.getMetadata().setCreatedAt(System.currentTimeMillis());
            crop.getMetadata().setUpdatedAt(System.currentTimeMillis());
            crop.getMetadata().setCreatedBy(getCurrentUserId());
            getCropsRef().child(cropId).setValue(crop, listener);
        }
    }

    public void addStorage(Storage storage, DatabaseReference.CompletionListener listener) {
        String storageId = getStorageRef().push().getKey();
        if (storageId != null) {
            storage.setStorageId(storageId);
            storage.setFarmerId(getCurrentUserId());
            storage.getMetadata().setCreatedAt(System.currentTimeMillis());
            storage.getMetadata().setUpdatedAt(System.currentTimeMillis());
            getStorageRef().child(storageId).setValue(storage, listener);
        }
    }

    public void updateCropStage(String cropId, CropStage stage, DatabaseReference.CompletionListener listener) {
        String stageId = getCropStagesRef(cropId).push().getKey();
        if (stageId != null) {
            getCropStagesRef(cropId).child(stageId).setValue(stage, listener);
        }
    }
}

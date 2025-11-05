package com.greenledger.app.data.repository;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.greenledger.app.data.local.GreenLedgerDatabase;
import com.greenledger.app.data.local.entities.BusinessPartnerEntity;
import com.greenledger.app.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BusinessPartnerRepository {
    private static final String TAG = "BusinessPartnerRepo";
    private final GreenLedgerDatabase database;
    private final FirebaseHelper firebaseHelper;
    private final Executor executor;

    public BusinessPartnerRepository(Context context) {
        database = GreenLedgerDatabase.getInstance(context);
        firebaseHelper = FirebaseHelper.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }

    public void syncPartners(SyncCallback callback) {
        DatabaseReference partnersRef = firebaseHelper.getBusinessPartnersRef();
        partnersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                executor.execute(() -> {
                    List<BusinessPartnerEntity> partners = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        BusinessPartnerEntity partner = child.getValue(BusinessPartnerEntity.class);
                        if (partner != null) {
                            partner.partnerId = child.getKey();
                            partners.add(partner);
                        }
                    }
                    database.businessPartnerDao().insertAll(partners);
                    if (callback != null) {
                        callback.onSyncComplete(partners);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to sync partners: " + error.getMessage());
                if (callback != null) {
                    callback.onSyncFailed(error.getMessage());
                }
            }
        });
    }

    public void getLocalPartners(DataCallback<List<BusinessPartnerEntity>> callback) {
        executor.execute(() -> {
            List<BusinessPartnerEntity> partners = database.businessPartnerDao().getAll();
            if (callback != null) {
                callback.onData(partners);
            }
        });
    }

    public void addPartner(BusinessPartnerEntity partner, DatabaseCallback callback) {
        DatabaseReference newPartnerRef = firebaseHelper.getBusinessPartnersRef().push();
        String partnerId = newPartnerRef.getKey();
        partner.partnerId = partnerId;

        newPartnerRef.setValue(partner)
            .addOnSuccessListener(aVoid -> {
                executor.execute(() -> {
                    database.businessPartnerDao().insert(partner);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                });
            })
            .addOnFailureListener(e -> {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            });
    }

    public void updatePartner(BusinessPartnerEntity partner, DatabaseCallback callback) {
        firebaseHelper.getBusinessPartnersRef().child(partner.partnerId)
            .setValue(partner)
            .addOnSuccessListener(aVoid -> {
                executor.execute(() -> {
                    database.businessPartnerDao().update(partner);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                });
            })
            .addOnFailureListener(e -> {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            });
    }

    public void deletePartner(BusinessPartnerEntity partner, DatabaseCallback callback) {
        firebaseHelper.getBusinessPartnersRef().child(partner.partnerId)
            .removeValue()
            .addOnSuccessListener(aVoid -> {
                executor.execute(() -> {
                    database.businessPartnerDao().delete(partner);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                });
            })
            .addOnFailureListener(e -> {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            });
    }

    public interface SyncCallback {
        void onSyncComplete(List<BusinessPartnerEntity> partners);
        void onSyncFailed(String error);
    }

    public interface DataCallback<T> {
        void onData(T data);
    }

    public interface DatabaseCallback {
        void onSuccess();
        void onError(String error);
    }
}

package com.greenledger.app.data.repository;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.greenledger.app.data.local.GreenLedgerDatabase;
import com.greenledger.app.data.local.entities.OrderEntity;
import com.greenledger.app.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OrderRepository {
    private static final String TAG = "OrderRepository";
    private final GreenLedgerDatabase database;
    private final FirebaseHelper firebaseHelper;
    private final Executor executor;

    public OrderRepository(Context context) {
        database = GreenLedgerDatabase.getInstance(context);
        firebaseHelper = FirebaseHelper.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }

    public void syncPartnerOrders(String partnerId, SyncCallback callback) {
        DatabaseReference ordersRef = firebaseHelper.getPartnerOrdersRef(partnerId);
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                executor.execute(() -> {
                    List<OrderEntity> orders = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        OrderEntity order = child.getValue(OrderEntity.class);
                        if (order != null) {
                            order.orderId = child.getKey();
                            orders.add(order);
                        }
                    }
                    database.orderDao().insertAll(orders);
                    if (callback != null) {
                        callback.onSyncComplete(orders);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to sync orders: " + error.getMessage());
                if (callback != null) {
                    callback.onSyncFailed(error.getMessage());
                }
            }
        });
    }

    public void addOrder(OrderEntity order, DatabaseCallback callback) {
        DatabaseReference newOrderRef = firebaseHelper.getOrdersRef().push();
        String orderId = newOrderRef.getKey();
        order.orderId = orderId;

        newOrderRef.setValue(order)
            .addOnSuccessListener(aVoid -> {
                executor.execute(() -> {
                    database.orderDao().insert(order);
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

    public void updateOrder(OrderEntity order, DatabaseCallback callback) {
        firebaseHelper.getOrdersRef().child(order.orderId)
            .setValue(order)
            .addOnSuccessListener(aVoid -> {
                executor.execute(() -> {
                    database.orderDao().update(order);
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

    public void deleteOrder(OrderEntity order, DatabaseCallback callback) {
        firebaseHelper.getOrdersRef().child(order.orderId)
            .removeValue()
            .addOnSuccessListener(aVoid -> {
                executor.execute(() -> {
                    database.orderDao().delete(order);
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
        void onSyncComplete(List<OrderEntity> orders);
        void onSyncFailed(String error);
    }

    public interface DatabaseCallback {
        void onSuccess();
        void onError(String error);
    }
}

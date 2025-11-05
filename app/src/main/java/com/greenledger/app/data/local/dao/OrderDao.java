package com.greenledger.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.greenledger.app.data.local.entities.OrderEntity;
import java.util.List;

@Dao
public interface OrderDao {
    @Query("SELECT * FROM orders WHERE partnerId = :partnerId ORDER BY createdAt DESC")
    List<OrderEntity> getOrdersForPartner(String partnerId);

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    List<OrderEntity> getAllOrders();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OrderEntity order);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<OrderEntity> orders);

    @Update
    void update(OrderEntity order);

    @Delete
    void delete(OrderEntity order);
}

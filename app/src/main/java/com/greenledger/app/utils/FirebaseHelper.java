package com.greenledger.app.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greenledger.app.models.User;

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

    public DatabaseReference getUserExpensesRef(String userId) {
        return database.child("expenses").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserMaterialsRef(String userId) {
        return database.child("rawMaterials").orderByChild("userId").equalTo(userId).getRef();
    }

    public DatabaseReference getUserLabourRef(String userId) {
        return database.child("labour").orderByChild("userId").equalTo(userId).getRef();
    }

    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void logout() {
        auth.signOut();
    }
}

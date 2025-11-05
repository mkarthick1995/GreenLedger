package com.greenledger.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.greenledger.app.R;
import com.greenledger.app.utils.FirebaseHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseHelper = FirebaseHelper.getInstance();

        if (!firebaseHelper.isUserLoggedIn()) {
            // TODO: Redirect to login activity
            finish();
            return;
        }

        setupNavigation();
    }

    private void setupNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Start with dashboard if this is the first launch
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_dashboard);
            // TODO: Load dashboard fragment
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_partners) {
            startActivity(new Intent(this, BusinessPartnerActivity.class));
        } else if (id == R.id.nav_farms) {
            // TODO: Launch farms activity
        } else if (id == R.id.nav_crops) {
            // TODO: Launch crops activity
        } else if (id == R.id.nav_storage) {
            // TODO: Launch storage activity
        } else if (id == R.id.nav_profile) {
            // TODO: Launch profile activity
        } else if (id == R.id.nav_logout) {
            firebaseHelper.logout();
            // TODO: Redirect to login activity
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

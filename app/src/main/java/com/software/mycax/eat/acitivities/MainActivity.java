package com.software.mycax.eat.acitivities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.software.mycax.eat.R;
import com.software.mycax.eat.fragment.AnalyticsFragment;
import com.software.mycax.eat.fragment.DashboardFragment;
import com.software.mycax.eat.fragment.ManageStudentsFragment;
import com.software.mycax.eat.fragment.SettingsFragment;
import com.software.mycax.eat.fragment.TestCreatorFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        if (mAuth.getCurrentUser() != null) {
            // if user is signed in, add their info into navigation header
            View hView = navigationView.inflateHeaderView(R.layout.nav_header_main);
            TextView textViewName = hView.findViewById(R.id.textViewName);
            TextView textViewEmail = hView.findViewById(R.id.textViewEmail);
            textViewName.setText(mAuth.getCurrentUser().getDisplayName());
            textViewEmail.setText(mAuth.getCurrentUser().getEmail());
        }
        moveFragment(new DashboardFragment());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_dashboard:
                moveFragment(new DashboardFragment());
                break;
            case R.id.nav_test_creator:
                moveFragment(new TestCreatorFragment());
                break;
            case R.id.nav_analytics:
                moveFragment(new AnalyticsFragment());
                break;
            case R.id.nav_manage_students:
                moveFragment(new ManageStudentsFragment());
                break;
            case R.id.nav_settings:
                moveFragment(new SettingsFragment());
                break;
            case R.id.nav_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * method is used for moving to another fragment.
     *
     * @param fragment * target fragment to replace *
     */
    private void moveFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment).commit();
    }
}

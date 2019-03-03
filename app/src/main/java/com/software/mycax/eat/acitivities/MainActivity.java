package com.software.mycax.eat.acitivities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;
import com.software.mycax.eat.fragment.AnalyticsFragment;
import com.software.mycax.eat.fragment.DashboardFragment;
import com.software.mycax.eat.fragment.ManageStudentsFragment;
import com.software.mycax.eat.fragment.SettingsFragment;
import com.software.mycax.eat.fragment.TestCreatorFragment;
import com.software.mycax.eat.models.User;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private User mUser;
    private NavigationView navigationView;
    private View hView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        if (mFirebaseUser != null) {
            // if user is signed in, add their info into navigation header
            hView = navigationView.inflateHeaderView(R.layout.nav_header_main);
            getUser();
        }
        moveFragment(new DashboardFragment());
    }

    @Override
    public void onResume(){
        super.onResume();
        if (hView != null) {
            updateDrawer();
            Log.d(Utils.getTag(), "onResume: updating drawer");
        } else Log.d(Utils.getTag(), "onResume: hView is null!");
    }

    /**
     * method is used set navigation drawer user data
     */
    private void updateDrawer() {
        TextView textViewName = hView.findViewById(R.id.textViewName);
        TextView textViewEmail = hView.findViewById(R.id.textViewEmail);
        CircleImageView iProfilePic = hView.findViewById(R.id.imageViewProfilePic);
        textViewName.setText(mFirebaseUser.getDisplayName());
        textViewEmail.setText(mFirebaseUser.getEmail());
        if (mFirebaseUser.getPhotoUrl() != null) Glide.with(this).load(mFirebaseUser.getPhotoUrl()).into(iProfilePic);
    }

    /**
     * method is used to inflate different navigation drawer
     * menus based on the account type
     */
    private void getUser() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(Objects.requireNonNull(mAuth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                Log.d(Utils.getTag(), "onDataChange: read value success");
                if (mUser.getAccountType() == Utils.ACCOUNT_TEACHER) {
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.activity_teacher_drawer);
                } else if (mUser.getAccountType() == Utils.ACCOUNT_STUDENT) {
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.activity_student_drawer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to read value", error.toException());
            }
        });
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

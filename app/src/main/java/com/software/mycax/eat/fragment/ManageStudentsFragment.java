package com.software.mycax.eat.fragment;


import android.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;
import com.software.mycax.eat.adapters.ManageStudentAdapter;
import com.software.mycax.eat.models.ManageStudent;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageStudentsFragment extends Fragment {
    private FirebaseUser mFirebaseUser;
    private AnimatedRecyclerView recyclerView;
    private AVLoadingIndicatorView loadingIndicatorView;

    public ManageStudentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_students, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle(R.string.menu_manage_students);
        loadingIndicatorView = v.findViewById(R.id.avi);
        recyclerView = v.findViewById(R.id.rv);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_bottom);
        recyclerView.setLayoutAnimation(animationController);
        mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            getTeacherCode();
        }
        return v;
    }

    private void getTeacherCode() {
        loadingIndicatorView.smoothToShow();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String teacherCode = dataSnapshot.child("teacherCode").getValue(String.class);
                Log.d(Utils.getTag(), "onDataChange: read value success");
                if (teacherCode != null) getStudents(teacherCode);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to read value", error.toException());
            }
        });
    }

    private void getStudents(String teacherCode) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String accountKey = teacherCode + "_0";
        mDatabase.child("users").orderByChild("accountKey").equalTo(accountKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //create a list of ManageStudent items
                Log.d(Utils.getTag(), "onDataChange: read value success");
                List<ManageStudent> manageStudentList = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String name = userSnapshot.child("name").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    Log.d(Utils.getTag(), "userSnapshot: added");
                    manageStudentList.add(new ManageStudent(name, email));
                }
                ManageStudentAdapter manageStudentAdapter = new ManageStudentAdapter(manageStudentList);
                recyclerView.setAdapter(manageStudentAdapter);
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to read value", error.toException());
            }
        });
        loadingIndicatorView.smoothToHide();
    }
}

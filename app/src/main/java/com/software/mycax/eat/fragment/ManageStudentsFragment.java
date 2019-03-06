package com.software.mycax.eat.fragment;


import android.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

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
import com.software.mycax.eat.models.User;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageStudentsFragment extends Fragment {
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private AnimatedRecyclerView recyclerView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private ManageStudentAdapter manageStudentAdapter;
    private ImageView imageEmpty;

    public ManageStudentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_students, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getActivity().setTitle(R.string.menu_manage_students);
        imageEmpty = v.findViewById(R.id.imageEmpty);
        loadingIndicatorView = v.findViewById(R.id.avi);
        recyclerView = v.findViewById(R.id.rv);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_bottom);
        recyclerView.setLayoutAnimation(animationController);
        manageStudentAdapter = new ManageStudentAdapter();
        recyclerView.setAdapter(manageStudentAdapter);
        mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            getTeacherCode();
        }
        return v;
    }

    private void getTeacherCode() {
        loadingIndicatorView.smoothToShow();
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
        String accountKey = teacherCode + "_0";
        mDatabase.child("users").orderByChild("accountKey").equalTo(accountKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //create a list of ManageStudent items
                Log.d(Utils.getTag(), "onDataChange: read value success");
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        Log.d(Utils.getTag(), "userSnapshot: added");
                        manageStudentAdapter.addItem(new ManageStudent(user.getName(), user.getEmail()));
                    }
                }
                if (manageStudentAdapter.getItemCount() < 1) {
                    recyclerView.setVisibility(View.GONE);
                    imageEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    imageEmpty.setVisibility(View.GONE);
                }
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

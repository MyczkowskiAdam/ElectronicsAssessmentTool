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
import com.software.mycax.eat.adapters.DashboardAdapter;
import com.software.mycax.eat.models.TestLink;
import com.software.mycax.eat.models.TestSet;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {
    private FirebaseUser mFirebaseUser;
    private AnimatedRecyclerView recyclerView;
    private AVLoadingIndicatorView loadingIndicatorView;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.menu_dashboard);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle(R.string.menu_manage_students);

        loadingIndicatorView = v.findViewById(R.id.avi);
        recyclerView = v.findViewById(R.id.rv);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_bottom);
        recyclerView.setLayoutAnimation(animationController);
        mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            getAccountInfo();
        }
        return v;
    }

    private void getAccountInfo() {
        loadingIndicatorView.smoothToShow();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String teacherCode = dataSnapshot.child("teacherCode").getValue(String.class);
                Integer accountType = dataSnapshot.child("accountType").getValue(Integer.class);
                Log.d(Utils.getTag(), "onDataChange: read value success");
                if (teacherCode != null && accountType != null) getTests(teacherCode, accountType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to read value", error.toException());
            }
        });
    }

    private void getTests(String teacherCode, final Integer accountTypeBoxed) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("tests").orderByChild("teacherCode").equalTo(teacherCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //create a list of ManageStudent items
                Log.d(Utils.getTag(), "onDataChange: read value success");
                List<TestLink> testLinkList = new ArrayList<>();
                for (DataSnapshot testSnapshot : dataSnapshot.getChildren()) {
                    TestSet testSet = testSnapshot.getValue(TestSet.class);
                    if (testSet != null) {
                        testLinkList.add(new TestLink(testSet.getTestUid(), testSet.getTestTopic()));
                        Log.d(Utils.getTag(), "testSnapshot: added");
                    }
                }
                DashboardAdapter dashboardAdapter = new DashboardAdapter(testLinkList, accountTypeBoxed, getActivity());
                recyclerView.setAdapter(dashboardAdapter);
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

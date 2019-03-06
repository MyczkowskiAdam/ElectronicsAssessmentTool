package com.software.mycax.eat.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.media.Image;
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
import com.software.mycax.eat.acitivities.AttemptTestActivity;
import com.software.mycax.eat.acitivities.UpdateTestActivity;
import com.software.mycax.eat.adapters.DashboardAdapter;
import com.software.mycax.eat.models.TestLink;
import com.software.mycax.eat.models.TestSet;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements DashboardAdapter.DashboardCallbackInterface {
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private AnimatedRecyclerView recyclerView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private DashboardAdapter dashboardAdapter;
    private ImageView imageEmpty;

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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getActivity().setTitle(R.string.menu_dashboard);

        imageEmpty = v.findViewById(R.id.imageEmpty);
        loadingIndicatorView = v.findViewById(R.id.avi);
        recyclerView = v.findViewById(R.id.rv);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_bottom);
        recyclerView.setLayoutAnimation(animationController);
        dashboardAdapter = new DashboardAdapter(this);
        recyclerView.setAdapter(dashboardAdapter);
        mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            getAccountInfo();
        }
        return v;
    }

    private void getAccountInfo() {
        loadingIndicatorView.smoothToShow();
        mDatabase.child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String teacherCode = dataSnapshot.child("teacherCode").getValue(String.class);
                Integer accountType = dataSnapshot.child("accountType").getValue(Integer.class);
                Log.d(Utils.getTag(), "onDataChange: read users value success");
                if (teacherCode != null && accountType != null) {
                    dashboardAdapter.setAccountType(accountType);
                    getTests(teacherCode, accountType);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to read value", error.toException());
            }
        });
    }

    private void getTests(String teacherCode, final Integer accountTypeBoxed) {
        mDatabase.child("tests").orderByChild("teacherCode").equalTo(teacherCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //create a list of ManageStudent items
                Log.d(Utils.getTag(), "onDataChange: read tests value success");
                for (DataSnapshot testSnapshot : dataSnapshot.getChildren()) {
                    TestSet testSet = testSnapshot.getValue(TestSet.class);
                    if (testSet != null) {
                        if (accountTypeBoxed == Utils.ACCOUNT_STUDENT) {
                            removeCompletedTest(testSet);
                        } else {
                            dashboardAdapter.addItem(new TestLink(testSet.getTestUid(), testSet.getTestTopic()));
                            Log.d(Utils.getTag(), "testSnapshot: added");
                        }
                    }
                }
                onCheckEmpty();
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to read tests value", error.toException());
            }
        });
        loadingIndicatorView.smoothToHide();
    }

    private void removeCompletedTest(final TestSet testSet) {
        mDatabase.child("testResults").child(mFirebaseUser.getUid()).child(testSet.getTestUid()).child("completed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //create a list of ManageStudent items
                Log.d(Utils.getTag(), "onDataChange: read testResults value success");
                Boolean isCompleted = dataSnapshot.getValue(Boolean.class);
                if (isCompleted != null && isCompleted)
                    Log.d(Utils.getTag(), "testSnapshot: skipping");
                else {
                    dashboardAdapter.addItem(new TestLink(testSet.getTestUid(), testSet.getTestTopic()));
                    Log.d(Utils.getTag(), "testSnapshot: added");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to testResults read value", error.toException());
            }
        });
    }

    @Override
    public void onHandleTestCompletion(int position, String testUid) {
        Intent intent = new Intent(getActivity(), AttemptTestActivity.class);
        intent.putExtra("testUid", testUid);
        intent.putExtra("adapterPosition", position);
        getActivity().startActivityForResult(intent, Utils.ATTEMPT_TEST_INTENT);
    }

    @Override
    public void onHandleTestUpdate(int position, String testUid) {
        Intent intent = new Intent(getActivity(), UpdateTestActivity.class);
        intent.putExtra("testUid", testUid);
        intent.putExtra("adapterPosition", position);
        getActivity().startActivityForResult(intent, Utils.EDIT_TEST_INTENT);
    }

    @Override
    public void onCheckEmpty() {
        if (dashboardAdapter.getItemCount() < 1) {
            recyclerView.setVisibility(View.GONE);
            imageEmpty.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            imageEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.ATTEMPT_TEST_INTENT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                boolean isCompleted = data.getBooleanExtra("isCompleted", false);
                if (isCompleted) {
                    int adapterPosition = data.getIntExtra("adapterPosition", -1);
                    if (adapterPosition != -1) {
                        dashboardAdapter.notifyItemRemoved(adapterPosition);
                    }
                }
            }
        } else if (requestCode == Utils.EDIT_TEST_INTENT && mFirebaseUser != null) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                boolean isEdited = data.getBooleanExtra("isEdited", false);
                if (isEdited) {
                    int adapterPosition = data.getIntExtra("adapterPosition", -1);
                    if (adapterPosition != -1) {
                        String testUid = data.getStringExtra("testUid");
                        getTestData(testUid, adapterPosition);
                    }
                }
            }
        }
    }

    private void getTestData(String testUid, final int adapterPosition) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("tests").child(testUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TestSet testSet = dataSnapshot.getValue(TestSet.class);
                if (testSet != null) {
                    dashboardAdapter.setItem(adapterPosition, new TestLink(testSet.getTestUid(), testSet.getTestTopic()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to read value", error.toException());
            }
        });
    }
}

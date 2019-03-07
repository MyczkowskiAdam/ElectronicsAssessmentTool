package com.software.mycax.eat.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;


import android.os.Handler;
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

import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.header.MaterialHeader;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements DashboardAdapter.DashboardCallbackInterface, SmoothRefreshLayout.OnRefreshListener {
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private AnimatedRecyclerView recyclerView;
    private DashboardAdapter dashboardAdapter;
    private ImageView imageEmpty;
    private Handler mHandler;
    private SmoothRefreshLayout mRefreshLayout;

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
        recyclerView = v.findViewById(R.id.rv);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_bottom);
        recyclerView.setLayoutAnimation(animationController);
        mHandler = new Handler();
        mRefreshLayout = v.findViewById(R.id.dashboard_fragment_refresh);
        mFirebaseUser = mAuth.getCurrentUser();
        mRefreshLayout.setHeaderView(new MaterialHeader(getActivity()));
        mRefreshLayout.setOnRefreshListener(this);
        if (mFirebaseUser != null) {
            mRefreshLayout.autoRefresh();
        }
        return v;
    }

    private void getAccountInfo() {
        mDatabase.child(Utils.CHILD_REF_USERS).child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String teacherCode = dataSnapshot.child(Utils.CHILD_REF_TEACHER_CODE).getValue(String.class);
                Integer accountType = dataSnapshot.child(Utils.CHILD_REF_ACCOUNT_TYPE).getValue(Integer.class);
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
        mDatabase.child(Utils.CHILD_REF_TESTS).orderByChild(Utils.CHILD_REF_TEACHER_CODE).equalTo(teacherCode).addListenerForSingleValueEvent(new ValueEventListener() {
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
                if (accountTypeBoxed == Utils.ACCOUNT_TEACHER) {
                    onCheckEmpty(dashboardAdapter.getItemCount());
                    recyclerView.scheduleLayoutAnimation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to read tests value", error.toException());
            }
        });
    }

    private void removeCompletedTest(final TestSet testSet) {
        mDatabase.child(Utils.CHILD_REF_TEST_RESULTS).child(mFirebaseUser.getUid()).child(testSet.getTestUid()).child(Utils.CHILD_REF_COMPLETED).addListenerForSingleValueEvent(new ValueEventListener() {
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
                onCheckEmpty(dashboardAdapter.getItemCount());
                recyclerView.scheduleLayoutAnimation();
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
        intent.putExtra(Utils.EXTRA_STRING_TESTUID, testUid);
        intent.putExtra(Utils.EXTRA_INT_ADAPTER_POSITION, position);
        getActivity().startActivityForResult(intent, Utils.ATTEMPT_TEST_INTENT);
    }

    @Override
    public void onHandleTestUpdate(int position, String testUid) {
        Intent intent = new Intent(getActivity(), UpdateTestActivity.class);
        intent.putExtra(Utils.EXTRA_STRING_TESTUID, testUid);
        intent.putExtra(Utils.EXTRA_INT_ADAPTER_POSITION, position);
        getActivity().startActivityForResult(intent, Utils.EDIT_TEST_INTENT);
    }

    @Override
    public void onCheckEmpty(int itemCount) {
        if (itemCount < 1) {
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
                boolean isCompleted = data.getBooleanExtra(Utils.EXTRA_BOOLEAN_IS_COMPLETED, false);
                if (isCompleted) {
                    int adapterPosition = data.getIntExtra(Utils.EXTRA_INT_ADAPTER_POSITION, -1);
                    if (adapterPosition != -1) {
                        dashboardAdapter.removeItem(adapterPosition);
                        onCheckEmpty(dashboardAdapter.getItemCount());
                    }
                }
            }
        } else if (requestCode == Utils.EDIT_TEST_INTENT && mFirebaseUser != null) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                boolean isEdited = data.getBooleanExtra(Utils.EXTRA_BOOLEAN_IS_EDITED, false);
                if (isEdited) {
                    int adapterPosition = data.getIntExtra(Utils.EXTRA_INT_ADAPTER_POSITION, -1);
                    if (adapterPosition != -1) {
                        String testUid = data.getStringExtra(Utils.EXTRA_STRING_TESTUID);
                        getTestData(testUid, adapterPosition);
                    }
                }
            }
        }
    }

    private void getTestData(String testUid, final int adapterPosition) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(Utils.CHILD_REF_TESTS).child(testUid).addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public void onRefreshing() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dashboardAdapter = new DashboardAdapter(DashboardFragment.this);
                    getAccountInfo();
                    recyclerView.setAdapter(dashboardAdapter);
                    mRefreshLayout.refreshComplete();
                    Log.d(Utils.getTag(), "Refreshing");
                }
            }, 1000);
    }

    @Override
    public void onLoadingMore() {
        Log.d(Utils.getTag(), "Loading more");
    }
}

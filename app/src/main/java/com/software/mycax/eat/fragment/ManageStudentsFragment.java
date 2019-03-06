package com.software.mycax.eat.fragment;


import android.app.Fragment;
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
import com.software.mycax.eat.adapters.ManageStudentAdapter;
import com.software.mycax.eat.models.ManageStudent;
import com.software.mycax.eat.models.User;

import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.header.MaterialHeader;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageStudentsFragment extends Fragment implements  SmoothRefreshLayout.OnRefreshListener{
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private AnimatedRecyclerView recyclerView;
    private ManageStudentAdapter manageStudentAdapter;
    private ImageView imageEmpty;
    private Handler mHandler;
    private SmoothRefreshLayout mRefreshLayout;

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
        recyclerView = v.findViewById(R.id.rv);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_bottom);
        recyclerView.setLayoutAnimation(animationController);
        mHandler = new Handler();
        mRefreshLayout = v.findViewById(R.id.manage_students_fragment_refresh);
        mFirebaseUser = mAuth.getCurrentUser();
        mRefreshLayout.setHeaderView(new MaterialHeader(getActivity()));
        mRefreshLayout.setOnRefreshListener(this);
        if (mFirebaseUser != null) {
            mRefreshLayout.autoRefresh();
        }
        return v;
    }

    private void getTeacherCode() {
        mDatabase.child(Utils.CHILD_REF_USERS).child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String teacherCode = dataSnapshot.child(Utils.CHILD_REF_TEACHER_CODE).getValue(String.class);
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
        mDatabase.child(Utils.CHILD_REF_USERS).orderByChild(Utils.CHILD_REF_ACCOUNT_KEY).equalTo(accountKey).addValueEventListener(new ValueEventListener() {
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
                recyclerView.scheduleLayoutAnimation();
                if (manageStudentAdapter.getItemCount() < 1) {
                    recyclerView.setVisibility(View.GONE);
                    imageEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    imageEmpty.setVisibility(View.GONE);
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
                manageStudentAdapter = new ManageStudentAdapter();
                getTeacherCode();
                recyclerView.setAdapter(manageStudentAdapter);
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

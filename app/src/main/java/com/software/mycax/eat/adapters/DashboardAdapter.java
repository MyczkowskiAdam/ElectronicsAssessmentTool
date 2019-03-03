package com.software.mycax.eat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;
import com.software.mycax.eat.acitivities.AttemptTestActivity;
import com.software.mycax.eat.acitivities.UpdateTestActivity;
import com.software.mycax.eat.models.TestLink;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class DashboardAdapter extends AnimatedRecyclerView.Adapter<DashboardAdapter.ItemHolder> {
    private final List<TestLink> testLinkList;
    private final int accountType;
    private final Context context;

    public DashboardAdapter(List<TestLink> testLinkList, int accountType, Context context) {
        this.testLinkList = testLinkList;
        this.accountType = accountType;
        this.context = context;
    }

    class ItemHolder extends AnimatedRecyclerView.ViewHolder implements View.OnClickListener {
        private String testUid;
        private final TextView tTestTopic;
        private FancyButton bDelete, bEdit, bAttemptTest;

        ItemHolder(final View itemView) {
            super(itemView);
            tTestTopic = itemView.findViewById(R.id.text_test_topic);
            if (accountType == Utils.ACCOUNT_TEACHER) {
                bDelete = itemView.findViewById(R.id.delete_test_btn);
                bDelete.setOnClickListener(this);
                bEdit = itemView.findViewById(R.id.edit_test_btn);
                bEdit.setOnClickListener(this);
            } else if (accountType == Utils.ACCOUNT_STUDENT) {
                bAttemptTest = itemView.findViewById(R.id.attempt_test_btn);
                bAttemptTest.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(final View v) {
            //handle button clicks
            if (v.getId() == R.id.delete_test_btn) {
                DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("tests").child(testUid);
                mPostReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(Utils.getTag(), "deleteTest:success");
                            Snackbar.make(v, R.string.test_delete_success, Snackbar.LENGTH_LONG).show();
                            testLinkList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());

                        } else {
                            Log.w(Utils.getTag(),"deleteTest:failure", task.getException());
                            Snackbar.make(v, R.string.test_delete_failure, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

            } else if (v.getId()== R.id.edit_test_btn) {
                Intent intent = new Intent(context, UpdateTestActivity.class);
                intent.putExtra("testUid", testUid);
                context.startActivity(intent);

            } else if (v.getId() == R.id.attempt_test_btn) {
                Intent intent = new Intent(context, AttemptTestActivity.class);
                intent.putExtra("testUid", testUid);
                context.startActivity(intent);
            }
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate item
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(accountType == Utils.ACCOUNT_STUDENT ? R.layout.test_student_item : R.layout.test_teacher_item, parent, false);
        return new ItemHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        // set item fields
        TestLink testLink = testLinkList.get(position);
        holder.tTestTopic.setText(testLink.getTestTopic());
        holder.testUid = testLink.getTestUid();
    }

    @Override
    public int getItemCount() {
        return testLinkList.size();
    }

}

package com.software.mycax.eat.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;
import com.software.mycax.eat.models.TestLink;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class DashboardAdapter extends AnimatedRecyclerView.Adapter<DashboardAdapter.ItemHolder> {
    private final List<TestLink> testLinkList;
    private int accountType;
    private final DashboardCallbackInterface mCallback;

    public interface DashboardCallbackInterface {

        void onHandleTestCompletion(int position, String testUid);

        void onHandleTestUpdate(int position, String testUid);

        void onCheckEmpty();
    }

    public DashboardAdapter(DashboardCallbackInterface mCallback) {
        this.testLinkList = new ArrayList<>();
        this.mCallback = mCallback;
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
            if (mCallback != null) {
                //handle button clicks
                if (v.getId() == R.id.delete_test_btn) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle(R.string.action_delete_test)
                            .setMessage(v.getContext().getResources().getString(R.string.text_delete_test, testLinkList.get(getAdapterPosition()).getTestTopic()))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("tests").child(testUid);
                                    mPostReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(Utils.getTag(), "deleteTest:success");
                                                Snackbar.make(v, R.string.test_delete_success, Snackbar.LENGTH_LONG).show();
                                                testLinkList.remove(getAdapterPosition());
                                                notifyItemRemoved(getAdapterPosition());
                                                mCallback.onCheckEmpty();
                                            } else {
                                                Log.w(Utils.getTag(), "deleteTest:failure", task.getException());
                                                Snackbar.make(v, R.string.test_delete_failure, Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }

                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();

                } else if (v.getId() == R.id.edit_test_btn) {
                    mCallback.onHandleTestUpdate(getAdapterPosition(), testUid);

                } else if (v.getId() == R.id.attempt_test_btn) {
                    mCallback.onHandleTestCompletion(getAdapterPosition(), testUid);
                }
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

    public void addItem(TestLink testLink) {
        testLinkList.add(testLink);
        notifyItemInserted(getItemCount() - 1);
    }

    public void setItem(int position, TestLink testLink) {
        testLinkList.set(position, testLink);
        notifyItemChanged(position);
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

}

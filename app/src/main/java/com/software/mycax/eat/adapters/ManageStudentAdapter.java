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
import com.google.firebase.auth.FirebaseAuth;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;
import com.software.mycax.eat.models.ManageStudent;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class ManageStudentAdapter extends AnimatedRecyclerView.Adapter<ManageStudentAdapter.ItemHolder> {
    private final List<ManageStudent> manageStudentList;

    public ManageStudentAdapter() {
        this.manageStudentList = new ArrayList<>();
    }

    class ItemHolder extends AnimatedRecyclerView.ViewHolder implements View.OnClickListener {
        final TextView name;
        final TextView email;
        private final FancyButton bResetPass;

        ItemHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            email = itemView.findViewById(R.id.text_email);
            bResetPass = itemView.findViewById(R.id.reset_password_btn);
            bResetPass.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //handle password reset button click
            if (v.getId() == R.id.reset_password_btn) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.action_reset_password)
                        .setMessage(v.getContext().getResources().getString(R.string.text_reset_password, email.getText().toString()))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(Utils.getTag(), "Reset email sent.");
                                                } else {
                                                    Log.w(Utils.getTag(), "Reset email not sent");
                                                }
                                            }
                                        });
                            }

                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate item
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        return new ItemHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        // set item fields
        ManageStudent manageStudent = manageStudentList.get(position);
        holder.name.setText(manageStudent.getStudentName());
        holder.email.setText(manageStudent.getStudentEmail());
    }

    @Override
    public int getItemCount() {
        return manageStudentList.size();
    }

    public void addItem(ManageStudent manageStudent) {
        manageStudentList.add(manageStudent);
        notifyItemInserted(getItemCount()-1);
    }

}

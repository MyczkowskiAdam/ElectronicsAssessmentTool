package com.software.mycax.eat.acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;
import com.software.mycax.eat.models.User;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private FirebaseUser mFirebaseUser;
    private User mUser;
    private EditText eName;
    private EditText eSchoolCode;
    private EditText eTeacherCode;
    private AppCompatTextView tAccountLabel;
    private CircleImageView iProfilePic;
    private int accountType;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        eName = findViewById(R.id.name_edit_text);
        eSchoolCode = findViewById(R.id.school_code_edit_text);
        eTeacherCode = findViewById(R.id.teacher_code_edit_text);
        tAccountLabel = findViewById(R.id.textViewAccountLabel);
        iProfilePic = findViewById(R.id.imageViewProfilePic);
        FancyButton bUpdateProfile = findViewById(R.id.update_user_button);
        if (mFirebaseUser != null) {
            // if user is signed in, allow data update
            setUserData();
            iProfilePic.setOnClickListener(this);
            iProfilePic.setOnLongClickListener(this);
            bUpdateProfile.setOnClickListener(this);
        }
    }

    /**
     * method is used to retrieve data from Firebase database
     */
    private void setUserData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                Log.d(Utils.getTag(), "onDataChange: read value success");
                eName.setText(mUser.getName());
                eTeacherCode.setText(mUser.getTeacherCode());
                eSchoolCode.setText(mUser.getSchoolCode());
                tAccountLabel.setText(getString(R.string.label_your_account, mUser.getEmail()));
                accountType = mUser.getAccountType();
                if (mFirebaseUser.getPhotoUrl() != null)
                    Glide.with(UpdateProfileActivity.this).load(mFirebaseUser.getPhotoUrl()).into(iProfilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to read value", error.toException());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // on back pressed navigate to previous activity
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.imageViewProfilePic) {
            // To pick a new profile picture we need READ_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, Utils.PERMISSION_READ_EXTERNAL_STORAGE);
            else {
                pickImage();
            }
        }
        if (v.getId() == R.id.update_user_button) {
            // Add new picture to profile builder
            new AlertDialog.Builder(v.getContext())
                    .setTitle(R.string.action_update_profile)
                    .setMessage(R.string.text_update_profile)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                            builder.setPhotoUri(mUri);
                            builder.setDisplayName(eName.getText().toString());
                            UserProfileChangeRequest profileUpdates = builder.build();
                            mFirebaseUser.updateProfile(profileUpdates) // Upload changes to Firebase
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(Utils.getTag(), "profileUpdate:success");
                                                Snackbar.make(v, R.string.update_profile, Snackbar.LENGTH_LONG).show();
                                            } else {
                                                Log.w(Utils.getTag(), "profileUpdate:failure");
                                                Snackbar.make(v, R.string.update_profile_failure, Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            User updateUser = new User(mFirebaseUser.getUid(), eName.getText().toString(), mFirebaseUser.getEmail(), eSchoolCode.getText().toString(),
                                    eTeacherCode.getText().toString(), accountType,
                                    eTeacherCode.getText().toString() + "_" + accountType);
                            // Upload changes to Firebase database
                            mDatabase.child("users").child(mFirebaseUser.getUid()).setValue(updateUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(Utils.getTag(), "createUserDatabaseEntry:success");
                                    Snackbar.make(v, R.string.update_success, Snackbar.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(Utils.getTag(), "createUserDatabaseEntry:failure; deleting user", e.getCause());
                                    Snackbar.make(v, R.string.update_failure, Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }

                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }

    @Override
    public boolean onLongClick(final View v) {
        if (v.getId() == R.id.imageViewProfilePic) {
            new AlertDialog.Builder(v.getContext())
                    .setTitle(R.string.action_remove_picture)
                    .setMessage(R.string.text_remove_picture)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            iProfilePic.setImageDrawable(getDrawable(R.drawable.ic_profile_image));
                            mUri = null;
                        }

                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // check if permission is granted
                pickImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Load new profile picture into ImageView
            mUri = data.getData();
            Glide.with(this).load(data.getData()).into(iProfilePic);
        }
    }

    /**
     * method is used to start picker activity
     */
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Utils.GALLERY_INTENT);
    }
}

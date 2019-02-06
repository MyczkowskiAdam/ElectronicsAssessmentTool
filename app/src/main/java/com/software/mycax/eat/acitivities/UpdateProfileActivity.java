package com.software.mycax.eat.acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private User mUser;
    private EditText eName, eSchoolCode, eTeacherCode;
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
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        eTeacherCode = findViewById(R.id.teacher_code_edit_text);
        eName = findViewById(R.id.name_edit_text);
        eSchoolCode = findViewById(R.id.school_code_edit_text);
        tAccountLabel = findViewById(R.id.textViewAccountLabel);
        iProfilePic = findViewById(R.id.imageViewProfilePic);
        FancyButton bUpdateProfile = findViewById(R.id.update_user_button);
        if (mFirebaseUser != null) {
            // if user is signed in, allow data update
            setUserData();
            iProfilePic.setOnClickListener(this);
            bUpdateProfile.setOnClickListener(this);
        }
    }

    private void setUserData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(Objects.requireNonNull(mAuth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                Log.d(Utils.getTag(), "onDataChange: read value success");
                eName.setText(mUser.getName());
                eTeacherCode.setText(mUser.getTeacherCode());
                eSchoolCode.setText(mUser.getSchoolCode());
                tAccountLabel.setText(getString(R.string.label_your_account, mUser.getEmail()));
                accountType = mUser.getAccountType();
                if (mFirebaseUser.getPhotoUrl() != null) Glide.with(UpdateProfileActivity.this).load(mFirebaseUser.getPhotoUrl()).into(iProfilePic);
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
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageViewProfilePic) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, Utils.PERMISSION_READ_EXTERNAL_STORAGE);
            else {
                pickImage();
            }
        }
        if (v.getId() == R.id.update_user_button)  {
            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
            if (mUri != null) {
                builder.setPhotoUri(mUri);
            }
            builder.setDisplayName(eName.getText().toString());
            UserProfileChangeRequest profileUpdates = builder.build();
            mFirebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(Utils.getTag(), "profileUpdate:success");
                                Toast.makeText(UpdateProfileActivity.this, R.string.update_profile, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            User updateUser = new User(eName.getText().toString(), mFirebaseUser.getEmail(), eSchoolCode.getText().toString(),
                    eTeacherCode.getText().toString(), accountType,
                    eTeacherCode.getText().toString() + "_" + accountType);
            mDatabase.child("users").child(mFirebaseUser.getUid()).setValue(updateUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(Utils.getTag(), "createUserDatabaseEntry:success");
                    Toast.makeText(UpdateProfileActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(Utils.getTag(), "createUserDatabaseEntry:failure; deleting user", e.getCause());
                    Toast.makeText(UpdateProfileActivity.this, R.string.update_failure, Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUri = data.getData();
            Glide.with(this).load(data.getData()).into(iProfilePic);
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Utils.GALLERY_INTENT);
    }
}
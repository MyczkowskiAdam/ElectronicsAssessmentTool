package com.software.mycax.eat.acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.software.mycax.eat.R;
import com.software.mycax.eat.models.User;

import mehdi.sakout.fancybuttons.FancyButton;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SignupAcitivity extends AppCompatActivity implements View.OnClickListener {
    private EditText eEmail, ePassword, eName, eSchoolCode, eTeacherCode;
    private FirebaseAuth mAuth;
    private Spinner accountTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_signup);
        eTeacherCode = findViewById(R.id.teacher_code_edit_text);
        eEmail = findViewById(R.id.email_edit_text);
        ePassword = findViewById(R.id.password_edit_text);
        eName = findViewById(R.id.name_edit_text);
        eSchoolCode = findViewById(R.id.school_code_edit_text);
        accountTypeSpinner = findViewById(R.id.account_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),
                R.array.account_types, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountTypeSpinner.setAdapter(adapter);
        FancyButton createUserButton = findViewById(R.id.create_user_button);
        createUserButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.create_user_button) {
            mAuth.createUserWithEmailAndPassword(eEmail.getText().toString(), ePassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                createNewUser(user);
                                startActivity(new Intent(SignupAcitivity.this, MainActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignupAcitivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }

    private void createNewUser(FirebaseUser firebaseUser) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        User newUser = new User(eName.getText().toString(), eEmail.getText().toString(), eSchoolCode.getText().toString(),
                eTeacherCode.getText().toString(), accountTypeSpinner.getSelectedItemPosition());
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(eName.getText().toString()).build();
        firebaseUser.updateProfile(profileUpdates);
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(newUser);
    }
}

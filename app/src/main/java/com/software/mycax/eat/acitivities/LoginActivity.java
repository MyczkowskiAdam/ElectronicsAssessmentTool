package com.software.mycax.eat.acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;

import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText eEmail;
    private EditText ePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        eEmail = findViewById(R.id.email_edit_text);
        ePassword= findViewById(R.id.password_edit_text);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FancyButton bLogin = findViewById(R.id.loginButton);
        bLogin.setOnClickListener(this);
        FancyButton bSignup = findViewById(R.id.signupButton);
        bSignup.setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.loginButton) {
            if (!TextUtils.isEmpty(eEmail.getText()) && !TextUtils.isEmpty(ePassword.getText()) && Utils.isEmailValid(eEmail.getText().toString())) {
                mAuth.signInWithEmailAndPassword(eEmail.getText().toString(), ePassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, move to main activity
                                    Log.d(Utils.getTag(), "signInWithEmail:success");
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(Utils.getTag(), "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                                    Snackbar.make(v, R.string.authentication_failed, Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                Snackbar.make(v, R.string.invalid_email, Snackbar.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.signupButton) {
            startActivity(new Intent(this, SignupActivity.class));
        }
    }
}

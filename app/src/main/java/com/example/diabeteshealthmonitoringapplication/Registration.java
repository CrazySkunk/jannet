package com.example.diabeteshealthmonitoringapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    private static final String TAG = "Registration.class";
    private String username, phone, email, password, cPassword;
    private EditText mUserName, mPhone, mEmail, mPassword, mCpasword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mUserName = findViewById(R.id.username_register);
        mEmail = findViewById(R.id.email_register);
        mPhone = findViewById(R.id.phone_no_register);
        mPassword = findViewById(R.id.password_register);
        mCpasword = findViewById(R.id.confirm_password);
        Button register = findViewById(R.id.register);
        ProgressBar progressBar = findViewById(R.id.progress);
        register.setOnClickListener(v -> {
            Log.i(TAG, "onCreate: register btn clicked");
            register.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            username = mUserName.getText().toString().trim();
            email = mEmail.getText().toString().trim();
            phone = mPhone.getText().toString().trim();
            password = mPassword.getText().toString().trim();
            cPassword = mCpasword.getText().toString().trim();
            if (username.isEmpty()) {
                mUserName.setError("Can't be empty");
            } else {
                if (email.isEmpty()) {
                    mEmail.setError("Can't be empty");
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        mEmail.setError("Invalid email address");
                    } else {
                        if (phone.isEmpty()) {
                            mPhone.setError("Can't be empty");
                        } else {
                            if (password.isEmpty()) {
                                mPassword.setError("Can't be empty");
                            } else {
                                if (cPassword.isEmpty()) {
                                    mCpasword.setError("Can't be empty");
                                } else {
                                    if (!password.equals(cPassword)) {
                                        mCpasword.setError("Passwords don't match");
                                    } else {
                                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        String uid = FirebaseAuth.getInstance().getUid();
                                                        User user = new User(
                                                                uid,
                                                                username,
                                                                email,
                                                                phone
                                                        );
                                                        FirebaseDatabase.getInstance().getReference("users/" + uid)
                                                                .setValue(user)
                                                                .addOnCompleteListener(t -> {
                                                                    if (t.isSuccessful()) {
                                                                        startActivity(new Intent(Registration.this, Homepage.class));
                                                                    }
                                                                });
                                                    }
                                                }).addOnFailureListener(e -> {
                                            register.setEnabled(true);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(Registration.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }
                            }

                        }
                    }
                }
            }
        });
    }
}
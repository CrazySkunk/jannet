package com.example.diabeteshealthmonitoringapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class MainActivity extends AppCompatActivity {
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText mEmail = findViewById(R.id.email_login);
        EditText pass = findViewById(R.id.psswd);

        Button login =  findViewById(R.id.Login);
        login.setOnClickListener(v -> {
            this.email = mEmail.getText().toString().trim();
            this.password = pass.getText().toString().trim();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(MainActivity.this, Homepage.class));
                        }
                    }).addOnFailureListener(e -> {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(MainActivity.this, "Invalid credential", Toast.LENGTH_SHORT).show();
                }
            });

        });

        Button registration = findViewById(R.id.Register);
        registration.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Registration.class));
        });

    }
}
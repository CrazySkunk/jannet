package com.example.diabeteshealthmonitoringapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.ims.RegistrationManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button loginBtn;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText mEmail = findViewById(R.id.email_login);
        EditText pass = findViewById(R.id.psswd);

        Button login = (Button) findViewById(R.id.Login);
        login.setOnClickListener(v -> {
            this.email = mEmail.getText().toString().trim();
            this.password = pass.getText().toString().trim();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(MainActivity.this, Homepage.class));
                        }
                    }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this, "Invalid credential", Toast.LENGTH_SHORT).show();
            });

        });

        Button registration = (Button) findViewById(R.id.Register);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegistration = new Intent(MainActivity.this, Registration.class);
                startActivity(intentRegistration);
            }
        });

    }
}
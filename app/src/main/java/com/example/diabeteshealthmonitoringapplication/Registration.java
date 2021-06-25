package com.example.diabeteshealthmonitoringapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Registration extends AppCompatActivity {
    private static final String TAG = "Registration.class";
    private String username, phone, email, password, cPassword;
    private EditText mUserName, mPhone, mEmail, mPassword, mCpasword;
    private ProgressDialog progressDialog;
    private ImageView imageView;
    private Uri selectedImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        mUserName = findViewById(R.id.username_register);
        mEmail = findViewById(R.id.email_register);
        mPhone = findViewById(R.id.phone_no_register);
        mPassword = findViewById(R.id.password_register);
        mCpasword = findViewById(R.id.confirm_password);
        Button register = findViewById(R.id.register);
        imageView = findViewById(R.id.image);
        imageView.setOnClickListener(v -> checkPermissions());
        register.setOnClickListener(v -> {
            Log.i(TAG, "onCreate: register btn clicked");
            progressDialog.show();
            register.setEnabled(false);
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
                                                        if (selectedImage == null) {
                                                            Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            StorageReference reference = FirebaseStorage.getInstance().getReference("user_images/" + uid);
                                                            reference.putFile(selectedImage)
                                                                    .addOnCompleteListener(task1 -> {
                                                                        if (task1.isSuccessful()) {
                                                                            String imageUrl = reference.getDownloadUrl().toString();
                                                                            User user = new User(
                                                                                    uid,
                                                                                    username,
                                                                                    email,
                                                                                    phone,
                                                                                    imageUrl
                                                                            );
                                                                            FirebaseDatabase.getInstance().getReference("users/" + uid)
                                                                                    .setValue(user)
                                                                                    .addOnCompleteListener(t -> {
                                                                                        if (t.isSuccessful()) {
                                                                                            startActivity(new Intent(Registration.this, HomePage.class));
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }

                                                    }
                                                }).addOnFailureListener(e -> {
                                            register.setEnabled(true);
                                            progressDialog.dismiss();
                                            if (e instanceof FirebaseAuthUserCollisionException) {
                                                Toast.makeText(Registration.this, "Use already exists consider logging in", Toast.LENGTH_SHORT).show();
                                            } else if (e instanceof FirebaseAuthInvalidUserException) {
                                                Toast.makeText(Registration.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Registration.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
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

    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                displayDialog();
            } else {
                goToGallery();
            }
        }
    }

    public void displayDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Diabetes monitor app");
        alertDialog.setMessage("This permission is necessary");
        alertDialog.setPositiveButton("Ok", (dialog, which) -> {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions.equals(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
            goToGallery();
        }
    }

    void goToGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageView.setImageBitmap(bm);
            } catch (Exception e) {
                Log.d(TAG, "Error: Image uri not converted to bitmap");
            }
        }
    }
}
package com.example.xyz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xyz.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class UpdateUserActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private Button chooseAvatar, saveChange, saveAvatar;
    private ImageView userImage;
    private EditText username, address, numberPhone;
    private RadioButton male, female, admin, customer, manager;
    private ProgressBar progress_bar;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private StorageTask mUploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        address = findViewById(R.id.address);
        numberPhone = findViewById(R.id.numberPhone);
        chooseAvatar = findViewById(R.id.chooseAvatar);
        saveChange = findViewById(R.id.saveChange);
        saveAvatar = findViewById(R.id.saveAvatar);
        username = findViewById(R.id.username);
        userImage = findViewById(R.id.userImage);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        admin = findViewById(R.id.admin);
        customer = findViewById(R.id.customer);
        manager = findViewById(R.id.manager);
        progress_bar = findViewById(R.id.progress_bar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getGender().equals("male")) male.setChecked(true);
                if (user.getGender().equals("female")) female.setChecked(true);
                if (user.getActor().equals("admin")) admin.setChecked(true);
                if (user.getActor().equals("customer")) customer.setChecked(true);
                address.setText(user.getAddress());
                numberPhone.setText(user.getNumber());

                if (user.getAvatar().equals("default")) {
                    userImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(UpdateUserActivity.this).load(user.getAvatar()).into(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Toast.makeText(UpdateUserActivity.this, firebaseUser.getUid(), Toast.LENGTH_SHORT).show();

        chooseAvatar.setOnClickListener(v -> getImageFromDevice());

        saveAvatar.setOnClickListener(v -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(UpdateUserActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile();
            }
        });

        saveChange.setOnClickListener(v -> saveInfo());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(userImage);
        }
    }

    private void getImageFromDevice() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            String src = System.currentTimeMillis() + "." + getFileExtension(imageUri);
            final StorageReference fileReference = storageReference.child(src);

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> progress_bar.setProgress(0), 100);
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progress_bar.setProgress((int) progress);
                    })
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return fileReference.getDownloadUrl();
                    })
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("avatar", downloadUri.toString());
                            databaseReference.child("avatar").setValue(downloadUri.toString());

                            Toast.makeText(UpdateUserActivity.this, "successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(UpdateUserActivity.this, "failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }

    private void saveInfo() {

        String gender = "male";
        String actor = "customer";
        if (female.isChecked()) gender = "female";
        if (admin.isChecked()) actor = "admin";
        if (manager.isChecked()) actor = "manager";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", username.getText().toString());
        hashMap.put("gender", gender);
        hashMap.put("number", numberPhone.getText().toString());
        hashMap.put("address", address.getText().toString());
        hashMap.put("actor", actor);

        final Task<Void> voidTask = databaseReference.updateChildren(Collections.unmodifiableMap(hashMap));

        Toast.makeText(UpdateUserActivity.this, "success", Toast.LENGTH_SHORT).show();
    }

}
package com.example.xyz;

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
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.xyz.Model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class UpdateManagerProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private EditText name, description, species, price, number;
    private ImageView image;
    private Button buttonImportImage, buttonUpload, buttonSaveImage;
    private RatingBar ratingBar;
    private ProgressBar progress_bar;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_manager_product);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        species = findViewById(R.id.species);
        price = findViewById(R.id.price);
        number = findViewById(R.id.number);
        image = findViewById(R.id.image);
        buttonImportImage = findViewById(R.id.buttonImportImage);
        buttonUpload = findViewById(R.id.buttonUpload);
        ratingBar = findViewById(R.id.ratingBar);
        buttonSaveImage = findViewById(R.id.buttonSaveImage);
        progress_bar = findViewById(R.id.progress_bar);

        Intent intent = getIntent();
        final String ProductID = intent.getStringExtra("productID");
        databaseReference = FirebaseDatabase.getInstance().getReference("Products").child(ProductID);
        storageReference = FirebaseStorage.getInstance().getReference("Products").child(ProductID);

        buttonImportImage.setOnClickListener(v -> {
            getImageFromDevice();
        });

        buttonSaveImage.setOnClickListener(v -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(UpdateManagerProductActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile();
            }
        });

        buttonUpload.setOnClickListener(v -> {
            saveInfo();
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Product product = snapshot.getValue(Product.class);
                    name.setText(product.getName());
                    description.setText(product.getDescription());
                    species.setText(String.valueOf(product.getSpecies()));
                    price.setText(String.valueOf(product.getPrice()));
                    number.setText(String.valueOf(product.getNumber()));
                    if (product.getImage().equals("default")) {
                        image.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(UpdateManagerProductActivity.this).load(product.getImage()).into(image);
                    }
                    ratingBar.setRating(product.getRating());
                } catch (Exception ex) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(image);
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
                            databaseReference.child("image").setValue(downloadUri.toString());

                            Toast.makeText(UpdateManagerProductActivity.this, "successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(UpdateManagerProductActivity.this, "failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateManagerProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }

    private void saveInfo() {
        HashMap<String, Object> hashMap1 = new HashMap<>();

        hashMap1.put("description", description.getText().toString());
        hashMap1.put("name", name.getText().toString());
        hashMap1.put("price", Float.parseFloat(price.getText().toString()));
        hashMap1.put("species", species.getText().toString());
        hashMap1.put("number", Integer.parseInt(number.getText().toString()));
        hashMap1.put("rating", ratingBar.getRating());

        databaseReference.updateChildren(hashMap1);

        //final Task<Void> voidTask = databaseReference.updateChildren(Collections.unmodifiableMap(hashMap1));

        Toast.makeText(UpdateManagerProductActivity.this, "success", Toast.LENGTH_SHORT).show();
    }
}
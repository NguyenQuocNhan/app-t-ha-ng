package com.example.xyz;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xyz.Model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CreateProductActivity extends AppCompatActivity {

    private Button show, getImage, upload;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private RatingBar ratingBar;
    private EditText name, description, species, price, number;
    private StorageReference storageReference;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        show = findViewById(R.id.show);
        getImage = findViewById(R.id.getImage);
        mImageView = findViewById(R.id.image);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        ratingBar = findViewById(R.id.rating);
        mProgressBar = findViewById(R.id.progress_bar);
        upload = findViewById(R.id.upload);
        species = findViewById(R.id.species);
        price = findViewById(R.id.price);
        number = findViewById(R.id.number);

        storageReference = FirebaseStorage.getInstance().getReference("Products");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Products");

        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromDevice();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(CreateProductActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateProductActivity.this, "Show", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(AddProductActivity.this, ActivityShow.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(mImageView);
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
                    // action ProgressBar upload
                    .addOnSuccessListener(taskSnapshot -> {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> mProgressBar.setProgress(0), 100);
                    })
                    // waiting upload
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mProgressBar.setProgress((int) progress);
                    })

                    // create link image to upload -> save firebase -> use link to retrieve image after that //
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return fileReference.getDownloadUrl();
                    })

                    // when success
                    // IMPORTANT!!!
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            String ID = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(ID).setValue(new Product(
                                    ID,
                                    name.getText().toString(),
                                    Float.parseFloat(price.getText().toString()),
                                    species.getText().toString(),
                                    Integer.parseInt(number.getText().toString()),
                                    description.getText().toString().trim(),
                                    downloadUri.toString(),
                                    ratingBar.getRating()
                            ));

                            Toast.makeText(CreateProductActivity.this, "successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CreateProductActivity.this, "failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // when error
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }

}
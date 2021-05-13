package com.example.xyz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.xyz.Model.CardShop;
import com.example.xyz.Model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductActivity extends AppCompatActivity {
    private TextView nameProduct, description, price, species, number;
    private RatingBar ratingBar;
    private ImageButton imageProduct;
    private Button addCard;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        nameProduct = findViewById(R.id.nameProduct);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        species = findViewById(R.id.species);
        number = findViewById(R.id.number);
        ratingBar = findViewById(R.id.ratingBar);
        imageProduct = findViewById(R.id.imageProduct);
        addCard = findViewById(R.id.addCard);

        Intent intent = getIntent();
        final String productID = intent.getStringExtra("productID");

        databaseReference = FirebaseDatabase.getInstance().getReference("Products").child(productID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                assert product != null;
                nameProduct.setText(product.getName());
                description.setText(product.getDescription());
                price.setText(product.getPrice() + " $ ");
                species.setText("species: " + product.getSpecies());
                number.setText("number: " + product.getNumber());
                ratingBar.setRating(product.getRating());
                Glide.with(ProductActivity.this).load(product.getImage()).into(imageProduct);

                imageProduct.setOnClickListener(v -> {
                    Intent intent1 = new Intent(ProductActivity.this, SeeImageActivity.class);
                    intent1.putExtra("image", product.getImage());
                    startActivity(intent1);
                });

                addCard.setOnClickListener(v -> {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Users").child(firebaseUser.getUid()).child("Orders");
                    String ID = myRef.push().getKey();
                    myRef.child(ID).setValue(new CardShop(
                            ID,
                            product.getID(),
                            1,
                            product.getPrice()
                    ));
                    Toast.makeText(ProductActivity.this, "add success", Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
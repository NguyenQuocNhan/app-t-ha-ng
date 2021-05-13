package com.example.xyz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xyz.Adapter.AdapterProduct2;
import com.example.xyz.Adapter.ProductAdapter;
import com.example.xyz.Model.Product;
import com.example.xyz.Model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TextView username;
    private ImageView imageUser;
    private ImageButton buttonMessage, buttonShopCard;
    private SearchView searchView;

    private RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<Product> listProduct;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Objects.requireNonNull(getSupportActionBar()).setTitle("");


        username = findViewById(R.id.username);
        imageUser = findViewById(R.id.imageUser);
        buttonMessage = findViewById(R.id.buttonMessage);
        buttonShopCard = findViewById(R.id.buttonShopCard);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        listProduct = new ArrayList<>();


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                if (user.getAvatar().equals("default")) {
                    imageUser.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MainActivity.this).load(user.getAvatar()).into(imageUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Products"), Product.class)
                        .build();

        productAdapter = new ProductAdapter(options);
        recyclerView.setAdapter(productAdapter);

        buttonMessage.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MessageActivity.class)));
        buttonShopCard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CardShopActivity.class)));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        productAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        productAdapter.stopListening();
    }

    private void search(String s) {
        DatabaseReference referenceSearch = FirebaseDatabase.getInstance().getReference("Products");
        referenceSearch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listProduct.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        if (VNCharacterUtils
                                .removeAccent(product.getName().toLowerCase())
                                .contains(VNCharacterUtils.removeAccent(s.toLowerCase()))) {
                            listProduct.add(product);
                        }
                    }
                    recyclerView.setAdapter(new AdapterProduct2(listProduct));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

   // @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.updateUser:
                startActivity(new Intent(MainActivity.this, UpdateUserActivity.class));
                return true;

            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;

            case R.id.managerOrder:
                startActivity(new Intent(MainActivity.this, ManagerOrderActivity.class));
                return true;

            case R.id.managerProduct:
                startActivity(new Intent(MainActivity.this, ManagerProductActivity.class));
                return true;

            case R.id.create:
                startActivity(new Intent(MainActivity.this, CreateProductActivity.class));
                return true;

            case R.id.info:
                //Toast.makeText(MainActivity2.this, "info", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }


}
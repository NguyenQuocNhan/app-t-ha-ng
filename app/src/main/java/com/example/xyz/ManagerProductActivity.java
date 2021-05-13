package com.example.xyz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

import com.example.xyz.Adapter.ManagerProductAdapter;
import com.example.xyz.Adapter.ProductAdapter;
import com.example.xyz.Adapter.SearchProductAdapter;
import com.example.xyz.Model.Product;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManagerProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;

    DatabaseReference databaseReference;
    ManagerProductAdapter managerProductAdapter;

    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_product);

        recyclerView = findViewById(R.id.RecyclerView);
        searchView = findViewById(R.id.SearchView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        productList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Products"), Product.class)
                        .build();

        managerProductAdapter = new ManagerProductAdapter(options);
        managerProductAdapter.startListening();
        recyclerView.setAdapter(managerProductAdapter);

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

    private void search(String s) {
        DatabaseReference referenceSearch = FirebaseDatabase.getInstance().getReference("Products");
        referenceSearch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    productList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        if (VNCharacterUtils.removeAccent(product.getName().toLowerCase()).contains(VNCharacterUtils.removeAccent(s).toLowerCase())) {
                            productList.add(product);
                        }
                    }
                    recyclerView.setAdapter(new SearchProductAdapter(productList));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
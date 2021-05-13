package com.example.xyz;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xyz.Adapter.OrderAdapter;
import com.example.xyz.Adapter.OrderAdapter2;
import com.example.xyz.Model.Order;
import com.example.xyz.Model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManagerOrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_order);

        searchView = findViewById(R.id.SearchView);
        recyclerView = findViewById(R.id.RecyclerView);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        //recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(FirebaseDatabase
                                        .getInstance()
                                        .getReference("Orders"),
                                Order.class)
                        .build();

        orderAdapter = new OrderAdapter(options);
        recyclerView.setAdapter(orderAdapter);

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
        orderAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        orderAdapter.stopListening();
    }

    private void search(String s)
    {
        List<Order> orderList = new ArrayList<>();
        DatabaseReference referenceSearch = FirebaseDatabase.getInstance().getReference("Orders");
        referenceSearch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        Order order = item.getValue(Order.class);

                        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users").child(order.getUser());
                        referenceUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if (VNCharacterUtils
                                        .removeAccent(user.getUsername().toLowerCase())
                                        .contains(VNCharacterUtils.removeAccent(s.toLowerCase()))) {
                                    orderList.add(order);
                                }
                                recyclerView.setAdapter(new OrderAdapter2(orderList));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
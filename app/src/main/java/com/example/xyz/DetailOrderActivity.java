package com.example.xyz;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xyz.Adapter.DetailOrderAdapter;
import com.example.xyz.Model.ItemOrder;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class DetailOrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    DetailOrderAdapter detailOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        recyclerView = findViewById(R.id.RecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        Intent intent = getIntent();
        final String OrderID = intent.getStringExtra("OrderID");
        try {
            FirebaseRecyclerOptions<ItemOrder> options =
                    new FirebaseRecyclerOptions.Builder<ItemOrder>()
                            .setQuery(FirebaseDatabase.getInstance().getReference("Orders").child(OrderID).child("order"), ItemOrder.class)
                            .build();

            detailOrderAdapter = new DetailOrderAdapter(options);
            detailOrderAdapter.startListening();
            recyclerView.setAdapter(detailOrderAdapter);
        } catch (Exception ex) {
        }

    }
}
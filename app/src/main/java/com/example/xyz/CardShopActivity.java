package com.example.xyz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xyz.Adapter.AdapterCardShop;
import com.example.xyz.Model.CardShop;
import com.example.xyz.Model.ItemOrder;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardShopActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    private EditText address;
    private Button order, getLocation;
    private RecyclerView recyclerView;

    LocationManager locationManager;

    AdapterCardShop adapterCardShop;
    List<CardShop> list;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_shop);
        address = findViewById(R.id.address);
        order = findViewById(R.id.order);
        getLocation = findViewById(R.id.getLocation);
        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        // get location //
        Intent intent = getIntent();
        final Double latitude = intent.getDoubleExtra("latitude", 0);
        final Double longitude = intent.getDoubleExtra("longitude", 0);
        if ((latitude != null && longitude != null) || (latitude != 0.0 && longitude != 0.0)) address.setText(latitude + ", " + longitude);

        // on my location
        ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        getLocation.setOnClickListener(v -> {
            startActivity(new Intent(this, MapsActivity.class));
            finish();
        });

        // fill order to recyclerView
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseRecyclerOptions<CardShop> options =
                new FirebaseRecyclerOptions.Builder<CardShop>()
                        .setQuery(FirebaseDatabase
                                        .getInstance()
                                        .getReference("Users")
                                        .child(firebaseUser.getUid())
                                        .child("Orders"),
                                CardShop.class)
                        .build();

        adapterCardShop = new AdapterCardShop(options);
        recyclerView.setAdapter(adapterCardShop);


        // fill order to list
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(firebaseUser.getUid())
                .child("Orders");

        // get my location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }

        // when data change and click button->"order"
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                order.setOnClickListener(v -> {

                    if (snapshot.exists()) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CardShop cardShop = dataSnapshot.getValue(CardShop.class);
                            list.add(cardShop);
                        }
                    }

                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        OnGPS();
                    } else {
                        getLocation();
                    }

                    orderToShop();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void orderToShop() {
        if (list.size() != 0 && address.getText().toString().length() > 1) {

            DatabaseReference referenceOrder = FirebaseDatabase
                    .getInstance()
                    .getReference("Orders");

            String ID = referenceOrder.push().getKey();

            float totalMoney = 0;
            for (CardShop cardShop : list) {
                totalMoney += cardShop.getSumMoney();
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ID", ID);
            hashMap.put("User", firebaseUser.getUid());
            hashMap.put("address", address.getText().toString());
            hashMap.put("totalMoney", totalMoney);
            referenceOrder.child(ID).setValue(hashMap);

            int index = 0;
            for (CardShop cardShop : list) {
                referenceOrder.child(ID).child("order").child(String.valueOf(index)).setValue(new ItemOrder(
                        index,
                        cardShop.getNumber(),
                        cardShop.getProductID(),
                        cardShop.getSumMoney()
                ));
                index++;
            }

            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Orders").removeValue();
            Toast.makeText(CardShopActivity.this, "success", Toast.LENGTH_SHORT).show();
            list.clear();
        } else {
            Toast.makeText(CardShopActivity.this, "input your address", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterCardShop.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterCardShop.stopListening();
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @SuppressLint("SetTextI18n")
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                CardShopActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                CardShopActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                String latitude = String.valueOf(lat);
                String longitude = String.valueOf(longi);
                address.setText(latitude + "," + longitude);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
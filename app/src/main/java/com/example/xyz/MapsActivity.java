package com.example.xyz;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 1;

    private Button ButtonMap, ButtonSatellite, ButtonPlus, ButtonMinus, ButtonLocation;
    private SearchView searchView;

    //private float zoom = 5;
    Double latitude, longitude;
    String location;

    GoogleMap mMap;
    LocationManager locationManager;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ButtonMap = findViewById(R.id.ButtonMap);
        ButtonSatellite = findViewById(R.id.ButtonSatellite);
        ButtonPlus = findViewById(R.id.ButtonPlus);
        ButtonMinus = findViewById(R.id.ButtonMinus);
        ButtonLocation = findViewById(R.id.ButtonLocation);
        searchView = findViewById(R.id.SearchView);

        ButtonMap.setOnClickListener(v -> {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        });

        ButtonSatellite.setOnClickListener(v -> {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        });

        ButtonPlus.setOnClickListener(v -> {
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(googleMap.getCameraPosition().zoom + 1));
        });

        ButtonMinus.setOnClickListener(v -> {
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(googleMap.getCameraPosition().zoom - 1));
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            Toast.makeText(getApplicationContext(), address.getLatitude() + " " + address.getLongitude(), Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "not search", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        location = "0, 0";
        latitude = 0.0;
        longitude = 0.0;

        ButtonLocation.setOnClickListener(v -> {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            //fetchLastLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                return;
            }
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()
                            + ", " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    LatLng sydney = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(sydney).title("My location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
                }
            });
        });

        // Add a marker in Sydney and move the camera
        //Intent intent = getIntent();
        //final String location = intent.getStringExtra("location");
        //final double latitude = intent.getDoubleExtra("latitude", 0);
        //final double longitude = intent.getDoubleExtra("longitude", 0);

        /*
        LatLng TTTH_KHTN = new LatLng(10.763181, 106.675664);
        MarkerOptions option=new MarkerOptions();
        option.position(TTTH_KHTN);
        option.title("Trung tâm tin học ĐH KHTN").snippet("This is cool");
        option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        option.alpha(0.8f);
        option.rotation(90);
        Marker maker = googleMap.addMarker(option);
        maker.showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TTTH_KHTN, 15));
         */

        googleMap.setOnMapLongClickListener(latLng -> {
            // Create the Marker
            MarkerOptions markerOptions = new MarkerOptions();

            // Setup the Maker
            markerOptions.position(latLng);

            // Setup to touch Maker then show coordinates
            markerOptions.title(latLng.latitude + " : " + latLng.longitude);

            // Clear previously click position.
            googleMap.clear();

            float zoom = (googleMap.getCameraPosition().zoom > 13) ? googleMap.getCameraPosition().zoom : 13;
            // Zoom the Marker
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

            // Add Marker on Map
            googleMap.addMarker(markerOptions);
        });
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                latitude = locationGPS.getLatitude();
                longitude = locationGPS.getLongitude();
                location = latitude + ", " + longitude;
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
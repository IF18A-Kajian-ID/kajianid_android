package com.kajianid.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kajianid.android.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsResultActivity extends AppCompatActivity {
    SharedPreferences sp;
    GoogleMap mMap;
    Geocoder geocoder;
    String full_address, address, city, state, country, postalCode;
    TextView tvAlamat, tvKodePos, tvLintang, tvBujur;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps_result, menu);
        return true;
    }

    //buat di ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_result);

        // Bundle untuk passing data
        final Bundle bundle = getIntent().getExtras();

        // Inisialisasi Variabel
        tvAlamat = findViewById(R.id.tvAlamat);
        tvKodePos = findViewById(R.id.tvKodePos);
        tvLintang = findViewById(R.id.tvLintang);
        tvBujur = findViewById(R.id.tvBujur);

        // Inisialisasi SharedPreferences
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Geocoder untuk mengambil data lokasi pada Google Maps
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        // fungsi Toolbar dan ActionBar
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // ini untuk bagian Google Maps API
        SupportMapFragment smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert smf != null;
        smf.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                try {
                    mMap = googleMap;

                    // Konversi
                    assert bundle != null;
                    String[] ss = String.valueOf(bundle.getString("defaultLocation")).split(",");
                    float[] LatLong = {Float.parseFloat(ss[0]), Float.parseFloat(ss[1])};
                    //mMap.setMapType(mMap.MAP_TYPE_HYBRID); (4)
                    //mMap.setMapType(mMap.MAP_TYPE_NORMAL); (1)
                    //mMap.setMapType(mMap.MAP_TYPE_TERRAIN); (3)

                    // Add a marker in location and move the camera
                    LatLng location = new LatLng(LatLong[0], LatLong[1]);
                    List<Address> addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1);
                    address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    country = addresses.get(0).getCountryName();
                    postalCode = addresses.get(0).getPostalCode();
                    //create your custom title
                    full_address = address +", "+city+", "+state+", "+country;

                    // Nulis di TextView
                    tvAlamat.setText(full_address);
                    tvKodePos.setText(postalCode);
                    tvLintang.setText(String.valueOf(location.latitude));
                    tvBujur.setText(String.valueOf(location.longitude));

                    mMap.addMarker(new MarkerOptions().position(location).title("Lokasi saat ini").draggable(true)).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17), 2000, null);
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            marker.setTitle("Tarik dan jatuhkan penanda ini untuk mengganti tujuan Anda");
                            marker.showInfoWindow();
                            return false;
                        }
                    });
                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {
                            marker.setTitle("Tarik penanda ini untuk mengganti tujuan Anda");
                            marker.showInfoWindow();
                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {
                            marker.setTitle("Jatuhkan penanda ini ke tujuan Anda");
                            marker.showInfoWindow();
                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            try {
                                LatLng pos = marker.getPosition();
                                List<Address> addresses = geocoder.getFromLocation(pos.latitude, pos.longitude, 1);
                                address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                                city = addresses.get(0).getLocality();
                                state = addresses.get(0).getAdminArea();
                                country = addresses.get(0).getCountryName();
                                postalCode = addresses.get(0).getPostalCode();
                                //create your custom title
                                full_address = address +", "+city+", "+state+", "+country;

                                // Nulis di TextView
                                tvAlamat.setText(full_address);
                                tvKodePos.setText(postalCode);
                                tvLintang.setText(String.valueOf(pos.latitude));
                                tvBujur.setText(String.valueOf(pos.longitude));

                                marker.setTitle("Lokasi baru");
                                marker.showInfoWindow();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // Ganti tampilan Google Maps sesuai SharedPreferences, defaultnya ke RoadMap (1)
                    mMap.setMapType(Integer.parseInt(sp.getString("map_type", "1")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void accept_changes(MenuItem item) {
        Intent data = new Intent();
        String passing = tvLintang.getText().toString() + "," + tvBujur.getText().toString();
        data.putExtra("defaultLocation", passing);
        data.putExtra("address", tvAlamat.getText().toString());
        data.setData(Uri.parse(passing));
        setResult(RESULT_OK, data);
        finish();
    }
}
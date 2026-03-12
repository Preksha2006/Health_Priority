package com.example.healthpriority;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize Places API
//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(), "AIzaSyBh5kLdFBOduHyUY_1rZXE6JH_5QIdwvu4");
//        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                    fetchHospitalsFromOSM(currentLatLng);
                } else {
                    Log.e("MapActivity", "Location is null");
                }
            });

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void fetchHospitalsFromOSM(LatLng location) {
        double lat = location.latitude;
        double lon = location.longitude;
        int radius = 1000;

        String query = "[out:json];" +
                "(" +
                "node[\"amenity\"=\"hospital\"](around:" + radius + "," + lat + "," + lon + ");" +
                "way[\"amenity\"=\"hospital\"](around:" + radius + "," + lat + "," + lon + ");" +
                "relation[\"amenity\"=\"hospital\"](around:" + radius + "," + lat + "," + lon + ");" +
                ");out center;";

        String overpassUrl = "https://overpass-api.de/api/interpreter";
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create("data=" + query,
                MediaType.parse("application/x-www-form-urlencoded"));
        Request request = new Request.Builder().url(overpassUrl).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OSM", "Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                if (response.isSuccessful() && responseData != null) {
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray elements = json.getJSONArray("elements");

                        runOnUiThread(() -> {
                            IconGenerator iconGenerator = new IconGenerator(MapActivity.this);

                            for (int i = 0; i < elements.length(); i++) {
                                try {
                                    JSONObject item = elements.getJSONObject(i);

                                    double itemLat = item.has("lat") ?
                                            item.getDouble("lat") :
                                            item.getJSONObject("center").getDouble("lat");

                                    double itemLon = item.has("lon") ?
                                            item.getDouble("lon") :
                                            item.getJSONObject("center").getDouble("lon");

                                    String name = item.optJSONObject("tags") != null ?
                                            item.getJSONObject("tags").optString("name", "Hospital " + (i + 1)) :
                                            "Hospital " + (i + 1);

                                    LatLng hospitalLocation = new LatLng(itemLat, itemLon);
                                    Bitmap iconBitmap = iconGenerator.makeIcon(name);

                                    Marker marker = map.addMarker(new MarkerOptions()
                                            .position(hospitalLocation)
                                            .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)));

                                    if (marker != null) {
                                        marker.setTag(name);
                                    }

                                    map.setOnMarkerClickListener(clickedMarker -> {
                                        LatLng pos = clickedMarker.getPosition();
                                        String hospitalName = (String) clickedMarker.getTag();

                                        Uri gmmIntentUri = Uri.parse("geo:" + pos.latitude + "," + pos.longitude +
                                                "?q=" + Uri.encode(hospitalName));
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");

                                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                            startActivity(mapIntent);
                                        } else {
                                            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" +
                                                    pos.latitude + "," + pos.longitude + "(" + Uri.encode(hospitalName) + ")");
                                            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                                            startActivity(webIntent);
                                        }

                                        return true;
                                    });

                                } catch (Exception e) {
                                    Log.e("OSM", "Error parsing item: " + e.getMessage());
                                }
                            }
                        });

                    } catch (Exception e) {
                        Log.e("OSM", "Error: " + e.getMessage());
                    }
                } else {
                    Log.e("OSM", "Bad response: " + responseData);
                }
            }
        });
    }
}

package com.example.countryguesser;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.countryguesser.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.material.tabs.TabLayout;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
     boolean classic = true;
     boolean discover = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.textView.setText("Gamemode Classic\n Name all the countries of the world in less than 10 mins");


        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(Objects.equals(binding.tabs.getTabAt(0), tab)){
                    classic = true;
                    discover = false;
                    binding.textView.setText("Gamemode Classic\n Name all the countries of the world in less than 10 mins");
                }
                if(Objects.equals(binding.tabs.getTabAt(1), tab)){
                    classic = false;
                    discover = true;
                    binding.textView.setText("Gamemode Discover\n Select the correct country on a blank map with limited lives");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style));

        try {
            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.countries, getApplicationContext());

            layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                @Override
                public void onFeatureClick(final com.google.maps.android.data.Feature feature) {
                    for (Object s : feature.getProperties()) {
                        Log.d("getProperties", "getProperties = " + s.toString());
                    }
                }
            });
            HashMap<String, String> countries = new HashMap<String, String>();

            System.out.println(countries);

            final GeoJsonPolygonStyle style = layer.getDefaultPolygonStyle();
            style.setStrokeColor(Color.BLACK);
            style.setFillColor(Color.WHITE);

            for (Feature s : layer.getFeatures()) {
                if(s.getProperty("type").equals("Sovereign country")) {
                    countries.put(s.getProperty("sov_a3"),s.getProperty("sovereignt"));
                }
            }
            System.out.println(countries);

            layer.addLayerToMap();

            binding.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(classic){
                        binding.play.setVisibility(View.GONE);
                        binding.textView.setVisibility(View.GONE);
                        binding.tabs.setVisibility(View.GONE);

                        binding.timer.setVisibility(View.VISIBLE);
                        binding.messageBar.setVisibility(View.VISIBLE);

                        new CountDownTimer(60000*10, 1000) {
                            int tick = 0;
                            int mins = 10;
                            @SuppressLint("SetTextI18n")
                            public void onTick(long millisUntilFinished) {
                                tick++;
                                int seconds = 60 - tick;
                                String newSeconds = Integer.toString(seconds);
                                if(tick == 51 || tick == 52 || tick == 53 || tick == 54 || tick == 55 || tick == 56 || tick == 57 || tick == 58 || tick == 59)
                                    newSeconds = "0" + seconds;
                                binding.timer.setText(mins-1+":" + newSeconds);
                                if(tick == 60) {
                                    tick = 0;
                                    mins--;
                                }
                            }

                            public void onFinish() {
                                binding.timer.setText("Finished!");
                            }
                        }.start();

                        binding.send.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                Log.i("s","IFGIUf");
                                if(countries.containsValue(binding.countryInput.getText().toString()) ||
                                countries.containsKey(binding.countryInput.getText().toString()))
                                {
                                    for (Feature s : layer.getFeatures()) {
                                        if(s.getProperty("type").equals("Sovereign country")) {
                                            if(s.getProperty("sovereignt").toLowerCase().equals(binding.countryInput.getText().toString().toLowerCase())){
                                                Log.i("s",s.getGeometry().getGeometryObject().toString());
                                                try {
                                                    String code = s.getProperty("sov_a3").toLowerCase();
                                                    String c = "R.raw."+code;
                                                    Log.i("c", c);
                                                    //int id = Integer.parseInt(c);
                                                    //Log.i("id", String.valueOf(id));
                                                    GeoJsonLayer temp = new GeoJsonLayer(mMap,R.raw.ukr, getApplicationContext());
                                                    GeoJsonPolygonStyle style = temp.getDefaultPolygonStyle();
                                                    style.setStrokeColor(Color.BLACK);
                                                    style.setFillColor(Color.GREEN);

                                                    temp.addLayerToMap();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        });

                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
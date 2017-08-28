package com.olegsagenadatrytwo.w4_w_googleplacesapi.view.mapsactivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.olegsagenadatrytwo.w4_w_googleplacesapi.model.MyNearLocations;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by omcna on 8/25/2017.
 */

class MapsActivityPresenter implements MapsActivityContract.Presenter, OnMapReadyCallback{

    private static final String TAG = "MapsActivityPresenter";

    private MapsActivityContract.View view;
    private Context context;

    private static final String KEY = "AIzaSyAEns11Xxw1w9YKlSv0RR08aON71tSEaPs";
    private static final String PLACES_SEARCH_URL = "maps.googleapis.com";
    private GoogleMap mMap;
    private FusedLocationProviderClient fuseLocationProviderClient;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double lat;
    private double lon;
    private SupportMapFragment mapFragment;

    private MyNearLocations myNearLocations;

    @Override
    public void attachView(MapsActivityContract.View view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void showMap(SupportMapFragment mapFragmentIn) {

        Log.d(TAG, "showMap: ");

        fuseLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fuseLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        LatLng lastLocation = new LatLng(lat , lon);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(lastLocation).title("You are here!"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                        getNearPlaces("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to get Last Location", Toast.LENGTH_SHORT).show();
                    }
                });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = mapFragmentIn;
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                getNearPlaces("");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(TAG, "onProviderEnabled: ");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "onProviderDisabled: ");

            }
        };

        view.mapShowed(true);
    }

    public void getNearPlaces(String category){
        if(category.equals("")){
            category = "restaurant";
        }
        Log.d(TAG, "getNearPlaces: " + lat +" "+  lon);
        //make request to get the l
        final OkHttpClient okHttpClient;
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=AIzaSyAEns11Xxw1w9YKlSv0RR08aON71tSEaPs
        final Request request;
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(PLACES_SEARCH_URL)
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("place")
                .addPathSegment("nearbysearch")
                .addPathSegment("json")
                .addQueryParameter("location", lat + "," + lon)
                .addQueryParameter("radius", "10000")
                .addQueryParameter("type", category)
                .addQueryParameter("key",KEY)
                .build();

        okHttpClient = new OkHttpClient();
        request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
                Toast.makeText(context, "Failed to make connection", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.d(TAG, "onResponse: " + response.body().string());
                Gson gson = new Gson();
                myNearLocations = gson.fromJson(response.body().string(), MyNearLocations.class);
                view.nearLocationsReceived(myNearLocations);
                placeMarkersOnMap();
            }
        });
    }

    private void placeMarkersOnMap() {
        view.placeMarkers(myNearLocations, mMap);
    }
}

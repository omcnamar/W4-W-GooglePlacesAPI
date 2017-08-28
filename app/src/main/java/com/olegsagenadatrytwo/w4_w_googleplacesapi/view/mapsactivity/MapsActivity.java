package com.olegsagenadatrytwo.w4_w_googleplacesapi.view.mapsactivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.olegsagenadatrytwo.w4_w_googleplacesapi.R;
import com.olegsagenadatrytwo.w4_w_googleplacesapi.model.MyNearLocations;

public class MapsActivity extends AppCompatActivity implements MapsActivityContract.View {

    //all the variables that will be used through out the program
    private static final String TAG = "MapsActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private LocationManager locationManager;

    private MapsActivityPresenter presenter;
    private RecyclerView rvRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private AdapterNearByPlaces adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("Near Places");
        rvRecyclerView = (RecyclerView) findViewById(R.id.rvPlaces);
        checkGPSThenPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_restaurants:
                presenter.getNearPlaces("restaurant");
                break;
            case R.id.action_atm:
                presenter.getNearPlaces("atm");
                break;
            case R.id.action_hospital:
                presenter.getNearPlaces("hospital");
                break;
            case R.id.action_stores:
                presenter.getNearPlaces("stores");
                break;

        }
        return true;
    }


                //this method will check if GPS is enabled and if it is then will ask for permission
    //if it is not then ask the user to turn the GPS on
    private void checkGPSThenPermission() {
        Log.d(TAG, "checkGPSThenPermission: ");

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(gps_enabled && network_enabled) {
            askPermission();
        }
        else{
            askUserToEnableGPS();
        }

    }

    //this method will ask the user to enable the GPS
    private void askUserToEnableGPS() {
        Log.d(TAG, "askUserToEnableGPS: ");
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Please Enable GPS in settings!!!")
                .setMessage("Click Yes to go to settings" + "\n" + "No to quit.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                })
                .show();
    }

    //this method will ask the user for permission
    private void askPermission() {
        Log.d(TAG, "askPermission: ");

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "need permission", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Permission Needed!!!")
                        .setMessage("To use this application you must allow location. Click Yes to allow No to quit.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                initPresenter();
                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.exit(0);
                            }
                        })
                        .show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            initPresenter();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initPresenter();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.\
                    System.exit(0);
                }
                return;
            }
        }
    }

    private void initPresenter() {
        presenter = new MapsActivityPresenter();
        presenter.attachView(this);
        presenter.setContext(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        presenter.showMap(mapFragment);
    }

    @Override
    public void showError(String a) {
        Log.d(TAG, "showError: ");
    }

    @Override
    public void mapShowed(boolean isSaved) {
        Log.d(TAG, "mapShowed: ");
    }

    //this method will populate the recycler view. This method is called by the presenter
    @Override
    public void nearLocationsReceived(final MyNearLocations myNearLocations) {
        //presenter.updateRecyclerViewWithNearByPlaces(myNearLocations, rvRecyclerView);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layoutManager = new LinearLayoutManager(getApplication());
                itemAnimator = new DefaultItemAnimator();
                rvRecyclerView.setLayoutManager(layoutManager);
                rvRecyclerView.setItemAnimator(itemAnimator);
                adapter = new AdapterNearByPlaces(myNearLocations.getResults(), getApplicationContext());
                rvRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void placeMarkers(final MyNearLocations myNearLocations, final GoogleMap mMap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.clear();
                for(int i = 0; i < myNearLocations.getResults().size(); i++){
                    LatLng place = new LatLng(myNearLocations.getResults().get(i).getGeometry().getLocation().getLat(),
                            myNearLocations.getResults().get(i).getGeometry().getLocation().getLng());
                    mMap.addMarker(new MarkerOptions().position(place).title(myNearLocations.getResults().get(i).getName()));
                }
            }
        });
    }
}

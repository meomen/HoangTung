package firebase.gopool.Running;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import firebase.gopool.Common.Common;
import firebase.gopool.Home.HomeActivity;
import firebase.gopool.MapDirectionHelper.FetchURL;
import firebase.gopool.MapDirectionHelper.TaskLoadedCallback;
import firebase.gopool.Model.LocationData;
import firebase.gopool.Model.LocationDriverRespone;
import firebase.gopool.Model.LocationFind;
import firebase.gopool.Model.TripData;
import firebase.gopool.Model.UpdateLocationRespone;
import firebase.gopool.R;
import firebase.gopool.Remote.BackendClient;
import firebase.gopool.Remote.BackendService;
import firebase.gopool.Utils.LatLngInterpolator;
import firebase.gopool.Utils.MapUtils;
import firebase.gopool.Utils.MarkerAnimation;
import firebase.gopool.dialogs.StopTripDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements TaskLoadedCallback,OnMapReadyCallback {

    private GoogleMap mMap;
    private ScheduledExecutorService mExecutor;
    private BackendService mBackendService;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FloatingActionButton mCurrentLocationBtn;
    private FloatingActionButton mCarLocationBtn;
    private Button btn_stop_trip, btn_pick_up,btn_arrive;

    private LatLng mCarLocation,preCarLocation;
    private String userIdDriver;
    private Marker mCarMarker;
    private LatLng currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mCurrentLocationBtn = (FloatingActionButton) findViewById(R.id.myLocationButton);
        mCarLocationBtn = (FloatingActionButton) findViewById(R.id.carLocation);
        btn_stop_trip = (Button) findViewById(R.id.btn_stop_trip);
        btn_pick_up = (Button) findViewById(R.id.btn_pickup);
        btn_arrive = (Button) findViewById(R.id.btn_arrive);


        userIdDriver = getIntent().getStringExtra("userIdDriver");

        updateCarLocation();

        mCurrentLocationBtn.setOnClickListener(v -> {
            LatLng point = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
            moveCameraNoMarker(point,
                    17f,
                    "My location");
        });

        mCarLocationBtn.setOnClickListener(view -> {
            if(mCarLocation != null) {
                moveCameraNoMarker(mCarLocation,17f,"");
            }
        });

        btn_pick_up.setOnClickListener(view -> {
            btn_pick_up.setVisibility(View.GONE);
            btn_arrive.setVisibility(View.VISIBLE);
            mCurrentLocationBtn.setVisibility(View.GONE);
            mMap.setMyLocationEnabled(false);
        });

        btn_arrive.setOnClickListener(view -> {
            Toast.makeText(MapsActivity.this,"Your trip is completed",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MapsActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        btn_stop_trip.setOnClickListener(view -> {
            StopTripDialog dialog = new StopTripDialog(MapsActivity.this, true,userIdDriver);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        drawTrip();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        getDeviceLocationAndAddMarker();
    }

    private void updateCarLocation() {
        mBackendService = BackendClient.getBackendService();
        Runnable helloRunnable = new Runnable() {
            public void run() {
                mBackendService.getLocationDriver(userIdDriver)
                        .enqueue(new Callback<LocationDriverRespone>() {
                            @Override
                            public void onResponse(Call<LocationDriverRespone> call, Response<LocationDriverRespone> response) {
                                LocationDriverRespone respone = response.body();
                                LocationData carlocation = respone.getmLocationDriver().get(0);
                                if(mCarLocation != null) {
                                    preCarLocation = mCarLocation;
                                }
                                mCarLocation = new LatLng(carlocation.mLatitude,carlocation.mLongitude);

                                if (mCarMarker == null) {
                                    mCarMarker = MapUtils.addCarMarkerAndGet(MapsActivity.this, mCarLocation, mMap);
                                    mCarMarker.setAnchor(0.5f, 0.5f);
                                } else {
                                    if (!preCarLocation.equals(mCarLocation)) {
                                        mCarMarker.setRotation(MapUtils.getBearing(preCarLocation, mCarLocation));
                                    }
                                    MarkerAnimation.animateMarkerToGB(mCarMarker, mCarLocation, new LatLngInterpolator.Spherical());
                                }

                            }

                            @Override
                            public void onFailure(Call<LocationDriverRespone> call, Throwable t) {
                                Log.e("Get location error",t.getMessage());
                            }
                        });

            }
        };

        mExecutor = Executors.newScheduledThreadPool(1);
        mExecutor.scheduleAtFixedRate(helloRunnable, 0, 5, TimeUnit.SECONDS);
    }
    private void drawTrip () {
        mBackendService.getTrip(Common.userID)
                .enqueue(new Callback<List<TripData>>() {
                    @Override
                    public void onResponse(Call<List<TripData>> call, Response<List<TripData>> response) {
                        if(response.body() != null) {
                            TripData tripData = response.body().get(0);
                            LatLng startPointCustomer = new LatLng(tripData.getmLatitudeStart(),tripData.getmLongitudeStart());
                            LatLng endPointCustomer = new LatLng(tripData.getmLatitudeEnd(),tripData.getmLongitudeEnd());
                            new FetchURL(MapsActivity.this, mMap).execute(getUrl(startPointCustomer, endPointCustomer, "driving"), "driving");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TripData>> call, Throwable t) {
                        Log.e("Get trip error",t.getMessage());
                    }
                });
    }

    private void stopExecutor() {
        if (mExecutor != null) {
            mExecutor.shutdown();
        }
    }

    private void moveCameraNoMarker(LatLng latLng, float zoom, String title) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    private void getDeviceLocationAndAddMarker() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d("TAG", "onComplete: getting found location!");
                        Location result = (Location) task.getResult();
                        currentLocation = new LatLng(result.getLatitude(),result.getLongitude());

                        moveCameraNoMarker(new LatLng(result.getLatitude(), result.getLongitude()),
                                17f,
                                "My location");

                    } else {
                        Log.d("TAG", "onComplete: current location is null");
                        Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("MapActivity", "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.customerOnMap = true;

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopExecutor();
    }

    @Override
    public void onTaskDone(Object... values) {

    }
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String google_api = getResources().getString(R.string.google_maps_api_key);
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + google_api;
        return url;
    }
}
package firebase.gopool.Running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import firebase.gopool.MapDirectionHelper.FetchURL;
import firebase.gopool.MapDirectionHelper.TaskLoadedCallback;
import firebase.gopool.R;
import firebase.gopool.Utils.MapUtils;
import firebase.gopool.dialogs.StopTripDialog;
import firebase.gopool.models.OfferRide;

public class MapsActivity extends AppCompatActivity implements TaskLoadedCallback,OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng currentLocation,pickupLocation,toLocation;
    private Marker carMarker,pickupMaker,passengerMarker;
    private static final float DEFAULT_ZOOM = 17f;
    private String rideId;
    private Polyline pickupPoly,toPoly;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private Button mPickupBtn, mGetOffBtn;
    private OfferRide offerRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        init();

    }

    private void init() {

        mPickupBtn = (Button) findViewById(R.id.btn_pickup);
        mGetOffBtn = (Button) findViewById(R.id.btn_getoff);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("TRIP");

        setOnClick();

        rideId = getIntent().getStringExtra("RideId");
//        mRef.child("availableRide").child(rideId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
//                offerRide = dataSnapshot.getValue(OfferRide.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//
//
//        pickupLocation = new LatLng(offerRide.getPickuplatitude(), offerRide.getPickuplongitude());
//        toLocation = new LatLng(offerRide.getToLat(), offerRide.getToLng());


        pickupLocation = new LatLng(21.03766867617107, 105.77380903065203);
        toLocation = new LatLng(21.02786189999999, 105.767096);

    }


    private void drawRoutePickUp() {

        pickupMaker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pick up location"));
        FetchURL fetchURL = new FetchURL(MapsActivity.this,mMap);
        fetchURL.execute(getUrl(currentLocation,pickupLocation,"driving"), "driving");
        pickupPoly = fetchURL.getPolyline();

    }


    private void setOnClick() {
        mPickupBtn.setOnClickListener(view -> {
            mPickupBtn.setVisibility(View.GONE);
            mGetOffBtn.setVisibility(View.VISIBLE);
        });

        mGetOffBtn.setOnClickListener(view -> {

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                StopTripDialog dialog = new StopTripDialog(MapsActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        getDeviceLocationAndAddMarker();
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
                            mMap.clear();


                            carMarker = MapUtils.addCarMarkerAndGet(MapsActivity.this, currentLocation, mMap);
                            carMarker.setAnchor(0.5f,0.5f);

                            moveCamera(new LatLng(result.getLatitude(), result.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My location");
                            drawRoutePickUp();

                        } else {
                            Log.d("TAG", "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        hideKeyboard(MapsActivity.this);
    }
    /**
     * Hides phone keyboard if clicked
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    @Override
    public void onTaskDone(Object... values) {

    }
}
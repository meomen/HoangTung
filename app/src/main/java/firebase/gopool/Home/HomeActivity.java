package firebase.gopool.Home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;

import com.andremion.counterfab.CounterFab;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.libraries.places.compat.PlaceBufferResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import com.google.android.libraries.places.compat.AutocompletePrediction;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.PlaceBuffer;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.Places;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import firebase.gopool.Chat.ChatDetailActivity;
import firebase.gopool.Common.Common;
import firebase.gopool.Common.CommonAgr;
import firebase.gopool.Login.LoginActivity;
import firebase.gopool.Map.CustomInfoWindowAdapter;
import firebase.gopool.Map.PlaceAutocompleteAdapter;
import firebase.gopool.Map.PlaceInfo;
import firebase.gopool.MapDirectionHelper.FetchURL;
import firebase.gopool.MapDirectionHelper.TaskLoadedCallback;
import firebase.gopool.Model.CheckRouteRequest;
import firebase.gopool.Model.CheckRouteRespone;
import firebase.gopool.Model.CreateTripRequest;
import firebase.gopool.Model.CreateTripRespone;
import firebase.gopool.Model.GetTripRespone;
import firebase.gopool.Model.LocationData;
import firebase.gopool.Model.LocationFind;
import firebase.gopool.Model.TripData;
import firebase.gopool.Model.UpdateLocationRespone;
import firebase.gopool.Model.UpdateSatusRequest;
import firebase.gopool.Model.UpdateStatusRespone;
import firebase.gopool.R;
import firebase.gopool.Remote.BackendClient;
import firebase.gopool.Remote.BackendService;
import firebase.gopool.Utils.BottomNavigationViewHelper;
import firebase.gopool.Utils.LatLngInterpolator;
import firebase.gopool.Utils.MapUtils;
import firebase.gopool.Utils.MarkerAnimation;
import firebase.gopool.Utils.UniversalImageLoader;
import firebase.gopool.dialogs.StopTripDialog;
import firebase.gopool.dialogs.WelcomeDialog;
import firebase.gopool.models.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TaskLoadedCallback {
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUMBER = 0;

    private Context mContext = HomeActivity.this;

    //Google map permissions
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private Boolean mLocationPermissionsGranted = false;
    private Place To, From;
    private PlaceInfo placeInfoFrom, placeInfoTo;


    //Google map variables
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private PlaceInfo mPlace;
    private Marker mMarker1,mMarker2, mCarMarker, mPartnerMarker, mCustomerMarker;
    private double currentLatitude, currentLongtitude;
    private Polyline currentPolyline;
    private ArrayList<Polyline> polylines;
    private MarkerOptions place1, place2;
    private LatLng currentLocation, preLocation, startPointCustomer, endPointCustomer;
    private Circle circle;
    private String directionsRequestUrl;
    private String userID, partnerID;

    //Widgets
    private AutoCompleteTextView destinationTextview, locationTextView;
    private Button mSearchBtn, mDirectionsBtn, mSwitchTextBtn;
    private RadioButton findButton, offerButton;
    private RadioGroup mRideSelectionRadioGroup;
    private BottomNavigationView bottomNavigationView;
    private ImageView mLocationBtn;
    private Button mStopSearchBtn, mStartTrip, mStopTrip, mPickUpBtn, mDropCustomerBtn;
    private CounterFab mCounterCar;
    private FloatingActionButton mCurrentLocation,mChatBtn;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;

    private Boolean carOwner, firstTimeFlag = true, isAvailable = false;
    private String typeofaction, role;
    private TripData mTripData;

    private ScheduledExecutorService mExecutor;
    private BackendService mBackendService;
    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            preLocation = currentLocation;
            currentLocation = new LatLng(locationResult.getLastLocation().getLatitude(),
                    locationResult.getLastLocation().getLongitude());
            showMarker(currentLocation);
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        if (mAuth.getCurrentUser() != null) {
            //Gets userID of current user signed in
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Common.userID = userID;

            //Disables offer button when the user is logged in and they have no car
            findButton = (RadioButton) findViewById(R.id.findButton);
            offerButton = (RadioButton) findViewById(R.id.offerButton);

            getUserInformation(userID);

            //Creates token with that new user ID
            updateFirebaseToken();

            checkNotifications();

            //Subscribes to a topic with that user ID so only that user can see messages with that user ID
            FirebaseMessaging.getInstance().subscribeToTopic(userID);

        }

        typeofaction = "to";
        //Intitate widgets
        destinationTextview = (AutoCompleteTextView) findViewById(R.id.destinationTextview);
        locationTextView = (AutoCompleteTextView) findViewById(R.id.locationTextview);

        locationTextView.setOnFocusChangeListener((view, motionEvent) -> {
            typeofaction = "from";
        });
        destinationTextview.setOnFocusChangeListener((view, motionEvent) -> {
            typeofaction = "to";

        });

        mSearchBtn = (Button) findViewById(R.id.searchBtn);
        mStopSearchBtn = (Button) findViewById(R.id.btn_stopsearch);
        mStartTrip = (Button) findViewById(R.id.btn_start_trip);
        mStopTrip = (Button) findViewById(R.id.btn_stop_trip);
        mSwitchTextBtn = (Button) findViewById(R.id.switchTextBtn);
        mDirectionsBtn = (Button) findViewById(R.id.directionsBtn);
        mPickUpBtn = (Button) findViewById(R.id.btn_pickup_customer);
        mDropCustomerBtn = (Button) findViewById(R.id.btn_drop_customer);
        mRideSelectionRadioGroup = (RadioGroup) findViewById(R.id.toggle);
        mLocationBtn = (ImageView) findViewById(R.id.locationImage);
        mCounterCar = (CounterFab) findViewById(R.id.fab_counter_car);
        mCurrentLocation = (FloatingActionButton) findViewById(R.id.myLocationButton);
        mBackendService = BackendClient.getBackendService();
        mChatBtn = (FloatingActionButton) findViewById(R.id.btn_chat);
        userLocationFAB();


        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocationAndAddMarker();
            }
        });

        mSwitchTextBtn.setOnClickListener(v -> {
            if (destinationTextview.getText().toString().trim().length() > 0 && locationTextView.getText().toString().trim().length() > 0) {
                String tempDestination1 = destinationTextview.getText().toString();
                String tempDestination12 = locationTextView.getText().toString();

                locationTextView.setText(tempDestination1);
                destinationTextview.setText(tempDestination12);

                locationTextView.dismissDropDown();
                destinationTextview.dismissDropDown();

            } else {
                Toast.makeText(mContext, "Please enter location and destination", Toast.LENGTH_SHORT).show();
            }
        });

        mSearchBtn.setOnClickListener(v -> {
            int whichIndex = mRideSelectionRadioGroup.getCheckedRadioButtonId();
            if (whichIndex == R.id.offerButton && destinationTextview.getText().toString().trim().length() > 0 && locationTextView.getText().toString().trim().length() > 0) {
                mStartTrip.setVisibility(View.VISIBLE);
                mCurrentLocation.setVisibility(View.VISIBLE);
                mStopTrip.setVisibility(View.GONE);


            } else if (whichIndex == R.id.findButton && destinationTextview.getText().toString().trim().length() > 0 && locationTextView.getText().toString().trim().length() > 0) {
                offerButton.setEnabled(false);
                offerButton.setAlpha(.5f);
                offerButton.setClickable(false);

                role = "Customer";
                if (circle != null) circle.remove();
                circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(currentLatitude, currentLongtitude))
                        .radius(200)
                        .strokeWidth(0f)
                        .fillColor(0x550000FF));
                moveCameraNoMarker(new LatLng(currentLatitude, currentLongtitude), 17f, "Range Search");
                updateCurrentLocation("Customer");
                createTrip("Customer");
                mStopSearchBtn.setVisibility(View.VISIBLE);
                mCounterCar.setVisibility(View.VISIBLE);
                mCounterCar.setEnabled(false);

            } else {
                Toast.makeText(mContext, "Please enter location and destination", Toast.LENGTH_SHORT).show();
            }
        });

        mStartTrip.setOnClickListener(view -> {

            role = "Driver";
            mMap.setMyLocationEnabled(false);
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            });
            MapUtils.startCurrentLocationUpdates(this, mFusedLocationProviderClient, mLocationCallback);
            moveCameraNoMarker(currentLocation, 17f, "");

            mStartTrip.setVisibility(View.GONE);
            mStopTrip.setVisibility(View.VISIBLE);

            findButton.setEnabled(false);
            findButton.setAlpha(.5f);
            findButton.setClickable(false);

            updateCurrentLocation("Driver");
            createTrip("Driver");
        });

        mStopSearchBtn.setOnClickListener(view -> {
            mStopSearchBtn.setVisibility(View.GONE);
            mCounterCar.setVisibility(View.GONE);
            mMap.clear();
            locationTextView.setText("");
            destinationTextview.setText("");
            moveCameraNoMarker(currentLocation, DEFAULT_ZOOM, "");

            if (carOwner) {
                offerButton.setEnabled(true);
                offerButton.setAlpha(1f);
                offerButton.setClickable(true);
            }
            stopExecutor();
            stopTrip(userID);
            role = null;

        });
        mStopTrip.setOnClickListener(view -> {
            role = null;
            Common.tripDriver = null;
            Common.tripCustomer = null;
            Common.flatStop = null;
            stopExecutor();
            stopTrip(userID);
            StopTripDialog dialog = new StopTripDialog(HomeActivity.this, mFusedLocationProviderClient, mLocationCallback);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        });

        mCounterCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SearchResultsActivity.class);
                intent.putExtra("trips", mTripData);
                startActivity(intent);
            }
        });

        mRideSelectionRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            mStartTrip.setVisibility(View.GONE);
            mStopTrip.setVisibility(View.GONE);
            mCurrentLocation.setVisibility(View.GONE);
            mStopSearchBtn.setVisibility(View.GONE);
        });

        mPickUpBtn.setOnClickListener(view -> {
            if(Common.currentPoly != null) Common.currentPoly.remove();

            new FetchURL(HomeActivity.this, mMap).execute(getUrl(startPointCustomer, endPointCustomer, "driving"), "driving");
            mPickUpBtn.setVisibility(View.GONE);
            mDropCustomerBtn.setVisibility(View.VISIBLE);
            mStopTrip.setVisibility(View.GONE);
            mMarker1.remove();

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(endPointCustomer)
                    .title("destination customer")
                    .snippet(Common.tripCustomer.getmEndAddress());
            mMarker2 = mMap.addMarker(markerOptions);
        });

        mDropCustomerBtn.setOnClickListener(view -> {
            if(Common.currentPoly != null) Common.currentPoly.remove();
            new FetchURL(HomeActivity.this, mMap).execute(getUrl(endPointCustomer, place2.getPosition(), "driving"), "driving");
            mDropCustomerBtn.setVisibility(View.GONE);
            mStopTrip.setVisibility(View.VISIBLE);
            mMarker1 = mMarker2;
            mCustomerMarker.remove();
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(place2.getPosition())
                    .title("destination driver")
                    .snippet(Common.tripDriver.getmEndAddress());

            mMarker2 = mMap.addMarker(markerOptions);
            mStopTrip.setText("End Trip");
            mStopTrip.setVisibility(View.VISIBLE);
            mChatBtn.setVisibility(View.GONE);
        });

        mChatBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ChatDetailActivity.class);
            intent.putExtra(CommonAgr.ID_CURRENT,userID);
            intent.putExtra(CommonAgr.ID_PARTNER,Common.tripCustomer.getmUserId());
            intent.putExtra(CommonAgr.KEY_CHAT_USER,"Tung");
            startActivity(intent);
        });

        initImageLoader();
        setupBottomNavigationView();

        // For display the welcome dialog on first app launch
        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun) {
            //... Display the dialog message here ...
            setupDialog();
            // Save the state
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();
        }

        //If play services is true = ok, then check for permissions and setup google maps
        if (isServicesOk()) {
            getLocationPermission();
        }

    }


    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(userID)
                .setValue(token);
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

    public void setupDialog() {
        WelcomeDialog dialog = new WelcomeDialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
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


    /** --------------------------- Setting up google maps / permissions and services ---------------------------- **/

    /**
     * Check if google play services is enabled or available for mobile device
     *
     * @return
     */
    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is ok and user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but it can be resolved
            Log.d(TAG, "isServicesOK: an error occurred but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "App cannot make map requests current", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    /**
     * sets up map from the view
     */
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        init();
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: getting found location!");
                            Location currentLocation = (Location) task.getResult();
                            placeInfoTo = new PlaceInfo();
                            placeInfoTo.setName("My Location");
                            moveCameraNoMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My location");
                            currentLatitude = currentLocation.getLatitude();
                            currentLongtitude = currentLocation.getLongitude();
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(HomeActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void getDeviceLocationAndAddMarker() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: getting found location!");
                            Location result = (Location) task.getResult();
                            currentLocation = new LatLng(result.getLatitude(), result.getLongitude());
                            mMap.clear();
                            To = null;

                            moveCamera(new LatLng(result.getLatitude(), result.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My location");
//                            drawMapMarker(From, false, null);
                            currentLatitude = result.getLatitude();
                            currentLongtitude = result.getLongitude();

                            LatLng latLng = new LatLng(currentLatitude, currentLongtitude);
                            place1 = new MarkerOptions()
                                    .position(latLng)
                                    .title("My location");

                            geoDecoder(result);
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(HomeActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCameraNoMarker(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        hideKeyboard(HomeActivity.this);
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet("");


            mMarker1 = mMap.addMarker(markerOptions);
        }

        hideKeyboard(HomeActivity.this);
    }


    /**
     * create marker on the given latlng
     *
     * @param latLng    lat lng of the given place
     * @param placeInfo
     */
    private void createMarker(LatLng latLng, PlaceInfo placeInfo) {
        String snippet = "Your Location";
        if (placeInfo != null) {
            snippet = "Address: " + placeInfo.getAddress() + "\n" +
                    "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                    "Website: " + placeInfo.getWebsiteUri() + "\n" +
                    "Price Rating: " + placeInfo.getRating() + "\n";

            MarkerOptions marker = new MarkerOptions()
                    .position(latLng)
                    .title(placeInfo.getName())
                    .snippet(snippet);

//            mMarker = mMap.addMarker(marker);
        } else {

            MarkerOptions marker = new MarkerOptions()
                    .position(latLng)
                    .title(snippet);

//            mMarker = mMap.addMarker(marker);
        }

    }


    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        hideKeyboard(HomeActivity.this);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mContext));

        if (placeInfo != null) {
            try {
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";

                if (destinationTextview.hasFocus() == true) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

                    place1 = new MarkerOptions()
                            .position(latLng)
                            .title(placeInfo.getName())
                            .snippet(snippet);

                    mMarker1 = mMap.addMarker(place1);

                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                    place2 = new MarkerOptions()
                            .position(latLng)
                            .title(placeInfo.getName())
                            .snippet(snippet);

                    mMarker2 = mMap.addMarker(place2);

                    directionsRequestUrl = getUrl(place1.getPosition(), place2.getPosition(), "driving");

                    new FetchURL(HomeActivity.this, mMap).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                }

            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
        hideKeyboard(HomeActivity.this);
    }

    private void geoDecoder(Location latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.getLatitude(), latLng.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0);
        destinationTextview.setText(address);
        destinationTextview.dismissDropDown();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mGoogleApiClient = new GoogleApiClient.Builder(HomeActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        destinationTextview.setOnItemClickListener(mAuotcompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, null);

        destinationTextview.setAdapter(mPlaceAutocompleteAdapter);

        destinationTextview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    goeLocate();
                }

                return false;
            }
        });

        locationTextView.setOnItemClickListener(mAuotcompleteClickListener);

        locationTextView.setAdapter(mPlaceAutocompleteAdapter);

        locationTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    goeLocate();
                }

                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goeLocate() {
        Log.d(TAG, "goeLocate: goeLocating");

        String searchString = destinationTextview.getText().toString();

        Geocoder geocoder = new Geocoder(HomeActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "goeLocate: IOException: " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);

            Log.e(TAG, "goeLocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

     /*
        ---------------------------- google places API autocomplete suggestions -------------------------------
     */

    private AdapterView.OnItemClickListener mAuotcompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard(HomeActivity.this);

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            assert item != null;
            final String placeId = item.getPlaceId();

            Places.getGeoDataClient(HomeActivity.this)
                    .getPlaceById(placeId).addOnCompleteListener(place -> {
                getPlaceDetails(place, typeofaction);

            }).addOnFailureListener(e -> {
                Log.e(HomeActivity.TAG, "Place can not be found", e);
            });
        }
    };


    private void getPlaceDetails(Task<PlaceBufferResponse> places, String typeofaction) {
        mMap.clear();

        final Place place = places.getResult().get(0);


        try {

            mPlace = new PlaceInfo();
            mPlace.setName(place.getName().toString());
            Log.d(TAG, "onResult: name: " + place.getName());
            mPlace.setAddress(place.getAddress().toString());
            Log.d(TAG, "onResult: address: " + place.getAddress());
            // mPlace.setAttributions(place.getAttributions().toString());
            //Log.d(TAG, "onResult: attributions: " + place.getAttributions());
            mPlace.setId(place.getId());
            Log.d(TAG, "onResult: id: " + place.getId());
            mPlace.setLatLng(place.getLatLng());
            Log.d(TAG, "onResult: latLng: " + place.getLatLng());
            mPlace.setRating(place.getRating());
            Log.d(TAG, "onResult: rating: " + place.getRating());
            mPlace.setPhoneNumber(place.getPhoneNumber().toString());
            Log.d(TAG, "onResult: phoneNumber: " + place.getPhoneNumber());
            mPlace.setWebsiteUri(place.getWebsiteUri());
            Log.d(TAG, "onResult: websiteUri: " + place.getWebsiteUri());
            Log.d(TAG, "onResult: place: " + mPlace.toString());

            if (destinationTextview.isFocused()) {
                currentLocation = mPlace.getLatLng();
            }

            if (typeofaction.equals("from")) {
                From = place;
                placeInfoFrom = mPlace;
                drawMapMarker(From, true, mPlace);
            } else {
                To = place;
                placeInfoTo = mPlace;
                drawMapMarker(To, true, mPlace);
            }

        } catch (NullPointerException e) {
            Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
        }

        moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

//        places.release();

    }

    private void drawMapMarker(Place From, Boolean status, PlaceInfo placeInfoFrom) {

        if (From != null) {
            createMarker(new LatLng(this.From.getViewport().getCenter().latitude,
                    this.From.getViewport().getCenter().longitude), this.placeInfoFrom);
        }

        if (To != null) {
            createMarker(new LatLng(To.getViewport().getCenter().latitude,
                    To.getViewport().getCenter().longitude), placeInfoTo);
        } else if (currentLocation != null) {
            moveCamera(currentLocation,
                    DEFAULT_ZOOM,
                    "My location");
        }

    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try {

                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
                // mPlace.setAttributions(place.getAttributions().toString());
                //Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id: " + place.getId());
                mPlace.setLatLng(place.getLatLng());
                Log.d(TAG, "onResult: latLng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phoneNumber: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: websiteUri: " + place.getWebsiteUri());
                Log.d(TAG, "onResult: place: " + mPlace.toString());

                if (destinationTextview.isFocused()) {
                    currentLocation = mPlace.getLatLng();
                }

            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

            places.release();

        }
    };

    /*
     * Permission locations
     * */

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: get location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /***
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        //BottomNavigationViewHelper.addBadge(mContext, bottomNavigationView);


        //Change current highlighted icon
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    private void setupBadge(int reminderLength) {
        if (reminderLength > 0) {
            //Adds badge and notification number to the BottomViewNavigation
            BottomNavigationViewHelper.addBadge(mContext, bottomNavigationView, reminderLength);
        }
    }

    /**
     * Checks if there are notifications available for the current logged in user.
     */
    private void checkNotifications() {
        mRef.child("Reminder").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int reminderLength = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        reminderLength++;
                    }
                }
                //Passes the number of notifications onto the setup badge method
                setupBadge(reminderLength);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /** --------------------------- Firebase ---------------------------- **/

    /***
     *  Setup the firebase object
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // userID = currentUser.getUid();
        checkCurrentUser(currentUser);
    }

    /**
     * Cheks to see if the @param 'user' is logged in
     *
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user if logged in");

        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = mMap.addPolyline((PolylineOptions) values[1]);
        }
    }

    public void getUserInformation(String uid) {
        mRef.child("user").child(uid).child("carOwner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carOwner = dataSnapshot.getValue(Boolean.class);
                if (carOwner == false) {
                    offerButton.setEnabled(false);
                    offerButton.setAlpha(.5f);
                    offerButton.setClickable(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void userLocationFAB() {
        mCurrentLocation = (FloatingActionButton) findViewById(R.id.myLocationButton);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        });
        mCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCameraNoMarker(currentLocation,
                        17f,
                        "My location");
            }
        });
    }

    private void showMarker(LatLng currentLocation) {
        if (mCarMarker == null) {
            mCarMarker = MapUtils.addCarMarkerAndGet(this, currentLocation, mMap);
            mCarMarker.setAnchor(0.5f, 0.5f);
        } else {
            if (!preLocation.equals(currentLocation)) {
                mCarMarker.setRotation(MapUtils.getBearing(preLocation, currentLocation));
            }
            MarkerAnimation.animateMarkerToGB(mCarMarker, currentLocation, new LatLngInterpolator.Spherical());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.customerOnMap = false;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(mCarMarker != null) MapUtils.startCurrentLocationUpdates(this, mFusedLocationProviderClient, mLocationCallback);
        if (Common.tripCustomer != null && Common.tripDriver != null && Common.flatStop == null) {
            if (role != null && role.equals("Driver")) {
                drawExtraRoute();
                mPickUpBtn.setVisibility(View.VISIBLE);
                mChatBtn.setVisibility(View.VISIBLE);
            }
        }
        else if (Common.flatStop != null) {
            if(Common.flatStop.equals(Common.CUSTOMER_STOP)) {
                if(Common.currentPoly != null) Common.currentPoly.remove();
                mPickUpBtn.setVisibility(View.GONE);
                mDropCustomerBtn.setVisibility(View.GONE);
                mStopTrip.setVisibility(View.VISIBLE);
                if(mCustomerMarker != null) mCustomerMarker.remove();
                if(mMarker1 != null) mMarker1.remove();
                if(mMarker2 != null) mMarker2.remove();

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(currentLocation)
                        .title("start driver")
                        .snippet("");
                mMarker1 = mMap.addMarker(markerOptions);
                markerOptions = new MarkerOptions()
                        .position(place2.getPosition())
                        .title("destination driver")
                        .snippet("");
                mMarker2 = mMap.addMarker(markerOptions);
                new FetchURL(HomeActivity.this, mMap).execute(getUrl(currentLocation, place2.getPosition(), "driving"), "driving");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocationProviderClient != null)
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);

    }

    private void updateCurrentLocation(String role) {
        Runnable helloRunnable = new Runnable() {
            public void run() {

                LocationData body = new LocationData(userID, role, currentLocation.latitude, currentLocation.longitude);
                mBackendService.updateLocation(body)
                        .enqueue(new Callback<UpdateLocationRespone>() {
                            @Override
                            public void onResponse(Call<UpdateLocationRespone> call, Response<UpdateLocationRespone> response) {
                                UpdateLocationRespone body = response.body();
                                List<LocationFind> locationFinds = body.getmLocationFind();
                                if (locationFinds != null && !locationFinds.isEmpty() && locationFinds.get(0) != null) {
                                    if (role.equals("Customer")) {
                                        partnerID = locationFinds.get(0).mUserId;
                                        checkRoute(partnerID);
                                        if (isAvailable) {
                                            mPartnerMarker = MapUtils.drawCar(HomeActivity.this, locationFinds.get(0), mMap, mPartnerMarker);
                                            isAvailable = false;
                                            mCounterCar.setEnabled(true);
                                            mCounterCar.setCount(1);
                                        } else {
                                            mTripData = null;
                                            mCounterCar.setCount(0);
                                            mCounterCar.setEnabled(false);
                                        }
                                    }
                                } else if (mPartnerMarker != null) {
                                    mPartnerMarker.remove();
                                    mPartnerMarker = null;
                                    mCounterCar.setCount(0);
                                    mCounterCar.setEnabled(false);
                                }
                            }

                            @Override
                            public void onFailure(Call<UpdateLocationRespone> call, Throwable t) {
                                Log.e("Error upda location", t.getLocalizedMessage());
                            }
                        });

            }
        };

        mExecutor = Executors.newScheduledThreadPool(1);
        mExecutor.scheduleAtFixedRate(helloRunnable, 0, 5, TimeUnit.SECONDS);

    }

    private void stopExecutor() {
        if (mExecutor != null) {
            mExecutor.shutdown();
        }
    }

    private void createTrip(String role) {
        CreateTripRequest createTripRequest = new CreateTripRequest();
        createTripRequest.setmRole(role);
        createTripRequest.setmUserId(userID);
        createTripRequest.setmStartAddress(destinationTextview.getText().toString());
        createTripRequest.setmLatitudeStart(place1.getPosition().latitude);
        createTripRequest.setmLongitudeStart(place1.getPosition().longitude);
        createTripRequest.setmEndAddress(locationTextView.getText().toString());
        createTripRequest.setmLatitudeEnd(place2.getPosition().latitude);
        createTripRequest.setmLongitudeEnd(place2.getPosition().longitude);
        createTripRequest.setmPoly(Common.poly);
        createTripRequest.setmStatus(0);
        Date currentTime = Calendar.getInstance().getTime();
        createTripRequest.setmCreateAt(currentTime.toString());
        mBackendService.createTrip(createTripRequest).enqueue(new Callback<CreateTripRespone>() {
            @Override
            public void onResponse(Call<CreateTripRespone> call, Response<CreateTripRespone> response) {
                CreateTripRespone createTripRespone = response.body();
                if (role.equals("Customer")) {
                    Common.tripCustomer = createTripRespone.getmBody();
                } else {
                    Common.tripDriver = createTripRespone.getmBody();
                }
            }

            @Override
            public void onFailure(Call<CreateTripRespone> call, Throwable t) {
                Log.e("Create Trip Error", t.getLocalizedMessage());
            }
        });

        Common.poly = null;
    }

    private void getTrip(String partnerID) {
        mBackendService.getTrip(partnerID)
                .enqueue(new Callback<List<TripData>>() {
                    @Override
                    public void onResponse(Call<List<TripData>> call, Response<List<TripData>> response) {
                        if (!response.body().isEmpty()) {
                            mTripData = response.body().get(0);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TripData>> call, Throwable t) {
                        Log.e("check Trip Error", t.getLocalizedMessage());
                    }
                });
    }

    private void checkRoute(String partnerID) {
        getTrip(partnerID);
        if (mTripData == null) return;
        CheckRouteRequest checkRouteRequest = new CheckRouteRequest();
        checkRouteRequest.setmCode(mTripData.getmPoly());
        checkRouteRequest.setmLatitude(place2.getPosition().latitude);
        checkRouteRequest.setmLongitude(place2.getPosition().longitude);

        final boolean[] result = new boolean[1];
        result[0] = false;
        mBackendService.checkRoute(checkRouteRequest)
                .enqueue(new Callback<CheckRouteRespone>() {
                    @Override
                    public void onResponse(Call<CheckRouteRespone> call, Response<CheckRouteRespone> response) {
                        isAvailable = response.body().ismNear();
                    }

                    @Override
                    public void onFailure(Call<CheckRouteRespone> call, Throwable t) {
                        Log.e("Check Route Error", t.getLocalizedMessage());
                    }
                });
    }


    private void stopTrip(String userID) {
        UpdateSatusRequest updateSatusRequest = new UpdateSatusRequest();
        updateSatusRequest.setUserId(userID);
        mBackendService.updateStatus(updateSatusRequest)
                .enqueue(new Callback<UpdateStatusRespone>() {
                    @Override
                    public void onResponse(Call<UpdateStatusRespone> call, Response<UpdateStatusRespone> response) {

                    }

                    @Override
                    public void onFailure(Call<UpdateStatusRespone> call, Throwable t) {
                        Log.e("stopTrip Error", t.getLocalizedMessage());
                    }
                });
    }

    public void drawExtraRoute() {
        if (Common.currentPoly != null) Common.currentPoly.remove();
        startPointCustomer = new LatLng(Common.tripCustomer.getmLatitudeStart(), Common.tripCustomer.getmLongitudeStart());
        endPointCustomer = new LatLng(Common.tripCustomer.getmLatitudeEnd(), Common.tripCustomer.getmLongitudeEnd());
        mCustomerMarker = MapUtils.addCustomerMarkerAndGet(HomeActivity.this, startPointCustomer, mMap,Common.tripCustomer.getmStartAddress());
        mMarker2.remove();
        new FetchURL(HomeActivity.this, mMap).execute(getUrl(place1.getPosition(), startPointCustomer, "driving"), "driving");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopExecutor();
    }
}

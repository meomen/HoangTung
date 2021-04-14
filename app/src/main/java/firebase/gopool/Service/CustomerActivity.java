package firebase.gopool.Service;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import firebase.gopool.Common.Common;
import firebase.gopool.Home.HomeActivity;
import firebase.gopool.Model.TripData;
import firebase.gopool.R;
import firebase.gopool.Remote.BackendClient;
import firebase.gopool.Remote.BackendService;
import firebase.gopool.Remote.FCMClient;
import firebase.gopool.Remote.IFCMService;
import firebase.gopool.Utils.FirebaseMethods;
import firebase.gopool.Utils.UniversalImageLoader;
import firebase.gopool.models.DataRequest;
import firebase.gopool.models.FCMResponse;
import firebase.gopool.models.Send;
import firebase.gopool.models.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerActivity extends AppCompatActivity {
    private static final String TAG = "CustomerActivity";

    private TextView txtUsername, txtTo, txtFrom;
    private String  to, from, userIdCustomer, rideID;
    private Boolean rideAccepted;

    //Widgets
    private FloatingActionButton acceptBtn, declineBtn;
    private CircleImageView mRequestProfilePhoto;

    //Firebase
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mFirebaseMethods;

    private Context mContext = CustomerActivity.this;
    private BackendService mBackendService;
    private IFCMService mService;
    private TripData mTripData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        final CustomerActivity c = this;

        getActivityData();
        setupDialog();
        getTrip(userIdCustomer);
        mFirebaseMethods = new FirebaseMethods(mContext);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mBackendService = BackendClient.getBackendService();
        acceptBtn = (FloatingActionButton) findViewById(R.id.confirmRideBtn);
        declineBtn = (FloatingActionButton) findViewById(R.id.declineRideBtn);
        mRequestProfilePhoto = (CircleImageView)findViewById(R.id.requestProfilePhoto);
        txtTo = (TextView) findViewById(R.id.to);
        txtFrom = (TextView) findViewById(R.id.from);
        txtUsername = (TextView) findViewById(R.id.message);
//        txtUsername.setText("Hi, i'm " + username + " and would like to request a seat on your journey!");


        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRide();
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineRide();
            }
        });
//     txtAddress = (TextView) findViewById(R.id.txtAddress);
//     txtTime.setText(title);
//     txtDistance.setText(body);


//        UniversalImageLoader.setImage(profile_photo, mRequestProfilePhoto, null,"");




    }

    private void getActivityData() {
        if (getIntent() != null) {
            userIdCustomer = getIntent().getStringExtra("userIdCustomer");
        }
    }

    private void setupDialog(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void acceptRide(){
//        myRef.child("requestRide")
//                .child(rideID)
//                .child(userID)
//                .child("accepted")
//                .setValue(true);

        //Will close the intent when the ride is accepted
        SendRespone("yes");
        Common.tripCustomer = mTripData;
        finish();
    }

    private void declineRide(){

//        myRef.child("requestRide")
//                .child(rideID)
//                .child(userID)
//                .removeValue();

        //Will close the intent when the ride is accepted
        SendRespone("no");
        finish();
    }

    private void getTrip(String partnerID) {
        mBackendService = BackendClient.getBackendService();
        mBackendService.getTrip(partnerID)
                .enqueue(new Callback<List<TripData>>() {
                    @Override
                    public void onResponse(Call<List<TripData>> call, Response<List<TripData>> response) {
                        if (!response.body().isEmpty()) {
                            mTripData = response.body().get(0);
                            txtTo.setText("To: " + mTripData.getmEndAddress());
                            txtFrom.setText("From: " + mTripData.getmStartAddress());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TripData>> call, Throwable t) {
                        Log.e("check Trip Error", t.getLocalizedMessage());
                    }
                });
    }

    private void SendRespone (String accept) {
        mService = Common.getFCMService();
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");


        tokens.orderByKey().equalTo(userIdCustomer)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Token token = dataSnapshot1.getValue(Token.class);

                            DataRequest data = new DataRequest(Common.userID,"respone",accept);
                            Send content = new Send(data, token.getToken());
                            mService.sendRequest(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, retrofit2.Response<FCMResponse> response) {
                                            Log.i("Send Respone", "onResponse: " + response.toString());
                                        }


                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("Send Respone Error", "onFailure: " + t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}

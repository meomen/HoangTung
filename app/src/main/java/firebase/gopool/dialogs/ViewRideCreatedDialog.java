package firebase.gopool.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import firebase.gopool.Account.ProfileActivity;
import firebase.gopool.Home.EditRideActivity;
import firebase.gopool.Model.AvailableRide;
import firebase.gopool.R;
import firebase.gopool.Running.MapsActivity;
import firebase.gopool.models.OfferRide;

public class ViewRideCreatedDialog extends Dialog implements
        View.OnClickListener {

    private static final String TAG = "ViewRideCreatedDialog";
    public Context c;
    public Dialog d;

    // variables
    private TextView mUsername, mRidesCompleted, mCost, mDepartureTime, mExtraTime, mFromStreet, mFromPostcode, mFromCity, mToStreet, mToPostcode, mToCity, mCancelDialogBtn, durationTextView, mPickupLocation;
    private RatingBar mRatingBar;
    private Button mEditRideBtn,mStartTripBtn;
    private FloatingActionButton mDeleteRideBtn, mPaticipantsRideBtn, mViewProfileBtn;
    private String userID, rides, seats, from, to, date, cost, username, dateOnly, extraTime, rideID, duration, ridesCompleted, pickupLocation;
    private Float rating;
    private OfferRide offerRide;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ride_details);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        setupWidgets();

        mPaticipantsRideBtn.setOnClickListener(this);
        mCancelDialogBtn.setOnClickListener(this);
        mEditRideBtn.setOnClickListener(this);
        mDeleteRideBtn.setOnClickListener(this);
        mViewProfileBtn.setOnClickListener(this);
        mStartTripBtn.setOnClickListener(this);
    }

    public ViewRideCreatedDialog(Context a, String rideID, String username, String rides, String seats, String from, String to, String date, String cost, Float rating, String dateOnly, String extraTime,
                                 String duration, String ridesCompleted, String pickupLocation, String userID) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.rideID = rideID;
        this.username = username;
        this.rides = rides;
        this.seats = seats;
        this.from = from;
        this.to = to;
        this.date = date;
        this.cost = cost;
        this.rating = rating;
        this.extraTime = extraTime;
        this.dateOnly = dateOnly;
        this.duration = duration;
        this.ridesCompleted = ridesCompleted;
        this.pickupLocation = pickupLocation;
        this.userID = userID;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_ride:
                dismiss();
                Intent intent1 = new Intent(c, EditRideActivity.class);
                intent1.putExtra("COST", cost);
                intent1.putExtra("EXTRATIME", extraTime);
                intent1.putExtra("DATE", date);
                intent1.putExtra("SEATS", seats);
                intent1.putExtra("DESTINATION2", to);
                intent1.putExtra("FROM2", from);
                intent1.putExtra("PICKUPTIME", dateOnly);
                intent1.putExtra("LENGTH", duration);
                intent1.putExtra("PICKUPLOCATION", pickupLocation);
                c.startActivity(intent1);
                break;
            case R.id.btn_start_trip:
                Intent intent = new Intent(c, MapsActivity.class);
                intent.putExtra("RideId",rideID);
                c.startActivity(intent);
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            case R.id.deleteRideBtn:
                showDialog();
                dismiss();
                break;
            case R.id.paticipantsRideBtn:
                showDialogParticpants();
                break;
            case R.id.viewProfileBtn:
                showIntentProfile();
                break;
            default:
                break;
        }
    }

    private void showDialog() {
        //Confirmation to delete the ride dialog
        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog(c, rideID);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showDialogParticpants() {
        //Confirmation to delete the ride dialog
        ParticipantsDialog dialog = new ParticipantsDialog(c, userID, rideID);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showIntentProfile() {
        //Confirmation to delete the ride dialog
        Intent intent = new Intent(c, ProfileActivity.class);
        c.startActivity(intent);
    }


    private void setupWidgets() {
        //Setup widgets
        mUsername = (TextView) findViewById(R.id.usernameTxt);
        mRidesCompleted = (TextView) findViewById(R.id.completedRidesTxt);
        mCost = (TextView) findViewById(R.id.costTxt);
        mDepartureTime = (TextView) findViewById(R.id.timeTxt);
        mExtraTime = (TextView) findViewById(R.id.extraTimeTxt);
        mFromStreet = (TextView) findViewById(R.id.streetNameTxt);
        mToStreet = (TextView) findViewById(R.id.streetName2Txt);
        durationTextView = (TextView) findViewById(R.id.durationNew);
        mPickupLocation = (TextView) findViewById(R.id.pickupLocationNew);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);


        mEditRideBtn = (Button) findViewById(R.id.btn_edit_ride);
        mStartTripBtn = (Button) findViewById(R.id.btn_start_trip);
        mDeleteRideBtn = (FloatingActionButton) findViewById(R.id.deleteRideBtn);
        mPaticipantsRideBtn = (FloatingActionButton) findViewById(R.id.paticipantsRideBtn);
        mViewProfileBtn = (FloatingActionButton) findViewById(R.id.viewProfileBtn);
        mCancelDialogBtn = (TextView) findViewById(R.id.dialogCancel);

        mCost.setText(cost);
        mUsername.setText(username);
        mRatingBar.setRating(rating);
        mDepartureTime.setText(dateOnly);
        mExtraTime.setText(extraTime);
        mFromStreet.setText(from);
        mToStreet.setText(to);
        durationTextView.setText("Duration: " + duration);
        mRidesCompleted.setText(ridesCompleted + " Rides");
        mPickupLocation.setText("Pickup: " + pickupLocation);
    }


}

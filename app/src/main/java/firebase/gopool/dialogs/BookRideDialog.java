package firebase.gopool.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import firebase.gopool.Account.ProfileActivity;
import firebase.gopool.Common.Common;
import firebase.gopool.Model.TripData;
import firebase.gopool.Payment.PaymentActivity;
import firebase.gopool.R;
import firebase.gopool.Remote.BackendClient;
import firebase.gopool.Remote.BackendService;
import firebase.gopool.Remote.IFCMService;
import firebase.gopool.Utils.MapUtils;
import firebase.gopool.Utils.SectionsStatePageAdapter;

public class BookRideDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "ViewRideCreatedDialog";
    public Context c;
    public Dialog d;

    private IFCMService mService;

    // variables
    private TextView mUsername, mFromStreet, mToStreet, mCancelDialogBtn;
    private RatingBar mRatingBar;
    private Button mEditRideBtn;
    private SectionsStatePageAdapter pageAdapter;
    private TripData trip;
    private FloatingActionButton mViewProfileBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ride_confirm);


        setupWidgets();

        mCancelDialogBtn.setOnClickListener(this);
        mEditRideBtn.setOnClickListener(this);
        mViewProfileBtn.setOnClickListener(this);
    }

    public BookRideDialog(Context a, TripData trip) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.trip = trip;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogConfirm:
                showDialog();
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            case R.id.viewProfileBtn:
                showIntentProfile();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void showDialog(){
        mService = Common.getFCMService();
        MapUtils.sendMessageRequest((AppCompatActivity)c,mService,trip);
    }

    private void showIntentProfile(){
        //Confirmation to delete the ride dialog
        Intent intent = new Intent(c, ProfileActivity.class);
        intent.putExtra("userID", "Duy Tung");
        c.startActivity(intent);
    }


    private void setupWidgets(){
        //Setup widgets
        mUsername = (TextView) findViewById(R.id.usernameTxt);
        mFromStreet = (TextView) findViewById(R.id.streetNameTxt);
        mToStreet = (TextView) findViewById(R.id.streetName2Txt);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);


        mEditRideBtn = (Button) findViewById(R.id.dialogConfirm);
        mCancelDialogBtn = (TextView) findViewById(R.id.dialogCancel);
        mViewProfileBtn = (FloatingActionButton) findViewById(R.id.viewProfileBtn);


        mFromStreet.setText(trip.getmStartAddress());
        mToStreet.setText(trip.getmEndAddress());
    }

}

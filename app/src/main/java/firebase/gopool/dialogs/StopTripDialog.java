package firebase.gopool.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import firebase.gopool.Booked.BookedActivity;
import firebase.gopool.Home.HomeActivity;
import firebase.gopool.R;
import firebase.gopool.Utils.FirebaseMethods;

public class StopTripDialog extends Dialog implements
        View.OnClickListener{
    private static final String TAG = "Stop trip";
    public Context c;
    public Dialog d;
    private TextView cancelDialog;
    private Button confirmDialog;
    public FusedLocationProviderClient mFusedLocationProviderClient;
    public LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_stop_trip);
        cancelDialog = (TextView) findViewById(R.id.dialogCancel);
        confirmDialog = (Button) findViewById(R.id.dialogConfirm);
        cancelDialog.setOnClickListener(this);
        confirmDialog.setOnClickListener(this);



    }

    public StopTripDialog(Context a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }
    public StopTripDialog(Context a, FusedLocationProviderClient mFusedLocationProviderClient, LocationCallback mLocationCallback){
        super(a);
        this.c = a;
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
        this.mLocationCallback = mLocationCallback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogConfirm:
                if (mFusedLocationProviderClient != null) {
                    mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                }
                dismiss();
                ((Activity) c).finish();
                Intent intent = new Intent(c, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                ((Activity) c).startActivity(intent);
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


}

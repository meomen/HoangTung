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
import firebase.gopool.Common.Common;
import firebase.gopool.Home.HomeActivity;
import firebase.gopool.R;
import firebase.gopool.Remote.IFCMService;
import firebase.gopool.Utils.FirebaseMethods;
import firebase.gopool.models.DataRequest;
import firebase.gopool.models.FCMResponse;
import firebase.gopool.models.Send;
import firebase.gopool.models.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class StopTripDialog extends Dialog implements
        View.OnClickListener{
    private static final String TAG = "Stop trip";
    public Context c;
    public Dialog d;
    private TextView cancelDialog;
    private Button confirmDialog;
    public FusedLocationProviderClient mFusedLocationProviderClient;
    public LocationCallback mLocationCallback;
    private boolean isCustomerStop = false;
    private IFCMService mService;
    private String userDriver;

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

    public StopTripDialog(Context a, boolean isCustomerStop,String userDriver) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.isCustomerStop = isCustomerStop;
        this.userDriver = userDriver;
    }
    public StopTripDialog(Context a, FusedLocationProviderClient mFusedLocationProviderClient, LocationCallback mLocationCallback){
        super(a);
        this.c = a;
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
        this.mLocationCallback = mLocationCallback;
    }

    public StopTripDialog(Context a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogConfirm:
                if (mFusedLocationProviderClient != null) {
                    mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                }
                if(isCustomerStop) {
                   CustomerStop();
                }
                else {
                    dismiss();
                    ((Activity) c).finish();
                    Intent intent = new Intent(c, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ((Activity) c).startActivity(intent);
                }
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void CustomerStop () {
        mService = Common.getFCMService();
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");


        tokens.orderByKey().equalTo(userDriver)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Token token = dataSnapshot1.getValue(Token.class);

                            DataRequest data = new DataRequest(Common.userID,Common.CUSTOMER_STOP,"");
                            Send content = new Send(data, token.getToken());
                            mService.sendRequest(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, retrofit2.Response<FCMResponse> response) {
                                            Log.i("Send Request", "onResponse: " + response.toString());
                                            if (response.body().success == 1 || response.code() == 200) {
                                                Toast.makeText(c, "Stop request sent!", Toast.LENGTH_SHORT).show();
                                                dismiss();

                                                Intent intent = new Intent(c, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                c.startActivity(intent);
                                            } else {
                                                Toast.makeText(c, "Stop request failed!", Toast.LENGTH_SHORT).show();
                                                dismiss();
                                            }
                                        }


                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("Send Request Error", "onFailure: " + t.getMessage());
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

package firebase.gopool.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;
import android.view.Window;
import android.widget.Toast;

import firebase.gopool.R;

public class BookingReceivedDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "BookingReceivedDialog";
    public Context c;
    public Dialog d;

    // variables
    private FloatingActionButton mConfirmBtn, declineRideBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_booking_received);

        mConfirmBtn = (FloatingActionButton) findViewById(R.id.confirmRideBtn);
        declineRideBtn = (FloatingActionButton) findViewById(R.id.declineRideBtn);

        declineRideBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);

    }

    public BookingReceivedDialog(Context a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmRideBtn:
                dismiss();
                Toast.makeText(c, "Ride confirmed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.declineRideBtn:
                Toast.makeText(c, "You cancelled the ride", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }




}

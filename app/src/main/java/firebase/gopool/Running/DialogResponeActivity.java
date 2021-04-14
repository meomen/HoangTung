package firebase.gopool.Running;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import firebase.gopool.Common.Common;
import firebase.gopool.Home.HomeActivity;
import firebase.gopool.R;

public class DialogResponeActivity extends AppCompatActivity {

    private String accept;
    private String userIdDriver;

    private TextView tv_title, tv_message;
    private ImageView img_icon;
    private Button btn_comfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_respone);

        accept = getIntent().getStringExtra("accept");
        userIdDriver = getIntent().getStringExtra("userIdDriver");

        img_icon = (ImageView) findViewById(R.id.img_icon);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_message = (TextView) findViewById(R.id.tv_message);
        btn_comfirm = (Button) findViewById(R.id.dialogConfirm);

        dialog();

    }

    private void dialog() {
        if(accept.equals("yes")) {
            img_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_png));
            tv_title.setText("Accept Ride");
            tv_message.setText("Drive had accept your request ride");
            btn_comfirm.setBackgroundColor(Color.GREEN);
            btn_comfirm.setOnClickListener(view -> {
                Intent intent = new Intent(DialogResponeActivity.this, MapsActivity.class);
                intent.putExtra("userIdDriver", userIdDriver);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
        }
        else if(accept.equals(Common.CUSTOMER_STOP)) {
            img_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));
            tv_title.setText("Customer Stop Trip");
            tv_message.setText("The customer wants to stop the trip");
            btn_comfirm.setBackgroundColor(Color.RED);
            btn_comfirm.setOnClickListener(view -> {
                Common.flatStop = Common.CUSTOMER_STOP;
                finish();
            });
        }
        else {
            img_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));
            tv_title.setText("No Accept Ride");
            tv_message.setText("Drive had not accept your request ride");
            btn_comfirm.setBackgroundColor(Color.RED);
            btn_comfirm.setOnClickListener(view -> {
                Intent intent = new Intent(DialogResponeActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
        }
    }

}
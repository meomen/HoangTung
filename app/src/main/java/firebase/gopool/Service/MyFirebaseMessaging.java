package firebase.gopool.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import firebase.gopool.Common.Common;
import firebase.gopool.Home.HomeActivity;
import firebase.gopool.R;
import firebase.gopool.Running.DialogResponeActivity;
import firebase.gopool.Running.MapsActivity;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private FirebaseAuth mAuth;
    private String userID;



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            //Gets userID of current user signed in
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }


       if (remoteMessage.getData().isEmpty()){

       } else {
//           if(remoteMessage.getData().get("body").contains(userID)){
//               showNotifcation(remoteMessage.getData());
//           }
           if(remoteMessage.getData().get("type").equals("request")) {
               showNotifcation(remoteMessage.getData());
           }
           else if (remoteMessage.getData().get("type").equals(Common.CUSTOMER_STOP)) {
                showCustomerStop(remoteMessage.getData());
           }
           else {
               if(remoteMessage.getData().get("accept").equals("yes")) {
                    showResponeYes(remoteMessage.getData());
               }
               else {
                   showResponeNo(remoteMessage.getData());
               }
           }
       }
    }



    private void showNotifcation(Map<String,String> data) {
        String userIdCustomer = data.get("userID").toString();


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "TEST";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification"
                    , NotificationManager.IMPORTANCE_DEFAULT );

            notificationChannel.setDescription("Testing");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 100});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Have 1 customer")
                .setContentText(userIdCustomer)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

        Intent intent = new Intent(this, CustomerActivity.class);
        intent.putExtra("userIdCustomer", userIdCustomer);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void showResponeYes(Map<String,String> data) {
        String userIdDriver = data.get("userID").toString();


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "TEST";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification"
                    , NotificationManager.IMPORTANCE_DEFAULT );

            notificationChannel.setDescription("Testing");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 100});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Driver accept your request")
                .setContentText(userIdDriver)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());



        Intent intent = new Intent(MyFirebaseMessaging.this, DialogResponeActivity.class);
        intent.putExtra("userIdDriver", userIdDriver);
        intent.putExtra("accept",  "yes");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void showResponeNo(Map<String,String> data) {
        String userIdDriver = data.get("userID").toString();


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "TEST";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification"
                    , NotificationManager.IMPORTANCE_DEFAULT );

            notificationChannel.setDescription("Testing");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 100});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Driver accept your request")
                .setContentText(userIdDriver)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

        Intent intent = new Intent(MyFirebaseMessaging.this, DialogResponeActivity.class);
        intent.putExtra("userIdDriver", userIdDriver);
        intent.putExtra("accept",  "no");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void showCustomerStop(Map<String,String> data) {
        String userIdDriver = data.get("userID").toString();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "TEST";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification"
                    , NotificationManager.IMPORTANCE_DEFAULT );

            notificationChannel.setDescription("Testing");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 100});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Customer stop trip")
                .setContentText(userIdDriver)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

        Intent intent = new Intent(MyFirebaseMessaging.this, DialogResponeActivity.class);
        intent.putExtra("userIdDriver", userIdDriver);
        intent.putExtra("accept",  Common.CUSTOMER_STOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

}

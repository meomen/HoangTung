package firebase.gopool.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import firebase.gopool.R;

public class MapUtils {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static Marker addCarMarkerAndGet(Context context, LatLng latLng, GoogleMap map){
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCarBitmap(context));
        return map.addMarker(new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor));
    }
    public static Bitmap getCarBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_car_bitmap);
        return Bitmap.createScaledBitmap(bitmap, 50, 100, false);
    }

    public static void startCurrentLocationUpdates(AppCompatActivity activity, FusedLocationProviderClient fusedLocationProviderClient, LocationCallback mLocationCallback) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);       //V?? ?????
        double lng = Math.abs(begin.longitude - end.longitude);     // Kinh ?????

          /*T??? ??i???m xu??t ph??t, ??i???m ?????n c?? 4 tr?????ng h???p:
           1: V?? ????? cao h??n, Kinh ????? cao h??n (G??c Tr??n-Ph???i)
           2: V?? ????? th???p h??n, K??nh ????? cao h??n (G??c D?????i-Ph???i)
           3: V?? ????? th???p h??n, K??nh ????? th???p h??n (G??c D?????i-Tr??i)
           4: V?? ????? cao h??n, K??nh ????? th???p h??n (G??c Tr??n-Tr??i)

           T????ng ???ng v???i m???i tr?????ng h???p s??? l?? 4 c??ch t??nh
         */

        if(begin.latitude < end.latitude && begin.longitude < end.longitude) {
            return (float)(Math.toDegrees(Math.atan(lng/lat)));
        }
        else if(begin.latitude >= end.latitude && begin.longitude < end.longitude) {
            return (float)((90 - Math.toDegrees(Math.atan(lng/lat)))+90);
        }
        else if(begin.latitude >= end.latitude && begin.longitude >= end.longitude) {
            return (float)(Math.toDegrees(Math.atan(lng/lat))+180);
        }
        else if(begin.latitude < end.latitude && begin.longitude >= end.longitude) {
            return (float)((90 - Math.toDegrees(Math.atan(lng/lat)))+270);
        }
        return -1;

    }


}

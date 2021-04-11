package firebase.gopool.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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

import java.util.ArrayList;
import java.util.List;

import firebase.gopool.Model.LocationFind;
import firebase.gopool.R;

public class MapUtils {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static LocationFind preFindList = null;

    public static Marker addCarMarkerAndGet(Context context, LatLng latLng, GoogleMap map) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCarBitmap(context));
        return map.addMarker(new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor));
    }

    public static Bitmap getCarBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_car_bitmap);
        return Bitmap.createScaledBitmap(bitmap, 56, 112, false);
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
        double lat = Math.abs(begin.latitude - end.latitude);       //Vĩ Độ
        double lng = Math.abs(begin.longitude - end.longitude);     // Kinh Độ

          /*Từ điểm xuât phát, điểm đến có 4 trường hợp:
           1: Vĩ độ cao hơn, Kinh độ cao hơn (Góc Trên-Phải)
           2: Vĩ độ thấp hơn, Kình độ cao hơn (Góc Dưới-Phải)
           3: Vĩ độ thấp hơn, Kình độ thấp hơn (Góc Dưới-Trái)
           4: Vĩ độ cao hơn, Kình độ thấp hơn (Góc Trên-Trái)

           Tương ứng với mỗi trường hợp sẽ là 4 cách tính
         */

        if (begin.latitude < end.latitude && begin.longitude < end.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        } else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        } else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        } else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        }
        return -1;

    }

    public static void drawCustomer(List<LocationFind> locationFindList, GoogleMap mMap) {

    }

//    public static void drawCar(Context context, List<LocationFind> locationFindList, GoogleMap mMap, ArrayList<Marker> mListMarkerCars) {
//        ArrayList<Marker> markers = new ArrayList<>();
//        if ((preFindList == null || preFindList.isEmpty()) && locationFindList != null) {
//            for (LocationFind locationFind : locationFindList) {
//                Marker marker = MapUtils.addCarMarkerAndGet(context, new LatLng(locationFind.mLatitude, locationFind.mLongitude), mMap);
//                markers.add(marker);
//            }
//            preFindList = locationFindList;
//            mListMarkerCars = markers;
//        } else if ((locationFindList == null || locationFindList.isEmpty()) && markers != null && !markers.isEmpty()) {
//            for (Marker marker : markers) {
//                marker.remove();
//            }
//        } else if (mListMarkerCars != null && !mListMarkerCars.isEmpty()) {
//            if (preFindList.equals(locationFindList)) {
//                return;
//            } else {
//                for (int i = 0; i < preFindList.size(); i++) {
//                    String id1 = preFindList.get(i).getmUserId();
//                    double lat1 = preFindList.get(i).getmLatitude();
//                    double lng1 = preFindList.get(i).getmLongitude();
//                    for (int j = 0; j < locationFindList.size(); j++) {
//                        String id2 = locationFindList.get(j).getmUserId();
//                        double lat2 = locationFindList.get(j).getmLatitude();
//                        double lng2 = locationFindList.get(j).getmLongitude();
//                        if (id1.equals(id2) && (lat1 != lat2 || lng1 != lng2)) {
//                            Marker marker = mListMarkerCars.get(i);
//                            marker.setRotation(MapUtils.getBearing(new LatLng(lat1,lng1),
//                                    new LatLng(lat2,lng2)));
//                            MarkerAnimation.animateMarkerToGB(marker, new LatLng(lng1,lng2), new LatLngInterpolator.Spherical());
//                        }
//                    }
//                }
//            }
//        }
//    }


    public static Marker drawCar(Context context, LocationFind locationFind, GoogleMap mMap, Marker mPartnerMarker) {
        if (mPartnerMarker == null && locationFind != null) {
            Marker marker = MapUtils.addCarMarkerAndGet(context, new LatLng(locationFind.mLatitude, locationFind.mLongitude), mMap);
            preFindList = locationFind;
            return marker;
        }else if (locationFind == null && mPartnerMarker != null ) {
            mPartnerMarker.remove();
            return null;
        }
        else if (mPartnerMarker != null) {
            LatLng currentLocation = new LatLng(locationFind.mLatitude,locationFind.mLongitude);
            LatLng preLocation = new LatLng(preFindList.mLatitude,preFindList.mLongitude);
            if(!preLocation.equals(currentLocation)) {
                mPartnerMarker.setRotation(MapUtils.getBearing(preLocation, currentLocation));
            }
            MarkerAnimation.animateMarkerToGB(mPartnerMarker, currentLocation, new LatLngInterpolator.Spherical());
            return mPartnerMarker;
        }
        return mPartnerMarker;
    }

}

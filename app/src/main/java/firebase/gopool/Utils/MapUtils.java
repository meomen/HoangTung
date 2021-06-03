package firebase.gopool.Utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import firebase.gopool.Common.Common;
import firebase.gopool.Home.HomeActivity;
import firebase.gopool.Model.LocationFind;
import firebase.gopool.Model.TripData;
import firebase.gopool.R;
import firebase.gopool.Remote.IFCMService;
import firebase.gopool.models.DataRequest;
import firebase.gopool.models.FCMResponse;
import firebase.gopool.models.Send;
import firebase.gopool.models.Token;
import firebase.gopool.models.User;
import retrofit2.Call;
import retrofit2.Callback;

public class MapUtils {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static LocationFind preFindList = null;

    public static Marker addCarMarkerAndGet(Context context, LatLng latLng, GoogleMap map) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCarBitmap(context));
        return map.addMarker(new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor).title("Car").snippet(""));
    }

    public static Bitmap getCarBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_car_bitmap);
        return Bitmap.createScaledBitmap(bitmap, 56, 112, false);
    }

    public static Marker addCustomerMarkerAndGet(Context context, LatLng latLng, GoogleMap map, String address) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCustomerBitmap(context));
        return map.addMarker(new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor).title("Pickup point").snippet(address));
    }

    public static Bitmap getCustomerBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_customer);
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
        } else if (locationFind == null && mPartnerMarker != null) {
            mPartnerMarker.remove();
            return null;
        } else if (mPartnerMarker != null) {
            LatLng currentLocation = new LatLng(locationFind.mLatitude, locationFind.mLongitude);
            LatLng preLocation = new LatLng(preFindList.mLatitude, preFindList.mLongitude);
            if (!preLocation.equals(currentLocation)) {
                mPartnerMarker.setRotation(MapUtils.getBearing(preLocation, currentLocation));
            }
            MarkerAnimation.animateMarkerToGB(mPartnerMarker, currentLocation, new LatLngInterpolator.Spherical());
            return mPartnerMarker;
        }
        return mPartnerMarker;
    }

    public static void sendMessageRequest(AppCompatActivity activity, IFCMService mService, TripData tripData) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");


        tokens.orderByKey().equalTo(tripData.getmUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Token token = dataSnapshot1.getValue(Token.class);

                            DataRequest data = new DataRequest(Common.userID,"request","");
                            Send content = new Send(data, token.getToken());
                            mService.sendRequest(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, retrofit2.Response<FCMResponse> response) {
                                            Log.i("Send Request", "onResponse: " + response.toString());
                                            if (response.body().success == 1 || response.code() == 200) {
                                                Toast.makeText(activity, "Booking request sent!", Toast.LENGTH_SHORT).show();
//
//                                                Intent intent = new Intent(activity, HomeActivity.class);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                activity.startActivity(intent);
                                                activity.finish();
                                            } else {
                                                Toast.makeText(activity, "Booking request failed!", Toast.LENGTH_SHORT).show();

//                                                Intent intent = new Intent(activity, HomeActivity.class);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                activity.startActivity(intent);
                                                activity.finish();
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

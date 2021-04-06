package firebase.gopool.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import firebase.gopool.R;

public class MapUtils {

    public static Marker addCarMarkerAndGet(Context context,LatLng latLng,GoogleMap map){
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCarBitmap(context));
        return map.addMarker(new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor));
    }
    public static Bitmap getCarBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_car_bitmap);
        return Bitmap.createScaledBitmap(bitmap, 50, 100, false);
    }

}

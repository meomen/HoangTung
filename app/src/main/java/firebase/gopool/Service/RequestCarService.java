package firebase.gopool.Service;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import firebase.gopool.Utils.MapUtils;

public class RequestCarService {

    public void searchCar(Context context, LatLng latLng, GoogleMap map) {
        Marker markerCar = MapUtils.addCarMarkerAndGet(context,latLng,map);
        markerCar.setAnchor(0.5f,0.5f);
    }
}

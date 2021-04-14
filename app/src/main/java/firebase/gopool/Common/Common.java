package firebase.gopool.Common;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import firebase.gopool.Model.TripData;
import firebase.gopool.Remote.BackendClient;
import firebase.gopool.Remote.BackendService;
import firebase.gopool.Remote.FCMClient;
import firebase.gopool.Remote.IFCMService;

public class Common {

    public static final String CUSTOMER_STOP = "customer_stop";
    public static final String DRIVER_STOP = "driver_stop";

    public static String fcmURL = "https://fcm.googleapis.com/";
    public static String className;
    public static LatLng pickupLatLng;
    public static String userID = null;
    public static String poly = null;
    public static TripData tripCustomer;
    public static TripData tripDriver;
    public static Polyline currentPoly;
    public static String flatStop;
    public static boolean customerOnMap = false;

    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
    public static BackendService getBackService(){
        return BackendClient.getClient().create(BackendService.class);
    }


    public static String getClassName() {
        return className;
    }

    public static LatLng getPickupLatLng() {
        return pickupLatLng;
    }

    public static void setPickupLatLng(LatLng pickupLatLng) {
        Common.pickupLatLng = pickupLatLng;
    }

    public static void setClassName(String className) {
        Common.className = className;
    }
}

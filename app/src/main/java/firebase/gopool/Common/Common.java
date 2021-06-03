package firebase.gopool.Common;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import firebase.gopool.Model.TripData;
import firebase.gopool.Remote.BackendClient;
import firebase.gopool.Remote.BackendService;
import firebase.gopool.Remote.FCMClient;
import firebase.gopool.Remote.IFCMService;
import firebase.gopool.models.User;

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

    public static String getFileName(ContentResolver contentResolver, Uri fileUri) {
        String result = null;
        if(fileUri.getScheme().equals("content")) {
            Cursor cursor = contentResolver.query(fileUri,null,null,null,null);
            try {
                if(cursor != null && cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

            }
            finally {
                cursor.close();
            }
        }
        if(result == null) {
            result = fileUri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1)
                result = result.substring(cut+1);
        }
        return result;
    }
}

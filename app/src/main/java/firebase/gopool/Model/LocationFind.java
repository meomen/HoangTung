package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

public class LocationFind {
    @SerializedName("user_id")
    public String mUserId;
    @SerializedName("latitude")
    public double mLatitude;
    @SerializedName("longitude")
    public double mLongitude;

    public LocationFind() {
    }

    public LocationFind(String mUserId, double mLatitude, double mLongitude) {
        this.mUserId = mUserId;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }
}

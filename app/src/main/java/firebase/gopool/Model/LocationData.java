package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

public class LocationData {

    @SerializedName("user_id")
    public String mUserId;
    @SerializedName("role")
    public String mRole;
    @SerializedName("latitude")
    public double mLatitude;
    @SerializedName("longitude")
    public double mLongitude;

    public LocationData() {
    }

    public LocationData(String mUserId, String mRole, double mLatitude, double mLongitude) {
        this.mUserId = mUserId;
        this.mRole = mRole;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmRole() {
        return mRole;
    }

    public void setmRole(String mRole) {
        this.mRole = mRole;
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

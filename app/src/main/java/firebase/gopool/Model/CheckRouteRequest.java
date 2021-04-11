package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

public class CheckRouteRequest {
    @SerializedName("latitude")
    public double mLatitude;
    @SerializedName("longitude")
    public double mLongitude;
    @SerializedName("code")
    public String mCode;

    public CheckRouteRequest(double mLatitude, double mLongitude, String mCode) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mCode = mCode;
    }

    public CheckRouteRequest() {
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

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }
}

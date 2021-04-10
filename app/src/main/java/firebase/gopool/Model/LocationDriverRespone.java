package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationDriverRespone {
    @SerializedName("status")
    int mStatus;
    @SerializedName("message")
    String mMessage;
    @SerializedName("location_driver")
    List<LocationData> mLocationDriver;

    public LocationDriverRespone() {
    }

    public LocationDriverRespone(int mStatus, String mMessage, List<LocationData> mLocationDriver) {
        this.mStatus = mStatus;
        this.mMessage = mMessage;
        this.mLocationDriver = mLocationDriver;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public List<LocationData> getmLocationDriver() {
        return mLocationDriver;
    }

    public void setmLocationDriver(List<LocationData> mLocationDriver) {
        this.mLocationDriver = mLocationDriver;
    }
}

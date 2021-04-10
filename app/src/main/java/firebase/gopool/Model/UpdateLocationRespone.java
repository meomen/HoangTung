package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateLocationRespone {
    @SerializedName("status")
    int mStatus;
    @SerializedName("distance")
    double mDistance;
    @SerializedName("message")
    String mMessage;
    @SerializedName("location_driver")
    List<LocationFind> mLocationFind;

    public UpdateLocationRespone() {
    }

    public UpdateLocationRespone(int mStatus, double mDistance, String mMessage, List<LocationFind> mLocationFind) {
        this.mStatus = mStatus;
        this.mDistance = mDistance;
        this.mMessage = mMessage;
        this.mLocationFind = mLocationFind;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public double getmDistance() {
        return mDistance;
    }

    public void setmDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public List<LocationFind> getmLocationFind() {
        return mLocationFind;
    }

    public void setmLocationFind(List<LocationFind> mLocationFind) {
        this.mLocationFind = mLocationFind;
    }
}

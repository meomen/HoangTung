package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

public class GetTripRespone {

    @SerializedName("start_address")
    String mStartAddress;
    @SerializedName("latitude_start")
    double mLatitudeStart;
    @SerializedName("longitude_start")
    double mLongitudeStart;
    @SerializedName("end_address")
    String mEndAddress;
    @SerializedName("latitude_end")
    double mLatitudeEnd;
    @SerializedName("longitude_end")
    double mLongitudeEnd;

    public GetTripRespone() {
    }

    public GetTripRespone(String mStartAddress, double mLatitudeStart, double mLongitudeStart, String mEndAddress, double mLatitudeEnd, double mLongitudeEnd) {
        this.mStartAddress = mStartAddress;
        this.mLatitudeStart = mLatitudeStart;
        this.mLongitudeStart = mLongitudeStart;
        this.mEndAddress = mEndAddress;
        this.mLatitudeEnd = mLatitudeEnd;
        this.mLongitudeEnd = mLongitudeEnd;
    }

    public String getmStartAddress() {
        return mStartAddress;
    }

    public void setmStartAddress(String mStartAddress) {
        this.mStartAddress = mStartAddress;
    }

    public double getmLatitudeStart() {
        return mLatitudeStart;
    }

    public void setmLatitudeStart(double mLatitudeStart) {
        this.mLatitudeStart = mLatitudeStart;
    }

    public double getmLongitudeStart() {
        return mLongitudeStart;
    }

    public void setmLongitudeStart(double mLongitudeStart) {
        this.mLongitudeStart = mLongitudeStart;
    }

    public String getmEndAddress() {
        return mEndAddress;
    }

    public void setmEndAddress(String mEndAddress) {
        this.mEndAddress = mEndAddress;
    }

    public double getmLatitudeEnd() {
        return mLatitudeEnd;
    }

    public void setmLatitudeEnd(double mLatitudeEnd) {
        this.mLatitudeEnd = mLatitudeEnd;
    }

    public double getmLongitudeEnd() {
        return mLongitudeEnd;
    }

    public void setmLongitudeEnd(double mLongitudeEnd) {
        this.mLongitudeEnd = mLongitudeEnd;
    }
}

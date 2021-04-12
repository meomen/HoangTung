package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class TripData implements Serializable {

    @SerializedName("id")
    String mId;
    @SerializedName("role")
    String mRole;
    @SerializedName("user_id")
    String mUserId;
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
    @SerializedName("status")
    int mStatus;
    @SerializedName("poly")
    String mPoly;
    @SerializedName("createdAt")
    String mCreateAt;

    public TripData() {
    }

    public TripData(String mId, String mRole, String mUserId, String mStartAddress, double mLatitudeStart, double mLongitudeStart, String mEndAddress, double mLatitudeEnd, double mLongitudeEnd, int mStatus, String mPoly, String mCreateAt) {
        this.mId = mId;
        this.mRole = mRole;
        this.mUserId = mUserId;
        this.mStartAddress = mStartAddress;
        this.mLatitudeStart = mLatitudeStart;
        this.mLongitudeStart = mLongitudeStart;
        this.mEndAddress = mEndAddress;
        this.mLatitudeEnd = mLatitudeEnd;
        this.mLongitudeEnd = mLongitudeEnd;
        this.mStatus = mStatus;
        this.mPoly = mPoly;
        this.mCreateAt = mCreateAt;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmRole() {
        return mRole;
    }

    public void setmRole(String mRole) {
        this.mRole = mRole;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
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

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public String getmCreateAt() {
        return mCreateAt;
    }

    public void setmCreateAt(String mCreateAt) {
        this.mCreateAt = mCreateAt;
    }

    public String getmPoly() {
        return mPoly;
    }

    public void setmPoly(String mPoly) {
        this.mPoly = mPoly;
    }
}

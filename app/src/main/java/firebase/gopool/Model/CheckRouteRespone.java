package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

public class CheckRouteRespone {

    @SerializedName("status")
    int mStatus;
    @SerializedName("near")
    boolean mNear;
    @SerializedName("message")
    String mMessage;

    public CheckRouteRespone() {
    }

    public CheckRouteRespone(int mStatus, boolean mNear, String mMessage) {
        this.mStatus = mStatus;
        this.mNear = mNear;
        this.mMessage = mMessage;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public boolean ismNear() {
        return mNear;
    }

    public void setmNear(boolean mNear) {
        this.mNear = mNear;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}


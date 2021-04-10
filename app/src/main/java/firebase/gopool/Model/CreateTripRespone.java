package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

public class CreateTripRespone {
    @SerializedName("status")
    int mStatus;
    @SerializedName("body")
    TripData mBody;
    @SerializedName("message")
    String mMessage;

    public CreateTripRespone() {
    }

    public CreateTripRespone(int mStatus, TripData mBody, String mMessage) {
        this.mStatus = mStatus;
        this.mBody = mBody;
        this.mMessage = mMessage;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public TripData getmBody() {
        return mBody;
    }

    public void setmBody(TripData mBody) {
        this.mBody = mBody;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}

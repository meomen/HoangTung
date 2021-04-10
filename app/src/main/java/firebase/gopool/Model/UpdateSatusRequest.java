package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

public class UpdateSatusRequest {

    @SerializedName("user_id")
    String userId;

    public UpdateSatusRequest() {
    }

    public UpdateSatusRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

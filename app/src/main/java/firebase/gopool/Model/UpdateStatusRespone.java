package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

public class UpdateStatusRespone {
    @SerializedName("status")
    int status;
    @SerializedName("user_id")
    String userId;
    @SerializedName("messsage")
    String message;

    public UpdateStatusRespone() {
    }

    public UpdateStatusRespone(int status, String userId, String message) {
        this.status = status;
        this.userId = userId;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

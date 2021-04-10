package firebase.gopool.Model;

import com.google.gson.annotations.SerializedName;

public class LocationData {

    @SerializedName("user_id")
    public String userID;
    @SerializedName("role")
    public String role;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("longitude")
    public String longitude;

    public LocationData() {
    }

    public LocationData(String userID, String role, String latitude, String longitude) {
        this.userID = userID;
        this.role = role;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}

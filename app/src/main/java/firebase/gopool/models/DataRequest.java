package firebase.gopool.models;

public class DataRequest {
    String userID;
    String type;
    String accept;

    public DataRequest(String userID, String type, String accept) {
        this.userID = userID;
        this.type = type;
        this.accept = accept;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}

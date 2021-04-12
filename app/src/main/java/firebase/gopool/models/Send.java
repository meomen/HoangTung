package firebase.gopool.models;

public class Send {
    public DataRequest data;
    public String to;

    public Send() {
    }

    public Send(DataRequest data, String to) {
        this.data = data;
        this.to = to;
    }

    public DataRequest getData() {
        return data;
    }

    public void setData(DataRequest data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}

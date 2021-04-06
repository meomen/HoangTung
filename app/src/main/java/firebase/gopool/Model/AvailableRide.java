package firebase.gopool.Model;

import java.io.Serializable;

import firebase.gopool.models.OfferRide;

public class AvailableRide extends OfferRide implements Serializable {
    private String status;
    private String accepted;

    public AvailableRide() {
        super();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }
}

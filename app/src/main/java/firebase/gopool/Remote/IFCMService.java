package firebase.gopool.Remote;

import firebase.gopool.models.FCMResponse;
import firebase.gopool.models.Send;
import firebase.gopool.models.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAVRkq9zA:APA91bER0il2SThmfzliibDeJ6qr3RffR_qazt0io53wcyiGbLfED7N2pNhfemHLGTUA5xSTmIC5RWTEO1-74VFCglRAanVb_r7uzMY6pEh__3UEe9w2Cb9tzHiGjjLu0W9MOUx0qsu3"
    })
//    @POST("fcm/send")
//    Call<FCMResponse> sendMessage(@Body Sender body);

    @POST("fcm/send")
    Call<FCMResponse> sendRequest(@Body Send body);
}

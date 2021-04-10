package firebase.gopool.Remote;

import firebase.gopool.models.FCMResponse;
import firebase.gopool.models.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface BackendService {
    @Headers({
            "Content-Type:application/json",
    })
    @POST("location/update")
    Call<FCMResponse> sendMessage(@Body Sender body);
}

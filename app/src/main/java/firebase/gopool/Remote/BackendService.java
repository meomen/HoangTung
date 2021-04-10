package firebase.gopool.Remote;

import firebase.gopool.Model.CreateTripRequest;
import firebase.gopool.Model.CreateTripRespone;
import firebase.gopool.Model.GetTripRespone;
import firebase.gopool.Model.LocationData;
import firebase.gopool.Model.LocationDriverRespone;
import firebase.gopool.Model.UpdateLocationRespone;
import firebase.gopool.models.FCMResponse;
import firebase.gopool.models.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BackendService {
    @Headers({
            "Content-Type:application/json",
    })
    @POST("location/update")
    Call<FCMResponse> sendMessage(@Body Sender body);

    @GET("location/trip/{id}")
    Call<GetTripRespone> getTrip(@Path("id") String id);

    @GET("location/{id}")
    Call<LocationDriverRespone> getLocationDriver(@Path("id") String id);

    @POST("location/update")
    Call<UpdateLocationRespone> updateLocation(@Body LocationData locationData);

    @POST("trip/create")
    Call<CreateTripRespone> createTrip (@Body CreateTripRequest createTripRequest);
}

package firebase.gopool.Remote;

import firebase.gopool.models.FCMResponse;
import firebase.gopool.models.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAO-7zNjc:APA91bGo9dSEzrb0Qm4gCynQ3fUrsKCj5q49Q40-dZyFUZnzJq2LsFiffHl9jG7pB33MetH6nhzyCXGt5Poi54Vj-4My_f_ykHvl_n-0FeJw9NobTHPi4QxhGSFGXs1YKklnoTJrT27N"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}

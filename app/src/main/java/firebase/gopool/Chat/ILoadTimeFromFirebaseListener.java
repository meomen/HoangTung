package firebase.gopool.Chat;


//lắng nghe sự kiện lấy thời gian từ Firebase
// Dùng để tạo ID hoặc lưu thời gian
public interface ILoadTimeFromFirebaseListener {
    void onLoadOnlyTimeSuccess( long estimateTimeInMs);
    void onLoadtimeFailed(String message);
}

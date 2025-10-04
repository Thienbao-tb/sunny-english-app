package hmd.example.firebaseprojectstudyenglish.notify;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "channel_service_example";  // Định nghĩa ID của kênh thông báo

    @Override
    public void onCreate() {
        super.onCreate();
        createChannelNotification();  // Tạo kênh thông báo khi ứng dụng được khởi tạo
    }

    // Phương thức tạo kênh thông báo (dành cho Android 8.0 trở lên)
    private void createChannelNotification() {
        // Kiểm tra nếu phiên bản Android lớn hơn Oreo (Android 8.0)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            // Tạo một kênh thông báo mới với tên và mức độ ưu tiên
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,  // ID của kênh
                    "Channel Service Example",  // Tên kênh
                    NotificationManager.IMPORTANCE_DEFAULT);  // Mức độ ưu tiên thông báo

            // Lấy dịch vụ NotificationManager để tạo kênh thông báo
            NotificationManager manager = getSystemService(NotificationManager.class);

            // Kiểm tra nếu NotificationManager không rỗng, tạo kênh thông báo
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}

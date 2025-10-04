package hmd.example.firebaseprojectstudyenglish.notify;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import hmd.example.firebaseprojectstudyenglish.R;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import hmd.example.firebaseprojectstudyenglish.MainActivity;
import hmd.example.firebaseprojectstudyenglish.taikhoan.LoginActivity;

import static hmd.example.firebaseprojectstudyenglish.notify.MyApplication.CHANNEL_ID;

public class MyService extends Service {

    private static final int ACTION_OK = 1;  // Định nghĩa hành động thông báo "OK"

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // Phương thức này không được sử dụng trong trường hợp này vì dịch vụ không liên kết với bất kỳ activity nào
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int actionNotify = -1;

        // Lấy giá trị của "action_notify_service" từ Intent
        try {
            actionNotify = intent.getIntExtra("action_notify_service", 0);
        } catch (Exception e) {
            e.printStackTrace();  // Nếu có lỗi xảy ra, in ra log
        }

        // Nếu giá trị của actionNotify không phải 0 hoặc -1, xử lý theo hành động thông báo
        if (actionNotify != 0 && actionNotify != -1) {
            handleActionNotify(actionNotify);
        } else {
            sendNotification();  // Gửi thông báo
        }

        return START_NOT_STICKY;  // Dịch vụ không tự động khởi động lại sau khi bị dừng
    }

    // Xử lý các hành động thông báo
    private void handleActionNotify(int action) {
        switch (action) {
            case ACTION_OK:
                okFunction();  // Gọi hàm xử lý khi hành động OK được kích hoạt
                break;
        }
    }

    // Hàm xử lý khi hành động OK được chọn
    private void okFunction() {
        // Lấy thể hiện tĩnh của activity LoginActivity
        LoginActivity activity = LoginActivity.instance;
        if (activity != null) {
            // Gọi phương thức clickStopService của activity để dừng dịch vụ
            activity.clickStopService();
        }
    }

    // Phương thức gửi thông báo
    private void sendNotification() {
        // Tạo intent mở LoginActivity khi người dùng nhấn vào thông báo
        Intent intent = new Intent(this, LoginActivity.class);

        // Tạo PendingIntent với FLAG_IMMUTABLE
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo RemoteViews để tùy chỉnh giao diện thông báo
        @SuppressLint("RemoteViewLayout")
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_login);

        // Thiết lập PendingIntent cho nút "OK" trong thông báo
        remoteViews.setOnClickPendingIntent(R.id.btn_ok, getPendingIntent(this, ACTION_OK));

        // Tạo thông báo và bắt đầu dịch vụ ở chế độ foreground
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // Biểu tượng nhỏ của thông báo
                .setContentIntent(pendingIntent)  // Đặt PendingIntent cho hành động nhấn vào thông báo
                .setCustomContentView(remoteViews)  // Đặt giao diện tùy chỉnh cho thông báo
                .build();

        // Chạy thông báo ở chế độ foreground để tránh bị dừng khi ứng dụng chạy nền
        startForeground(1, notification);
    }

    // Phương thức tạo PendingIntent cho hành động thông báo
    private PendingIntent getPendingIntent(Context context, int action) {
        // Tạo intent gửi đến MyReceiver để xử lý hành động thông báo
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action_notify", action);

        // Trả về PendingIntent để thực hiện hành động thông báo
        return PendingIntent.getBroadcast(
                context.getApplicationContext(),
                action,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

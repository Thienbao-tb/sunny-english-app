package hmd.example.firebaseprojectstudyenglish.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    // Phương thức onReceive sẽ được gọi khi nhận được một broadcast
    @Override
    public void onReceive(Context context, Intent intent) {
        // Lấy giá trị action_notify từ intent (mặc định là 0 nếu không có giá trị này)
        int actionNotify = intent.getIntExtra("action_notify", 0);

        // Tạo một Intent mới để gọi dịch vụ MyService
        Intent intentService = new Intent(context, MyService.class);

        // Gửi giá trị action_notify đến MyService qua putExtra
        intentService.putExtra("action_notify_service", actionNotify);

        // Bắt đầu dịch vụ MyService và truyền theo Intent vừa tạo
        context.startService(intentService);
    }
}

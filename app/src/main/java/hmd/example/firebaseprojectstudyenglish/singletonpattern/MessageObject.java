package hmd.example.firebaseprojectstudyenglish.singletonpattern;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import hmd.example.firebaseprojectstudyenglish.R;

public class MessageObject {

    // Đối tượng MessageObject duy nhất (Singleton)
    private static MessageObject instance = new MessageObject();

    // Constructor riêng để ngăn ngừa việc khởi tạo đối tượng từ bên ngoài lớp này
    private MessageObject(){}

    // Phương thức công khai để lấy đối tượng duy nhất của lớp MessageObject
    public static MessageObject getInstance(){
        return instance;
    }

    // Phương thức hiển thị hộp thoại thông báo
    public void ShowDialogMessage(int gravity, Context context, String message, int type){

        // Tạo đối tượng Dialog mới
        final Dialog dialog = new Dialog(context);

        // Yêu cầu dialog không có tiêu đề
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Đặt layout cho dialog sử dụng layout tùy chỉnh (thongbao)
        dialog.setContentView(R.layout.thongbao);

        // Lấy đối tượng window của dialog để điều chỉnh các thuộc tính
        Window window = dialog.getWindow();
        if(window == null){
            return;  // Nếu window không hợp lệ, dừng phương thức
        }

        // Đặt tham số layout cho cửa sổ dialog (chiều rộng full màn hình, chiều cao tự động thay đổi)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        // Đặt màu nền của cửa sổ dialog là trong suốt
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Cập nhật các thuộc tính của cửa sổ, cụ thể là vị trí (gravity)
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;  // Vị trí của dialog trên màn hình
        window.setAttributes(windowAttributes);

        // Kiểm tra nếu gravity là Gravity.BOTTOM, cho phép đóng dialog khi chạm ra ngoài, nếu không thì không cho phép
        if(Gravity.BOTTOM == gravity)
        {
            dialog.setCancelable(true);
        }
        else {
            dialog.setCancelable(false);
        }

        // Lấy các thành phần UI trong dialog
        TextView txt_name = (TextView) dialog.findViewById(R.id.dialogError2_name);
        TextView txt_message = (TextView) dialog.findViewById(R.id.dialogError2_content);
        Button btn_oke = (Button) dialog.findViewById(R.id.btn_dialogError_Oke);

        // Thiết lập tiêu đề cho thông báo, nếu type là 1 thì là "SUCCESS", nếu không là "ERROR"
        if(type == 1) txt_name.setText("SUCCESS");
        else txt_name.setText("ERROR");

        // Đặt nội dung của thông báo
        txt_message.setText(message);

        // Đặt sự kiện cho nút "OK" trong dialog để đóng dialog khi được nhấn
        btn_oke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();  // Đóng dialog
            }
        });

        // Hiển thị dialog
        dialog.show();
    }
}

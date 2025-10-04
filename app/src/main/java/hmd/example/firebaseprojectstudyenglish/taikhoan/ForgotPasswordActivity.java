package hmd.example.firebaseprojectstudyenglish.taikhoan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.singletonpattern.MessageObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText EmailF; // Ô nhập Email của người dùng
    private Button btnResetP; // Nút reset mật khẩu

    FirebaseAuth mAuth; // FirebaseAuth dùng để gửi email reset mật khẩu
    private MessageObject messageObject = MessageObject.getInstance(); // Đối tượng thông báo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Ánh xạ các view vào các biến
        EmailF = (EditText) findViewById(R.id.etEmailForgot);
        btnResetP = (Button) findViewById(R.id.btnResetPass);

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        // Đặt sự kiện khi người dùng nhấn vào nút reset mật khẩu
        btnResetP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(); // Gọi phương thức reset mật khẩu
            }
        });
    }

    // Phương thức để thực hiện reset mật khẩu
    private void resetPassword() {
        String email = EmailF.getText().toString().trim();

        // Kiểm tra nếu email trống
        if (email.isEmpty()) {
            EmailF.setError("Hãy nhập Email của bạn!"); // Hiển thị lỗi nếu email trống
            EmailF.requestFocus();
            return;
        }

        // Kiểm tra nếu email không hợp lệ
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EmailF.setError("Hãy nhập đúng Email!"); // Hiển thị lỗi nếu email không hợp lệ
            EmailF.requestFocus();
            return;
        }

        // Gửi email reset mật khẩu qua Firebase
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Nếu gửi thành công
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Hãy kiểm tra (Hộp thư đến) của bạn để tiến hành thiết lập lại mật khẩu!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class); // Quay lại màn hình đăng nhập
                    startActivity(intent);
                } else {
                    // Nếu không thành công
                    messageObject.ShowDialogMessage(Gravity.CENTER,
                            ForgotPasswordActivity.this,
                            "KHÔNG THÀNH CÔNG! Hãy kiểm tra lại Email của bạn và thử lại!",
                            0); // Hiển thị thông báo lỗi
                }
            }
        });
    }
}

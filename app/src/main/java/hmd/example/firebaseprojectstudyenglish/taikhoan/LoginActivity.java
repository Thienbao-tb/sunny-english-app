package hmd.example.firebaseprojectstudyenglish.taikhoan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import hmd.example.firebaseprojectstudyenglish.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import hmd.example.firebaseprojectstudyenglish.MainActivity;
import hmd.example.firebaseprojectstudyenglish.admin.AdminActivity;
import hmd.example.firebaseprojectstudyenglish.database.DatabaseAccess;
import hmd.example.firebaseprojectstudyenglish.notify.MyService;
import hmd.example.firebaseprojectstudyenglish.singletonpattern.MessageObject;

public class LoginActivity extends AppCompatActivity {

    Button btnDangnhap;
    TextView tvDangky, tvforgotPassword;
    EditText edttaikhoan, edtmatkhau;
    DatabaseAccess DB;
    FirebaseDatabase rootNode; // Cơ sở dữ liệu Firebase
    DatabaseReference userref; // Tham chiếu đến "User" trong Firebase
    private FirebaseAuth mAuth; // Khởi tạo FirebaseAuth
    private final MessageObject messageObject = MessageObject.getInstance(); // Đối tượng thông báo

    public static LoginActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);

        instance = this;

        AnhXa(); // Ánh xạ các view vào các biến
        DB = DatabaseAccess.getInstance(getApplicationContext()); // Khởi tạo DatabaseAccess
        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        // Đăng nhập thành công sẽ chuyển sang MainActivity
        btnDangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edttaikhoan.getText().toString().trim();
                String matkhau = edtmatkhau.getText().toString().trim();

                // Kiểm tra nhập liệu email và mật khẩu
                if (TextUtils.isEmpty(email)) {
                    messageObject.ShowDialogMessage(Gravity.CENTER,
                            LoginActivity.this,
                            "Hãy nhập Email của bạn!!",
                            0);
                    return;
                }

                if (TextUtils.isEmpty(matkhau)) {
                    messageObject.ShowDialogMessage(Gravity.CENTER,
                            LoginActivity.this,
                            "Hãy nhập mật khẩu của bạn!!",
                            0);
                    return;
                }

                // Đăng nhập người dùng đã tồn tại
                mAuth.signInWithEmailAndPassword(email, matkhau)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),
                                                    "Đăng nhập thành công!!",
                                                    Toast.LENGTH_LONG)
                                            .show();

                                    // Bắt đầu dịch vụ thông báo
                                    Intent intent = new Intent(LoginActivity.this, MyService.class);
                                    startService(intent);

                                    DB.iduser = mAuth.getCurrentUser().getUid();
                                    DB.CapNhatUser(DB.iduser);

                                    // Lấy thông tin "role" của người dùng từ Firebase
                                    rootNode = FirebaseDatabase.getInstance();
                                    userref = rootNode.getReference("User").child(DB.iduser);
                                    userref.child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            int roleValue = Integer.parseInt(task.getResult().getValue().toString());

                                            Intent intent;
                                            // Nếu là admin, chuyển đến AdminActivity
                                            if (roleValue == 0) {
                                                intent = new Intent(LoginActivity.this, AdminActivity.class);
                                            }
                                            // Nếu là người dùng bình thường, chuyển đến MainActivity
                                            else {
                                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                            }

                                            startActivity(intent); // Mở màn hình tương ứng
                                        }
                                    });
                                } else {
                                    // Nếu đăng nhập thất bại
                                    messageObject.ShowDialogMessage(Gravity.CENTER,
                                            LoginActivity.this,
                                            "Sai Email hoặc mật khẩu!!",
                                            0);
                                }
                            }
                        });
            }
        });

        // Nếu người dùng muốn đăng ký tài khoản mới, chuyển sang màn hình đăng ký
        tvDangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Nếu người dùng quên mật khẩu, chuyển sang màn hình khôi phục mật khẩu
        tvforgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    // Ánh xạ các view trong layout
    private void AnhXa() {
        btnDangnhap = (Button) findViewById(R.id.buttonDangnhap);
        tvDangky = (TextView) findViewById(R.id.textView_register);
        tvforgotPassword = (TextView) findViewById(R.id.textView_forgotPassword);
        edttaikhoan = (EditText) findViewById(R.id.editTextUser);
        edtmatkhau = (EditText) findViewById(R.id.editTextPass);
    }

    // Dừng dịch vụ MyService nếu cần
    public void clickStopService() {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

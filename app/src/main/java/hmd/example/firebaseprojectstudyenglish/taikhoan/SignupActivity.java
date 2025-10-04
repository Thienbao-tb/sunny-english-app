
package hmd.example.firebaseprojectstudyenglish.taikhoan;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.database.DatabaseAccess;
import hmd.example.firebaseprojectstudyenglish.singletonpattern.MessageObject;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    // Các view trong layout
    TextView tvDangNhap;
    EditText edtHoTen, edtEmail, edtSdt, edtMatKhau, edtXacNhan;
    Button btnSignUp;
    FirebaseAuth mAuth;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseAccess DB;
    private MessageObject messageObject = MessageObject.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        AnhXa(); // Ánh xạ các view vào các biến

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth
        DB = DatabaseAccess.getInstance(getApplicationContext()); // Khởi tạo DatabaseAccess

        // Thiết lập sự kiện click cho TextView để chuyển sang màn hình đăng nhập
        tvDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Thiết lập sự kiện click cho nút đăng ký
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoten = edtHoTen.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String sdt = edtSdt.getText().toString().trim();
                String matkhau = edtMatKhau.getText().toString().trim();
                String xacnhanmatkhau = edtXacNhan.getText().toString().trim();

                // Kiểm tra các trường nhập liệu có trống không
                if (hoten.equals("") || email.equals("") || sdt.equals("") || matkhau.equals("")) {
                    messageObject.ShowDialogMessage(Gravity.CENTER,
                            SignupActivity.this,
                            "Vui lòng điền đầy đủ thông tin của bạn!!",
                            0);
                } else {
                    // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp không
                    if (matkhau.equals(xacnhanmatkhau)) {
                        // Kiểm tra xem email đã được đăng ký chưa
                        Boolean kiemtrataikhoan = DB.checktaikhoan(email);
                        if (kiemtrataikhoan == false) {
                            // Đăng ký người dùng mới trên Firebase
                            mAuth.createUserWithEmailAndPassword(email, matkhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Mở kết nối đến cơ sở dữ liệu SQLite và thêm thông tin người dùng
                                        DB.open();
                                        Boolean insert = DB.insertData(mAuth.getCurrentUser().getUid(), hoten, email, sdt, 0, 1);
                                        DB.close();
                                        btnSignUp.setText(insert.toString());
                                        Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                        // Thêm thông tin người dùng vào Firebase
                                        rootNode = FirebaseDatabase.getInstance();
                                        reference = rootNode.getReference("User");
                                        User newuser = new User(mAuth.getCurrentUser().getUid(), hoten, 0, email, sdt, 1);
                                        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newuser);

                                        // Chuyển đến màn hình đăng nhập sau khi đăng ký thành công
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Hiển thị thông báo khi đăng ký thất bại
                                        messageObject.ShowDialogMessage(Gravity.CENTER,
                                                SignupActivity.this,
                                                "Đăng ký thất bại!!",
                                                0);
                                    }
                                }
                            });
                        } else {
                            // Thông báo nếu email đã được đăng ký
                            messageObject.ShowDialogMessage(Gravity.CENTER,
                                    SignupActivity.this,
                                    "Email đã được đăng ký!!",
                                    0);
                        }
                    } else {
                        // Thông báo khi mật khẩu xác nhận không trùng khớp
                        messageObject.ShowDialogMessage(Gravity.CENTER,
                                SignupActivity.this,
                                "Xác nhận lại, mật khẩu không trùng khớp!!",
                                0);
                        edtMatKhau.setText("");
                        edtXacNhan.setText("");
                    }
                }
            }
        });
    }

    // Hàm ánh xạ các view từ layout
    private void AnhXa() {
        tvDangNhap = (TextView) findViewById(R.id.textView_login);
        edtHoTen = (EditText) findViewById(R.id.editTextEmailNav);
        edtEmail = (EditText) findViewById(R.id.editTextEmail);
        edtSdt = (EditText) findViewById(R.id.editTextSdt);
        edtMatKhau = (EditText) findViewById(R.id.editTextMatKhau);
        edtXacNhan = (EditText) findViewById(R.id.editTextXacNhan);
        btnSignUp = (Button) findViewById(R.id.buttonSignUp);
    }
}




//
//public class SignupActivity extends AppCompatActivity {
//
//    TextView tvDangNhap;
//    EditText edtHoTen, edtEmail, edtSdt, edtMatKhau, edtXacNhan;
//    Button btnSignUp;
//    FirebaseAuth mAuth;
//    FirebaseDatabase rootNode;
//    DatabaseReference reference;
//    private MessageObject messageObject = MessageObject.getInstance();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
//        AnhXa();
//
//        mAuth = FirebaseAuth.getInstance();
//        tvDangNhap.setOnClickListener(v -> {
//            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
//            startActivity(intent);
//        });
//
//        btnSignUp.setOnClickListener(v -> {
//            String hoten = edtHoTen.getText().toString().trim();
//            String email = edtEmail.getText().toString().trim();
//            String sdt = edtSdt.getText().toString().trim();
//            String matkhau = edtMatKhau.getText().toString().trim();
//            String xacnhanmatkhau = edtXacNhan.getText().toString().trim();
//
//            // Kiểm tra thông tin người dùng
//            if (hoten.equals("") || email.equals("") || sdt.equals("") || matkhau.equals("") || xacnhanmatkhau.equals("")) {
//                messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Vui lòng điền đầy đủ thông tin của bạn!!", 0);
//                return;
//            }
//
//            // Kiểm tra mật khẩu xác nhận có trùng khớp không
//            if (!matkhau.equals(xacnhanmatkhau)) {
//                messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Mật khẩu xác nhận không trùng khớp!!", 0);
//                edtMatKhau.setText("");
//                edtXacNhan.setText("");
//                return;
//            }
//
//            // Kiểm tra email đã tồn tại trên Firebase chưa
//            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    if (task.getResult().getSignInMethods().size() > 0) {
//                        messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Email đã được đăng ký!!", 0);
//                    } else {
//                        // Đăng ký người dùng với Firebase Authentication
//                        mAuth.createUserWithEmailAndPassword(email, matkhau).addOnCompleteListener(task1 -> {
//                            if (task1.isSuccessful()) {
//                                // Lưu thông tin người dùng vào Firebase Realtime Database
//                                rootNode = FirebaseDatabase.getInstance();
//                                reference = rootNode.getReference("Users");
//                                String uid = mAuth.getCurrentUser().getUid();
//                                User newUser = new User(uid, hoten, 0, email, sdt, 1);  // Tạo đối tượng User
//                                reference.child(uid).setValue(newUser).addOnCompleteListener(task2 -> {
//                                    if (task2.isSuccessful()) {
//                                        // Đăng ký thành công, chuyển đến màn hình đăng nhập
//                                        Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                                        startActivity(intent);
//                                    } else {
//                                        messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Lỗi khi lưu thông tin vào Firebase", 0);
//                                    }
//                                });
//                            } else {
//                                messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Đăng ký thất bại!!", 0);
//                            }
//                        });
//                    }
//                } else {
//                    messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Lỗi khi kiểm tra email", 0);
//                }
//            });
//        });
//    }
//
//    private void AnhXa() {
//        tvDangNhap = findViewById(R.id.textView_login);
//        edtHoTen = findViewById(R.id.editTextEmailNav);
//        edtEmail = findViewById(R.id.editTextEmail);
//        edtSdt = findViewById(R.id.editTextSdt);
//        edtMatKhau = findViewById(R.id.editTextMatKhau);
//        edtXacNhan = findViewById(R.id.editTextXacNhan);
//        btnSignUp = findViewById(R.id.buttonSignUp);
//    }
//}

//
//public class SignupActivity extends AppCompatActivity {
//
//    TextView tvDangNhap;
//    EditText edtHoTen, edtEmail, edtSdt, edtMatKhau, edtXacNhan;
//    Button btnSignUp;
//    FirebaseAuth mAuth;
//    FirebaseDatabase rootNode;
//    DatabaseReference reference;
//    private MessageObject messageObject = MessageObject.getInstance();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
//        AnhXa();
//
//        mAuth = FirebaseAuth.getInstance();
//        tvDangNhap.setOnClickListener(v -> {
//            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
//            startActivity(intent);
//        });
//
//        btnSignUp.setOnClickListener(v -> {
//            String hoten = edtHoTen.getText().toString().trim();
//            String email = edtEmail.getText().toString().trim();
//            String sdt = edtSdt.getText().toString().trim();
//            String matkhau = edtMatKhau.getText().toString().trim();
//            String xacnhanmatkhau = edtXacNhan.getText().toString().trim();
//
//            // Kiểm tra thông tin người dùng
//            if (hoten.equals("") || email.equals("") || sdt.equals("") || matkhau.equals("") || xacnhanmatkhau.equals("")) {
//                messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Vui lòng điền đầy đủ thông tin của bạn!!", 0);
//                return;
//            }
//
//            // Kiểm tra mật khẩu xác nhận có trùng khớp không
//            if (!matkhau.equals(xacnhanmatkhau)) {
//                messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Mật khẩu xác nhận không trùng khớp!!", 0);
//                edtMatKhau.setText("");
//                edtXacNhan.setText("");
//                return;
//            }
//
//            // Kiểm tra email đã tồn tại trên Firebase chưa
//            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    if (task.getResult().getSignInMethods().size() > 0) {
//                        messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Email đã được đăng ký!!", 0);
//                    } else {
//                        // Đăng ký người dùng với Firebase Authentication
//                        mAuth.createUserWithEmailAndPassword(email, matkhau).addOnCompleteListener(task1 -> {
//                            if (task1.isSuccessful()) {
//                                // Lưu thông tin người dùng vào Firebase Realtime Database
//                                rootNode = FirebaseDatabase.getInstance();
//                                reference = rootNode.getReference("Users");
//                                String uid = mAuth.getCurrentUser().getUid();
//                                User newUser = new User(uid, hoten, 0, email, sdt, 1);  // Tạo đối tượng User
//
//                                // Thêm log lỗi nếu không thành công
//                                reference.child(uid).setValue(newUser).addOnCompleteListener(task2 -> {
//                                    if (task2.isSuccessful()) {
//                                        // Đăng ký thành công, chuyển đến màn hình đăng nhập
//                                        Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                                        startActivity(intent);
//                                    } else {
//                                        Log.e("Firebase", "Lỗi khi lưu thông tin: " + task2.getException().getMessage());
//                                        messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Lỗi khi lưu thông tin vào Firebase", 0);
//                                    }
//                                }).addOnFailureListener(e -> {
//                                    Log.e("Firebase", "Lỗi khi lưu thông tin: " + e.getMessage());
//                                    messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Lỗi khi lưu thông tin vào Firebase", 0);
//                                });
//                            } else {
//                                Log.e("Firebase", "Đăng ký thất bại: " + task1.getException().getMessage());
//                                messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Đăng ký thất bại: " + task1.getException().getMessage(), 0);
//                            }
//                        }).addOnFailureListener(e -> {
//                            Log.e("Firebase", "Lỗi đăng ký: " + e.getMessage());
//                            messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Đăng ký thất bại: " + e.getMessage(), 0);
//                        });
//                    }
//                } else {
//                    messageObject.ShowDialogMessage(Gravity.CENTER, SignupActivity.this, "Lỗi khi kiểm tra email", 0);
//                }
//            });
//        });
//    }
//
//    private void AnhXa() {
//        tvDangNhap = findViewById(R.id.textView_login);
//        edtHoTen = findViewById(R.id.editTextEmailNav);
//        edtEmail = findViewById(R.id.editTextEmail);
//        edtSdt = findViewById(R.id.editTextSdt);
//        edtMatKhau = findViewById(R.id.editTextMatKhau);
//        edtXacNhan = findViewById(R.id.editTextXacNhan);
//        btnSignUp = findViewById(R.id.buttonSignUp);
//    }
//}


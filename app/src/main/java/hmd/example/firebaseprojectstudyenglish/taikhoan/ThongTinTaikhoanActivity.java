package hmd.example.firebaseprojectstudyenglish.taikhoan;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hmd.example.firebaseprojectstudyenglish.MainActivity;
import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.admin.AdminActivity;
import hmd.example.firebaseprojectstudyenglish.database.Database;
import hmd.example.firebaseprojectstudyenglish.database.DatabaseAccess;

public class ThongTinTaikhoanActivity extends AppCompatActivity {

    final  String DATABASE_NAME = "HocNgonNgu.db"; // Định nghĩa tên của cơ sở dữ liệu
    DatabaseAccess DB; // Đối tượng để truy cập cơ sở dữ liệu
    SQLiteDatabase database; // Đối tượng SQLiteDatabase để tương tác với cơ sở dữ liệu SQLite
    ImageView imghome; // Biểu tượng hình ảnh trang chủ
    EditText tvHoten,tvEmail,tvSdt,tvUID; // Các EditText hiển thị thông tin người dùng
    TextView tvtaikhoan, tvTen,tvPoint; // Các TextView hiển thị thông tin người dùng
    Button btnCapNhat; // Nút cập nhật thông tin
    String iduser; // ID người dùng (lấy từ DB)
    User user; // Đối tượng User để lưu thông tin người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_taikhoan); // Set layout activity

        DB = DatabaseAccess.getInstance(getApplicationContext()); // Khởi tạo đối tượng DB
        AnhXa(); // Gọi phương thức AnhXa() để ánh xạ các View
        iduser = DB.iduser; // Lấy ID người dùng từ DatabaseAccess
        LayUser(); // Lấy thông tin người dùng từ cơ sở dữ liệu

        // Xử lý sự kiện click nút cập nhật thông tin
        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CapNhatThongTin(); // Gọi phương thức cập nhật thông tin khi nhấn nút
            }
        });

        // Xử lý sự kiện click vào nút hình ảnh trang chủ
        imghome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getRole() == 0) { // Nếu vai trò là admin
                    Intent intent = new Intent(ThongTinTaikhoanActivity.this, AdminActivity.class); // Mở trang Admin
                    startActivity(intent);
                }
                else { // Nếu là người dùng thông thường
                    Intent intent = new Intent(ThongTinTaikhoanActivity.this, MainActivity.class); // Mở trang chính
                    intent.putExtras(getIntent()); // Truyền thông tin sang MainActivity
                    startActivity(intent);
                    finish(); // Kết thúc Activity hiện tại
                }
            }
        });
    }

    // Phương thức ánh xạ các View trong layout
    private void AnhXa() {
        tvHoten = findViewById(R.id.textIntEdtHoten);
        tvEmail = findViewById(R.id.textIntEdtEmail);
        tvSdt = findViewById(R.id.textIntEdtSdt);
        tvUID = findViewById(R.id.textIntEdtUID);
        tvtaikhoan = findViewById(R.id.tVusername);
        tvTen = findViewById(R.id.textViewTen);
        tvPoint = findViewById(R.id.textviewPoint);
        btnCapNhat = findViewById(R.id.buttonCapNhat);
        imghome = findViewById(R.id.imgHOME);

        // Các trường không cần chỉnh sửa
        tvUID.setEnabled(false);
        tvEmail.setEnabled(false);
    }

    // Phương thức cập nhật thông tin người dùng
    private void CapNhatThongTin() {
        String hoten = tvHoten.getText().toString(); // Lấy tên người dùng
        String sdt = tvSdt.getText().toString(); // Lấy số điện thoại

        if(hoten == "" || sdt == "") { // Kiểm tra xem các trường có trống không
            Toast.makeText(this, "Không hợp lệ", Toast.LENGTH_SHORT).show();
        }
        else {
            Boolean checkupdate = DB.capnhatthongtin(DB.iduser, hoten, sdt); // Gọi phương thức cập nhật thông tin
            if(checkupdate == true) {
                Toast.makeText(this, "Câp nhật thành công", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Phương thức truyền thông tin người dùng vào các trường hiển thị
    private void TruyenThongTin() {
        tvHoten.setText(user.getHoTen());
        tvTen.setText(user.getHoTen());
        tvtaikhoan.setText(user.getEmail());
        tvPoint.setText(String.valueOf(user.getPoint()));
        tvEmail.setText(user.getEmail());
        tvSdt.setText(user.getSDT());
        tvUID.setText(user.getIduser());
    }

    // Phương thức lấy thông tin người dùng từ cơ sở dữ liệu
    public void LayUser() {
        database = Database.initDatabase(ThongTinTaikhoanActivity.this, DATABASE_NAME); // Mở cơ sở dữ liệu
        Cursor cursor = database.rawQuery("SELECT * FROM User WHERE ID_User = ?", new String[]{String.valueOf(DB.iduser)}); // Truy vấn thông tin người dùng
        cursor.moveToNext(); // Di chuyển con trỏ đến dòng đầu tiên
        String Iduser = cursor.getString(0); // Lấy ID người dùng
        String HoTen = cursor.getString(1); // Lấy tên người dùng
        int Point = cursor.getInt(2); // Lấy điểm người dùng
        String Email = cursor.getString(3); // Lấy email
        String SDT = cursor.getString(4); // Lấy số điện thoại
        int Role = cursor.getInt(5); // Lấy vai trò

        user = new User(Iduser, HoTen, Point, Email, SDT, Role); // Khởi tạo đối tượng User với thông tin lấy từ cơ sở dữ liệu
        TruyenThongTin(); // Truyền thông tin vào các trường hiển thị
    }
}

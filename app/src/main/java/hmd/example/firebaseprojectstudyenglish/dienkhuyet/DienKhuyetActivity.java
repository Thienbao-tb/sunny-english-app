package hmd.example.firebaseprojectstudyenglish.dienkhuyet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import hmd.example.firebaseprojectstudyenglish.MainActivity;
import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.bohoctap.BoHocTap;
import hmd.example.firebaseprojectstudyenglish.bohoctap.BoHocTapAdapter;
import hmd.example.firebaseprojectstudyenglish.database.Database;

import java.util.ArrayList;

public class DienKhuyetActivity extends AppCompatActivity {

    ListView listView; // Đối tượng ListView để hiển thị danh sách các bộ câu hỏi
    ImageView imgback; // Nút quay lại
    ArrayList<BoHocTap> boHocTapArrayList; // Danh sách các bộ câu hỏi
    BoHocTapAdapter boHocTapAdapter; // Adapter để gắn dữ liệu vào ListView
    final String DATABASE_NAME = "HocNgonNgu.db"; // Tên cơ sở dữ liệu
    SQLiteDatabase database; // Đối tượng SQLiteDatabase để thao tác với cơ sở dữ liệu
    int idbocauhoi; // ID của bộ câu hỏi đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dien_khuyet); // Gán layout cho Activity

        listView = findViewById(R.id.lvdienkhuyet); // Ánh xạ ListView từ giao diện
        imgback = findViewById(R.id.imgVBackDK); // Ánh xạ ImageView (nút quay lại)
        boHocTapArrayList = new ArrayList<>(); // Khởi tạo danh sách các bộ câu hỏi
        AddArrayBTN(); // Gọi hàm để thêm bộ câu hỏi vào danh sách
        boHocTapAdapter = new BoHocTapAdapter(DienKhuyetActivity.this, R.layout.row_bodienkhuyet, boHocTapArrayList); // Khởi tạo Adapter
        listView.setAdapter(boHocTapAdapter); // Gắn Adapter vào ListView
        boHocTapAdapter.notifyDataSetChanged(); // Thông báo Adapter để cập nhật giao diện

        // Xử lý sự kiện khi người dùng nhấn nút quay lại
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DienKhuyetActivity.this, MainActivity.class); // Quay lại màn hình chính
                intent.putExtras(getIntent()); // Truyền dữ liệu của Intent hiện tại
                startActivity(intent); // Bắt đầu Activity mới
                finish(); // Kết thúc Activity hiện tại
            }
        });

        // Xử lý sự kiện khi người dùng chọn một bộ câu hỏi từ danh sách
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                database = Database.initDatabase(DienKhuyetActivity.this, DATABASE_NAME); // Khởi tạo cơ sở dữ liệu
                String a = null;
                Cursor cursor = database.rawQuery("SELECT * FROM BoCauHoi", null); // Lấy tất cả bộ câu hỏi
                for (int i = position; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i); // Di chuyển đến vị trí của câu hỏi đã chọn
                    int idbo = cursor.getInt(0); // Lấy ID bộ câu hỏi
                    String tenbo = cursor.getString(2); // Lấy tên bộ câu hỏi
                    a = tenbo; // Gán tên bộ câu hỏi vào biến
                    idbocauhoi = idbo; // Gán ID bộ câu hỏi đã chọn
                    break;
                }

                // Chuyển sang màn hình điền khuyết và truyền ID bộ câu hỏi
                Intent quiz = new Intent(DienKhuyetActivity.this, FillBlanksActivity.class);
                quiz.putExtra("BoDK", idbocauhoi); // Truyền ID bộ câu hỏi vào Intent
                startActivity(quiz); // Bắt đầu Activity FillBlanksActivity
            }
        });
    }

    // Hàm để thêm các bộ câu hỏi vào danh sách
    private void AddArrayBTN() {
        database = Database.initDatabase(DienKhuyetActivity.this, DATABASE_NAME); // Khởi tạo cơ sở dữ liệu
        Cursor cursor = database.rawQuery("SELECT * FROM BoCauHoi", null); // Lấy tất cả bộ câu hỏi
        boHocTapArrayList.clear(); // Xóa danh sách cũ

        // Duyệt qua tất cả các bộ câu hỏi và thêm vào danh sách
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i); // Di chuyển đến vị trí i
            int idbo = cursor.getInt(0); // Lấy ID bộ câu hỏi
            int stt = cursor.getInt(1); // Lấy số thứ tự bộ câu hỏi
            String tenbo = cursor.getString(2); // Lấy tên bộ câu hỏi
            boHocTapArrayList.add(new BoHocTap(idbo, stt, tenbo)); // Thêm bộ câu hỏi vào danh sách
        }
    }
}

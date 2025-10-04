package hmd.example.firebaseprojectstudyenglish.luyennghe;

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

public class LuyenNgheActivity extends AppCompatActivity {
    ListView listView;
    ImageView imgback;
    ArrayList<BoHocTap> boCauHoiArrayList;
    BoHocTapAdapter boCauHoiAdapter;
    final  String DATABASE_NAME = "HocNgonNgu.db";  // Tên cơ sở dữ liệu
    SQLiteDatabase database;
    int idbocauhoi;  // ID của bộ câu hỏi được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luyen_nghe);

        // Ánh xạ các view
        listView = findViewById(R.id.lvluyennghe);
        imgback = findViewById(R.id.imgVBackLN);

        // Khởi tạo danh sách bộ câu hỏi và adapter
        boCauHoiArrayList = new ArrayList<>();
        AddArrayBLN();  // Thêm bộ câu hỏi vào danh sách
        boCauHoiAdapter = new BoHocTapAdapter(LuyenNgheActivity.this, R.layout.row_boluyennghe, boCauHoiArrayList);
        listView.setAdapter(boCauHoiAdapter);  // Gán adapter cho ListView
        boCauHoiAdapter.notifyDataSetChanged();  // Cập nhật dữ liệu trong ListView

        // Xử lý sự kiện khi người dùng chọn một bộ câu hỏi trong ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                database = Database.initDatabase(LuyenNgheActivity.this, DATABASE_NAME);  // Khởi tạo cơ sở dữ liệu
                String a = null;
                Cursor cursor = database.rawQuery("SELECT * FROM BoCauHoi", null);  // Truy vấn dữ liệu từ bảng BoCauHoi

                // Duyệt qua các bộ câu hỏi và lấy thông tin của bộ câu hỏi tại vị trí đã chọn
                for (int i = position; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    int idbo = cursor.getInt(0);
                    int stt = cursor.getInt(1);
                    String tenbo = cursor.getString(2);
                    a = tenbo;  // Lưu tên bộ câu hỏi
                    idbocauhoi = idbo;  // Lưu ID bộ câu hỏi
                    break;
                }

                // Mở Activity ListeningActivity và truyền ID bộ câu hỏi vào Intent
                Intent quiz = new Intent(LuyenNgheActivity.this, ListeningActivity.class);
                quiz.putExtra("Bo", idbocauhoi);
                startActivity(quiz);  // Bắt đầu Activity ListeningActivity
            }
        });

        // Xử lý sự kiện khi người dùng nhấn nút quay lại
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình chính (MainActivity)
                Intent intent = new Intent(LuyenNgheActivity.this, MainActivity.class);
                intent.putExtras(getIntent());  // Truyền dữ liệu từ Activity hiện tại sang MainActivity
                startActivity(intent);  // Bắt đầu Activity MainActivity
                finish();  // Kết thúc Activity hiện tại
            }
        });
    }

    // Phương thức để thêm các bộ câu hỏi vào danh sách boCauHoiArrayList
    private void AddArrayBLN() {
        database = Database.initDatabase(LuyenNgheActivity.this, DATABASE_NAME);  // Khởi tạo cơ sở dữ liệu
        Cursor cursor = database.rawQuery("SELECT * FROM BoCauHoi", null);  // Truy vấn tất cả các bộ câu hỏi từ bảng BoCauHoi
        boCauHoiArrayList.clear();  // Xóa dữ liệu cũ trong danh sách

        // Duyệt qua các kết quả truy vấn và thêm vào danh sách boCauHoiArrayList
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int idbo = cursor.getInt(0);
            int stt = cursor.getInt(1);
            String tenbo = cursor.getString(2);
            boCauHoiArrayList.add(new BoHocTap(idbo, stt, tenbo));  // Thêm bộ câu hỏi vào danh sách
        }
    }
}

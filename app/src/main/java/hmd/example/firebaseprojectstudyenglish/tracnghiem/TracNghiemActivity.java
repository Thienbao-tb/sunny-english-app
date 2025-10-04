package hmd.example.firebaseprojectstudyenglish.tracnghiem;

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

public class TracNghiemActivity extends AppCompatActivity {

    ListView listView;  // ListView để hiển thị các bộ câu hỏi
    ArrayList<BoHocTap> boHocTapArrayList;  // Danh sách các bộ câu hỏi
    BoHocTapAdapter boHocTapAdapter;  // Adapter để gắn dữ liệu vào ListView
    ImageView imgback;  // Nút quay lại
    final  String DATABASE_NAME = "HocNgonNgu.db";  // Tên cơ sở dữ liệu
    SQLiteDatabase database;  // Cơ sở dữ liệu SQLite
    int idbocauhoi;  // ID của bộ câu hỏi khi người dùng chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trac_nghiem);  // Gắn layout vào Activity

        // Khởi tạo các view
        listView = findViewById(R.id.lvtracnghiem);
        imgback = findViewById(R.id.imgVBackTN);

        // Khởi tạo danh sách bộ câu hỏi
        boHocTapArrayList = new ArrayList<>();

        // Thêm dữ liệu vào danh sách bộ câu hỏi
        AddArrayBTN();

        // Tạo và gắn adapter vào ListView
        boHocTapAdapter = new BoHocTapAdapter(TracNghiemActivity.this, R.layout.row_botracnghiem, boHocTapArrayList);
        listView.setAdapter(boHocTapAdapter);
        boHocTapAdapter.notifyDataSetChanged();  // Cập nhật lại adapter

        // Xử lý sự kiện khi người dùng chọn một bộ câu hỏi
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Mở cơ sở dữ liệu và lấy thông tin của bộ câu hỏi được chọn
                database = Database.initDatabase(TracNghiemActivity.this, DATABASE_NAME);
                String a = null;
                Cursor cursor = database.rawQuery("SELECT * FROM BoCauHoi", null);

                // Duyệt qua các bộ câu hỏi và lấy thông tin của bộ câu hỏi tại vị trí người dùng chọn
                for (int i = position; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    int idbo = cursor.getInt(0);
                    int stt = cursor.getInt(1);
                    String tenbo = cursor.getString(2);
                    a = tenbo;
                    idbocauhoi = idbo;  // Lưu ID của bộ câu hỏi
                    break;
                }

                // Mở QuizActivity để bắt đầu làm bài kiểm tra với bộ câu hỏi đã chọn
                Intent quiz = new Intent(TracNghiemActivity.this, QuizActivity.class);
                quiz.putExtra("Bo", idbocauhoi);  // Truyền ID bộ câu hỏi vào QuizActivity
                startActivity(quiz);  // Bắt đầu QuizActivity
            }
        });

        // Xử lý sự kiện khi người dùng nhấn nút quay lại
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại MainActivity
                Intent intent = new Intent(TracNghiemActivity.this, MainActivity.class);
                intent.putExtras(getIntent());  // Truyền dữ liệu từ Activity trước
                startActivity(intent);  // Mở MainActivity
                finish();  // Kết thúc TracNghiemActivity
            }
        });
    }

    // Phương thức thêm dữ liệu vào danh sách bộ câu hỏi
    private void AddArrayBTN() {
        // Mở cơ sở dữ liệu và truy vấn các bộ câu hỏi
        database = Database.initDatabase(TracNghiemActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM BoCauHoi", null);
        boHocTapArrayList.clear();  // Xóa dữ liệu cũ trong danh sách

        // Duyệt qua các bộ câu hỏi và thêm vào danh sách
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int idbo = cursor.getInt(0);  // ID bộ câu hỏi
            int stt = cursor.getInt(1);  // Số thứ tự bộ câu hỏi
            String tenbo = cursor.getString(2);  // Tên bộ câu hỏi
            boHocTapArrayList.add(new BoHocTap(idbo, stt, tenbo));  // Thêm bộ câu hỏi vào danh sách
        }
    }
}

package hmd.example.firebaseprojectstudyenglish.hoctuvung;

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

// Lớp HocTuVungActivity hiển thị danh sách các bộ từ vựng và cho phép người dùng chọn bộ từ vựng để học.
public class HocTuVungActivity extends AppCompatActivity {

    final  String DATABASE_NAME = "HocNgonNgu.db"; // Tên của cơ sở dữ liệu
    SQLiteDatabase database; // Đối tượng SQLiteDatabase để truy cập cơ sở dữ liệu
    ImageView imgback; // Biến hình ảnh dùng cho nút quay lại (back)

    ArrayList<BoHocTap> boTuVungs; // Danh sách các bộ từ vựng
    BoHocTapAdapter adapter; // Adapter để hiển thị danh sách bộ từ vựng trong ListView
    ListView botuvungs; // ListView hiển thị danh sách bộ từ vựng

    int idbo; // ID của bộ từ vựng được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoctuvung); // Gán layout cho Activity

        // Khởi tạo các view
        botuvungs = findViewById(R.id.listviewHTV);
        imgback = findViewById(R.id.imgVBackHTV);

        // Khởi tạo danh sách bộ từ vựng
        boTuVungs = new ArrayList<>();
        AddArrayBTV(); // Thêm các bộ từ vựng vào danh sách từ cơ sở dữ liệu

        // Tạo adapter để liên kết dữ liệu với ListView
        adapter = new BoHocTapAdapter(HocTuVungActivity.this, R.layout.row_botuvung, boTuVungs);
        botuvungs.setAdapter(adapter); // Thiết lập adapter cho ListView
        adapter.notifyDataSetChanged(); // Thông báo cho adapter cập nhật dữ liệu

        // Thiết lập sự kiện click cho từng mục trong ListView
        botuvungs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idbo = boTuVungs.get(position).getIdBo(); // Lấy ID bộ từ vựng khi người dùng chọn
                Intent dstv = new Intent(HocTuVungActivity.this, DSTuVungActivity.class); // Mở Activity hiển thị danh sách từ vựng
                dstv.putExtra("idbo", idbo); // Truyền ID bộ từ vựng sang Activity tiếp theo
                startActivity(dstv); // Bắt đầu Activity mới
            }
        });

        // Thiết lập sự kiện click cho nút quay lại
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HocTuVungActivity.this, MainActivity.class); // Trở lại màn hình chính
                startActivity(intent);
            }
        });
    }

    // Hàm AddArrayBTV dùng để thêm các bộ từ vựng vào danh sách từ cơ sở dữ liệu
    private void AddArrayBTV(){
        database = Database.initDatabase(HocTuVungActivity.this, DATABASE_NAME); // Mở kết nối với cơ sở dữ liệu
        Cursor cursor = database.rawQuery("SELECT * FROM BoCauHoi", null); // Truy vấn tất cả bộ câu hỏi
        boTuVungs.clear(); // Xóa danh sách bộ từ vựng cũ

        // Duyệt qua các bộ từ vựng và thêm chúng vào danh sách
        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i); // Di chuyển đến vị trí của bộ từ vựng
            int idbo = cursor.getInt(0); // Lấy ID bộ từ vựng
            int stt = cursor.getInt(1); // Lấy thứ tự của bộ
            String tenbo = cursor.getString(2); // Lấy tên bộ từ vựng
            boTuVungs.add(new BoHocTap(idbo, stt, tenbo)); // Thêm bộ từ vựng vào danh sách
        }
    }
}

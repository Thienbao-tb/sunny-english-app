package hmd.example.firebaseprojectstudyenglish.sapxepcau;

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

public class SapXepCauActivity extends AppCompatActivity {

    final  String DATABASE_NAME = "HocNgonNgu.db";  // Tên cơ sở dữ liệu
    SQLiteDatabase database;  // Đối tượng để kết nối và thao tác với cơ sở dữ liệu
    ImageView imgback;  // Biến cho nút quay lại

    ArrayList<BoHocTap> boTuVungs;  // Danh sách các bộ học tập (BoHocTap)
    BoHocTapAdapter adapter;  // Bộ adapter để hiển thị danh sách
    ListView botuvungs;  // ListView để hiển thị các bộ học tập

    int idbo;  // ID của bộ học tập khi người dùng chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sapxepcau);  // Gắn layout vào Activity
        botuvungs = findViewById(R.id.listviewSXC);  // Ánh xạ ListView
        imgback = findViewById(R.id.imgVBackSXC);  // Ánh xạ ImageView quay lại
        boTuVungs = new ArrayList<>();  // Khởi tạo danh sách các bộ học tập
        AddArrayBTV();  // Thêm các bộ học tập vào danh sách
        adapter = new BoHocTapAdapter(SapXepCauActivity.this, R.layout.row_bosapxepcau, boTuVungs);  // Khởi tạo adapter
        botuvungs.setAdapter(adapter);  // Gắn adapter vào ListView
        adapter.notifyDataSetChanged();  // Cập nhật giao diện của ListView

        // Lắng nghe sự kiện khi người dùng chọn một bộ học tập từ danh sách
        botuvungs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idbo = boTuVungs.get(position).getIdBo();  // Lấy ID bộ học tập đã chọn
                Intent sxc = new Intent(SapXepCauActivity.this, ArrangeSentencesActivity.class);  // Chuyển đến màn hình xếp câu
                sxc.putExtra("idbo", idbo);  // Truyền ID bộ học tập đến màn hình xếp câu
                startActivity(sxc);  // Bắt đầu Activity
            }
        });

        // Lắng nghe sự kiện khi người dùng nhấn nút quay lại
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SapXepCauActivity.this, MainActivity.class);  // Quay lại MainActivity
                intent.putExtras(getIntent());  // Truyền dữ liệu của Intent hiện tại
                startActivity(intent);  // Bắt đầu Activity
                finish();  // Kết thúc Activity hiện tại
            }
        });
    }

    // Phương thức để thêm các bộ học tập vào danh sách từ cơ sở dữ liệu
    private void AddArrayBTV() {
        database = Database.initDatabase(SapXepCauActivity.this, DATABASE_NAME);  // Kết nối cơ sở dữ liệu
        Cursor cursor = database.rawQuery("SELECT * FROM BoCauHoi", null);  // Truy vấn lấy tất cả các bộ câu hỏi
        boTuVungs.clear();  // Xóa danh sách cũ trước khi thêm mới

        // Duyệt qua các bộ học tập trong cơ sở dữ liệu và thêm vào danh sách boTuVungs
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);  // Di chuyển con trỏ đến vị trí i
            int idbo = cursor.getInt(0);  // Lấy ID bộ câu hỏi
            int stt = cursor.getInt(1);  // Lấy số thứ tự bộ câu hỏi
            String tenbo = cursor.getString(2);  // Lấy tên bộ câu hỏi
            boTuVungs.add(new BoHocTap(idbo, stt, tenbo));  // Thêm bộ học tập vào danh sách
        }
    }
}

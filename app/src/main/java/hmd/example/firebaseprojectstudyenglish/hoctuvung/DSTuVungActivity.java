package hmd.example.firebaseprojectstudyenglish.hoctuvung;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.database.Database;

import java.util.ArrayList;

public class DSTuVungActivity extends AppCompatActivity {

    final String DATABASE_NAME = "HocNgonNgu.db";
    SQLiteDatabase database;
    ImageView imgback;

    GridView dstuvungs;
    Button Ontap;
    ArrayList<TuVung> DStuvung;
    DSTuVungAdapter adapter;

    int idbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ds_tuvung);

        // Lấy ID bộ từ vựng từ Intent
        Intent intent = getIntent();
        idbo = intent.getIntExtra("idbo", 0);

        // Khởi tạo các view
        dstuvungs = findViewById(R.id.lgvTuVung);
        Ontap = findViewById(R.id.btnOnTap);
        imgback = findViewById(R.id.imgVBackDSTV);
        DStuvung = new ArrayList<>();
        AddArrayTV();

        // Nếu không có từ vựng, thông báo và quay lại màn hình trước
        if (DStuvung.size() <= 0) {
            Toast.makeText(DSTuVungActivity.this, "Nội dung sẽ cập nhật trong thời gian sớm nhất! Mong mọi người thông cảm!!", Toast.LENGTH_LONG).show();
            Intent error = new Intent(DSTuVungActivity.this, HocTuVungActivity.class);
            finish();
            startActivity(error);
        } else {
            // Thiết lập adapter cho GridView
            adapter = new DSTuVungAdapter(DSTuVungActivity.this, R.layout.row_dstuvung, DStuvung);
            dstuvungs.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            // Sự kiện quay lại màn hình trước
            imgback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent
                            = new Intent(DSTuVungActivity.this,
                            HocTuVungActivity.class);
                    startActivity(intent);
                }
            });


            Ontap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent ontap = new Intent(DSTuVungActivity.this, WordMattersActivity.class);
                    ontap.putExtra("idbo", idbo);
                    startActivity(ontap);
                }
            });
        }
    }

    // Thêm dữ liệu vào danh sách từ vựng
    private void AddArrayTV() {
        database = Database.initDatabase(DSTuVungActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM TuVung WHERE ID_Bo = ?", new String[]{String.valueOf(idbo)});
        DStuvung.clear();

        // Duyệt qua các kết quả từ Cursor và tạo đối tượng TuVung
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int idtu = cursor.getInt(0);
            int idbo = cursor.getInt(1);
            String dapan = cursor.getString(2);
            String dichnghia = cursor.getString(3);
            String loaitu = cursor.getString(4);
            String audio = cursor.getString(5);
            byte[] anh = cursor.getBlob(6);

            // Thêm đối tượng TuVung vào danh sách
            DStuvung.add(new TuVung(idtu, idbo, dapan, dichnghia, loaitu, audio, anh));
        }
    }
}

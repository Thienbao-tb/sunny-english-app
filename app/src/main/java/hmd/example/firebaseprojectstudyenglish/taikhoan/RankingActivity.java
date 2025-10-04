package hmd.example.firebaseprojectstudyenglish.taikhoan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hmd.example.firebaseprojectstudyenglish.MainActivity;
import hmd.example.firebaseprojectstudyenglish.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class RankingActivity extends AppCompatActivity {

    RecyclerView recyclerView; // RecyclerView để hiển thị danh sách người dùng
    AdapterRank adapterRank; // Adapter để gắn dữ liệu vào RecyclerView
    TextView tvRank, tvTest; // Các TextView (không sử dụng trong mã này)
    TextView tvPointrank1, tvPointrank2, tvPointrank3; // Các TextView hiển thị điểm của top 3 người dùng
    ImageView imgback; // Hình ảnh nút quay lại
    Query database; // Truy vấn dữ liệu từ Firebase
    ArrayList<UserRanking> list; // Danh sách người dùng xếp hạng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking); // Set layout cho activity

        AnhXa(); // Gọi phương thức ánh xạ các View

        // Truy vấn dữ liệu từ Firebase để lấy thông tin người dùng, sắp xếp theo điểm
        database = FirebaseDatabase.getInstance().getReference("User").orderByChild("point");

        // Thiết lập RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách và Adapter
        list = new ArrayList<>();
        adapterRank = new AdapterRank(this, list);
        recyclerView.setAdapter(adapterRank);

        // Lắng nghe sự thay đổi dữ liệu từ Firebase
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // Duyệt qua dữ liệu và thêm vào danh sách
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserRanking userRanking = dataSnapshot.getValue(UserRanking.class);
                    list.add(userRanking);
                }
                // Đảo ngược danh sách để xếp hạng từ cao xuống thấp
                Collections.reverse(list);

                // Cập nhật điểm của top 3 người dùng
                Top3point();

                // Cập nhật lại RecyclerView
                adapterRank.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                // Xử lý khi có lỗi trong quá trình đọc dữ liệu từ Firebase
            }
        });

        // Xử lý sự kiện click nút quay lại
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại MainActivity khi nhấn nút quay lại
                Intent intent = new Intent(RankingActivity.this, MainActivity.class);
                intent.putExtras(getIntent()); // Truyền dữ liệu giữa các activity
                startActivity(intent);
                finish(); // Kết thúc activity hiện tại
            }
        });
    }

    // Phương thức ánh xạ các View từ layout vào mã
    private void AnhXa() {
        recyclerView = findViewById(R.id.userranklist);
        tvPointrank1 = findViewById(R.id.tvpointrank1);
        tvPointrank2 = findViewById(R.id.tvpointrank2);
        tvPointrank3 = findViewById(R.id.tvpointrank3);
        imgback = findViewById(R.id.imgVBackRank);
    }

    // Phương thức cập nhật điểm của top 3 người dùng vào TextView
    private void Top3point() {
        tvPointrank1.setText(String.valueOf(list.get(0).getPoint()));
        tvPointrank2.setText(String.valueOf(list.get(1).getPoint()));
        tvPointrank3.setText(String.valueOf(list.get(2).getPoint()));
    }
}

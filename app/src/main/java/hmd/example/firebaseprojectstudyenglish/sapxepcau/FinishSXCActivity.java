package hmd.example.firebaseprojectstudyenglish.sapxepcau;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import hmd.example.firebaseprojectstudyenglish.MainActivity;
import hmd.example.firebaseprojectstudyenglish.R;

public class FinishSXCActivity extends AppCompatActivity {

    TextView txtcongrats, txtfinalqtrue, txtfinaltext, txtfinalScore;  // Các TextView để hiển thị kết quả
    Button btnReturn;  // Nút để quay lại màn hình chính
    ImageView imgtrophy;  // Hình ảnh cup chiến thắng với hiệu ứng hoạt hình
    final String DATABASE_NAME = "HocNgonNgu.db";  // Tên cơ sở dữ liệu
    SQLiteDatabase database;  // Đối tượng SQLiteDatabase để thao tác với cơ sở dữ liệu
    int score;  // Điểm người chơi đạt được
    int questiontrue;  // Số câu trả lời đúng
    int qcount;  // Tổng số câu hỏi
    Animation smalltobig;  // Hiệu ứng hoạt hình thay đổi kích thước hình ảnh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_hoctap);  // Gắn layout vào Activity

        // Tải hiệu ứng hoạt hình từ file XML
        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);

        // Lấy dữ liệu từ Intent (điểm, số câu trả lời đúng, tổng số câu hỏi)
        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);
        questiontrue = intent.getIntExtra("questiontrue", 0);
        qcount = intent.getIntExtra("qcount", 0);

        // Ánh xạ các thành phần giao diện
        Anhxa();

        // Cập nhật các TextView với kết quả
        if (questiontrue == 4) {
            txtfinalqtrue.setText(questiontrue + " / " + qcount);  // Hiển thị số câu trả lời đúng / tổng câu hỏi
            txtcongrats.setText("Your final result: ");  // Thông báo kết quả
            txtfinaltext.setText("Almost there!!");  // Câu chúc mừng
            txtfinalScore.setText(" " + score);  // Hiển thị điểm
        }

        // Nếu số câu trả lời đúng ít hơn 4
        if (questiontrue < 4) {
            txtfinalqtrue.setText(questiontrue + " / " + qcount);  // Hiển thị số câu trả lời đúng / tổng câu hỏi
            txtcongrats.setText("Your final result: ");  // Thông báo kết quả
            txtfinaltext.setText("Good luck next time !!");  // Lời chúc động viên
            txtfinalScore.setText(" " + score);  // Hiển thị điểm
        }

        // Sự kiện khi nhấn nút quay lại
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình chính (MainActivity)
                startActivity(new Intent(FinishSXCActivity.this, MainActivity.class));
            }
        });
    }

    // Phương thức ánh xạ các thành phần giao diện
    public void Anhxa() {
        txtfinalScore = findViewById(R.id.tvPoints);  // Ánh xạ TextView điểm
        txtcongrats = findViewById(R.id.txtcongrats);  // Ánh xạ TextView lời chúc mừng
        txtfinalqtrue = findViewById(R.id.txtquestiontrue);  // Ánh xạ TextView số câu đúng
        txtfinaltext = findViewById(R.id.txtfinaltext);  // Ánh xạ TextView thông điệp cuối cùng
        btnReturn = findViewById(R.id.btnReturn);  // Ánh xạ nút quay lại
        imgtrophy = findViewById(R.id.imgtrophy);  // Ánh xạ hình ảnh cup chiến thắng

        // Áp dụng hiệu ứng hoạt hình cho hình ảnh cup
        imgtrophy.startAnimation(smalltobig);
    }
}

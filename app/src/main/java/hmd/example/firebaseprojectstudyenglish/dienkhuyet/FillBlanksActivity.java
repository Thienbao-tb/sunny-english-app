package hmd.example.firebaseprojectstudyenglish.dienkhuyet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.database.Database;
import hmd.example.firebaseprojectstudyenglish.database.DatabaseAccess;
import hmd.example.firebaseprojectstudyenglish.taikhoan.User;

import java.util.ArrayList;

public class FillBlanksActivity extends AppCompatActivity {

    final  String DATABASE_NAME = "HocNgonNgu.db";
    SQLiteDatabase database;
    DatabaseAccess DB;
    TextView txtscoreDK, txtquestcountDK, txtquestionDK, txttimeDK, txtGoiy;
    EditText edtAnswerDK;
    Button btnconfirm, btnQuit;
    private ArrayList<CauDienKhuyet> cauDienKhuyets; // Danh sách câu hỏi điền khuyết
    int questioncurrent = 0; // Câu hỏi hiện tại
    int questiontrue = 0; // Số câu trả lời đúng
    String answer; // Đáp án đúng cho câu hỏi
    int score = 0; // Điểm số
    int idbo; // ID bộ câu hỏi

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_blanks);
        DB = DatabaseAccess.getInstance(getApplicationContext()); // Khởi tạo DatabaseAccess

        Anhxa(); // Ánh xạ các thành phần giao diện
        LayUser(); // Lấy thông tin người dùng

        // Nhận ID bộ câu hỏi từ Intent
        Intent intent = getIntent();
        idbo = intent.getIntExtra("BoDK", 0);
        txttimeDK.setText(" "); // Đặt lại thời gian ban đầu

        cauDienKhuyets = new ArrayList<>();
        AddArrayCDK(); // Thêm câu hỏi từ cơ sở dữ liệu

        // Kiểm tra nếu không có câu hỏi nào
        if (cauDienKhuyets.size() <= 0) {
            Toast.makeText(FillBlanksActivity.this, "Nội dung sẽ cập nhật trong thời gian sớm nhất! Mong mọi người thông cảm!", Toast.LENGTH_LONG).show();
            Intent error = new Intent(FillBlanksActivity.this, DienKhuyetActivity.class);
            finish();
            startActivity(error);
        } else {
            // Hiển thị câu hỏi đầu tiên
            shownextquestion(questioncurrent, cauDienKhuyets);

            // Đếm ngược thời gian cho câu hỏi
            CountDownTimer countDownTimer = new CountDownTimer(3000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    questioncurrent++; // Chuyển sang câu hỏi tiếp theo
                    edtAnswerDK.setTextColor(Color.BLACK); // Đặt lại màu sắc của ô nhập đáp án
                    btnconfirm.setEnabled(true); // Kích hoạt nút xác nhận
                    edtAnswerDK.setText(""); // Xóa ô nhập đáp án
                    answer = ""; // Xóa đáp án đúng
                    shownextquestion(questioncurrent, cauDienKhuyets); // Hiển thị câu hỏi tiếp theo
                }
            };

            // Khi người dùng nhấn nút xác nhận
            btnconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer(); // Kiểm tra câu trả lời
                    showanswer(); // Hiển thị đáp án
                    countDownTimer.start(); // Bắt đầu đếm ngược cho câu hỏi tiếp theo
                }
            });

            // Khi người dùng nhấn nút thoát
            btnQuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FillBlanksActivity.this, DienKhuyetActivity.class);
                    startActivity(intent); // Quay lại màn hình chính
                }
            });
        }
    }

    // Lấy thông tin người dùng từ cơ sở dữ liệu
    public void LayUser() {
        database = Database.initDatabase(FillBlanksActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM User WHERE ID_User = ?", new String[]{String.valueOf(DB.iduser)});
        cursor.moveToNext();
        String Iduser = cursor.getString(0);
        String HoTen = cursor.getString(1);
        int Point = cursor.getInt(2);
        String Email = cursor.getString(3);
        String SDT = cursor.getString(4);
        int Role = cursor.getInt(5);
        user = new User(Iduser, HoTen, Point, Email, SDT, Role); // Tạo đối tượng User
    }

    // Thêm câu hỏi vào danh sách
    private void AddArrayCDK() {
        database = Database.initDatabase(FillBlanksActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM DienKhuyet WHERE ID_Bo = ?", new String[]{String.valueOf(idbo)});
        cauDienKhuyets.clear(); // Xóa danh sách câu hỏi cũ

        // Đọc các câu hỏi từ cơ sở dữ liệu và thêm vào danh sách
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int idcau = cursor.getInt(0);
            int idbo = cursor.getInt(1);
            String noidung = cursor.getString(2);
            String dapan = cursor.getString(3);
            String goiy = cursor.getString(4);

            // Thêm câu hỏi vào danh sách
            cauDienKhuyets.add(new CauDienKhuyet(idcau, idbo, noidung, dapan, goiy));
        }
    }

    // Hiển thị câu hỏi tiếp theo
    public void shownextquestion(int pos, ArrayList<CauDienKhuyet> cauDienKhuyets) {
        txtquestcountDK.setText("Question: " + (questioncurrent + 1) + "/" + cauDienKhuyets.size());
        edtAnswerDK.setBackground(this.getResources().getDrawable(R.drawable.bgbtn));

        // Nếu đã hết câu hỏi
        if (pos == cauDienKhuyets.size()) {
            // Cập nhật điểm và chuyển sang màn hình kết thúc
            DB.capnhatdiem(DB.iduser, user.getPoint(), score);
            Intent intent = new Intent(FillBlanksActivity.this, FinishDKActivity.class);
            intent.putExtra("scoreDK", score);
            intent.putExtra("questiontrueDK", questiontrue);
            intent.putExtra("qcountDK", pos);
            startActivity(intent);
        } else {
            // Hiển thị câu hỏi hiện tại
            answer = cauDienKhuyets.get(pos).getDapan();
            txtGoiy.setText(cauDienKhuyets.get(pos).getGoiy());
            txtquestionDK.setText(cauDienKhuyets.get(pos).getNoidung());
        }
    }

    // Hiển thị đáp án đúng
    public void showanswer() {
        edtAnswerDK.setText(answer); // Điền đáp án đúng vào ô
        edtAnswerDK.setTextColor(Color.GREEN); // Đặt màu sắc là xanh
        edtAnswerDK.clearFocus(); // Mất focus ô nhập
    }

    // Kiểm tra câu trả lời của người dùng
    public void checkAnswer() {
        btnconfirm.setEnabled(false); // Vô hiệu hóa nút xác nhận
        if (answer.equals(edtAnswerDK.getText().toString())) {
            // Nếu câu trả lời đúng
            Toast.makeText(this, "Đáp án chính xác", Toast.LENGTH_SHORT).show();
            edtAnswerDK.setTextColor(Color.GREEN); // Đặt màu xanh cho ô nhập
            questiontrue++; // Tăng số câu đúng
            score += 5; // Cộng điểm
            txtscoreDK.setText("Score: " + score); // Hiển thị điểm
            edtAnswerDK.clearFocus(); // Mất focus ô nhập
        } else {
            // Nếu câu trả lời sai
            Toast.makeText(this, "Sai rồi", Toast.LENGTH_SHORT).show();
            edtAnswerDK.setTextColor(Color.RED); // Đặt màu đỏ cho ô nhập
            edtAnswerDK.startAnimation(shakeError()); // Thực hiện animation lắc
            edtAnswerDK.clearFocus(); // Mất focus ô nhập
        }
    }

    // Animation lắc ô nhập đáp án khi sai
    public TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7)); // Lắc 7 lần
        return shake;
    }

    // Ánh xạ các thành phần giao diện
    public void Anhxa() {
        txtscoreDK = findViewById(R.id.txtscoreDK);
        txtquestcountDK = findViewById(R.id.txtquestcountDK);
        txtquestionDK = findViewById(R.id.txtquestionDK);
        txttimeDK = findViewById(R.id.txttimeDK);
        edtAnswerDK = findViewById(R.id.AnswerDK);
        btnconfirm = findViewById(R.id.btnconfirmDK);
        txtGoiy = findViewById(R.id.textviewGoiy);
        btnQuit = findViewById(R.id.btnQuitDK);
    }
}

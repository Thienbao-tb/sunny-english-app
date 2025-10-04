package hmd.example.firebaseprojectstudyenglish.tracnghiem;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.database.Database;
import hmd.example.firebaseprojectstudyenglish.database.DatabaseAccess;
import hmd.example.firebaseprojectstudyenglish.taikhoan.User;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    final  String DATABASE_NAME = "HocNgonNgu.db";  // Tên cơ sở dữ liệu
    SQLiteDatabase database;  // Đối tượng SQLiteDatabase để thao tác với cơ sở dữ liệu
    DatabaseAccess DB;  // Đối tượng truy cập cơ sở dữ liệu
    TextView txtscore, txtquestcount, txtquestion, txttime;  // Các view hiển thị điểm số, số câu hỏi, câu hỏi và thời gian
    RadioGroup rdgchoices;  // Nhóm các lựa chọn câu trả lời
    RadioButton btnop1, btnop2, btnop3, btnop4;  // Các nút radio cho từng lựa chọn
    Button btnconfirm, btnquit;  // Nút xác nhận và nút thoát
    private ArrayList<CauTracNghiem> cauTracNghiems;  // Danh sách các câu hỏi trắc nghiệm
    int questioncurrent = 0;  // Câu hỏi hiện tại
    int questiontrue = 0;  // Số câu trả lời đúng
    int answer = 0;  // Đáp án đúng
    int score = 0;  // Điểm số hiện tại
    int idbo;  // ID bộ câu hỏi

    User user;  // Đối tượng người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);  // Gắn layout vào Activity
        DB = DatabaseAccess.getInstance(getApplicationContext());  // Khởi tạo đối tượng DatabaseAccess
        Anhxa();  // Khởi tạo các view

        LayUser();  // Lấy thông tin người dùng
        Intent intent = getIntent();
        idbo = intent.getIntExtra("Bo", 0);  // Lấy ID bộ câu hỏi từ Intent
        txttime.setText(" ");  // Xóa thời gian

        cauTracNghiems = new ArrayList<>();  // Khởi tạo danh sách câu hỏi
        AddArrayCTN();  // Thêm các câu hỏi vào danh sách

        // Nếu không có câu hỏi, hiển thị thông báo và quay lại màn hình trước
        if (cauTracNghiems.size() <= 0) {
            Toast.makeText(QuizActivity.this, "Nội dung sẽ cập nhật cập nhật trong thời gian sớm nhất! Mong mọi người thông cảm!!", Toast.LENGTH_LONG).show();
            Intent error = new Intent(QuizActivity.this, TracNghiemActivity.class);
            finish();
            startActivity(error);
        } else {
            // Hiển thị câu hỏi đầu tiên
            shownextquestion(questioncurrent, cauTracNghiems);

            // Tạo bộ đếm thời gian đếm ngược cho mỗi câu hỏi
            CountDownTimer countDownTimer = new CountDownTimer(3000, 300) {
                @Override
                public void onTick(long millisUntilFinished) {
                    showanswer();  // Hiển thị đáp án đúng sau mỗi tick
                }

                @Override
                public void onFinish() {
                    btnconfirm.setEnabled(true);  // Kích hoạt nút xác nhận
                    shownextquestion(questioncurrent, cauTracNghiems);  // Hiển thị câu hỏi tiếp theo
                }
            };

            // Khi người dùng nhấn nút xác nhận
            btnconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkans();  // Kiểm tra câu trả lời
                    questioncurrent++;  // Chuyển sang câu hỏi tiếp theo
                    countDownTimer.start();  // Bắt đầu đếm ngược
                }
            });

            // Khi người dùng nhấn nút thoát
            btnquit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(QuizActivity.this, TracNghiemActivity.class);  // Quay lại màn hình câu hỏi
                    startActivity(intent);
                }
            });
        }
    }

    // Phương thức để ánh xạ các view từ layout
    public void Anhxa() {
        txtscore = findViewById(R.id.txtscoreTN);
        txtquestcount = findViewById(R.id.txtquestcountTN);
        txtquestion = findViewById(R.id.txtquestionTN);
        txttime = findViewById(R.id.txttimeTN);
        rdgchoices = findViewById(R.id.radiochoices);
        btnop1 = findViewById(R.id.op1);
        btnop2 = findViewById(R.id.op2);
        btnop3 = findViewById(R.id.op3);
        btnop4 = findViewById(R.id.op4);
        btnconfirm = findViewById(R.id.btnconfirmTN);
        btnquit = findViewById(R.id.btnQuitTN);
    }

    // Phương thức để thêm các câu hỏi vào danh sách từ cơ sở dữ liệu
    private void AddArrayCTN() {
        database = Database.initDatabase(QuizActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM TracNghiem WHERE ID_Bo = ?", new String[]{String.valueOf(idbo)});
        cauTracNghiems.clear();

        // Duyệt qua các câu hỏi trong cơ sở dữ liệu và thêm vào danh sách
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int idcau = cursor.getInt(0);
            int idbo = cursor.getInt(1);
            String noidung = cursor.getString(2);
            String A = cursor.getString(3);
            String B = cursor.getString(4);
            String C = cursor.getString(5);
            String D = cursor.getString(6);
            String True = cursor.getString(7);

            cauTracNghiems.add(new CauTracNghiem(idcau, idbo, noidung, A, B, C, D, True));
        }
    }

    // Phương thức để lấy thông tin người dùng từ cơ sở dữ liệu
    public void LayUser() {
        database = Database.initDatabase(QuizActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM User WHERE ID_User = ?", new String[]{String.valueOf(DB.iduser)});
        cursor.moveToNext();
        String Iduser = cursor.getString(0);
        String HoTen = cursor.getString(1);
        int Point = cursor.getInt(2);
        String Email = cursor.getString(3);
        String SDT = cursor.getString(4);
        int Role = cursor.getInt(5);
        user = new User(Iduser, HoTen, Point, Email, SDT, Role);
    }

    // Phương thức để hiển thị câu hỏi tiếp theo
    public void shownextquestion(int pos, ArrayList<CauTracNghiem> cauTracNghiems) {
        txtquestcount.setText("Question: " + (questioncurrent + 1) + "/" + cauTracNghiems.size());
        rdgchoices.clearCheck();  // Xóa lựa chọn đã chọn
        btnop1.setBackground(this.getResources().getDrawable(R.drawable.bgbtn));
        btnop2.setBackground(this.getResources().getDrawable(R.drawable.bgbtn));
        btnop3.setBackground(this.getResources().getDrawable(R.drawable.bgbtn));
        btnop4.setBackground(this.getResources().getDrawable(R.drawable.bgbtn));

        // Nếu đã hết câu hỏi, cập nhật điểm và chuyển sang màn hình kết thúc
        if (pos == cauTracNghiems.size()) {
            DB.capnhatdiem(DB.iduser, user.getPoint(), score);
            Intent intent = new Intent(QuizActivity.this, FinishQuizActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("questiontrue", questiontrue);
            intent.putExtra("qcount", pos);
            startActivity(intent);
        } else {
            answer = Integer.valueOf(cauTracNghiems.get(pos).getDapanTrue());  // Lấy đáp án đúng
            txtquestion.setText(cauTracNghiems.get(pos).getNoidung());
            btnop1.setText(cauTracNghiems.get(pos).getDapanA());
            btnop2.setText(cauTracNghiems.get(pos).getDapanB());
            btnop3.setText(cauTracNghiems.get(pos).getDapanC());
            btnop4.setText(cauTracNghiems.get(pos).getDapanD());
        }
    }

    // Phương thức kiểm tra câu trả lời của người dùng
    public void checkans() {
        btnconfirm.setEnabled(false);
        if (btnop1.isChecked()) {
            if (1 == answer) {
                questiontrue++;
                score += 5;
            }
        }
        if (btnop2.isChecked()) {
            if (2 == answer) {
                questiontrue++;
                score += 5;
            }
        }
        if (btnop3.isChecked()) {
            if (3 == answer) {
                questiontrue++;
                score += 5;
            }
        }
        if (btnop4.isChecked()) {
            if (4 == answer) {
                questiontrue++;
                score += 5;
            }
        }

        txtscore.setText("Score: " + score);
    }

    // Phương thức để hiển thị đáp án đúng
    public void showanswer() {
        if (1 == answer) {
            btnop1.setBackground(this.getResources().getDrawable(R.drawable.button_2));
            btnop2.setBackground(this.getResources().getDrawable(R.drawable.button_1));
            btnop3.setBackground(this.getResources().getDrawable(R.drawable.button_1));
            btnop4.setBackground(this.getResources().getDrawable(R.drawable.button_1));
        }
        if (2 == answer) {
            btnop1.setBackground(this.getResources().getDrawable(R.drawable.button_1));
            btnop2.setBackground(this.getResources().getDrawable(R.drawable.button_2));
            btnop3.setBackground(this.getResources().getDrawable(R.drawable.button_1));
            btnop4.setBackground(this.getResources().getDrawable(R.drawable.button_1));
        }
        if (3 == answer) {
            btnop1.setBackground(this.getResources().getDrawable(R.drawable.button_1));
            btnop2.setBackground(this.getResources().getDrawable(R.drawable.button_1));
            btnop3.setBackground(this.getResources().getDrawable(R.drawable.button_2));
            btnop4.setBackground(this.getResources().getDrawable(R.drawable.button_1));
        }
        if (4 == answer) {
            btnop1.setBackground(this.getResources().getDrawable(R.drawable.button_1));
            btnop2.setBackground(this.getResources().getDrawable(R.drawable.button_1));
            btnop3.setBackground(this.getResources().getDrawable(R.drawable.button_1));
            btnop4.setBackground(this.getResources().getDrawable(R.drawable.button_2));
        }
    }
}

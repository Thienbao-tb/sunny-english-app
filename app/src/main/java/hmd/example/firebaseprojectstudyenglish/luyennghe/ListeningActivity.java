package hmd.example.firebaseprojectstudyenglish.luyennghe;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class ListeningActivity extends AppCompatActivity {
    final String DATABASE_NAME = "HocNgonNgu.db"; // Tên cơ sở dữ liệu
    SQLiteDatabase database;
    DatabaseAccess DB;
    TextView txtscore, txtquestcount;  // TextViews hiển thị điểm số và số câu hỏi
    RadioGroup rdgchoices;  // Nhóm các câu trả lời
    RadioButton btnop1, btnop2, btnop3, btnop4; // Các câu trả lời
    Button btnconfirm, btnquit; // Nút xác nhận và thoát
    ImageView imHA;  // Hình ảnh câu hỏi
    private ArrayList<CauLuyenNghe> cauLuyenNghes;  // Danh sách các câu hỏi luyện nghe
    private MediaPlayer mediaPlayer;  // Trình phát âm thanh
    private ImageButton ImgBT; // Nút phát âm thanh

    User user;
    String URL = "https://firebasestorage.googleapis.com/v0/b/english-dd117.appspot.com/o/mobile%2FLuyenNghe_Hey%20Mama%20-%20David%20Guetta%20feat_%20Nicki%20Mina.mp3?alt=media&token=935b455f-a441-43a9-9cac-ef7cbf35dcf5";
    int questioncurrent = 0, questiontrue = 0, answer = 0, score = 0, idbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_listening); // Set layout cho activity
        DB = DatabaseAccess.getInstance(getApplicationContext()); // Lấy thông tin database
        Anhxa();  // Ánh xạ các view
        LayUser();  // Lấy thông tin người dùng
        Intent intent = getIntent();
        idbo = intent.getIntExtra("Bo", 0); // Lấy id bộ câu hỏi
        cauLuyenNghes = new ArrayList<>();
        AddArrayCLN();  // Thêm câu hỏi vào danh sách

        // Kiểm tra nếu không có câu hỏi nào
        if (cauLuyenNghes.size() <= 0) {
            Toast.makeText(ListeningActivity.this, "Nội dung sẽ cập nhật trong thời gian sớm nhất!", Toast.LENGTH_LONG).show();
            Intent error = new Intent(ListeningActivity.this, LuyenNgheActivity.class);
            finish();
            startActivity(error);
        } else {
            shownextquestion(questioncurrent, cauLuyenNghes); // Hiển thị câu hỏi đầu tiên

            mediaPlayer = new MediaPlayer(); // Khởi tạo MediaPlayer
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // Thiết lập kiểu âm thanh

            // Lắng nghe sự kiện chuẩn bị phát âm thanh
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start(); // Bắt đầu phát khi âm thanh sẵn sàng
                }
            });

            // Đếm ngược thời gian trước khi chuyển câu hỏi
            CountDownTimer countDownTimer = new CountDownTimer(3000, 300) {
                @Override
                public void onTick(long millisUntilFinished) {
                    showanswer();  // Hiển thị đáp án đúng khi kết thúc thời gian
                }

                @Override
                public void onFinish() {
                    btnconfirm.setEnabled(true); // Kích hoạt nút xác nhận sau khi hết thời gian
                    shownextquestion(questioncurrent, cauLuyenNghes);  // Chuyển câu hỏi tiếp theo
                }
            };

            // Khi nhấn nút xác nhận
            btnconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkans();  // Kiểm tra câu trả lời
                    questioncurrent++;  // Tiến tới câu hỏi tiếp theo
                    countDownTimer.start();  // Bắt đầu đếm ngược cho câu hỏi mới
                }
            });

            // Khi nhấn nút phát âm thanh
            this.ImgBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mediaURL = URL; // URL phát âm thanh
                    MediaPlayerUtils.playURLMedia(ListeningActivity.this, mediaPlayer, mediaURL);  // Phát âm thanh từ URL
                }
            });

            // Khi nhấn nút thoát
            btnquit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();  // Thoát activity hiện tại
                    doStop();  // Dừng phát âm thanh
                    Intent intent = new Intent(ListeningActivity.this, LuyenNgheActivity.class);
                    startActivity(intent);  // Chuyển đến activity Luyện Nghe
                }
            });
        }
    }

    // Ánh xạ các thành phần UI
    public void Anhxa() {
        txtscore = findViewById(R.id.txtscoreLN);
        txtquestcount = findViewById(R.id.txtquestcountLN);
        rdgchoices = findViewById(R.id.radiochoices);
        btnop1 = findViewById(R.id.op1);
        btnop2 = findViewById(R.id.op2);
        btnop3 = findViewById(R.id.op3);
        btnop4 = findViewById(R.id.op4);
        btnconfirm = findViewById(R.id.btnconfirmLN);
        btnquit = findViewById(R.id.btnQuitLN);
        imHA = findViewById(R.id.imgHinh);
        ImgBT = findViewById(R.id.ImgBT);
    }

    // Thêm câu hỏi vào danh sách
    private void AddArrayCLN() {
        database = Database.initDatabase(ListeningActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM LuyenNghe WHERE ID_Bo = ?", new String[]{String.valueOf(idbo)});
        cauLuyenNghes.clear();  // Xóa danh sách cũ

        // Đọc dữ liệu từ cơ sở dữ liệu và thêm vào danh sách
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int idbai = cursor.getInt(0);
            int idbo = cursor.getInt(1);
            String A = cursor.getString(2);
            String B = cursor.getString(3);
            String C = cursor.getString(4);
            String D = cursor.getString(5);
            String True = cursor.getString(6);
            byte[] hinh = cursor.getBlob(7);
            String audio = cursor.getString(8);
            cauLuyenNghes.add(new CauLuyenNghe(idbai, idbo, A, B, C, D, True, hinh, audio)); // Thêm câu hỏi vào danh sách
        }
    }

    // Lấy thông tin người dùng
    public void LayUser() {
        database = Database.initDatabase(ListeningActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM User WHERE ID_User = ?", new String[]{String.valueOf(DB.iduser)});
        cursor.moveToNext();
        user = new User(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5)); // Tạo đối tượng User
    }

    // Hiển thị câu hỏi tiếp theo
    public void shownextquestion(int pos, ArrayList<CauLuyenNghe> cauLuyenNghes) {
        if (pos > 0) doStop();  // Dừng phát âm thanh nếu có
        txtquestcount.setText("Question: " + (questioncurrent + 1) + "/" + cauLuyenNghes.size()); // Hiển thị số câu hỏi hiện tại
        rdgchoices.clearCheck();  // Hủy chọn câu trả lời trước
        resetButtons();  // Đặt lại màu nền các nút câu trả lời

        // Nếu đã hết câu hỏi
        if (pos == cauLuyenNghes.size()) {
            DB.capnhatdiem(DB.iduser, user.getPoint(), score);  // Cập nhật điểm số vào cơ sở dữ liệu
            Intent intent = new Intent(ListeningActivity.this, FinishQuizLSActivity.class);  // Chuyển sang màn hình kết thúc
            intent.putExtra("score", score);
            intent.putExtra("questiontrue", questiontrue);
            intent.putExtra("qcount", pos);
            startActivity(intent);
        } else {
            // Cập nhật hình ảnh và câu hỏi
            updateQuestion(cauLuyenNghes.get(pos));
        }
    }

    // Cập nhật câu hỏi
    private void updateQuestion(CauLuyenNghe question) {
        byte[] anh = question.getHinhanh();
        Bitmap img = BitmapFactory.decodeByteArray(anh, 0, anh.length);
        imHA.setImageBitmap(img);  // Hiển thị hình ảnh câu hỏi
        String URLaudio = question.getAudio();
        URL = URLaudio;
        answer = Integer.valueOf(question.getDapanTrue());  // Lấy đáp án đúng

        // Cập nhật các lựa chọn
        btnop1.setText(question.getDapanA());
        btnop2.setText(question.getDapanB());
        btnop3.setText(question.getDapanC());
        btnop4.setText(question.getDapanD());
    }

    // Kiểm tra đáp án
    public void checkans() {
        btnconfirm.setEnabled(false);  // Vô hiệu hóa nút xác nhận khi đã chọn
        if (btnop1.isChecked() && 1 == answer) {
            score += 5;  // Nếu chọn đúng, cộng điểm
            questiontrue++;
        }
        if (btnop2.isChecked() && 2 == answer) {
            score += 5;
            questiontrue++;
        }
        if (btnop3.isChecked() && 3 == answer) {
            score += 5;
            questiontrue++;
        }
        if (btnop4.isChecked() && 4 == answer) {
            score += 5;
            questiontrue++;
        }

        txtscore.setText("Score: " + score);  // Cập nhật điểm
    }

    // Hiển thị đáp án đúng
    public void showanswer() {
        resetButtons();  // Đặt lại màu nền của các nút
        if (answer == 1) {
            btnop1.setBackground(this.getResources().getDrawable(R.drawable.button_2)); // Màu nền đáp án đúng
        }
        if (answer == 2) {
            btnop2.setBackground(this.getResources().getDrawable(R.drawable.button_2));
        }
        if (answer == 3) {
            btnop3.setBackground(this.getResources().getDrawable(R.drawable.button_2));
        }
        if (answer == 4) {
            btnop4.setBackground(this.getResources().getDrawable(R.drawable.button_2));
        }
    }

    // Đặt lại màu nền các nút câu trả lời
    private void resetButtons() {
        btnop1.setBackground(this.getResources().getDrawable(R.drawable.button_1));
        btnop2.setBackground(this.getResources().getDrawable(R.drawable.button_1));
        btnop3.setBackground(this.getResources().getDrawable(R.drawable.button_1));
        btnop4.setBackground(this.getResources().getDrawable(R.drawable.button_1));
    }

    // Bắt đầu phát âm thanh
    private void doStart() {
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();  // Nếu âm thanh đang phát, tạm dừng
            this.mediaPlayer.reset();  // Khởi tạo lại trình phát âm thanh
        } else {
            this.mediaPlayer.start();  // Bắt đầu phát âm thanh
        }
    }

    // Dừng phát âm thanh
    private void doStop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();  // Dừng phát âm thanh
        }
        mediaPlayer.reset();  // Khởi tạo lại trình phát âm thanh
    }
}

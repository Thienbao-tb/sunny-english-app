package hmd.example.firebaseprojectstudyenglish.sapxepcau;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.database.Database;
import hmd.example.firebaseprojectstudyenglish.database.DatabaseAccess;
import hmd.example.firebaseprojectstudyenglish.taikhoan.User;

import java.util.ArrayList;
import java.util.Random;

public class ArrangeSentencesActivity extends AppCompatActivity {

    final String DATABASE_NAME = "HocNgonNgu.db";  // Tên cơ sở dữ liệu
    SQLiteDatabase database;  // Đối tượng để làm việc với cơ sở dữ liệu
    DatabaseAccess DB;  // Đối tượng DatabaseAccess để truy cập các dữ liệu liên quan

    private int presCounter = 0;  // Biến đếm số lần người dùng đã sắp xếp
    private String[] keys = {"part1", "part2", "part3", "part4"};  // Các phần của câu
    private String textAnswer = "ENGLISH";  // Đáp án cho câu hiện tại
    private int maxPresCounter = 4;  // Số lượng phần cần phải được sắp xếp cho mỗi câu
    Button btnquit;  // Nút thoát
    TextView textScreen, textTitle;  // Các TextView để hiển thị tiêu đề và thông báo
    TextView tvQuestionCount, tvScore;  // Các TextView để hiển thị số câu hỏi và điểm số
    ArrayList<CauSapXep> cauSapXeps;  // Danh sách các câu sắp xếp
    Animation smallbigforth;  // Hiệu ứng hoạt hình cho các phần tử
    int idbo;  // ID bộ câu hỏi
    private int score = 0;  // Điểm của người chơi
    int dem;  // Biến đếm dùng để thêm các phần vào layout
    int cau = 1;  // Biến đếm câu hỏi
    User user;  // Đối tượng người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_sentences);
        DB = DatabaseAccess.getInstance(getApplicationContext());  // Khởi tạo đối tượng truy cập cơ sở dữ liệu
        AnhXa();  // Ánh xạ các thành phần giao diện

        Intent intent = getIntent();
        idbo = intent.getIntExtra("idbo", 0);  // Lấy ID bộ câu hỏi từ Intent
        cauSapXeps = new ArrayList<>();
        AddArraySXC();  // Thêm các câu sắp xếp vào danh sách

        LayUser();  // Lấy thông tin người dùng

        // Kiểm tra nếu danh sách câu sắp xếp rỗng
        if (cauSapXeps.size() <= 0) {
            Toast.makeText(ArrangeSentencesActivity.this, "Nội dung sẽ cập nhật trong thời gian sớm nhất! Mong mọi người thông cảm!!", Toast.LENGTH_LONG).show();
            Intent error = new Intent(ArrangeSentencesActivity.this, SapXepCauActivity.class);
            finish();
            startActivity(error);
        } else {
            textAnswer = cauSapXeps.get(0).getDapan();  // Lấy đáp án câu đầu tiên

            // Khởi tạo hiệu ứng hoạt hình
            smallbigforth = AnimationUtils.loadAnimation(this, R.anim.smallbigforth);

            // Gán các phần của câu
            keys[0] = cauSapXeps.get(0).getPart1();
            keys[1] = cauSapXeps.get(0).getPart2();
            keys[2] = cauSapXeps.get(0).getPart3();
            keys[3] = cauSapXeps.get(0).getPart4();

            // Cập nhật thông tin câu hỏi và điểm
            tvQuestionCount.setText("Question: " + cau + "/" + cauSapXeps.size());
            tvScore.setText("Score: " + score);

            // Trộn các phần của câu
            keys = shuffleArray(keys);

            dem = 0;
            // Thêm các phần vào các layout tương ứng
            while (dem < keys.length) {
                if (dem < 1) {
                    addView(((LinearLayout) findViewById(R.id.layoutPart1)), keys[dem], ((EditText) findViewById(R.id.editDapAn)));
                } else if (dem < 2) {
                    addView(((LinearLayout) findViewById(R.id.layoutPart2)), keys[dem], ((EditText) findViewById(R.id.editDapAn)));
                } else if (dem < 3) {
                    addView(((LinearLayout) findViewById(R.id.layoutPart3)), keys[dem], ((EditText) findViewById(R.id.editDapAn)));
                } else {
                    addView(((LinearLayout) findViewById(R.id.layoutPart4)), keys[dem], ((EditText) findViewById(R.id.editDapAn)));
                }
                dem++;
            }

            // Xử lý sự kiện cho nút thoát
            btnquit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent intent = new Intent(ArrangeSentencesActivity.this, SapXepCauActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    // Phương thức ánh xạ các thành phần giao diện
    private void AnhXa() {
        textScreen = (TextView) findViewById(R.id.textSXC);
        textTitle = (TextView) findViewById(R.id.textOption);
        tvQuestionCount = (TextView) findViewById(R.id.tvQuestionSXC);
        tvScore = (TextView) findViewById(R.id.tvScoreSXC);
        btnquit = (Button) findViewById(R.id.btnQuitSXC);
    }

    // Phương thức lấy thông tin người dùng từ cơ sở dữ liệu
    public void LayUser() {
        database = Database.initDatabase(ArrangeSentencesActivity.this, DATABASE_NAME);
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

    // Thêm câu sắp xếp vào danh sách từ cơ sở dữ liệu
    private void AddArraySXC() {
        database = Database.initDatabase(ArrangeSentencesActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM SapXepCau WHERE IDBo = ?", new String[]{String.valueOf(idbo)});
        cauSapXeps.clear();

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int idcau = cursor.getInt(0);
            int idbo = cursor.getInt(1);
            String dapan = cursor.getString(2);
            String part1 = cursor.getString(3);
            String part2 = cursor.getString(4);
            String part3 = cursor.getString(5);
            String part4 = cursor.getString(6);

            cauSapXeps.add(new CauSapXep(idcau, idbo, dapan, part1, part2, part3, part4));
        }
    }

    // Phương thức trộn mảng
    private String[] shuffleArray(String[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
        return ar;
    }

    // Phương thức thêm các phần vào layout và xử lý sự kiện khi người dùng nhấn vào chúng
    private void addView(LinearLayout viewParent, final String text, final EditText editText) {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        linearLayoutParams.rightMargin = 5;

        final TextView textView = new TextView(this);

        textView.setLayoutParams(linearLayoutParams);
        textView.setBackground(this.getResources().getDrawable(R.drawable.bgbtn));
        textView.setTextColor(this.getResources().getColor(R.color.colorPurple));
        textView.setGravity(Gravity.CENTER);
        textView.setText(text);
        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setTextSize(30);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/FredokaOneRegular.ttf");

        textScreen.setTypeface(typeface);
        textTitle.setTypeface(typeface);
        tvQuestionCount.setTypeface(typeface);
        tvScore.setTypeface(typeface);
        editText.setTypeface(typeface);
        textView.setTypeface(typeface);
        textView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (presCounter < maxPresCounter) {
                    if (presCounter == 0)
                        editText.setText("");

                    int chuoi = 0;

                    if (editText.getText().toString().equals("") || chuoi == 3) {
                        editText.setText(editText.getText().toString() + text);
                        chuoi++;
                    } else {
                        editText.setText(editText.getText().toString() + " " + text);
                        chuoi++;
                    }

                    textView.startAnimation(smallbigforth);
                    textView.animate().alpha(0).setDuration(300);
                    presCounter++;
                    textView.setClickable(false);

                    if (presCounter == maxPresCounter)
                        doValidate();
                }
            }
        });
        viewParent.addView(textView);
    }

    // Phương thức để xác thực câu trả lời và chuyển sang câu tiếp theo
    private void doValidate() {
        presCounter = 0;

        EditText editText = findViewById(R.id.editDapAn);
        LinearLayout Part1 = findViewById(R.id.layoutPart1);
        LinearLayout Part2 = findViewById(R.id.layoutPart2);
        LinearLayout Part3 = findViewById(R.id.layoutPart3);
        LinearLayout Part4 = findViewById(R.id.layoutPart4);

        if (editText.getText().toString().equals(textAnswer)) {
            if (cau == cauSapXeps.size()) {
                score += 5;
                DB.capnhatdiem(DB.iduser, user.getPoint(), score);
                Toast.makeText(ArrangeSentencesActivity.this, "Hoàn Thành Xuất Sắc!!~(^.^)~", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ArrangeSentencesActivity.this, FinishSXCActivity.class);
                intent.putExtra("score", score);
                intent.putExtra("questiontrue", cau);
                intent.putExtra("qcount", cauSapXeps.size());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ArrangeSentencesActivity.this, "Chính xác!!(^.^)", Toast.LENGTH_SHORT).show();
                textAnswer = cauSapXeps.get(cau).getDapan();
                keys[0] = cauSapXeps.get(cau).getPart1();
                keys[1] = cauSapXeps.get(cau).getPart2();
                keys[2] = cauSapXeps.get(cau).getPart3();
                keys[3] = cauSapXeps.get(cau).getPart4();
                editText.setText("");
                score += 5;
                cau++;
                tvQuestionCount.setText("Question: " + cau + "/" + cauSapXeps.size());
                tvScore.setText("Score: " + score);
            }
        } else {
            Toast.makeText(ArrangeSentencesActivity.this, "Sai rồi!!(T.T)", Toast.LENGTH_SHORT).show();
            editText.setText("");
        }

        keys = shuffleArray(keys);
        Part1.removeAllViews();
        Part2.removeAllViews();
        Part3.removeAllViews();
        Part4.removeAllViews();

        dem = 0;
        // Thêm lại các phần đã trộn vào layout tương ứng
        while (dem < keys.length) {
            if (dem < 1) {
                addView(Part1, keys[dem], editText);
            } else if (dem < 2) {
                addView(Part2, keys[dem], editText);
            } else if (dem < 3) {
                addView(Part3, keys[dem], editText);
            } else {
                addView(Part4, keys[dem], editText);
            }
            dem++;
        }
    }
}

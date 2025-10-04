package hmd.example.firebaseprojectstudyenglish.hoctuvung;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.database.Database;
import hmd.example.firebaseprojectstudyenglish.database.DatabaseAccess;
import hmd.example.firebaseprojectstudyenglish.luyennghe.MediaPlayerUtils;
import hmd.example.firebaseprojectstudyenglish.taikhoan.User;

import java.util.ArrayList;
import java.util.Random;

public class WordMattersActivity extends AppCompatActivity {


    final String DATABASE_NAME = "HocNgonNgu.db"; // Tên cơ sở dữ liệu
    SQLiteDatabase database;
    DatabaseAccess DB;

    private int presCounter = 0; // Bộ đếm cho các ký tự đã nhấn
    private String[] keys = {"a", "b", "c", "d", "e", "f", "g", "v", "w", "x", "y", "z"}; // Mảng chứa các ký tự để người dùng chọn

    private MediaPlayer mediaPlayer; // Đối tượng MediaPlayer để phát âm thanh
    String URL = "https://github.com/duchmdev/..."; // URL âm thanh mặc định
    String UL = "hello"; // Biến để lưu URL trước khi phát

    private String textAnswer = "ENGLISH"; // Câu trả lời đúng
    private int maxPresCounter = 0; // Số lượng ký tự tối đa có thể được chọn
    private TextView textScreen, textQuestion, textTitle;
    private TextView tvWordCount, tvScore; // Các TextView để hiển thị từ vựng và điểm số
    private ImageButton ListenTV; // Nút để phát âm thanh
    private ImageView imgview; // Hình ảnh hiển thị từ vựng
    private Button btnquit; // Nút thoát
    private ArrayList<TuVung> DStuvung; // Danh sách từ vựng
    private Animation smallbigforth; // Hiệu ứng hoạt hình
    private int idbo; // ID bộ từ vựng
    private int score = 0; // Điểm số
    private int dem; // Biến đếm
    private int tu = 1; // Biến đếm từ vựng hiện tại
    User user; // Thông tin người dùng
    Animation smalltobig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_matters);
        smalltobig = AnimationUtils.loadAnimation(WordMattersActivity.this, R.anim.smalltobig);

        DB = DatabaseAccess.getInstance(getApplicationContext());
        AnhXa(); // Khởi tạo các view
        LayUser(); // Lấy thông tin người dùng

        Intent intent = getIntent();
        idbo = intent.getIntExtra("idbo", 0); // Lấy ID bộ từ vựng
        DStuvung = new ArrayList<>(); // Khởi tạo danh sách từ vựng

        AddArrayTV(); // Thêm từ vựng vào danh sách

        mediaPlayer = new MediaPlayer(); // Tạo đối tượng MediaPlayer để phát âm thanh
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(mp -> mp.start()); // Phát âm thanh khi đã sẵn sàng

        Bitmap img = BitmapFactory.decodeByteArray(DStuvung.get(0).getAnh(), 0, DStuvung.get(0).getAnh().length);
        imgview.setImageBitmap(img); // Hiển thị hình ảnh từ vựng
        imgview.startAnimation(smalltobig); // Áp dụng hiệu ứng cho hình ảnh
        textQuestion.setText("(" + DStuvung.get(0).getLoaitu() + ") - (" + DStuvung.get(0).getDichnghia() + ")");
        tvWordCount.setText("Word: " + tu + "/" + DStuvung.size()); // Hiển thị từ vựng hiện tại
        tvScore.setText("Score: " + score); // Hiển thị điểm số

        textAnswer = DStuvung.get(0).getDapan(); // Cập nhật câu trả lời đúng
        URL = DStuvung.get(0).getAudio(); // Cập nhật URL âm thanh

        smallbigforth = AnimationUtils.loadAnimation(this, R.anim.smallbigforth); // Hiệu ứng khi nhấn vào ký tự

        for (int i = 0; i < textAnswer.length(); i++) {
            keys[i] = String.valueOf(textAnswer.charAt(i)); // Gán các ký tự vào mảng keys
        }

        keys = shuffleArray(keys); // Trộn lại các ký tự để hiển thị ngẫu nhiên

        // Tạo các view ký tự cho người dùng chọn
        dem = 0;
        while (dem < keys.length) {
            if (dem < 4) {
                addView(((LinearLayout) findViewById(R.id.layoutParent1)), keys[dem], ((EditText) findViewById(R.id.editText)));
            } else if (dem < 8) {
                addView(((LinearLayout) findViewById(R.id.layoutParent2)), keys[dem], ((EditText) findViewById(R.id.editText)));
            } else {
                addView(((LinearLayout) findViewById(R.id.layoutParent3)), keys[dem], ((EditText) findViewById(R.id.editText)));
            }
            dem++;
        }

        // When the video file ready for playback.
        ListenTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // http://example.coom/mysong.mp3
                //String mediaURL = MediaPlayerUtils.URL_MEDIA_SAMPLE;
                doStop();
                if (UL == URL)  MediaPlayerUtils.playURLMedia(WordMattersActivity.this, mediaPlayer, UL);
                else {
                    String mediaURL = URL;
                    UL = URL;
                    MediaPlayerUtils.playURLMedia(WordMattersActivity.this, mediaPlayer, mediaURL);
                }
            }
        });

        btnquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent
                        = new Intent(WordMattersActivity.this,
                        HocTuVungActivity.class);
                startActivity(intent);
            }
        });


        maxPresCounter = textAnswer.length(); // Cập nhật số lượng ký tự tối đa
    }
    private void AnhXa() {
        textQuestion = findViewById(R.id.textQuestion);
        imgview = findViewById(R.id.imgview);
        textScreen = findViewById(R.id.textScreen);
        textTitle = findViewById(R.id.textTitle);
        tvWordCount = findViewById(R.id.tvWord);
        tvScore = findViewById(R.id.tvScore);
        ListenTV = findViewById(R.id.ListenTuVung);
        btnquit = findViewById(R.id.btnQuitHTV);
    }

    // Lấy thông tin người dùng từ cơ sở dữ liệu
    public void LayUser() {
        database = Database.initDatabase(WordMattersActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM User WHERE ID_User = ?", new String[]{String.valueOf(DB.iduser)});
        cursor.moveToNext();
        user = new User(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getInt(5)
        );
    }

    // Thêm từ vựng từ cơ sở dữ liệu vào danh sách DStuvung
    private void AddArrayTV() {
        database = Database.initDatabase(WordMattersActivity.this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM TuVung WHERE ID_Bo = ?", new String[]{String.valueOf(idbo)});
        DStuvung.clear();
        while (cursor.moveToNext()) {
            DStuvung.add(new TuVung(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getBlob(6)
            ));
        }
    }

    // Trộn lại mảng các ký tự
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

    // Thêm ký tự vào view cho người dùng chọn
    private void addView(LinearLayout viewParent, final String text, final EditText editText) {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linearLayoutParams.rightMargin = 30;

        final TextView textView = new TextView(this);
        textView.setLayoutParams(linearLayoutParams);
        textView.setBackground(this.getResources().getDrawable(R.drawable.bgpink));
        textView.setTextColor(this.getResources().getColor(R.color.colorPurple));
        textView.setGravity(Gravity.CENTER);
        textView.setText(text);
        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setTextSize(35);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/FredokaOneRegular.ttf");
        textScreen.setTypeface(typeface);
        textTitle.setTypeface(typeface);
        editText.setTypeface(typeface);
        textView.setTypeface(typeface);
        tvScore.setTypeface(typeface);
        tvWordCount.setTypeface(typeface);

        // Khi người dùng nhấn vào ký tự
        textView.setOnClickListener(v -> {
            if (presCounter < maxPresCounter) {
                if (presCounter == 0)
                    editText.setText(""); // Xóa văn bản hiện tại
                editText.setText(editText.getText().toString() + text); // Thêm ký tự vào EditText
                textView.startAnimation(smallbigforth); // Hiệu ứng cho ký tự
                textView.animate().alpha(0).setDuration(300); // Làm mờ ký tự đã chọn
                presCounter++;
                textView.setClickable(false); // Vô hiệu hóa ký tự đã chọn
                if (presCounter == maxPresCounter) doValidate(); // Kiểm tra kết quả khi đủ số ký tự
            }
        });

        viewParent.addView(textView); // Thêm ký tự vào view
    }

    // Kiểm tra kết quả trả lời
    private void doValidate() {
        presCounter = 0;

        EditText editText = findViewById(R.id.editText);
        LinearLayout linearLayout1 = findViewById(R.id.layoutParent1);
        LinearLayout linearLayout2 = findViewById(R.id.layoutParent2);
        LinearLayout linearLayout3 = findViewById(R.id.layoutParent3);

        // Nếu câu trả lời đúng
        if (editText.getText().toString().equals(textAnswer)) {
            if (tu == DStuvung.size()) {
                score += 5;
                DB.capnhatdiem(DB.iduser, user.getPoint(), score); // Cập nhật điểm
                Toast.makeText(WordMattersActivity.this, "Hoàn Thành Xuất Sắc!!~(^.^)~", Toast.LENGTH_SHORT).show();
                // Chuyển đến màn hình kết thúc
                Intent intent = new Intent(WordMattersActivity.this, FinishHTVActivity.class);
                intent.putExtra("score", score);
                intent.putExtra("questiontrue", tu);
                intent.putExtra("qcount", DStuvung.size());
                startActivity(intent);
                finish();
            } else {
                if (tu > 0) doStop();
                Toast.makeText(WordMattersActivity.this, "Chính xác!!(^.^)", Toast.LENGTH_SHORT).show();
                Bitmap img = BitmapFactory.decodeByteArray(DStuvung.get(tu).getAnh(), 0, DStuvung.get(tu).getAnh().length);
                imgview.setImageBitmap(img); // Hiển thị hình ảnh tiếp theo
                imgview.startAnimation(smalltobig);
                textQuestion.setText("(" + DStuvung.get(tu).getLoaitu() + ") - (" + DStuvung.get(tu).getDichnghia() + ")");
                textAnswer = DStuvung.get(tu).getDapan(); // Cập nhật câu trả lời
                URL = DStuvung.get(tu).getAudio(); // Cập nhật URL âm thanh
                for (int i = 0; i < textAnswer.length(); i++) {
                    keys[i] = String.valueOf(textAnswer.charAt(i)); // Gán lại các ký tự
                }
                maxPresCounter = textAnswer.length(); // Cập nhật số ký tự
                editText.setText(""); // Xóa văn bản
                score += 5; // Cộng điểm
                tu++; // Tăng số thứ tự từ vựng
                tvWordCount.setText("Word: " + tu + "/" + DStuvung.size()); // Hiển thị số từ
                tvScore.setText("Score: " + score); // Hiển thị điểm số
            }
        } else {
            Toast.makeText(WordMattersActivity.this, "Sai rồi!!(T.T)", Toast.LENGTH_SHORT).show();
            editText.setText(""); // Xóa văn bản khi sai
        }

        keys = shuffleArray(keys); // Trộn lại các ký tự
        linearLayout1.removeAllViews(); // Xóa các view cũ
        linearLayout2.removeAllViews();
        linearLayout3.removeAllViews();

        // Thêm các ký tự mới vào view
        dem = 0;
        while (dem < keys.length) {
            if (dem < 4) {
                addView(linearLayout1, keys[dem], editText);
            } else if (dem < 8) {
                addView(linearLayout2, keys[dem], editText);
            } else {
                addView(linearLayout3, keys[dem], editText);
            }
            dem++;
        }
    }

    // Dừng phát âm thanh
    private void doStop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // Dừng âm thanh nếu đang phát
        }
        mediaPlayer.reset(); // Đặt lại MediaPlayer
    }
}
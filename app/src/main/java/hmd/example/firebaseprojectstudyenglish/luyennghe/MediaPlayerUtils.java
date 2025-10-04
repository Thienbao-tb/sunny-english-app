package hmd.example.firebaseprojectstudyenglish.luyennghe;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;

import hmd.example.firebaseprojectstudyenglish.singletonpattern.MessageObject;

public class MediaPlayerUtils {
    // Đường dẫn URL mẫu cho tệp âm thanh (MP3) để kiểm tra
    public static final String URL_MEDIA_SAMPLE  = "https://firebasestorage.googleapis.com/v0/b/english-dd117.appspot.com/o/mobile%2FLuyenNghe_Hey%20Mama%20-%20David%20Guetta%20feat_%20Nicki%20Mina.mp3?alt=media&token=935b455f-a441-43a9-9cac-ef7cbf35dcf5";

    // Nhãn dùng cho log để dễ dàng xác định trong quá trình gỡ lỗi
    public static final String LOG_TAG= "MediaPlayerTutorial";

    // Phương thức dùng để phát âm thanh từ URL
    public static void playURLMedia(Context context, MediaPlayer mediaPlayer, String videoURL)  {
        try {
            // Log URL của media để dễ dàng theo dõi
            Log.i(LOG_TAG, "Media URL: "+ videoURL);

            // Chuyển đổi URL thành đối tượng Uri
            Uri uri = Uri.parse(videoURL);

            // Gán nguồn phát media cho MediaPlayer
            mediaPlayer.setDataSource(context, uri);

            // Chuẩn bị MediaPlayer để phát âm thanh một cách bất đồng bộ
            mediaPlayer.prepareAsync();

        } catch(Exception e) {
            // Nếu có lỗi xảy ra, thông báo lỗi sẽ được hiển thị
            MessageObject messageObject = MessageObject.getInstance();
            Log.e(LOG_TAG, "Error Play URL Media: "+ e.getMessage());

            // Hiển thị hộp thoại thông báo lỗi với nội dung chi tiết
            messageObject.ShowDialogMessage(Gravity.CENTER,
                    context,
                    "Error Play URL Media: "+ e.getMessage(),
                    0);
            e.printStackTrace();  // In ra chi tiết lỗi
        }
    }
}

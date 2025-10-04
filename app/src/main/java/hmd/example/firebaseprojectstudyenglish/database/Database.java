package hmd.example.firebaseprojectstudyenglish.database;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Database {

    // Phương thức để khởi tạo cơ sở dữ liệu SQLite
    public static SQLiteDatabase initDatabase(Activity activity, String databaseName){
        try {
            // Xác định đường dẫn của cơ sở dữ liệu trong thiết bị
            String outFileName = activity.getApplicationInfo().dataDir + "/ /" + databaseName;
            Log.e("Huynh_outFileName", outFileName); // Log để xem đường dẫn của cơ sở dữ liệu

            // Tạo đối tượng File để kiểm tra xem cơ sở dữ liệu đã tồn tại hay chưa
            File f = new File(outFileName);
            if(!f.exists()) { // Nếu cơ sở dữ liệu chưa tồn tại
                // Mở cơ sở dữ liệu từ thư mục assets của ứng dụng
                InputStream e = activity.getAssets().open(databaseName);

                // Tạo thư mục "databases" nếu chưa tồn tại
                File folder = new File(activity.getApplicationInfo().dataDir + "/databases/");
                if (!folder.exists()) {
                    folder.mkdir(); // Tạo thư mục "databases"
                }

                // Tạo file đầu ra để sao chép cơ sở dữ liệu vào
                FileOutputStream myOutput = new FileOutputStream(outFileName);

                // Tạo một mảng byte để đọc dữ liệu từ InputStream
                byte[] buffer = new byte[1024];

                int length;
                // Đọc từ InputStream và ghi vào FileOutputStream
                while ((length = e.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length); // Ghi dữ liệu vào file
                }

                // Đảm bảo rằng tất cả dữ liệu đã được ghi ra
                myOutput.flush();
                myOutput.close(); // Đóng FileOutputStream
                e.close(); // Đóng InputStream
            }
        } catch (IOException e) {
            e.printStackTrace(); // In lỗi nếu có ngoại lệ trong quá trình sao chép
        }

        // Mở cơ sở dữ liệu và trả về đối tượng SQLiteDatabase để tương tác với cơ sở dữ liệu
        return activity.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
    }
}

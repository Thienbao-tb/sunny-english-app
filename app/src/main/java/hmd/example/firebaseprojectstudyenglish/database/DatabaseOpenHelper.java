package hmd.example.firebaseprojectstudyenglish.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    // Tên cơ sở dữ liệu SQLite
    public  static final  String DBNAME = "HocNgonNgu.db";
    // Phiên bản của cơ sở dữ liệu
    public  static final  int DBVERSION = 1;

    // Constructor của DatabaseOpenHelper, khởi tạo cơ sở dữ liệu với tên và phiên bản
    public DatabaseOpenHelper(@Nullable Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    // Phương thức này được gọi khi phiên bản cơ sở dữ liệu thay đổi
    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        // Nếu phiên bản cơ sở dữ liệu thay đổi, xóa bảng "User" và tạo lại
        MyDB.execSQL("drop table if exists User ");
    }
}


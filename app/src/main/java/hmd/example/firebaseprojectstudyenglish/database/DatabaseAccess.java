package hmd.example.firebaseprojectstudyenglish.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    SQLiteDatabase db;
    final String DATABASE_NAME = "HocNgonNgu.db"; // Tên cơ sở dữ liệu SQLite
    FirebaseDatabase rootNode; // Cơ sở dữ liệu Firebase
    DatabaseReference userref; // Tham chiếu tới dữ liệu người dùng trong Firebase
    private static DatabaseAccess instance;
    Cursor c = null;
    public String iduser;
    Map<String, String> user; // Lưu trữ dữ liệu người dùng (Ví dụ: HoTen: "John")
    Map<String, Long> diem; // Firebase sử dụng kiểu Long cho giá trị số (ví dụ: điểm)
    Map<String, Long> role; // Lưu trữ vai trò của người dùng

    // Constructor: Khởi tạo DatabaseOpenHelper để quản lý cơ sở dữ liệu SQLite
    private DatabaseAccess(Context context){
        this.openHelper = new DatabaseOpenHelper(context);
    }

    // Pattern Singleton để lấy instance của DatabaseAccess
    public static DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    // Mở kết nối tới cơ sở dữ liệu để ghi
    public void open(){
        this.db = openHelper.getWritableDatabase();
    }

    // Đóng kết nối cơ sở dữ liệu
    public void close(){
        if(db != null){
            this.db.close();
        }
    }

    // Thêm dữ liệu người dùng mới vào cơ sở dữ liệu SQLite
    public Boolean insertData(String iduser, String hoten, String email, String sdt, int diem, int role){
        db = openHelper.getWritableDatabase(); // Lấy cơ sở dữ liệu để ghi
        ContentValues contentValues = new ContentValues(); // Tạo đối tượng chứa dữ liệu sẽ thêm vào cơ sở dữ liệu
        contentValues.put("ID_User", iduser); // Lưu ID người dùng
        contentValues.put("HoTen", hoten); // Lưu tên người dùng
        contentValues.put("Point", diem); // Lưu điểm người dùng
        contentValues.put("Email", email); // Lưu email người dùng
        contentValues.put("SDT", sdt); // Lưu số điện thoại người dùng
        contentValues.put("Role", role); // Lưu vai trò người dùng

        // Chèn dữ liệu vào bảng "User" trong cơ sở dữ liệu SQLite
        long result = db.insert("User", null, contentValues);
        // Kiểm tra kết quả của thao tác chèn dữ liệu. Nếu kết quả là -1 thì không thành công
        if(result == -1) {
            return false;
        } else {
            return true; // Nếu thành công, trả về true
        }
    }

    // Kiểm tra xem tài khoản đã tồn tại trong cơ sở dữ liệu hay chưa
    public Boolean checktaikhoan(String email){
        db = openHelper.getWritableDatabase(); // Lấy cơ sở dữ liệu để đọc
        Cursor cursor = db.rawQuery("Select * from User where Email = ?", new String[]{email}); // Truy vấn người dùng theo email
        // Nếu tìm thấy dữ liệu trong cơ sở dữ liệu, trả về true
        if(cursor.getCount() > 0){
            return true;
        } else {
            return false; // Nếu không tìm thấy, trả về false
        }
    }

    // Cập nhật thông tin người dùng từ Firebase và lưu vào SQLite
    public void CapNhatUser(String iduser) {
        db = openHelper.getWritableDatabase(); // Lấy cơ sở dữ liệu để ghi
        rootNode = FirebaseDatabase.getInstance(); // Lấy instance Firebase
        userref = rootNode.getReference("User").child(iduser); // Lấy tham chiếu đến dữ liệu người dùng trong Firebase

        // Kiểm tra xem người dùng đã tồn tại trong SQLite chưa
        Cursor cursor = db.rawQuery("Select * from User where ID_User = ?", new String[]{iduser});
        if(cursor.getCount() > 0) {
            // Nếu đã có dữ liệu trong SQLite, cập nhật dữ liệu từ Firebase vào SQLite
            userref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Lấy dữ liệu người dùng từ Firebase
                    user = (Map<String, String>) dataSnapshot.getValue();
                    diem = (Map<String, Long>) dataSnapshot.getValue();
                    role = (Map<String, Long>) dataSnapshot.getValue();

                    db = openHelper.getWritableDatabase(); // Lấy cơ sở dữ liệu để ghi
                    ContentValues contentValues = new ContentValues(); // Tạo đối tượng chứa dữ liệu mới

                    // Cập nhật thông tin người dùng vào cơ sở dữ liệu SQLite
                    contentValues.put("HoTen", user.get("hoTen"));
                    int Point = diem.get("point").intValue();
                    contentValues.put("Point", Point);
                    contentValues.put("SDT", user.get("sdt"));
                    int Role = role.get("role").intValue();
                    contentValues.put("Role", Role);

                    // Cập nhật bảng "User" với dữ liệu mới
                    db.update("User", contentValues, "ID_User = ?", new String[]{iduser});
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Xử lý lỗi nếu có
                }
            });
        } else {
            // Nếu chưa có dữ liệu trong SQLite, thêm mới từ Firebase vào SQLite
            userref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Lấy dữ liệu từ Firebase
                    user = (Map<String, String>) dataSnapshot.getValue();
                    diem = (Map<String, Long>) dataSnapshot.getValue();
                    role = (Map<String, Long>) dataSnapshot.getValue();

                    db = openHelper.getWritableDatabase(); // Lấy cơ sở dữ liệu để ghi
                    ContentValues contentValues = new ContentValues(); // Tạo đối tượng chứa dữ liệu sẽ chèn

                    // Chèn dữ liệu vào SQLite
                    contentValues.put("ID_User", iduser);
                    contentValues.put("HoTen", user.get("hoTen"));
                    int Point = diem.get("point").intValue();
                    contentValues.put("Point", Point);
                    contentValues.put("Email", user.get("email"));
                    contentValues.put("SDT", user.get("sdt"));
                    int Role = role.get("role").intValue();
                    contentValues.put("Role", Role);

                    // Chèn dữ liệu vào bảng "User"
                    db.insert("User", null, contentValues);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Xử lý lỗi nếu có
                }
            });
        }
    }

    // Cập nhật thông tin người dùng (họ tên, số điện thoại) trong cả Firebase và SQLite
    public Boolean capnhatthongtin(String iduser, String hoten, String sdt) {
        rootNode = FirebaseDatabase.getInstance(); // Lấy instance Firebase
        userref = rootNode.getReference("User").child(iduser); // Lấy tham chiếu người dùng từ Firebase

        // Cập nhật dữ liệu trong Firebase
        userref.child("hoTen").setValue(hoten);
        userref.child("sdt").setValue(sdt);

        db = openHelper.getWritableDatabase(); // Lấy cơ sở dữ liệu SQLite
        ContentValues contentValues = new ContentValues(); // Tạo đối tượng chứa dữ liệu cập nhật
        contentValues.put("HoTen", hoten);
        contentValues.put("SDT", sdt);

        // Kiểm tra xem người dùng đã tồn tại trong SQLite chưa
        Cursor cursor = db.rawQuery("Select * from User where ID_User = ?", new String[]{iduser});
        if(cursor.getCount() > 0) {
            // Cập nhật dữ liệu trong SQLite
            long result = db.update("User", contentValues, "ID_User = ?", new String[]{iduser});
            if(result == -1) {
                return false; // Nếu có lỗi xảy ra, trả về false
            } else {
                return true; // Nếu thành công, trả về true
            }
        } else {
            return false; // Nếu không tìm thấy người dùng, trả về false
        }
    }

    // Cập nhật điểm của người dùng trong cả Firebase và SQLite
    public Boolean capnhatdiem(String iduser, int Point, int PointPlus) {
        // Cập nhật điểm trong Firebase
        rootNode = FirebaseDatabase.getInstance();
        userref = rootNode.getReference("User").child(iduser);
        userref.child("point").setValue(Point + PointPlus);

        db = openHelper.getWritableDatabase(); // Lấy cơ sở dữ liệu SQLite
        ContentValues contentValues = new ContentValues(); // Tạo đối tượng chứa điểm mới
        contentValues.put("Point", Point + PointPlus);

        // Kiểm tra xem người dùng đã tồn tại trong SQLite chưa
        Cursor cursor = db.rawQuery("Select * from User where ID_User = ?", new String[]{iduser});
        if(cursor.getCount() > 0) {
            // Cập nhật điểm trong SQLite
            long result = db.update("User", contentValues, "ID_User = ?", new String[]{iduser});
            if(result == -1) {
                return false; // Nếu có lỗi, trả về false
            } else {
                return true; // Nếu thành công, trả về true
            }
        } else {
            return false; // Nếu không tìm thấy người dùng, trả về false
        }
    }
}

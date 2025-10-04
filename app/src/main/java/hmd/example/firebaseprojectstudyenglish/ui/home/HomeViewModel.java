package hmd.example.firebaseprojectstudyenglish.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    // MutableLiveData là lớp được sử dụng để lưu trữ và quản lý dữ liệu có thể thay đổi.
    // Đây là nơi dữ liệu sẽ được lưu trữ và có thể được cập nhật khi cần thiết.
    private MutableLiveData<String> mText;

    // Constructor của HomeViewModel
    // Trong constructor, chúng ta khởi tạo giá trị mặc định cho mText
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        // Đặt giá trị mặc định cho mText, đây sẽ là thông báo ban đầu trong Fragment
        mText.setValue("This is home fragment");
    }

    // Phương thức trả về đối tượng LiveData để cho phép các Fragment hoặc Activity quan sát dữ liệu
    public LiveData<String> getText() {
        return mText;
    }
}

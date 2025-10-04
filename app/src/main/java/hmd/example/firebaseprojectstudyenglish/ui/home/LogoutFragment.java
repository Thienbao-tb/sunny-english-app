package hmd.example.firebaseprojectstudyenglish.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.taikhoan.LoginActivity;

public class LogoutFragment extends Fragment {

    // onCreateView là phương thức khởi tạo và thiết lập giao diện cho fragment
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate layout cho fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Chuyển hướng người dùng đến activity đăng nhập (LoginActivity)
        startActivity(new Intent(getActivity(), LoginActivity.class));

        // Trả về view root để hiển thị
        return root;
    }
}
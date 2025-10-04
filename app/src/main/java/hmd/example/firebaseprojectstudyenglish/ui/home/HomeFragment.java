package hmd.example.firebaseprojectstudyenglish.ui.home;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import hmd.example.firebaseprojectstudyenglish.R;
import hmd.example.firebaseprojectstudyenglish.dienkhuyet.DienKhuyetActivity;
import hmd.example.firebaseprojectstudyenglish.hoctuvung.HocTuVungActivity;
import hmd.example.firebaseprojectstudyenglish.luyennghe.LuyenNgheActivity;
import hmd.example.firebaseprojectstudyenglish.sapxepcau.SapXepCauActivity;
import hmd.example.firebaseprojectstudyenglish.taikhoan.RankingActivity;
import hmd.example.firebaseprojectstudyenglish.tracnghiem.TracNghiemActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    CardView cardViewHocTuVung, cardViewTracNghiem, cardViewSapXepCau, cardViewLuyenNghe, cardViewDienKhuyet, cardViewXepHang;
    ImageView imgview;
    final String DATABASE_NAME = "HocNgonNgu.db";
    SQLiteDatabase database;

    // onCreateView là phương thức để khởi tạo và thiết lập các view cho fragment
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Khởi tạo HomeViewModel
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        // Inflate layout cho fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Gắn kết các view (CardView và ImageView) với các thành phần giao diện tương ứng
        cardViewHocTuVung = root.findViewById(R.id.cardViewHocTuVung);
        cardViewDienKhuyet = root.findViewById(R.id.cardViewDienKhuyet);
        cardViewTracNghiem = root.findViewById(R.id.cardViewTracNghiem);
        cardViewSapXepCau = root.findViewById(R.id.cardViewSapXepCau);
        cardViewLuyenNghe = root.findViewById(R.id.cardViewLuyenNghe);
        cardViewXepHang = root.findViewById(R.id.cardViewXepHang);

        // Xử lý sự kiện click cho các CardView, mỗi CardView sẽ mở một Activity tương ứng
        cardViewHocTuVung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở activity học từ vựng
                Intent intent = new Intent(getActivity(), HocTuVungActivity.class);
                startActivity(intent);
            }
        });

        cardViewDienKhuyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở activity điền khuyết
                Intent intent = new Intent(getActivity(), DienKhuyetActivity.class);
                startActivity(intent);
            }
        });

        cardViewTracNghiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở activity trắc nghiệm
                Intent intent = new Intent(getActivity(), TracNghiemActivity.class);
                startActivity(intent);
            }
        });

        cardViewSapXepCau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở activity sắp xếp câu
                Intent intent = new Intent(getActivity(), SapXepCauActivity.class);
                startActivity(intent);
            }
        });

        cardViewLuyenNghe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở activity luyện nghe
                Intent intent = new Intent(getActivity(), LuyenNgheActivity.class);
                startActivity(intent);
            }
        });

        cardViewXepHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở activity xếp hạng
                Intent intent = new Intent(getActivity(), RankingActivity.class);
                startActivity(intent);
            }
        });

        // Trả về view root để hiển thị
        return root;
    }
}

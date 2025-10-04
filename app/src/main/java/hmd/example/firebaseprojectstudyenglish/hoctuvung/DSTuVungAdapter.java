package hmd.example.firebaseprojectstudyenglish.hoctuvung;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import hmd.example.firebaseprojectstudyenglish.R;

import java.util.List;

public class DSTuVungAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<TuVung> dstuvungs; // Danh sách các đối tượng TuVung

    // Constructor để khởi tạo adapter với context, layout và danh sách TuVung
    public DSTuVungAdapter(Context context, int layout, List<TuVung> dstuvungs) {
        this.context = context;
        this.layout = layout;
        this.dstuvungs = dstuvungs;
    }

    // Trả về số lượng phần tử trong danh sách
    @Override
    public int getCount() {
        return dstuvungs.size();
    }

    // Trả về đối tượng TuVung ở vị trí nhất định (chưa được sử dụng trong mã này)
    @Override
    public Object getItem(int position) {
        return null;
    }

    // Trả về id của mục (chưa được sử dụng trong mã này)
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // Lớp ViewHolder để tối ưu hóa việc tái sử dụng view
    private class ViewHolder {
        ImageView imgHinh;       // Hình ảnh của từ vựng
        TextView twDichNghia;    // Nghĩa của từ vựng
        TextView twTuVung;       // Từ vựng và loại từ
    }

    // Trả về view cho mỗi item trong ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // Nếu convertView là null, cần phải tạo mới ViewHolder và view
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            // Gán các view cho ViewHolder
            holder.imgHinh = convertView.findViewById(R.id.imgHinh);
            holder.twDichNghia = convertView.findViewById(R.id.twDichNghia);
            holder.twTuVung = convertView.findViewById(R.id.twTuVung);

            convertView.setTag(holder); // Lưu ViewHolder vào convertView
        } else {
            holder = (ViewHolder) convertView.getTag(); // Nếu convertView đã có, lấy lại ViewHolder
        }

        // Lấy TuVung tại vị trí hiện tại
        TuVung tuVung = dstuvungs.get(position);

        // Cập nhật dữ liệu vào các view trong ViewHolder
        holder.twDichNghia.setText(tuVung.getDichnghia());
        holder.twTuVung.setText(tuVung.getDapan() + " (" + tuVung.getLoaitu() + "):");

        // Chuyển đổi mảng byte thành Bitmap và gán vào ImageView
        Bitmap img = BitmapFactory.decodeByteArray(tuVung.getAnh(), 0, tuVung.getAnh().length);
        holder.imgHinh.setImageBitmap(img);

        return convertView; // Trả về convertView đã được cập nhật
    }
}



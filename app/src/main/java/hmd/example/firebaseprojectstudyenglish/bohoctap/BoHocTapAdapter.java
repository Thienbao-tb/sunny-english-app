package hmd.example.firebaseprojectstudyenglish.bohoctap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import hmd.example.firebaseprojectstudyenglish.R;

import java.util.List;

public class BoHocTapAdapter extends BaseAdapter {
    private Context context; // Đối tượng Context giúp truy cập các dịch vụ hệ thống
    private int layout; // Layout dùng để hiển thị mỗi dòng trong ListView
    private List<BoHocTap> boHocTapList; // Danh sách các đối tượng BoHocTap

    // Hàm khởi tạo của Adapter, truyền vào context, layout và danh sách BoHocTap
    public BoHocTapAdapter(Context context, int layout, List<BoHocTap> boHocTapList) {
        this.context = context;
        this.layout = layout;
        this.boHocTapList = boHocTapList;
    }

    @Override
    public int getCount() {
        // Trả về số lượng phần tử trong danh sách boHocTapList
        return boHocTapList.size();
    }

    @Override
    public Object getItem(int position) {
        // Trả về đối tượng BoHocTap tại vị trí "position", nhưng không dùng trong ứng dụng này
        return null;
    }

    @Override
    public long getItemId(int position) {
        // Trả về id của item tại vị trí "position", không dùng trong ứng dụng này
        return 0;
    }

    // Lớp ViewHolder giúp tối ưu hóa việc tìm kiếm các thành phần giao diện trong mỗi item của ListView
    private class ViewHolder{
        TextView txtTenBo; // TextView để hiển thị tên bộ học tập
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Kiểm tra nếu convertView chưa được tạo ra, thì tạo mới
        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Nạp layout cho item trong ListView
            convertView = inflater.inflate(layout, null);
            // Lấy đối tượng TextView từ layout
            holder.txtTenBo = (TextView) convertView.findViewById(R.id.tvTenBo);
            convertView.setTag(holder); // Gắn ViewHolder vào convertView
        } else {
            // Nếu convertView đã được tạo, tái sử dụng nó
            holder = (ViewHolder) convertView.getTag();
        }

        // Lấy đối tượng BoHocTap từ danh sách tại vị trí "position"
        BoHocTap BoHT = boHocTapList.get(position);
        // Hiển thị tên bộ học tập vào TextView
        holder.txtTenBo.setText(BoHT.getTenBo());

        // Trả về convertView để hiển thị trong ListView
        return convertView;
    }
}

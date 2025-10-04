package hmd.example.firebaseprojectstudyenglish.hoctuvung;

import java.io.Serializable;

public class TuVung implements Serializable {
    private int idtu;
    private int idbo;
    private String dapan;
    private String dichnghia;
    private String loaitu;
    private String audio;
    private byte[] anh;
    // Constructor để khởi tạo một đối tượng TuVung với các thông tin đã cho
    public TuVung(int idtu, int idbo, String dapan, String dichnghia, String loaitu, String audio, byte[] anh) {
        this.idtu = idtu;
        this.idbo = idbo;
        this.dapan = dapan;
        this.dichnghia = dichnghia;
        this.loaitu = loaitu;
        this.audio = audio;
        this.anh = anh;
    }
// Getter và Setter cho các thuộc tính của TuVung

    public int getIdtu() {
        return idtu; // Trả về ID của từ vựng
    }

    public void setIdtu(int idtu) {
        this.idtu = idtu; // Thiết lập ID cho từ vựng
    }

    public int getIdbo() {
        return idbo; // Trả về ID của bộ từ vựng
    }

    public void setIdbo(int idbo) {
        this.idbo = idbo; // Thiết lập ID cho bộ từ vựng
    }

    public String getDapan() {
        return dapan; // Trả về đáp án đúng của từ vựng
    }

    public void setDapan(String dapan) {
        this.dapan = dapan; // Thiết lập đáp án cho từ vựng
    }

    public String getDichnghia() {
        return dichnghia; // Trả về nghĩa của từ vựng
    }

    public void setDichnghia(String dichnghia) {
        this.dichnghia = dichnghia; // Thiết lập nghĩa cho từ vựng
    }

    public String getLoaitu() {
        return loaitu; // Trả về loại từ của từ vựng
    }

    public void setLoaitu(String loaitu) {
        this.loaitu = loaitu; // Thiết lập loại từ cho từ vựng
    }

    public String getAudio() {
        return audio; // Trả về URL hoặc tên file âm thanh của từ vựng
    }

    public void setAudio(String audio) {
        this.audio = audio; // Thiết lập âm thanh cho từ vựng
    }

    public byte[] getAnh() {
        return anh; // Trả về hình ảnh của từ vựng dưới dạng mảng byte
    }

    public void setAnh(byte[] anh) {
        this.anh = anh; // Thiết lập hình ảnh cho từ vựng
    }
}

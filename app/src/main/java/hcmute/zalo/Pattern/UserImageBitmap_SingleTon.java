package hcmute.zalo.Pattern;

import android.graphics.Bitmap;

public class UserImageBitmap_SingleTon {
    private Bitmap anhdaidien = null, anhbia = null;
    static UserImageBitmap_SingleTon userImageBitmap_singleTon;

    private UserImageBitmap_SingleTon(){

    }
    //Tạo một nơi chứa ảnh đại diện và ảnh bìa duy nhất
    public static UserImageBitmap_SingleTon getInstance() {
        //Kiểm tra nếu chưa có ảnh thì tạo mới
        if (userImageBitmap_singleTon == null) {
            userImageBitmap_singleTon = new UserImageBitmap_SingleTon();
        }
        return userImageBitmap_singleTon;
    }
    //Trả về dữ liệu cho ảnh đại diện
    public Bitmap getAnhbia() {
        return anhbia;
    }
    //Trả về dữ liệu cho ảnh bìa
    public Bitmap getAnhdaidien() {
        return anhdaidien;
    }
    //gắn dữ liệu cho ảnh đại diện
    public void setAnhbia(Bitmap anhbia) {
        this.anhbia = anhbia;
    }
    //gắn dữ liệu cho ảnh bìa
    public void setAnhdaidien(Bitmap anhdaidien) {
        this.anhdaidien = anhdaidien;
    }
}

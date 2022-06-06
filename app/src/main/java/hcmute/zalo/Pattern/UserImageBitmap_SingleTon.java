package hcmute.zalo.Pattern;

import android.graphics.Bitmap;

public class UserImageBitmap_SingleTon {
    private Bitmap anhdaidien = null, anhbia = null;
    static UserImageBitmap_SingleTon userImageBitmap_singleTon;

    private UserImageBitmap_SingleTon(){

    }
    public static UserImageBitmap_SingleTon getInstance() {
        if (userImageBitmap_singleTon == null) {
            userImageBitmap_singleTon = new UserImageBitmap_SingleTon();
        }
        return userImageBitmap_singleTon;
    }

    public Bitmap getAnhbia() {
        return anhbia;
    }

    public Bitmap getAnhdaidien() {
        return anhdaidien;
    }

    public void setAnhbia(Bitmap anhbia) {
        this.anhbia = anhbia;
    }

    public void setAnhdaidien(Bitmap anhdaidien) {
        this.anhdaidien = anhdaidien;
    }
}

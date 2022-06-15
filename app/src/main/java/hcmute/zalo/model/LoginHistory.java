package hcmute.zalo.model;

import java.util.Date;

public class LoginHistory {
    //Thuộc tính của bảng lịch sử đăng nhập
    //Số điện thoại đăng nhập
    private String userPhone;
    //Ngày đăng nhâp
    private String dateLogin;
    //Tên thiết bị đăng nhâp
    private String deviceName;

    public  LoginHistory(){

    }
    //Tạo mới một lịch sử đăng nhập
    public LoginHistory(String userPhone, String dateLogin, String deviceName) {
        this.userPhone = userPhone;
        this.dateLogin = dateLogin;
        this.deviceName = deviceName;
    }

    //Lấy dữ liệu của các thuộc tính của bảng lịch sử đăng nhập
    public String getUserPhone() {
        return userPhone;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDateLogin() {
        return dateLogin;
    }
    //Gắn dữ liệu cho các thuộc tính của bảng lịch sử đăng nhập
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setDateLogin(String dateLogin) {
        this.dateLogin = dateLogin;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}

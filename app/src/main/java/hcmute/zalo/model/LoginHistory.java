package hcmute.zalo.model;

import java.util.Date;

public class LoginHistory {
    private String userPhone;
    private String dateLogin;
    private String deviceName;

    public  LoginHistory(){

    }

    public LoginHistory(String userPhone, String dateLogin, String deviceName) {
        this.userPhone = userPhone;
        this.dateLogin = dateLogin;
        this.deviceName = deviceName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getDateLogin() {
        return dateLogin;
    }

    public void setDateLogin(String dateLogin) {
        this.dateLogin = dateLogin;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}

package hcmute.zalo.model;

import java.util.Date;

public class LoginHistory {
    private String userPhone;
    private Date dateLogin;
    private String locationName;
    private String deviceName;

    public LoginHistory(String userPhone, Date dateLogin) {
        this.userPhone = userPhone;
        this.dateLogin = dateLogin;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Date getDateLogin() {
        return dateLogin;
    }

    public void setDateLogin(Date dateLogin) {
        this.dateLogin = dateLogin;
    }
}

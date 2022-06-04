package hcmute.zalo.model;

import java.util.Date;

public class LoginHistory {
    private String userId;
    private Date dateLogin;
    private String locationName;
    private String deviceName;

    public LoginHistory(String userId, Date dateLogin) {
        this.userId = userId;
        this.dateLogin = dateLogin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDateLogin() {
        return dateLogin;
    }

    public void setDateLogin(Date dateLogin) {
        this.dateLogin = dateLogin;
    }
}

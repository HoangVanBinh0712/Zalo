package hcmute.zalo.model;

public class Participants {
    private String messageid;
    private String userPhone;
    public Participants(String messageid, String userPhone) {
        this.messageid = messageid;
        this.userPhone = userPhone;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }


}

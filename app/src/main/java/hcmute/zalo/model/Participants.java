package hcmute.zalo.model;

public class Participants {
    private String messageid;
    private String userid;
    public Participants(String messageid, String userid) {
        this.messageid = messageid;
        this.userid = userid;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


}

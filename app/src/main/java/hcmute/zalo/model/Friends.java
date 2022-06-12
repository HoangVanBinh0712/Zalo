package hcmute.zalo.model;

import java.util.Date;

public class Friends {
    public String getFriendPhone() {
        return friendPhone;
    }

    public String getFriendName() {
        return friendName;
    }

    public Date getDayBecome() {
        return dayBecome;
    }

    public void setFriendPhone(String friendPhone) {
        this.friendPhone = friendPhone;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public void setDayBecome(Date dayBecome) {
        this.dayBecome = dayBecome;
    }

    public Friends(String friendPhone, String friendName, Date dayBecome) {
        this.friendPhone = friendPhone;
        this.friendName = friendName;
        this.dayBecome = dayBecome;
    }

    public Friends() {
    }

    @Override
    public String toString() {
        return "Friends{" +
                "friendPhone='" + friendPhone + '\'' +
                ", friendName='" + friendName + '\'' +
                ", dayBecome=" + dayBecome +
                '}';
    }

    private String friendPhone;
    private String friendName;
    private Date dayBecome;

}

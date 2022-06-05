package hcmute.zalo.model;

public class Friends {
    private String friendPhone;
    private String friendName;
    private String friendPhoneNumber;

    public Friends(String friendPhone, String friendName, String friendPhoneNumber) {
        this.friendPhone = friendPhone;
        this.friendName = friendName;
        this.friendPhoneNumber = friendPhoneNumber;
    }

    public String getFriendPhone() {
        return friendPhone;
    }

    public void setFriendPhone(String friendPhone) {
        this.friendPhone = friendPhone;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendPhoneNumber() {
        return friendPhoneNumber;
    }

    public void setFriendPhoneNumber(String friendPhoneNumber) {
        this.friendPhoneNumber = friendPhoneNumber;
    }
}

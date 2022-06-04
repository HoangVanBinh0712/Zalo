package hcmute.zalo.model;

public class Friends {
    private String friendId;
    private String friendName;
    private String friendPhoneNumber;

    public Friends(String friendId, String friendName, String friendPhoneNumber) {
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendPhoneNumber = friendPhoneNumber;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
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

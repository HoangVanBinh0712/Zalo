package hcmute.zalo.model;

public class FriendRequest {
    private String senderId;
    private String receiverId;
    private String invitation;
    private boolean accept;

    public FriendRequest(String senderId, String receiverId, String invitation, boolean accept) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.invitation = invitation;
        this.accept = accept;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }
}

package hcmute.zalo.model;

public class FriendRequest {
    private String senderphone;
    private String receiverPhone;
    private String invitation;
    private boolean accept;

    public FriendRequest(String senderphone, String receiverPhone, String invitation, boolean accept) {
        this.senderphone = senderphone;
        this.receiverPhone = receiverPhone;
        this.invitation = invitation;
        this.accept = accept;
    }

    public String getSenderphone() {
        return senderphone;
    }

    public void setSenderphone(String senderphone) {
        this.senderphone = senderphone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
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

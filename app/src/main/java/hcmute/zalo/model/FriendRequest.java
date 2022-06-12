package hcmute.zalo.model;

import java.util.Date;

public class FriendRequest {
    public FriendRequest(String senderPhone, String senderName, String receiverPhone, String receiverName, String invitation) {
        this.senderPhone = senderPhone;
        this.senderName = senderName;
        this.receiverPhone = receiverPhone;
        this.receiverName = receiverName;
        this.invitation = invitation;
        this.dateRequest = new Date();
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getInvitation() {
        return invitation;
    }

    public Date getDateRequest() {
        return dateRequest;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }

    public void setDateRequest(Date dateRequest) {
        this.dateRequest = dateRequest;
    }

    public FriendRequest() {
    }

    private String senderPhone;
    private String senderName;
    private String receiverPhone;
    private String receiverName;
    private String invitation;
    private Date dateRequest;



}

package hcmute.zalo.model;

import java.util.Date;

public class FriendRequest {
    //Khởi tạo mới một lời mời kết bạn
    public FriendRequest(String senderPhone, String senderName, String receiverPhone, String receiverName, String invitation) {
        this.senderPhone = senderPhone;
        this.senderName = senderName;
        this.receiverPhone = receiverPhone;
        this.receiverName = receiverName;
        this.invitation = invitation;
        this.dateRequest = new Date();
    }
    //Lấy dữ liệu của các thuộc tính của lời mời kết bạn
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
    //gắn dữ liệu cho các thuộc tính của lời mời kết bạn
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
    //Thuộc tính bảng lời mời kết bạn
    //Số điện thoại người gửi lời mời kết bạn
    private String senderPhone;
    //Tên người gửi lời mời kết bạn
    private String senderName;
    //Số điện thoại người nhận lời mời kết bạn
    private String receiverPhone;
    //Tên người nhận lời mời kết bạn
    private String receiverName;
    //Nội dung lời mời kết bạn
    private String invitation;
    //Ngày gửi lời mời kết bạn
    private Date dateRequest;



}

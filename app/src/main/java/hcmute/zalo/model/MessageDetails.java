package hcmute.zalo.model;

import java.util.ArrayList;
import java.util.Date;

//Tạo mới một tin nhắn
public class MessageDetails {
    public MessageDetails(String messageId, String senderPhone, Date timeSended, String content, String viewer) {
        this.messageId = messageId;
        this.senderPhone = senderPhone;
        this.timeSended = timeSended;
        this.content = content;
        this.viewer = viewer;
    }

    //gắn dữ liệu cho các thuộc tính của tín nhắn
    public void setViewer(String viewer) {
        this.viewer = viewer;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public void setTimeSended(Date timeSended) {
        this.timeSended = timeSended;
    }

    public void setContent(String content) {
        this.content = content;
    }
    //lấy dữ liệu cửa các thuộc tính của tín nhắn
    public String getViewer() {
        return viewer;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public Date getTimeSended() {
        return timeSended;
    }

    public String getContent() {
        return content;
    }
    //Thuộc tính của bẳng tin nhắn
    //id của cuộc trò chuyện
    private String messageId;
    //Số điện thoại người gửi
    private String senderPhone;
    //Thời gian gửi tin nhắn
    private Date timeSended;
    //Nội dung tin nhắn
    private String content;
    //số điện thoại người nhận
    private String viewer;

    public MessageDetails() {
    }
    //Chuyển sang dạng chuỗi
    @Override
    public String toString() {
        return "MessageDetails{" +
                "messageId='" + messageId + '\'' +
                ", senderPhone='" + senderPhone + '\'' +
                ", timeSended=" + timeSended +
                ", content='" + content + '\'' +
                '}';
    }
}

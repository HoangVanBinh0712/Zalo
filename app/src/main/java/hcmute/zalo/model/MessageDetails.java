package hcmute.zalo.model;

import java.util.Date;

public class MessageDetails {
    public MessageDetails(String messageId, String senderId, Date timeSended, String content) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.timeSended = timeSended;
        this.content = content;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getTimeSended() {
        return timeSended;
    }

    public void setTimeSended(Date timeSended) {
        this.timeSended = timeSended;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String messageId;
    private String senderId;
    private Date timeSended;
    private String content;

}

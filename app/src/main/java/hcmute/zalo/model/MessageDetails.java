package hcmute.zalo.model;

import java.util.ArrayList;
import java.util.Date;

public class MessageDetails {
    public MessageDetails(String messageId, String senderPhone, Date timeSended, String content, String viewer) {
        this.messageId = messageId;
        this.senderPhone = senderPhone;
        this.timeSended = timeSended;
        this.content = content;
        this.viewer = viewer;
    }

    public void setViewer(String viewer) {
        this.viewer = viewer;
    }

    public String getViewer() {
        return viewer;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
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
    private String senderPhone;
    private Date timeSended;
    private String content;
    private String viewer;

    public MessageDetails() {
    }

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

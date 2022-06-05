package hcmute.zalo.model;

import java.util.ArrayList;
import java.util.Date;

public class MessageDetails {
    public MessageDetails(String messageId, String senderId, Date timeSended, String content,ArrayList<String> viewer) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.timeSended = timeSended;
        this.content = content;
        this.viewer = viewer;
    }

    public void setViewer(ArrayList<String> viewer) {
        this.viewer = viewer;
    }

    public ArrayList<String> getViewer() {
        return viewer;
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
    private ArrayList<String> viewer;

}

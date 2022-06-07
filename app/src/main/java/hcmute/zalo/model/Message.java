package hcmute.zalo.model;

public class Message {
    // Random -> kiem tra co chua. Random -> co chua
    // 1000000000 - 9999999999
    private String messageId;
    private String messageName;
    public Message(String messageId, String messageName) {
        this.messageId = messageId;
        this.messageName = messageName;
    }

    public Message() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }


}

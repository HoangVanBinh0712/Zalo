package hcmute.zalo.model;

public class Message {
    //Thuộc tính bảng cuộc trò chuyện
    //id của cuộc trò chuyện
    private String messageId;
    //Tên của cuộc trò chuyện
    private String messageName;
    //Tạo mới một cuộc trò chuyện
    public Message(String messageId, String messageName) {
        this.messageId = messageId;
        this.messageName = messageName;
    }

    public Message() {
    }
    //Lấy dữ liệu của các thuộc tính bảng cuộc trò chuyện
    public String getMessageId() {
        return messageId;
    }

    public String getMessageName() {
        return messageName;
    }
    //gắn dữ liệu cho các thuộc tính bảng cuộc trò chuyện
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }


}

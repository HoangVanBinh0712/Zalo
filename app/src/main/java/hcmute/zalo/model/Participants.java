package hcmute.zalo.model;

public class Participants {
    //thuộc tính của bảng thành viên cuộc trò chuyện
    //id của cuộc trò chuyện
    private String messageid;
    //số điện thoại thành viên
    private String userPhone;
    //Tạo mới một thành viên cho cuỗ trò chuyện
    public Participants(String messageid, String userPhone) {
        this.messageid = messageid;
        this.userPhone = userPhone;
    }

    public Participants() {
    }

    //Lấy dữ liệu của các thuộc tính của thành viên trong cuộc trò chuyện
    public String getMessageid() {
        return messageid;
    }

    public String getUserPhone() {
        return userPhone;
    }
    //Gắn dữ liệu cho các thuộc tính của thành viên trong cuộc trò chuyện
    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }


}

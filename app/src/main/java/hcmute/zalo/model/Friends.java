package hcmute.zalo.model;

import java.util.Date;

public class Friends {
    //Lấy dữ liệu các thuộc tính của bạn bè
    public String getFriendPhone() {
        return friendPhone;
    }

    public String getFriendName() {
        return friendName;
    }

    public Date getDayBecome() {
        return dayBecome;
    }
    //gắn dữ kiệu cho các thuộc tính của bạn bè
    public void setFriendPhone(String friendPhone) {
        this.friendPhone = friendPhone;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public void setDayBecome(Date dayBecome) {
        this.dayBecome = dayBecome;
    }
    //Khởi tạo mới một bạn bè
    public Friends(String friendPhone, String friendName, Date dayBecome) {
        this.friendPhone = friendPhone;
        this.friendName = friendName;
        this.dayBecome = dayBecome;
    }

    public Friends() {
    }
    //Chuyển thành dạng chuỗi
    @Override
    public String toString() {
        return "Friends{" +
                "friendPhone='" + friendPhone + '\'' +
                ", friendName='" + friendName + '\'' +
                ", dayBecome=" + dayBecome +
                '}';
    }
    //Thuộc tính bảng bạn bè
    //Số điện thoại của bạn bè
    private String friendPhone;
    //Tên của bạn bè
    private String friendName;
    //Ngày kết bạn
    private Date dayBecome;

}

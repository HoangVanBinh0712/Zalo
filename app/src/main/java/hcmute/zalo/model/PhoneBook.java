package hcmute.zalo.model;

public class PhoneBook {
    //thuộc tính cảu bảng danh bạ
    //số điện thoại của người chứa danh bạ
    private String userPhone;
    //số điện thoại của người có trong danh bạ
    private String phonebookName;
    //tên của người có trong danh bạ
    private String phonebookNumber;

    public PhoneBook() {
    }
    //tạo mới một danh bạ
    public PhoneBook(String userPhone, String phonebookName, String phonebookNumber) {
        this.userPhone = userPhone;
        this.phonebookName = phonebookName;
        this.phonebookNumber = phonebookNumber;
    }

    //Lấy dữ liệu của các thuộc tính của bảng danh bạ
    public String getPhonebookName() {
        return phonebookName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getPhonebookNumber() {
        return phonebookNumber;
    }
    //gắn dữ liệu cho các thuộc tính của bảng danh bạ
    public void setPhonebookNumber(String phonebookNumber) {
        this.phonebookNumber = phonebookNumber;
    }

    public void setPhonebookName(String phonebookName) {
        this.phonebookName = phonebookName;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
    //Chuyển sang dạng chuỗi
    @Override
    public String toString() {
        return "PhoneBook{" +
                "userPhone='" + userPhone + '\'' +
                ", phonebookName='" + phonebookName + '\'' +
                ", phonebookNumber='" + phonebookNumber + '\'' +
                '}';
    }
}

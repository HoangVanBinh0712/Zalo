package hcmute.zalo.model;

import java.util.Date;

public class User {

    //Lấy dữ liệu của các thuộc tính của người dùng
    public String getFullname() {
        return fullname;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public Boolean getSex() {
        return sex;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getBackground() {
        return background;
    }
    //gắn dữ liệu cho các thuộc tính của người dùng
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public User() {
    }
    //Thuộc tính của bảng người dùng
    //Tên của người dùng
    public String fullname;
    //Số điện thoại của người dùng
    public String phone;
    //mật khẩu tài khoản của người dùng
    private String password;
    //Ngày sinh của người dùng
    public String birthday;
    //Lới giới thiệu về bản thân người dùng
    public String description;
    //Giới tính của người dùng
    public Boolean sex;
    //Ảnh đại diện của người dùng
    public String avatar;
    //Ảnh bìa của người dùng
    public String background;
    //tạo mới một người dùng
    public User(String fullname, String phone, String password, String birthday, String description, Boolean sex, String avatar, String background) {
        this.fullname = fullname;
        this.phone = phone;
        this.password = password;
        this.birthday = birthday;
        this.description = description;
        this.sex = sex;
        this.avatar = avatar;
        this.background = background;
    }
    //Chuyển sang dạng chuỗi
    @Override
    public String toString() {
        return "User{" +
                ", fullname='" + fullname + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                ", description='" + description + '\'' +
                ", sex=" + sex +
                ", avatar='" + avatar + '\'' +
                ", background='" + background + '\'' +
                '}';
    }
}



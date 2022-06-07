package hcmute.zalo.model;

import java.util.Date;

public class User {

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public User() {
    }

    public String fullname;
    public String phone;
    private String password;
    public String birthday;
    public String description;
    public Boolean sex;
    public String avatar;
    public String background;
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



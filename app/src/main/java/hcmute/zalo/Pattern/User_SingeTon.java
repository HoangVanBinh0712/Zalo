package hcmute.zalo.Pattern;

import hcmute.zalo.model.User;

public class User_SingeTon {
    private User user = null;
    static User_SingeTon user_singeTon;
    private User_SingeTon() {
    }
    //Tạo một người dùng duy nhất trong ứng dụng
    public static User_SingeTon getInstance() {
        //Kiểm tra nếu chưa có người dùng thì tạo mới
        if (user_singeTon == null) {
            user_singeTon = new User_SingeTon();
        }
        return user_singeTon;
    }
    //Gắn dữ liệu người dùng
    public User getUser(){
        return this.user;
    }
    //Trả về dữ liệu người dùng
    public void setUser(User user){
        this.user = user;
    }
}

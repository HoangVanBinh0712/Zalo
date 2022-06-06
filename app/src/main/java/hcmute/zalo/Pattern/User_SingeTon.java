package hcmute.zalo.Pattern;

import hcmute.zalo.model.User;

public class User_SingeTon {
    private User user = null;
    static User_SingeTon user_singeTon;
    private User_SingeTon() {
    }

    public static User_SingeTon getInstance() {
        if (user_singeTon == null) {
            user_singeTon = new User_SingeTon();
        }
        return user_singeTon;
    }
    public User getUser(){
        return this.user;
    }
    public void setUser(User user){
        this.user = user;
    }
}

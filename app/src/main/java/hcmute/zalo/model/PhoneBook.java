package hcmute.zalo.model;

public class PhoneBook {
    private String userPhone;
    private String phonebookName;
    private String phonebookNumber;

    public PhoneBook() {
    }

    public PhoneBook(String userPhone, String phonebookName, String phonebookNumber) {
        this.userPhone = userPhone;
        this.phonebookName = phonebookName;
        this.phonebookNumber = phonebookNumber;
    }

    public String getPhonebookName() {
        return phonebookName;
    }

    public void setPhonebookName(String phonebookName) {
        this.phonebookName = phonebookName;
    }

    public String getPhonebookNumber() {
        return phonebookNumber;
    }

    public void setPhonebookNumber(String phonebookNumber) {
        this.phonebookNumber = phonebookNumber;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    @Override
    public String toString() {
        return "PhoneBook{" +
                "userPhone='" + userPhone + '\'' +
                ", phonebookName='" + phonebookName + '\'' +
                ", phonebookNumber='" + phonebookNumber + '\'' +
                '}';
    }
}

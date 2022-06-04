package hcmute.zalo.model;

public class PhoneBook {
    private String userId;
    private String phonebookName;
    private String phonebookNumber;

    public PhoneBook(String userId, String phonebookName, String phonebookNumber) {
        this.userId = userId;
        this.phonebookName = phonebookName;
        this.phonebookNumber = phonebookNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}

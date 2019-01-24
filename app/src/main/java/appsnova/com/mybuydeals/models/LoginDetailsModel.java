package appsnova.com.mybuydeals.models;

public class LoginDetailsModel {

    private String UserID;
    private String userName;
    private String userMobile;
    private String userEmail;
    private String loginDate;

    public LoginDetailsModel(String userID, String userName, String userMobile, String userEmail, String loginDate) {
        UserID = userID;
        this.userName = userName;
        this.userMobile = userMobile;
        this.userEmail = userEmail;
        this.loginDate = loginDate;
    }

    public LoginDetailsModel() {
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }
}

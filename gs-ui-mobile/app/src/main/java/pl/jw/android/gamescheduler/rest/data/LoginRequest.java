package pl.jw.android.gamescheduler.rest.data;

/**
 * Created by jacek on 2016-09-28.
 */
public class LoginRequest {

    private String userName;
    private String userNameAlias;
    private String password;

    public LoginRequest(String userName, String userNameAlias, String password) {
        this.userName = userName;
        this.userNameAlias = userNameAlias;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserNameAlias() {
        return userNameAlias;
    }

    public String getPassword() {
        return password;
    }
}

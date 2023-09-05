package es.tfg.musiccommunity.messages.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LoginForm {
    @NotBlank
    @Size(min=3, max = 60)
    private String login;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String firebaseToken;

    public LoginForm(String login, String password, String firebaseToken) {
        this.login = login;
        this.password = password;
        this.firebaseToken = firebaseToken;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}

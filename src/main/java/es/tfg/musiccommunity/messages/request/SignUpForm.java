package es.tfg.musiccommunity.messages.request;

import java.util.Set;

import javax.validation.constraints.*;

public class SignUpForm {
    @NotBlank
    @Size(min = 1,  max = 50)
    private String login;

    @NotBlank
    @Size(min = 1, max = 60)
    @Email
    private String email;
    
    private Set<String> role;
    
    @NotBlank
    @Size(min = 1, max = 40)
    private String password;

    @NotBlank
    @Size(min = 1, max = 9)
    private String phone;

    public SignUpForm(String login, String email, Set<String> role, String password, String phone) {
        this.login=login;
        this.email = email;
        this.role = role;
        this.password = password;
        this.phone = phone;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Set<String> getRole() {
    	return this.role;
    }
    
    public void setRole(Set<String> role) {
    	this.role = role;
    }
}

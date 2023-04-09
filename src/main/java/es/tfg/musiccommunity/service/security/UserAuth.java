package es.tfg.musiccommunity.service.security;

import es.tfg.musiccommunity.model.UserProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserAuth implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Long id;

    private String phone;

    private String login;

    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserAuth(Long id, String login, 
                        String email, String password, String phone,
			    		Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.phone = phone;
        this.login = login;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserAuth build(UserProfile user) {
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName().name())
        ).collect(Collectors.toList());

        return new UserAuth(
                user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.getPassword(),
                user.getPhone(),
                authorities
        );
    }

    public Long getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserAuth user = (UserAuth) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id.intValue();
        return result;
      }
}

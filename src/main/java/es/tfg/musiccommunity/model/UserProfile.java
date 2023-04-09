package es.tfg.musiccommunity.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "login"
    }),
    @UniqueConstraint(columnNames = {
        "email"
    })
})
public class UserProfile{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min=3, max = 50)
    private String login;

    @NaturalId
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min=6, max = 100)
    private String password;

    @NotBlank
    @Size(min=3, max = 50)
    private String phone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "roles_per_user", 
    	joinColumns = @JoinColumn(name = "user_id"), 
    	inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Size(max = 4000)
    private String bio;

    @ManyToMany(cascade = { 
        CascadeType.PERSIST, 
        CascadeType.MERGE
    })
    @JoinTable(name = "tags_per_user",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> interests;

    // Seguidores
    /* FOLLOWED -> SEGUIDO // FOLLOWER -> SEGUIDOR */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_followers",
        joinColumns = @JoinColumn(name = "followed_id"),
        inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private Set<UserProfile> followers;

    // Gente a la que sigue
    @ManyToMany(mappedBy = "followers")
    private Set<UserProfile> following;

    public UserProfile() {}

    public UserProfile(String login, String email, String password, String phone) {
        this.phone = phone;
        this.login = login;
        this.email = email;
        this.password = password;
        this.bio = "";
        this.interests = new HashSet<>();
        this.followers = new HashSet<>();
        this.following = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<Tag> getInterests() {
        return interests;
    }

    public void setInterests(Set<Tag> interests) {
        this.interests = interests;
    }

    public Set<UserProfile> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserProfile> followers) {
        this.followers = followers;
    }

    public void addFollower(UserProfile follower) {
        followers.add(follower);
        follower.following.add(this);
    }

    
    public Set<UserProfile> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserProfile> following) {
        this.following = following;
    }

    public void addFollowing(UserProfile followed) {
        followed.addFollower(this);
    }
}

package es.tfg.musiccommunity.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class UserProfileInfoDto {

    private Long id;
    private String login;
    private String email;
    private String phone;
    private String bio;
    private List<TagDto> interests;

    @JsonCreator
    public UserProfileInfoDto(Long id, String login, String email,
        String phone, String bio, List<TagDto> interests){
        this.id = id;
        this.login = login;
        this.email = email;
        this.phone = phone;
        this.bio = bio;
        this.interests = interests;
    }

    public Long getId(){
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getBio() {
        return this.bio;
    }

    public List<TagDto> getInterests() {
        return this.interests;
    }

}

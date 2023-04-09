package es.tfg.musiccommunity.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class FollowerDto {

    private Long id;
    private String login;
    private String bio;
    private List<TagDto> interests;

    @JsonCreator
    public FollowerDto(Long id, String login, String bio, List<TagDto> interests) {
        this.id = id;
        this.login = login;
        this.bio = bio;
        this.interests = interests;
    }

    public Long getId(){
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }

    public String getBio() {
        return this.bio;
    }

    public List<TagDto> getInterests() {
        return this.interests;
    }
    
}

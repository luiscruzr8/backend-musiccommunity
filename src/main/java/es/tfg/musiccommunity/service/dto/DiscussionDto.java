package es.tfg.musiccommunity.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class DiscussionDto {

    private Long id;
    private String title;
    private LocalDateTime creationDateTime;
    private String type;
    private String login;
    private String description;
    private List<TagDto> tags;

    @JsonCreator
    public DiscussionDto(Long id, String title, LocalDateTime creationDateTime, String type, String login,
            String description, List<TagDto> tags) {
        this.id = id;
        this.title = title;
        this.creationDateTime = creationDateTime;
        this.type = type;
        this.login = login;
        this.description = description;
        this.tags = tags;
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public LocalDateTime getCreationDateTime() {
        return this.creationDateTime;
    }

    public String getType() {
        return this.type;
    }

    public String getLogin() {
        return this.login;
    }

    public String getDescription() {
        return this.description;
    }

    public List<TagDto> getTags() {
        return this.tags;
    }
}
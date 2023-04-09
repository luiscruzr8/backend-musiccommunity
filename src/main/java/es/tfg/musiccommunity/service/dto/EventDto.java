package es.tfg.musiccommunity.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EventDto {

    private Long id;
    private String title;
    private LocalDateTime creationDateTime;
    private String type;
    private String cityName;
    private String login;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<TagDto> tags;

    @JsonCreator
    public EventDto(Long id, String title, LocalDateTime creationDateTime, String type, String cityName, String login,
            String description, LocalDateTime startDateTime, LocalDateTime endDateTime, List<TagDto> tags) {
        this.id = id;
        this.title = title;
        this.creationDateTime = creationDateTime;
        this.type = type;
        this.cityName = cityName;
        this.login = login;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
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

    public String getCityName() {
        return this.cityName;
    }

    public String getLogin() {
        return this.login;
    }

    public String getDescription() {
        return this.description;
    }

    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return this.endDateTime;
    }

    public List<TagDto> getTags() {
        return this.tags;
    }
}

package es.tfg.musiccommunity.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class OpinionDto {

    private Long id;
    private String title;
    private LocalDateTime creationDateTime;
    private String type;
    private String login;
    private String description;
    private Long scoreId;
    private ScoreDto scoreDto;
    private List<TagDto> tags;

    @JsonCreator
    public OpinionDto(Long id, String title, LocalDateTime creationDateTime, String type, String login,
            String description, ScoreDto scoreDto, List<TagDto> tags){
        this.id = id;
        this.title = title;
        this.creationDateTime = creationDateTime;
        this.type = type;
        this.login = login;
        this.description = description;
        this.scoreDto = scoreDto;
        this.scoreId = scoreDto.getId();
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

    public Long getScoreId(){
        return this.scoreId;
    }
    
    public ScoreDto getScoreDto() {
        return this.scoreDto;
    }

    public List<TagDto> getTags() {
        return this.tags;
    }

}

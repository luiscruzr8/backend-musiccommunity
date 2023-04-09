package es.tfg.musiccommunity.service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ScoreDto {

    private Long id;
    private String scoreName;
    private String fileType;
    private String login;
    private LocalDateTime uploadDateTime;

    @JsonCreator
    public ScoreDto(Long id, String scoreName, String fileType, String login, 
            LocalDateTime uploadDateTime) {
        this.id = id;
        this.scoreName = scoreName;
        this.fileType = fileType;
        this.login = login;
        this.uploadDateTime = uploadDateTime;
    }

    public Long getId() {
        return id;
    }

    public String getScoreName() {
        return scoreName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getLogin() {
        return login;
    }

    public LocalDateTime getUploadDateTime() {
        return uploadDateTime;
    }
    
}

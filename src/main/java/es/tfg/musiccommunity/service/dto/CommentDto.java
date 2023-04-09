package es.tfg.musiccommunity.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class CommentDto {

    private Long id;
    private String commentText;
    private LocalDateTime commentDate;
    private String login;
    private List<CommentDto> responses;

    @JsonCreator
    public CommentDto(Long id, String commentText, LocalDateTime commentDate, String login,
            List<CommentDto> responses) {
        this.id = id;
        this.commentText = commentText;
        this.commentDate = commentDate;
        this.login = login;
        this.responses = responses;
    }

    public Long getId() {
        return this.id;
    }

    public String getCommentText() {
        return this.commentText;
    }

    public LocalDateTime getCommentDate() {
        return this.commentDate;
    }

    public String getLogin() {
        return this.login;
    }

    public List<CommentDto> getResponses() {
        return this.responses;
    }
}

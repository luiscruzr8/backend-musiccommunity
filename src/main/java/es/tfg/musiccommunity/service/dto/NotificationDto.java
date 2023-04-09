package es.tfg.musiccommunity.service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;

public class NotificationDto {
    private Long id;
    private LocalDateTime notificationDateTime;
    private String login;
    private String from;
    private String type;
    private Long postId;
    private String postType;
    private Long commentId;
    private Long recommendationId;
    private int rate;

    @JsonCreator
    public NotificationDto(Long id, String type, LocalDateTime notificationDateTime, String login, String from,
            String postType, Long postId, Long commentId, Long recommendationId, int rate) {
        this.id = id;
        this.type = type;
        this.notificationDateTime = notificationDateTime;
        this.login = login;
        this.from = from;
        this.postType = postType;
        this.postId = postId;
        this.commentId = commentId;
        this.recommendationId = recommendationId;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public String getLogin() {
        return login;
    }

    public String getFrom() {
        return from;
    }

    public String getType() {
        return type;
    }

    public String getPostType() {
        return postType;
    }

    public Long getPostId() {
        return postId;
    
    }
    public Long getRecommendationId() {
        return recommendationId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public int getRate() {
        return rate;
    }
}
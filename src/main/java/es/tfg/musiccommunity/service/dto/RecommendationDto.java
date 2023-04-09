package es.tfg.musiccommunity.service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;

public class RecommendationDto {

    private Long id;
    private LocalDateTime creationDateTime;
    private String recTitle;
    private String recText;
    private String login;
    private PostDto post;
    private Long postId;
    private double rating;

    @JsonCreator
    public RecommendationDto(Long id, LocalDateTime creationDateTime, String recTitle, String recText,
            String login, PostDto post, double rating) {
        this.id = id;
        this.creationDateTime = creationDateTime;
        this.recTitle = recTitle;
        this.recText = recText;
        this.login = login;
        this.post = post;
        this.postId = post.getId();
        this.rating = rating;
    }

    public Long getId(){
        return this.id;
    }

    public String getRecTitle(){
        return this.recTitle;
    }

    public String getRecText(){
        return this.recText;
    }

    public LocalDateTime getCreationDateTime(){
        return this.creationDateTime;
    }

    public String getLogin(){
        return this.login;
    }

    public PostDto getPost(){
        return this.post;
    }

    public Long getPostId(){
        return this.postId;
    }

    public double getRating(){
        return this.rating;
    }
    
}

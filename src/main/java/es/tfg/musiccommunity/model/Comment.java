package es.tfg.musiccommunity.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserProfile user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "comment_date")
    private LocalDateTime commentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_to")
    private Comment responseTo;

    public Comment() { }

    public Comment(UserProfile user, Post post, String commentText) {
        this.user = user;
        this.post = post;
        this.commentText = commentText;
        this.commentDate = LocalDateTime.now();
    }

    public Comment(UserProfile user, Post post, String commentText, Comment responseTo) {
        this.user = user;
        this.post = post;
        this.commentText = commentText;
        this.commentDate = LocalDateTime.now();
        this.responseTo = responseTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getCommentDate() {
        return commentDate;
    }

    public Comment getResponseTo() {
        return responseTo;
    }

    public void setResponseTo(Comment responseTo) {
        this.responseTo = responseTo;
    }

}
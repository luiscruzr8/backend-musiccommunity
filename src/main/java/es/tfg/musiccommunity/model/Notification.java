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
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", updatable = false)
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    // Notification user
    private UserProfile user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_user_id")
    private UserProfile fromUser;

    @Column(name = "post_type", updatable = false)
    private String postType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rec_id")
    private Recommendation recommendation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "notification_date_time")
    private LocalDateTime notificationDateTime;

    private int rate;

    @Column(name = "is_read")
    private boolean isRead;

    public Notification() { }

    // Notificación de nuevo seguidor
    public Notification(String type, UserProfile user, UserProfile fromUser) {
        this.type = type;
        this.user = user;
        this.fromUser = fromUser;
        this.notificationDateTime = LocalDateTime.now();
        this.isRead = false;
    }

    // Notificación de un Seguido que ha hace recomendación
    public Notification(String type, UserProfile user, UserProfile fromUser, Recommendation recommendation) {
        this.type = type;
        this.user = user;
        this.fromUser = fromUser;
        this.recommendation = recommendation;
        this.notificationDateTime = LocalDateTime.now();
        this.isRead = false;
    }

    // Notificación de un Seguidor que putúa tu recomendación
    public Notification(String type, UserProfile user, UserProfile fromUser, Recommendation recommendation, int rate) {
        this.type = type;
        this.user = user;
        this.fromUser = fromUser;
        this.recommendation = recommendation;
        this.rate = rate;
        this.notificationDateTime = LocalDateTime.now();
        this.isRead = false;
    }

    // Notificación de un Seguido que hace post
    public Notification(String type, UserProfile user, UserProfile fromUser, String postType, Post post) {
        this.type = type;
        this.user = user;
        this.fromUser = fromUser;
        this.postType = postType;
        this.post = post;
        this.notificationDateTime = LocalDateTime.now();
        this.isRead = false;
    }

    // Notificación de un Seguidor que hace comentario a tu post
    public Notification(String type, UserProfile user, UserProfile fromUser, String postType, Post post, Comment comment) {
        this.type = type;
        this.user = user;
        this.fromUser = fromUser;
        this.postType = postType;
        this.post = post;
        this.comment = comment;
        this.notificationDateTime = LocalDateTime.now();
        this.isRead = false;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public UserProfile getUser() {
        return user;
    }

    public UserProfile getFromUser() {
        return fromUser;
    }

    public String getPostType() {
        return postType;
    }

    public Post getPost() {
        return post;
    }

    public Recommendation getRecommendation() {
        return recommendation;
    }

    public int getRate() {
        return rate;
    }

    public Comment getComment() {
        return comment;
    }

    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead() {
        this.isRead = true;
    }
}
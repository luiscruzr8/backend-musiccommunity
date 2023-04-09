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
import javax.persistence.Version;
import javax.validation.constraints.Size;

@Entity
@Table(name = "recommendations")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creation_date_time")
    private LocalDateTime creationDateTime;

    @Column(name = "rec_title")
    private String recTitle;

    @Size(max = 4000)
    @Column(name = "rec_text")
    private String recText;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    //Recommendation author/creator
    private UserProfile user;

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "rating")
    private double rating;

    @Version
	@Column(name = "version")
    private int version;
    
    public Recommendation() { }

    public Recommendation(String recTitle, String recText, UserProfile user, Post post){
        this.creationDateTime = LocalDateTime.now();
        this.user = user;
        this.post = post;
        this.recTitle = recTitle;
        this.recText = recText;
        this.rating = 0.0;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public String getRecTitle() {
        return recTitle;
    }

    public void setRecTitle(String recTitle) {
        this.recTitle = recTitle;
    }

    public String getRecText() {
        return recText;
    }

    public void setRecText(String recText) {
        this.recText = recText;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

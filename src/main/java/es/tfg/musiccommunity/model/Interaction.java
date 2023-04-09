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

@Entity
@Table(name = "interactions")
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserProfile user;

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "recommendation_id")
    private Recommendation recommendation;

    private int rate;

    @Column(name = "rate_date_time")
    private LocalDateTime rateDateTime;

    @Version
	@Column(name = "version")
    private int version;

    public Interaction() { }

    public Interaction(UserProfile user, Recommendation rec, int rate) {
        this.user = user;
        this.recommendation = rec;
        this.rate = rate;
        this.rateDateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public Recommendation getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Recommendation recommendation) {
        this.recommendation = recommendation;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public LocalDateTime getRateDateTime() {
        return rateDateTime;
    }

    public void setRateDateTime(LocalDateTime rateDateTime) {
        this.rateDateTime = rateDateTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

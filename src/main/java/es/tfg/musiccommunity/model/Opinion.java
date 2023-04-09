package es.tfg.musiccommunity.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity(name = "Opinion")
@DiscriminatorValue("Opinion")
public class Opinion extends Post {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "score_id")
    private Score score;


    public Opinion() { }

    public Opinion(String title, UserProfile user, Score score,
            String description, Set<Tag> tags) {
        this.title = title;
        this.type = "Opinion";
        this.creationDateTime = LocalDateTime.now();
        this.user = user;
        this.description = description;
        this.score = score;
        this.tags = tags;

    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

}

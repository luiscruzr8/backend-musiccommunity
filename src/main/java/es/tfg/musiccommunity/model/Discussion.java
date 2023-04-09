package es.tfg.musiccommunity.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "Discussion")
@DiscriminatorValue("Discussion")
public class Discussion extends Post {

    public Discussion() { }

    public Discussion(String title, UserProfile user,
            String description, Set<Tag> tags) {
        this.title = title;
        this.type = "Discussion";
        this.creationDateTime = LocalDateTime.now();
        this.user = user;
        this.description = description;
        this.tags = tags;
    }

}

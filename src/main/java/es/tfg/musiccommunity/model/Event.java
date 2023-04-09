package es.tfg.musiccommunity.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "Event")
@DiscriminatorValue("Event")
public class Event extends Post {

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "city_id")
    private City city;


    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    public Event() { }

    public Event(String title, City city, UserProfile user,
            String description, LocalDateTime startDateTime, LocalDateTime endDateTime, Set<Tag> tags) {
        this.title = title;
        this.type = "Event";
        this.creationDateTime = LocalDateTime.now();
        this.city = city;
        this.user = user;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.tags = tags;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}

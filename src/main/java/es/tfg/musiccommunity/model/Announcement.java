package es.tfg.musiccommunity.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "Announcement")
@DiscriminatorValue("Announcement")
public class Announcement extends Post {

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "city_id")
    private City city;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "contact_phone")
    private String contactPhone;

    public Announcement() { }

    public Announcement(String title, City city, UserProfile user,
            String description, LocalDate endDate, String contactPhone, Set<Tag> tags) {
        this.title = title;
        this.type = "Announcement";
        this.creationDateTime = LocalDateTime.now();
        this.city = city;
        this.user = user;
        this.description = description;
        this.endDate = endDate;
        this.contactPhone = contactPhone;
        this.tags = tags;

    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

}
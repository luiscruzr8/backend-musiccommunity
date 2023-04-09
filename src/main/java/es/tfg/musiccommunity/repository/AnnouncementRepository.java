package es.tfg.musiccommunity.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.Announcement;
import es.tfg.musiccommunity.model.City;
import es.tfg.musiccommunity.model.UserProfile;

@Repository
public interface AnnouncementRepository extends BasePostRepository<Announcement>, JpaRepository<Announcement, Long> {

    public List<Announcement> findByEndDateBetweenOrderByCreationDateTimeDesc(LocalDate from, LocalDate to);

    public List<Announcement> findByUserOrderByCreationDateTimeDesc(UserProfile user);

    public List<Announcement> findByCityOrderByCreationDateTimeDesc(City city);
}

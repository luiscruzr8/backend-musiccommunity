package es.tfg.musiccommunity.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.City;
import es.tfg.musiccommunity.model.Event;
import es.tfg.musiccommunity.model.UserProfile;

@Repository
public interface EventRepository extends BasePostRepository<Event>, JpaRepository<Event, Long> {

    public List<Event> findByStartDateTimeBetweenOrderByCreationDateTimeDesc(LocalDateTime from, LocalDateTime to);

    public List<Event> findByUserOrderByCreationDateTimeDesc(UserProfile user);

    public List<Event> findByCityOrderByCreationDateTimeDesc(City city);
}

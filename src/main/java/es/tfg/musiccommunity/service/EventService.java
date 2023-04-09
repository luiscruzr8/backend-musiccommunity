package es.tfg.musiccommunity.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.tfg.musiccommunity.model.City;
import es.tfg.musiccommunity.model.Event;
import es.tfg.musiccommunity.model.ImgPost;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.CityRepository;
import es.tfg.musiccommunity.repository.EventRepository;
import es.tfg.musiccommunity.repository.ImgPostRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.dto.EventDto;
import es.tfg.musiccommunity.service.dto.TagDto;

@Service
public class EventService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ImgPostRepository imgPostRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private NotificationService notificationService;

    /* TODOS LOS EVENTOS */
    public ResponseEntity<List<EventDto>> getAllEvents(String keyword) {
        List<Event> events;
        if (keyword.isEmpty()) {
            events = eventRepository.findThemAllOrderByCreationDateTimeAsc();
        } else {
            events = eventRepository.findThemAllByTitleOrderByCreationDateTimeAsc(keyword);
        }
        List<EventDto> eventsDto = new ArrayList<>(25);
        for (Event event : events) {
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> eventTags = event.getTags().stream().collect(Collectors.toList()) ;
            eventTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : eventTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            eventsDto.add(new EventDto(event.getId(), event.getTitle(), event.getCreationDateTime(), event.getType(),
                    event.getCity().getName(), event.getUser().getLogin(), event.getDescription(),
                    event.getStartDateTime(), event.getEndDateTime(),tags));
        }
        return new ResponseEntity<>((eventsDto), HttpStatus.OK);
    }

    /* INFORMACION DE UN EVENTO POR ID */
    public ResponseEntity<EventDto> getEventInfo(Long id) {
        Optional<Event> e = eventRepository.findById(id);
        if (e.isPresent()) {
            Event event = e.get();
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> eventTags = event.getTags().stream().collect(Collectors.toList()) ;
            eventTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : eventTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            EventDto eventDto = new EventDto(event.getId(), event.getTitle(), event.getCreationDateTime(), event.getType(),
                event.getCity().getName(), event.getUser().getLogin(), event.getDescription(), event.getStartDateTime(),
                event.getEndDateTime(),tags);
            return new ResponseEntity<>((eventDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /* CREACIÓN DE UN EVENTO */
    @Transactional
    public ResponseEntity<Long> createEvent(String login, EventDto eventData) {
        Set<Tag> tags;
        /* COMPROBAMOS QUE LA FECHA DE CREACIÓN SEA POSTERIOR A HOY, Y LA DE FIN POSTERIOR A LA DE CREACIÓN */
        /*      "FECHA-INICIO-ANTES-DE-AHORA                            Ó           FECHA-DE-DESPUES-ES-ANTERIOR */
        if (eventData.getStartDateTime().isBefore(LocalDateTime.now()) || !eventData.getStartDateTime().isBefore(eventData.getEndDateTime())) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        Optional<City> city = cityRepository.findByName(eventData.getCityName());
        if (user.isPresent() && city.isPresent()) {
            tags = commonService.handleTags(eventData.getTags());
            Event event = new Event(eventData.getTitle(), city.get(), user.get(),
                eventData.getDescription(), eventData.getStartDateTime(), eventData.getEndDateTime(), tags);
            /* Guardamos el evento */
            event = eventRepository.save(event);
            notificationService.notifyNewPost(user.get(), user.get().getFollowers(), event);
            return new ResponseEntity<>((event.getId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /* ACTUALIZACIÓN DE UN EVENTO */
    /*
     * OJO: PASARLE AL DTO SOLO LOS VALORES DE "title", "cityName", "description",
     * "startDateTime", "endDateTime"
     */
    @Transactional
    public ResponseEntity<Long> updateEvent(String login, EventDto updatedEvent, Long eventId) {
        Set<Tag> tags;
        /* COMPROBAMOS QUE LA FECHA DE CREACIÓN SEA POSTERIOR A HOY, Y LA DE FIN POSTERIOR A LA DE CREACIÓN */
        /*      "FECHA-INICIO-ANTES-DE-AHORA                            Ó           FECHA-DE-DESPUES-ES-ANTERIOR */
        if (updatedEvent.getStartDateTime().isBefore(LocalDateTime.now()) || !updatedEvent.getStartDateTime().isBefore(updatedEvent.getEndDateTime())) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Optional<Event> eventToUpdate = eventRepository.findById(eventId);
        if (eventToUpdate.isPresent()) { 
            /* COMPROBAMOS QUE QUIEN ACTUALIZA SEA EL MISMO CREADOR */
            Optional<UserProfile> user = userProfileRepository.findByLogin(login);
            if (user.isPresent()) {
                if (!eventToUpdate.get().getUser().equals(user.get())) {
                    return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
                }
                Event event = eventToUpdate.get();
                /* BUSCAMOS LA CIUDAD A ACTUALIZAR (SUPONEMOS QUE NO SEA NINGUNA QUE NO ESTÉ EN NUESTRO SELECTOR) */
                Optional<City> city = cityRepository.findByName(updatedEvent.getCityName());
                if (city.isPresent()) {
                    tags = commonService.handleTags(updatedEvent.getTags());
                    event.setTitle(updatedEvent.getTitle());
                    event.setCity(city.get());
                    event.setDescription(updatedEvent.getDescription());
                    event.setStartDateTime(updatedEvent.getStartDateTime());
                    event.setEndDateTime(updatedEvent.getEndDateTime());
                    event.setTags(tags);
                    event = eventRepository.save(event);

                return new ResponseEntity<>((event.getId()), HttpStatus.OK);
                } 
            } 
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

    /* BORRADO DE UN EVENTO */
    @Transactional
    public ResponseEntity<Void> deleteEvent(String login, Long eventId) {
        /* PRIMERO SE BUSCA EL EVENTO */
        Optional<Event> event = eventRepository.findById(eventId);
        if (!event.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        /* SE COMPRUEBA QUE EL USUARIO SEA SU CREADOR */
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (!user.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        } else {
            if (!user.get().equals(event.get().getUser())) {
                return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
            }
            /* BORRAMOS IMAGEN */
            Optional<ImgPost> imgPost = imgPostRepository.findByPost(event.get());
            if (imgPost.isPresent()) {
                imgPostRepository.delete(imgPost.get());
            }
            /* BORRAMOS NOTIFICACIONES */
            commonService.deleteNotificationsOfPost(event.get());
            /* BORRAMOS COMENTARIOS */
            commonService.deleteComments(event.get());
            /* BORRAMOS RECOMENDACIONES */
            commonService.deleteRecommendations(event.get());
            /* SE BORRA EL EVENTO */
            eventRepository.delete(event.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}

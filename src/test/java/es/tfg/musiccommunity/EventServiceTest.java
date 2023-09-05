package es.tfg.musiccommunity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.tfg.musiccommunity.model.Announcement;
import es.tfg.musiccommunity.model.City;
import es.tfg.musiccommunity.model.Discussion;
import es.tfg.musiccommunity.model.Event;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.AnnouncementRepository;
import es.tfg.musiccommunity.repository.CityRepository;
import es.tfg.musiccommunity.repository.DiscussionRepository;
import es.tfg.musiccommunity.repository.EventRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.EventService;
import es.tfg.musiccommunity.service.dto.EventDto;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class EventServiceTest { 

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private EventService eventService;

    private static final String ESPANA ="España";
    private static final String BARCELONA = "Barcelona";
    private static final double LATITUDE_BARCELONA = 41.3818;
    private static final double LONGITUDE_BARCELONA = 2.1685;
    private static final String VALENCIA = "Valencia";
    private static final double LATITUDE_VALENCIA = 39.4561165;
    private static final double LONGITUDE_VALENCIA = -0.3545661;
    private static final String TITU_1 = "titulo1";
    private static final String TITU_2 = "titulo2";
    private static final String TITU_3 = "titulo3";
    private static final String USUAR_1 = "usuario1";
    private static final String USUAR_2 = "usuario2";
    private static final String USUAR_1_MAIL = "usuario1@mail.com";
    private static final String USUAR_2_MAIL = "usuario2@mail.com";
    private static final String LUISIN = "Luisin";
    private static final String PHONE_NUM_1 = "123456789";
    private static final String PHONE_NUM_2 = "763547676";
    private static final String DESCRIP_1 = "descripcion1";
    private static final String DESCRIP_2 = "descripcion2";
    private static final String DESCRIP_3 = "descripcion3";
    private static final String CONTACT_PHONE_1 = "987654321";
    private static final String LOGIN = "login";
    private static final String TEST = "test";
    private static final String UNEXISTENT = "unexistent";
    private static final String EVENT = "Event";
    private static final String ETIQUETA_1 = "tag";
    private static final String ETIQUETA_2 = "etiqueta";

    @Test
    public void getZeroEventsTest() {
        ResponseEntity<List<EventDto>> events = eventService.getAllEvents("");
        Assert.assertEquals(0,events.getBody().size());
        Assert.assertEquals(HttpStatus.OK, events.getStatusCode());
    }

    @Test
    public void getAllEventsTest(){
        /* Añadimos a un usuario para que pueda añadir posts. */
        UserProfile user = new UserProfile(USUAR_1, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(BARCELONA, ESPANA, LATITUDE_BARCELONA, LONGITUDE_BARCELONA);
        cityRepository.save(city);

        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);

        /* Añadimos 1 evento, 1 discusion y 1 anuncio */
        Event e1 = new Event(TITU_1, city, user, DESCRIP_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), tags);
        eventRepository.save(e1);

        Discussion d1 = new Discussion(TITU_2, user, DESCRIP_2, new HashSet<>());
        discussionRepository.save(d1);

        Announcement a1 = new Announcement(TITU_3, city, user, DESCRIP_3, LocalDate.now().plusDays(1), CONTACT_PHONE_1, new HashSet<>());
        announcementRepository.save(a1);

        Event e2 = new Event(TITU_2, city, user, DESCRIP_2, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusHours(2), new HashSet<>());
        eventRepository.save(e2);

        ResponseEntity<List<EventDto>> events = eventService.getAllEvents("");
        Assert.assertEquals(2, events.getBody().size());
        /* PODEMOS COMPROBAR QUE EL PRIMERO ES EL EVENTO UNO PORQUE ESTAN ORDENADOS POR CREATION DATE */
        Assert.assertEquals(e1.getId(),events.getBody().get(0).getId());
        Assert.assertEquals(e1.getDescription(),events.getBody().get(0).getDescription());
        Assert.assertEquals(e1.getType(),events.getBody().get(0).getType());
        Assert.assertEquals(e1.getTitle(),events.getBody().get(0).getTitle());
        Assert.assertEquals(e1.getCity().getName(),events.getBody().get(0).getCityName());
        Assert.assertEquals(e1.getCreationDateTime(),events.getBody().get(0).getCreationDateTime());
        Assert.assertEquals(e1.getEndDateTime(),events.getBody().get(0).getEndDateTime());
        Assert.assertEquals(e1.getStartDateTime(),events.getBody().get(0).getStartDateTime());
        Assert.assertEquals(e1.getUser().getLogin(),events.getBody().get(0).getLogin());
        Assert.assertEquals(ETIQUETA_2, events.getBody().get(0).getTags().get(0).getTagName());
        Assert.assertEquals(ETIQUETA_1, events.getBody().get(0).getTags().get(1).getTagName());
    }

    @Test
    public void getUnexistentEventInfoTest() {
        ResponseEntity<EventDto> events = eventService.getEventInfo(1L);
        Assert.assertEquals(null, events.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, events.getStatusCode());
    }

    @Test
    public void getEventInfoTest() {
        /* Añadimos dos usuarios para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(USUAR_1, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(USUAR_2, USUAR_2_MAIL, USUAR_2, PHONE_NUM_2, "");
        userProfileRepository.save(user2);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(VALENCIA, ESPANA, LATITUDE_VALENCIA, LONGITUDE_VALENCIA);
        cityRepository.save(city);

        /* Añadimos 2 eventos */
        Event e1 = new Event(TITU_1, city, user1, DESCRIP_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);

        Event e2 = new Event(TITU_2, city, user2, DESCRIP_2, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusHours(2), tags);
        eventRepository.save(e2);

        ResponseEntity<EventDto> event1 = eventService.getEventInfo(e1.getId());
        Assert.assertEquals(e1.getId(), event1.getBody().getId());
        Assert.assertEquals(e1.getUser().getLogin(), event1.getBody().getLogin());

        ResponseEntity<EventDto> event2 = eventService.getEventInfo(e2.getId());
        Assert.assertEquals(e2.getId(), event2.getBody().getId());
        Assert.assertEquals(e2.getUser().getLogin(), event2.getBody().getLogin());
        Assert.assertEquals(ETIQUETA_2, event2.getBody().getTags().get(0).getTagName());
        Assert.assertEquals(ETIQUETA_1, event2.getBody().getTags().get(1).getTagName());
    }

    @Test
    public void deleteUnexistentEventAndUnexistentUserTest() {
        ResponseEntity<Void> response = eventService.deleteEvent(UNEXISTENT,1L);
        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        /* Añadimos un usuario para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(USUAR_1, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user1);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(BARCELONA, ESPANA, LATITUDE_BARCELONA, LONGITUDE_BARCELONA);
        cityRepository.save(city);

        /* Añadimos 1 evento */
        Event e1 = new Event(TITU_1, city, user1, DESCRIP_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        ResponseEntity<Void> response2 = eventService.deleteEvent(UNEXISTENT,e1.getId());
        Assert.assertEquals(null, response2.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    public void deleteEventUnauthorizedUserTest() {
        /* Añadimos dos usuarios para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(USUAR_1, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUAR_2, USUAR_2_MAIL, USUAR_2, PHONE_NUM_2, "");
        userProfileRepository.save(user2);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(VALENCIA, ESPANA, LATITUDE_VALENCIA, LONGITUDE_VALENCIA);
        cityRepository.save(city);

        /* Añadimos 1 eventos */
        Event e1 = new Event(TITU_1, city, user2, DESCRIP_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        ResponseEntity<Void> response = eventService.deleteEvent(user1.getLogin(),e1.getId());
        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void deleteEventTest() {
        /* Añadimos dos usuarios para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(USUAR_1, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(USUAR_2, USUAR_2_MAIL, USUAR_2, PHONE_NUM_2, "");
        userProfileRepository.save(user2);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(BARCELONA, ESPANA, LATITUDE_BARCELONA, LONGITUDE_BARCELONA);
        cityRepository.save(city);

        /* Añadimos 2 eventos */
        Event e1 = new Event(TITU_1, city, user1, DESCRIP_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        Event e2 = new Event(TITU_2, city, user2, DESCRIP_2, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusHours(2), new HashSet<>());
        eventRepository.save(e2);

        ResponseEntity<Void> response = eventService.deleteEvent(user1.getLogin(),e1.getId());
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<Void> response2 = eventService.deleteEvent(user1.getLogin(),e1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        Assert.assertEquals(null, response2.getBody());
    }

    @Test
    public void createEventInvalidDatesTest(){
        /* FECHA INICIO ANTERIOR A AHORA */
        EventDto evData = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(3), new ArrayList<>());
        ResponseEntity<Long> created = eventService.createEvent(LOGIN, evData);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());

        /* FECHA FIN ANTERIOR A FECHA INICIO */
        EventDto evData2 = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().plusHours(1), LocalDateTime.now(), new ArrayList<>());
        ResponseEntity<Long> created2 = eventService.createEvent(LOGIN, evData2);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created2.getStatusCode());
        Assert.assertEquals(null, created2.getBody());

        /* FECHA INICIO = FECHA FIN */
        EventDto evData3 = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());
        ResponseEntity<Long> created3 = eventService.createEvent(LOGIN, evData3);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created3.getStatusCode());
        Assert.assertEquals(null, created3.getBody());

        /* FECHA INICIO Y FECHA FIN  ANTERIORES A AHORA*/
        EventDto evData4 = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30), new ArrayList<>());
        ResponseEntity<Long> created4 = eventService.createEvent(LOGIN, evData4);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created4.getStatusCode());
        Assert.assertEquals(null, created4.getBody());

        /* FECHA INICIO = FECHA FIN  ANTERIORES A AHORA*/
        EventDto evData5 = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(1), new ArrayList<>());
        ResponseEntity<Long> created5 = eventService.createEvent(LOGIN, evData5);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created5.getStatusCode());
        Assert.assertEquals(null, created5.getBody());
    }

    @Test
    public void createEventUnexistentUserAndUnexistentCityTest() {
        /* AMBOS NO EXISTEN */
        EventDto evData = new EventDto(null, null, null, null, VALENCIA, LUISIN, null, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new ArrayList<>());
        ResponseEntity<Long> created = eventService.createEvent(LOGIN, evData);
        Assert.assertEquals(HttpStatus.NOT_FOUND, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());

        UserProfile user1 = new UserProfile(USUAR_1, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user1);
        /* NO EXISTE LA CIUDAD */
        ResponseEntity<Long> created2 = eventService.createEvent(user1.getLogin(), evData);
        Assert.assertEquals(HttpStatus.NOT_FOUND, created2.getStatusCode());
        Assert.assertEquals(null, created2.getBody());
        /* BORRAMOS TODOS LOS USUARIOS POR SI ACASO */
        userProfileRepository.deleteAll();

        City city = new City(VALENCIA, ESPANA, LATITUDE_VALENCIA, LONGITUDE_VALENCIA);
        cityRepository.save(city);
        /* NO EXISTE EL USUARIO */
        ResponseEntity<Long> created3 = eventService.createEvent(LUISIN, evData);
        Assert.assertEquals(HttpStatus.NOT_FOUND, created3.getStatusCode());
        Assert.assertEquals(null, created3.getBody());
    }
    
    @Test
    public void createEventTest(){
        UserProfile user1 = new UserProfile(LUISIN, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user1);
        City city = new City(BARCELONA, ESPANA, LATITUDE_BARCELONA, LONGITUDE_BARCELONA);
        cityRepository.save(city);

        LocalDateTime start = LocalDateTime.now().plusMinutes(10);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        EventDto evData = new EventDto(null, TEST, null, null, BARCELONA, LUISIN, TEST, start, end, new ArrayList<>());
        ResponseEntity<Long> created = eventService.createEvent(LUISIN, evData);
        Assert.assertEquals(HttpStatus.CREATED, created.getStatusCode());

        ResponseEntity<EventDto> result = eventService.getEventInfo(created.getBody());        
        Assert.assertEquals(evData.getTitle(), result.getBody().getTitle());
        Assert.assertEquals(EVENT, result.getBody().getType());
        Assert.assertEquals(evData.getCityName(), result.getBody().getCityName());
        Assert.assertEquals(LUISIN, result.getBody().getLogin());
        Assert.assertEquals(start, result.getBody().getStartDateTime());
        Assert.assertEquals(end, result.getBody().getEndDateTime());
        Assert.assertEquals(evData.getDescription(), result.getBody().getDescription());
    }

    @Test
    public void updateEventInvalidDatesTest(){
        /* FECHA INICIO ANTERIOR A AHORA */
        EventDto evData = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(3), new ArrayList<>());
        ResponseEntity<Long> created = eventService.updateEvent(LOGIN, evData, 1L);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());

        /* FECHA FIN ANTERIOR A FECHA INICIO */
        EventDto evData2 = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().plusHours(1), LocalDateTime.now(), new ArrayList<>());
        ResponseEntity<Long> created2 = eventService.updateEvent(LOGIN, evData2, 1L);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created2.getStatusCode());
        Assert.assertEquals(null, created2.getBody());

        /* FECHA INICIO = FECHA FIN */
        EventDto evData3 = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());
        ResponseEntity<Long> created3 = eventService.updateEvent(LOGIN, evData3, 1L);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created3.getStatusCode());
        Assert.assertEquals(null, created3.getBody());

        /* FECHA INICIO Y FECHA FIN  ANTERIORES A AHORA*/
        EventDto evData4 = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30), new ArrayList<>());
        ResponseEntity<Long> created4 = eventService.updateEvent(LOGIN, evData4, 1L);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created4.getStatusCode());
        Assert.assertEquals(null, created4.getBody());

        /* FECHA INICIO = FECHA FIN  ANTERIORES A AHORA*/
        EventDto evData5 = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(1), new ArrayList<>());
        ResponseEntity<Long> created5 = eventService.updateEvent(LOGIN, evData5, 1L);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created5.getStatusCode());
        Assert.assertEquals(null, created5.getBody());
    }

    @Test
    public void updateUnexistentEventTest(){
        EventDto evData = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(3), new ArrayList<>());
        ResponseEntity<Long> created = eventService.updateEvent(LOGIN, evData, 1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());
    }

    @Test
    public void updateEventUnexistentUserTest(){
        UserProfile user1 = new UserProfile(USUAR_1, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user1);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(VALENCIA, ESPANA, LATITUDE_VALENCIA, LONGITUDE_VALENCIA);
        cityRepository.save(city);

        /* Añadimos 3 eventos y 1 anuncio */
        Event e1 = new Event(TITU_1, city, user1, DESCRIP_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        EventDto evData = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(3), new ArrayList<>());
        ResponseEntity<Long> created = eventService.updateEvent(LOGIN, evData, e1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());
    }

    @Test
    public void updateEventUnauthorizedUserTest(){
        UserProfile user1 = new UserProfile(USUAR_1, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUAR_2, USUAR_2_MAIL, USUAR_2, PHONE_NUM_2, "");
        userProfileRepository.save(user2);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(BARCELONA, ESPANA, LATITUDE_BARCELONA, LONGITUDE_BARCELONA);
        cityRepository.save(city);

        /* Añadimos 3 eventos y 1 anuncio */
        Event e1 = new Event(TITU_1, city, user1, DESCRIP_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        EventDto evData = new EventDto(null, null, null, null, null, null, null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(3), new ArrayList<>());
        ResponseEntity<Long> created = eventService.updateEvent(user2.getLogin(), evData, e1.getId());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());
    }

    @Test
    public void updatedEventUnexistentCityTest(){
        UserProfile user1 = new UserProfile(USUAR_1, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUAR_2, USUAR_2_MAIL, USUAR_2, PHONE_NUM_2, "");
        userProfileRepository.save(user2);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(VALENCIA, ESPANA, LATITUDE_VALENCIA, LONGITUDE_VALENCIA);
        cityRepository.save(city);

        Event e1 = new Event(TITU_1, city, user1, DESCRIP_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        EventDto evData = new EventDto(null, null, null, null, BARCELONA, null, null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(3), new ArrayList<>());
        ResponseEntity<Long> created = eventService.updateEvent(user1.getLogin(), evData, e1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());
    }

    @Test
    public void updateEventTest() {
        UserProfile user1 = new UserProfile(USUAR_1, USUAR_1_MAIL, USUAR_1, PHONE_NUM_1, "");
        userProfileRepository.save(user1);
        City city = new City(VALENCIA, ESPANA, LATITUDE_VALENCIA, LONGITUDE_VALENCIA);
        cityRepository.save(city);

        City city2 = new City(BARCELONA, ESPANA, LATITUDE_BARCELONA, LONGITUDE_BARCELONA);
        cityRepository.save(city2);

        LocalDateTime start = LocalDateTime.now().plusMinutes(10);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        EventDto evData = new EventDto(null, TEST, null, null, VALENCIA, LUISIN, TEST, start, end, new ArrayList<>());
        Long created = eventService.createEvent(user1.getLogin(), evData).getBody();
    
        EventDto evEdited = new EventDto(null, "newTest", null, EVENT, BARCELONA, USUAR_1, "newTest", LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(11), new ArrayList<>());
        ResponseEntity<Long> edited = eventService.updateEvent(user1.getLogin(), evEdited, created);
        Assert.assertEquals(HttpStatus.OK, edited.getStatusCode());

        ResponseEntity<EventDto> result = eventService.getEventInfo(edited.getBody());
        Assert.assertEquals(evEdited.getTitle(), result.getBody().getTitle());
        Assert.assertEquals(evEdited.getCityName(), result.getBody().getCityName());
        Assert.assertEquals(evEdited.getDescription(), result.getBody().getDescription());
        Assert.assertEquals(evEdited.getStartDateTime(), result.getBody().getStartDateTime());
        Assert.assertEquals(evEdited.getEndDateTime(), result.getBody().getEndDateTime());

    }

}

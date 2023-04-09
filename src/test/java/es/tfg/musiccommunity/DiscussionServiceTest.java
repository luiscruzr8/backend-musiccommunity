package es.tfg.musiccommunity;

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

import es.tfg.musiccommunity.model.Event;
import es.tfg.musiccommunity.model.City;
import es.tfg.musiccommunity.model.Discussion;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.CityRepository;
import es.tfg.musiccommunity.repository.DiscussionRepository;
import es.tfg.musiccommunity.repository.EventRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.DiscussionService;
import es.tfg.musiccommunity.service.dto.DiscussionDto;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class DiscussionServiceTest { 

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private DiscussionService discussionService;

    private static final String ESPANA ="España";
    private static final String MADRID = "Madrid";
    private static final double LATITUDE_MADRID = 40.4893538;
    private static final double LONGITUDE_MADRID = -3.6827461;
    private static final String TITL_1 = "titulo1";
    private static final String TITL_2 = "titulo2";
    private static final String TITL_3 = "titulo3";
    private static final String UP_1 = "usuario1";
    private static final String UP_2 = "usuario2";
    private static final String UP_1_MAIL = "usuario1@mail.com";
    private static final String UP_2_MAIL = "usuario2@mail.com";
    private static final String LUIS = "Luisin";
    private static final String NUM_PHONE_1 = "123456789";
    private static final String NUM_PHONE_2 = "763547676";
    private static final String DES_1 = "descripcion1";
    private static final String DES_2 = "descripcion2";
    private static final String DES_3 = "descripcion3";
    private static final String LOGIN = "login";
    private static final String TEST = "test";
    private static final String UNEXISTENT = "unexistent";
    private static final String DISCUSSION = "Discussion";
    private static final String ETIQUETA_1 = "tag";
    private static final String ETIQUETA_2 = "etiqueta";

    @Test
    public void getZeroDiscussionsTest() {
        ResponseEntity<List<DiscussionDto>> discussions = discussionService.getAllDiscussions("");
        Assert.assertEquals(0,discussions.getBody().size());
        Assert.assertEquals(HttpStatus.OK, discussions.getStatusCode());
    }

    @Test
    public void getAllDiscussionsTest(){
        /* Añadimos dos usuarios para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(UP_1, UP_1_MAIL, UP_1, NUM_PHONE_1);
        user1 = userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(UP_2, UP_2_MAIL, UP_2, NUM_PHONE_2);
        user2 = userProfileRepository.save(user2);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(MADRID, ESPANA, LATITUDE_MADRID, LONGITUDE_MADRID);
        city = cityRepository.save(city);

        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);

        /* Añadimos 2 eventos y 2 anuncios */
        Discussion d1 = new Discussion(TITL_1, user1, DES_1, tags);
        discussionRepository.save(d1);

        Discussion d2 = new Discussion(TITL_2, user2, DES_2, new HashSet<>());
        discussionRepository.save(d2);

        Event e1 = new Event(TITL_1, city, user1, DES_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        ResponseEntity<List<DiscussionDto>> discussions = discussionService.getAllDiscussions("");
        Assert.assertEquals(2, discussions.getBody().size());
        /* PODEMOS COMPROBAR QUE EL PRIMERO ES EL DiscussionO UNO PORQUE ESTAN ORDENADOS POR CREATION DATE */
        Assert.assertEquals(d1.getId(),discussions.getBody().get(0).getId());
        Assert.assertEquals(d1.getDescription(),discussions.getBody().get(0).getDescription());
        Assert.assertEquals(d1.getType(),discussions.getBody().get(0).getType());
        Assert.assertEquals(d1.getTitle(),discussions.getBody().get(0).getTitle());
        Assert.assertEquals(d1.getCreationDateTime(),discussions.getBody().get(0).getCreationDateTime());
        Assert.assertEquals(d1.getUser().getLogin(),discussions.getBody().get(0).getLogin());
        Assert.assertEquals(ETIQUETA_2, discussions.getBody().get(0).getTags().get(0).getTagName());
        Assert.assertEquals(ETIQUETA_1, discussions.getBody().get(0).getTags().get(1).getTagName());
    }

    @Test
    public void getUnexistentDiscussionInfoTest() {
        ResponseEntity<DiscussionDto> discussions = discussionService.getDiscussionInfo(1L);
        Assert.assertEquals(null, discussions.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, discussions.getStatusCode());
    }

    @Test
    public void getDiscussionInfoTest() {
        /* Añadimos dos usuarios para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(UP_1, UP_1_MAIL, UP_1, NUM_PHONE_1);
        userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(UP_2, UP_2_MAIL, UP_2, NUM_PHONE_2);
        userProfileRepository.save(user2);

        /* Añadimos 2 anuncios */
        Discussion d1 = new Discussion(TITL_3, user2, DES_3, new HashSet<>());
        discussionRepository.save(d1);

        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);
        
        Discussion d2 = new Discussion(TITL_2, user1, DES_2, tags);
        discussionRepository.save(d2);

        ResponseEntity<DiscussionDto> discussion1 = discussionService.getDiscussionInfo(d1.getId());
        Assert.assertEquals(d1.getId(), discussion1.getBody().getId());
        Assert.assertEquals(d1.getUser().getLogin(), discussion1.getBody().getLogin());

        ResponseEntity<DiscussionDto> discussion2 = discussionService.getDiscussionInfo(d2.getId());
        Assert.assertEquals(d2.getId(), discussion2.getBody().getId());
        Assert.assertEquals(d2.getUser().getLogin(), discussion2.getBody().getLogin());

        Assert.assertEquals(ETIQUETA_2, discussion2.getBody().getTags().get(0).getTagName());
        Assert.assertEquals(ETIQUETA_1, discussion2.getBody().getTags().get(1).getTagName());
    }

    @Test
    public void deleteUnexistentDiscussionAndUnexistentUserTest() {
        ResponseEntity<Void> response = discussionService.deleteDiscussion(UNEXISTENT,1L);
        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        /* Añadimos un usuario para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(UP_1, UP_1_MAIL, UP_1, NUM_PHONE_1);
        userProfileRepository.save(user1);

        Discussion d1 = new Discussion(TITL_1, user1, DES_1, new HashSet<>());
        discussionRepository.save(d1);

        ResponseEntity<Void> response2 = discussionService.deleteDiscussion(UNEXISTENT,d1.getId());
        Assert.assertEquals(null, response2.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    public void deleteDiscussionUnauthorizedUserTest() {
        /* Añadimos dos usuarios para que pueda añadir posts. */
        UserProfile user1 = new UserProfile(UP_1, UP_1_MAIL, UP_1, NUM_PHONE_1);
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(UP_2, UP_2_MAIL, UP_2, NUM_PHONE_2);
        userProfileRepository.save(user2);

        Discussion d1 = new Discussion(TITL_2, user1, DES_2, new HashSet<>());
        discussionRepository.save(d1);

        ResponseEntity<Void> response = discussionService.deleteDiscussion(user2.getLogin(),d1.getId());
        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void deleteDiscussionTest() {
        /* Añadimos un usuario para que pueda añadir posts. */
        UserProfile user2 = new UserProfile(UP_2, UP_2_MAIL, UP_2, NUM_PHONE_2);
        userProfileRepository.save(user2);

        Discussion d1 = new Discussion(TITL_2, user2, DES_2, new HashSet<>());
        discussionRepository.save(d1);

        ResponseEntity<Void> response = discussionService.deleteDiscussion(user2.getLogin(),d1.getId());
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    
        ResponseEntity<Void> response2 = discussionService.deleteDiscussion(user2.getLogin(),d1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        Assert.assertEquals(null, response2.getBody());
    }

    @Test
    public void createDiscussionUnexistentUserTest(){
        DiscussionDto disData = new DiscussionDto(null, null, null, null, LUIS, null, new ArrayList<>());
        ResponseEntity<Long> created = discussionService.createDiscussion(UNEXISTENT, disData);
        Assert.assertEquals(HttpStatus.NOT_FOUND, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());
    }

    @Test
    public void createDiscussionTest() {
        UserProfile user1 = new UserProfile(UP_1, UP_1_MAIL, UP_1, NUM_PHONE_1);
        userProfileRepository.save(user1);
        DiscussionDto disData = new DiscussionDto(null, TEST, null, null, UP_1, TEST, new ArrayList<>());
        ResponseEntity<Long> created = discussionService.createDiscussion(UP_1, disData);
        Assert.assertEquals(HttpStatus.CREATED, created.getStatusCode());

        ResponseEntity<DiscussionDto> result = discussionService.getDiscussionInfo(created.getBody());
        Assert.assertEquals(disData.getTitle(), result.getBody().getTitle());
        Assert.assertEquals(DISCUSSION, result.getBody().getType());
        Assert.assertEquals(disData.getLogin(), result.getBody().getLogin());
        Assert.assertEquals(disData.getDescription(), result.getBody().getDescription());
    }

    @Test
    public void updateDiscussionUnauthorizedUserTest() {
        UserProfile user1 = new UserProfile(LUIS, UP_1_MAIL, UP_1, NUM_PHONE_1);
        userProfileRepository.save(user1);
        Discussion d1 = new Discussion(TITL_3, user1, DES_3, new HashSet<>());
        discussionRepository.save(d1);
        UserProfile user2 = new UserProfile(UP_2, UP_2_MAIL, UP_2, NUM_PHONE_2);
        userProfileRepository.save(user2);
        DiscussionDto updatedDis = new DiscussionDto(null, null, null, null, LUIS, null, new ArrayList<>());
        ResponseEntity<Long> updated = discussionService.updateDiscussion(user2.getLogin(), updatedDis, d1.getId());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, updated.getStatusCode());
        Assert.assertEquals(null, updated.getBody());
    }

    @Test
    public void updateUnexistentDiscussionTest() {
        DiscussionDto updatedDis = new DiscussionDto(null, null, null, null, LUIS, null, new ArrayList<>());
        ResponseEntity<Long> updated = discussionService.updateDiscussion(LOGIN, updatedDis, 1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, updated.getStatusCode());
        Assert.assertEquals(null, updated.getBody());
    }

    @Test
    public void updateDiscussionUnexistentUserTest(){
        UserProfile user1 = new UserProfile(LUIS, UP_1_MAIL, UP_1, NUM_PHONE_1);
        userProfileRepository.save(user1);
        Discussion d1 = new Discussion(TITL_1, user1, DES_2, new HashSet<>());
        discussionRepository.save(d1);
        
        DiscussionDto updatedDis = new DiscussionDto(null, null, null, null, LUIS, null, new ArrayList<>());
        ResponseEntity<Long> updated = discussionService.updateDiscussion(UNEXISTENT, updatedDis, d1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, updated.getStatusCode());
        Assert.assertEquals(null, updated.getBody());
    }

    @Test
    public void updateDiscussionTest() {
        UserProfile user1 = new UserProfile(LUIS, UP_1_MAIL, UP_1, NUM_PHONE_1);
        userProfileRepository.save(user1);
        Discussion d1 = new Discussion(TITL_2, user1, DES_2, new HashSet<>());
        discussionRepository.save(d1);
        DiscussionDto disData = new DiscussionDto(null, TEST, null, null, LUIS, TEST, new ArrayList<>());
        ResponseEntity<Long> updated = discussionService.updateDiscussion(user1.getLogin(), disData, d1.getId());
        Assert.assertEquals(HttpStatus.OK, updated.getStatusCode());

        ResponseEntity<DiscussionDto> result = discussionService.getDiscussionInfo(d1.getId());
        Assert.assertEquals(disData.getTitle(), result.getBody().getTitle());
        Assert.assertEquals(DISCUSSION, result.getBody().getType());
        Assert.assertEquals(disData.getLogin(), result.getBody().getLogin());
        Assert.assertEquals(disData.getDescription(), result.getBody().getDescription());
    }
}   

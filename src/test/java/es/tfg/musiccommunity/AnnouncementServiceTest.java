package es.tfg.musiccommunity;

import java.time.LocalDate;
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
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.AnnouncementRepository;
import es.tfg.musiccommunity.repository.CityRepository;
import es.tfg.musiccommunity.repository.DiscussionRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.AnnouncementService;
import es.tfg.musiccommunity.service.dto.AnnouncementDto;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class AnnouncementServiceTest { 

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private AnnouncementService announcementService;

    private static final String ESP ="España";
    private static final String SEV = "Sevilla";
    private static final double LATITUDE_SEV = 37.3914105;
    private static final double LONGITUDE_SEV = -5.9591776;
    private static final String SANT = "Santiago de Compostela";
    private static final double LATITUDE_SANT = 42.8802410;
    private static final double LONGITUDE_SANT = -8.5473632;
    private static final String TITLE_1 = "titulo1";
    private static final String TITLE_2 = "titulo2";
    private static final String TITLE_3 = "titulo3";
    private static final String TITLE_4 = "titulo4";
    private static final String US_1 = "usuario1";
    private static final String US_2 = "usuario2";
    private static final String US_1_MAIL = "usuario1@mail.com";
    private static final String US_2_MAIL = "usuario2@mail.com";
    private static final String LUISIN = "Luisin";
    private static final String PHONE_NUMBER_1 = "123456789";
    private static final String PHONE_NUMBER_2 = "763547676";
    private static final String DESCRI_1 = "descripcion1";
    private static final String DESCRI_2 = "descripcion2";
    private static final String DESCRI_3 = "descripcion3";
    private static final String DESCRI_4 = "descripcion4";
    private static final String CONT_PHONE_1 = "987654321";
    private static final String CONT_PHONE_2 = "912876543";
    private static final String CONT_PHONE_3 = "876562434";
    private static final String CONT_PHONE_4 = "746465465";
    private static final String CONT_PHONE_5 = "252068981";
    private static final String CONT_PHONE_6 = "123768364";
    private static final String LOGIN = "login";
    private static final String TEST = "test";
    private static final String UNEXISTENT = "unexistent";
    private static final String INVALID_PHONE = "alkajsdkj";
    private static final String ANNOUNCEMENT = "Announcement";
    private static final String TAG_1 = "tag";
    private static final String TAG_2 = "etiqueta";

    @Test
    public void getZeroAnnouncementsTest() {
        ResponseEntity<List<AnnouncementDto>> announcements = announcementService.getAllAnnouncements("");
        Assert.assertEquals(0,announcements.getBody().size());
        Assert.assertEquals(HttpStatus.OK, announcements.getStatusCode());
    }

    @Test
    public void getAllAnnouncementsTest(){
        /* Añadimos dos usuarios para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(US_1, US_1_MAIL, US_1, PHONE_NUMBER_1);
        user1 = userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(US_2, US_2_MAIL, US_2, PHONE_NUMBER_2);
        user2 = userProfileRepository.save(user2);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(SEV, ESP, LATITUDE_SEV, LONGITUDE_SEV);
        city = cityRepository.save(city);

        /* Añadimos 2 eventos y 2 anuncios */
        Discussion d1 = new Discussion(TITLE_1, user1, DESCRI_1, new HashSet<Tag>());
        discussionRepository.save(d1);

        Tag t1 = new Tag(TAG_1);
        Tag t2 = new Tag(TAG_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);

        Discussion d2 = new Discussion(TITLE_2, user2, DESCRI_2, new HashSet<>());
        discussionRepository.save(d2);

        Announcement a1 = new Announcement(TITLE_3, city, user1, DESCRI_3, LocalDate.now().plusDays(1), CONT_PHONE_1, tags);
        announcementRepository.save(a1);

        ResponseEntity<List<AnnouncementDto>> announcements = announcementService.getAllAnnouncements("");
        Assert.assertEquals(1, announcements.getBody().size());
        /* PODEMOS COMPROBAR QUE EL PRIMERO ES EL AnnouncementO UNO PORQUE ESTAN ORDENADOS POR CREATION DATE */
        Assert.assertEquals(a1.getId(),announcements.getBody().get(0).getId());
        Assert.assertEquals(a1.getDescription(),announcements.getBody().get(0).getDescription());
        Assert.assertEquals(a1.getType(),announcements.getBody().get(0).getType());
        Assert.assertEquals(a1.getTitle(),announcements.getBody().get(0).getTitle());
        Assert.assertEquals(a1.getCity().getName(),announcements.getBody().get(0).getCityName());
        Assert.assertEquals(a1.getCreationDateTime(),announcements.getBody().get(0).getCreationDateTime());
        Assert.assertEquals(a1.getEndDate(),announcements.getBody().get(0).getEndDate());
        Assert.assertEquals(a1.getContactPhone(),announcements.getBody().get(0).getContactPhone());
        Assert.assertEquals(a1.getUser().getLogin(),announcements.getBody().get(0).getLogin());
        Assert.assertEquals(TAG_2, announcements.getBody().get(0).getTags().get(0).getTagName());
        Assert.assertEquals(TAG_1, announcements.getBody().get(0).getTags().get(1).getTagName());
    }

    @Test
    public void getUnexistentAnnouncementInfoTest() {
        ResponseEntity<AnnouncementDto> announcements = announcementService.getAnnouncementInfo(1L);
        Assert.assertEquals(null, announcements.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, announcements.getStatusCode());
    }

    @Test
    public void getAnnouncementInfoTest() {
        /* Añadimos dos usuarios para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(US_1, US_1_MAIL, US_1, PHONE_NUMBER_1);
        userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(US_2, US_2_MAIL, US_2, PHONE_NUMBER_2);
        userProfileRepository.save(user2);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(SANT, ESP, LATITUDE_SANT, LONGITUDE_SANT);
        cityRepository.save(city);

        /* Añadimos 2 anuncios */
        Announcement a1 = new Announcement(TITLE_3, city, user2, DESCRI_3, LocalDate.now().plusDays(3), CONT_PHONE_2, new HashSet<>());
        announcementRepository.save(a1);

        Tag t1 = new Tag(TAG_1);
        Tag t2 = new Tag(TAG_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);
        
        Announcement a2 = new Announcement(TITLE_4, city, user1, DESCRI_4, LocalDate.now().plusDays(1), CONT_PHONE_4, tags);
        announcementRepository.save(a2);

        ResponseEntity<AnnouncementDto> announcement1 = announcementService.getAnnouncementInfo(a1.getId());
        Assert.assertEquals(a1.getId(), announcement1.getBody().getId());
        Assert.assertEquals(a1.getUser().getLogin(), announcement1.getBody().getLogin());

        ResponseEntity<AnnouncementDto> announcement2 = announcementService.getAnnouncementInfo(a2.getId());
        Assert.assertEquals(a2.getId(), announcement2.getBody().getId());
        Assert.assertEquals(a2.getUser().getLogin(), announcement2.getBody().getLogin());
        Assert.assertEquals(TAG_2, announcement2.getBody().getTags().get(0).getTagName());
        Assert.assertEquals(TAG_1, announcement2.getBody().getTags().get(1).getTagName());
    }

    @Test
    public void deleteUnexistentAnnouncementAndUnexistentUserTest() {
        ResponseEntity<Void> response = announcementService.deleteAnnouncement(UNEXISTENT,1L);
        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        /* Añadimos un usuario para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(US_1, US_1_MAIL, US_1, PHONE_NUMBER_1);
        userProfileRepository.save(user1);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(SEV, ESP, LATITUDE_SEV, LONGITUDE_SEV);
        city = cityRepository.save(city);

        Announcement a1 = new Announcement(TITLE_1, city, user1, DESCRI_2, LocalDate.now().plusDays(3), CONT_PHONE_1, new HashSet<>());
        announcementRepository.save(a1);

        ResponseEntity<Void> response2 = announcementService.deleteAnnouncement(UNEXISTENT,a1.getId());
        Assert.assertEquals(null, response2.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    public void deleteAnnouncementUnauthorizedUserTest() {
        /* Añadimos dos usuarios para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(US_1, US_1_MAIL, US_1, PHONE_NUMBER_1);
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(US_2, US_2_MAIL, US_2, PHONE_NUMBER_2);
        userProfileRepository.save(user2);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(SANT, ESP, LATITUDE_SANT, LONGITUDE_SANT);
        cityRepository.save(city);

        Announcement a1 = new Announcement(TITLE_1, city, user1, DESCRI_2, LocalDate.now().plusDays(5), CONT_PHONE_1, new HashSet<>());
        announcementRepository.save(a1);

        ResponseEntity<Void> response = announcementService.deleteAnnouncement(user2.getLogin(),a1.getId());
        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void deleteAnnouncementTest() {
        /* Añadimos dos usuarios para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(US_1, US_1_MAIL, US_1, PHONE_NUMBER_1);
        userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(US_2, US_2_MAIL, US_2, PHONE_NUMBER_2);
        userProfileRepository.save(user2);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(SEV, ESP, LATITUDE_SEV, LONGITUDE_SEV);
        city = cityRepository.save(city);

        /* Añadimos 2 anuncios */
        Announcement a1 = new Announcement(TITLE_1, city, user1, DESCRI_2, LocalDate.now().plusDays(3), CONT_PHONE_1, new HashSet<>());
        announcementRepository.save(a1);
        
        Announcement a2 = new Announcement(TITLE_2, city, user2, DESCRI_2, LocalDate.now().plusDays(1), CONT_PHONE_3, new HashSet<>());
        announcementRepository.save(a2);

        ResponseEntity<Void> response = announcementService.deleteAnnouncement(user1.getLogin(),a1.getId());
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    
        ResponseEntity<Void> response2 = announcementService.deleteAnnouncement(user1.getLogin(),a1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        Assert.assertEquals(null, response2.getBody());
    }

    @Test
    public void createAnnouncementInvalidDateOrPhoneNumberTest() {
        /* FECHA FIN ANTERIOR A HOY */
        AnnouncementDto anData = new AnnouncementDto(null, null, null, null, null, null, null, LocalDate.now().minusDays(1),null, new ArrayList<>());
        ResponseEntity<Long> created = announcementService.createAnnouncement(LOGIN, anData);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());
        /* NUMERO DE TELEFONO INVALIDO */
        AnnouncementDto anData2 = new AnnouncementDto(null, null, null, null, null, null, null, LocalDate.now(),INVALID_PHONE, new ArrayList<>());
        ResponseEntity<Long> created2 = announcementService.createAnnouncement(LOGIN, anData2);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, created2.getStatusCode());
        Assert.assertEquals(null, created2.getBody());
    }

    @Test
    public void createAnnouncementUnexistentUserAndUnexistentCityTest(){
        /* FECHA FIN IGUAL A HOY */
        AnnouncementDto anData = new AnnouncementDto(null, null, null, null, SANT, LUISIN, null, LocalDate.now(), CONT_PHONE_3, new ArrayList<>());
        ResponseEntity<Long> created = announcementService.createAnnouncement(LOGIN, anData);
        Assert.assertEquals(HttpStatus.NOT_FOUND, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());

        UserProfile user1 = new UserProfile(LUISIN, US_1_MAIL, US_1, PHONE_NUMBER_1);
        userProfileRepository.save(user1);
        /* NO EXISTE LA CIUDAD */
        ResponseEntity<Long> created2 = announcementService.createAnnouncement(user1.getLogin(), anData);
        Assert.assertEquals(HttpStatus.NOT_FOUND, created2.getStatusCode());
        Assert.assertEquals(null, created2.getBody());

        /* BORRAMOS TODOS LOS USUARIOS POR SI ACASO */
        userProfileRepository.deleteAll();

        City city = new City(SANT, ESP, LATITUDE_SANT, LONGITUDE_SANT);
        cityRepository.save(city);
        /* NO EXISTE EL USUARIO */
        ResponseEntity<Long> created3 = announcementService.createAnnouncement(LUISIN, anData);
        Assert.assertEquals(HttpStatus.NOT_FOUND, created3.getStatusCode());
        Assert.assertEquals(null, created3.getBody());
    }

    @Test
    public void createAnnouncementTest() {
        UserProfile user1 = new UserProfile(US_1, US_1_MAIL, US_1, PHONE_NUMBER_1);
        userProfileRepository.save(user1);
        City city = new City(SEV, ESP, LATITUDE_SEV, LONGITUDE_SEV);
        cityRepository.save(city);
        LocalDate end = LocalDate.now().plusDays(3);
        AnnouncementDto anData = new AnnouncementDto(null, TEST, null, null, SEV, US_1, TEST, end,CONT_PHONE_6, new ArrayList<>());
        ResponseEntity<Long> created = announcementService.createAnnouncement(US_1, anData);
        Assert.assertEquals(HttpStatus.CREATED, created.getStatusCode());

        ResponseEntity<AnnouncementDto> result = announcementService.getAnnouncementInfo(created.getBody());
        Assert.assertEquals(anData.getTitle(), result.getBody().getTitle());
        Assert.assertEquals(ANNOUNCEMENT, result.getBody().getType());
        Assert.assertEquals(anData.getCityName(), result.getBody().getCityName());
        Assert.assertEquals(anData.getLogin(), result.getBody().getLogin());
        Assert.assertEquals(end, result.getBody().getEndDate());
        Assert.assertEquals(anData.getContactPhone(), result.getBody().getContactPhone());
        Assert.assertEquals(anData.getDescription(), result.getBody().getDescription());
    }

    @Test
    public void updateAnnouncementInvalidDateOrPhoneNumberTest() {
        /* FECHA FIN ANTERIOR A HOY */
        AnnouncementDto updatedAn = new AnnouncementDto(null, null, null, null, null, null, null, LocalDate.now().minusDays(1),null, new ArrayList<>());
        ResponseEntity<Long> updated = announcementService.updateAnnouncement(LOGIN, updatedAn, 1L);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, updated.getStatusCode());
        Assert.assertEquals(null, updated.getBody());
        /* NUMERO DE TELEFONO INVALIDO */
        AnnouncementDto updatedAn2 = new AnnouncementDto(null, null, null, null, null, null, null, LocalDate.now(),INVALID_PHONE, new ArrayList<>());
        ResponseEntity<Long> updated2 = announcementService.updateAnnouncement(LOGIN, updatedAn2, 2L);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, updated2.getStatusCode());
        Assert.assertEquals(null, updated2.getBody());
    }

    @Test
    public void updateAnnouncementUnauthorizedUserTest() {
        UserProfile user1 = new UserProfile(LUISIN, US_1_MAIL, US_1, PHONE_NUMBER_1);
        userProfileRepository.save(user1);
        City city = new City(SANT, ESP, LATITUDE_SANT, LONGITUDE_SANT);
        cityRepository.save(city);
        Announcement a1 = new Announcement(TITLE_1, city, user1, DESCRI_2, LocalDate.now().plusDays(3), CONT_PHONE_1, new HashSet<>());
        announcementRepository.save(a1);
        UserProfile user2 = new UserProfile(US_2, US_2_MAIL, US_2, PHONE_NUMBER_2);
        userProfileRepository.save(user2);
        AnnouncementDto updatedAn = new AnnouncementDto(null, null, null, null, SANT, LUISIN, null, LocalDate.now(),CONT_PHONE_5, new ArrayList<>());
        ResponseEntity<Long> updated = announcementService.updateAnnouncement(user2.getLogin(), updatedAn, a1.getId());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, updated.getStatusCode());
        Assert.assertEquals(null, updated.getBody());
    }

    @Test
    public void updateUnexistentAnnouncementTest() {
        AnnouncementDto updatedAn = new AnnouncementDto(null, null, null, null, SEV, LUISIN, null, LocalDate.now(),CONT_PHONE_5, new ArrayList<>());
        ResponseEntity<Long> updated = announcementService.updateAnnouncement(LOGIN, updatedAn, 1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, updated.getStatusCode());
        Assert.assertEquals(null, updated.getBody());
    }

    @Test
    public void updateAnnouncementUnexistentUserAndUnexistentCityTest(){
        UserProfile user1 = new UserProfile(LUISIN, US_1_MAIL, US_1, PHONE_NUMBER_1);
        userProfileRepository.save(user1);
        City city = new City(SANT, ESP, LATITUDE_SANT, LONGITUDE_SANT);
        cityRepository.save(city);
        Announcement a1 = new Announcement(TITLE_1, city, user1, DESCRI_2, LocalDate.now().plusDays(3), CONT_PHONE_1, new HashSet<>());
        announcementRepository.save(a1);
        
        AnnouncementDto updatedAn = new AnnouncementDto(null, null, null, null, SEV, LUISIN, null, LocalDate.now(),CONT_PHONE_5, new ArrayList<>());
        ResponseEntity<Long> updated = announcementService.updateAnnouncement(UNEXISTENT, updatedAn, a1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, updated.getStatusCode());
        Assert.assertEquals(null, updated.getBody());
        
        /* NO EXISTE LA CIUDAD */
        ResponseEntity<Long> updated2 = announcementService.updateAnnouncement(user1.getLogin(), updatedAn, a1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, updated2.getStatusCode());
        Assert.assertEquals(null, updated2.getBody());
    }

    @Test
    public void updateAnnouncementTest() {
        UserProfile user1 = new UserProfile(LUISIN, US_1_MAIL, US_1, PHONE_NUMBER_1);
        userProfileRepository.save(user1);
        City city = new City(SEV, ESP, LATITUDE_SEV, LONGITUDE_SEV);
        city = cityRepository.save(city);
        LocalDate end = LocalDate.now().plusDays(3);
        Announcement a1 = new Announcement(TITLE_4, city, user1, DESCRI_4, LocalDate.now().plusDays(1), CONT_PHONE_4, new HashSet<>());
        announcementRepository.save(a1);
        AnnouncementDto anData = new AnnouncementDto(null, TEST, null, null, SEV, LUISIN, TEST, end,CONT_PHONE_6, new ArrayList<>());
        ResponseEntity<Long> updated = announcementService.updateAnnouncement(user1.getLogin(), anData, a1.getId());
        Assert.assertEquals(HttpStatus.OK, updated.getStatusCode());

        ResponseEntity<AnnouncementDto> result = announcementService.getAnnouncementInfo(a1.getId());
        Assert.assertEquals(anData.getTitle(), result.getBody().getTitle());
        Assert.assertEquals(ANNOUNCEMENT, result.getBody().getType());
        Assert.assertEquals(anData.getCityName(), result.getBody().getCityName());
        Assert.assertEquals(anData.getLogin(), result.getBody().getLogin());
        Assert.assertEquals(end, result.getBody().getEndDate());
        Assert.assertEquals(anData.getContactPhone(), result.getBody().getContactPhone());
        Assert.assertEquals(anData.getDescription(), result.getBody().getDescription());
    }
}   

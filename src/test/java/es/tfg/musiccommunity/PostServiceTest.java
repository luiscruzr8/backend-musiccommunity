package es.tfg.musiccommunity;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.tfg.musiccommunity.model.City;
import es.tfg.musiccommunity.model.Comment;
import es.tfg.musiccommunity.model.Discussion;
import es.tfg.musiccommunity.model.Event;
import es.tfg.musiccommunity.model.Announcement;
import es.tfg.musiccommunity.model.Post;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.AnnouncementRepository;
import es.tfg.musiccommunity.repository.CityRepository;
import es.tfg.musiccommunity.repository.CommentRepository;
import es.tfg.musiccommunity.repository.DiscussionRepository;
import es.tfg.musiccommunity.repository.EventRepository;
import es.tfg.musiccommunity.repository.PostRepository;
import es.tfg.musiccommunity.repository.TagRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.PostService;
import es.tfg.musiccommunity.service.dto.CommentDto;
import es.tfg.musiccommunity.service.dto.PostDto;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostServiceTest {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostService postService;


    private static final String SANTIAGO = "Santiago de compostela";
    private static final double LATITUDE_SANTIAGO = 42.880241;
    private static final double LONGITUDE_SANTIAGO = -8.5473632;
    private static final String CORUNA = "A Coruña";
    private static final double LATITUDE_CORUNA = 43.3712591;
    private static final double LONGITUDE_CORUNA = -8.418801;
    private static final double LATITUDE_ORDES = 43.0879655;
    private static final double LONGITUDE_ORDES = -8.3973677;
    private static final String ESPANA ="España";
    private static final String BILBAO = "Bilbao";
    private static final double LATITUDE_BILBAO = 43.2603479;
    private static final double LONGITUDE_BILBAO = -2.9334110;
    private static final String SANTANDER = "Santander";
    private static final double LATITUDE_SANTANDER = 43.4722475;
    private static final double LONGITUDE_SANTANDER = -3.8199358;
    private static final String TITULO_1 = "titulo1";
    private static final String TITULO_2 = "titulo2";
    private static final String TITULO_3 = "titulo3";
    private static final String TITULO_4 = "titulo4";
    private static final String USUARIO_1 = "usuario1";
    private static final String USUARIO_2 = "usuario2";
    private static final String USUARIO_1_MAIL = "usuario1@mail.com";
    private static final String USUARIO_2_MAIL = "usuario2@mail.com";
    private static final String PHONE_1 = "123456789";
    private static final String PHONE_2 = "763547676";
    private static final String DESCRIPCION_1 = "descripcion1";
    private static final String DESCRIPCION_2 = "descripcion2";
    private static final String DESCRIPCION_3 = "descripcion3";
    private static final String DESCRIPCION_4 = "descripcion4";
    private static final String CONTACT_PHONE_1 = "987654321";
    private static final String CONTACT_PHONE_2 = "912876543";
    private static final String EVENT = "Event";
    private static final String ANNOUNCEMENT = "Announcement";
    private static final String UNEXISTENT = "unexistent";
    private static final String OLA = "ola";
    private static final String ADIOS = "adios";
    private static final String COMMENT_TEXT_1 = "commentText1";
    private static final String COMMENT_TEXT_2 = "commentText2";

    /* TEST POST SERVICE */
    @Test()
    public void getUnexistentUserPostsTest() {
        ResponseEntity<List<PostDto>> userPosts = postService.getUserPosts(UNEXISTENT,"","");
        Assert.assertEquals(HttpStatus.NOT_FOUND, userPosts.getStatusCode());
        Assert.assertEquals(null,userPosts.getBody());
    }

    @Test
    public void getUser1AndUser2PostsTest() {
        /* PARA ESTE TEST ES INDIFERENTE ESTAR LOGEADO, YA QUE LA AUTORIZACIÓN SE REALIZA EN EN CONTROLLER */
        /* Añadimos a un usuario para que pueda añadir posts. */
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(SANTANDER, ESPANA, LATITUDE_SANTANDER, LONGITUDE_SANTANDER);
        cityRepository.save(city);

        /* Añadimos 1 evento, 1 discusion y 1 anuncio */
        Event e1 = new Event(TITULO_1, city, user1, DESCRIPCION_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        HashSet<Tag> tags = new HashSet<>();
        Tag t1 = new Tag(OLA);
        tagRepository.save(t1);
        Tag t2 = new Tag(ADIOS);
        tagRepository.save(t2);
        tags.add(t1);
        tags.add(t2);

        Discussion d1 = new Discussion(TITULO_2, user1, DESCRIPCION_2, tags);
        discussionRepository.save(d1);

        Announcement a1 = new Announcement(TITULO_3, city, user1, DESCRIPCION_3, LocalDate.now().plusDays(1), CONTACT_PHONE_1, new HashSet<>());
        announcementRepository.save(a1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2);
        userProfileRepository.save(user2);

        /* Añadimos 1 anuncio */
        Announcement a2 = new Announcement(TITULO_4, city, user2, DESCRIPCION_4, LocalDate.now().plusDays(1), CONTACT_PHONE_2, new HashSet<>());
        announcementRepository.save(a2);

        /* COMPROBAMOS QUE EN TOTAL HAY 4 */
        List<Post> posts = postRepository.findThemAllOrderByCreationDateTimeAsc();
        Assert.assertEquals(4, posts.size());

        /* COMPROBAMOS QUE DEL USUARIO QUE QUEREMOS (USER1), HAY 3 */
        List<Post> postsUser1 = postRepository.findByUser(user1);
        Assert.assertEquals(3, postsUser1.size());

        /* COMPROBAMOS QUE EL SERVICIO DEVUELVE LO MISMO QUE EL REPOSITORIO */
        ResponseEntity<List<PostDto>> postsDtoUser1 = postService.getUserPosts(user1.getLogin(),"","");
        Assert.assertEquals(HttpStatus.OK, postsDtoUser1.getStatusCode());
        Assert.assertEquals(3, postsDtoUser1.getBody().size());

        /* ESCOGEMOS EL PRIMER DATO PARA COMPARAR */
        Assert.assertEquals(postsUser1.get(0).getTitle(), postsDtoUser1.getBody().get(0).getTitle());
        Assert.assertEquals(postsUser1.get(0).getCreationDateTime(), postsDtoUser1.getBody().get(0).getCreationDateTime());
        Assert.assertEquals(postsUser1.get(0).getUser().getLogin(), postsDtoUser1.getBody().get(0).getLogin());
        Assert.assertEquals(postsUser1.get(0).getType(), postsDtoUser1.getBody().get(0).getType());
        Assert.assertEquals(postsUser1.get(0).getDescription(), postsDtoUser1.getBody().get(0).getDescription());

        /* COMPROBAMOS QUE DEL USUARIO QUE QUEREMOS(USER2), HAY 1 */
        List<Post> postsUser2 = postRepository.findByUser(user2);
        Assert.assertEquals(1, postsUser2.size());

        /* COMPROBAMOS QUE EL SERVICIO DEVUELVE LO MISMO QUE EL REPOSITORIO */
        ResponseEntity<List<PostDto>> postsDtoUser2 = postService.getUserPosts(user2.getLogin(),"","");
        Assert.assertEquals(HttpStatus.OK, postsDtoUser2.getStatusCode());
        Assert.assertEquals(1, postsDtoUser2.getBody().size());

        /* ESCOGEMOS EL PRIMER DATO PARA COMPARAR */
        Assert.assertEquals(postsUser2.get(0).getTitle(), postsDtoUser2.getBody().get(0).getTitle());
        Assert.assertEquals(postsUser2.get(0).getCreationDateTime(), postsDtoUser2.getBody().get(0).getCreationDateTime());
        Assert.assertEquals(postsUser2.get(0).getUser().getLogin(), postsDtoUser2.getBody().get(0).getLogin());
        Assert.assertEquals(postsUser2.get(0).getType(), postsDtoUser2.getBody().get(0).getType());
        Assert.assertEquals(postsUser2.get(0).getDescription(), postsDtoUser2.getBody().get(0).getDescription());
    }

    @Test
    public void getUnexistentTypePostsTest() {
        /* Añadimos a un usuario para que pueda añadir posts. */
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(BILBAO, ESPANA, LATITUDE_BILBAO, LONGITUDE_BILBAO);
        cityRepository.save(city);

        /* Añadimos 2 eventos */
        Event e1 = new Event(TITULO_1, city, user1, DESCRIPCION_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        Event e2 = new Event(TITULO_2, city, user1, DESCRIPCION_2, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusHours(2), new HashSet<>());
        eventRepository.save(e2);

        /* Comprobamos que no existen anuncios para este usuario */
        ResponseEntity<List<PostDto>> postsDto = postService.getUserPosts(user1.getLogin(), ANNOUNCEMENT, "");
        Assert.assertEquals(0, postsDto.getBody().size());

        /* Comprobamos que hay dos eventos para este usuario */
        ResponseEntity<List<PostDto>> postsDto2 = postService.getUserPosts(user1.getLogin(), EVENT, "");
        Assert.assertEquals(2, postsDto2.getBody().size());
    }

    @Test
    public void getUnexistentUserPostsByTypeTest() {
        /* Comprobamos que no existen anuncios para este usuario */
        ResponseEntity<List<PostDto>> postsDto = postService.getUserPosts(UNEXISTENT, EVENT, "");
        Assert.assertEquals(null, postsDto.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, postsDto.getStatusCode());
    }

    @Test
    public void getUserPostsByTypeTest() {
        /* Añadimos a un usuario para que pueda añadir posts. */
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(SANTANDER, ESPANA, LATITUDE_SANTANDER, LONGITUDE_SANTANDER);
        cityRepository.save(city);

        /* Añadimos 1 eventos y 1 anuncio */
        Event e1 = new Event(TITULO_1, city, user1, DESCRIPCION_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);

        Announcement a1 = new Announcement(TITULO_3, city, user1, DESCRIPCION_3, LocalDate.now().plusDays(1), CONTACT_PHONE_1, new HashSet<>());
        announcementRepository.save(a1);

        /* Comprobamos que no existen anuncios para este usuario */
        ResponseEntity<List<PostDto>> postsDto = postService.getUserPosts(user1.getLogin(), EVENT, "");
        Assert.assertEquals(1, postsDto.getBody().size());
        /* COMPROBAMOS QUE ESE OBJETO ES EL QUE INTRODUCIMOS */ 
        Assert.assertEquals(e1.getId(), postsDto.getBody().get(0).getId());
        Assert.assertEquals(e1.getUser().getLogin(), postsDto.getBody().get(0).getLogin());
        Assert.assertEquals(e1.getDescription(), postsDto.getBody().get(0).getDescription());
        Assert.assertEquals(e1.getType(), postsDto.getBody().get(0).getType());
        Assert.assertEquals(e1.getTitle(), postsDto.getBody().get(0).getTitle());
        Assert.assertEquals(e1.getCreationDateTime(), postsDto.getBody().get(0).getCreationDateTime());

        ResponseEntity<List<PostDto>> postsDto2 = postService.getUserPosts(user1.getLogin(), ANNOUNCEMENT, "");
        Assert.assertEquals(1, postsDto2.getBody().size());
        /* COMPROBAMOS QUE ESE OBJETO ES EL QUE INTRODUCIMOS */ 
        Assert.assertEquals(a1.getId(), postsDto2.getBody().get(0).getId());
        Assert.assertEquals(a1.getUser().getLogin(), postsDto2.getBody().get(0).getLogin());
        Assert.assertEquals(a1.getDescription(), postsDto2.getBody().get(0).getDescription());
        Assert.assertEquals(a1.getType(), postsDto2.getBody().get(0).getType());
        Assert.assertEquals(a1.getTitle(), postsDto2.getBody().get(0).getTitle());
        Assert.assertEquals(a1.getCreationDateTime(), postsDto2.getBody().get(0).getCreationDateTime());
    }

    @Test
    public void getPostsByUnexistentKeywordTest() {
        /* Añadimos a un usuario para que pueda añadir posts. */
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        /* Añadimos 2 discusiones */
        Discussion d1 = new Discussion(TITULO_2, user1, OLA, new HashSet<>());
        discussionRepository.save(d1);

        Discussion d2 = new Discussion(TITULO_1, user1, ADIOS, new HashSet<>());
        discussionRepository.save(d2);

        /* COMPROBAMOS QUE NO ENCONTRAMOS TITULOS QUE CONTENGAN "ANUN" */
        ResponseEntity<List<PostDto>> postsDto = postService.getUserPosts(user1.getLogin(),"","anun");
        Assert.assertEquals(0, postsDto.getBody().size());
    }

    @Test
    public void getPostsByTypeAndKeywordTest() {
        /* Añadimos a un usuario para que pueda añadir posts. */
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        /* Añadimos una ciudad para que pueda añadir posts */
        City city = new City(BILBAO, ESPANA, LATITUDE_BILBAO, LONGITUDE_BILBAO);
        cityRepository.save(city);

        /* Añadimos 1 discusion y 1 anuncio */
        Discussion d1 = new Discussion("tit1", user1, DESCRIPCION_1, new HashSet<>());
        discussionRepository.save(d1);

        Announcement a1 = new Announcement("titu3", city, user1, DESCRIPCION_3, LocalDate.now().plusDays(1), CONTACT_PHONE_1, new HashSet<>());
        announcementRepository.save(a1);

        /* COMPROBAMOS QUE ENCONTRAMOS LOS TITULOS QUE CONTENGAN "TITU" */
        ResponseEntity<List<PostDto>> postsDto = postService.getUserPosts(user1.getLogin(),"","titu");
        Assert.assertEquals(1, postsDto.getBody().size());
        /* COMPARAMOS CON EL VALOR ENCONTRADO */
        Assert.assertEquals(a1.getTitle(), postsDto.getBody().get(0).getTitle());
        Assert.assertEquals(a1.getDescription(), postsDto.getBody().get(0).getDescription());
        Assert.assertEquals(a1.getType(), postsDto.getBody().get(0).getType());
        Assert.assertEquals(a1.getCreationDateTime(), postsDto.getBody().get(0).getCreationDateTime());
        Assert.assertEquals(a1.getUser().getLogin(), postsDto.getBody().get(0).getLogin());

        /* COMPROBAMOS QUE ENCONTRAMOS LOS TITULOS QUE CONTENGAN "TIT" */
        ResponseEntity<List<PostDto>> postsDto2 = postService.getUserPosts(user1.getLogin(),"","tit");
        Assert.assertEquals(2, postsDto2.getBody().size());
        Assert.assertEquals(d1.getTitle(), postsDto2.getBody().get(0).getTitle());
        Assert.assertEquals(d1.getDescription(), postsDto2.getBody().get(0).getDescription());
        Assert.assertEquals(d1.getType(), postsDto2.getBody().get(0).getType());
        Assert.assertEquals(d1.getCreationDateTime(), postsDto2.getBody().get(0).getCreationDateTime());
        Assert.assertEquals(d1.getUser().getLogin(), postsDto2.getBody().get(0).getLogin());

        /* COMPROBAMOS QUE ENCONTRAMOS LOS TITULOS QUE CONTENGAN "TIT" Y SEAN DE TIPO ANUNCIO*/
        ResponseEntity<List<PostDto>> postsDto3 = postService.getUserPosts(user1.getLogin(),ANNOUNCEMENT,"tit");
        Assert.assertEquals(1, postsDto3.getBody().size());
        Assert.assertEquals(a1.getTitle(), postsDto3.getBody().get(0).getTitle());
        Assert.assertEquals(a1.getDescription(), postsDto3.getBody().get(0).getDescription());
        Assert.assertEquals(a1.getType(), postsDto3.getBody().get(0).getType());
        Assert.assertEquals(a1.getCreationDateTime(), postsDto3.getBody().get(0).getCreationDateTime());
        Assert.assertEquals(a1.getUser().getLogin(), postsDto3.getBody().get(0).getLogin());
    }

    @Test
    public void getZeroClosestOrCloserPostsTest() {
        /* Añadimos a un usuario para que pueda añadir posts. */
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        /* Añadimos 1 discusion y 1 anuncio */
        Discussion d1 = new Discussion("tit1", user1, DESCRIPCION_1, new HashSet<>());
        discussionRepository.save(d1);

        ResponseEntity<List<PostDto>> posts = postService.getPostsNearby(LATITUDE_BILBAO, LONGITUDE_BILBAO, true);
        Assert.assertEquals(0, posts.getBody().size());
        Assert.assertEquals(HttpStatus.OK, posts.getStatusCode());

        ResponseEntity<List<PostDto>> posts2 = postService.getPostsNearby(LATITUDE_BILBAO, LONGITUDE_BILBAO, false);
        Assert.assertEquals(0, posts2.getBody().size());
        Assert.assertEquals(HttpStatus.OK, posts2.getStatusCode());
    }

    @Test
    public void getClosestOrCloserPostsTest() {
        City sdc = new City(SANTIAGO, ESPANA, LATITUDE_SANTIAGO, LONGITUDE_SANTIAGO);
        cityRepository.save(sdc);
        City lcr = new City(CORUNA, ESPANA, LATITUDE_CORUNA, LONGITUDE_CORUNA);
        cityRepository.save(lcr);
        City bb = new City(BILBAO, ESPANA, LATITUDE_BILBAO, LONGITUDE_BILBAO);
        cityRepository.save(bb);

        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        HashSet<Tag> tags = new HashSet<>();
        Tag t1 = new Tag(OLA);
        tagRepository.save(t1);
        Tag t2 = new Tag(ADIOS);
        tagRepository.save(t2);
        tags.add(t1);
        tags.add(t2);

        Event e1 = new Event(TITULO_1, sdc, user1, DESCRIPCION_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), tags);
        eventRepository.save(e1);
        Announcement a1 = new Announcement(TITULO_3, lcr, user1, DESCRIPCION_3, LocalDate.now().plusDays(1), CONTACT_PHONE_1, new HashSet<>());
        announcementRepository.save(a1);
        Discussion d1 = new Discussion(TITULO_2, user1, DESCRIPCION_2, new HashSet<>());
        discussionRepository.save(d1);
        Announcement a2 = new Announcement(TITULO_3, bb, user1, DESCRIPCION_3, LocalDate.now().plusDays(1), CONTACT_PHONE_1, new HashSet<>());
        announcementRepository.save(a2);

        ResponseEntity<List<PostDto>> posts = postService.getPostsNearby(LATITUDE_ORDES, LONGITUDE_ORDES, true);
        Assert.assertEquals(1, posts.getBody().size());
        Assert.assertEquals(HttpStatus.OK, posts.getStatusCode());
        Assert.assertEquals(EVENT, posts.getBody().get(0).getType());
        Assert.assertEquals(e1.getId(), posts.getBody().get(0).getId());
        Assert.assertEquals(e1.getTitle(), posts.getBody().get(0).getTitle());

        ResponseEntity<List<PostDto>> posts2 = postService.getPostsNearby(LATITUDE_ORDES, LONGITUDE_ORDES, false);
        Assert.assertEquals(2, posts2.getBody().size());
        Assert.assertEquals(HttpStatus.OK, posts2.getStatusCode());
        Assert.assertEquals(EVENT, posts2.getBody().get(0).getType());
        Assert.assertEquals(e1.getId(), posts2.getBody().get(0).getId());
        Assert.assertEquals(e1.getTitle(), posts2.getBody().get(0).getTitle());
        Assert.assertEquals(ANNOUNCEMENT, posts2.getBody().get(1).getType());
        Assert.assertEquals(a1.getId(), posts2.getBody().get(1).getId());
        Assert.assertEquals(a1.getTitle(), posts2.getBody().get(1).getTitle());
    }

    @Test
    public void getUnexistentCityPostsTest() {
        ResponseEntity<List<PostDto>> posts = postService.getCityPosts(UNEXISTENT);
        Assert.assertEquals(null, posts.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, posts.getStatusCode());
    }

    @Test
    public void getZeroCityPostsTest() {
        City sdc = new City(SANTIAGO, ESPANA, LATITUDE_SANTIAGO, LONGITUDE_SANTIAGO);
        cityRepository.save(sdc);
        City lcr = new City(CORUNA, ESPANA, LATITUDE_CORUNA, LONGITUDE_CORUNA);
        cityRepository.save(lcr);
        City bb = new City(BILBAO, ESPANA, LATITUDE_BILBAO, LONGITUDE_BILBAO);
        cityRepository.save(bb);

        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        Announcement a2 = new Announcement(TITULO_3, bb, user1, DESCRIPCION_3, LocalDate.now().plusDays(1), CONTACT_PHONE_1, new HashSet<>());
        announcementRepository.save(a2);

        ResponseEntity<List<PostDto>> posts = postService.getCityPosts(lcr.getName());
        Assert.assertEquals(0, posts.getBody().size());
        Assert.assertEquals(HttpStatus.OK, posts.getStatusCode());

        ResponseEntity<List<PostDto>> posts2 = postService.getCityPosts(sdc.getName());
        Assert.assertEquals(0, posts2.getBody().size());
        Assert.assertEquals(HttpStatus.OK, posts2.getStatusCode());
    }

    @Test
    public void getCityPostsTest() {
        City sdc = new City(SANTIAGO, ESPANA, LATITUDE_SANTIAGO, LONGITUDE_SANTIAGO);
        cityRepository.save(sdc);
        City bb = new City(BILBAO, ESPANA, LATITUDE_BILBAO, LONGITUDE_BILBAO);
        cityRepository.save(bb);

        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        HashSet<Tag> tags = new HashSet<>();
        Tag t1 = new Tag(OLA);
        tagRepository.save(t1);
        Tag t2 = new Tag(ADIOS);
        tagRepository.save(t2);
        tags.add(t1);
        tags.add(t2);

        Event e1 = new Event(TITULO_1, sdc, user1, DESCRIPCION_1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1), new HashSet<>());
        eventRepository.save(e1);
        Announcement a1 = new Announcement(TITULO_3, sdc, user1, DESCRIPCION_3, LocalDate.now().plusDays(1), CONTACT_PHONE_1, tags);
        announcementRepository.save(a1);
        Discussion d1 = new Discussion(TITULO_2, user1, DESCRIPCION_2, new HashSet<>());
        discussionRepository.save(d1);
        Announcement a2 = new Announcement(TITULO_3, bb, user1, DESCRIPCION_3, LocalDate.now().plusDays(1), CONTACT_PHONE_1, new HashSet<>());
        announcementRepository.save(a2);

        ResponseEntity<List<PostDto>> posts = postService.getCityPosts(bb.getName());
        Assert.assertEquals(1, posts.getBody().size());
        Assert.assertEquals(HttpStatus.OK, posts.getStatusCode());
        Assert.assertEquals(a2.getId(), posts.getBody().get(0).getId());
        Assert.assertEquals(a2.getTitle(), posts.getBody().get(0).getTitle());
        Assert.assertEquals(a2.getType(), posts.getBody().get(0).getType());


        ResponseEntity<List<PostDto>> posts2 = postService.getCityPosts(sdc.getName());
        Assert.assertEquals(2, posts2.getBody().size());
        Assert.assertEquals(HttpStatus.OK, posts2.getStatusCode());
        Assert.assertEquals(e1.getId(), posts2.getBody().get(0).getId());
        Assert.assertEquals(e1.getTitle(), posts2.getBody().get(0).getTitle());
        Assert.assertEquals(EVENT, posts2.getBody().get(0).getType());
        Assert.assertEquals(a1.getId(), posts2.getBody().get(1).getId());
        Assert.assertEquals(a1.getTitle(), posts2.getBody().get(1).getTitle());
        Assert.assertEquals(ANNOUNCEMENT, posts2.getBody().get(1).getType());
        Assert.assertEquals(ADIOS, posts2.getBody().get(1).getTags().get(0).getTagName());
        Assert.assertEquals(OLA, posts2.getBody().get(1).getTags().get(1).getTagName());
    }

    /************************** COMMENTS ***************************/
    
    @Test
    public void getZeroCommentsPostTest() {
        /* Añadimos a un usuario para que pueda añadir posts. */
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        Discussion d1 = new Discussion(TITULO_2, user1, OLA, new HashSet<>());
        discussionRepository.save(d1);

        ResponseEntity<List<CommentDto>> postComments = postService.getPostComments(d1.getId());
        Assert.assertEquals(HttpStatus.OK, postComments.getStatusCode());
        Assert.assertEquals(0, postComments.getBody().size());
    }

    @Test
    public void getCommentsUnexistentPostTest() {
        ResponseEntity<List<CommentDto>> postComments = postService.getPostComments(-1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, postComments.getStatusCode());
        Assert.assertEquals(null, postComments.getBody());
    }

    @Test
    public void getCommentsPostTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2);
        userProfileRepository.save(user2);

        Discussion d2 = new Discussion(TITULO_1, user1, ADIOS, new HashSet<>());
        discussionRepository.save(d2);

        Comment comment1 = new Comment(user2, d2, COMMENT_TEXT_1);
        commentRepository.save(comment1);
        Comment comment2 = new Comment(user1, d2, COMMENT_TEXT_2);
        commentRepository.save(comment2);

        ResponseEntity<List<CommentDto>> postComments = postService.getPostComments(d2.getId());
        Assert.assertEquals(HttpStatus.OK, postComments.getStatusCode());
        Assert.assertEquals(2, postComments.getBody().size());
    }

    @Test
    public void makeCommentUnexistentUserTest() {
        ResponseEntity<Long> postComments = postService.makeComment(UNEXISTENT,-1L, 
            new CommentDto(null, "commentText", LocalDateTime.now(), "null", null));
        Assert.assertEquals(HttpStatus.NOT_FOUND, postComments.getStatusCode());
        Assert.assertEquals(null, postComments.getBody());
    }

    @Test
    public void makeCommentUnexistentPostTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        ResponseEntity<Long> postComments = postService.makeComment(USUARIO_1,-1L, 
            new CommentDto(null, "commentText", LocalDateTime.now(), "null", null));
        Assert.assertEquals(HttpStatus.NOT_FOUND, postComments.getStatusCode());
        Assert.assertEquals(null, postComments.getBody());
    }

    @Test
    public void makeCommentTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        Discussion d1 = new Discussion(TITULO_2, user1, OLA, new HashSet<>());
        discussionRepository.save(d1);

        CommentDto comment1 = new CommentDto(null, DESCRIPCION_1, LocalDateTime.now(), "null", null);
        
        ResponseEntity<List<CommentDto>> noComments = postService.getPostComments(d1.getId());
        Assert.assertEquals(HttpStatus.OK, noComments.getStatusCode());
        Assert.assertEquals(0, noComments.getBody().size());

        ResponseEntity<Long> makeCommentResult = postService.makeComment(USUARIO_1, d1.getId(), comment1);
        Assert.assertEquals(HttpStatus.OK, makeCommentResult.getStatusCode());

        ResponseEntity<List<CommentDto>> oneComments = postService.getPostComments(d1.getId());
        Assert.assertEquals(makeCommentResult.getBody(), oneComments.getBody().get(0).getId());
    }

    @Test
    public void makeResponseUnexistentUserTest() {
        ResponseEntity<Long> makeResponseResult = postService.makeResponseComment(UNEXISTENT, 1L, 1L, null);
        Assert.assertEquals(HttpStatus.NOT_FOUND, makeResponseResult.getStatusCode());
    }

    @Test
    public void makeResponseUnexistentPostTest() {
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2);
        userProfileRepository.save(user2);

        ResponseEntity<Long> makeResponseResult = postService.makeResponseComment(user2.getLogin(), -1L, 1L, null);
        Assert.assertEquals(HttpStatus.NOT_FOUND, makeResponseResult.getStatusCode());
    }

    @Test
    public void makeResponseUnexistentCommentTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        Discussion d2 = new Discussion(TITULO_1, user1, ADIOS, new HashSet<>());
        discussionRepository.save(d2);

        ResponseEntity<Long> makeResponseResult = postService.makeResponseComment(user1.getLogin(),
            d2.getId(), -1L, null);
        Assert.assertEquals(HttpStatus.NOT_FOUND, makeResponseResult.getStatusCode());
    }

    @Test
    public void makeResponseTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2);
        userProfileRepository.save(user2);

        Discussion d2 = new Discussion(TITULO_1, user1, ADIOS, new HashSet<>());
        discussionRepository.save(d2);

        Comment comment1 = new Comment(user2, d2, COMMENT_TEXT_1);
        commentRepository.save(comment1);

        CommentDto response = new CommentDto(null, DESCRIPCION_1, LocalDateTime.now(), "null", null);
        
        ResponseEntity<Long> makeResponseResult = postService.makeResponseComment(user1.getLogin(), 
            d2.getId(), comment1.getId(), response);
        Assert.assertEquals(HttpStatus.OK, makeResponseResult.getStatusCode());

        ResponseEntity<List<CommentDto>> postComments = postService.getPostComments(d2.getId());
        Assert.assertEquals(HttpStatus.OK, postComments.getStatusCode());
        Assert.assertEquals(makeResponseResult.getBody(),
            postComments.getBody().get(0).getResponses().get(0).getId());
    }

    @Test
    public void deleteUnexistentCommentTest() {
        ResponseEntity<Void> unexistentDelete = postService.deleteComment(USUARIO_1, -1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentDelete.getStatusCode());
    }

    @Test
    public void deleteUnexistentUserTest() {
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2);
        userProfileRepository.save(user2);

        Discussion d2 = new Discussion(TITULO_1, user2, ADIOS, new HashSet<>());
        discussionRepository.save(d2);

        Comment comment1 = new Comment(user2, d2, COMMENT_TEXT_1);
        commentRepository.save(comment1);

        ResponseEntity<Void> unexistentDelete = postService.deleteComment(USUARIO_1, comment1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentDelete.getStatusCode());
    }

    @Test
    public void deleteUnauthorizedUserTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1);
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2);
        userProfileRepository.save(user2);

        Discussion d1 = new Discussion(TITULO_2, user1, OLA, new HashSet<>());
        discussionRepository.save(d1);

        Comment comment1 = new Comment(user1, d1, COMMENT_TEXT_1);
        commentRepository.save(comment1);

        ResponseEntity<Void> unauthorizeDelete = postService.deleteComment(user2.getLogin(), comment1.getId());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, unauthorizeDelete.getStatusCode());
    }

    @Test
    public void deleteCommentTest() {
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2);
        userProfileRepository.save(user2);

        Discussion d2 = new Discussion(TITULO_1, user2, ADIOS, new HashSet<>());
        discussionRepository.save(d2);

        Comment comment1 = new Comment(user2, d2, COMMENT_TEXT_1);
        commentRepository.save(comment1);

        Comment response1 = new Comment(user2, d2, COMMENT_TEXT_1, comment1);
        commentRepository.save(response1);

        ResponseEntity<Void> deleted = postService.deleteComment(user2.getLogin(), comment1.getId());
        Assert.assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());

        ResponseEntity<List<CommentDto>> postComments = postService.getPostComments(d2.getId());
        Assert.assertEquals(HttpStatus.OK, postComments.getStatusCode());
        Assert.assertEquals(0, postComments.getBody().size());
    }
}


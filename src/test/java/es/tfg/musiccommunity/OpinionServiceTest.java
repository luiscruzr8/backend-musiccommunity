package es.tfg.musiccommunity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import es.tfg.musiccommunity.model.Discussion;
import es.tfg.musiccommunity.model.Opinion;
import es.tfg.musiccommunity.model.Score;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.repository.DiscussionRepository;
import es.tfg.musiccommunity.repository.OpinionRepository;
import es.tfg.musiccommunity.repository.ScoreRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.OpinionService;
import es.tfg.musiccommunity.service.dto.OpinionDto;
import es.tfg.musiccommunity.service.dto.ScoreDto;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class OpinionServiceTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private OpinionService opinionService;

    private static final String TITULO_1 = "titulo1";
    private static final String TITULO_2 = "titulo2";
    private static final String TITULO_3 = "titulo3";
    private static final String USUARIO_1 = "usuario1";
    private static final String USUARIO_2 = "usuario2";
    private static final String USUARIO_1_MAIL = "usuario1@mail.com";
    private static final String USUARIO_2_MAIL = "usuario2@mail.com";
    private static final String PHONE_1 = "123456789";
    private static final String PHONE_2 = "763547676";
    private static final String DESCRIPCION_1 = "descripcion1";
    private static final String DESCRIPCION_2 = "descripcion2";
    private static final String DESCRIPCION_3 = "descripcion3";
    private static final String UNEXISTENT = "unexistent";
    private static final String OPINION = "Opinion";
    private static final String FILE_PRUEBA = "src/test/resources/test-files/prueba.pdf";
    private static final String FILE_PRUEBA_2 = "src/test/resources/test-files/prueba2.pdf";
    private static final String FILE_TEST = "src/test/resources/test-files/test.pdf";
    private static final String PDF_EXT = "application/pdf";
    private static final String ETIQUETA_1 = "tag";
    private static final String ETIQUETA_2 = "etiqueta";

    @Test 
    public void getZeroOpinionsTest() {
        ResponseEntity<List<OpinionDto>> opinions = opinionService.getAllOpinions("");
        Assert.assertEquals(0, opinions.getBody().size());
        Assert.assertEquals(HttpStatus.OK, opinions.getStatusCode());
    }

    @Test
    public void getAllOpinionsTest() throws IOException {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        user1 = userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        user2 = userProfileRepository.save(user2);

        File prueba = new File(FILE_PRUEBA);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);

        File prueba2 = new File(FILE_PRUEBA_2);
        FileInputStream input2 = new FileInputStream(prueba2);
        MultipartFile multipart2 = new MockMultipartFile(prueba2.getName(), prueba2.getName(), PDF_EXT, input2);

        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore1);

        Score newScore2 = new Score(multipart2.getOriginalFilename(), multipart2.getContentType(), multipart2.getBytes(), user2);
        scoreRepository.save(newScore2);

        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);

        Opinion o1 = new Opinion(TITULO_1, user1, newScore1, DESCRIPCION_1, new HashSet<Tag>());
        opinionRepository.save(o1);

        Opinion o2 = new Opinion(TITULO_2, user2, newScore2, DESCRIPCION_2, tags);
        opinionRepository.save(o2);

        Discussion d1 = new Discussion(TITULO_3, user1, DESCRIPCION_3, new HashSet<Tag>());
        discussionRepository.save(d1);

        ResponseEntity<List<OpinionDto>> opinions = opinionService.getAllOpinions("");
        Assert.assertEquals(2,opinions.getBody().size());

        Assert.assertEquals(o2.getId(),opinions.getBody().get(1).getId());
        Assert.assertEquals(o2.getDescription(),opinions.getBody().get(1).getDescription());
        Assert.assertEquals(OPINION,opinions.getBody().get(1).getType());
        Assert.assertEquals(o2.getTitle(),opinions.getBody().get(1).getTitle());
        Assert.assertEquals(o2.getCreationDateTime(),opinions.getBody().get(1).getCreationDateTime());
        Assert.assertEquals(o2.getUser().getLogin(),opinions.getBody().get(1).getLogin());
        Assert.assertEquals(newScore2.getId(), opinions.getBody().get(1).getScoreDto().getId());
        Assert.assertEquals(ETIQUETA_2, opinions.getBody().get(1).getTags().get(0).getTagName());
        Assert.assertEquals(ETIQUETA_1, opinions.getBody().get(1).getTags().get(1).getTagName());
    }

    @Test
    public void getUnexistentOpinionInforTest() {
        ResponseEntity<OpinionDto> opinions = opinionService.getOpinionInfo(1L);
        Assert.assertEquals(null, opinions.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, opinions.getStatusCode());
    }

    @Test
    public void getOpinionInfoTest() throws IOException {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        user1 = userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        user2 = userProfileRepository.save(user2);

        File prueba = new File(FILE_PRUEBA_2);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);

        File prueba2 = new File(FILE_TEST);
        FileInputStream input2 = new FileInputStream(prueba2);
        MultipartFile multipart2 = new MockMultipartFile(prueba2.getName(), prueba2.getName(), PDF_EXT, input2);

        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore1);

        Score newScore2 = new Score(multipart2.getOriginalFilename(), multipart2.getContentType(), multipart2.getBytes(), user2);
        scoreRepository.save(newScore2);

        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);

        Opinion o1 = new Opinion(TITULO_3, user2, newScore2, DESCRIPCION_3, tags);
        opinionRepository.save(o1);

        Opinion o2 = new Opinion(TITULO_2, user1, newScore1, DESCRIPCION_3, new HashSet<Tag>());
        opinionRepository.save(o2);

        ResponseEntity<OpinionDto> op1 = opinionService.getOpinionInfo(o1.getId());
        Assert.assertEquals(o1.getId(),op1.getBody().getId());
        Assert.assertEquals(o1.getDescription(),op1.getBody().getDescription());
        Assert.assertEquals(OPINION,op1.getBody().getType());
        Assert.assertEquals(o1.getTitle(),op1.getBody().getTitle());
        Assert.assertEquals(o1.getCreationDateTime(),op1.getBody().getCreationDateTime());
        Assert.assertEquals(o1.getUser().getLogin(),op1.getBody().getLogin());
        Assert.assertEquals(newScore2.getId(), op1.getBody().getScoreDto().getId());
        Assert.assertEquals(ETIQUETA_2, op1.getBody().getTags().get(0).getTagName());
        Assert.assertEquals(ETIQUETA_1, op1.getBody().getTags().get(1).getTagName());

        ResponseEntity<OpinionDto> op2 = opinionService.getOpinionInfo(o2.getId());
        Assert.assertEquals(o2.getId(),op2.getBody().getId());
        Assert.assertEquals(o2.getDescription(),op2.getBody().getDescription());
        Assert.assertEquals(OPINION,op2.getBody().getType());
        Assert.assertEquals(o2.getTitle(),op2.getBody().getTitle());
        Assert.assertEquals(o2.getCreationDateTime(),op2.getBody().getCreationDateTime());
        Assert.assertEquals(o2.getUser().getLogin(),op2.getBody().getLogin());
        Assert.assertEquals(newScore1.getId(), op2.getBody().getScoreDto().getId());
    }

    @Test
    public void deleteUnexstenteOpinionAndUnexistentUserTest() throws IOException {
        ResponseEntity<Void> response = opinionService.deleteOpinion(UNEXISTENT,1L);
        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        File prueba2 = new File(FILE_TEST);
        FileInputStream input2 = new FileInputStream(prueba2);
        MultipartFile multipart2 = new MockMultipartFile(prueba2.getName(), prueba2.getName(), PDF_EXT, input2);

        /* Añadimos un usuario para que puedan añadir posts. */
        UserProfile user1 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user1);

        Score newScore2 = new Score(multipart2.getOriginalFilename(), multipart2.getContentType(), multipart2.getBytes(), user1);
        scoreRepository.save(newScore2);

        Opinion o1 = new Opinion(TITULO_3, user1, newScore2, DESCRIPCION_3, new HashSet<Tag>());
        opinionRepository.save(o1);

        ResponseEntity<Void> response2 = opinionService.deleteOpinion(UNEXISTENT,o1.getId());
        Assert.assertEquals(null, response2.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    public void deleteOpinionUnauthorizedUserTest() throws IOException {
        File prueba = new File(FILE_PRUEBA_2);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);

        UserProfile user1 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user2);

        Score newScore2 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore2);

        Opinion o1 = new Opinion(TITULO_3, user1, newScore2, DESCRIPCION_3, new HashSet<Tag>());
        opinionRepository.save(o1);

        ResponseEntity<Void> response = opinionService.deleteOpinion(user2.getLogin(), o1.getId());
        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void deleteOpinionTest() throws IOException {
        File test = new File(FILE_TEST);
        FileInputStream input = new FileInputStream(test);
        MultipartFile multipart = new MockMultipartFile(test.getName(), test.getName(), PDF_EXT, input);

        UserProfile user1 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user1);

        Score newScore2 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        newScore2.setIsPublic(true);
        scoreRepository.save(newScore2);

        Opinion o1 = new Opinion(TITULO_3, user1, newScore2, DESCRIPCION_3, new HashSet<Tag>());
        opinionRepository.save(o1);

        Assert.assertEquals(true, newScore2.getIsPublic());

        ResponseEntity<Void> response = opinionService.deleteOpinion(user1.getLogin(), o1.getId());
        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Assert.assertEquals(false, newScore2.getIsPublic());
    }

    @Test
    public void createOpinionUnexistentScoreAndUserTest() throws IOException {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        ScoreDto unexistentScoreDto = new ScoreDto(-1L, TITULO_1, PDF_EXT, UNEXISTENT, null);
        OpinionDto opData = new OpinionDto(null, null, null, null, user1.getLogin(), null, unexistentScoreDto,new ArrayList<>());
        ResponseEntity<Long> created = opinionService.createOpinion(UNEXISTENT, opData);
        Assert.assertEquals(HttpStatus.NOT_FOUND, created.getStatusCode());
        Assert.assertEquals(null, created.getBody());

        File prueba = new File(FILE_PRUEBA);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);
        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore1);

        ScoreDto scoreDto = new ScoreDto(newScore1.getId(), newScore1.getScoreName(), newScore1.getFileType(), UNEXISTENT, null);
        OpinionDto opData2 = new OpinionDto(null, null, null, null, UNEXISTENT, null, scoreDto, new ArrayList<>());
        ResponseEntity<Long> created2 = opinionService.createOpinion(UNEXISTENT, opData2);
        Assert.assertEquals(HttpStatus.NOT_FOUND, created2.getStatusCode());
        Assert.assertEquals(null, created2.getBody());
    }

    @Test
    public void createOpinionTest() throws IOException {
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        File prueba = new File(FILE_PRUEBA_2);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);
        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user2);
        scoreRepository.save(newScore1);

        Assert.assertEquals(false, newScore1.getIsPublic());

        ScoreDto scoreDto = new ScoreDto(newScore1.getId(), newScore1.getScoreName(), newScore1.getFileType(), UNEXISTENT, null);
        OpinionDto opData = new OpinionDto(null, TITULO_3, null, null, null, DESCRIPCION_3, scoreDto, new ArrayList<>());
        ResponseEntity<Long> created = opinionService.createOpinion(user2.getLogin(), opData);
        Assert.assertEquals(HttpStatus.CREATED, created.getStatusCode());

        ResponseEntity<OpinionDto> result = opinionService.getOpinionInfo(created.getBody());
        Assert.assertEquals(opData.getTitle(), result.getBody().getTitle());
        Assert.assertEquals(OPINION, result.getBody().getType());
        Assert.assertEquals(user2.getLogin(), result.getBody().getLogin());
        Assert.assertEquals(opData.getDescription(), result.getBody().getDescription());

        Assert.assertEquals(true, newScore1.getIsPublic());
    }

    @Test
    public void updateUnexistentOpinionTest() {
        ScoreDto unexistentScoreDto = new ScoreDto(-1L, TITULO_1, PDF_EXT, UNEXISTENT, null);
        OpinionDto opData = new OpinionDto(null, TITULO_3, null, null, null, DESCRIPCION_3, unexistentScoreDto, new ArrayList<>());
        ResponseEntity<Long> updated = opinionService.updateOpinion(USUARIO_1, opData, -1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, updated.getStatusCode());
        Assert.assertEquals(null, updated.getBody());
    }

    @Test
    public void updateOpinionUnexistentOrUnauthorizedUser() throws IOException {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        File test = new File(FILE_TEST);
        FileInputStream input = new FileInputStream(test);
        MultipartFile multipart = new MockMultipartFile(test.getName(), test.getName(), PDF_EXT, input);
        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user2);
        scoreRepository.save(newScore1);

        Opinion o1 = new Opinion(TITULO_2, user2, newScore1, DESCRIPCION_2, new HashSet<Tag>());
        opinionRepository.save(o1);

        ScoreDto scoreDto = new ScoreDto(newScore1.getId(), newScore1.getScoreName(), newScore1.getFileType(), UNEXISTENT, null);
        OpinionDto opData = new OpinionDto(o1.getId(), TITULO_1, null, null, null, DESCRIPCION_1, scoreDto, new ArrayList<>());
        ResponseEntity<Long> updated = opinionService.updateOpinion(UNEXISTENT, opData, o1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, updated.getStatusCode());
        Assert.assertEquals(null, updated.getBody());

        ResponseEntity<Long> updated2 = opinionService.updateOpinion(user1.getLogin(), opData, o1.getId());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, updated2.getStatusCode());
        Assert.assertEquals(null, updated2.getBody());
    }

    @Test
    public void updateOpinionTest() throws IOException {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        File test = new File(FILE_TEST);
        FileInputStream input = new FileInputStream(test);
        MultipartFile multipart = new MockMultipartFile(test.getName(), test.getName(), PDF_EXT, input);
        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore1);

        Opinion o1 = new Opinion(TITULO_2, user1, newScore1, DESCRIPCION_2, new HashSet<Tag>());
        opinionRepository.save(o1);

        ScoreDto scoreDto = new ScoreDto(newScore1.getId(), newScore1.getScoreName(), newScore1.getFileType(), UNEXISTENT, null);
        OpinionDto opData = new OpinionDto(o1.getId(), TITULO_1, null, null, null, DESCRIPCION_1, scoreDto, new ArrayList<>());
        ResponseEntity<Long> updated = opinionService.updateOpinion(user1.getLogin(), opData, o1.getId());
        Assert.assertEquals(HttpStatus.OK, updated.getStatusCode());
        Assert.assertEquals(o1.getId(), updated.getBody());

        ResponseEntity<OpinionDto> result = opinionService.getOpinionInfo(o1.getId());
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assert.assertEquals(TITULO_1, result.getBody().getTitle());
        Assert.assertEquals(DESCRIPCION_1, result.getBody().getDescription());
        Assert.assertEquals(USUARIO_1, result.getBody().getLogin());

    }
}
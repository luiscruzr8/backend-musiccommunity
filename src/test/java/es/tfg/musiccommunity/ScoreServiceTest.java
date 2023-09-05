package es.tfg.musiccommunity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.tfg.musiccommunity.model.Comment;
import es.tfg.musiccommunity.model.Interaction;
import es.tfg.musiccommunity.model.Opinion;
import es.tfg.musiccommunity.model.Recommendation;
import es.tfg.musiccommunity.model.Score;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.CommentRepository;
import es.tfg.musiccommunity.repository.InteractionRepository;
import es.tfg.musiccommunity.repository.OpinionRepository;
import es.tfg.musiccommunity.repository.RecommendationRepository;
import es.tfg.musiccommunity.repository.ScoreRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.ScoreService;
import es.tfg.musiccommunity.service.dto.ScoreDto;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class ScoreServiceTest { 

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private ScoreService scoreService;

    private static final String USUARIO_1 = "usuario1";
    private static final String USUARIO_2 = "usuario2";
    private static final String USUARIO_1_MAIL = "usuario1@mail.com";
    private static final String USUARIO_2_MAIL = "usuario2@mail.com";
    private static final String PHONE_1 = "123456789";
    private static final String PHONE_2 = "763547676";
    private static final String UNEXISTENT = "unexistent";
    private static final String FILE_PRUEBA = "src/test/resources/test-files/prueba.pdf";
    private static final String FILE_PRUEBA_2 = "src/test/resources/test-files/prueba2.pdf";
    private static final String FILE_TEST = "src/test/resources/test-files/test.pdf";
    private static final String FILE_INVALID_EXT = "src/test/resources/test-files/invalid.txt";
    private static final String FILE_INVALID_NAME = "src/test/resources/test-files/invalid..1.pdf";
    private static final String PDF_EXT = "application/pdf";

    @Test
    public void getUnexistentUserScoresTest() {
        ResponseEntity<List<ScoreDto>> scores = scoreService.getUserScores(UNEXISTENT, false);
        Assert.assertEquals(HttpStatus.NOT_FOUND, scores.getStatusCode());
    }

    @Test
    public void getZeroUserScoresTest() {
        UserProfile user = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user);

        ResponseEntity<List<ScoreDto>> scores = scoreService.getUserScores(user.getLogin(), false);
        Assert.assertEquals(0,scores.getBody().size());
        Assert.assertEquals(HttpStatus.OK, scores.getStatusCode());

        ResponseEntity<List<ScoreDto>> publicScores = scoreService.getUserScores(user.getLogin(), true);
        Assert.assertEquals(0,publicScores.getBody().size());
        Assert.assertEquals(HttpStatus.OK, publicScores.getStatusCode());
    }

    @Test
    public void getUserScoresTest() throws IOException {
        File prueba = new File(FILE_PRUEBA);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);

        File prueba2 = new File(FILE_PRUEBA_2);
        FileInputStream input2 = new FileInputStream(prueba2);
        MultipartFile multipart2 = new MockMultipartFile(prueba2.getName(), prueba2.getName(), PDF_EXT, input2);

        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore1);

        Score newScore2 = new Score(multipart2.getOriginalFilename(), multipart2.getContentType(), multipart2.getBytes(), user2);
        scoreRepository.save(newScore2);

        ResponseEntity<List<ScoreDto>> scoresUser1 = scoreService.getUserScores(user1.getLogin(), false);
        Assert.assertEquals(HttpStatus.OK, scoresUser1.getStatusCode());
        Assert.assertEquals(1,scoresUser1.getBody().size());
        Assert.assertEquals(multipart.getOriginalFilename().replace(".pdf", ""),scoresUser1.getBody().get(0).getScoreName());
        Assert.assertEquals(multipart.getOriginalFilename().replace(".pdf", ""),scoresUser1.getBody().get(0).getScoreName());
        Assert.assertEquals(multipart.getContentType(),scoresUser1.getBody().get(0).getFileType());        

        ResponseEntity<List<ScoreDto>> publicScoresUser1 = scoreService.getUserScores(user1.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, publicScoresUser1.getStatusCode());
        Assert.assertEquals(0,publicScoresUser1.getBody().size());
    }

    @Test
    public void getUnexistentUserScoresByKeywordTest() {
        ResponseEntity<List<ScoreDto>> scores = scoreService.getUserScoresByKeyword(UNEXISTENT, "pru", false);
        Assert.assertEquals(HttpStatus.NOT_FOUND, scores.getStatusCode());
    }

    @Test
    public void getZeroUserScoresByKeywordTest() throws IOException {
        UserProfile user = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user);

        File prueba = new File(FILE_TEST);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);

        Score newScore = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user);
        scoreRepository.save(newScore);

        ResponseEntity<List<ScoreDto>> scoresByKeyword = scoreService.getUserScoresByKeyword(user.getLogin(), "prueba", false);
        Assert.assertEquals(0,scoresByKeyword.getBody().size());
        Assert.assertEquals(HttpStatus.OK, scoresByKeyword.getStatusCode());

        ResponseEntity<List<ScoreDto>> publicScoresByKeyword = scoreService.getUserScoresByKeyword(user.getLogin(), "prueba", true);
        Assert.assertEquals(0,publicScoresByKeyword.getBody().size());
        Assert.assertEquals(HttpStatus.OK, publicScoresByKeyword.getStatusCode());
    }

    @Test
    public void getUserScoresByKeywordTest() throws IOException {
        File prueba = new File(FILE_PRUEBA);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);

        File prueba2 = new File(FILE_PRUEBA_2);
        FileInputStream input2 = new FileInputStream(prueba2);
        MultipartFile multipart2 = new MockMultipartFile(prueba2.getName(), prueba2.getName(), PDF_EXT, input2);

        File prueba3 = new File(FILE_TEST);
        FileInputStream input3 = new FileInputStream(prueba3);
        MultipartFile multipart3 = new MockMultipartFile(prueba3.getName(), prueba3.getName(), PDF_EXT, input3);

        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore1);

        Score newScore2 = new Score(multipart2.getOriginalFilename(), multipart2.getContentType(), multipart2.getBytes(), user2);
        scoreRepository.save(newScore2);

        Score newScore3 = new Score(multipart3.getOriginalFilename(), multipart3.getContentType(), multipart3.getBytes(), user1);
        newScore3.setIsPublic(true);
        scoreRepository.save(newScore3);

        ResponseEntity<List<ScoreDto>> scoresUser1 = scoreService.getUserScoresByKeyword(user1.getLogin(), "e", false);
        Assert.assertEquals(HttpStatus.OK, scoresUser1.getStatusCode());
        Assert.assertEquals(2,scoresUser1.getBody().size());

        ResponseEntity<List<ScoreDto>> publicScoresUser1 = scoreService.getUserScoresByKeyword(user1.getLogin(), "test", true);
        Assert.assertEquals(HttpStatus.OK, publicScoresUser1.getStatusCode());
        Assert.assertEquals(1,publicScoresUser1.getBody().size());
        Assert.assertEquals(multipart3.getOriginalFilename().replace(".pdf", ""),publicScoresUser1.getBody().get(0).getScoreName());
        Assert.assertEquals(multipart3.getOriginalFilename().replace(".pdf", ""),publicScoresUser1.getBody().get(0).getScoreName());
        Assert.assertEquals(multipart3.getContentType(),publicScoresUser1.getBody().get(0).getFileType()); 
    }

    @Test
    public void deleteUnexistentScoreTest() {
        ResponseEntity<Void> errorUnexistent = scoreService.deleteScore(-1L, USUARIO_1);
        Assert.assertEquals(HttpStatus.NOT_FOUND, errorUnexistent.getStatusCode());
    }

    @Test
    public void deleteUnexistentOrUnauthorizedUserScoreTest() throws IOException {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        File prueba = new File(FILE_PRUEBA);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);

        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore1);

        ResponseEntity<Void> errorUnexistentUser = scoreService.deleteScore(newScore1.getId(), UNEXISTENT);
        Assert.assertEquals(HttpStatus.NOT_FOUND, errorUnexistentUser.getStatusCode());

        ResponseEntity<Void> errorUnauthorizedUser = scoreService.deleteScore(newScore1.getId(), user2.getLogin());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, errorUnauthorizedUser.getStatusCode());
    }

    @Test
    public void deleteScoreTest() throws IOException {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        File prueba = new File(FILE_PRUEBA);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);

        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore1);

        Opinion newOp = new Opinion("TITULO PRUEBA",user1,newScore1, "DESCRIPCION PRUEBA", new HashSet<>());
        opinionRepository.save(newOp);

        Comment newComment = new Comment(user1, newOp, "commentText");
        commentRepository.save(newComment);

        Comment newResponse = new Comment(user1, newOp, "commentText", newComment);
        commentRepository.save(newResponse);

        Recommendation newRec = new Recommendation("recTitle", "recText", user1, newOp);
        recommendationRepository.save(newRec);

        Interaction newInt = new Interaction(user1, newRec, 6);
        interactionRepository.save(newInt);

        ResponseEntity<List<ScoreDto>> scoresUser1 = scoreService.getUserScores(user1.getLogin(), false);
        Assert.assertEquals(HttpStatus.OK, scoresUser1.getStatusCode());
        Assert.assertEquals(1,scoresUser1.getBody().size());

        ResponseEntity<Void> successfulDelete = scoreService.deleteScore(newScore1.getId(), user1.getLogin());
        Assert.assertEquals(HttpStatus.NO_CONTENT, successfulDelete.getStatusCode());

        ResponseEntity<List<ScoreDto>> scoresUser1After = scoreService.getUserScores(user1.getLogin(), false);
        Assert.assertEquals(HttpStatus.OK, scoresUser1After.getStatusCode());
        Assert.assertEquals(0,scoresUser1After.getBody().size());
    }

    @Test
    public void storeInvalidScoreTest() throws IOException {
        File prueba = new File(FILE_INVALID_EXT);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), "text/plain", input);

        File prueba2 = new File(FILE_INVALID_NAME);
        FileInputStream input2 = new FileInputStream(prueba2);
        MultipartFile multipart2 = new MockMultipartFile(prueba2.getName(), prueba2.getName(), PDF_EXT, input2);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        //Invalida por '..'
        ResponseEntity<Long> errorInvalidFileName = scoreService.storeScore(multipart2, user2.getLogin());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, errorInvalidFileName.getStatusCode());

        //Invalida por extensión no .pdf
        ResponseEntity<Long> errorInvalidExtension = scoreService.storeScore(multipart, user2.getLogin());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, errorInvalidExtension.getStatusCode());
    }

    @Test
    public void storeUnexistentUserScoreTest() throws IOException {
        File prueba3 = new File(FILE_TEST);
        FileInputStream input3 = new FileInputStream(prueba3);
        MultipartFile multipart3 = new MockMultipartFile(prueba3.getName(), prueba3.getName(), PDF_EXT, input3);

        ResponseEntity<Long> errorUnexistentUser = scoreService.storeScore(multipart3, UNEXISTENT);
        Assert.assertEquals(HttpStatus.NOT_FOUND, errorUnexistentUser.getStatusCode());
    }

    @Test
    public void storeScoreTest() throws IOException {
        File prueba2 = new File(FILE_PRUEBA_2);
        FileInputStream input2 = new FileInputStream(prueba2);
        MultipartFile multipart2 = new MockMultipartFile(prueba2.getName(), prueba2.getName(), PDF_EXT, input2);

        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        ResponseEntity<Long> errorUnexistentUser = scoreService.storeScore(multipart2, user1.getLogin());
        Assert.assertEquals(HttpStatus.OK, errorUnexistentUser.getStatusCode());

        ResponseEntity<List<ScoreDto>> scoresUser = scoreService.getUserScores(user1.getLogin(), false);
        Assert.assertEquals(HttpStatus.OK, scoresUser.getStatusCode());
        Assert.assertEquals(1,scoresUser.getBody().size());
        Assert.assertEquals(multipart2.getOriginalFilename().replace(".pdf", ""),scoresUser.getBody().get(0).getScoreName());
        Assert.assertEquals(multipart2.getContentType(),scoresUser.getBody().get(0).getFileType());
    }

    @Test
    public void getUnexistentScoreInfoOrFileTest() {
        //Probamos ScoreInfo y ScoreFile juntas porque hacen los mismo, aunque una devulva solo el dto, y la otra el recurso
        ResponseEntity<ScoreDto> errorUnexistentScoreInfo = scoreService.getScoreInfo(-1L, USUARIO_1);
        Assert.assertEquals(HttpStatus.NOT_FOUND, errorUnexistentScoreInfo.getStatusCode());

        ResponseEntity<Resource> errorUnexistentScoreFile = scoreService.getScoreFile(-1L, USUARIO_1);
        Assert.assertEquals(HttpStatus.NOT_FOUND, errorUnexistentScoreFile.getStatusCode());
    }

    @Test
    public void getUnauthorizedUserScoreInfoOrFileTest() throws IOException {
        File prueba3 = new File(FILE_TEST);
        FileInputStream input3 = new FileInputStream(prueba3);
        MultipartFile multipart3 = new MockMultipartFile(prueba3.getName(), prueba3.getName(), PDF_EXT, input3);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Score newScore3 = new Score(multipart3.getOriginalFilename(), multipart3.getContentType(), multipart3.getBytes(), user2);
        scoreRepository.save(newScore3);

        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        //Probamos ScoreInfo y ScoreFile juntas porque hacen los mismo, aunque una devulva solo el dto, y la otra el recurso
        ResponseEntity<ScoreDto> errorUnauthorizedScoreInfo = scoreService.getScoreInfo(newScore3.getId(), user1.getLogin());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, errorUnauthorizedScoreInfo.getStatusCode());

        ResponseEntity<Resource> errorUnauthorizedScoreFile = scoreService.getScoreFile(newScore3.getId(), user1.getLogin());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, errorUnauthorizedScoreFile.getStatusCode());
    }

    @Test
    public void getScoreInfoTest() throws IOException {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        File prueba = new File(FILE_PRUEBA);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);

        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore1);

        File prueba2 = new File(FILE_PRUEBA_2);
        FileInputStream input2 = new FileInputStream(prueba2);
        MultipartFile multipart2 = new MockMultipartFile(prueba2.getName(), prueba2.getName(), PDF_EXT, input2);

        Score newScore2 = new Score(multipart2.getOriginalFilename(), multipart2.getContentType(), multipart2.getBytes(), user1);
        newScore2.setIsPublic(true);
        scoreRepository.save(newScore2);
        
        ResponseEntity<ScoreDto> nonAuthorPublicResponse = scoreService.getScoreInfo(newScore2.getId(), user2.getLogin());
        Assert.assertEquals(HttpStatus.OK, nonAuthorPublicResponse.getStatusCode());
        Assert.assertEquals(newScore2.getScoreName().replace(".pdf", ""),nonAuthorPublicResponse.getBody().getScoreName());
        Assert.assertEquals(newScore2.getFileType(),nonAuthorPublicResponse.getBody().getFileType());

        ResponseEntity<ScoreDto> nonAuthorResponse = scoreService.getScoreInfo(newScore1.getId(), user2.getLogin());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, nonAuthorResponse.getStatusCode());

        ResponseEntity<ScoreDto> authorResponse = scoreService.getScoreInfo(newScore1.getId(), user1.getLogin());
        Assert.assertEquals(HttpStatus.OK, authorResponse.getStatusCode());
        Assert.assertEquals(newScore1.getScoreName().replace(".pdf", ""),authorResponse.getBody().getScoreName());
        Assert.assertEquals(newScore1.getFileType(),authorResponse.getBody().getFileType());
    }

    @Test
    public void getScoreFileTest() throws IOException {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        File prueba = new File(FILE_PRUEBA);
        FileInputStream input = new FileInputStream(prueba);
        MultipartFile multipart = new MockMultipartFile(prueba.getName(), prueba.getName(), PDF_EXT, input);

        Score newScore1 = new Score(multipart.getOriginalFilename(), multipart.getContentType(), multipart.getBytes(), user1);
        scoreRepository.save(newScore1);

        File prueba2 = new File(FILE_PRUEBA_2);
        FileInputStream input2 = new FileInputStream(prueba2);
        MultipartFile multipart2 = new MockMultipartFile(prueba2.getName(), prueba2.getName(), PDF_EXT, input2);

        Score newScore2 = new Score(multipart2.getOriginalFilename(), multipart2.getContentType(), multipart2.getBytes(), user1);
        newScore2.setIsPublic(true);
        scoreRepository.save(newScore2);
        
        ResponseEntity<Resource> nonAuthorPublicResponse = scoreService.getScoreFile(newScore2.getId(), user2.getLogin());
        Assert.assertEquals(HttpStatus.OK, nonAuthorPublicResponse.getStatusCode());

        ResponseEntity<Resource> nonAuthorResponse = scoreService.getScoreFile(newScore1.getId(), user2.getLogin());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, nonAuthorResponse.getStatusCode());

        ResponseEntity<Resource> authorResponse = scoreService.getScoreFile(newScore1.getId(), user1.getLogin());
        Assert.assertEquals(HttpStatus.OK, authorResponse.getStatusCode());
    }
}
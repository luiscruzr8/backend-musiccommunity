package es.tfg.musiccommunity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

import es.tfg.musiccommunity.model.Discussion;
import es.tfg.musiccommunity.model.Interaction;
import es.tfg.musiccommunity.model.Recommendation;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.DiscussionRepository;
import es.tfg.musiccommunity.repository.InteractionRepository;
import es.tfg.musiccommunity.repository.RecommendationRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.RecommendationService;
import es.tfg.musiccommunity.service.dto.PostDto;
import es.tfg.musiccommunity.service.dto.RecommendationDto;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class RecommendationServiceTest {

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private RecommendationService recommendationService;

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
    private static final String DISCUSSION = "Discussion";
    private static final String UNEXISTENT = "unexistent";
    private static final String ETIQUETA_1 = "etiqueta1";
    private static final String ETIQUETA_2 = "etiqueta2";

    @Test
    public void getZeroRecommendationsTest() {
        ResponseEntity<List<RecommendationDto>> zeroRecommendations = recommendationService.getRecommendations(false,TITULO_3);
        Assert.assertEquals(HttpStatus.OK, zeroRecommendations.getStatusCode());
        Assert.assertEquals(0, zeroRecommendations.getBody().size());

        ResponseEntity<List<RecommendationDto>> zeroTop10Recommendations = recommendationService.getRecommendations(true,TITULO_3);
        Assert.assertEquals(HttpStatus.OK, zeroTop10Recommendations.getStatusCode());
        Assert.assertEquals(0, zeroTop10Recommendations.getBody().size());
    }

    @Test
    public void getUnexistentUserRecommendationsTest() {
        ResponseEntity<List<RecommendationDto>> unexistenteUserRecommendations = recommendationService.getUserRecommendations(UNEXISTENT,false,"");
        Assert.assertEquals(null, unexistenteUserRecommendations.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistenteUserRecommendations.getStatusCode());
        
    }

    @Test
    public void getZeroUserRecommendationsTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);
        ResponseEntity<List<RecommendationDto>> zeroUserRecommendations = recommendationService.getUserRecommendations(USUARIO_1,false,"TITULO_1");
        Assert.assertEquals(HttpStatus.OK, zeroUserRecommendations.getStatusCode());
        Assert.assertEquals(0, zeroUserRecommendations.getBody().size());

        ResponseEntity<List<RecommendationDto>> zeroTop10UserRecommendations = recommendationService.getUserRecommendations(USUARIO_1,true,TITULO_1);
        Assert.assertEquals(HttpStatus.OK, zeroTop10UserRecommendations.getStatusCode());
        Assert.assertEquals(0, zeroTop10UserRecommendations.getBody().size());
    }

    @Test
    public void getRecommendationsTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);

        Discussion d1 = new Discussion(TITULO_1, user1, DESCRIPCION_1, tags);
        discussionRepository.save(d1);

        Recommendation r1 = new Recommendation(TITULO_2, TITULO_2, user2, d1);
        r1.setRating(4.5);
        recommendationRepository.save(r1);
        Recommendation r2 = new Recommendation(TITULO_3, TITULO_3, user1, d1);
        r2.setRating(9.0);
        recommendationRepository.save(r2);

        ResponseEntity<List<RecommendationDto>> recommendations = recommendationService.getRecommendations(false,"");
        Assert.assertEquals(HttpStatus.OK, recommendations.getStatusCode());
        Assert.assertEquals(2, recommendations.getBody().size());
        Assert.assertEquals(r1.getRecText(), recommendations.getBody().get(0).getRecText());
        Assert.assertTrue(r1.getRating() == recommendations.getBody().get(0).getRating());


        ResponseEntity<List<RecommendationDto>> top10Recommendations = recommendationService.getRecommendations(true,"");
        Assert.assertEquals(HttpStatus.OK, top10Recommendations.getStatusCode());
        Assert.assertEquals(2, top10Recommendations.getBody().size());
        Assert.assertEquals(r2.getRecText(), top10Recommendations.getBody().get(0).getRecText());
        Assert.assertTrue(r2.getRating() == top10Recommendations.getBody().get(0).getRating());
    }

    @Test
    public void getUserRecommendationsTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);

        Discussion d1 = new Discussion(TITULO_1, user1, DESCRIPCION_1, tags);
        discussionRepository.save(d1);

        Recommendation r1 = new Recommendation(TITULO_2, TITULO_2, user2, d1);
        r1.setRating(4.5);
        recommendationRepository.save(r1);
        Recommendation r2 = new Recommendation(TITULO_3, TITULO_3, user1, d1);
        r2.setRating(9.0);
        recommendationRepository.save(r2);
        Recommendation r3 = new Recommendation(TITULO_1, TITULO_1, user1, d1);
        r2.setRating(8.75);
        recommendationRepository.save(r3);

        ResponseEntity<List<RecommendationDto>> userRecommendations = recommendationService.getUserRecommendations(USUARIO_1,false,"");
        Assert.assertEquals(HttpStatus.OK, userRecommendations.getStatusCode());
        Assert.assertEquals(2, userRecommendations.getBody().size());
        Assert.assertEquals(r2.getRecTitle(), userRecommendations.getBody().get(0).getRecTitle());
        Assert.assertEquals(r2.getRecText(), userRecommendations.getBody().get(0).getRecText());
        Assert.assertTrue(r2.getRating() == userRecommendations.getBody().get(0).getRating());


        ResponseEntity<List<RecommendationDto>> top10UserRecommendations = recommendationService.getUserRecommendations(USUARIO_1, true,"");
        Assert.assertEquals(HttpStatus.OK, top10UserRecommendations.getStatusCode());
        Assert.assertEquals(2, top10UserRecommendations.getBody().size());
        Assert.assertEquals(r3.getRecTitle(), top10UserRecommendations.getBody().get(1).getRecTitle());
        Assert.assertEquals(r3.getRecText(), top10UserRecommendations.getBody().get(1).getRecText());
        Assert.assertTrue(r3.getRating() == top10UserRecommendations.getBody().get(1).getRating());
    }

    @Test
    public void getUnexistentRecommendationInfoTest() {
        ResponseEntity<RecommendationDto> unexistentRecommendationInfo = recommendationService.getRecommendationInfo(-1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentRecommendationInfo.getStatusCode());
        Assert.assertEquals(null, unexistentRecommendationInfo.getBody());
    }

    @Test
    public void getRecommendationInfoTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);

        Discussion d1 = new Discussion(TITULO_1, user1, DESCRIPCION_1, tags);
        discussionRepository.save(d1);
        Recommendation r1 = new Recommendation(TITULO_2, TITULO_2, user2, d1);
        r1.setRating(4.5);
        recommendationRepository.save(r1);
        Recommendation r2 = new Recommendation(TITULO_3, TITULO_3, user1, d1);
        r2.setRating(9.0);
        recommendationRepository.save(r2);

        ResponseEntity<RecommendationDto> recommendation1 = recommendationService.getRecommendationInfo(r1.getId());
        Assert.assertEquals(HttpStatus.OK, recommendation1.getStatusCode());
        Assert.assertEquals(r1.getRecTitle(), recommendation1.getBody().getRecTitle());
        Assert.assertEquals(r1.getRecText(), recommendation1.getBody().getRecText());
        Assert.assertTrue(r1.getRating() == recommendation1.getBody().getRating());
        Assert.assertEquals(r1.getPost().getId(), recommendation1.getBody().getPostId());

        ResponseEntity<RecommendationDto> recommendation2 = recommendationService.getRecommendationInfo(r2.getId());
        Assert.assertEquals(HttpStatus.OK, recommendation2.getStatusCode());
        Assert.assertEquals(r2.getRecTitle(), recommendation2.getBody().getRecTitle());
        Assert.assertEquals(r2.getRecText(), recommendation2.getBody().getRecText());
        Assert.assertTrue(r2.getRating() == recommendation2.getBody().getRating());
        Assert.assertEquals(r2.getPost().getId(), recommendation2.getBody().getPostId());
    }

    @Test
    public void createRecommendationUnexistentUserAndPostTest() {
        PostDto pDto = new PostDto(-1L, TITULO_2, null, DISCUSSION, UNEXISTENT, DESCRIPCION_2, null);
        RecommendationDto recDto = new RecommendationDto(null, null, TITULO_1, TITULO_1, USUARIO_1, pDto, 5.0);

        ResponseEntity<Long> createdUnexistentUser = recommendationService.createRecommendation(UNEXISTENT, recDto);
        Assert.assertEquals(HttpStatus.NOT_FOUND, createdUnexistentUser.getStatusCode());
        Assert.assertEquals(null, createdUnexistentUser.getBody());

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        ResponseEntity<Long> createdUnexistentPost = recommendationService.createRecommendation(USUARIO_2, recDto);
        Assert.assertEquals(HttpStatus.NOT_FOUND, createdUnexistentPost.getStatusCode());
        Assert.assertEquals(null, createdUnexistentPost.getBody());
    }

    @Test
    public void createRecommendationTest() {
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Discussion d1 = new Discussion(TITULO_1, user2, DESCRIPCION_1, new HashSet<>());
        discussionRepository.save(d1);

        PostDto pDto = new PostDto(d1.getId(), d1.getTitle(), null, DISCUSSION, d1.getUser().getLogin(), d1.getDescription(), null);
        RecommendationDto recDto = new RecommendationDto(null, null, TITULO_3, TITULO_3, null, pDto, 5.0);

        ResponseEntity<Long> created = recommendationService.createRecommendation(USUARIO_2, recDto);
        Assert.assertEquals(HttpStatus.OK, created.getStatusCode());

        ResponseEntity<List<RecommendationDto>> recs = recommendationService.getRecommendations(false,"");
        Assert.assertEquals(HttpStatus.OK, recs.getStatusCode());
        Assert.assertEquals(created.getBody(), recs.getBody().get(0).getId());
    }

    @Test
    public void deleteUnexistentRecommendationTest() {
        ResponseEntity<Void> deleted = recommendationService.deleteRecommendation(USUARIO_1, -1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, deleted.getStatusCode());
        Assert.assertEquals(null, deleted.getBody());
    }

    @Test
    public void deleteUnexistentOrUnauthorizedUserRecommendationTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Discussion d2 = new Discussion(TITULO_2, user1, DESCRIPCION_2, new HashSet<>());
        discussionRepository.save(d2);

        Recommendation r3 = new Recommendation(TITULO_3,TITULO_3, user2, d2);
        recommendationRepository.save(r3);

        ResponseEntity<Void> unexistentUserDeleted = recommendationService.deleteRecommendation(UNEXISTENT, r3.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentUserDeleted.getStatusCode());
        Assert.assertEquals(null, unexistentUserDeleted.getBody());

        ResponseEntity<Void> unauthorizedUserDeleted = recommendationService.deleteRecommendation(user1.getLogin(), r3.getId());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedUserDeleted.getStatusCode());
        Assert.assertEquals(null, unauthorizedUserDeleted.getBody());
    }

    @Test
    public void deleteRecommendationTest() {
        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Discussion d1 = new Discussion(TITULO_1, user2, DESCRIPCION_1, new HashSet<>());
        discussionRepository.save(d1);

        Recommendation r1 = new Recommendation(TITULO_2, TITULO_2, user2, d1);
        recommendationRepository.save(r1);

        ResponseEntity<List<RecommendationDto>> recs = recommendationService.getRecommendations(false,"");
        Assert.assertEquals(HttpStatus.OK, recs.getStatusCode());
        Assert.assertEquals(1, recs.getBody().size());
        Assert.assertEquals(r1.getId(), recs.getBody().get(0).getId());

        ResponseEntity<Void> deleted = recommendationService.deleteRecommendation(USUARIO_2, r1.getId());
        Assert.assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());

        ResponseEntity<List<RecommendationDto>> afterDelete = recommendationService.getRecommendations(false,"");
        Assert.assertEquals(HttpStatus.OK, afterDelete.getStatusCode());
        Assert.assertEquals(0, afterDelete.getBody().size());
    }

    @Test
    public void updateUnexistentRecommendationTest() {
        PostDto pDto = new PostDto(null, null, null, null, null, null, null);
        RecommendationDto recDto = new RecommendationDto(null,null,null,null,null,pDto,0);
        ResponseEntity<Long> updated = recommendationService.updateRecommendation(USUARIO_1, recDto, -1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, updated.getStatusCode());
        Assert.assertEquals(null, updated.getBody());
    }

    @Test
    public void updateUnexistentOrUnauthorizedUserRecommendationTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Discussion d1 = new Discussion(TITULO_1, user1, DESCRIPCION_1, new HashSet<>());
        discussionRepository.save(d1);

        Recommendation r2 = new Recommendation(TITULO_3, TITULO_3, user1, d1);
        recommendationRepository.save(r2);

        PostDto pDto = new PostDto(d1.getId(), d1.getTitle(), null, DISCUSSION, d1.getUser().getLogin(), d1.getDescription(), null);
        RecommendationDto recDto = new RecommendationDto(null, null, TITULO_2, TITULO_2, null, pDto, 5.0);

        ResponseEntity<Long> unexistentUserUpdated = recommendationService.updateRecommendation(UNEXISTENT, recDto, r2.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentUserUpdated.getStatusCode());
        Assert.assertEquals(null, unexistentUserUpdated.getBody());

        ResponseEntity<Long> unauthorizedUserUpdated = recommendationService.updateRecommendation(user2.getLogin(), recDto, r2.getId());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedUserUpdated.getStatusCode());
        Assert.assertEquals(null, unauthorizedUserUpdated.getBody());
    }

    @Test
    public void updateRecommendationTest() {

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Discussion d2 = new Discussion(TITULO_2, user2, DESCRIPCION_2, new HashSet<>());
        discussionRepository.save(d2);

        Recommendation r3 = new Recommendation(TITULO_3, TITULO_3, user2, d2);
        recommendationRepository.save(r3);

        PostDto pDto = new PostDto(d2.getId(), d2.getTitle(), null, DISCUSSION, d2.getUser().getLogin(), d2.getDescription(), null);
        RecommendationDto recDto = new RecommendationDto(null, null, TITULO_1, TITULO_1, null, pDto, 5.0);

        ResponseEntity<Long> updated = recommendationService.updateRecommendation(user2.getLogin(), recDto, r3.getId());
        Assert.assertEquals(HttpStatus.OK, updated.getStatusCode());
        Assert.assertEquals(r3.getId(), updated.getBody());

        ResponseEntity<List<RecommendationDto>> afterUpdate = recommendationService.getRecommendations(false,"");
        Assert.assertEquals(HttpStatus.OK, afterUpdate.getStatusCode());
        Assert.assertEquals(1, afterUpdate.getBody().size());
        Assert.assertFalse(TITULO_3 == afterUpdate.getBody().get(0).getRecText());
        Assert.assertEquals(TITULO_1, afterUpdate.getBody().get(0).getRecText());
    }

    @Test
    public void rateRecommendationUnexistentUserOrRecommendationTest() {
        ResponseEntity<Long> unexistentUser = recommendationService.rateRecommendation(UNEXISTENT, -1L, 6);
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentUser.getStatusCode());
        Assert.assertEquals(null, unexistentUser.getBody());

        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        ResponseEntity<Long> unexistentRecommendation = recommendationService.rateRecommendation(user1.getLogin(), -1L, 6);
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentRecommendation.getStatusCode());
        Assert.assertEquals(null, unexistentRecommendation.getBody());
    }

    @Test
    public void rateRecommendationTest() {
        UserProfile user1 = new UserProfile(USUARIO_1, USUARIO_1_MAIL, USUARIO_1, PHONE_1, "");
        userProfileRepository.save(user1);

        UserProfile user2 = new UserProfile(USUARIO_2, USUARIO_2_MAIL, USUARIO_2, PHONE_2, "");
        userProfileRepository.save(user2);

        Discussion d2 = new Discussion(TITULO_2, user2, DESCRIPCION_2, new HashSet<>());
        discussionRepository.save(d2);

        Recommendation r3 = new Recommendation(TITULO_3, TITULO_3, user2, d2);
        recommendationRepository.save(r3);

        ResponseEntity<Long> rateRecommendation = recommendationService.rateRecommendation(user1.getLogin(), r3.getId(), 7);
        Assert.assertEquals(HttpStatus.OK, rateRecommendation.getStatusCode());
        Optional<Interaction> a1 = interactionRepository.findByRecommendationAndUser(r3, user1);
        Interaction aux1;
        if (a1.isPresent()) {
            aux1 = a1.get();
            Assert.assertEquals(rateRecommendation.getBody(), aux1.getId());
            Assert.assertEquals(7, aux1.getRate());

            ResponseEntity<List<RecommendationDto>> afterFirstRating = recommendationService.getRecommendations(false,"");
            Assert.assertTrue(7 == afterFirstRating.getBody().get(0).getRating());

            ResponseEntity<Long> updateRateRecommendation = recommendationService.rateRecommendation(user1.getLogin(), r3.getId(), 3);
            Assert.assertEquals(HttpStatus.OK, updateRateRecommendation.getStatusCode());

            Optional<Interaction> a2 = interactionRepository.findByRecommendationAndUser(r3, user1);
            Interaction aux2;
            if (a2.isPresent()) {
                aux2 = a2.get();
                Assert.assertEquals(updateRateRecommendation.getBody(), aux2.getId());
                Assert.assertEquals(3, aux2.getRate());
                ResponseEntity<Long> newRateRecommendation = recommendationService.rateRecommendation(user2.getLogin(), r3.getId(), 5);
                Assert.assertEquals(HttpStatus.OK, newRateRecommendation.getStatusCode());

                ResponseEntity<List<RecommendationDto>> afterSecondRating = recommendationService.getRecommendations(false,"");
                Assert.assertTrue(((3+5)/2) == afterSecondRating.getBody().get(0).getRating());
            }
        }
    }
}
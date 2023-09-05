package es.tfg.musiccommunity;

import java.util.ArrayList;
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

import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.DiscussionService;
import es.tfg.musiccommunity.service.NotificationService;
import es.tfg.musiccommunity.service.PostService;
import es.tfg.musiccommunity.service.RecommendationService;
import es.tfg.musiccommunity.service.UserProfileService;
import es.tfg.musiccommunity.service.dto.CommentDto;
import es.tfg.musiccommunity.service.dto.DiscussionDto;
import es.tfg.musiccommunity.service.dto.NotificationDto;
import es.tfg.musiccommunity.service.dto.PostDto;
import es.tfg.musiccommunity.service.dto.RecommendationDto;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class NotificationServiceTest { 

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private PostService postService;

    private static final String TEST_1 = "ZXYYYY";
    private static final String TEST_1_MAIL = "test1@test1";
    private static final String TEST_2 = "GHIIII";
    private static final String TEST_2_MAIL = "test2@test2";
    private static final String TEST_3 = "ABCCCC";
    private static final String TEST_3_MAIL = "test3@test3";
    private static final String PHONE_1 = "123456789";
    private static final String PHONE_2 = "987654321";
    private static final String PHONE_3 = "123459876";
    private static final String UNEXISTENT = "unexistent";
    private static final String DISCUSSION = "Discussion";
    private static final String TYPE_1 = "NewFollower";
    private static final String TYPE_2 = "NewRecommendation";
    private static final String TYPE_3 = "NewPost";
    private static final String TYPE_4 = "NewRate";
    private static final String TYPE_5 = "NewComment";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";

    @Test
    public void notifyNewFollowerTest() {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        userProfileRepository.save(u1);
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);

        ResponseEntity<List<NotificationDto>> beforeNotifsUser2 = notificationService.getNotifications(u2.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, beforeNotifsUser2.getStatusCode());
        Assert.assertEquals(0, beforeNotifsUser2.getBody().size());

        userProfileService.followUser(u1.getLogin(), u2.getLogin());

        ResponseEntity<List<NotificationDto>> afterNotifsUser2 = notificationService.getNotifications(u2.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, afterNotifsUser2.getStatusCode());
        Assert.assertEquals(1, afterNotifsUser2.getBody().size());
        Assert.assertEquals(TYPE_1, afterNotifsUser2.getBody().get(0).getType());
        Assert.assertEquals(u2.getLogin(), afterNotifsUser2.getBody().get(0).getLogin());
        Assert.assertEquals(u1.getLogin(), afterNotifsUser2.getBody().get(0).getFrom());
    }

    @Test
    public void notifyNewPostTest()  {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        userProfileRepository.save(u1);
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);
        UserProfile u3 = new UserProfile(TEST_3, TEST_3_MAIL, TEST_3, PHONE_3, "");
        userProfileRepository.save(u3);

        userProfileService.followUser(u1.getLogin(), u2.getLogin());
        userProfileService.followUser(u2.getLogin(), u1.getLogin());        
        userProfileService.followUser(u3.getLogin(), u2.getLogin());

        DiscussionDto dDto = new DiscussionDto(null, TITLE, null, null, null, DESCRIPTION, new ArrayList<>());
        ResponseEntity<Long> response = discussionService.createDiscussion(u2.getLogin(), dDto);

        ResponseEntity<List<NotificationDto>> notifsUser1 = notificationService.getNotifications(u1.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, notifsUser1.getStatusCode());
        Assert.assertEquals(2, notifsUser1.getBody().size());
        Assert.assertEquals(TYPE_1, notifsUser1.getBody().get(1).getType());
        Assert.assertEquals(TYPE_3, notifsUser1.getBody().get(0).getType());
        Assert.assertEquals(u1.getLogin(), notifsUser1.getBody().get(0).getLogin());
        Assert.assertEquals(u2.getLogin(), notifsUser1.getBody().get(0).getFrom());
        Assert.assertEquals(response.getBody(), notifsUser1.getBody().get(0).getPostId());
        Assert.assertEquals(DISCUSSION, notifsUser1.getBody().get(0).getPostType());


        ResponseEntity<List<NotificationDto>> notifsUser3 = notificationService.getNotifications(u3.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, notifsUser3.getStatusCode());
        Assert.assertEquals(1, notifsUser3.getBody().size());
        Assert.assertEquals(TYPE_3, notifsUser3.getBody().get(0).getType());
        Assert.assertEquals(u3.getLogin(), notifsUser3.getBody().get(0).getLogin());
        Assert.assertEquals(u2.getLogin(), notifsUser3.getBody().get(0).getFrom());
        Assert.assertEquals(response.getBody(), notifsUser3.getBody().get(0).getPostId());
        Assert.assertEquals(DISCUSSION, notifsUser3.getBody().get(0).getPostType());
    }

    @Test
    public void notifyNewPostCommentedTest()  {
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);
        UserProfile u3 = new UserProfile(TEST_3, TEST_3_MAIL, TEST_3, PHONE_3, "");
        userProfileRepository.save(u3);

        userProfileService.followUser(u3.getLogin(), u2.getLogin());

        DiscussionDto dDto = new DiscussionDto(null, TITLE, null, null, null, DESCRIPTION, new ArrayList<>());
        ResponseEntity<Long> response = discussionService.createDiscussion(u2.getLogin(), dDto);

        CommentDto cDto = new CommentDto(null, "commentText", null, null, new ArrayList<>());
        ResponseEntity<Long> comment = postService.makeComment(u3.getLogin(), response.getBody(), cDto);


        ResponseEntity<List<NotificationDto>> notifsUser3 = notificationService.getNotifications(u3.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, notifsUser3.getStatusCode());
        Assert.assertEquals(1, notifsUser3.getBody().size());
        Assert.assertEquals(TYPE_3, notifsUser3.getBody().get(0).getType());
        Assert.assertEquals(u3.getLogin(), notifsUser3.getBody().get(0).getLogin());
        Assert.assertEquals(u2.getLogin(), notifsUser3.getBody().get(0).getFrom());

        ResponseEntity<List<NotificationDto>> notifsUser2 = notificationService.getNotifications(u2.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, notifsUser2.getStatusCode());
        Assert.assertEquals(2, notifsUser2.getBody().size());
        Assert.assertEquals(TYPE_1, notifsUser2.getBody().get(1).getType());
        Assert.assertEquals(TYPE_5, notifsUser2.getBody().get(0).getType());
        Assert.assertEquals(u2.getLogin(), notifsUser2.getBody().get(0).getLogin());
        Assert.assertEquals(u3.getLogin(), notifsUser2.getBody().get(0).getFrom());
        Assert.assertEquals(response.getBody(), notifsUser2.getBody().get(0).getPostId());
        Assert.assertEquals(DISCUSSION, notifsUser2.getBody().get(0).getPostType());
        Assert.assertEquals(comment.getBody(), notifsUser2.getBody().get(0).getCommentId());
    }

    @Test
    public void notifyNewRecommendationTest()  {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        userProfileRepository.save(u1);
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);

        userProfileService.followUser(u1.getLogin(), u2.getLogin());
        userProfileService.followUser(u2.getLogin(), u1.getLogin());

        DiscussionDto dDto = new DiscussionDto(null, TITLE, null, null, null, DESCRIPTION, new ArrayList<>());
        ResponseEntity<Long> response = discussionService.createDiscussion(u2.getLogin(), dDto);

        PostDto pDto = new PostDto(response.getBody(), "", null, null, null, null, null);
        RecommendationDto rDto = new RecommendationDto(null, null, "recTitle", "recText", null, pDto, 0);
        ResponseEntity<Long> recommendation = recommendationService.createRecommendation(u1.getLogin(), rDto);

        ResponseEntity<List<NotificationDto>> notifsUser2 = notificationService.getNotifications(u2.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, notifsUser2.getStatusCode());
        Assert.assertEquals(2, notifsUser2.getBody().size());
        Assert.assertEquals(TYPE_1, notifsUser2.getBody().get(1).getType());
        Assert.assertEquals(TYPE_2, notifsUser2.getBody().get(0).getType());
        Assert.assertEquals(u2.getLogin(), notifsUser2.getBody().get(0).getLogin());
        Assert.assertEquals(u1.getLogin(), notifsUser2.getBody().get(0).getFrom());
        Assert.assertEquals(recommendation.getBody(), notifsUser2.getBody().get(0).getRecommendationId());
    }

    @Test
    public void notifyNewRatedRecommendationTest()  {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        userProfileRepository.save(u1);
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);
        UserProfile u3 = new UserProfile(TEST_3, TEST_3_MAIL, TEST_3, PHONE_3, "");
        userProfileRepository.save(u3);

        userProfileService.followUser(u1.getLogin(), u2.getLogin());
        userProfileService.followUser(u2.getLogin(), u1.getLogin());

        DiscussionDto dDto = new DiscussionDto(null, TITLE, null, null, null, DESCRIPTION, new ArrayList<>());
        ResponseEntity<Long> response = discussionService.createDiscussion(u2.getLogin(), dDto);

        PostDto pDto = new PostDto(response.getBody(), "", null, null, null, null, null);
        RecommendationDto rDto = new RecommendationDto(null, null, "recTitle", "recText", null, pDto, 0);
        ResponseEntity<Long> recommendation = recommendationService.createRecommendation(u1.getLogin(), rDto);

        recommendationService.rateRecommendation(u3.getLogin(), recommendation.getBody(), 8);

        ResponseEntity<List<NotificationDto>> notifsUser1 = notificationService.getNotifications(u1.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, notifsUser1.getStatusCode());
        Assert.assertEquals(3, notifsUser1.getBody().size());
        Assert.assertEquals(TYPE_1, notifsUser1.getBody().get(2).getType());
        Assert.assertEquals(TYPE_3, notifsUser1.getBody().get(1).getType());
        Assert.assertEquals(TYPE_4, notifsUser1.getBody().get(0).getType());
        Assert.assertEquals(u1.getLogin(), notifsUser1.getBody().get(0).getLogin());
        Assert.assertEquals(u3.getLogin(), notifsUser1.getBody().get(0).getFrom());
        Assert.assertEquals(recommendation.getBody(), notifsUser1.getBody().get(0).getRecommendationId());
        Assert.assertEquals(8, notifsUser1.getBody().get(0).getRate());
    }

    @Test
    public void getUnexistentUserNotificationsTest(){
        ResponseEntity<List<NotificationDto>> notificationsUnexistent = notificationService.getNotifications(UNEXISTENT, true);
        Assert.assertEquals(HttpStatus.NOT_FOUND, notificationsUnexistent.getStatusCode());
        Assert.assertEquals(null, notificationsUnexistent.getBody());
    }

    @Test
    public void getReadAndUnreadUserNotificationsTest()  {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        userProfileRepository.save(u1);
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);

        userProfileService.followUser(u1.getLogin(), u2.getLogin());
        userProfileService.followUser(u2.getLogin(), u1.getLogin());

        ResponseEntity<List<NotificationDto>> notifsUser1 = notificationService.getNotifications(u1.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, notifsUser1.getStatusCode());
        Assert.assertEquals(1, notifsUser1.getBody().size());
        Assert.assertEquals(TYPE_1, notifsUser1.getBody().get(0).getType());

        ResponseEntity<Void> markAsRead1 = notificationService.markNotificationAsRead(u1.getLogin(), notifsUser1.getBody().get(0).getId());
        Assert.assertEquals(HttpStatus.OK, markAsRead1.getStatusCode());

        ResponseEntity<List<NotificationDto>> notifsUser1After = notificationService.getNotifications(u1.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, notifsUser1After.getStatusCode());
        Assert.assertEquals(0, notifsUser1After.getBody().size());

        ResponseEntity<List<NotificationDto>> allNotifsUser1 = notificationService.getNotifications(u1.getLogin(), false);
        Assert.assertEquals(HttpStatus.OK, allNotifsUser1.getStatusCode());
        Assert.assertEquals(1, allNotifsUser1.getBody().size());
        Assert.assertEquals(TYPE_1, allNotifsUser1.getBody().get(0).getType());
    }

    @Test
    public void markAsReadTest()  {
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);
        UserProfile u3 = new UserProfile(TEST_3, TEST_3_MAIL, TEST_3, PHONE_3, "");
        userProfileRepository.save(u3);

        userProfileService.followUser(u2.getLogin(), u3.getLogin());
        userProfileService.followUser(u3.getLogin(), u2.getLogin());

        ResponseEntity<List<NotificationDto>> notifsUser2 = notificationService.getNotifications(u2.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, notifsUser2.getStatusCode());
        Assert.assertEquals(1, notifsUser2.getBody().size());
        Assert.assertEquals(TYPE_1, notifsUser2.getBody().get(0).getType());

        ResponseEntity<Void> markAsRead1 = notificationService.markNotificationAsRead(UNEXISTENT, notifsUser2.getBody().get(0).getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, markAsRead1.getStatusCode());

        ResponseEntity<Void> markAsRead2 = notificationService.markNotificationAsRead(u2.getLogin(),-1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, markAsRead2.getStatusCode());

        ResponseEntity<Void> markAsRead3 = notificationService.markNotificationAsRead(u3.getLogin(), notifsUser2.getBody().get(0).getId());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, markAsRead3.getStatusCode());

        ResponseEntity<Void> markAsReadRight = notificationService.markNotificationAsRead(u2.getLogin(), notifsUser2.getBody().get(0).getId());
        Assert.assertEquals(HttpStatus.OK, markAsReadRight.getStatusCode());

        ResponseEntity<List<NotificationDto>> notifsUser1After = notificationService.getNotifications(u2.getLogin(), true);
        Assert.assertEquals(HttpStatus.OK, notifsUser1After.getStatusCode());
        Assert.assertEquals(0, notifsUser1After.getBody().size());
    }
}
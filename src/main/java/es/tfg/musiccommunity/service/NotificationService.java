package es.tfg.musiccommunity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import es.tfg.musiccommunity.model.Comment;
import es.tfg.musiccommunity.model.Notification;
import es.tfg.musiccommunity.model.Post;
import es.tfg.musiccommunity.model.Recommendation;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.NotificationRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.dto.NotificationDto;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private FirebaseService firebaseService;

    private static final String NEW_FOLLOWER = "NewFollower";
    private static final String NEW_RECOMMENDATION = "NewRecommendation";
    private static final String NEW_POST = "NewPost";
    private static final String NEW_RATE = "NewRate";
    private static final String NEW_COMMENT = "NewComment";


    public void notifyNewFollower(UserProfile fromUser, UserProfile user) {
        Notification not = new Notification(NEW_FOLLOWER, user, fromUser);
        notificationRepository.save(not);

        try {
            firebaseService.sendNotificationToUser(user.getFirebaseToken(), "New follower",
                    fromUser.getLogin() + " has started following you!.");
        } catch (FirebaseMessagingException e) {
            Logger.getLogger("NotificationService").info("Error on notifyNewFollower sending it to user " + user.getLogin());
            e.printStackTrace();
        }
    }

    public void notifyNewPost(UserProfile fromUser, Set<UserProfile> followers, Post post){
        for(UserProfile user : followers) {
            Notification nPost = new Notification(NEW_POST, user, fromUser, post.getType(), post);
            notificationRepository.save(nPost);

            try {
                firebaseService.sendNotificationToUser(user.getFirebaseToken(), "New post",
                        fromUser.getLogin() + " has created a new post - " + post.getType() + ": " + post.getTitle() + ".");
            } catch (FirebaseMessagingException e) {
                Logger.getLogger("NotificationService").info("Error on notifyNewPost sending it to user " + user.getLogin());
                e.printStackTrace();
            }
        }
    }

    public void notifyNewCommentedPost(UserProfile fromUser, UserProfile user, Post post, Comment comment) {
        Notification nCommentedPost = new Notification(NEW_COMMENT, user, fromUser, post.getType(), post, comment);
        notificationRepository.save(nCommentedPost);

        try {
            firebaseService.sendNotificationToUser(user.getFirebaseToken(), "New comment",
                    fromUser.getLogin() + " has commented on your post - " + post.getType() +": " + comment.getCommentText() + ".");
        } catch (FirebaseMessagingException e) {
            Logger.getLogger("NotificationService").info("Error on notifyNewCommentedPost sending it to user " + user.getLogin());
            e.printStackTrace();
        }
    }

    public void notifyNewRecommendation(UserProfile fromUser, Set<UserProfile> followers, Recommendation rec){
        for(UserProfile user : followers) {
            Notification nRec = new Notification(NEW_RECOMMENDATION, user, fromUser, rec);
            notificationRepository.save(nRec);

            try {
                firebaseService.sendNotificationToUser(user.getFirebaseToken(), "New recommendation",
                        fromUser.getLogin() + " has recommended a post - " + rec.getPost().getType() +": " + rec.getRecTitle() + ".");
            } catch (FirebaseMessagingException e) {
                Logger.getLogger("NotificationService").info("Error on notifyNewRecommendation sending it to user " + user.getLogin());
                e.printStackTrace();
            }
        }
    }

    public void notifyNewRatedRecommendation(UserProfile fromUser, UserProfile user, Recommendation rec, int rate) {
        Notification nRatedRec = new Notification(NEW_RATE, user, fromUser, rec, rate);
        notificationRepository.save(nRatedRec);

        try {
            firebaseService.sendNotificationToUser(user.getFirebaseToken(), "New recommendation",
                    fromUser.getLogin() + " has rated your recommendation : " + rec.getRecTitle() + ".");
        } catch (FirebaseMessagingException e) {
            Logger.getLogger("NotificationService").info("Error on notifyNewRecommendation sending it to user " + user.getLogin());
            e.printStackTrace();
        }
    }

    private List<NotificationDto> prepareNotificationDtos(List<Notification> notifs) {
        List<NotificationDto> notifsDto = new ArrayList<>();
        for(Notification n : notifs) {
            notifsDto.add(new NotificationDto(
                n.getId(), 
                n.getType(), 
                n.getNotificationDateTime(), 
                n.getUser().getLogin(),
                n.getFromUser().getLogin(), 
                n.getPostType(), 
                n.getPost() != null ? n.getPost().getId() : null, 
                n.getComment() != null ? n.getComment().getId() : null, 
                n.getRecommendation() != null ? n.getRecommendation().getId() : null, 
                n.getRate()));
        }
        return notifsDto;
    }

    public ResponseEntity<List<NotificationDto>> getNotifications(String login, boolean unread) {
        List<Notification> notifications;
        Optional<UserProfile> u = userProfileRepository.findByLogin(login);
        if (!u.isPresent()){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
        UserProfile user = u.get();
        if(unread) {
            notifications = notificationRepository.findAllNotReadByUser(user);
        } else {
            notifications = notificationRepository.findAllByUser(user);
        }
        return new ResponseEntity<>(prepareNotificationDtos(notifications),HttpStatus.OK); 
    }

    public ResponseEntity<Void> markNotificationAsRead(String login, Long id) {
        Optional<UserProfile> u = userProfileRepository.findByLogin(login);
        if(!u.isPresent()){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
        UserProfile user = u.get();
        Optional<Notification> notif = notificationRepository.findById(id);
        if(!notif.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Notification notification = notif.get();
        if(notification.getUser() != user) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        notification.setIsRead();
        notificationRepository.save(notification);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
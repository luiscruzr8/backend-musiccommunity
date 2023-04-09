package es.tfg.musiccommunity.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tfg.musiccommunity.model.Comment;
import es.tfg.musiccommunity.model.Interaction;
import es.tfg.musiccommunity.model.Notification;
import es.tfg.musiccommunity.model.Post;
import es.tfg.musiccommunity.model.Recommendation;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.repository.CommentRepository;
import es.tfg.musiccommunity.repository.InteractionRepository;
import es.tfg.musiccommunity.repository.NotificationRepository;
import es.tfg.musiccommunity.repository.RecommendationRepository;
import es.tfg.musiccommunity.repository.TagRepository;
import es.tfg.musiccommunity.service.dto.TagDto;

@Service
public class CommonService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TagRepository tagRepository;

    protected Set<Tag> handleTags(List<TagDto> tagsFromDto) {
        Set<Tag> tags = new HashSet<>();
        if (!tagsFromDto.isEmpty()){
            for (TagDto t : tagsFromDto) {
                String s = t.getTagName();
                Optional<Tag> aux = tagRepository.findByTagName(s.toLowerCase());
                Tag tag;
                if (!aux.isPresent()){
                    tag = new Tag(s.toLowerCase());
                    tagRepository.save(tag);
                } else {
                    tag = aux.get();
                }
                tags.add(tag);
            }
        }
        return tags;
    }

    protected void deleteComments(Post p) {
        List<Comment> commentsToDelete = commentRepository.findByPostOrderByCommentDateDesc(p);
        for (Comment comment : commentsToDelete) {
            List<Comment> responsesToComment = commentRepository.findByResponseToOrderByCommentDateDesc(comment);
            for (Comment response : responsesToComment) {
                commentRepository.delete(response);
            }
            commentRepository.delete(comment);
        }
    }

    protected void deleteRecommendations(Post p) {
        List<Recommendation> recommendationsToDelete = recommendationRepository.findByPost(p);
        for (Recommendation recommendation : recommendationsToDelete) {
            List<Interaction> interactions = interactionRepository.findByRecommendation(recommendation);
            for (Interaction it : interactions) {
                interactionRepository.delete(it);
            }
            recommendationRepository.delete(recommendation);
        }
    }

    /* Función auxiliar común a las 3 siguientes */
    private void deleteNotifications(List<Notification> notificationsToDelete) {
        for (Notification notif : notificationsToDelete) {
            notificationRepository.delete(notif);
        }
    }

    protected void deleteNotificationsOfRecommendation(Recommendation rec) {
        List<Notification> notificationsToDelete = notificationRepository.findByRecommendation(rec);
        deleteNotifications(notificationsToDelete);
    }

    protected void deleteNotificationsOfComment(Comment com) {
        List<Notification> notificationsToDelete = notificationRepository.findByComment(com);
        deleteNotifications(notificationsToDelete);
    }

    protected void deleteNotificationsOfPost(Post post) {
        List<Notification> notificationsToDelete = notificationRepository.findByPost(post);
        deleteNotifications(notificationsToDelete);
    }
}
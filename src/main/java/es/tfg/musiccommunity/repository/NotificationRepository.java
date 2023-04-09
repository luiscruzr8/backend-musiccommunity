package es.tfg.musiccommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.Comment;
import es.tfg.musiccommunity.model.Notification;
import es.tfg.musiccommunity.model.Post;
import es.tfg.musiccommunity.model.Recommendation;
import es.tfg.musiccommunity.model.UserProfile;

@Repository
public interface NotificationRepository extends JpaRepository <Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = FALSE ORDER BY n.notificationDateTime DESC")
    public List<Notification> findAllNotReadByUser(@Param("user") UserProfile user);

    @Query("SELECT n FROM Notification n WHERE n.user = :user ORDER BY n.notificationDateTime DESC")
    public List<Notification> findAllByUser(@Param("user") UserProfile user);

    public List<Notification> findByRecommendation(Recommendation rec);

    public List<Notification> findByPost(Post post);

    public List<Notification> findByComment(Comment com);
    
}

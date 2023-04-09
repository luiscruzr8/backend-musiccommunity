package es.tfg.musiccommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.Post;
import es.tfg.musiccommunity.model.Recommendation;
import es.tfg.musiccommunity.model.UserProfile;

@Repository
public interface RecommendationRepository extends JpaRepository <Recommendation, Long> {

    public List<Recommendation> findByPost(Post post);

    public List<Recommendation> findByUserOrderByCreationDateTimeAsc(UserProfile user);

    @Query("SELECT r FROM Recommendation r WHERE r.user = :user AND LOWER(r.recTitle) LIKE %:recTitle% ORDER BY r.creationDateTime ASC")
    public List<Recommendation> findByUserAndRecTitleOrderByCreationDateTimeAsc(@Param("user") UserProfile user, @Param("recTitle") String recTitle);

    public List<Recommendation> findTop10ByUserOrderByRatingDesc(UserProfile user);

    @Query("SELECT r FROM Recommendation r WHERE r.user = :user AND LOWER(r.recTitle) LIKE %:recTitle% ORDER BY r.rating DESC")
    public List<Recommendation> findTop10ByUserAndRecTitleOrderByRatingDesc(@Param("user") UserProfile user, @Param("recTitle") String recTitle);

    public List<Recommendation> findAllByOrderByCreationDateTimeAsc();

    @Query("SELECT r FROM Recommendation r WHERE LOWER(r.recTitle) LIKE %:recTitle% ORDER BY r.creationDateTime ASC")
    public List<Recommendation> findAllByRecTitleOrderByCreationDateTimeAsc(@Param("recTitle") String recTitle);

    public List<Recommendation> findTop10ByOrderByRatingDesc();

    @Query("SELECT r FROM Recommendation r WHERE LOWER(r.recTitle) LIKE %:recTitle% ORDER BY r.rating DESC")
    public List<Recommendation> findTop10ByRecTitleOrderByRatingDesc(@Param("recTitle") String recTitle);

}

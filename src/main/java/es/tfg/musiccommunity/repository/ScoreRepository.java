package es.tfg.musiccommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.Score;
import es.tfg.musiccommunity.model.UserProfile;

@Repository
public interface ScoreRepository extends JpaRepository <Score,Long> {

    public List<Score> findByUserOrderByScoreName(UserProfile user);

    @Query("SELECT s FROM Score s WHERE s.user = :user AND s.isPublic = true ORDER BY s.scoreName")
    public List<Score> findPublicScoresByUserOrderByScoreName(@Param("user") UserProfile user);

    @Query("SELECT s FROM Score s WHERE s.user = :user AND LOWER(s.scoreName) LIKE %:keyword% ORDER BY s.scoreName")
    public List<Score> findByUserAndScoreNameOrderByScoreName(@Param("user") UserProfile user, @Param("keyword") String keyword);

    @Query("SELECT s FROM Score s WHERE s.user = :user AND s.isPublic = true AND LOWER(s.scoreName) LIKE %:keyword% ORDER BY s.scoreName")
    public List<Score> findPublicScoresByUserAndScoreOrderByScoreName(@Param("user") UserProfile user, @Param("keyword") String keyword);

}

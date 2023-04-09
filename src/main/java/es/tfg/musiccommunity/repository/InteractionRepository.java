package es.tfg.musiccommunity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.Recommendation;
import es.tfg.musiccommunity.model.Interaction;
import es.tfg.musiccommunity.model.UserProfile;

@Repository
public interface InteractionRepository extends JpaRepository <Interaction, Long> {

    @Query("SELECT ROUND(AVG(i.rate),0) FROM Interaction i WHERE i.recommendation = :rec")
    public Optional<Integer> getAvgRateForRecommendation(@Param("rec") Recommendation rec);

    @Query("SELECT i FROM Interaction i WHERE i.recommendation = :rec")
    public List<Interaction> findByRecommendation(@Param("rec") Recommendation rec);

    @Query("SELECT i FROM Interaction i WHERE i.user = :user")
    public List<Interaction> findByUser(@Param("user") UserProfile user);

    @Query("SELECT i FROM Interaction i WHERE i.recommendation = :rec AND i.user = :user")
    public Optional<Interaction> findByRecommendationAndUser(@Param("rec") Recommendation rec, @Param("user") UserProfile user);
    
}

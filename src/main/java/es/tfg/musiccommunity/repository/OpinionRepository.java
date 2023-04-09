package es.tfg.musiccommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.Opinion;
import es.tfg.musiccommunity.model.Score;
import es.tfg.musiccommunity.model.UserProfile;

@Repository
public interface OpinionRepository extends BasePostRepository<Opinion>, JpaRepository<Opinion, Long> {

    public List<Opinion> findByUser(UserProfile user);

    public int countByScore(Score score);

    public List<Opinion> findByScore(Score score);

}

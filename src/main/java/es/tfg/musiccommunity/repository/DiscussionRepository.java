package es.tfg.musiccommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.Discussion;
import es.tfg.musiccommunity.model.UserProfile;

@Repository
public interface DiscussionRepository extends BasePostRepository<Discussion>, JpaRepository<Discussion, Long> {

    public List<Discussion> findByUserOrderByCreationDateTimeDesc(UserProfile user);
}

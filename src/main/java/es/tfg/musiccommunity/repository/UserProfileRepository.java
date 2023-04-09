package es.tfg.musiccommunity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository <UserProfile, Long> {

    Optional<UserProfile> findByLogin(String login);

    UserProfile findUserByLogin(String login);

    List<UserProfile> findByOrderByLogin();

    @Query("SELECT u FROM UserProfile u WHERE LOWER(u.login) LIKE %:login% ORDER BY u.login ASC")
    List<UserProfile> findAllByLoginLike(@Param("login") String login);

    Boolean existsByLogin(String login);

    Boolean existsByEmail(String email);

    List<UserProfile> findAllByInterestsOrderByLoginAsc(Tag tag);

}


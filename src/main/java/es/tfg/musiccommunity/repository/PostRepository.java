package es.tfg.musiccommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.City;
import es.tfg.musiccommunity.model.Post;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;

@Repository
public interface PostRepository extends BasePostRepository<Post>, JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.user = :user ORDER BY p.creationDateTime ASC")
    public List<Post> findByUser(@Param("user") UserProfile user);

    List<Post> findAllByTagsOrderByCreationDateTimeAsc(Tag tag);

    @Query("SELECT p FROM Post p WHERE p.user = :user AND p.type = :type ORDER BY p.creationDateTime ASC")
    public List<Post> findByUserAndPostType(@Param("user") UserProfile user, @Param("type") String type);

    @Query("SELECT p FROM Post p WHERE p.user = :user AND LOWER(p.title) LIKE %:keyword% ORDER BY p.creationDateTime ASC")
    public List<Post> findByUserAndTitle(@Param("user") UserProfile user, @Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.user = :user AND p.type = :type AND LOWER(p.title) LIKE %:keyword% ORDER BY p.creationDateTime ASC")
    public List<Post> findByUserAndTypeAndTitle(@Param("user") UserProfile user, @Param("type") String type, @Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.type = :type ORDER BY p.creationDateTime ASC")
    public List<Post> findByType(@Param("type") String type);

    @Query("SELECT p FROM Post p WHERE p.type = :type AND LOWER(p.title) LIKE %:keyword% ORDER BY p.creationDateTime ASC")
    public List<Post> findByTypeAndTitle(@Param("type") String type, @Param("keyword") String keyword);

    @Query("SELECT p from Post p WHERE p.city IN :cities ORDER BY p.creationDateTime ASC")
    public List<Post> findByCityIn(@Param("cities") List<City> cities);

    @Query("SELECT p from Post p WHERE p.city = :city ORDER BY p.creationDateTime ASC")
    public List<Post> findByClosestCity(@Param("city") City city);

}

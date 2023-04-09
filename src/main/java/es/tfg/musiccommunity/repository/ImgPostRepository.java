package es.tfg.musiccommunity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.ImgPost;
import es.tfg.musiccommunity.model.Post;

@Repository
public interface ImgPostRepository extends JpaRepository <ImgPost, Long> {

    public Optional<ImgPost> findByPost(Post post);

}

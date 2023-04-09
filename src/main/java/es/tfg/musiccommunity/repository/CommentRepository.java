package es.tfg.musiccommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.Comment;
import es.tfg.musiccommunity.model.Post;

@Repository
public interface CommentRepository extends JpaRepository <Comment, Long> {

    public List<Comment> findByPost(Post post);

    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.responseTo IS NULL " + 
        "ORDER BY c.commentDate DESC") 
    public List<Comment> findByPostOrderByCommentDateDesc(@Param("post") Post post);

    @Query("SELECT c FROM Comment c WHERE c.responseTo = :responseTo " + 
        "ORDER BY c.commentDate DESC") 
    public List<Comment> findByResponseToOrderByCommentDateDesc(@Param("responseTo") Comment responseTo);


}
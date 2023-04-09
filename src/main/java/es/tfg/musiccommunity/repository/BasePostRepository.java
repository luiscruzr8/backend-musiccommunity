package es.tfg.musiccommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import es.tfg.musiccommunity.model.Post;

@NoRepositoryBean // Read-Only
public interface BasePostRepository <EntityType extends Post> extends JpaRepository<EntityType, Long> {
    // #{#entityName} will be magically replaced by type arguments in children

    @Query("SELECT e FROM #{#entityName} e ORDER BY creation_date_time ASC") 
    List<EntityType> findThemAllOrderByCreationDateTimeAsc();

    @Query("SELECT e FROM #{#entityName} e WHERE LOWER(e.title) LIKE %:keyword% ORDER BY creation_date_time ASC")
    List<EntityType> findThemAllByTitleOrderByCreationDateTimeAsc(@Param("keyword") String keyword);
}

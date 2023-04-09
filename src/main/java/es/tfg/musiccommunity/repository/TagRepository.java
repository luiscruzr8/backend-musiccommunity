package es.tfg.musiccommunity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tfg.musiccommunity.model.Tag;

@Repository
public interface TagRepository extends JpaRepository <Tag, Long> {
    
    public Boolean existsByTagName(String tagName);

    public Optional<Tag> findByTagName(String tagName);
}

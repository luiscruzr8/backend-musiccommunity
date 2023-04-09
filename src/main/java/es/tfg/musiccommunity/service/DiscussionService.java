package es.tfg.musiccommunity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.tfg.musiccommunity.model.Discussion;
import es.tfg.musiccommunity.model.ImgPost;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.DiscussionRepository;
import es.tfg.musiccommunity.repository.ImgPostRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.dto.DiscussionDto;
import es.tfg.musiccommunity.service.dto.TagDto;

@Service
public class DiscussionService {

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private ImgPostRepository imgPostRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private NotificationService notificationService;

    /* TODOS LOS DISCUSIONES */
    public ResponseEntity<List<DiscussionDto>> getAllDiscussions(String keyword) {
        List<Discussion> discussions;
        if (keyword.isEmpty()) {
            discussions = discussionRepository.findThemAllOrderByCreationDateTimeAsc();
        } else {
            discussions = discussionRepository.findThemAllByTitleOrderByCreationDateTimeAsc(keyword);
        }
        List<DiscussionDto> discussionsDto = new ArrayList<>(25);
        for (Discussion discussion : discussions) {
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> discussionTags = discussion.getTags().stream().collect(Collectors.toList()) ;
            discussionTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : discussionTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            discussionsDto.add(new DiscussionDto(discussion.getId(), discussion.getTitle(), discussion.getCreationDateTime(),
                discussion.getType(), discussion.getUser().getLogin(), discussion.getDescription(), tags));
        }
        return new ResponseEntity<>((discussionsDto), HttpStatus.OK);
    }

    /* INFORMACION DE UNA DISCUSION POR ID */
    public ResponseEntity<DiscussionDto> getDiscussionInfo(Long id) {
        Optional<Discussion> d = discussionRepository.findById(id);
        if (d.isPresent()) {
            Discussion discussion = d.get();
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> discussionTags = discussion.getTags().stream().collect(Collectors.toList()) ;
            discussionTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : discussionTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            DiscussionDto discussionDto = new DiscussionDto(discussion.getId(), discussion.getTitle(), discussion.getCreationDateTime(),
                discussion.getType(), discussion.getUser().getLogin(), discussion.getDescription(), tags);
            return new ResponseEntity<>((discussionDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /* CREACIÓN DE UNA DISCUSION */
    @Transactional
    public ResponseEntity<Long> createDiscussion(String login, DiscussionDto discussionData) {
        Set<Tag> tags;
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (user.isPresent()) {
            tags = commonService.handleTags(discussionData.getTags());
            Discussion discussion = new Discussion(discussionData.getTitle(), user.get(), 
                discussionData.getDescription(), tags);
            discussion = discussionRepository.save(discussion);
            notificationService.notifyNewPost(user.get(), user.get().getFollowers(), discussion);
            return new ResponseEntity<>((discussion.getId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /* ACTUALIZACIÓN DE UNA DISCUSION */
    @Transactional
    public ResponseEntity<Long> updateDiscussion(String login, DiscussionDto updatedDiscussion, Long discussionId) {
        Set<Tag> tags;
        Optional<Discussion> discussionToUpdate = discussionRepository.findById(discussionId);
        if (discussionToUpdate.isPresent()) { 
            /* COMPROBAMOS QUE QUIEN ACTUALIZA SEA EL MISMO CREADOR */
            Optional<UserProfile> user = userProfileRepository.findByLogin(login);
            if (user.isPresent()) {
                if (!discussionToUpdate.get().getUser().equals(user.get())) {
                    return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
                }
                Discussion discussion = discussionToUpdate.get();
                tags = commonService.handleTags(updatedDiscussion.getTags());
                discussion.setTitle(updatedDiscussion.getTitle());
                discussion.setDescription(updatedDiscussion.getDescription());
                discussion.setTags(tags);
                discussion = discussionRepository.save(discussion);

                return new ResponseEntity<>((discussion.getId()), HttpStatus.OK);
            } 
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

    /* BORRADO DE UNA DISCUSION */
    @Transactional
    public ResponseEntity<Void> deleteDiscussion(String login, Long discussionId) {
        Optional<Discussion> discussion = discussionRepository.findById(discussionId);
        if (!discussion.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        /* SE COMPRUEBA QUE EL USUARIO SEA SU CREADOR */
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (!user.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        } else {
            if (!user.get().equals(discussion.get().getUser())) {
                return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
            }
            /* BORRAMOS IMAGEN */
            Optional<ImgPost> imgPost = imgPostRepository.findByPost(discussion.get());
            if (imgPost.isPresent()) {
                imgPostRepository.delete(imgPost.get());
            }
            /* BORRAMOS NOTIFICACIONES */
            commonService.deleteNotificationsOfPost(discussion.get());
            /* BORRAMOS COMENTARIOS */
            commonService.deleteComments(discussion.get());
            /* BORRAMOS RECOMENDACIONES */
            commonService.deleteRecommendations(discussion.get());
            discussionRepository.delete(discussion.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}

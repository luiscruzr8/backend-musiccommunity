package es.tfg.musiccommunity.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.tfg.musiccommunity.model.Interaction;
import es.tfg.musiccommunity.model.Post;
import es.tfg.musiccommunity.model.Recommendation;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.InteractionRepository;
import es.tfg.musiccommunity.repository.PostRepository;
import es.tfg.musiccommunity.repository.RecommendationRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.dto.PostDto;
import es.tfg.musiccommunity.service.dto.RecommendationDto;
import es.tfg.musiccommunity.service.dto.TagDto;

@Service
public class RecommendationService {

    @Autowired
    private InteractionRepository interactionRepository;
    
    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommonService commonService;

    public ResponseEntity<List<RecommendationDto>> getRecommendations(boolean top10, String keyword) {
        List<Recommendation> recommendations;
        if (top10 && keyword.isEmpty()) {
            recommendations = recommendationRepository.findTop10ByOrderByRatingDesc();
        } else if (top10 && !keyword.isEmpty()){
            recommendations = recommendationRepository.findTop10ByRecTitleOrderByRatingDesc(keyword);
        } else if (!top10 && keyword.isEmpty()) {
            recommendations = recommendationRepository.findAllByOrderByCreationDateTimeAsc();
        } else {
            recommendations = recommendationRepository.findAllByRecTitleOrderByCreationDateTimeAsc(keyword);
        }
        List<RecommendationDto> recommendationsDto = new ArrayList<>(25);
        for (Recommendation r : recommendations) {
            Post p = r.getPost();
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> postTags = p.getTags().stream().collect(Collectors.toList()) ;
            postTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : postTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            PostDto pDto = new PostDto(p.getId(), p.getTitle(), p.getCreationDateTime(), p.getType(),
                p.getUser().getLogin(), p.getDescription(), tags);
            recommendationsDto.add(new RecommendationDto(r.getId(), r.getCreationDateTime(), r.getRecTitle(), r.getRecText(),
                r.getUser().getLogin(), pDto, r.getRating()));
        }
        return new ResponseEntity<>((recommendationsDto), HttpStatus.OK);
    }

    public ResponseEntity <List<RecommendationDto>> getUserRecommendations(String login, boolean top10, String keyword) {
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (!user.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        } else {
            List<RecommendationDto> recommendationsDto = new ArrayList<>(25);
            List<Recommendation> recommendations;
            if (top10 && keyword.isEmpty()) {
                recommendations = recommendationRepository.findTop10ByUserOrderByRatingDesc(user.get());
            } else if (top10 && !keyword.isEmpty()){
                recommendations = recommendationRepository.findTop10ByUserAndRecTitleOrderByRatingDesc(user.get(), keyword);
            } else if (!top10 && keyword.isEmpty()) {
                recommendations = recommendationRepository.findByUserOrderByCreationDateTimeAsc(user.get());
            } else {
                recommendations = recommendationRepository.findByUserAndRecTitleOrderByCreationDateTimeAsc(user.get(), keyword);
            }
            for (Recommendation r : recommendations) {
                Post p = r.getPost();
                List<TagDto> tags = new ArrayList<>(5);
                List<Tag> postTags = p.getTags().stream().collect(Collectors.toList()) ;
                postTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
                for (Tag tag : postTags) {
                    tags.add(new TagDto(tag.getTagName()));
                }
                PostDto pDto = new PostDto(p.getId(), p.getTitle(), p.getCreationDateTime(), p.getType(),
                    p.getUser().getLogin(), p.getDescription(), tags);
                recommendationsDto.add(new RecommendationDto(r.getId(), r.getCreationDateTime(), r.getRecTitle(), r.getRecText(),
                    r.getUser().getLogin(), pDto, r.getRating()));
            }
            return new ResponseEntity<>((recommendationsDto), HttpStatus.OK);
        }
    }

    /* INFORMACION DE UNA RECOMENDACIÓN POR ID */
    public ResponseEntity<RecommendationDto> getRecommendationInfo(Long id) {
        Optional<Recommendation> r = recommendationRepository.findById(id);
        if (r.isPresent()) {
            Recommendation rec = r.get();
            List<TagDto> tags = new ArrayList<>(5);
            Post p = rec.getPost();
            List<Tag> postTags = p.getTags().stream().collect(Collectors.toList()) ;
            postTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : postTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            PostDto pDto = new PostDto(p.getId(), p.getTitle(), p.getCreationDateTime(), p.getType(),
                    p.getUser().getLogin(), p.getDescription(), tags);
            RecommendationDto recommendationDto = new RecommendationDto(rec.getId(), rec.getCreationDateTime(), rec.getRecTitle(),
                rec.getRecText(), rec.getUser().getLogin(), pDto, rec.getRating());
            return new ResponseEntity<>((recommendationDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /* CREACIÓN DE UNA RECOMENDACIÓN */
    @Transactional
    public ResponseEntity<Long> createRecommendation(String login, RecommendationDto recData) {
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (!user.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        Optional<Post> p = postRepository.findById(recData.getPostId());
        if (!p.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        } 
        Post post = p.get();
        Recommendation rec = new Recommendation(recData.getRecTitle(), recData.getRecText(), user.get(), post);
        rec = recommendationRepository.save(rec);
        notificationService.notifyNewRecommendation(user.get(), user.get().getFollowers(), rec);
        return new ResponseEntity<>((rec.getId()), HttpStatus.OK);
    }

    /* ACTUALIZACION DE UNA RECOMENDACIÓN */
    @Transactional
    public ResponseEntity<Long> updateRecommendation(String login, RecommendationDto updatedRec, Long recId) {
        Optional<Recommendation> recToUpdate = recommendationRepository.findById(recId);
        if (recToUpdate.isPresent()) { 
            /* COMPROBAMOS QUE QUIEN ACTUALIZA SEA EL MISMO CREADOR */
            Optional<UserProfile> user = userProfileRepository.findByLogin(login);
            if (user.isPresent()) {
                if (!recToUpdate.get().getUser().equals(user.get())) {
                    return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
                }
                Recommendation recommendation = recToUpdate.get();
                recommendation.setRecTitle(updatedRec.getRecTitle());
                recommendation.setRecText(updatedRec.getRecText());
                recommendation = recommendationRepository.save(recommendation);
                return new ResponseEntity<>((recommendation.getId()), HttpStatus.OK);
            } 
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

    /* BORRADO DE UNA RECOMENDACIÓN */
    @Transactional
    public ResponseEntity<Void> deleteRecommendation(String login, Long recommendationId) {
        /* PRIMERO SE BUSCA LA RECOMENDACION */
        Optional<Recommendation> recommendation = recommendationRepository.findById(recommendationId);
        if (!recommendation.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        /* SE COMPRUEBA QUE EL USUARIO SEA SU CREADOR */
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (!user.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        } else {
            if (!user.get().equals(recommendation.get().getUser())) {
                return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
            }
            /* SE BORRAN LAS NOTIFICACIONES */
            commonService.deleteNotificationsOfRecommendation(recommendation.get());
            /* SE BORRA  LA RECOMENDACIÓN */
            recommendationRepository.delete(recommendation.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @Transactional
    public ResponseEntity<Long> rateRecommendation(String login, Long recId, int newRate) {
        Interaction interaction;
        Optional<Integer> newRating;
        Optional<UserProfile> u = userProfileRepository.findByLogin(login);
        if (!u.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        Optional<Recommendation> r = recommendationRepository.findById(recId);
        if (!r.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        UserProfile user = u.get();
        Recommendation rec = r.get();
        Optional<Interaction> i = interactionRepository.findByRecommendationAndUser(rec, user);
        if (i.isPresent()) {
            interaction = i.get();
            interaction.setRate(newRate);
            interaction.setRateDateTime(LocalDateTime.now());
            interaction = interactionRepository.save(interaction);
        } else {
            interaction = new Interaction(user, rec, newRate);
            interaction = interactionRepository.save(interaction);
        }
        notificationService.notifyNewRatedRecommendation(user, rec.getUser(), rec, newRate);
        newRating = interactionRepository.getAvgRateForRecommendation(rec);
        if (newRating.isPresent()) {
            rec.setRating(newRating.get());
            recommendationRepository.save(rec);
        }
        return new ResponseEntity<>((interaction.getId()), HttpStatus.OK);
    }
}
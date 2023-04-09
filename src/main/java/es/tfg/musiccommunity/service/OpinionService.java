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

import es.tfg.musiccommunity.model.Score;
import es.tfg.musiccommunity.model.Opinion;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.ScoreRepository;
import es.tfg.musiccommunity.repository.OpinionRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.dto.ScoreDto;
import es.tfg.musiccommunity.service.dto.OpinionDto;
import es.tfg.musiccommunity.service.dto.TagDto;

@Service
public class OpinionService {

    @Autowired 
    private OpinionRepository opinionRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private NotificationService notificationService;

    /* TODOS LOS POSTS DE OPINION (CON PARTITURA) */
    public ResponseEntity<List<OpinionDto>> getAllOpinions(String keyword) {
        List<Opinion> opinions;
        if (keyword.isEmpty()) {
            opinions = opinionRepository.findThemAllOrderByCreationDateTimeAsc();
        } else {
            opinions = opinionRepository.findThemAllByTitleOrderByCreationDateTimeAsc(keyword);
        }
        List<OpinionDto> opinionsDto = new ArrayList<>(25);
        for(Opinion opinion: opinions) {
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> opinionTags = opinion.getTags().stream().collect(Collectors.toList()) ;
            opinionTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : opinionTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            Score s = opinion.getScore();
            ScoreDto sDto = new ScoreDto(s.getId(), s.getScoreName().replace(".pdf", ""), s.getFileType(), s.getUser().getLogin(),
                s.getUploadDateTime());
            opinionsDto.add(new OpinionDto(opinion.getId(), opinion.getTitle(), opinion.getCreationDateTime(), opinion.getType(),
                opinion.getUser().getLogin(), opinion.getDescription(), sDto, tags));
        }
        return new ResponseEntity<>((opinionsDto), HttpStatus.OK);   
    }

    /* INFORMACION DE UN POST DE OPINION (CON PARTITURA) */
    public ResponseEntity<OpinionDto> getOpinionInfo(Long id) {
        Optional<Opinion> t = opinionRepository.findById(id);
        if (t.isPresent()) {
            Opinion opinion = t.get();
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> opinionTags = opinion.getTags().stream().collect(Collectors.toList()) ;
            opinionTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : opinionTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            Score s = opinion.getScore();
            ScoreDto sDto = new ScoreDto(s.getId(), s.getScoreName().replace(".pdf", ""), s.getFileType(), s.getUser().getLogin(),
                s.getUploadDateTime());
            OpinionDto opinionDto = new OpinionDto(opinion.getId(), opinion.getTitle(), opinion.getCreationDateTime(), opinion.getType(),
                opinion.getUser().getLogin(), opinion.getDescription(), sDto, tags);
            return new ResponseEntity<>((opinionDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /* CREACIÓN DE UN POST DE OPINION (CON PARTITURA) */
    @Transactional
    public ResponseEntity<Long> createOpinion(String login, OpinionDto opinionData) {
        Set<Tag> tags;
        Optional<Score> s = scoreRepository.findById(opinionData.getScoreId());
        if (s.isPresent()){
            Score score = s.get();
            Optional<UserProfile> u = userProfileRepository.findByLogin(login);
            if (u.isPresent()) {
                UserProfile user = u.get();
                tags = commonService.handleTags(opinionData.getTags());
                /* CAMBIAMOS EL ESTADO DE LA PARTITURA A PUBLICA */
                if (opinionRepository.countByScore(score) == 0 ) {
                    score.setIsPublic(true);
                    scoreRepository.save(score);
                }
                Opinion opinion = new Opinion(opinionData.getTitle(), user, score, opinionData.getDescription(), tags);
                opinion = opinionRepository.save(opinion);
                notificationService.notifyNewPost(user, user.getFollowers(), opinion);
                return new ResponseEntity<>((opinion.getId()), HttpStatus.CREATED);
            }
        } 
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    /* ACTUALIZACIÓN DE UN POST DE OPINION (CON PARTITURA) */
    @Transactional
    public ResponseEntity<Long> updateOpinion(String login, OpinionDto updatedOpinion, Long opinionId) {
        Set<Tag> tags;
        Optional<Opinion> opinionToUpdate = opinionRepository.findById(opinionId);
        if (opinionToUpdate.isPresent()) {
            Optional<UserProfile> u = userProfileRepository.findByLogin(login);
            if (u.isPresent()) {
                UserProfile user = u.get();
                if (!opinionToUpdate.get().getUser().equals(user)) {
                    return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
                }
                Opinion opinion = opinionToUpdate.get();
                tags = commonService.handleTags(updatedOpinion.getTags());
                opinion.setTitle(updatedOpinion.getTitle());
                opinion.setDescription(updatedOpinion.getDescription());
                opinion.setTags(tags);
                opinion = opinionRepository.save(opinion);
                return new ResponseEntity<>((opinion.getId()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    
    /* BORRADO DE UN DE OPINION (CON PARTITURA) */
    @Transactional
    public ResponseEntity<Void> deleteOpinion(String login, Long opinionId) {
        /* PRIMERO BUSCAMOS EL POST */
        Optional<Opinion> opinion = opinionRepository.findById(opinionId);
        if (!opinion.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        } 
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (!user.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        } else {
            if (!user.get().equals(opinion.get().getUser())) {
                return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
            }
            Score score = opinion.get().getScore();
            /* BORRAMOS NOTIFICACIONES */
            commonService.deleteNotificationsOfPost(opinion.get());
            /* BORRAMOS COMENTARIOS */
            commonService.deleteComments(opinion.get());
            /* BORRAMOS RECOMENDACIONES */
            commonService.deleteRecommendations(opinion.get());
            /* SE BORRA EL POST */
            opinionRepository.delete(opinion.get());
            /* CAMBIAMOS EL ESTADO DE LA PARTITURA A PUBLICA SI LA PARTITURA NO TIENES MAS POSTS*/
            if (opinionRepository.countByScore(score) < 1) {
                score.setIsPublic(false);
                scoreRepository.save(score);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}

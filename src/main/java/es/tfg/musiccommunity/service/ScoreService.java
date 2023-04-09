package es.tfg.musiccommunity.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import es.tfg.musiccommunity.model.Opinion;
import es.tfg.musiccommunity.model.Score;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.OpinionRepository;
import es.tfg.musiccommunity.repository.ScoreRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.dto.ScoreDto;

@Service
public class ScoreService {

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CommonService commonService;

    public ResponseEntity<List<ScoreDto>> getUserScores(String login, boolean onlyPublic) {
        List<ScoreDto> scoresDto = new ArrayList<>(20);
        List<Score> scores;
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (user.isPresent()) { 
            if (onlyPublic) {
                scores = scoreRepository.findPublicScoresByUserOrderByScoreName(user.get());
            } else {
                scores = scoreRepository.findByUserOrderByScoreName(user.get());
            }
            for(Score s : scores) {
                scoresDto.add(new ScoreDto(s.getId(), s.getScoreName().replace(".pdf", ""), s.getFileType(), s.getUser().getLogin(), s.getUploadDateTime()));
            }
            return new ResponseEntity<>((scoresDto),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<ScoreDto>> getUserScoresByKeyword(String login, String keyword, boolean onlyPublic) {
        List<ScoreDto> scoresDto = new ArrayList<>(20);
        List<Score> scores;
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (user.isPresent()) { 
            if (onlyPublic) {
                scores = scoreRepository.findPublicScoresByUserAndScoreOrderByScoreName(user.get(), keyword);
            } else {
                scores = scoreRepository.findByUserAndScoreNameOrderByScoreName(user.get(), keyword);
            }
            for(Score s : scores) {
                scoresDto.add(new ScoreDto(s.getId(), s.getScoreName().replace(".pdf", ""), s.getFileType(), s.getUser().getLogin(), s.getUploadDateTime()));
            }
            return new ResponseEntity<>((scoresDto),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Long> storeScore(MultipartFile file, String login) {
        String scoreName = StringUtils.cleanPath(file.getOriginalFilename());
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (user.isPresent()) {
            try {
                // Check if the file's name contains invalid characters
                if(scoreName.contains("..")) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                // Check if the file's type is not pdf
                if(!file.getContentType().contains("pdf")) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                Score newScore = new Score(scoreName, file.getContentType(), file.getBytes(), user.get());
                scoreRepository.save(newScore);
                return new ResponseEntity<>(newScore.getId(),HttpStatus.OK);

            } catch (IOException ex) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<ScoreDto> getScoreInfo(Long id, String login) {
        Optional<Score> score = scoreRepository.findById(id);
        if (score.isPresent()) {
            Optional<UserProfile> user = userProfileRepository.findByLogin(login);
            if (user.isPresent()) { 
                UserProfile u = user.get();
                UserProfile uScore = score.get().getUser();
                if (score.get().getIsPublic() || u == uScore) {
                    Score s = score.get();
                    ScoreDto scoreDto = new ScoreDto(s.getId(), s.getScoreName().replace(".pdf", ""), s.getFileType(), s.getUser().getLogin(), s.getUploadDateTime());
                    return new ResponseEntity<>((scoreDto),HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } 

    public ResponseEntity<Resource> getScoreFile(Long id, String login) {
        Optional<Score> score = scoreRepository.findById(id);
        if (score.isPresent()) {
            Optional<UserProfile> user = userProfileRepository.findByLogin(login);
            if (user.isPresent()) { 
                UserProfile u = user.get();
                UserProfile uScore = score.get().getUser();
                if (score.get().getIsPublic() || u == uScore) {
                    Score s = score.get();
                    return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(s.getFileType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + s.getScoreName() + "\"")
                        .body(new ByteArrayResource(s.getData()));
                }
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> deleteScore(Long id, String login) {
        Optional<Score> score = scoreRepository.findById(id);
        if (score.isPresent()) {
            Optional<UserProfile> user = userProfileRepository.findByLogin(login);
            if (user.isPresent()) { 
                UserProfile u = user.get();
                UserProfile uScore = score.get().getUser();
                if (u == uScore) {
                    List<Opinion> opinionsWithScore = opinionRepository.findByScore(score.get());
                    for(Opinion t : opinionsWithScore) {
                        commonService.deleteComments(t);
                        commonService.deleteRecommendations(t);
                        opinionRepository.delete(t);
                    }
                    scoreRepository.delete(score.get());
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

package es.tfg.musiccommunity.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.tfg.musiccommunity.service.ScoreService;
import es.tfg.musiccommunity.service.dto.ScoreDto;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("")
    public ResponseEntity<List<ScoreDto>> getUserScores(Authentication auth, @RequestParam(value="login", required=false) String login,
            @RequestParam(value="keyword", required=false) String keyword, @RequestParam(value="onlyPublic", required=true) boolean onlyPublic) {
        if (login == null) {
            login = auth.getName();
        }
        if (keyword == null) {
            return scoreService.getUserScores(login, onlyPublic);
        } else {
            return scoreService.getUserScoresByKeyword(login, keyword, onlyPublic);
        }
    }

    @PostMapping("/uploadScore")
    public ResponseEntity<Long> uploadScore(Authentication auth, @RequestParam("score") MultipartFile score) {
        String login = auth.getName();
        return scoreService.storeScore(score, login);
    }

    @GetMapping("/score-info/{scoreId}")
    public ResponseEntity<ScoreDto> getScoreInfo(Authentication auth, @PathVariable Long scoreId) {
        String login = auth.getName();
        return scoreService.getScoreInfo(scoreId, login);
    }

    @GetMapping("/score/{scoreId}")
    public ResponseEntity<Resource> getScoreFile(Authentication auth, @PathVariable Long scoreId) {
        String login = auth.getName();
        return scoreService.getScoreFile(scoreId, login);
    }

    @DeleteMapping("/score/{scoreId}")
    public ResponseEntity<Void> deleteScore(Authentication auth, @PathVariable Long scoreId) {
        String login = auth.getName();
        return scoreService.deleteScore(scoreId, login);
    }
}

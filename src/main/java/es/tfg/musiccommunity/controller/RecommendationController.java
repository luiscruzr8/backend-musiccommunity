package es.tfg.musiccommunity.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.tfg.musiccommunity.service.RecommendationService;
import es.tfg.musiccommunity.service.dto.RecommendationDto;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController { 

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("")
    public ResponseEntity<List<RecommendationDto>> getAll(@RequestParam(value="top10", required=true) boolean top10,
            @RequestParam(value="keyword", required=false) String param) {
        String keyword = (param == null || param.isEmpty()) ? "" : param;
        return recommendationService.getRecommendations(top10, keyword);
    }

    @GetMapping("/user")
    public ResponseEntity<List<RecommendationDto>> getUserRecommendations(@RequestParam(value="login", required=true) String login,
            @RequestParam(value="top10", required=true) boolean top10, @RequestParam(value="keyword", required=false) String param) {
        String keyword = (param == null || param.isEmpty()) ? "" : param;
        return recommendationService.getUserRecommendations(login, top10, keyword);  
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationDto> getRecommendation(@PathVariable Long id){
        return  recommendationService.getRecommendationInfo(id);
    }

    /* CREACIÓN DE RECOMENDACIONES */
    @PostMapping("")
    public ResponseEntity<Long> createRecommendation(Authentication auth, @RequestBody RecommendationDto recDto){
        String login = auth.getName();
        return recommendationService.createRecommendation(login, recDto);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateRecommendation(Authentication auth, @RequestBody RecommendationDto recDto, @PathVariable Long id){
        String login = auth.getName();
        return recommendationService.updateRecommendation(login, recDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(Authentication auth, @PathVariable Long id){
        String login = auth.getName();
        return recommendationService.deleteRecommendation(login, id);
    }

    /* VALORAR RECOMENDACIÓN */
    @PostMapping("/{id}/rate")
    public ResponseEntity<Long> rateRecommendation(Authentication auth, @PathVariable Long id, @RequestParam(value="rate", required=true)int rate){
        String login = auth.getName();
        return recommendationService.rateRecommendation(login, id, rate);
    }
}

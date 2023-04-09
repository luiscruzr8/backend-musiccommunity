package es.tfg.musiccommunity.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.tfg.musiccommunity.service.CityService;
import es.tfg.musiccommunity.service.dto.CityDto;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping("")
    public ResponseEntity<List<CityDto>> getAllCities(@RequestParam(value="keyword", required=false) String param1) {
        String keyword = (param1 == null || param1.isEmpty()) ? "" : param1;
            return cityService.getAllCities(keyword);
    }

    @GetMapping("closest")
    public ResponseEntity<CityDto> getClosestCity(@RequestParam(value="latitude", required=true) double param1, 
            @RequestParam(value="longitude", required=true) double param2) {
        return cityService.getClosestCity(param1, param2);
    }
}

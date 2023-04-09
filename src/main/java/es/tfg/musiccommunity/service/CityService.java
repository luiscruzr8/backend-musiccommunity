package es.tfg.musiccommunity.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import es.tfg.musiccommunity.model.City;
import es.tfg.musiccommunity.repository.CityRepository;
import es.tfg.musiccommunity.service.dto.CityDto;

@Service
public class CityService {
    
    @Autowired
    private CityRepository cityRepository;

    public ResponseEntity<List<CityDto>> getAllCities(String keyword) {
        List<City> cities;
        if (keyword.isEmpty()) {
            cities = cityRepository.findByOrderByCountryAscNameAsc();
        } else {
            cities = cityRepository.findByNameLike(keyword);
        }
        List<CityDto> citiesDto = new ArrayList<>(25);
        for (City city : cities) {
            citiesDto.add(new CityDto(city.getId(), city.getName(), city.getCountry()));
        }
        return new ResponseEntity<>((citiesDto), HttpStatus.OK);
    }

    public ResponseEntity<CityDto> getClosestCity(double lat, double lon) {
        City city = cityRepository.findClosestCity(lat, lon);
        CityDto cityDto = new CityDto(city.getId(), city.getName(), city.getCountry());
        return new ResponseEntity<>((cityDto), HttpStatus.OK);
    }

}
